package cliente;

import java.io.*;
import java.net.Socket;

import comun.Archivo;
import comun.ProtocoloComunicacion;

public class ClienteUploadThread implements Runnable {

	private ClienteCloud cliente;
	private Archivo archivo;

	public ClienteUploadThread(ClienteCloud cliente, Archivo archivo) {
		this.cliente = cliente;
		this.archivo = archivo;
	}

	@Override
	public void run() {

		try {
			System.out.println("[CLIENT] Upload file " + cliente.getUsername() + "@" + archivo.getFileName());

			Socket socket = new Socket(cliente.getServerHost(), cliente.getServerPort());
			File file = new File(cliente.getPathCarpetaPersonal() + archivo.getFileName());

			byte[] bytes = new byte[512];
			BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

			outputStream.writeUTF(ProtocoloComunicacion.getComandoUpload(cliente.getUsername(), cliente.getPassword(), archivo.getFileName()));
			outputStream.writeUTF(ProtocoloComunicacion.BR);

			int leidos = fileIn.read(bytes);
			while (leidos != -1) {
				outputStream.write(bytes, 0, leidos);
				leidos = fileIn.read(bytes);
			}
			outputStream.flush();
			fileIn.close();

			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();

		}

	}
}