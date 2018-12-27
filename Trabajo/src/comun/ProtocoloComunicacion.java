package comun;

public class ProtocoloComunicacion {

    public final static String SEPARATOR = "#";

    public final static String UPLOAD = "UPLOAD";
    public final static String DOWNLOAD = "DOWNLOAD";
    public final static String SYNC = "SYNC";
    public final static String END = "END";
    public final static String BR = "\r\n";

    /**
     * Devuelve comando de sincronizacion con el servidor, una cadena similar a:
     * SYNC#username#password
     * Cadena de conexion
     * @param username
     * @param password
     * @return
     */
    public static String getComandoSync(String username, String password) {
        StringBuilder stb = new StringBuilder();
        stb.append(SYNC).append(SEPARATOR);
        stb.append(username).append(SEPARATOR).append(password);
        return stb.toString();
    }
}
