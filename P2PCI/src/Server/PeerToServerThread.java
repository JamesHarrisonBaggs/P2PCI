package Server;

import java.io.IOException;

public class PeerToServerThread implements Runnable{
	
	public void run(){
		try {
			System.out.println("p2sthread");
			Peer.handleConsoleCommands();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
