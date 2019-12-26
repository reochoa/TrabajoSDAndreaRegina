package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorCloudListener {

	public static void main(String[] args) {
		if (args.length > -1) {
	    	
			ServidorCloud servidorCloud = new ServidorCloud();
//			servidorCloud.setServerPort(Integer.parseInt(args[0]));
//			servidorCloud.setPathArchivos(args[1]);
			try {
				ServerSocket serverSocket = new ServerSocket(servidorCloud.getServerPort());
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						System.out.println("[SERVER] Conexion aceptada.....");

						ServidorCloudThread servidorCloudThread = new ServidorCloudThread(servidorCloud, socket);
						servidorCloudThread.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
