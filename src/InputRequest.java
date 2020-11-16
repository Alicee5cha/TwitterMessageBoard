import org.json.simple.*;

// Class to be transfered across network
public class InputRequest {
	
	 private String _class;
     private String command;
     private String argument;
     private String from;
     private JSONObject message;
     private int after;
     private String media;
     private String channel;

     public InputRequest(String _class, String command, String argument)
     {
    	 this._class = _class;
		 this.command = command;
		 this.argument = argument;
	 }
     
	 public void setClass(String _class)
	 {
		 this._class = _class;
	 }

	 public String get_Class()
	 {
		 return _class;	
	 }
	 
	 public void setFrom(String from)
	 {
		 this.from = from;
	 }

	 public String getFrom()
	 {
		 return from;
	 }
	 
	 public void setCommand(String command)
	 {
		 this.command = command;
	 }

	 public String getCommand()
	 {
		 return command;
	 }

	 public void setArgument(String argument)
	 {
		 this.argument = argument;
	 }

	 public String getArgument()
	 {
		 return argument;
	 }
	 
	 public void setMessage(JSONObject message)
	 {
		 this.message = message;
	 }

	 public JSONObject getMessage()
	 {
		 return message;
	 }
	 
	 public void setAfter(int after)
	 {
		 this.after = after;
	 }

	 public int getAfter()
	 {
		 return after;
	 }
	
	 public void setMedia(String media)
	 {
		 this.media = media;
	 }
	 
	 public String getMedia()
	 {
		 return media;
	 }

    // Serializes this object into a JSONObject
   @SuppressWarnings("unchecked")
    public JSONObject toJSON()
    {
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("Command", command);
		   obj.put("Argument", argument);
		   return obj;

	}
  
  // Serializes this object into a JSONObject
 @SuppressWarnings("unchecked")
  public JSONObject toJSONOpen()
  {
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("identity", from);
		   return obj;

	}
 
 // Serializes this object into a JSONObject
@SuppressWarnings("unchecked")
 public JSONObject toJSONPost()
 {
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("From", from);
		   obj.put("Message", message);
		   return obj;

	}

// Serializes this object into a JSONObject
@SuppressWarnings("unchecked")
public JSONObject toJSONMessage(String _class, String from, String when, String body, String media)
{
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("From", from);
		   obj.put("When", when);
		   obj.put("Body", body);
		   obj.put("Media", media);
		   return obj;

	}

// Serializes this object into a JSONObject
@SuppressWarnings("unchecked")
public JSONObject toJSONGet()
{
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("From", from);
		   obj.put("After", after);
		   return obj;

	}

//Serializes this object into a JSONObject
@SuppressWarnings("unchecked")
public JSONObject toJSONSub()
{
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("From", from);
		   obj.put("Channel", argument);
		   return obj;

	}
 }