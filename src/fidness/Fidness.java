package fidness;

import fidness.datos.ArchivoEjercicios;
import fidness.excepcion.FidnessException;
import fidness.servicio.SistemaFidness;
import fidness.vista.VentanaFidness;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Fidness {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Fidness().iniciar());
    }

    private void iniciar() {
        try {
            Path archivo = Path.of(System.getProperty("user.home"), "Fidness", "ejercicios.dat");
            SistemaFidness sistema = new SistemaFidness(new ArchivoEjercicios(archivo));
            new VentanaFidness(sistema).setVisible(true);
        } catch (FidnessException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "No se pudo abrir Fidness",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
