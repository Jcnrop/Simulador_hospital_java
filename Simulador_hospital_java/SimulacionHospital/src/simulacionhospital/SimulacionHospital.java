package simulacionhospital;

public class SimulacionHospital extends Thread {

    private Hospital hospital;

    public SimulacionHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public void run() {
        Sanitario sanitario;
        Auxiliar auxiliar;
        Paciente paciente;

        for (int i = 1; i <= 10; i++) {
            sanitario = new Sanitario(hospital, "S" + String.format("%02d", i));
            sanitario.start();
        }
        sanitario = new Sanitario(hospital, "SAux");
        sanitario.start();

        for (int i = 1; i <= 2; i++) {
            auxiliar = new Auxiliar(hospital, "A" + i);
            auxiliar.start();
        }
        for (int i = 1; i <= 2000; i++) {
            paciente = new Paciente(hospital, "P" + String.format("%04d", i));
            paciente.start();
            try {
                Thread.sleep(1000 + (int) (2000 * Math.random()));
            } catch (Exception e) {
                System.out.println("Error en la espera entre pacientes");
            }
        }
    }
}
