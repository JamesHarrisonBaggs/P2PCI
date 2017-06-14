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
		DataOutputStream out = null;
		try {

			// InetAddress.getByName(null) gets loopback address. Port 7734.
			echoSocket = new Socket(InetAddress.getByName(null), 7734);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
			out = new DataOutputStream(echoSocket.getOutputStream());

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: hostname");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: hostname");
		}

		// If the socket is created, write Hello world, and wait for the echo.
		if (echoSocket != null && out != null && in != null) {
			try {
				File folder = new File("peerA");
				File[] files = folder.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						int number = Integer.parseInt(files[i].getName().replaceAll("[^0-9]", ""));
						String title = getTitle(number);
						out.writeBytes("ADD RFC " + number + " P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\nTitle: " + title + "\n\n");
						String responseLine = "";
						String line;
						while (!(line = in.readLine()).isEmpty()) {
							responseLine += line + "\n";
						}
						System.out.println(responseLine);
					}
					
				}
				while(true) {
					Scanner command = new Scanner(System.in);
					//String input = command.nextLine();
					if (command.next().equalsIgnoreCase("lookup")) {
						int number = command.nextInt();
						String title = getTitle(number);
						out.writeBytes("LOOKUP RFC " + number + " P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\nTitle: " + title + "\n\n");
					} else if (command.next().equalsIgnoreCase("listall")) {
						out.writeBytes("LIST ALL P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\n\n");
					} else if (command.next().equalsIgnoreCase("quit")) {
						out.writeBytes("QUIT P2P-CI/1.0\nHost: " + echoSocket.getLocalSocketAddress() + "\nPort: " + echoSocket.getPort() + "\n\n");
						command.close();
						break;
					} else {
						System.out.println("Wrong command, try again.");
					}
					String responseLine;
					if ((responseLine = in.readLine()) != null) {
						System.out.println(responseLine);
					}
					Thread.sleep(1000);
				}
				/*for (int i = 0; i <= 10; i++) {
					out.writeBytes("Hello World! " + i + "\n");
					String responseLine;
					if ((responseLine = in.readLine()) != null) {
						System.out.println(responseLine);
					}
					Thread.sleep(1000);
				}*/

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
