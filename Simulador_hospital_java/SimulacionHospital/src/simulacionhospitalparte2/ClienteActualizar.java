package simulacionhospitalparte2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteActualizar extends Thread {

    private final String mensaje = "Actualizar";
    private Socket cliente;
    private DataInputStream entrada;
    private DataOutputStream salida;

    private JTextArea colaRec;
    private JTextField pacienteRec, auxiliarRec;
    private JTextArea personasSD;
    private ArrayList<JTextField> puestosSV;
    private JTextField auxiliarSV, vacunasSV;
    private ArrayList<JTextField> puestosSO;

    public ClienteActualizar(JTextArea colaRec, JTextField pacienteRec, JTextField auxiliarRec,
            JTextArea personasSD, ArrayList<JTextField> puestosSV,
            JTextField auxiliarSV, JTextField vacunasSV, ArrayList<JTextField> puestosSO) {
        this.colaRec = colaRec;
        this.pacienteRec = pacienteRec;
        this.auxiliarRec = auxiliarRec;
        this.personasSD = personasSD;
        this.puestosSV = puestosSV;
        this.auxiliarSV = auxiliarSV;
        this.vacunasSV = vacunasSV;
        this.puestosSO = puestosSO;
    }


    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);
                cliente = new Socket(InetAddress.getLocalHost(), 5000); //Creamos el socket para conectarnos al puerto 5000 del servidor
                entrada = new DataInputStream(cliente.getInputStream()); //Creamos los canales de E/S
                salida = new DataOutputStream(cliente.getOutputStream());

                salida.writeUTF(mensaje); //Enviamos un mensaje al servidor

                actualizarInterfaz();

                entrada.close(); //Cerramos los flujos de entrada y salida
                salida.close();
                cliente.close();//Cerramos la conexión
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (InterruptedException ex) {
                Logger.getLogger(ClienteActualizar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void actualizarInterfaz() {
        try {
            colaRec.setText(entrada.readUTF());
            pacienteRec.setText(entrada.readUTF());
            auxiliarRec.setText(entrada.readUTF());
            personasSD.setText(entrada.readUTF());
            
            for(JTextField puesto:puestosSV){
                puesto.setText(entrada.readUTF());
            }
            
            auxiliarSV.setText(entrada.readUTF());
            vacunasSV.setText(entrada.readUTF());
            
            for(JTextField puesto:puestosSO){
                puesto.setText(entrada.readUTF());
            }
        } catch (IOException ex) {
            System.out.println("Se perdio la conexión con el servidor.");
        }
        
    }
}
