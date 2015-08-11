package nvluan.rikkei.adapter;

public class UserItem {
	int id;
	String username;
	String password;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	boolean password_enable;

	public boolean isPassword_enable() {
		return password_enable;
	}

	public void setPassword_enable(boolean password_enable) {
		this.password_enable = password_enable;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
