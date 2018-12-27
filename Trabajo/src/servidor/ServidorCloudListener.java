package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorCloudListener {

    public static void main(String[] args) {

        ServidorCloud servidorCloud = new ServidorCloud();
        try {
            ServerSocket serverSocket = new ServerSocket(servidorCloud.getServerPort());
            while (true) {
                System.out.println("[LISTENER] Esperando conexiones.....");
                Socket socket = serverSocket.accept();
                System.out.println("[LISTENER] Conexion aceptada.....");

                ServidorCloudThread servidorCloudThread = new ServidorCloudThread(servidorCloud, socket);
                servidorCloudThread.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
