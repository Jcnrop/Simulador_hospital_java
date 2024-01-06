package simulacionhospitalparte2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteCerrar implements Runnable {

    private final String mensaje = "Cerrar";
    private String nombrePuesto;
    private Socket cliente;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public ClienteCerrar(String nombrePuesto) {
        this.nombrePuesto = nombrePuesto;
    }
    
    @Override
    public void run() {
            try {
                cliente = new Socket(InetAddress.getLocalHost(), 5000); //Creamos el socket para conectarnos al puerto 5000 del servidor
                entrada = new DataInputStream(cliente.getInputStream()); //Creamos los canales de E/S
                salida = new DataOutputStream(cliente.getOutputStream());

                salida.writeUTF(mensaje); //Enviamos un mensaje al servidor
                salida.writeUTF(nombrePuesto);//Le enviamos el puesto a cerrar
                
                entrada.close(); //Cerramos los flujos de entrada y salida
                salida.close();
                cliente.close();//Cerramos la conexión
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
    }
}
