package cliente;

import comun.Archivo;

public class ClienteDeleteThread implements Runnable {

    private ClienteCloud cliente;
    private Archivo archivo;

    public ClienteDeleteThread(ClienteCloud cliente, Archivo archivo) {
        this.cliente = cliente;
        this.archivo = archivo;
    }

    @Override
    public void run() {

    }
}
