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
	}

	public Map<String, Archivo> getArchivosLocales() {
		return archivosLocales;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getPathCarpetaPersonal() {
		return pathCarpetaPersonal;
	}

	public Map<String, Archivo> getArchivosServidor() {
		return archivosServidor;
	}

	// ----------------------------------------------------
	// PRINCIPAL
	// ----------------------------------------------------
	public static void main(String[] args) {
		ClienteCloud cliente = new ClienteCloud();
		if (args.length > -1) {
//			cliente.serverHost = args[0];
//			cliente.serverPort = Integer.parseInt(args[1]);
//			cliente.username = args[2];
//			cliente.password = args[3];
//			cliente.pathCarpetaPersonal = args[4];

		cliente.serverHost = "localhost";
		cliente.serverPort = 7070;
		cliente.username = "reochoa";
		cliente.password = "password";
		cliente.pathCarpetaPersonal = "D:/temp/";

			try {
				while (true) {

					ClienteSyncThread syncThread = new ClienteSyncThread(cliente);

					Map<String, Archivo> aux = Archivo.leerArchivos(cliente.getPathCarpetaPersonal());
					cliente.getArchivosLocales().putAll(aux);
					syncThread.run();

					// Comparar archivosLocales con archivosServidor
					// segun diferencias, habra que descargar archivos o subir archivos

					for (String filename : cliente.getArchivosLocales().keySet()) {
						Archivo local = cliente.archivosLocales.get(filename);

						if (local.getEstado().equals(EstadoArchivo.modificado)
								|| local.getEstado().equals(EstadoArchivo.nuevo)) {

							ClienteUploadThread uploadThread = new ClienteUploadThread(cliente, local);
							uploadThread.run();
						}
					}
					for (String filename : cliente.archivosServidor.keySet()) {
						Archivo servidor = cliente.archivosServidor.get(filename);

						if (servidor.getEstado().equals(EstadoArchivo.modificado)
								|| servidor.getEstado().equals(EstadoArchivo.nuevo)) {

							ClienteDownloadThread downloadThread = new ClienteDownloadThread(cliente, servidor);
							downloadThread.run();

						}
					}
					Thread.currentThread().sleep(20000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
