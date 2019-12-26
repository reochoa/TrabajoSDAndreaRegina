package comun;


public class Main {
	public static void main(String[] args) {

		if(LoginUsuario.existUser("reochoa")) {
			System.out.println("Ya existe username");
		}else {
			System.out.println("No existe username asi, registrando usuario");
			LoginUsuario.addUser(new User ("reochoa","holaholita"));
		}
		for (String username : LoginUsuario.getUsernames()) {
			System.out.println(username);
		}
	}

}
