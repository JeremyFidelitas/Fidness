package fidness.modelo;

import fidness.excepcion.FidnessException;
import java.io.Serializable;

public class Ejercicio implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int idEjercicio;
    private String nombre;
    private GrupoMuscular grupoMuscular;
    private String instrucciones;
    private boolean activo;

    public Ejercicio(int idEjercicio, String nombre, GrupoMuscular grupoMuscular,
            String instrucciones) throws FidnessException {
        if (idEjercicio <= 0) {
            throw new FidnessException("El identificador del ejercicio no es válido.");
        }
        this.idEjercicio = idEjercicio;
        actualizarDatos(nombre, grupoMuscular, instrucciones);
        activo = true;
    }

    public void actualizarDatos(String nombre, GrupoMuscular grupo, String instrucciones)
            throws FidnessException {
        if (nombre == null || nombre.isBlank()) {
            throw new FidnessException("Escriba el nombre del ejercicio.");
        }
        if (grupo == null) {
            throw new FidnessException("Seleccione un grupo muscular.");
        }
        if (instrucciones == null || instrucciones.isBlank()) {
            throw new FidnessException("Escriba las instrucciones del ejercicio.");
        }
        this.nombre = nombre.strip();
        this.grupoMuscular = grupo;
        this.instrucciones = instrucciones.strip();
    }

    public void desactivar() {
        activo = false;
    }

    public int getIdEjercicio() {
        return idEjercicio;
    }

    public String getNombre() {
        return nombre;
    }

    public GrupoMuscular getGrupoMuscular() {
        return grupoMuscular;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public boolean isActivo() {
        return activo;
    }
}
