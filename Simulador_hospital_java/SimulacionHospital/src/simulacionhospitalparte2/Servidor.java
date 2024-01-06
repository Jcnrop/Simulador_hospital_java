package simulacionhospitalparte2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {

    private ServerSocket servidor;
    private Socket conexion;
    private DataOutputStream salida;
    private DataInputStream entrada;

    private Hospital hospital;
    private String colaRec = "", pacienteRec = "", auxiliarRec = "";
    private String personasSD = "";
    private String[] puestosSV = new String[10];
    private String auxiliarSV = "", vacunasSV = "";
    private String[] puestosSO = new String[20];

    public Servidor(Hospital hospital) {
        try {
            servidor = new ServerSocket(5000); //Creamos un ServerSocketen el Puerto 5000
        } catch (IOException ex) {
            System.out.println("PROBLEMA AL CREAR EL SERVIDOR");
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.hospital=hospital;

        for (int i = 0; i < puestosSV.length; i++) {
            puestosSV[i] = "";
        }

        for (int i = 0; i < puestosSO.length; i++) {
            puestosSO[i] = "";
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                conexion = servidor.accept(); //Esperamos una conexión

                entrada = new DataInputStream(conexion.getInputStream()); //Abrimos los canales de E/S
                salida = new DataOutputStream(conexion.getOutputStream());

                String mensaje = entrada.readUTF(); //Leemos el mensaje del cliente

                if (mensaje.equals("Actualizar")) {
                    enviarDatosHospital();
                } else if (mensaje.equals("Cerrar")) {
                    String idPuesto=entrada.readUTF();
                    hospital.getSalaVacunacion().cerrarPuesto(idPuesto);
                }
                
                entrada.close();//Cerramos los flujos de entrada y salida
                salida.close();
                conexion.close(); //Y cerramos la conexión
            }
        } catch (IOException e) {
        }
    }

    public synchronized void enviarDatosHospital() {
        try {
            salida.writeUTF(colaRec);
            salida.writeUTF(pacienteRec);
            salida.writeUTF(auxiliarRec);
            salida.writeUTF(personasSD);
            for (String puestoSV : puestosSV) {
                salida.writeUTF(puestoSV);
            }
            salida.writeUTF(auxiliarSV);
            salida.writeUTF(vacunasSV);
            for (String puestoSO : puestosSO) {
                salida.writeUTF(puestoSO);
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Para las variables que son String
    public synchronized void actualizarVariable(String nombreVariable, String valorNuevo) {
        switch (nombreVariable) {
            case "colaRec":
                colaRec = valorNuevo;
                break;
            case "pacienteRec":
                pacienteRec = valorNuevo;
                break;
            case "auxiliarRec":
                auxiliarRec = valorNuevo;
                break;
            case "personasSD":
                personasSD = valorNuevo;
                break;
            case "auxiliarSV":
                auxiliarSV = valorNuevo;
                break;
            case "vacunasSV":
                vacunasSV = valorNuevo;
                break;
        }
    }

    //Para las variables que son String[]
    public synchronized void actualizarVariable(String nombreVariable, int posicion, String valorNuevo) {
        switch (nombreVariable) {
            case "puestosSV":
                puestosSV[posicion] = valorNuevo;
                break;
            case "puestosSO":
                puestosSO[posicion] = valorNuevo;
                break;
        }
    }
}
