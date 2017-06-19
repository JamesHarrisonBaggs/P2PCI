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
		InputStream in = null;
		BufferedReader br = null;
		PrintStream out = null;
		InputStream is = null;
		OutputStream os = null;
		
		try {
			in = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			while (true) {
				String command = "";
				for(String line = br.readLine(); !line.isEmpty(); line =br.readLine()) {
					command += line.trim() + "\n";
				}
				System.out.println(command);
				Scanner sc = new Scanner(command);
				String method = sc.next();
				sc.close();
				if (method.equals("GET")) {
					String fileName;
					try {
						fileName = "peer/" + get(command);
					} catch (InputMismatchException e) {
						out.println(version + " 400 Bad Request\n");
						socket.close();
						break;
					} catch (IllegalArgumentException e) {
						out.println(version + " 505 P2P-CI Version Not Supported\n");
						socket.close();
						break;
					}
					if (fileName.isEmpty()) {
						out.println(version + " 404 Not Found\n");
						socket.close();
						break;
					}
					out.println(version + "200 OK");
					SimpleDateFormat date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
					long lastModified = new File(fileName).lastModified();
					long length = new File(fileName).length();
					out.println("Date: " + date.format(new Date()));
					out.println("OS: " + System.getProperty("os.name"));
					out.println("Last-Modified: " + date.format(new Date(lastModified)));
					out.println("Content-Length: " + length);
					out.println("Content-Type: text/text");
					out.println();
					
					File f = new File(fileName);
					byte[] bytes = new byte[16 * 1024];
					in = new FileInputStream(f);
					os = socket.getOutputStream();
					int count;
			        while ((count = in.read(bytes)) > 0) {
			            out.write(bytes, 0, count);
			        }

			        os.close();
			        in.close();
			        socket.close();
			        
				} else {
					
				}
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
		/*sc.nextLine();
		Scanner third = new Scanner(sc.nextLine());
		String OSLine = third.nextLine();
		String OS = OSLine.split(" ", 2)[1];
		third.close();*/
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
