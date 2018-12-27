package servidor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTest {

	public final static int SOCKET_PORT = 8080;
	public final static String FILE_TO_SEND = "D:/srv/alejandro/apestas.txt";

	public static void main(String[] args) throws IOException {
		BufferedInputStream bis = null;
		OutputStream os = null;
		ServerSocket serverSocket = null;
		Socket sock = null;
		try {
			serverSocket = new ServerSocket(SOCKET_PORT);
			while (true) {
				System.out.println("Esperando conexion...");
				try {
					sock = serverSocket.accept();
					System.out.println("Conexion aceptada : ");
					File file = new File(FILE_TO_SEND);
					byte[] bytes = new byte[1024];
					bis = new BufferedInputStream( new FileInputStream(file));
					os = sock.getOutputStream();
					System.out.println("Subiendo " + FILE_TO_SEND );
					int leidos=bis.read(bytes);
					while(leidos!=-1) {
						os.write(bytes,0,leidos);
						os.flush();
						leidos=bis.read(bytes);
					}
					System.out.println("OK");
				} finally {
					if (bis != null)
						bis.close();
					if (os != null)
						os.close();
					if (sock != null)
						sock.close();
				}
			}
		} finally {
			if (serverSocket != null)
				serverSocket.close();
		}
	}

	public void subirArchivo(String cadena) {
			
	}

}
