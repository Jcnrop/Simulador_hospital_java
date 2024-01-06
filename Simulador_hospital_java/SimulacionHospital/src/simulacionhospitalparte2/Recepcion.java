package simulacionhospitalparte2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Recepcion {

    private JTextArea colaInterfaz;
    private JTextField pacienteInterfaz;
    private JTextField auxiliarInterfaz;
    private Hospital hospital;
    private final Queue<Paciente> colaEspera = new ConcurrentLinkedQueue<Paciente>();
    private SynchronousQueue<Paciente> esperaAlAuxiliar = new SynchronousQueue<>();
    private SynchronousQueue<Puesto> esperaPuesto = new SynchronousQueue<>();
    private Semaphore atenderPaciente = new Semaphore(1, true);

    public Recepcion(JTextArea colaInterfaz, JTextField pacienteInterfaz, JTextField auxiliarInterfaz) {
        this.colaInterfaz = colaInterfaz;
        this.pacienteInterfaz = pacienteInterfaz;
        this.auxiliarInterfaz = auxiliarInterfaz;
    }

    public void añadirCola(Paciente paciente) {
        synchronized (colaEspera) {
            colaEspera.offer(paciente);
            actualizarColaInterfaz();
        }
        try {
            atenderPaciente.acquire();
            synchronized (colaEspera) {
                colaEspera.poll();
                actualizarColaInterfaz();
            }

            pacienteInterfaz.setText(paciente.getIdPaciente());
            hospital.getServidor().actualizarVariable("pacienteRec", paciente.getIdPaciente());

            esperaAlAuxiliar.put(paciente);
            paciente.setPuestoVacunacion(esperaPuesto.take());
        } catch (InterruptedException ex) {
            String mensaje="El paciente "+paciente.getIdPaciente()+" pide disculpas por venir sin cita y se marcha";
            hospital.imprimirMensaje(mensaje, true);
        } finally {
            pacienteInterfaz.setText("");
            hospital.getServidor().actualizarVariable("pacienteRec", "");
            atenderPaciente.release();
        }
    }

    public void atenderPacientes(Auxiliar auxiliar) {
        Paciente paciente;
        Puesto puesto;
        String mensaje="";
        int numPacientes = 10;//Número de pacientes que atenderá el auxiliar antes de descansar
        auxiliarInterfaz.setText(auxiliar.getIdAuxiliar());
        hospital.getServidor().actualizarVariable("auxiliarRec", auxiliar.getIdAuxiliar());
        for (int i = 0; i < numPacientes; i++) {
            try {
                paciente = esperaAlAuxiliar.take();
                Thread.sleep(500 + (int) (500 * Math.random()));//tiempo en revisar los datos del paciente
                if (paciente.isCitado()) {
                    puesto=hospital.buscarPuestoVacunacion();
                    esperaPuesto.put(puesto);
                    mensaje="Paciente " + paciente.getIdPaciente() + " será vacunado en el puesto"
                        + puesto.getIdPuesto() +
                            " por el sanitario " + puesto.getSanitarioAsignado().getIdSanitario();
                    hospital.imprimirMensaje(mensaje, true);
                } else {
                    paciente.interrupt();
                    mensaje="Paciente "+ paciente.getIdPaciente() + " ha acudido sin cita.";
                    hospital.imprimirMensaje(mensaje, true);
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        auxiliarInterfaz.setText("");
        hospital.getServidor().actualizarVariable("auxiliarRec", "");
    }

    public synchronized void actualizarColaInterfaz() {
        colaInterfaz.setText(colaEspera.toString());
        hospital.getServidor().actualizarVariable("colaRec", colaEspera.toString());
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }
}
