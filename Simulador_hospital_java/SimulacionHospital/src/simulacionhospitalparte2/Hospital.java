package simulacionhospitalparte2;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hospital {

    private Recepcion recepcion;
    private SalaDescanso salaDescanso;
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;
    private Log log;
    private Servidor servidor;

    public Hospital(Recepcion recepcion, SalaDescanso salaDescanso,
            SalaVacunacion salaVacunacion, SalaObservacion salaObservacion) {
        this.recepcion = recepcion;
        this.salaDescanso = salaDescanso;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        recepcion.setHospital(this);
        salaDescanso.setHospital(this);
        salaVacunacion.setHospital(this);
        salaObservacion.setHospital(this);
        try {
            log = new Log("evolucionHospital.txt", true);
        } catch (IOException ex) {
            Logger.getLogger(Hospital.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        servidor=new Servidor(this);
        servidor.start();
    }

    public void entrarRecepcion(Paciente paciente) {
        recepcion.añadirCola(paciente);
    }

    public void entrarSalaVacunacion(Paciente paciente) {
        salaVacunacion.entrarSala(paciente);
    }

    public void entrarSalaObservacion(Paciente paciente) {
        salaObservacion.entrarSala(paciente);
    }

    public void entrarSalaDescanso(Thread persona, int min, int max) {
        salaDescanso.descansar(persona, min, max);
    }

    public void atenderRecepcion(Auxiliar auxiliar) {
        recepcion.atenderPacientes(auxiliar);
    }

    public Puesto buscarPuestoVacunacion() {
        Puesto puesto;
        puesto = salaVacunacion.buscarPuestoPaciente();
        return puesto;
    }

    public void asignarPuestoVacunacion(Sanitario sanitario) {
        salaVacunacion.asignarPuestoSanitario(sanitario);
    }

    public void atenderPuestoVacunacion(Sanitario sanitario) {
        salaVacunacion.atenderPuesto(sanitario);
    }

    public void atenderSalaObservacion(Sanitario sanitario) {
        salaObservacion.observarPacientes(sanitario);
    }

    public void crearVacunas(Auxiliar auxiliar) {
        salaVacunacion.crearVacunas(auxiliar);
    }

    public void vigilarSalaObservacion(Sanitario sanitario) {
        salaObservacion.vigilarSala(sanitario);
    }

    public Log getLog() {
        return log;
    }

    public void imprimirMensaje(String mensaje) {
        try {
            getLog().addLine(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void imprimirMensaje(String mensaje, boolean imprimirPantalla) {
        if(imprimirPantalla){
            System.out.println(mensaje);
        }
        try {
            getLog().addLine(mensaje);
        } catch (IOException ex) {
            Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Servidor getServidor() {
        return servidor;
    }

    public SalaVacunacion getSalaVacunacion() {
        return salaVacunacion;
    }
    
    
}
