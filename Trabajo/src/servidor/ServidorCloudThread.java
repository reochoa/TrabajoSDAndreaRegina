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

	private void subirArchivo(String cadena) {
		String[] cadenaAux = cadena.split(ProtocoloComunicacion.SEPARATOR);
		String fileName = cadenaAux[0];
		String hash = cadenaAux[1];
		BufferedInputStream bis = null;
		OutputStream os = null;
		String pathFile = servidorCloud.getPathArchivos() + "/" + username + fileName;
		try {
			File file = new File(pathFile);
			byte[] bytes = new byte[1024];
			bis = new BufferedInputStream(new FileInputStream(file));
			os = socket.getOutputStream();
			System.out.println("Subiendo: " + pathFile);
			int leidos = bis.read(bytes);
			while (leidos != -1) {
				os.write(bytes, 0, leidos);
				os.flush();
				leidos = bis.read(bytes);
			}
			System.out.println("Subido");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// CERRAR
		}

	}

	public void descargarArchivo() throws IOException {

		File file = new File(servidorCloud.getPathArchivos() + username + "/" + filename);

		byte[] bytes = new byte[1024];
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
		OutputStream output = socket.getOutputStream();
		int leidos = input.read(bytes);
		while (leidos != -1) {
			output.write(bytes, 0, leidos);
			output.flush();
			leidos = input.read(bytes);
		}
		input.close();
		System.out.println("--> llegando");
//		PrintWriter writer = new PrintWriter(output, true);
//		
//		// Fin de la comunicacion, se cierran sockets
//		writer.println(ProtocoloComunicacion.END);
//		writer.println(ProtocoloComunicacion.END);
//		System.out.println("--> llegando" + writer);
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

		Map<String, Archivo> archivos = Archivo.leerArchivos(servidorCloud.getPathArchivos() + username);
		System.out.println(archivos);

		for (String filename : archivos.keySet()) {
			System.out.println("[ARCHIVOS " + username + "]" + filename);
			Archivo aux = archivos.get(filename);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			writer.println(filename + "#" + aux.getHash() + "#" + dateFormat.format(aux.getFechaModificacion()));
		}

		// Fin de la comunicacion, se cierran sockets
		writer.println(ProtocoloComunicacion.END);
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
				System.out.println("----------------FIN FIN DESCARGAR----------------------");
				break;
			}

			socket.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
