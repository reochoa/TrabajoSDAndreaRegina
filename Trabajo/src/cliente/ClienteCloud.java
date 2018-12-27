package cliente;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import comun.Archivo;
import comun.ProtocoloComunicacion;

public class ClienteCloud {


    private String username;
    private String password;
    private String serverHost;
    private int serverPort;
    private String pathCarpetaPersonal;
    // Map de nombreFichero y la representacion de archivo(por SO el nombre tiene
    // que ser unico)
    private Map<String, Archivo> archivosLocales;
    private Map<String, Archivo> archivosServidor;

    public ClienteCloud() {
        archivosLocales = new HashMap<>();
        archivosServidor = new HashMap<>();
        leerConfiguracion();
    }

    /**
     * Lee la configuración desde un archivo properties
     */
    public void leerConfiguracion() {
        serverHost = "localhost";
        serverPort = 7070;
        username = "alejandro";
        password = "password";
//        pathCarpetaPersonal = "/home/alejandro/temp/";
        pathCarpetaPersonal ="D:/tmp/";
    }

    @Override
    public String toString() {
        return "cliente.ClienteCloud{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", serverHost='"
                + serverHost + '\'' + ", serverPort=" + serverPort + ", pathCarpetaPersonal='" + pathCarpetaPersonal
                + '\'' + '}';
    }

    /**
     * Lee los archivos de la carpeta local y recupera los del servidor
     */
    public void comprobarArchivos() throws IOException {
        //archivos locales
        archivosLocales = Archivo.leerArchivos(pathCarpetaPersonal);
        System.out.println(archivosLocales);

        //archicos servidor
        Socket socket = new Socket(serverHost, serverPort);
        InputStream input = socket.getInputStream();
        OutputStream ouput = socket.getOutputStream();

        PrintWriter writer = new PrintWriter(ouput, true);
        writer.println(ProtocoloComunicacion.getComandoSync(username, password));

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String respuesta = reader.readLine();

        while (!ProtocoloComunicacion.END.equals(respuesta)) {
            System.out.println(respuesta);
            respuesta = reader.readLine();
        }
        System.out.println("FIN");

    }


    public void subirArchivo(Archivo archivo) {
        StringBuilder stb = new StringBuilder();
        stb.append(ProtocoloComunicacion.UPLOAD).append(ProtocoloComunicacion.SEPARATOR);
        stb.append(archivo.getFileName()).append(ProtocoloComunicacion.SEPARATOR).append(archivo.getHash());

        try {
            Socket socket = new Socket(serverHost, serverPort);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(stb.toString() + "\r\n");
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String respuesta = reader.readLine();
            if ("OK".equals(respuesta)) {
                FileInputStream input = new FileInputStream(pathCarpetaPersonal + archivo.getFileName());
                byte[] buffer = new byte[1024];
                int leidos;
                BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
                //leidos = input.read(buffer);
                while ((leidos = input.read(buffer)) != -1) {
                    output.write(buffer, 0, leidos);
                }
                input.close();
                respuesta = reader.readLine();
                if ("COMPLETE".equals(respuesta)) {
                    output.close();
                    input.close();
                    writer.close();
                    reader.close();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void bajarArchivo(Archivo archivo) {

    }


    public Map<String, Archivo> getArchivosLocales() {
        return archivosLocales;
    }

    public void setArchivosLocales(Map<String, Archivo> archivosLocales) {
        this.archivosLocales = archivosLocales;
    }

    // ----------------------------------------------------
    // PRINCIPAL
    // ----------------------------------------------------
    public static void main(String[] args) {
        try {
            ClienteCloud clienteCloud = new ClienteCloud();
//            clienteCloud.login();
            clienteCloud.comprobarArchivos();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
