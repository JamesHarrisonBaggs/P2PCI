package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Peer {

	public static void main(String[] args) throws InterruptedException {
		// Modified example socket program from
		// http://www.javaworld.com/article/2077322/core-java/core-java-sockets-programming-in-java-a-tutorial.html

		// Declare the Socket and its in/out handles
		Socket echoSocket = null;
		BufferedReader in = null;
		PrintStream out = null;
		try {

			// InetAddress.getByName(null) gets loopback address. Port 7734.
			echoSocket = new Socket(InetAddress.getByName(null), 7734);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			out = new PrintStream(echoSocket.getOutputStream());

			// create a new thread for upload port probably here
			
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: hostname");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: hostname");
		}

		// If the socket is created, write Hello world, and wait for the echo.
		if (echoSocket != null && out != null && in != null) {
			try {
				File folder = new File("peer");
				File[] files = folder.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						int number = Integer.parseInt(files[i].getName().replaceAll("[^0-9]", ""));
						String title = getTitle(number);
						out.println("ADD RFC " + number + " P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\nTitle: " + title + "\n");
						String responseLine = "";
						for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
							responseLine += line + "\n";
						}
						System.out.println(responseLine);
					}
					
				}
				Scanner command = new Scanner(System.in);
				while(true) {
					System.out.print("Wait for input: ");
					String input = command.nextLine();
					input = input.replaceAll("([\\n\\r]+\\s*)*$", "").toLowerCase();
					System.out.println(input);
					if (input.startsWith("lookup")) {
						int number = Integer.parseInt(input.split(" ")[1]);
						String title = getTitle(number);
						out.println("LOOKUP RFC " + number + " P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\nTitle: " + title + "\n");
					} else if (input.startsWith("listall")) {
						out.println("LIST ALL P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\n");
					} else if (input.startsWith("quit")) {
						out.println("QUIT P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\n");
						break;
					} else if (input.startsWith("get")) {
						//communicate with another peer to get a rfc file here probably
					} else {
						System.out.println("Wrong command, try again.");
						continue;
					}
					String responseLine = "";
					for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
						responseLine += line + "\n";
					}
					System.out.println(responseLine);
					//Thread.sleep(1000);
				}
				command.close();
				out.close();
				in.close();
				echoSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
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
