import java.net.*;
import java.io.*;
import java.util.*;

public class EchoServer
{
	private volatile static Vector<ClientHandler> userList = new Vector<ClientHandler>();

	public static void main(String[] args) throws IOException
	{
		final int PORT = 12345;

		System.out.println("Opening port...\n");
		try
		(
			ServerSocket serverSocket = new ServerSocket(PORT);
		) {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ClientHandler c = new ClientHandler(clientSocket, userList);
				c.start();
				userList.add(c);	
			}
		}
		catch(IOException ioEx)
		{
			System.out.println("Unable to attach to port!");
			System.out.println(ioEx.getMessage());
			System.exit(1);
		}
	}
}
