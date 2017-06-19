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
	private static String inetAddress;
	private static Socket p2pSocket;
	private static boolean done = false;
	
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
		registerUploadPort();
		
		// uploadRFCIndexes();
		// handleConsoleCommands();
		// Listen for connections and start a new thread for each
		
			//Start p2pThread
				//Transfer File
			


		if (socketToServer != null && out != null && in != null && authStatus == 1 && uploadPort != null) {
			try {
				uploadRFCIndexes();

				Runnable p2sThread = new PeerToServerThread();
				new Thread(p2sThread).start();
				
				while(!done){
					p2pSocket = uploadPort.accept();
					Runnable p2pThread = new PeerToPeerThread(p2pSocket);
					new Thread(p2pThread).start();
				}
				

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


	public static void handleConsoleCommands() throws IOException{
		while (true) {
			System.out.println("Waiting for input. Valid options are listall, lookup <RFC#>, get <RFC#> <Hostname> <Port#>, and quit");
			String input = console.nextLine();
			input = input.replaceAll("([\\n\\r]+\\s*)*$", "").toLowerCase();
			System.out.println(input);
			if (input.startsWith("lookup")) {
				int number = Integer.parseInt(input.split(" ")[1]);
				String title = getTitle(number);
				out.println(
						"LOOKUP RFC " + number + " P2P-CI/1.0\nHost: " + inetAddress
								+ "\nPort: " + uploadPort.getLocalPort() + "\nTitle: " + title + "\n");
			} else if (input.startsWith("listall")) {
				out.println("LIST ALL P2P-CI/1.0\nHost: " + inetAddress + "\nPort: "
						+ uploadPort.getLocalPort() + "\n");
			} else if (input.startsWith("quit")) {
				out.println("QUIT P2P-CI/1.0\nHost: " + inetAddress + "\nPort: "
						+ uploadPort.getLocalPort() + "\n");
				done = true;
				break;
			} else if (input.startsWith("get")) {
				int RFCNumber = Integer.parseInt(input.split(" ")[1]);
				String peerHostname = input.split(" ")[2];
				int peerUPort = Integer.parseInt(input.split(" ")[3]);
				Socket socketToPeer = new Socket(peerHostname, peerUPort);
				
				BufferedReader pin = new BufferedReader(new InputStreamReader(socketToPeer.getInputStream()));
				PrintStream pout = new PrintStream(socketToPeer.getOutputStream());
				OutputStream fout = new FileOutputStream(new File("peer/rfc" + RFCNumber + ".txt"));
				pout.println("GET RFC " + RFCNumber + " P2P-CI/1.0");
				pout.println("Host: " + peerHostname);
				pout.println("OS: " + System.getProperty("os.name"));
				
				
				
				//TODO this just echoes first 6 lines of p2p file transfer (maybe handle potential errors?)
				String responseLine = "";
				for (String line = pin.readLine(); !line.isEmpty(); line = pin.readLine()) {
					responseLine += line + "\n";
				}
				System.out.println(responseLine);
				
				
				// 
				InputStream pin2 = socketToPeer.getInputStream();
				byte[] bytes = new byte[16*1024];

		        int count;
		        while ((count = pin2.read(bytes)) > 0) {
		            fout.write(bytes, 0, count);
		        }
		        
		        pin2.close();
				
		        responseLine = "";
				for (String line = pin.readLine(); !line.isEmpty(); line = pin.readLine()) {
					responseLine += line + "\n";
				}
				System.out.println(responseLine);
				pin.close();
				
				socketToPeer.close();
				fout.close();
				
				
				
			} else {
				System.out.println("Wrong command, try again.");
				continue;
			}
			String responseLine = "";
			for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
				responseLine += line + "\n";
			}
			System.out.println(responseLine);
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
		
		inetAddress = socketToServer.getLocalAddress().toString().replaceAll("/", "");

		System.out.println("Connected to Server!");
		System.out.println("------------------------");
	}
	
	//Redundant method. Any message (such as 
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
				out.println("ADD RFC " + number + " P2P-CI/1.0\nHost: " + inetAddress
						+ "\nPort: " + uploadPort.getLocalPort() + "\nTitle: " + title + "\n");
				String responseLine = "";
				for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
					responseLine += line + "\n";
				}

				System.out.println(responseLine);
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
