package cliente;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteTest {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", 8080);
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			
			

			writer.println("mierda");

//			writer.write("mierda\r\n");
//			writer.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
