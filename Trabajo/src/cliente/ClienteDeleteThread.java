package cliente;

import comun.Archivo;
import comun.ProtocoloComunicacion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteDeleteThread implements Runnable {

    private ClienteCloud cliente;
    private Archivo archivo;

    public ClienteDeleteThread(ClienteCloud cliente, Archivo archivo) {
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

            outputStream.writeUTF(ProtocoloComunicacion.getComandoDelete(cliente.getUsername(), cliente.getPassword(), archivo.getFileName()));
            outputStream.writeUTF(ProtocoloComunicacion.BR);

            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException ex) {

        }
    }
}
