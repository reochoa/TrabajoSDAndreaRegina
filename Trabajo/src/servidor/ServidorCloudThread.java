package servidor;

import comun.Archivo;
import comun.LoginUsuario;
import comun.ProtocoloComunicacion;
import comun.User;

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

	private void subirArchivo() {
		try {
			DataInputStream socketIn = new DataInputStream(socket.getInputStream());
			byte[] bytes = new byte[512];
			String linea = socketIn.readUTF(); // descartamos linea en blanco

			BufferedOutputStream fileOut = new BufferedOutputStream(
					new FileOutputStream(servidorCloud.getPathArchivos() + username + "/" + filename));
			int leidos = socketIn.read(bytes);
			while (leidos != -1) {
				fileOut.write(bytes, 0, leidos);
				leidos = socketIn.read(bytes);
			}
			fileOut.flush();
			fileOut.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void descargarArchivo() {
		try {
			File file = new File(servidorCloud.getPathArchivos() + username + "/" + filename);
			byte[] bytes = new byte[512];
			BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(file));
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

			outputStream.writeUTF(ProtocoloComunicacion.END + ProtocoloComunicacion.SEPARATOR + "55");
			outputStream.writeUTF(ProtocoloComunicacion.BR);

			int leidos = fileInput.read(bytes);
			while (leidos != -1) {
				outputStream.write(bytes, 0, leidos);
				leidos = fileInput.read(bytes);
			}
			outputStream.flush();

			fileInput.close();

			socket.shutdownOutput();
			socket.shutdownInput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void eliminarArchivo() {
		try {

			File file = new File(servidorCloud.getPathArchivos() + username + "/" + filename);
			file.delete();

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @throws IOException
	 */
	private void leerArchivosUsuario() throws IOException {
		try {
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

			File file = new File(servidorCloud.getPathArchivos() + username);
			if (!file.exists()) {
				file.mkdirs();
			}

			Map<String, Archivo> archivos = Archivo.leerArchivos(servidorCloud.getPathArchivos() + username, true);

			for (String filename : archivos.keySet()) {
				Archivo aux = archivos.get(filename);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				outputStream.writeUTF(filename + ProtocoloComunicacion.SEPARATOR + aux.getHash()
						+ ProtocoloComunicacion.SEPARATOR + dateFormat.format(aux.getFechaModificacion())
						+ ProtocoloComunicacion.SEPARATOR + aux.getId());
			}

			// Fin de la comunicacion, se cierran socket
			outputStream.writeUTF(ProtocoloComunicacion.END);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			String cadena = dataInputStream.readUTF();
			String[] cadenas = cadena.split(ProtocoloComunicacion.SEPARATOR);
			username = cadenas[1];
			password = User.encryptPassword(cadenas[2]);
			User user = LoginUsuario.getUser(username);
			System.out.println("Usuario: " + user.getUsername() + ", password: " + user.getPassword()
					+ ", password introducida: " + password);

			if (LoginUsuario.existUser(username) && user.getPassword().equals(password)) {
				switch (cadenas[0]) {
				case ProtocoloComunicacion.SYNC:
					leerArchivosUsuario();
					break;
				case ProtocoloComunicacion.DOWNLOAD:
					filename = cadenas[3];
					descargarArchivo();
					break;
				case ProtocoloComunicacion.UPLOAD:
					filename = cadenas[3];
					subirArchivo();
					break;
				case ProtocoloComunicacion.DELETE:
					filename = cadenas[3];
					eliminarArchivo();
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
