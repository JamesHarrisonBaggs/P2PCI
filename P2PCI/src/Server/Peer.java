package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Peer {

	private static Socket echoSocket;
	private static BufferedReader in;
	private static PrintStream out;
	private static Scanner console;

	public static void main(String[] args) throws InterruptedException {

		console = new Scanner(System.in); 
		
		connectToServer();

		//authenticate();
		
		//uploadIndexes();
		
		//handleCommands();
		
		
		if (echoSocket != null && out != null && in != null) {
			try {
				File folder = new File("peer");
				File[] files = folder.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						int number = Integer.parseInt(files[i].getName().replaceAll("[^0-9]", ""));
						String title = getTitle(number);
						out.println("ADD RFC " + number + " P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress()
								+ "\nPort: " + echoSocket.getPort() + "\nTitle: " + title + "\n");
						String responseLine = "";
						for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
							responseLine += line + "\n";
						}
						System.out.println(responseLine);
					}

				}
				while (true) {
					System.out.print("Wait for input: ");
					String input = console.nextLine();
					input = input.replaceAll("([\\n\\r]+\\s*)*$", "").toLowerCase();
					System.out.println(input);
					if (input.startsWith("lookup")) {
						int number = Integer.parseInt(input.split(" ")[1]);
						String title = getTitle(number);
						out.println("LOOKUP RFC " + number + " P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress()
								+ "\nPort: " + echoSocket.getPort() + "\nTitle: " + title + "\n");
					} else if (input.startsWith("listall")) {
						out.println("LIST ALL P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: "
								+ echoSocket.getPort() + "\n");
					} else if (input.startsWith("quit")) {
						out.println("QUIT P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: "
								+ echoSocket.getPort() + "\n");
						break;
					} else if (input.startsWith("get")) {
						// communicate with another peer to get a rfc file here
						// probably
					} else {
						System.out.println("Wrong command, try again.");
						continue;
					}
					String responseLine = "";
					for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
						responseLine += line + "\n";
					}
					System.out.println(responseLine);
					// Thread.sleep(1000);
				}
				console.close();
				out.close();
				in.close();
				echoSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	// Queries the user for server ip Address, and connects to the server. Re
	private static void connectToServer() {
		String hostname = null;
		try {

			System.out.println("Enter Server's IP address (assumes port 7734. Enter 127.0.0.1 for loopback address:");
			hostname = console.nextLine();
			// InetAddress.getByName(null) gets loopback address. Port 7734.
			echoSocket = new Socket(hostname, 7734);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			out = new PrintStream(echoSocket.getOutputStream());

			// create a new thread for upload port probably here

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + hostname);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to:" + hostname);
		}

		System.out.println("Connected to Server!");
		System.out.println("------------------------");
	}

	public static String getTitle(int number) throws FileNotFoundException, InputMismatchException {
		String title = "";
		Scanner ts = new Scanner(new File("Test/rfc-index.txt"));
		while (ts.hasNextLine()) {
			Scanner ls = new Scanner(ts.nextLine());
			int index = 0;
			try {
				index = ls.nextInt();
			} catch (NoSuchElementException e) {
				continue;
			}
			if (index == number) {
				title = ls.nextLine().split("\\.")[0].trim();
				break;
			}
			ls.close();
		}
		ts.close();
		return title;
	}
}
