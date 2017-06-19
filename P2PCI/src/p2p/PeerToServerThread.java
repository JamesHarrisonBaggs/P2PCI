package p2p;

import java.io.IOException;

public class PeerToServerThread implements Runnable{
	
	public void run(){
		try {
			Peer.handleConsoleCommands();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
