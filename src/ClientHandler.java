import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.*; // required for Scanner and List

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ClientHandler extends Thread {
	private Socket client;
	private PrintWriter toClient;
	private BufferedReader fromClient;
	private RequestIssuer issuer = new RequestIssuer();
	
	//current users channel
	ChannelBoard cb;
	Channels channels = new Channels();
	
	private Vector<ClientHandler> userList = new Vector<ClientHandler>();
	private Vector<ChannelBoard> allSyncChannels = new Vector<ChannelBoard>();

	
	private int readCount = 0;
	private int seq = 0;
	int numberOfMessages = 0;
	private String channel = null;
	private int after = 0;
	private String message = null;
	private String subChannel = null;
	private String unSubChannel = null;
	private boolean isLogged = false;
	private boolean isChannelOpen = false;
	private boolean post = false;
	private String errorMessage = "";
	private String attachment;

	public ClientHandler(Socket socket, Vector<ClientHandler> userList) throws IOException {
		client = socket;
		toClient = new PrintWriter(client.getOutputStream(), true);
		fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
		this.userList = userList;
	}

	public void run() {
		try {
			String inputLine;
			while ((inputLine = fromClient.readLine()) != null) {
				// take a request before reading request
				issuer.takeRequest();
				post = false;
				// parse request
				Scanner sc = new Scanner(inputLine);
				String _class = "";
				String command = "";
				String argument = "";
				try {
					command = sc.next();
					argument = sc.skip(" ").nextLine();
					sc.close();

				} catch (NoSuchElementException e) {}

				InputRequest inReq = new InputRequest(_class, command, argument);

				switch (command)
				{
				
				default: _class = "_errorRequest"; errorMessage = "ILLEGAL REQUEST"; printIllegal(errorMessage); break; 
					
				case "open":
					{
						_class = "_openRequest"; 
						inReq.setClass(_class);
						inReq.setFrom(argument);
						JSONObject open = inReq.toJSONOpen();
						String jsonString = open.toJSONString();
						String encodedString = new String(jsonString);
						
						System.out.println(encodedString);
						isLogged = true;
							
						openChannel(encodedString, argument);
						isChannelOpen = true;
						
						break;
					}
					
				case "post":
					{
						_class = "_postRequest";
						inReq.setClass(_class);
						inReq.setFrom(channel);
						inReq.setMedia(attachment);
						
						String when = Integer.toString(seq + 1);
						JSONObject message = inReq.toJSONMessage("_message", channel, when, argument, attachment);
						inReq.setMessage(message);
						
						JSONObject post = inReq.toJSONPost();
						String jsonString = post.toJSONString();
						String encodedString = new String(jsonString);
						
						if (isChannelOpen == true)
						{
							System.out.println(encodedString);
							postMessage(argument, attachment, userList);
						} 	
						break;
					}
					
				case "attach": { attachment = argument; break;}
					
				case "sub":
					{
						_class = "_subRequest";
						inReq.setClass(_class);
						inReq.setFrom(channel);
						inReq.setArgument(argument);
						JSONObject sub = inReq.toJSONSub();
						String jsonString = sub.toJSONString();
						String encodedString = new String(jsonString);
						
						if (isChannelOpen == true)
						{
								System.out.println(encodedString);
								subChannel(encodedString, userList, argument);
						}	
						break;
					}
					
				case "unsub":
					{
						_class = "_unsubRequest";
						inReq.setClass(_class);
						inReq.setFrom(channel);
						inReq.setArgument(argument);
						JSONObject sub = inReq.toJSONSub();
						String jsonString = sub.toJSONString();
						String encodedString = new String(jsonString);
						
						if (isChannelOpen == true)
						{
							System.out.println(encodedString);
							unsubChannel(encodedString, userList, argument);

						}
						break;
					}
				case "quit": {quitUser(command); return;}
				}
			}

        } catch (IOException e) {
            System.out.println("IOException."); 
		}
	}

	public void printIllegal(String errorMessage) {
		
		synchronized (ClientHandler.class) {
		OutputResponse errorRes = new OutputResponse("_ErrorResponse", errorMessage);
		JSONObject objErr = errorRes.toJSONError();
		String JSONStringErr = objErr.toJSONString();
		JSONStringErr = new String(JSONStringErr);
		toClient.println(1);
        toClient.println(JSONStringErr);
		}
}

	public void quitUser(String command)
	{
		synchronized (ClientHandler.class) {
			try {
				fromClient.close();
				toClient.close();
			} catch (IOException e) {
	            System.out.println(e.getMessage());
			}
		}

	}
	
	public void openChannel(String encodedString, String argument)
	{
			synchronized (ClientHandler.class) {
				 seq = 0;

				channel = argument;
				cb = new ChannelBoard(channel);
				
				if (channels.addChannelBoard(cb) == false)
				{
					errorMessage = "CHANNEL NAME ALREADY IN USE";
					printIllegal(errorMessage);
				}
				else
				{
					channels.addChannelBoard(cb);
					allSyncChannels.add(cb);
					toClient.println(1);
					toClient.println(encodedString);				
				}
				
		} 	
	}
	
	public void postMessage(String argument, String attachment, Vector<ClientHandler> userList)
	{
		
		synchronized (ClientHandler.class) {
			
			if (argument.length() < 1000) {
				
				post = true;
				++seq;
				//message response
				String responseClass = "_message";
				String when = Integer.toString(seq);
				OutputResponse postReq = new OutputResponse(responseClass, channel, when, argument, attachment);
				JSONObject objPost = postReq.toJSONOut();
				String JSONStringMess = objPost.toJSONString();
				JSONStringMess = new String(JSONStringMess);
				
				attachment = "";
				cb.addMessage(objPost);
				channels.AddChannelsMessages();

				Vector<JSONObject> messages = new Vector<JSONObject>();

					if (post == true);
					{
						messages = getMessages();
					}
				
				String messagesToString = "";
				
				for (JSONObject m : messages)
				{
					String jsonString = m.toJSONString();
					String newMessage = new String(jsonString);

					messagesToString = messagesToString + (newMessage + "   ");
				}
				
		        toClient.println(2);
				toClient.println(JSONStringMess);
				toClient.println(messagesToString);
				return;
				
			}
			else
			{
				errorMessage = "MESSAGE TOO LONG - UNDER 1000 CHARS";
				printIllegal(errorMessage);
			}
		}
	}
	
	public Vector<JSONObject> getMessages()
	{
			synchronized (ClientHandler.class) {
				
				Vector<JSONObject> messages = new Vector<JSONObject>();
				messages.clear();
				
				for (ChannelBoard cb : allSyncChannels)
				{
					for (int i = 0; i < cb.getSize(); i++)
					{
						messages.add(cb.getMessages().get(i));
					}
				}
				
				return messages;

		}
			}
	
	public void subChannel(String encodedString, Vector<ClientHandler> userList, String argument) 
	{
			synchronized (ClientHandler.class) {
				
				if (subscribeCB(userList, argument) == true)
				{
					toClient.println(1);
					toClient.println(encodedString);
				}
				else
				{
					errorMessage = "CHANNEL DOES NOT EXIST";
					printIllegal(errorMessage);
				}   
		}
	}
	
	public boolean subscribeCB(Vector<ClientHandler> userList, String argument)
	{	
		subChannel = argument;
		
		for (ClientHandler ch : userList)
		{
			if (ch.channels.getChannelBoard(subChannel) != null)
			{
				allSyncChannels.add(ch.channels.getChannelBoard(subChannel));
				return true;
			}

		}
		return false;
	}
	
	public void unsubChannel(String encodedString, Vector<ClientHandler> userList, String argument) 
	{
			synchronized (ClientHandler.class) {
				
				if (unsubscribeCB(userList, argument) == true)
				{
					toClient.println(1);
					toClient.println(encodedString);
				}
				else
				{
					errorMessage = "CHANNEL DOES NOT EXIST";
					printIllegal(errorMessage);
				} 
		}
	}
	
	public boolean unsubscribeCB(Vector<ClientHandler> userList, String argument)
	{	
		unSubChannel = argument;
		
		for (ClientHandler ch : userList)
		{
			if (ch.channels.getChannelBoard(unSubChannel) != null)
			{
				allSyncChannels.remove(ch.channels.getChannelBoard(unSubChannel));
				return true;
			}
			
		}
		return false;
	}
}
