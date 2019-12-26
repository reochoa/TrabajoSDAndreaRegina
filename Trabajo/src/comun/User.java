package comun;


public class User {
	private String username;
	private String password;
	
	public User (String username, String password) {
		this.username=username;
		this.password=password;
	}
	public String getUsername() {
		return this.username;
	}
	public String getPassword() {
		return this.password;
	}
	public boolean equals (User user) {
		 return this.username.equals(user.getUsername());
	}
	public String toString() {
		return ("Username: "+ this.getUsername());
		
	}

}
