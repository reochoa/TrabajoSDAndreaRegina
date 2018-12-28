package servidor;

import comun.Archivo;
import comun.ProtocoloComunicacion;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Map;

public class ServidorCloudThread implements Runnable {

	private ServidorCloud servidorCloud;
	private Socket socket;
	private String username;
	private String password;
	private String filename;

	public ServidorCloudThread(ServidorCloud servidorCloud, Socket socket) {
		this.servidorCloud = servidorCloud;
		this.socket = socket;
	}

	private void subirArchivo() throws IOException {
		DataInputStream socketIn = new DataInputStream(socket.getInputStream());
		byte[] bytes = new byte[512];
		BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(servidorCloud.getPathArchivos() + username + "/" + filename));
		int leidos = socketIn.read(bytes);
		while (leidos != -1) {
			fileOut.write(bytes, 0, leidos);
			fileOut.flush();
			leidos = socketIn.read(bytes);
		}
		fileOut.close();
		socket.close();
	}

	public void descargarArchivo() throws IOException {
		File file = new File(servidorCloud.getPathArchivos() + username + "/" + filename);
		byte[] bytes = new byte[512];
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		
		output.writeUTF(ProtocoloComunicacion.END  + ProtocoloComunicacion.SEPARATOR + "55" + ProtocoloComunicacion.BR);
		output.writeUTF(ProtocoloComunicacion.BR);
		output.flush();
		
		int leidos = input.read(bytes);
		while (leidos != -1) {
			output.write(bytes, 0, leidos);
			output.flush();
			leidos = input.read(bytes);
		}
		input.close();	
		socket.close();
	}

	/**
	 * @throws IOException
	 */
	private void leerArchivosUsuario() throws IOException {
		InputStream input = socket.getInputStream();
		OutputStream ouput = socket.getOutputStream();

		File file = new File(servidorCloud.getPathArchivos() + username);
		if (!file.exists()) {
			file.mkdirs();
		}

		PrintWriter writer = new PrintWriter(ouput, true);
		Map<String, Archivo> archivos = Archivo.leerArchivos(servidorCloud.getPathArchivos() + username, true);

		for (String filename : archivos.keySet()) {
			Archivo aux = archivos.get(filename);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			writer.println(filename + ProtocoloComunicacion.SEPARATOR + aux.getHash() + ProtocoloComunicacion.SEPARATOR + dateFormat.format(aux.getFechaModificacion()) + ProtocoloComunicacion.SEPARATOR + aux.getId());
		}

		// Fin de la comunicacion, se cierran socket
		writer.println(ProtocoloComunicacion.END);
		socket.close();
	}

	@Override
	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String cadena = bufferedReader.readLine();
			String[] cadenas = cadena.split(ProtocoloComunicacion.SEPARATOR);
			username = cadenas[1];
			password = cadenas[2];

			switch (cadenas[0]) {
			case ProtocoloComunicacion.SYNC:
				System.out.println("-------------------SINCRONIZACION----------------------");
				leerArchivosUsuario();
				System.out.println("----------------FIN SINCRONIZACION----------------------");
				break;
			case ProtocoloComunicacion.DOWNLOAD:
				System.out.println("------------------------DESCARGAR----------------------");
				filename = cadenas[3];
				descargarArchivo();
				System.out.println("--------------------FIN DESCARGAR----------------------");
				break;
			case ProtocoloComunicacion.UPLOAD:
				System.out.println("------------------------SUBIR----------------------");
				filename = cadenas[3];
				subirArchivo();
				System.out.println("-----------------FIN SUBIR-------------------------");
				break;
			}

		

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
