import java.util.Vector;

import org.json.simple.*;

// Class to be transfered across network
public class OutputResponse {
	
	 private String _class;
	 private String from;
	 private String when;
	 private String body;
     private String error;
     private String media;
     private Vector<JSONObject> messages;

     public OutputResponse(String _class, String from, String when, String body, String media)
     {
    	 this._class = _class;
    	 this.from = from;
    	 this.when = when;
    	 this.body = body;
    	 this.media = media;
	 }
     
     public OutputResponse(String _class)
     {
    	 this._class = _class;
	 }
     
     public OutputResponse(String _class, String error)
     {
    	 this._class = _class;
    	 this.error = error;
	 }
     
	 public void setClass(String _class)
	 {
		 this._class = _class;
	 }

	 public String get_Class()
	 {
		 return _class;	
	 }
	 
	 public void setError(String error)
	 {
		 this.error = error;
	 }

	 public String getError()
	 {
		 return error;	
	 }
	 
	 public void setFrom(String from)
	 {
		 this.from = from;
	 }

	 public String getFrom()
	 {
		 return from;
	 }
	 
	 public void setWhen(String when)
	 {
		 this.when = when;
	 }

	 public String getWhen()
	 {
		 return when;
	 }
	 
	 public void setBody(String body)
	 {
		 this.body = body;
	 }

	 public String getBody()
	 {
		 return body;
	 }
	 
	 public void setMessages(Vector<JSONObject> messages)
	 {
		 this.messages = messages;
	 }

	 public Vector<JSONObject> getMessages()
	 {
		 return messages;
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
   public JSONObject toJSONOut()
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

 @SuppressWarnings("unchecked")
  public JSONObject toJSONError()
  {
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("Error", error);
		   return obj;
	}
 
@SuppressWarnings("unchecked")
 public JSONObject toJSONSuc()
 {
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   return obj;
	}

@SuppressWarnings("unchecked")
public JSONObject toJSONMessages()
{
		// create JSON Object
		   JSONObject obj = new JSONObject();
		   obj.put("_class", _class);
		   obj.put("Messages", messages);
		   return obj;
	}
 }