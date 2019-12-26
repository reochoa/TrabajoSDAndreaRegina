package cliente;

import java.io.*;
import java.net.Socket;

import comun.Archivo;
import comun.ProtocoloComunicacion;

public class ClienteDownloadThread implements Runnable {

    private ClienteCloud cliente;
    private Archivo archivo;

    public ClienteDownloadThread(ClienteCloud cliente, Archivo archivo) {
        this.cliente = cliente;
        this.archivo = archivo;
    }

    @Override
    public void run() {
        try {
            System.out.println("[CLIENT] Download file " + cliente.getUsername() + "@" + archivo.getFileName());

            Socket socket = new Socket(cliente.getServerHost(), cliente.getServerPort());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF(ProtocoloComunicacion.getComandoDownload(cliente.getUsername(), cliente.getPassword(), archivo.getFileName()));
            outputStream.writeUTF(ProtocoloComunicacion.BR);

            String linea = inputStream.readUTF();
            String[] cadenas = linea.split(ProtocoloComunicacion.SEPARATOR);
            int id = Integer.parseInt(cadenas[1]);

            linea = inputStream.readUTF(); // descartamos linea en blanco

            byte[] bytes = new byte[512];
            BufferedOutputStream fileOutput = new BufferedOutputStream(
                    new FileOutputStream(cliente.getPathCarpetaPersonal() + archivo.getFileName()));
            int leidos = inputStream.read(bytes);
            while (leidos != -1) {
                fileOutput.write(bytes, 0, leidos);
                leidos = inputStream.read(bytes);
            }
            fileOutput.flush();
            fileOutput.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
