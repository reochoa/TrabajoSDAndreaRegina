package cliente;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import comun.Archivo;
import comun.EstadoArchivo;
import comun.ProtocoloComunicacion;

public class ClienteCloud {

	private String username;
	private String password;
	private String serverHost;
	private int serverPort;
	private String pathCarpetaPersonal;
	// Map de nombreFichero y la representacion de archivo(por SO el nombre tiene
	// que ser unico)
	private Map<String, Archivo> archivosLocales;
	private Map<String, Archivo> archivosServidor;

	public ClienteCloud() {
		archivosLocales = new HashMap<>();
		archivosServidor = new HashMap<>();
		leerConfiguracion();
	}

	/**
	 * Lee la configuración desde un archivo properties
	 */
	public void leerConfiguracion() {
		serverHost = "localhost";
		serverPort = 7070;
		username = "alejandro";
		password = "password";
//        pathCarpetaPersonal = "/home/alejandro/temp/";
		pathCarpetaPersonal = "D:/temp/";
	}

	@Override
	public String toString() {
		return "cliente.ClienteCloud{" + "username='" + username + '\'' + ", password='" + password + '\''
				+ ", serverHost='" + serverHost + '\'' + ", serverPort=" + serverPort + ", pathCarpetaPersonal='"
				+ pathCarpetaPersonal + '\'' + '}';
	}

	/**
	 * Lee los archivos de la carpeta local y recupera los del servidor
	 */
	public void comprobarArchivos() throws IOException {
		// archivos locales
		archivosLocales = Archivo.leerArchivos(pathCarpetaPersonal);
		System.out.println(archivosLocales);

		// archicos servidor
		Socket socket = new Socket(serverHost, serverPort);
		InputStream input = socket.getInputStream();
		OutputStream ouput = socket.getOutputStream();

		PrintWriter writer = new PrintWriter(ouput, true);
		writer.println(ProtocoloComunicacion.getComandoSync(username, password));

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String respuesta = reader.readLine();

		while (!ProtocoloComunicacion.END.equals(respuesta)) {
			System.out.println(respuesta);
			String[] cadenas = respuesta.split("#");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			Date fechaModificacion = new Date();
			try {
				fechaModificacion = dateFormat.parse(cadenas[2]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Archivo aux = new Archivo(cadenas[0], fechaModificacion, cadenas[1]);
			archivosServidor.put(cadenas[0], aux);
			respuesta = reader.readLine();
		}

		for (String filename : archivosLocales.keySet()) {
			Archivo local = archivosLocales.get(filename);

			if (archivosServidor.containsKey(filename)) {
				Archivo servidor = archivosServidor.get(filename);
				if (!local.getHash().equals(servidor.getHash())) {
					if (local.getFechaModificacion().before(servidor.getFechaModificacion())) {
						local.setEstado(EstadoArchivo.desactualizado);
						servidor.setEstado(EstadoArchivo.modificado);
					} else {
						local.setEstado(EstadoArchivo.modificado);
						servidor.setEstado(EstadoArchivo.desactualizado);
					}
				} else {
					local.setEstado(EstadoArchivo.sinCambios);
					servidor.setEstado(EstadoArchivo.sinCambios);
				}

			}

		}
	}

	public void subirArchivo(Archivo archivo) {

	}

	public void descargarArchivo(Archivo archivo) throws IOException {
		// archicos servidor
		System.out.println("------------------------>" + archivo);
		Socket socket = new Socket(serverHost, serverPort);
		InputStream input = socket.getInputStream();
		OutputStream ouput = socket.getOutputStream();

		PrintWriter writer = new PrintWriter(ouput, true);
		writer.println(ProtocoloComunicacion.getComandoDownload(username, password, archivo.getFileName()));
		
		System.out.println("------------------------>" + archivo);
		
		byte[] bytes = new byte[1024];
		BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(pathCarpetaPersonal + archivo.getFileName()));
		int leidos = input.read(bytes);
		while (leidos != -1) {
			System.out.println(leidos);
			fileOutput.write(bytes, 0, leidos);
			fileOutput.flush();
			leidos = input.read(bytes);
		}
		fileOutput.close();
		
//		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//		String respuesta = reader.readLine();
//		 respuesta = reader.readLine();
//		 
//		 while(respuesta == null) {
//			 respuesta = reader.readLine();
//			 System.out.println(respuesta);
//		 }
//		
//		if(ProtocoloComunicacion.END.equals(respuesta)) {
//			
//		}
		
	}

	public Map<String, Archivo> getArchivosLocales() {
		return archivosLocales;
	}

	public void setArchivosLocales(Map<String, Archivo> archivosLocales) {
		this.archivosLocales = archivosLocales;
	}

	// ----------------------------------------------------
	// PRINCIPAL
	// ----------------------------------------------------
	public static void main(String[] args) {
		try {
			ClienteCloud clienteCloud = new ClienteCloud();
//            clienteCloud.login();
			clienteCloud.comprobarArchivos();

			// Comparar archivosLocales con archivosServidor
			// segun diferencias, habra que descargar archivos o subir archivos

			for (String filename : clienteCloud.archivosLocales.keySet()) {
				Archivo local = clienteCloud.archivosLocales.get(filename);
				System.out.println(local.toString());

				if (local.getEstado().equals(EstadoArchivo.modificado)
						|| local.getEstado().equals(EstadoArchivo.nuevo)) {
					clienteCloud.subirArchivo(local);
				}
			}
			for (String filename : clienteCloud.archivosServidor.keySet()) {
				Archivo servidor = clienteCloud.archivosServidor.get(filename);
				System.out.println(servidor.toString());

				if (servidor.getEstado().equals(EstadoArchivo.modificado)
						|| servidor.getEstado().equals(EstadoArchivo.nuevo)) {
					clienteCloud.descargarArchivo(servidor);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
