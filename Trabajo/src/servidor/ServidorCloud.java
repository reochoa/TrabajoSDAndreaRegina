package servidor;

import java.util.HashMap;
import java.util.Map;

public class ServidorCloud {
    public final static String CMD_SEPARATOR = "#";
    public final static String CMD_LOGIN = "LOGIN";
    public final static String CMD_UPLOAD = "UPLOAD";
    public final static String CMD_DOWNLOAD = "DOWNLOAD";


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
        serverPort = 8080;
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
