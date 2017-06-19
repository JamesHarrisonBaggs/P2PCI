package p2p;

public class RFC {
	
	private int number;
	
	private String title;
	
	private String hostname;
	
	public RFC() {
		number = 0;
		title = null;
		hostname = null;
	}
	
	public RFC(int number, String title, String hostname) {
		setNumber(number);
		setTitle(title);
		setHostname(hostname);
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

	public String toString() {
		return "RFC " + number + " " + title + " " + hostname;
	}
}
