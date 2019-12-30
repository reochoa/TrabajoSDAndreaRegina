package cliente;

import comun.Archivo;
import comun.EstadoArchivo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ClienteCloud {

    private String username;
    private String password;
    private String serverHost;
    private int serverPort;
    private String pathCarpetaPersonal;
    // Map de nombreFichero y la representacion de archivo(por SO el nombre tiene
    // que ser unico)
    private Map<String, Archivo> archivosLocales;
    private Map<String, Archivo> archivosLocalesAnterior;
    private Map<String, Archivo> archivosServidor;
    private Map<String, Archivo> archivosServidorAnterior;

    private Map<String, Archivo> archivosProcesar;


    public ClienteCloud() {
        archivosLocales = new HashMap<>();
        archivosServidor = new HashMap<>();
        archivosLocalesAnterior = new HashMap<>();
        archivosServidorAnterior = new HashMap<>();
        archivosProcesar = new HashMap<>();
    }

    public Map<String, Archivo> getArchivosServidor() {
        return archivosServidor;
    }

    public Map<String, Archivo> getArchivosLocales() {
        return archivosLocales;
    }

    public Map<String, Archivo> getArchivosLocalesAnterior() {
        return archivosLocalesAnterior;
    }

    public Map<String, Archivo> getArchivosServidorAnterior() {
        return archivosServidorAnterior;
    }

    public Map<String, Archivo> getArchivosProcesar() {
        return archivosProcesar;
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
            cliente.serverPort = 6060;
            cliente.username = "anlaram";
            cliente.password = "holaholita";
            cliente.pathCarpetaPersonal = "C:/Users/Regina/Desktop/prueba/";

            try {
                while (true) {
                    ClienteSyncThread syncThread = new ClienteSyncThread(cliente);
                    syncThread.run();

                    System.out.println(cliente.getArchivosLocales());
                    System.out.println(cliente.getArchivosLocalesAnterior());

                    System.out.println(cliente.getArchivosServidor());
                    System.out.println(cliente.getArchivosServidorAnterior());


                    for (Archivo archivoProcesar : cliente.getArchivosProcesar().values()) {


                        //Archivos modificados o nuevos en cliente
                        if (archivoProcesar.getEstado().equals(EstadoArchivo.CLIENT_MODIFIED)
                                || archivoProcesar.getEstado().equals(EstadoArchivo.CLIENT_NEW)) {

                            ClienteUploadThread uploadThread = new ClienteUploadThread(cliente, archivoProcesar);
                            uploadThread.run();
                        }

                        //Archivos modificados o nuevos en servidor
                        if (archivoProcesar.getEstado().equals(EstadoArchivo.SERVER_MODIFIED)
                                || archivoProcesar.getEstado().equals(EstadoArchivo.SERVER_NEW)) {

                            ClienteDownloadThread downloadThread = new ClienteDownloadThread(cliente, archivoProcesar);
                            downloadThread.run();
                        }

                        if(archivoProcesar.getEstado().equals(EstadoArchivo.CLIENT_DELETED)) {
                            ClienteDeleteThread deleteThread = new ClienteDeleteThread(cliente, archivoProcesar);
                            deleteThread.run();
                        }

                        if(archivoProcesar.getEstado().equals(EstadoArchivo.SERVER_DELETED)) {
                            File file = new File(cliente.pathCarpetaPersonal + archivoProcesar.getFileName());
                            file.delete();
                        }

                    }

                    Thread.currentThread().sleep(5000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
