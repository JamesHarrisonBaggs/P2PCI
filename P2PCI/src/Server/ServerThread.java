package Server;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

// Modified from example at https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
public class ServerThread implements Runnable {
	protected Socket socket;
	
	protected int clientPort;
	
	protected String version;
	
	protected String theKey = "yx31nyb071dj98s";
	
	protected static SecretKeySpec secretKey;

	protected static byte[] key;
	
	public ServerThread(Socket clientSocket) {
		this.socket = clientSocket;
		version = "P2P-CI/1.0";
		
		//example from http://aesencryption.net/
		try {
			key = MessageDigest.getInstance("SHA-1").digest(theKey.getBytes("UTF-8"));
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
				if (method.equals("LOGIN")) {
					if (login(command)) {
						out.println("User authenticated\n");
					} else {
						out.println("Invalid User\n");
						socket.close();
						return;
					}
				} else if (method.equals("ADD")) {
					RFC rfc = new RFC();
					try {
						rfc = add(command);
					} catch (InputMismatchException e) {
						out.println(version + " 400 Bad Request\n");
						continue;
					} catch (IllegalArgumentException e) {
						out.println(version + " 505 P2P-CI Version Not Supported\n");
						continue;
					}
					out.println(version + "200 OK");
					int port = 0;
					for (int i = 0; i < Server.ports.size(); i++) {
						UPort p = Server.ports.get(i);
						if (rfc.getHostname().equals(p.getHostname())) {
							port = p.getPort();
						}
					}
					out.println(rfc.toString() + " " + port + "\n");
				} else if (method.equals("LOOKUP")) {
					LinkedList<RFC> results = new LinkedList<RFC>();
					try {
						results = find(command);
					} catch (InputMismatchException e) {
						out.println(version + " 400 Bad Request\n");
						continue;
					} catch (IllegalArgumentException e) {
						out.println(version + " 505 P2P-CI Version Not Supported\n");
						continue;
					}
					if (results.size() == 0) {
						out.println(version + " 404 Not Found\n");
						continue;
					}
					out.println(version + "200 OK");
					int port = 0;
					for (int i = 0; i < results.size(); i++) {
						RFC rfc = results.get(i);
						for (int j = 0; j < Server.ports.size(); j++) {
							UPort p = Server.ports.get(j);
							if (rfc.getHostname().equals(p.getHostname())) {
								port = p.getPort();
							}
						}
						out.println(rfc.toString() + " " + port);
					}
					out.println();
				} else if (method.equals("LIST")) {
					LinkedList<RFC> all = new LinkedList<RFC>();
					try {
						all = list(command);
					} catch (IllegalArgumentException e) {
						out.println(version + " 505 P2P-CI Version Not Supported\n");
						continue;
					}
					out.println(version + "200 OK");
					int port = 0;
					out.println("Peers: ");
					for (int i = 0; i < Server.ports.size(); i++) {
						out.println(Server.ports.get(i).toString());
					}
					out.println("RFCs: ");
					for (int i = 0; i < all.size(); i++) {
						RFC rfc = all.get(i);
						for (int j = 0; j < Server.ports.size(); j++) {
							UPort p = Server.ports.get(j);
							if (rfc.getHostname().equals(p.getHostname())) {
								port = p.getPort();
							}
						}
						out.println(rfc.toString() + " " + port);
					}
					out.println();
				} else if (method.equals("QUIT")) {
					UPort p = quit(command);
					int i = 0;
					do {
						for(i = 0; i < Server.RFCs.size(); i++) {
							if (Server.RFCs.get(i).getHostname().equals(p.getHostname())) {
								Server.RFCs.delete(i);
								break;
							}
						}
					} while (Server.RFCs.size() != i);
					do {
						for(i = 0; i < Server.ports.size(); i++) {
							if (Server.ports.get(i).getHostname().equals(p.getHostname())) {
								Server.ports.delete(i);
								break;
							}
						}
					} while (Server.ports.size() != i);
					socket.close();
					return;
				}
			}
		} catch (IOException e) {
			System.out.println("IO Exception while reading lines " + e);
			return;
		}	
	}

	private boolean login(String msg) throws FileNotFoundException {
		Scanner sc = new Scanner(msg);
		Scanner first = new Scanner(sc.nextLine());
		first.next();
		String user = first.next();
		String encoded = first.next();
		first.close();
		sc.close();
		String decoded = "";
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			decoded = new String(cipher.doFinal(Base64.getDecoder().decode(encoded)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Scanner fs = new Scanner(new File("/Test/logins.txt"));
		while(fs.hasNextLine()) {
			Scanner ls = new Scanner(fs.nextLine());
			if (user.equals(ls.next()) && decoded.equals(ls.next())) {
				ls.close();
				fs.close();
				return true;
			}
			ls.close();
		}
		fs.close();
		return false;
	}

	public RFC add(String msg) {
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
		Scanner second = new Scanner(sc.nextLine());
		second.next();
		String hostname = second.next();
		second.close();
		Scanner third = new Scanner(sc.nextLine());
		third.next();
		int port = third.nextInt();
		third.close();
		Scanner forth = new Scanner(sc.nextLine());
		String titleLine = forth.nextLine();
		String title = titleLine.split(" ", 2)[1];
		forth.close();
		sc.close();
		UPort p = new UPort(hostname, port);
		addPort(p);
		RFC rfc = new RFC(number, title, hostname);
		Server.RFCs.addFirst(rfc);
		return rfc;
	}
	
	private void addPort(UPort p) {
		if (Server.ports.size() == 0) {
			Server.ports.addFirst(p);
			return;
		}
		for (int i = 0; i < Server.ports.size(); i++) {
			if (Server.ports.get(i).getHostname().equals(p.getHostname())) {
				return;
			}
		}
		Server.ports.addFirst(p);
	}

	public LinkedList<RFC> find(String msg) {
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
		Scanner second = new Scanner(sc.nextLine());
		second.next();
		String hostname = second.next();
		second.close();
		Scanner third = new Scanner(sc.nextLine());
		third.close();
		Scanner forth = new Scanner(sc.nextLine());
		String titleLine = forth.nextLine();
		String title = titleLine.split(" ", 2)[1];
		forth.close();
		sc.close();
		RFC rfc = new RFC(number, title, hostname);
		ListIterator<RFC> iterator = Server.RFCs.listIterator();
		LinkedList<RFC> results = new LinkedList<RFC>();
		while (iterator.hasNext()) {
			RFC next = iterator.next();
			if (next.getNumber() == rfc.getNumber()) {
				results.addFirst(next);
			}
		}
		return results;
	}
	
	public LinkedList<RFC> list(String msg) {
		Scanner sc = new Scanner(msg);
		Scanner first = new Scanner(sc.nextLine());
		String numberLine = first.nextLine();
		String clientVersion = numberLine.split(" ")[2];
		if (!clientVersion.equals(version)) {
			first.close();
			sc.close();
			throw new IllegalArgumentException();
		}
		first.close();
		sc.close();
		return Server.RFCs;
	}
	
	public UPort quit(String msg) {
		Scanner sc = new Scanner(msg);
		sc.nextLine();
		Scanner second = new Scanner(sc.nextLine());
		second.next();
		String hostname = second.next();
		second.close();
		Scanner third = new Scanner(sc.nextLine());
		third.next();
		int port = third.nextInt();
		third.close();
		sc.close();
		UPort p = new UPort(hostname, port);
		return p;
	}
}
