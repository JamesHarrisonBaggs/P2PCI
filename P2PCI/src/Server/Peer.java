package Server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Peer {

	public static void main(String[] args) throws InterruptedException {
		File workingDir = new File("./");
		File[] list1 = workingDir.listFiles();
		String[] list2 = workingDir.list();
		
		for(String s: list2 ){
			System.out.println(s);
		}
		
		for(File f: list1){
			System.out.println(f.getName());
		}
	}
		
	
	public static ServerSocket getServerSocket() throws InterruptedException{
		ServerSocket uSocket = null;
		try {
			uSocket = new ServerSocket(7735);
		} catch (IOException e) {
			System.out.println("Exception thrown");
			e.printStackTrace();
		}
		Thread.sleep(10000);
		
		return uSocket;
	}

	public static void directoryTestExample() {
		// Directory Creation and Deletion Example

		File path = new File("./peer31");
		boolean success = (path.mkdirs());
		if (success) {
			System.out.println("Directory created");
			System.out.println("Press \"ENTER\" to continue...");
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			scanner.close();

			try {
				success = deleteRecursive(path);
			} catch (FileNotFoundException e) {
				System.out.println("Problem deletingRecursively: " + e);
			}

			if (success) {
				System.out.println("Success Deleting Directory");
			} else {
				System.out.println("Directory not delted.");
			}

		} else {
			System.out.println("Directory not created");
		}
	}

	public static void socketsTestExample() throws InterruptedException {
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

	// Taken verbatim (with a minor change) from Paulitex's recursive directory
	// deletion solution on
	// https://stackoverflow.com/questions/779519/delete-directories-recursively-in-java
	public static boolean deleteRecursive(File path) throws FileNotFoundException {
		if (!path.exists())
			throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}
}
