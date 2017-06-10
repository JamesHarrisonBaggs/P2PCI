package Server;

import java.io.*;
import java.net.*;

// Modified from example at https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
public class ServerThread extends Thread {
	protected Socket socket;

	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
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

		String line;
		try {
			while (true) {
				line = br.readLine();
				if (line != null && line.indexOf("Hello World! 10") == -1) {
					System.out.println(line);
					out.println(line);
					//Server.strings.add(line);
				} else {
					socket.close();
					System.out.println("Finished reading!");
					return;
				}
			}
		} catch (IOException e) {
			System.out.println("IO Exception while reading lines " + e);
			return;
		}
		
	}

}
