package Server;

public class Peer {
	
	private String hostname;
	
	private int port;
	
	public Peer(String hostname, int port) {
		setHostname(hostname);
		setPort(port);
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
}
