package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Peer {

	private static Socket socketToServer;
	private static BufferedReader in;
	private static PrintStream out;
	private static Scanner console;
	private static ServerSocket uploadPort;

	public static void main(String[] args) throws InterruptedException {

		// Open the Console Scanner, And Peer Upload Socket
		console = new Scanner(System.in);
		try {
			uploadPort = new ServerSocket(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		

		// Connect to the server
		connectToServer();

		// Authenticate with the server (Username, password)
		int authStatus;
		while((authStatus = authenticate()) != 1){
			if(authStatus == -1){
				break;
			}
		}
		
		// Send Upload Port to the Server
		//registerUploadPort();
		
		// Start the Peer to Server thread (for handling peer console commands)
			// uploadRFCIndexes();
			// handleConsoleCommands();
		// Listen for connections
			//Start p2pThread
				//Transfer File
			


		if (socketToServer != null && out != null && in != null && authStatus == 1) {
			try {
				uploadRFCIndexes();

				handleConsoleCommands();
				

			} catch (IOException e) {
				e.printStackTrace();
			}
			

		}
		
		// Close everything
		try {
			console.close();
			out.close();
			in.close();
			socketToServer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Returns 1 if authenticated, 0 if not authenticated, -1 if quit command
	private static int authenticate() {
		return 1;
	}


	private static void handleConsoleCommands() throws IOException{
		while (true) {
			System.out.print("Wait for input: ");
			String input = console.nextLine();
			input = input.replaceAll("([\\n\\r]+\\s*)*$", "").toLowerCase();
			System.out.println(input);
			if (input.startsWith("lookup")) {
				int number = Integer.parseInt(input.split(" ")[1]);
				String title = getTitle(number);
				out.println(
						"LOOKUP RFC " + number + " P2P-CI/1.0\nHost: " + uploadPort.getInetAddress()
								+ "\nPort: " + uploadPort.getLocalPort() + "\nTitle: " + title + "\n");
			} else if (input.startsWith("listall")) {
				out.println("LIST ALL P2P-CI/1.0\nHost: " + uploadPort.getInetAddress() + "\nPort: "
						+ uploadPort.getLocalPort() + "\n");
			} else if (input.startsWith("quit")) {
				out.println("QUIT P2P-CI/1.0\nHost: " + uploadPort.getInetAddress() + "\nPort: "
						+ uploadPort.getLocalPort() + "\n");
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
			System.out.println("Server Response: ------------");
			System.out.println(responseLine);
			System.out.println("End of Server Response --------");
		}
	}

	// Queries the user for server ip Address, and connects to the server. Re
	private static void connectToServer() {
		String hostname = null;
		try {

			System.out.println("Enter Server's IP address (assumes port 7734. Enter 127.0.0.1 for loopback address:");
			hostname = console.nextLine();
			// InetAddress.getByName(null) gets loopback address. Port 7734.
			socketToServer = new Socket(hostname, 7734);
			in = new BufferedReader(new InputStreamReader(socketToServer.getInputStream()));
			out = new PrintStream(socketToServer.getOutputStream());

			// create a new thread for upload port probably here

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + hostname);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to:" + hostname);
		}

		System.out.println("Connected to Server!");
		System.out.println("------------------------");
	}
	
	private static void registerUploadPort() {
		System.out.println("Registering P2P Upload Port...");
		
		System.out.println("Upload Port Successfully Registered with Centralized Index");
	}

	private static void uploadRFCIndexes() throws IOException {
		File folder = new File("peer");
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				int number = Integer.parseInt(files[i].getName().replaceAll("[^0-9]", ""));
				String title = getTitle(number);
				out.println("ADD RFC " + number + " P2P-CI/1.0\nHost: " + socketToServer.getLocalSocketAddress()
						+ "\nPort: " + socketToServer.getPort() + "\nTitle: " + title + "\n");
				String responseLine = "";
				for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
					responseLine += line + "\n";
				}

				System.out.println("Server Response: \n-------------------");
				System.out.println(responseLine);
				System.out.println("End of Server Response\n------------------");
			}

		}
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
