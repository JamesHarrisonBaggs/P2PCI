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
		
		System.out.println("Server Started!");
		// Wait/Accept an incoming connection
		try { 
			while(true) {
				System.out.println("Waiting for connections...");
				clientSocket = echoServer.accept();
				Runnable serverThread = new ServerThread(clientSocket);
				new Thread(serverThread).start();
				clientSocket= null;
				
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
