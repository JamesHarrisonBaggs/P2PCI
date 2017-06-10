package Server;

import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
	
	private LinkedList<Peer> Peers;

	private LinkedList<RFC> RFCs;

	public Server() {

	}

	public static void main(String[] args) {
		
		// Declare Sockets (and their handles)
		ServerSocket echoServer = null;
		Socket clientSocket = null;
		String line;
		BufferedReader in = null;
		PrintStream out = null;
		
		// Start the Server Socket
		try {
			echoServer = new ServerSocket(7734);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Wait/Accept an incoming connection
		try { 
			clientSocket = echoServer.accept();
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintStream(clientSocket.getOutputStream());
			
			// Read a line and echo it
			while(true){
				line = in.readLine();
				System.out.println(line);
				out.println(line);
				if(line.indexOf("Hello World!") != -1){
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}

	

}
