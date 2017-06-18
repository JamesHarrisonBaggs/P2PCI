package Server;

public class UPort {
	private String hostname;

	private int port;

	public UPort(String hostname, int port) {
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
	
	public String toString() {
		return hostname + " " + port;
	}
}
