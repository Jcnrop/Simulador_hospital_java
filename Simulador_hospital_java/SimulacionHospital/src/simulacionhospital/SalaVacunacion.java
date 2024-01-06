package simulacionhospital;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class SalaVacunacion {

    private ArrayList<Puesto> puestos = new ArrayList<>();
    private JTextField auxiliarInterfaz;
    private JTextField vacunasInterfaz;
    private final Object lockPuesto = new Object();
    private final Object lockVacunas = new Object();
    private Hospital hospital;
    private int vacunasDisponibles = 0;
    private Semaphore vacunasSemaforo = new Semaphore(vacunasDisponibles, true);

    public SalaVacunacion(ArrayList<JTextField> puestosInterfaz, JTextField auxiliarInterfaz, JTextField vacunasInterfaz) {
        for (int i = 1; i <= puestosInterfaz.size(); i++) {
            this.puestos.add(new Puesto("SVP" + i, puestosInterfaz.get(i - 1)));
        }
        this.auxiliarInterfaz = auxiliarInterfaz;
        this.vacunasInterfaz = vacunasInterfaz;
        vacunasInterfaz.setText(String.valueOf(vacunasDisponibles));
    }

    public Puesto buscarPuestoPaciente() {
        Puesto puesto = null;
        while (puesto == null) {
            for (Puesto p : puestos) {
                if (p.estaDisponible()) {
                    puesto = p;
                    break;
                }
            }
            if (puesto == null) {
                synchronized (lockPuesto) {
                    try {
                        lockPuesto.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
        return puesto;
    }

    public synchronized void asignarPuestoSanitario(Sanitario sanitario) {
        String mensaje="";
        for (Puesto p : puestos) {
            if (p.getSanitarioAsignado() == null) {
                p.setSanitarioAsignado(sanitario);
                sanitario.setPuesto(p);
                
                mensaje="Al sanitario " + sanitario.getIdSanitario()
                        + " se le ha asignado el puesto " + p.getIdPuesto();
                hospital.imprimirMensaje(mensaje);
                
                break;
            }
        }
    }

    public void entrarSala(Paciente paciente) {
        paciente.getPuestoVacunacion().setPacienteAtendido(paciente);
        paciente.getPuestoVacunacion().actualizarInterfaz();

        paciente.getPuestoVacunacion().esperarVacuna(paciente);

        paciente.getPuestoVacunacion().setPacienteAtendido(null);
        paciente.getPuestoVacunacion().actualizarInterfaz();
    }

    public void atenderPuesto(Sanitario sanitario) {
        Paciente paciente;
        String mensaje = "";
        int numPacientes = 15;//Número de pacientes que atenderá el sanitario antes de descansar

        sanitario.getPuesto().setSanitarioAsignado(sanitario);
        sanitario.getPuesto().actualizarInterfaz();
        sanitario.getPuesto().setAbierto(true);

        synchronized (lockPuesto) {
            lockPuesto.notifyAll();//Avisa al auxiliar que hay un sanitario disponible
        }

        for (int i = 0; i < numPacientes; i++) {
            try {
                paciente = sanitario.getPuesto().esperarPaciente();
                
                mensaje=sanitario.getIdSanitario() + " va a por vacunas.";
                hospital.imprimirMensaje(mensaje);
                
                vacunasSemaforo.acquire();
                
                mensaje=sanitario.getIdSanitario() + " ha conseguido vacunas.";
                hospital.imprimirMensaje(mensaje);
                
                synchronized (lockVacunas) {
                    vacunasDisponibles--;
                    vacunasInterfaz.setText(String.valueOf(vacunasDisponibles));
                }
                Thread.sleep(3000 + (int) (2000 * Math.random()));
                sanitario.getPuesto().ponerVacuna();

                mensaje = "Paciente " + paciente.getIdPaciente() + " ha sido vacunado en el puesto"
                        + paciente.getPuestoVacunacion().getIdPuesto() + " por el sanitario " + sanitario.getIdSanitario();
                hospital.imprimirMensaje(mensaje);
            } catch (InterruptedException ex) {
                Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
            }

            synchronized (lockPuesto) {
                lockPuesto.notifyAll();//Avisa al auxiliar que hay un puesto libre
            }
        }

        sanitario.getPuesto().setSanitarioAsignado(null);
        sanitario.getPuesto().actualizarInterfaz();
        sanitario.getPuesto().setAbierto(false);

    }

    public void crearVacunas(Auxiliar auxiliar) {
        int numVacunas = 20;//Vacunas que hay que realizar antes de irse a descansar
        int tasaProduccion = 1;//Número de vacunas elaboradas en cada ronda
        auxiliarInterfaz.setText(auxiliar.getIdAuxiliar());
        while (numVacunas > 0) {
            try {
                Thread.sleep(500 + (int) (500 * Math.random()));
            } catch (InterruptedException ex) {
                Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (int i = 0; i < tasaProduccion; i++) {
                synchronized (lockVacunas) {
                    vacunasDisponibles++;
                    vacunasInterfaz.setText(String.valueOf(vacunasDisponibles));
                }

                vacunasSemaforo.release();
                numVacunas--;
            }
        }
        auxiliarInterfaz.setText("");
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

}
