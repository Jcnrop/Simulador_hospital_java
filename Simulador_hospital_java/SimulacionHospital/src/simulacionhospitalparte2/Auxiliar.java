package simulacionhospitalparte2;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Auxiliar extends Thread {

    private Hospital hospital;
    private String id;

    public Auxiliar(Hospital hospital, String id) {
        this.hospital = hospital;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            if (id.equals("A1")) {
                hospital.atenderRecepcion(this);
                hospital.entrarSalaDescanso(this, 3000, 5000);
            } else if(id.equals("A2")){
                hospital.crearVacunas(this);
                hospital.entrarSalaDescanso(this, 1000, 4000);
            }
        }

    }

    public String getIdAuxiliar() {
        return id;
    }
    
    @Override
    public String toString() {
        return id;
    }
    
    
}
