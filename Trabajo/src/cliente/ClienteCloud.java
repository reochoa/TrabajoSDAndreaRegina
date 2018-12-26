package cliente;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comun.Archivo;
import comun.EstadoArchivo;

public class ClienteCloud {
	public final static String CMD_SEPARATOR = "#";
	public final static String CMD_LOGIN = "LOGIN";
	public final static String CMD_UPLOAD = "UPLOAD";
	public final static String CMD_DOWNLOAD = "DOWNLOAD";

	private String username;
	private String password;
	private String serverHost;
	private int serverPort;
	private String pathCarpetaPersonal;
	// Map de nombreFichero y la representacion de archivo(por SO el nombre tiene
	// que ser unico)
	private Map<String, Archivo> archivos;

	public ClienteCloud() {
		archivos = new HashMap<>();
		leerConfiguracion();
	}

	/**
	 * Lee la configuración desde un archivo properties
	 */
	public void leerConfiguracion() {
		serverHost = "localhost";
		serverPort = 8080;
		username = "regina";
		password = "password";
		pathCarpetaPersonal = "D:/temp/";
	}

	@Override
	public String toString() {
		return "ClienteCloud{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", serverHost='"
				+ serverHost + '\'' + ", serverPort=" + serverPort + ", pathCarpetaPersonal='" + pathCarpetaPersonal
				+ '\'' + '}';
	}

	public boolean login() throws IOException {
		System.out.println("Conectando...");
		Socket socket = new Socket(serverHost, serverPort);
		System.out.println("Haciendo login");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		StringBuilder stb = new StringBuilder();
		stb.append(CMD_LOGIN).append(CMD_SEPARATOR);
		stb.append(username).append(CMD_SEPARATOR).append(password);

		System.out.println(stb.toString());
		writer.write(stb.toString() +"\r\n");
		writer.flush();

		System.out.println("Esperando confirmacion");
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String respuesta = reader.readLine();
		
		System.out.println("Respuesta " + respuesta);

		socket.close();

		return "OK".equals(respuesta);
	}

	public void leerArchivos() {
		archivos.clear();
		try {
			File carpetaPersonal = new File(pathCarpetaPersonal);
			if (carpetaPersonal.isDirectory() && carpetaPersonal.canRead()) {
				List<File> files = Arrays.asList(carpetaPersonal.listFiles());
				// Hash, algoritmo SHA-1 (identificador unico del archivo)
				MessageDigest messageDigestSha = MessageDigest.getInstance("SHA-1");

				byte[] buffer = new byte[1024];
				for (File file : files) {
					FileInputStream fis = new FileInputStream(file);
					int nread = 0;
					while ((nread = fis.read(buffer)) != -1) {
						messageDigestSha.update(buffer, 0, nread);
					}

					byte[] result = messageDigestSha.digest();
					StringBuffer sb = new StringBuffer();
					// Pasar HASH a base 64 (como se suele representar)
					for (int i = 0; i < result.length; i++) {
						sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
					}

					System.out.println("Archivo{" + file.getName() + " " + sb.toString() + "}");
					Archivo archivo = new Archivo(file.getName(), new Date(file.lastModified()), EstadoArchivo.nuevo,
							sb.toString());
					archivos.put(file.getName(), archivo);
				}
			}
		} catch (NoSuchAlgorithmException | IOException ex) {
			ex.printStackTrace();
		}
	}

	public void subirArchivo(Archivo archivo) {
		StringBuilder stb = new StringBuilder();
		stb.append(CMD_UPLOAD).append(CMD_SEPARATOR);
		stb.append(archivo.getFileName()).append(CMD_SEPARATOR).append(archivo.getHash());

		try {
			Socket socket = new Socket(serverHost, serverPort);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write(stb.toString() + "\r\n");
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String respuesta = reader.readLine();
			if ("OK".equals(respuesta)) {
				FileInputStream input = new FileInputStream(pathCarpetaPersonal + archivo.getFileName());
				byte[] buffer = new byte[1024];
				int leidos;
				BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
				//leidos = input.read(buffer);
				while ((leidos = input.read(buffer)) != -1) {
					output.write(buffer, 0, leidos);
				}
				input.close();
				respuesta = reader.readLine();
				if ("COMPLETE".equals(respuesta)) {
					output.close();
					input.close();
					writer.close();
					reader.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void comprobarArchivos() {
		try {
			Socket socket = new Socket(serverHost, serverPort);
			InputStream input = socket.getInputStream();
			InputStream ouput = socket.getInputStream();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public Map<String, Archivo> getArchivos() {
		return archivos;
	}

	public void setArchivos(Map<String, Archivo> archivos) {
		this.archivos = archivos;
	}

	// ----------------------------------------------------
	// PRINCIPAL
	// ----------------------------------------------------
	public static void main(String[] args) {
		System.out.println("conectando con el servidor");
		try {
			ClienteCloud clienteCloud = new ClienteCloud();
			clienteCloud.login();
			

			clienteCloud.leerArchivos();

			for (String filename : clienteCloud.archivos.keySet()) {

				clienteCloud.subirArchivo(clienteCloud.archivos.get(filename));
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
