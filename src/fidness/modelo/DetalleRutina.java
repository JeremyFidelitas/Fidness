package fidness.modelo;

import fidness.excepcion.FidnessException;

public class DetalleRutina {
    private final Ejercicio ejercicio;
    private int series;
    private int repeticiones;

    public DetalleRutina(Ejercicio ejercicio, int series, int repeticiones)
            throws FidnessException {
        if (ejercicio == null) {
            throw new FidnessException("Seleccione un ejercicio.");
        }
        this.ejercicio = ejercicio;
        actualizarCantidades(series, repeticiones);
    }

    public void actualizarCantidades(int series, int repeticiones) throws FidnessException {
        if (series <= 0 || repeticiones <= 0) {
            throw new FidnessException("Las series y repeticiones deben ser mayores que cero.");
        }
        this.series = series;
        this.repeticiones = repeticiones;
    }

    public Ejercicio getEjercicio() { return ejercicio; }
    public int getSeries() { return series; }
    public int getRepeticiones() { return repeticiones; }
}
