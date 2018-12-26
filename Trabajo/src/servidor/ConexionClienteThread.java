package servidor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ConexionClienteThread implements Runnable {

	private ServidorCloud servidorCloud;
	private Socket socket;
	private String username;
	private String password;

	public ConexionClienteThread(ServidorCloud servidorCloud, Socket socket) {
		this.servidorCloud = servidorCloud;
		this.socket = socket;
	}

	private void login(String cadena) throws IOException {

		System.out.println("--> CADENA " + cadena);
		String[] cadenaAux = cadena.split(ServidorCloud.CMD_SEPARATOR);
		username = cadenaAux[0];
		password = cadenaAux[1];

		System.out.println(servidorCloud.getPathArchivos() + username);
		File file = new File(servidorCloud.getPathArchivos() + username);
		
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		writer.write("OK"+"\r\n");
		writer.flush();
		
		
		
	}

	private void subirArchivo(String cadena) {
		String[] cadenaAux = cadena.split(ServidorCloud.CMD_SEPARATOR);
		String fileName = cadenaAux[0];
		String hash = cadenaAux[1];
		try {
			OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write("OK" + "\r\n");
			writer.flush();
//			InputStreamReader reader = new InputStreamReader(socket.getInputStream());
			FileOutputStream output = new FileOutputStream(fileName);
			byte[] buffer = new byte[1024];
			int leidos;
			BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
			//leidos = input.read(buffer);
			while ((leidos = input.read(buffer)) != -1) {
				output.write(buffer, 0, leidos);
			}
			output.close();
			writer.write("COMPLETE"+ "\r\n");
			writer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String cadena = bufferedReader.readLine();

			System.out.println("....>" + cadena);
			if (cadena.startsWith(ServidorCloud.CMD_LOGIN)) {
				login(cadena);
			} else if (cadena.startsWith(ServidorCloud.CMD_UPLOAD)) {
				System.out.println("Upload");
				subirArchivo(cadena);
			}
			
			socket.close();

		} catch (IOException ex) {

		}
	}

}
