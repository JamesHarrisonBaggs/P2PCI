package Server;

public class RFC {
	
	private int number;
	
	private String title;
	
	private String hostname;
	
	private int port;
	
	public RFC() {
		number = 0;
		title = null;
		hostname = null;
		setPort(0);
	}
	
	public RFC(int number, String title, String hostname, int port) {
		setNumber(number);
		setTitle(title);
		setHostname(hostname);
		setPort(port);
	}

	public int getNumber() {
		return number;
	}

	private void setNumber(int number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	private void setTitle(String title) {
		this.title = title;
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

	public void setPort(int port) {
		this.port = port;
	}

	public String toString() {
		return "RFC " + number + " " + title + " " + hostname + " " + port;
	}
}
