package simulacionhospitalparte2;

import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class Puesto {

    private JTextField puestoInterfaz;
    private String idPuesto;
    private Sanitario sanitarioAsignado = null;
    private Paciente pacienteAtendido = null;
    private boolean abierto = false;
    private boolean puestoPillado = false;
    private boolean hayQueLimpiar = false;
    private SynchronousQueue<Paciente> esperaPaciente = new SynchronousQueue<>();
    private SynchronousQueue<Boolean> esperaVacuna = new SynchronousQueue<>();
    private SynchronousQueue<Boolean> esperaReaccion = new SynchronousQueue<>();

    public Puesto(String idPuesto, JTextField puestoInterfaz) {
        this.puestoInterfaz = puestoInterfaz;
        this.idPuesto = idPuesto;
    }

    public void esperarVacuna(Paciente paciente) {
        try {
            esperaPaciente.put(paciente);
            paciente.setVacunado(esperaVacuna.take());
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Paciente esperarPaciente() throws InterruptedException {
        Paciente paciente;
        paciente = esperaPaciente.take();

        return paciente;
    }

    public void esperarReaccion(Paciente paciente) {
        try {
            paciente.setReaccion(esperaReaccion.take());
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ponerVacuna() throws InterruptedException {
        esperaVacuna.put(true);
    }

    public void atenderReaccion() {
        try {
            esperaReaccion.put(false);
        } catch (InterruptedException ex) {
            Logger.getLogger(Puesto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized boolean estaDisponible() {
        boolean respuesta = true;
        if (!abierto || pacienteAtendido != null || puestoPillado) {
            respuesta = false;
        }
        return respuesta;
    }

    public synchronized void actualizarInterfaz(Hospital hospital) {
        String[] frase = {"", ""};
        if (sanitarioAsignado != null) {
            frase[0] = sanitarioAsignado.getIdSanitario();
        }
        if (pacienteAtendido != null) {
            frase[1] = pacienteAtendido.getIdPaciente();
        }

        puestoInterfaz.setText(Arrays.toString(frase));
        hospital.getServidor().actualizarVariable("puestos" + idPuesto.substring(0, 2),
                Integer.parseInt(idPuesto.substring(3)) - 1, Arrays.toString(frase));
    }

    public Sanitario getSanitarioAsignado() {
        return sanitarioAsignado;
    }

    public void setSanitarioAsignado(Sanitario sanitarioAsignado) {
        this.sanitarioAsignado = sanitarioAsignado;
    }

    public synchronized Paciente getPacienteAtendido() {
        return pacienteAtendido;
    }

    public synchronized void setPacienteAtendido(Paciente pacienteAtendido) {
        this.pacienteAtendido = pacienteAtendido;
    }

    public synchronized boolean isAbierto() {
        return abierto;
    }

    public synchronized void setAbierto(boolean abierto) {
        this.abierto = abierto;
    }

    public String getIdPuesto() {
        return idPuesto;
    }

    public synchronized boolean isHayQueLimpiar() {
        return hayQueLimpiar;
    }

    public synchronized void setHayQueLimpiar(boolean hayQueLimpiar) {
        this.hayQueLimpiar = hayQueLimpiar;
    }

    public synchronized boolean isPuestoPillado() {
        return puestoPillado;
    }

    public synchronized void setPuestoPillado(boolean puestoPillado) {
        this.puestoPillado = puestoPillado;
    }
    
    

}
