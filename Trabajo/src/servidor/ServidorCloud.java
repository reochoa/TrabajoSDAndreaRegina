package servidor;

import java.util.HashMap;
import java.util.Map;

public class ServidorCloud {

    private int serverPort;
    private HashMap<String, String> usuarios;
    private HashMap<String, Map<String, String>> archivos;
    private String pathArchivos;


    public ServidorCloud() {
        leerConfiguracion();
    }

    /**
     * Lee la configuración desde un archivo properties
     */
    public void leerConfiguracion() {
        pathArchivos = "D:/srv/";
//        pathArchivos = "/home/reochoa/srv/";
        serverPort = 7070;
    }

    public int getServerPort() {
        return serverPort;
    }

    public HashMap<String, String> getUsuarios() {
        return usuarios;
    }

    public HashMap<String, Map<String, String>> getArchivos() {
        return archivos;
    }

    public String getPathArchivos() {
        return pathArchivos;
    }
}
