package simulacionhospitalparte2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private BufferedWriter buffered;
    private String ruta;

    public Log(String ruta) throws IOException {
        this.ruta = ruta;
        this.open(true);
    }

    public Log(String ruta, boolean reset) throws IOException {
        this.ruta = ruta;
        this.open(!reset);
    }

    private synchronized void open(boolean append) throws IOException {
        this.buffered = new BufferedWriter(new FileWriter(this.ruta, append));
    }

    public synchronized void addLine(String line) throws IOException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        String formatoFecha = sdf.format(new Date());
        this.open(true);
        this.buffered.write("[" + formatoFecha + "] " + line + "\n");
        this.close();
    }
    
    public synchronized void resetLog() throws IOException{
        this.open(false);
        this.close();
    }
    
    private synchronized void close() throws IOException{
        this.buffered.close();
    }
    
}