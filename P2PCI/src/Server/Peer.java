package Server;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Peer {

	private static Socket socketToServer;
	private static BufferedReader in;
	private static PrintStream out;
	private static Scanner console;
	private static ServerSocket uploadPort;
	private static String inetAddress;
	private static Socket p2pSocket;
	private static boolean done = false;
	private static SecretKeySpec secretKey;
	private static String version = "P2P-CI/1.0";
	
	public static void main(String[] args) throws InterruptedException, IOException {
		// Open the Console Scanner, And Peer Upload Socket
		console = new Scanner(System.in);
		try {
			uploadPort = new ServerSocket(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Connect to the server
		connectToServer();
		
		System.out.println("Please enter username and password to gain acess (username password): ");
		String login[] = console.nextLine().split(" ");
		String username = login[0];
		String password = login[1];
		secretKey = new SecretKey().getKey();
		String encoded = encode(password);
		out.println("LOGIN " + username + " " + encoded + "\n");
		
		String responseLine = "";
		for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
			responseLine += line + "\n";
		}
		System.out.println(responseLine);
		if (responseLine.trim().equals("Invalid User")) {socketToServer.close();  System.exit(0);}
		
		if (socketToServer != null && out != null && in != null && uploadPort != null) {
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
	
	//example from http://aesencryption.net/
	private static String encode(String password) {
		Cipher cipher;
		String encoded = "";
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        encoded = Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return encoded;
	}

	public static void handleConsoleCommands() throws IOException{
		while (true) {
			System.out.println("Waiting for input. Valid options are listall, lookup <RFC#>, get <RFC#> <Hostname> <Port#>, and quit");
			String input = console.nextLine();
			input = input.replaceAll("([\\n\\r]+\\s*)*$", "").toLowerCase();
			System.out.println(input);
			if (input.startsWith("lookup")) {
				int number = 0;
				try {
					number = Integer.parseInt(input.split(" ")[1]);
				} catch (Exception e) {
					System.out.println(version + " 400 Bad Request");
					System.out.println("Try again.");
					System.out.println();
					continue;
				}
				String title = getTitle(number);
				out.println(
						"LOOKUP RFC " + number + " " + version + "\nHost: " + inetAddress
								+ "\nPort: " + uploadPort.getLocalPort() + "\nTitle: " + title + "\n");
			} else if (input.startsWith("listall")) {
				out.println("LIST ALL " + version + "\nHost: " + inetAddress + "\nPort: "
						+ uploadPort.getLocalPort() + "\n");
			} else if (input.startsWith("quit")) {
				out.println("QUIT " + version + "\nHost: " + inetAddress + "\nPort: "
						+ uploadPort.getLocalPort() + "\n");
				done = true;
				System.exit(0);
				break;
			} else if (input.startsWith("get")) {
				
				int RFCNumber = Integer.parseInt(input.split(" ")[1]);
				String peerHostname = input.split(" ")[2];
				int peerUPort = Integer.parseInt(input.split(" ")[3]);
				Socket socketToPeer = new Socket(peerHostname, peerUPort);
				
				BufferedReader peerIn = new BufferedReader(new InputStreamReader(socketToPeer.getInputStream()));
				PrintStream peerOut = new PrintStream(socketToPeer.getOutputStream());
				OutputStream fileOut = new FileOutputStream(new File("peer/rfc" + RFCNumber + ".txt"));
				InputStream pin2 = socketToPeer.getInputStream();
				peerOut.println("GET RFC " + RFCNumber + " " + version);
				peerOut.println("Host: " + peerHostname);
				peerOut.println("OS: " + System.getProperty("os.name"));
				peerOut.println();
				
				//TODO this just echoes first 6 lines of p2p file transfer (maybe handle potential errors?)
				String responseLine = "";
				for (String line = peerIn.readLine(); !line.isEmpty(); line = peerIn.readLine()) {
					responseLine += line + "\n";
				}
				System.out.println(responseLine);
				 
				byte[] bytes = new byte[16*1024];

		        int count;
		        while ((count = pin2.read(bytes)) >= 0) {
		            fileOut.write(bytes, 0, count);
		        }
		        
		        System.out.println("File Retrieved!\n");
		        
				peerIn.close();
				peerOut.close();
				pin2.close();
				fileOut.close();
				socketToPeer.close();
				
				out.println("ADD RFC " + RFCNumber + " " + version + "\nHost: " + inetAddress
						+ "\nPort: " + uploadPort.getLocalPort() + "\nTitle: " + getTitle(RFCNumber) + "\n");

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
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host:" + hostname);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to:" + hostname);
		}
		
		inetAddress = socketToServer.getLocalAddress().toString().replaceAll("/", "");

		System.out.println("Connected to Server!");
		System.out.println("------------------------");
	}

	private static void uploadRFCIndexes() throws IOException {
		File folder = new File("peer");
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				int number = Integer.parseInt(files[i].getName().replaceAll("[^0-9]", ""));
				String title = getTitle(number);
				out.println("ADD RFC " + number + " " + version + "\nHost: " + inetAddress
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
