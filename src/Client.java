import javafx.application.*;
import javafx.concurrent.Task;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Client extends Application
{
	public static void main(String[] args) throws IOException {
		
		launch(args);
	    }
	
	Socket messageSocket;
	PrintWriter out;
	Scanner in;
	TextField hostInput,openInput;
	TextArea received, postInput;
	Button closeConn;
	Button post, openConfirm, postConfirm, closeChan, sub, unSub, changeImage, attachFile, saveButton; 
	ToggleButton darkMode;
	Label hostPrompt, messagePrompt, openPrompt, file;
	String host, channel, message;
	boolean conHost, isOpen, isDark;
	Text pName;
	File selectedFile = null, profile;
	
	HBox hostPane, footerPane, subPane, attaching;
	VBox miscPane, openPane, postPane, messagePane;
	GridPane profilePane;
	
	Vector<String> subscribedList = new Vector<String>();

  public void start(Stage stage)
	{
		BorderPane rootPane;
		Scene scene;

		//border pane
		rootPane = new BorderPane();
		
		//rootPane layout
		hostPane = new HBox();
	    messagePane = new VBox();
	    footerPane = new HBox();
	    miscPane = new VBox();
		miscPane.setPadding(new Insets(10, 10, 10, 10));
	    rootPane.setTop(hostPane);
	    rootPane.setLeft(miscPane);
	    rootPane.setRight(messagePane);
	    rootPane.setBottom(footerPane);
	    
		//Initially set to false until host accepted
		messagePane.setVisible(false);
		miscPane.setVisible(false);
		
		isOpen = false;
		isDark = false;
	    
	    //hostPane set to Top of BorderPane
		hostPane.setPadding(new Insets(30, 0, 0, 20));
	    hostPane.setSpacing(10);
	    hostPane.setStyle("-fx-background-color: #1da1f2;");
	    hostPane.setPrefHeight(100);
	    
	    Image twitter = new Image(Client.class.getResourceAsStream("twitter1.png"));
	  	ImageView logo = new ImageView();
	  	logo.setImage(twitter);
	  	logo.setCache(true);
	    
		hostPrompt = new Label("Enter Host Name ");
		hostPrompt.setFont(new Font("Arial", 20));
		hostPrompt.setPadding(new Insets(5, 0, 0, 0));
		hostInput = new TextField();
		hostInput.setPrefColumnCount(25);
		
		closeConn = new Button("Close connection");
		closeConn.setOnAction(event->closeDown());
		closeConn.setFont(new Font("Arial", 20));
		closeConn.setPadding(new Insets(10, 10, 10, 10));
		closeConn.setVisible(false);
				
		hostPane.getChildren().addAll(logo, hostPrompt, hostInput, closeConn);
		
		//Action to accept host
		hostInput.setOnAction(event->acceptHost());
		
		//message box on right
		received = new TextArea();
		received.setPrefHeight(400);
		received.setPrefWidth(550);
		received.setWrapText(true);
		received.setEditable(false);
		received.setFont(new Font("Arial", 20));
		
		attaching = new HBox();
		Text temp = new Text();
		attaching.setSpacing(10);
		attaching.setPadding(new Insets(5, 5, 5, 5));
		attaching.setVisible(false);
		saveButton = new Button("Save");
		attaching.getChildren().addAll(temp, saveButton);
		messagePane.getChildren().addAll(received, attaching);
		saveButton.setOnAction(event->saveAttach());
		
		//miscPane set to Left of BorderPane
		//Open channel pane
		openPane = new VBox();
		openPane.setSpacing(10);
		openPane.setPadding(new Insets(5, 5, 10, 5));
		openPrompt = new Label("Enter Channel Name");
		openPrompt.setPadding(new Insets(5, 5, 5, 5));
		openPrompt.setFont(new Font("Arial", 20));
		openInput = new TextField();
		openInput.setPrefColumnCount(25);
		openConfirm = new Button("Open");
		openConfirm.setFont(new Font("Arial", 15));
		openConfirm.setPadding(new Insets(10, 10, 10, 10));
		openPane.getChildren().addAll(openPrompt, openInput, openConfirm);
		
		miscPane.getChildren().add(openPane);
		
		//Post message pane
		postPane = new VBox();
		postPane.setSpacing(10);
		postPane.setPadding(new Insets(5, 5, 20, 5));
		postInput = new TextArea();
		postInput.setPrefColumnCount(30);
		postInput.setPromptText("Enter a message...");
		postInput.setWrapText(true);
		postInput.setPrefHeight(120);
		
		HBox attachments = new HBox();
		attachments.setSpacing(10);
		attachFile = new Button("Attach Files");
		attachFile.setFont(new Font("Arial", 15));
		attachFile.setPadding(new Insets(10, 10, 10, 10));
		file = new Label("file");
		file.setPadding(new Insets(5, 5, 5, 5));
		file.setFont(new Font("Arial", 15));
		file.setVisible(false);
		
		attachments.getChildren().addAll(attachFile, file);
		
		postConfirm = new Button("Post");
		postConfirm.setFont(new Font("Arial", 15));
		postConfirm.setPadding(new Insets(10, 10, 10, 10));
		postPane.getChildren().addAll(postInput, attachments, postConfirm);
		attachFile.setOnAction(event->attachToMess());
		
		postConfirm.setOnAction(event->postMessage());
		openConfirm.setOnAction(event->openChannel(stage));

		//Subscribe/Unsubscribe to channels pane
		subPane = new HBox();
		subPane.setSpacing(10);
		subPane.setPadding(new Insets(5, 5, 5, 5));
		sub = new Button("Subscribe");
		sub.setFont(new Font("Arial", 15));
		sub.setPadding(new Insets(10, 20, 10, 20));
		
		unSub = new Button("Unsubscribe");
		unSub.setFont(new Font("Arial", 15));
		unSub.setPadding(new Insets(10, 10, 10, 10));
		
		subPane.getChildren().addAll(sub, unSub);
		
		sub.setOnAction(event->subToPane());
		unSub.setOnAction(event->unsubFromPane());
		
		//footer for bottom
		footerPane.setStyle("-fx-background-color: #303030;");
		footerPane.setPrefHeight(60);
		footerPane.setPadding(new Insets(10, 10, 10, 10));
		darkMode = new ToggleButton("Switch to 'Dark Mode'");
		darkMode.setFont(new Font("Arial", 15));
		darkMode.setPadding(new Insets(10, 10, 10, 10));

		darkMode.setOnAction(event->dark(rootPane));	
		footerPane.getChildren().add(darkMode);
		
		scene = new Scene(rootPane, 1200, 800);
		stage.setScene(scene);
		stage.setTitle("Client");
		stage.show();
}
  
    public boolean dark(BorderPane rootPane)
  {
	  if (isDark == false)
	  {
		  	//rootPane background
		  	rootPane.setStyle("-fx-background-color: #65696b;");
			//message box colours
		    received.setStyle("-fx-control-inner-background: #65696b; -fx-text-fill: #ffffff;");
		    //hostPane colours
		    hostPane.setStyle("-fx-background-color: #323639;");
		    hostPrompt.setStyle("-fx-text-fill: #ffffff;");
		    darkMode.setText("Switch to 'Light Mode'");
		    //postPane colours
		    file.setStyle("-fx-text-fill: #ffffff;");
			
		    isDark = true;
	  }
	  else
	  {
		  	//rootPane background
		  	rootPane.setStyle("-fx-background-color: #ffffff;");
			//message box colours
		    received.setStyle("-fx-control-inner-background: #ffffff; -fx-text-fill: #000000;");
		    //hostPane colours
		    hostPane.setStyle("-fx-background-color: #1da1f2;");
		    hostPrompt.setStyle("-fx-text-fill: #000000;");
		    darkMode.setText("Switch to 'Dark Mode'");
		    //postPane colours
		    file.setStyle("-fx-text-fill: #ffffff;");
			
		    isDark = false;
	  } 	
		return isDark;
  }

	public boolean acceptHost()
	{
		final int ECHO = 12345;
		host = hostInput.getText();
		
      try
		{
          messageSocket = new Socket(host, ECHO);
          out = new PrintWriter(messageSocket.getOutputStream(), true);
          in = new Scanner(messageSocket.getInputStream());
          
          	hostPrompt.setText("Connected to: " + host);
          	hostInput.setVisible(false);
			hostInput.setText("");
			closeConn.setVisible(true);
			
			messagePane.setVisible(true);
			miscPane.setVisible(true);
			openInput.requestFocus();

			conHost = true;
	
        } catch (UnknownHostException e) {
        	conHost = false;
        	
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Connection Error");
			alert.setHeaderText(null);
			alert.setContentText("Invalid host: " + host);
			alert.showAndWait();
			
        } catch (IOException e) {
        	conHost = false;
        	
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Connection Error");
			alert.setHeaderText(null);
			alert.setContentText("Couldn't get I/O for the connection to host: " + host);
			alert.showAndWait();
        }
	return conHost;
 	}
	
	public boolean openChannel(Stage stage)
	{
		String userInput, response;
		while (!(openInput.getText().isEmpty()))
		{
			userInput = "open " + openInput.getText();
			out.println(userInput);

			int n = in.nextInt();
			in.nextLine();

			response = in.nextLine();
			
			Object object = JSONValue.parse(response);
			JSONObject json = (JSONObject) object;

			String error = (String) json.get("_class");

			 if (error.equals("_ErrorResponse"))
			 {
				 error = (String) json.get("Error");

				 for (int i = 0; i < n; i++)
					{
						received.appendText("*** " + error + " ***\n");
					}
				 openInput.setText("");
			 }
			 else
			 {
				channel = (String) json.get("identity");
	
				for (int i = 0; i < n; i++)
				{
					received.appendText("*** " + channel + " Channel is open ***\n");
				}
				
				isOpen = true;
			 }
			 openInput.setText("");
		}	
		
		//Profile pane
	    profilePane = new GridPane();
	    profilePane.setHgap(100);
	    profilePane.setVgap(5);
	    profilePane.setPadding(new Insets(5, 10, 20, 10));
	    
	    Image pPic = new Image(Client.class.getResourceAsStream("placeholder.png"));
	    ImageView profile = new ImageView();
	    profile.setImage(pPic);
	    profile.setCache(true);
	    profile.maxWidth(128);
	    profile.maxWidth(128);
		profile.setFitHeight(128);
	    profile.setFitWidth(128);
	    profile.setPreserveRatio(true);
	    profilePane.add(profile, 0, 0);

	    pName = new Text(channel);
	    pName.setFont(new Font("Arial", 30));
	    profilePane.add(pName, 0, 1);
	    
	    closeChan = new Button("Close Channel");
	    closeChan.setFont(new Font("Arial", 15));
	    closeChan.setPadding(new Insets(10, 10, 10, 10));
	    profilePane.add(closeChan, 1, 1);
	    closeChan.setOnAction(event->closeChannel());
		
	    changeImage = new Button("Edit Profile");
	    changeImage.setFont(new Font("Arial", 15));
	    changeImage.setPadding(new Insets(10, 22, 10, 22));
	    profilePane.add(changeImage, 2, 1);
	    changeImage.setOnAction(event->editProfile(stage, profile));
	    
	    postInput.requestFocus();
		miscPane.getChildren().remove(openPane);
		miscPane.getChildren().addAll(profilePane, postPane, subPane);

		return isOpen;
	}
	
	public void closeChannel()
	{
		miscPane.getChildren().remove(profilePane);
		miscPane.getChildren().remove(postPane);
		miscPane.getChildren().remove(subPane);
		miscPane.getChildren().add(openPane);
		isOpen = false;
	}
	
	public void editProfile(Stage stage, ImageView profile)
	{
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().addAll(
			     new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
			    ,new FileChooser.ExtensionFilter("PNG Files", "*.png")
			    ,new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
			);
		File profilePic = chooser.showOpenDialog(stage);
		Image image = new Image(profilePic.toURI().toString());
		profile.setImage(image); 
	}
	
	public void subToPane()
	{
		Stage subToStage = new Stage();
		BorderPane subToRoot = new BorderPane();
		Scene subToScene;
		VBox subToPane;
		HBox subSearch;
		Label title;
		TextField subInput;
		
		subSearch = new HBox();
		subToPane = new VBox();
		subToRoot.setTop(subSearch);
		subToRoot.setLeft(subToPane);
		
		title = new Label("Subscribe To");
		title.setFont(new Font("Arial", 20));
		title.setPadding(new Insets(10, 10, 10, 10));
		subInput = new TextField();
		subInput.setPrefColumnCount(15);
		subSearch.getChildren().addAll(title, subInput);
		
		subInput.setOnAction(event->subTo(subInput, subToStage));

		subToScene = new Scene(subToRoot, 500, 50);
		subToStage.setScene(subToScene);
		subToStage.setTitle("Subscribe");
		subToStage.show();
		
		if (isDark == true)
		{
			subToRoot.setStyle("-fx-background-color: #65696b;");
			title.setStyle("-fx-text-fill: #ffffff;");
			//everything else styled accordingly
		}
		
	}
	
	public void subTo(TextField subInput, Stage subToStage)
	{
		String userInput, response, channelSub;
		while (!(subInput.getText().isEmpty()))
		{
			userInput = "sub " + subInput.getText();
			out.println(userInput);

			int n = in.nextInt();
			in.nextLine();

			response = in.nextLine();
			
			Object object = JSONValue.parse(response);
			JSONObject json = (JSONObject) object;
			
			String error = (String) json.get("_class");

			 if (error.equals("_ErrorResponse"))
			 {
				 error = (String) json.get("Error");

				 for (int i = 0; i < n; i++)
					{
						received.appendText("*** " + error + " ***\n");
					}
				 subInput.setText("");
			 }
			 else
			 {
				channelSub = (String) json.get("Channel");

				for (int i = 0; i < n; i++)
				{
					received.appendText("*** Subscribed to " + channelSub + " ***\n");
					subscribedList.add(channelSub);
				}	
			 }

			subInput.setText("");
		}	
		
		subToStage.close();
	}
	
	public void unsubFromPane()
	{
		Stage unsubFromStage = new Stage();
		BorderPane unsubFromRoot = new BorderPane();
		Scene unsubFromScene;
		VBox unsubFromPane;
		HBox unsubSearch;
		Label title;
		TextField unsubInput;
		
		unsubSearch = new HBox();
		unsubFromPane = new VBox();
		unsubFromRoot.setTop(unsubSearch);
		unsubFromRoot.setLeft(unsubFromPane);
		
		title = new Label("Unsubscribe From");
		title.setFont(new Font("Arial", 20));
		title.setPadding(new Insets(10, 10, 10, 10));
		unsubInput = new TextField();
		unsubInput.setPrefColumnCount(15);
		unsubSearch.getChildren().addAll(title, unsubInput);
		
		unsubInput.setOnAction(event->unsubFrom(unsubInput, unsubFromStage));
		
		unsubFromScene = new Scene(unsubFromRoot, 500, 50);
		unsubFromStage.setScene(unsubFromScene);
		unsubFromStage.setTitle("Unsubscribe");
		unsubFromStage.show();
		
		if (isDark  == true)
		{
			unsubFromRoot.setStyle("-fx-background-color: #65696b;");
			title.setStyle("-fx-text-fill: #ffffff;");
			//everything else styled accordingly
		}
	}
	
	public void unsubFrom(TextField unsubInput, Stage unsubFromStage)
	{		
		String userInput, response, channelUnsub;
		while (!(unsubInput.getText().isEmpty()))
		{
			userInput = "unsub " + unsubInput.getText();
			out.println(userInput);

			int n = in.nextInt();
			in.nextLine();

			response = in.nextLine();

			Object object = JSONValue.parse(response);
			JSONObject json = (JSONObject) object;

			String error = (String) json.get("_class");
			
			if (error.equals("_ErrorResponse"))
			 {
				 error = (String) json.get("Error");

				 for (int i = 0; i < n; i++)
					{
						received.appendText("*** " + error + " ***\n");
					}
				 unsubInput.setText("");
			 }
			else
			 {
				channelUnsub = (String) json.get("Channel");

				for (int i = 0; i < n; i++)
				{
					received.appendText("*** Unubscribed from " + channelUnsub + " ***\n");
					subscribedList.add(channelUnsub);
				}	
			 }

			unsubInput.setText("");
		}
		unsubFromStage.close();
	}
	
	public void postMessage()
	{			
		 file.setVisible(false);
		 String userInput, response;
	     while (!(postInput.getText().isEmpty()) || (selectedFile != null)) {
	            
	    	 userInput = "post " +  postInput.getText();
	    	 out.println(userInput);

	    	 int n = in.nextInt();
			 in.nextLine();
			 response = in.nextLine();	 
			 String messages = in.nextLine();
			 
			 String[] lines = messages.split("   ");
			 Vector<String> bodys = new Vector<String>();
			 Vector<String> froms = new Vector<String>();
			 Vector<String> medias = new Vector<String>();

			 for (int i = 0; i < lines.length; i++)
			 {
				 Object objectMess = JSONValue.parse(lines[i]);
				 JSONObject jsonMess = (JSONObject) objectMess;
				 bodys.add((String) jsonMess.get("Body"));
				 froms.add((String) jsonMess.get("From"));
				 medias.add((String) jsonMess.get("Media"));
				 
				 try {    
		                byte [] newBytes = Base64.getDecoder().decode(medias.get(i));
		                Path path = Paths.get("");
		                File newFile = new File(Files.write(path, newBytes).toUri());
		                typeOfAttachment(newFile);
		                received.appendText(froms.get(i) + ": " + bodys.get(i) + " Attachment: " + newFile.getName() + "\n");	 

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NullPointerException ne) {
				}
		            finally
		            {
		            	received.appendText(froms.get(i) + ": " + bodys.get(i) + "\n");	 

		    			Object object = JSONValue.parse(response);
		    			JSONObject json = (JSONObject) object;

		    			String error = (String) json.get("_class");

		    			 if (error.equals("_ErrorResponse"))
		    			 {
		    				 error = (String) json.get("Error");

		    				received.appendText("*** " + error + " ***\n");

		    				 postInput.setText("");
		    				 selectedFile = null;
		    			 }
		    			 else
		    			 {
		    	            	if(selectedFile != null)
		    	            	{
		    	            		attaching.setVisible(true);
		    	            		typeOfAttachment(selectedFile);
		    	            	}
		    	            }
		    				postInput.setText("");
		    				selectedFile = null;
		            }
			 }   
	        }
	     }
	
	public void attachToMess()
	{
		Stage attachStage = new Stage();

		//setMedia() for message
		//code it to JSON, use toJSONMessages()
		//important
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().addAll(
			     new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
			    ,new FileChooser.ExtensionFilter("PNG Files", "*.png")
			    ,new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
			    ,new FileChooser.ExtensionFilter("MP4 Files", "*.mp4")
			    ,new FileChooser.ExtensionFilter("AVI Files", "*.avi")
			    ,new FileChooser.ExtensionFilter("MP3 Files", "*.mp3")
			);

			selectedFile = chooser.showOpenDialog(attachStage);

            try {
//            	FileInputStream fileInputStreamReader = new FileInputStream(selectedFile);
//            	byte[] bytes = new byte[(int)selectedFile.length()];
//            	fileInputStreamReader.read(bytes);
//            	String encodedFile = Base64.getEncoder().encodeToString(bytes);
//            	fileInputStreamReader.close();
            	
                byte [] bytes = Files.readAllBytes(selectedFile.toPath());
                String encodedFile = Base64.getEncoder().encodeToString(bytes);

	            String userInput;
	      	     while (!(encodedFile.isEmpty())) 
	      	     {    
	      	    	userInput = "attach " +  encodedFile;
	      	    	out.println(userInput);
	      	    	file.setText(selectedFile.getName());
	      	    	file.setVisible(true);
	      	    	encodedFile = "";
	      	     }

            	
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
	    } catch (IOException e) {
	    	System.out.println(e.getMessage());
	    }
	}
	
	public void saveAttach()
	{
		Stage attachStage = new Stage();
		
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().addAll(
			     new FileChooser.ExtensionFilter("JPG Files", "*.jpg")
			    ,new FileChooser.ExtensionFilter("PNG Files", "*.png")
			    ,new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg")
			    ,new FileChooser.ExtensionFilter("MP4 Files", "*.mp4")
			    ,new FileChooser.ExtensionFilter("AVI Files", "*.avi")
			);

				chooser.showSaveDialog(attachStage);

	}
	
	public void getMessages()
	{
		//if list of clienthandlers have pressed post then messages will refresh for each client
		//important
	}

  public void closeDown()
	{
			try
			{	
				out.println("quit");
				
				hostPrompt.setText("Enter Host Name ");
				hostInput.requestFocus();
				hostInput.setVisible(true);
				closeConn.setVisible(false);
				
				messagePane.setVisible(false);
				miscPane.setVisible(false);
				
				in.close();
				out.close();
				messageSocket.close();			

			} catch (IOException e) {
	            System.out.println(e.getMessage());
			}		
	}
  
  public void typeOfAttachment(File selectedFile)
  {
	  if ((selectedFile.getName().contains(".png")) || (selectedFile.getName().contains(".jpg")) || (selectedFile.getName().contains(".jpeg")))
  	{
  		attaching.getChildren().remove(0, 2);
  	    Image image = new Image(selectedFile.toURI().toString());
  	    ImageView attachImage = new ImageView();	            	  
  	    attachImage.setFitHeight(200);
  	    attachImage.setFitWidth(200);
  	    attachImage.setPreserveRatio(true);
  	  	attachImage.setImage(image);
  	    attachImage.setCache(true);
  	    attachImage.maxWidth(200);
  	    attachImage.maxWidth(200);   
  	  	attaching.getChildren().addAll(attachImage, saveButton);
  	  	
  	}
  	
  	if ((selectedFile.getName().contains(".avi")) || (selectedFile.getName().contains(".mp4")))
  	{
  		attaching.getChildren().remove(0, 2);
  		Media video = new Media(selectedFile.toURI().toString());
  		MediaPlayer videoPlayer = new MediaPlayer(video);
  		videoPlayer.setAutoPlay(true);
  		MediaView videoViewer = new MediaView(videoPlayer);
  		videoViewer.setCache(true);
  		videoViewer.maxWidth(200);
  		videoViewer.maxHeight(200);
  		videoViewer.setFitHeight(200);
  	    videoViewer.setFitWidth(200);
  	    videoViewer.setPreserveRatio(true);
  		
  		attaching.getChildren().addAll(videoViewer, saveButton);
  	}
  	
  	if ((selectedFile.getName().contains(".mp3")))
  	{
  		attaching.getChildren().remove(0, 2);
  		Media audio = new Media(selectedFile.toURI().toString());
  		MediaPlayer audioPlayer = new MediaPlayer(audio);
  		audioPlayer.setAutoPlay(true);
  		MediaView audioViewer = new MediaView(audioPlayer);
  		audioViewer.setCache(true);
  		audioViewer.maxWidth(200);
  		audioViewer.maxHeight(200);
  		audioViewer.setFitHeight(200);
  		audioViewer.setFitWidth(200);
  	    audioViewer.setPreserveRatio(true);
  		
  		attaching.getChildren().addAll(audioViewer, saveButton);
  }
  }
}