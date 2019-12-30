 package servidor;

import java.util.HashMap;
import java.util.Map;

public class ServidorCloud {

    private int serverPort;
    private HashMap<String, Map<String, String>> archivos;
    private String pathArchivos;


    public ServidorCloud() {
        leerConfiguracion();
    }

    public void leerConfiguracion() {
        pathArchivos = "C:/Users/Regina/Desktop/srv/";
        serverPort = 6060;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setPathArchivos(String pathArchivos) {
        this.pathArchivos = pathArchivos;
    }

    public int getServerPort() {
        return serverPort;
    }

    public HashMap<String, Map<String, String>> getArchivos() {
        return archivos;
    }

    public String getPathArchivos() {
        return pathArchivos;
    }
}
