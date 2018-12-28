package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import comun.Archivo;
import comun.EstadoArchivo;
import comun.ProtocoloComunicacion;

public class ClienteSyncThread implements Runnable {

	private ClienteCloud cliente;

	public ClienteSyncThread(ClienteCloud cliente) {
		this.cliente = cliente;
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket(cliente.getServerHost(), cliente.getServerPort());
			InputStream input = socket.getInputStream();
			OutputStream ouput = socket.getOutputStream();

			PrintWriter writer = new PrintWriter(ouput, true);
			writer.println(ProtocoloComunicacion.getComandoSync(cliente.getUsername(), cliente.getPassword()));

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String respuesta = reader.readLine();

			while (!ProtocoloComunicacion.END.equals(respuesta)) {
				String[] cadenas = respuesta.split(ProtocoloComunicacion.SEPARATOR);
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
				Date fechaModificacion = new Date();
				try {
					fechaModificacion = dateFormat.parse(cadenas[2]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				int id = Integer.parseInt(cadenas[3]);
				Archivo aux = new Archivo(id, cadenas[0], fechaModificacion, cadenas[1]);
				cliente.getArchivosServidor().put(cadenas[0], aux);
				respuesta = reader.readLine();
			}

			for (String filename : cliente.getArchivosLocales().keySet()) {
				Archivo local = cliente.getArchivosLocales().get(filename);

				if (cliente.getArchivosServidor().containsKey(filename)) {
					Archivo servidor = cliente.getArchivosServidor().get(filename);
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
						local.setId(servidor.getId());
						servidor.setEstado(EstadoArchivo.sinCambios);
					}

				}

			}
		} catch (IOException e) {
			// TODO: handle exception
		}
	}

}
