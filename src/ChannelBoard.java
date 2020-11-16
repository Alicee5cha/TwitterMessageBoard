
import java.util.Vector;
import org.json.simple.JSONObject;

public class ChannelBoard {
	private Vector<JSONObject> stringMessages;
	private String channelName;

	public ChannelBoard(String channelName) {
		
		this.channelName = channelName;
		this.stringMessages = new Vector<JSONObject>();
	}

	public Vector<JSONObject> getMessages() {
		
		return stringMessages;
	}
	
	public int getSize()
	{
		return stringMessages.size();
	}
	
	public synchronized void addMessage(JSONObject objPost) {

		stringMessages.add(objPost);	
	}
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelName() {
		return channelName;
	}

}
