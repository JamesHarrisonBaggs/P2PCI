package Server;

import java.io.*;
import java.net.*;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

// Modified from example at https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
public class ServerThread extends Thread {
	protected Socket socket;
	
	protected int clientPort;
	
	protected String version;

	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
		version = "P2P-CI/1.0";
		clientPort = 0;
	}

	public void run() {
		InputStream in = null;
		BufferedReader br = null;
		PrintStream out = null;

		try {
			in = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			out = new PrintStream(socket.getOutputStream());

		} catch (IOException e) {
			System.out.println("IO Exception " + e);
		}
		
		try {
			while (true) {
				String command = "";
				String line;
				while (!(line = br.readLine()).isEmpty()) {
					//System.out.println(line);
					command += line + "\n";
				}
				System.out.println(command);
				Scanner sc = new Scanner(command);
				String method = sc.next();
				sc.close();
				if (method.equals("ADD")) {
					RFC rfc = new RFC();
					try {
						rfc = add(command);
					} catch (InputMismatchException e) {
						out.println(version + " 400 Bad Request\n");
					} catch (IllegalArgumentException e) {
						out.println(version + " 505 P2P-CI Version Not Supported\n");
					}
					out.println(version + "200 OK");
					out.println(rfc.toString() + "\n");
				} else if (method.equals("LOOKUP")) {
					LinkedList<RFC> results = new LinkedList<RFC>();
					try {
						results = find(command);
					} catch (InputMismatchException e) {
						out.println(version + " 400 Bad Request\n");
					} catch (IllegalArgumentException e) {
						out.println(version + " 505 P2P-CI Version Not Supported\n");
					}
					if (results.size() == 0) {
						out.println(version + " 404 Not Found\n");
					}
					ListIterator<RFC> iterator = results.listIterator();
					out.println(version + "200 OK");
					while(iterator.hasNext()) {
						out.println(iterator.next().toString());
					}
					out.println("\n");
				} else if (method.equals("LIST")) {
					LinkedList<RFC> all = new LinkedList<RFC>();
					try {
						all = list(command);
					} catch (IllegalArgumentException e) {
						out.println(version + " 505 P2P-CI Version Not Supported\n");
					}
					ListIterator<RFC> iterator = all.listIterator();
					out.println(version + "200 OK");
					while(iterator.hasNext()) {
						out.println(iterator.next().toString());
					}
					out.println("\n");
				} else if (method.equals("QUIT")) {
					int port = sc.nextInt();
					//UPort p = new UPort(hostname, clientPort);
					ListIterator<RFC> iterator = Server.RFCs.listIterator();
					while (iterator.hasNext()) {
						RFC rfc = iterator.next();
						if (rfc.getPort() == port) {
							Server.RFCs.remove(rfc);
						}
					}
					//Server.ports.remove(p);
					socket.close();
					return;
				}
			}
		} catch (IOException e) {
			System.out.println("IO Exception while reading lines " + e);
			return;
		}	
	}
	
	public RFC add(String msg) {
		Scanner sc = new Scanner(msg);
		Scanner first = new Scanner(sc.nextLine());
		String numberLine = first.nextLine();
		int number = 0;
		try {
			number = Integer.parseInt(numberLine.split(" ")[2]);
		} catch (InputMismatchException e) {
			e.printStackTrace();
		}
		String clientVersion = numberLine.split(" ")[3];
		if (!clientVersion.equals(version)) {
			first.close();
			sc.close();
			throw new IllegalArgumentException();
		}
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
		//UPort p = new UPort(hostname, port);
		//clientPort = port;
		//if (!Server.ports.contains(p)) { Server.ports.addFirst(p); }
		RFC rfc = new RFC(number, title, hostname, port);
		Server.RFCs.addFirst(rfc);
		return rfc;
	}
	
	public LinkedList<RFC> find(String msg) {
		Scanner sc = new Scanner(msg);
		Scanner first = new Scanner(sc.nextLine());
		String numberLine = first.nextLine();
		int number = 0;
		try {
			number = Integer.parseInt(numberLine.split(" ")[2]);
		} catch (InputMismatchException e) {
			e.printStackTrace();
		}
		String clientVersion = numberLine.split(" ")[3];
		if (!clientVersion.equals(version)) {
			first.close();
			sc.close();
			throw new IllegalArgumentException();
		}
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
		RFC rfc = new RFC(number, title, hostname, port);
		ListIterator<RFC> iterator = Server.RFCs.listIterator();
		LinkedList<RFC> results = new LinkedList<RFC>();
		while (iterator.hasNext()) {
			RFC next = iterator.next();
			if (next == rfc) {
				results.add(next);
			}
		}
		return results;
	}
	
	public LinkedList<RFC> list(String msg) {
		Scanner sc = new Scanner(msg);
		Scanner first = new Scanner(sc.nextLine());
		String numberLine = first.nextLine();
		String clientVersion = numberLine.split(" ")[2];
		if (!clientVersion.equals(version)) {
			first.close();
			sc.close();
			throw new IllegalArgumentException();
		}
		first.close();
		sc.close();
		return Server.RFCs;
	}
}
