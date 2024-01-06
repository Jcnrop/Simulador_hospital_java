package simulacionhospital;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Paciente extends Thread {

    private Hospital hospital;
    private String id;
    private boolean citado = true;
    private boolean vacunado = false;
    private boolean reaccion = false;
    private int porcentajeCitado = 1;
    private int probabilidadReaccion = 5;
    private Puesto puestoVacunacion = null;
    private Puesto puestoObservacion = null;

    public Paciente(Hospital hospital, String id) {
        this.hospital = hospital;
        this.id = id;
        if (porcentajeCitado > Math.random() * 100) {
            citado = false;
        }
    }

    @Override
    public void run() {
        String mensaje = "";
        hospital.entrarRecepcion(this);
        if (puestoVacunacion != null) {
            hospital.entrarSalaVacunacion(this);

            mensaje = id + " se dirige a la sala de observacion";
            hospital.imprimirMensaje(mensaje);

            hospital.entrarSalaObservacion(this);
        }

        mensaje = "El paciente " + id + " se va del hospital";
        hospital.imprimirMensaje(mensaje);
    }

    public Puesto getPuestoVacunacion() {
        return puestoVacunacion;
    }

    public void setPuestoVacunacion(Puesto puestoVacunacion) {
        this.puestoVacunacion = puestoVacunacion;
    }

    public Puesto getPuestoObservacion() {
        return puestoObservacion;
    }

    public void setPuestoObservacion(Puesto puestoObservacion) {
        this.puestoObservacion = puestoObservacion;
    }

    public boolean isCitado() {
        return citado;
    }

    public String getIdPaciente() {
        return id;
    }

    public void setVacunado(boolean vacunado) {
        this.vacunado = vacunado;
    }

    public int getProbabilidadReaccion() {
        return probabilidadReaccion;
    }

    public boolean isReaccion() {
        return reaccion;
    }

    public void setReaccion(boolean reaccion) {
        this.reaccion = reaccion;
    }

    @Override
    public String toString() {
        return id;
    }

}
