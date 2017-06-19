package Server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

class PeerToPeerThread implements Runnable {

	private Socket socket;

	private String version = "P2P-CI/1.0";

	public PeerToPeerThread(Socket peerSocket) {
		this.socket = peerSocket;
	}

	public void run() {

		// Initialize Input and Output
		BufferedReader br = null;
		PrintStream out = null;

		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			String command = "";
			for (String line = br.readLine(); !line.isEmpty(); line = br.readLine()) {
				command += line.trim() + "\n";
			}
			System.out.println(command);
			Scanner sc = new Scanner(command);
			String method = sc.next();
			sc.close();
			if (method.equals("GET")) {
				String fileName = null;
				try {
					fileName = get(command);
				} catch (InputMismatchException e) {
					out.println(version + " 400 Bad Request\n");
					socket.close();
				} catch (IllegalArgumentException e) {
					out.println(version + " 505 P2P-CI Version Not Supported\n");
					socket.close();
				}
				if (fileName.isEmpty()) {
					out.println(version + " 404 Not Found\n");
					socket.close();
				} else {
					fileName = "peer/" + fileName;
					out.println(version + " 200 OK");
					SimpleDateFormat date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
					long lastModified = new File(fileName).lastModified();
					long length = new File(fileName).length();
					out.println("Date: " + date.format(new Date()));
					out.println("OS: " + System.getProperty("os.name"));
					out.println("Last-Modified: " + date.format(new Date(lastModified)));
					out.println("Content-Length: " + length);
					out.println("Content-Type: text/text");
					out.println();
	
					Thread.sleep(2000);
					
					File f = new File(fileName);
					InputStream is = new FileInputStream(f);
					OutputStream os = socket.getOutputStream();
					
					byte[] bytes = new byte[16 * 1024];
					int count;
					while ((count = is.read(bytes)) >= 0) {
						os.write(bytes, 0, count);
					}
					
					
					os.close();
					is.close();
	
					out.close();
					socket.close();
				}
			} else {
				out.println(version + " 400 Bad Request\n");
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String get(String msg) {
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
		/*
		 * sc.nextLine(); Scanner third = new Scanner(sc.nextLine()); String
		 * OSLine = third.nextLine(); String OS = OSLine.split(" ", 2)[1];
		 * third.close();
		 */
		sc.close();
		File folder = new File("peer");
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				int index = Integer.parseInt(files[i].getName().replaceAll("[^0-9]", ""));
				if (index == number) {
					return files[i].getName();
				}
			}
		}
		return "";
	}

}
