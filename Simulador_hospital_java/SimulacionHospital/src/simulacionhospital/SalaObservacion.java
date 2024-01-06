package simulacionhospital;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class SalaObservacion {

    private boolean haySanitarios = false;
    private ArrayList<Puesto> puestos = new ArrayList<>();
    private Hospital hospital;
    private Semaphore puestosSemaforo = new Semaphore(20, true);
    private final Object lockPuesto = new Object();

    public SalaObservacion(ArrayList<JTextField> puestosInterfaz) {
        for (int i = 1; i <= puestosInterfaz.size(); i++) {
            this.puestos.add(new Puesto("SOP" + i, puestosInterfaz.get(i - 1)));
        }
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public void entrarSala(Paciente paciente) {
        String mensaje="";
        try {
            puestosSemaforo.acquire();
            synchronized (lockPuesto) {
                for (Puesto p : puestos) {
                    if (p.getPacienteAtendido() == null) {
                        p.setPacienteAtendido(paciente);
                        paciente.setPuestoObservacion(p);
                        paciente.getPuestoObservacion().actualizarInterfaz();
                        break;
                    }
                }
            }

            Thread.sleep(10000);

            if (paciente.getProbabilidadReaccion() > Math.random() * 100) {
                paciente.setReaccion(true);
                
                mensaje="El paciente "+paciente.getIdPaciente()+" ha tenido una reacción";
                hospital.imprimirMensaje(mensaje);

                paciente.getPuestoObservacion().esperarReaccion(paciente);
            }

            synchronized (lockPuesto) {
                paciente.getPuestoObservacion().setPacienteAtendido(null);
                paciente.getPuestoObservacion().actualizarInterfaz();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaObservacion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            puestosSemaforo.release();
        }
    }

    public void observarPacientes(Sanitario sanitario) {
        Puesto puestoPaciente;
        haySanitarios = true;
        do {
            puestoPaciente = null;
            synchronized (lockPuesto) {
                for (Puesto p : puestos) {
                    if (p.getPacienteAtendido() != null && p.getPacienteAtendido().isReaccion()
                            && p.getSanitarioAsignado() == null) {
                        p.setSanitarioAsignado(sanitario);
                        p.actualizarInterfaz();
                        puestoPaciente = p;
                        break;
                    }
                }
            }

            if (puestoPaciente != null) {
                try {
                    Thread.sleep(2000 + (int) (3000 * Math.random()));
                    hospital.getLog().addLine("La reacción del Paciente "+puestoPaciente.getPacienteAtendido().getIdPaciente()+
                            " ha sido atendida por el sanitario "+sanitario.getIdSanitario());
                } catch (InterruptedException ex) {
                    Logger.getLogger(SalaObservacion.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
                }
                puestoPaciente.atenderReaccion();
                synchronized (lockPuesto) {
                    puestoPaciente.setSanitarioAsignado(null);
                    puestoPaciente.actualizarInterfaz();
                }
            }
        } while (puestoPaciente != null);

    }

    public void vigilarSala(Sanitario sanitario) {
        if (!haySanitarios) {
            observarPacientes(sanitario);
        }

        haySanitarios = false;
    }
}
