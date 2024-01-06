package simulacionhospitalparte2;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Sanitario extends Thread {

    private Hospital hospital;
    private String idSanitario;
    private Puesto puesto = null;

    public Sanitario(Hospital hospital, String id) {
        this.hospital = hospital;
        this.idSanitario = id;
    }

    @Override
    public void run() {
        hospital.entrarSalaDescanso(this, 1000, 3000);
        //Este sanitario se encargará de atender a los pacientes reaccionados si los otros tardan mucho en venir
        if (idSanitario.equals("SAux")) {
            try {
                Thread.sleep(120000);//Tiempo que espera el sanitario para vigilar la sala de observación
            } catch (InterruptedException ex) {
                Logger.getLogger(Sanitario.class.getName()).log(Level.SEVERE, null, ex);
            }
            hospital.vigilarSalaObservacion(this);
        } else {
            hospital.asignarPuestoVacunacion(this);
            while (true) {
                hospital.atenderPuestoVacunacion(this);
                hospital.entrarSalaDescanso(this, 5000, 8000);
                hospital.atenderSalaObservacion(this);
            }
        }
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public String getIdSanitario() {
        return idSanitario;
    }

    @Override
    public String toString() {
        return idSanitario;
    }

}
