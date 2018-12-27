package cliente;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClienteTest {

	public final static int SOCKET_PORT = 8080;
	public final static String SERVER = "localhost";
	public final static String FILE_TO_RECEIVED = "D:/temp/descargado.txt";

	public final static int FILE_SIZE = 6022386;

	public static void main(String[] args) throws IOException {
		BufferedOutputStream bos = null;
		Socket sock = null;
		try {
			sock = new Socket(SERVER, SOCKET_PORT);
			System.out.println("Conectando ...");
			byte[] bytes = new byte[1024];
			InputStream is = sock.getInputStream();
			bos = new BufferedOutputStream(new FileOutputStream(FILE_TO_RECEIVED));
			int leidos = is.read(bytes);
			while (leidos != -1) {
				bos.write(bytes, 0, leidos);
				bos.flush();
				leidos = is.read(bytes);
			}

			System.out.println("Archivo " + FILE_TO_RECEIVED + " downloaded");
		} finally {
			if (bos != null)
				bos.close();
			if (sock != null)
				sock.close();
		}
	}

}
