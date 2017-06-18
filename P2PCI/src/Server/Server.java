package Server;

import java.io.*;
import java.net.*;

public class Server {
	
	public static LinkedList<UPort> ports;

	public static LinkedList<RFC> RFCs;

	public static void main(String[] args) {
		ports = new LinkedList<UPort>();
		RFCs = new LinkedList<RFC>();
		// Declare Sockets (and their handles)
		ServerSocket echoServer = null;
		Socket clientSocket = null;
		
		// Start the Server Socket
		try {
			echoServer = new ServerSocket(7734);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Wait/Accept an incoming connection
		try { 
			while(true) {
				clientSocket = echoServer.accept();
				new ServerThread(clientSocket).run();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
