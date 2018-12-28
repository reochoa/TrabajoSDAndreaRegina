package cliente;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
			Socket socket = new Socket(cliente.getServerHost(), cliente.getServerPort());
			DataInputStream input = new DataInputStream(socket.getInputStream());
			OutputStream ouput = socket.getOutputStream();

			PrintWriter writer = new PrintWriter(ouput, true);
			writer.println(ProtocoloComunicacion.getComandoDownload(cliente.getUsername(), cliente.getPassword(),
					archivo.getFileName()));

			String linea = input.readLine();
			String[] cadenas = linea.split(ProtocoloComunicacion.SEPARATOR);
			int id = Integer.parseInt(cadenas[1]);

			linea = input.readLine(); // descartamos linea en blanco

			byte[] bytes = new byte[512];
			BufferedOutputStream fileOutput = new BufferedOutputStream(
					new FileOutputStream(cliente.getPathCarpetaPersonal() + archivo.getFileName()));
			int leidos = input.read(bytes);
			while (leidos != -1) {
				fileOutput.write(bytes, 0, leidos);
				leidos = input.read(bytes);
			}
			fileOutput.flush();
			fileOutput.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
