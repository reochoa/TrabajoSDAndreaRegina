package cliente;

import java.io.*;
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

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF(ProtocoloComunicacion.getComandoSync(cliente.getUsername(), cliente.getPassword()));
            outputStream.writeUTF(ProtocoloComunicacion.BR); //línea en blanco

            String response = inputStream.readUTF();

            cliente.getArchivosServidorAnterior().clear();
			cliente.getArchivosServidorAnterior().putAll(cliente.getArchivosServidorAnterior());

            while (!ProtocoloComunicacion.END.equals(response)) {
                String[] cadenas = response.split(ProtocoloComunicacion.SEPARATOR);
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
                response = inputStream.readUTF();
            }


            System.out.println("servidor" + cliente.getArchivosServidor());
            System.out.println("local" + cliente.getArchivosLocales());
            System.out.println("local_antes" + cliente.getArchivosLocalesAnterior());

            for (String filename : cliente.getArchivosLocales().keySet()) {

                Archivo fileServer = cliente.getArchivosServidor().get(filename);
				Archivo fileServerOld = cliente.getArchivosServidorAnterior().get(filename);
                Archivo fileLocal = cliente.getArchivosLocales().get(filename);
                Archivo fileLocalOld = cliente.getArchivosLocalesAnterior().get(filename);


                if (fileServer != null && fileLocal != null) {
                	if(fileServer.equals(fileLocal)) {
						fileLocal.setEstado(EstadoArchivo.SYNCHRONIZED);
					} else {
						if (fileLocal.getFechaModificacion().before(fileServer.getFechaModificacion())) {
							fileLocal.setEstado(EstadoArchivo.SERVER_MODIFIED);
						} else if (fileLocal.getFechaModificacion().after(fileServer.getFechaModificacion())) {
							fileLocal.setEstado(EstadoArchivo.CLIENT_MODIFIED);
						}
					}
                    cliente.getArchivosProcesar().put(filename, fileLocal);
                }

                if (fileServer == null && fileServerOld == null) {
                    fileLocal.setEstado(EstadoArchivo.CLIENT_NEW);
                    cliente.getArchivosProcesar().put(filename, fileLocal);
                }

                if (fileLocal == null && fileLocalOld != null) {
                    fileServer.setEstado(EstadoArchivo.SERVER_NEW);
                    cliente.getArchivosProcesar().put(filename, fileServer);
                }



            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
