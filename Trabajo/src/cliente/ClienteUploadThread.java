package cliente;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
			Socket socket = new Socket(cliente.getServerHost(), cliente.getServerPort());
			File file = new File(cliente.getPathCarpetaPersonal() + archivo.getFileName());

			byte[] bytes = new byte[512];
			BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file));
			DataOutputStream socketOut = new DataOutputStream(socket.getOutputStream());

			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(ProtocoloComunicacion.getComandoUpload(cliente.getUsername(), cliente.getPassword(), archivo.getFileName()));

			int leidos = fileIn.read(bytes);
			while (leidos != -1) {
				System.out.println(leidos + " " + archivo.getFileName());
				socketOut.write(bytes, 0, leidos);
				leidos = fileIn.read(bytes);
			}
			socketOut.flush();
			fileIn.close();
			socket.close();
		} catch (IOException ex) {

		}

	}
}