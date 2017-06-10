package Server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

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
				for (int i = 0; i <= 10; i++) {
					out.writeBytes("Hello World! " + i + "\n");
					String responseLine;
					if ((responseLine = in.readLine()) != null) {
						System.out.println(responseLine);
					}
					Thread.sleep(1000);
				}

				out.close();
				in.close();
				echoSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private String hostname;

	private int port;
	
	private LinkedList<RFC> RFCs;

	public Peer(String hostname, int port, LinkedList<RFC> RFCs) {
		setHostname(hostname);
		setPort(port);
		setRFCs(RFCs);
		
	}

	public String getHostname() {
		return hostname;
	}

	private void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	private void setPort(int port) {
		this.port = port;
	}

	public LinkedList<RFC> getRFCs() {
		return RFCs;
	}

	public void setRFCs(LinkedList<RFC> rFCs) {
		RFCs = rFCs;
	}
}
