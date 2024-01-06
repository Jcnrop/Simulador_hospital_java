package simulacionhospital;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

public class SalaDescanso {

    private JTextArea colaInterfaz;
    private ArrayList<Thread> personasDentro = new ArrayList<>();
    private final Object lock = new Object();
    private Hospital hospital;

    public SalaDescanso(JTextArea colaInterfaz) {
        this.colaInterfaz = colaInterfaz;
    }

    public void descansar(Thread persona, int min, int max) {
        String mensaje="";
        synchronized (lock) {
            personasDentro.add(persona);
            colaInterfaz.setText(personasDentro.toString());

        }
        try {
            mensaje=persona.toString()+" comienza su descanso.";
            hospital.imprimirMensaje(mensaje);
            
            Thread.sleep(min + (int) ((max - min) * Math.random()));
            
            mensaje=persona.toString()+" termina su descanso.";
            hospital.imprimirMensaje(mensaje);
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaDescanso.class.getName()).log(Level.SEVERE, null, ex);
        }

        synchronized (lock) {
            personasDentro.remove(persona);
            colaInterfaz.setText(personasDentro.toString());
        }
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }
}
