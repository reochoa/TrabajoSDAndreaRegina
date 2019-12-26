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

            cliente.getArchivosLocalesAnterior().clear();
            cliente.getArchivosLocalesAnterior().putAll(cliente.getArchivosLocales());

            cliente.getArchivosLocales().clear();
            cliente.getArchivosLocales().putAll(Archivo.leerArchivos(cliente.getPathCarpetaPersonal()));

            cliente.getArchivosServidorAnterior().clear();
            cliente.getArchivosServidorAnterior().putAll(cliente.getArchivosServidor());

            cliente.getArchivosServidor().clear();
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

            cliente.getArchivosProcesar().clear();
            cliente.getArchivosProcesar().putAll(cliente.getArchivosLocales());
            cliente.getArchivosProcesar().putAll(cliente.getArchivosLocalesAnterior());
            cliente.getArchivosProcesar().putAll(cliente.getArchivosServidor());
            cliente.getArchivosProcesar().putAll(cliente.getArchivosServidorAnterior());

            for (Archivo archivo : cliente.getArchivosProcesar().values()) {

                if (cliente.getArchivosLocalesAnterior().containsValue(archivo)
                        && !cliente.getArchivosLocales().containsValue(archivo)) {
                    archivo.setEstado(EstadoArchivo.CLIENT_DELETED);
                } else if (cliente.getArchivosServidorAnterior().containsValue(archivo)
                        && !cliente.getArchivosServidor().containsValue(archivo)) {
                    archivo.setEstado(EstadoArchivo.SERVER_DELETED);
                } else if (!cliente.getArchivosLocales().containsValue(archivo)) {
                    archivo.setEstado(EstadoArchivo.SERVER_NEW);
                } else if (!cliente.getArchivosServidor().containsValue(archivo)) {
                    archivo.setEstado(EstadoArchivo.CLIENT_NEW);
                } else if (cliente.getArchivosLocales().containsValue(archivo)
                        && cliente.getArchivosServidor().containsValue(archivo)) {

                    Archivo archivoLocal = cliente.getArchivosLocales().get(archivo.getFileName());
                    Archivo archivoServidor = cliente.getArchivosServidor().get(archivo.getFileName());

                    if (archivoLocal.getHash().equals(archivoServidor.getHash())) {
                        archivo.setEstado(EstadoArchivo.SYNCHRONIZED);
                    } else {
                        if (archivoLocal.getFechaModificacion().before(archivoServidor.getFechaModificacion())) {
                            archivo.setEstado(EstadoArchivo.SERVER_MODIFIED);
                        } else if (archivoLocal.getFechaModificacion().after(archivoServidor.getFechaModificacion())) {
                            archivo.setEstado(EstadoArchivo.CLIENT_MODIFIED);
                        }
                    }
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
