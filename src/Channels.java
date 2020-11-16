import java.util.Vector;

import org.json.simple.JSONObject;

public class Channels {

	private volatile Vector<ChannelBoard> subChannelsList;
	private Vector<JSONObject> allMessages;
	private int size;
	
	public Channels()
	{
		this.subChannelsList = new Vector<ChannelBoard>();
		this.allMessages = new Vector<JSONObject>();
	}

	public Vector<JSONObject> AddChannelsMessages()
	{
		for (ChannelBoard cb : subChannelsList)
		{
			for (int i = 0; i < cb.getSize(); i++)
			{
				allMessages.add(cb.getMessages().get(i));
			}
		}
		return allMessages;
		
	}
	
	public Vector<JSONObject> getAllMessages()
	{
		return allMessages;
	}
	
	public int getSize()
	{
		for (ChannelBoard cb : subChannelsList)
		{
			size += cb.getSize();
		}
		return size/2;
	}
	
	public ChannelBoard getChannelBoard(String name)
	{
		for (ChannelBoard c : subChannelsList)
		{
			if (c.getChannelName().equals(name))
			{
				return c;	
			}
		}
		return null;	
	}
	
	public synchronized boolean addChannelBoard(ChannelBoard cb)
	{	
		if (subChannelsList.isEmpty() == false)
		{
			for (ChannelBoard c : subChannelsList)
			{
				if (c.getChannelName().equals(cb.getChannelName()))
				{
					return false;
				}
			}
		}
		
		subChannelsList.add(cb);
		return true;
	}
	
	public Vector<ChannelBoard> getSubList()
	{
		return subChannelsList;
	}
}
