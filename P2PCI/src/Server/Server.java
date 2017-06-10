package Server;

import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
	
	private static LinkedList<UPort> ports;

	private static LinkedList<RFC> RFCs;

	public Server() {
		ports = new LinkedList<UPort>();
		RFCs = new LinkedList<RFC>();
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
				Scanner sc = new Scanner(line);
				String method = sc.next();
				sc.close();
				if (method.equals("ADD")) {
					add(line);
				} else if (method.equals("LOOKUP")) {
					find(line);
				} else if (method.equals("LIST")) {
					list(line);
				}
				System.out.println(line);
				out.println(line);
				if(line.indexOf("Hello World! 10") != -1){
					clientSocket = echoServer.accept();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void add(String msg) {
		Scanner sc = new Scanner(msg);
		Scanner first = new Scanner(sc.nextLine());
		String numberLine = first.nextLine();
		int number = Integer.parseInt(numberLine.split(" ")[2]);
		first.close();
		Scanner second = new Scanner(sc.nextLine());
		second.next();
		String hostname = second.next();
		second.close();
		Scanner third = new Scanner(sc.nextLine());
		third.next();
		int port = third.nextInt();
		third.close();
		Scanner forth = new Scanner(sc.nextLine());
		String titleLine = forth.nextLine();
		String title = titleLine.split(" ", 2)[1];
		forth.close();
		sc.close();
		UPort p = new UPort(hostname, port);
		if (!ports.contains(p)) { ports.addFirst(p); }
		RFC rfc = new RFC(number, title, hostname);
		RFCs.addFirst(rfc);
	}
	
	public static LinkedList<RFC> find(String msg) {
		Scanner sc = new Scanner(msg);
		Scanner first = new Scanner(sc.nextLine());
		String numberLine = first.nextLine();
		int number = Integer.parseInt(numberLine.split(" ")[2]);
		first.close();
		sc.close();
		ListIterator<RFC> iterator = RFCs.listIterator();
		LinkedList<RFC> results = new LinkedList<RFC>();
		while (iterator.hasNext()) {
			RFC rfc = iterator.next();
			if (rfc.getNumber() == number) {
				results.add(rfc);
			}
		}
		return results;
	}
	
	public static LinkedList<RFC> list(String msg) {
		return RFCs;
	}
	

}
