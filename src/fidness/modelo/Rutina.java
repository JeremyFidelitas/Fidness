package fidness.modelo;

import fidness.excepcion.FidnessException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Rutina {
    private final int idRutina;
    private final String nombre;
    private final LocalDate fechaCreacion;
    private final Cliente propietario;
    private final List<DetalleRutina> detalles;

    public Rutina(int idRutina, String nombre, Cliente propietario) throws FidnessException {
        if (idRutina <= 0 || propietario == null || nombre == null || nombre.isBlank()) {
            throw new FidnessException("La rutina necesita un nombre, un cliente y un identificador.");
        }
        this.idRutina = idRutina;
        this.nombre = nombre.strip();
        this.propietario = propietario;
        fechaCreacion = LocalDate.now();
        detalles = new ArrayList<>();
    }

    public void agregarEjercicio(Ejercicio ejercicio, int series, int repeticiones)
            throws FidnessException {
        if (ejercicio == null || !ejercicio.isActivo()) {
            throw new FidnessException("Seleccione un ejercicio activo.");
        }
        for (DetalleRutina detalle : detalles) {
            if (detalle.getEjercicio().getIdEjercicio() == ejercicio.getIdEjercicio()) {
                throw new FidnessException("Ese ejercicio ya está en la rutina.");
            }
        }
        detalles.add(new DetalleRutina(ejercicio, series, repeticiones));
    }

    public void actualizarDetalle(int idEjercicio, int series, int repeticiones)
            throws FidnessException {
        buscarDetalle(idEjercicio).actualizarCantidades(series, repeticiones);
    }

    public void quitarEjercicio(int idEjercicio) throws FidnessException {
        detalles.remove(buscarDetalle(idEjercicio));
    }

    private DetalleRutina buscarDetalle(int idEjercicio) throws FidnessException {
        for (DetalleRutina detalle : detalles) {
            if (detalle.getEjercicio().getIdEjercicio() == idEjercicio) {
                return detalle;
            }
        }
        throw new FidnessException("El ejercicio no está en la rutina.");
    }

    public boolean esValida() { return !detalles.isEmpty(); }
    public int getIdRutina() { return idRutina; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public Cliente getPropietario() { return propietario; }
    public List<DetalleRutina> getDetalles() { return List.copyOf(detalles); }
}
