package servidor;

import comun.Archivo;
import comun.ProtocoloComunicacion;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServidorCloudThread implements Runnable {

    private ServidorCloud servidorCloud;
    private Socket socket;
    private String username;
    private String password;

    public ServidorCloudThread(ServidorCloud servidorCloud, Socket socket) {
        this.servidorCloud = servidorCloud;
        this.socket = socket;
    }

    private void subirArchivo(String cadena) {
        String[] cadenaAux = cadena.split(ProtocoloComunicacion.SEPARATOR);
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
            writer.write("COMPLETE" + "\r\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @throws IOException
     */
    private void leerArchivosUsuario() throws IOException {
        InputStream input = socket.getInputStream();
        OutputStream ouput = socket.getOutputStream();

        File file = new File(servidorCloud.getPathArchivos() + username);
        if (!file.exists()) {
            file.mkdirs();
        }

        PrintWriter writer = new PrintWriter(ouput, true);

        Map<String, Archivo> archivos = Archivo.leerArchivos(servidorCloud.getPathArchivos() + username);
        System.out.println(archivos);

        for (String filename : archivos.keySet()) {
            System.out.println("[ARCHIVOS " + username + "]" + filename);
            writer.println(filename);
        }

        //Fin de la comunicacion, se cierran sockets
        writer.println(ProtocoloComunicacion.END);
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String cadena = bufferedReader.readLine();
            String[] cadenas = cadena.split(ProtocoloComunicacion.SEPARATOR);
            username = cadenas[1];
            password = cadenas[2];

            switch (cadenas[0]) {
                case ProtocoloComunicacion.SYNC:
                    System.out.println("----------------SINCRONIZACION----------------------");
                    leerArchivosUsuario();
                    System.out.println("----------------FIN SINCRONIZACION----------------------");
            }

            socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
