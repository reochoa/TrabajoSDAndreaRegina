package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTest {

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(8080);

			while (true) {
				Socket socket = serverSocket.accept();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String cadena = bufferedReader.readLine();

				System.out.println(cadena);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
