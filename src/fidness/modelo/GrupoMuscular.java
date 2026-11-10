package fidness.modelo;

public enum GrupoMuscular {
    PIERNA("Pierna"), ESPALDA("Espalda"), PECHO("Pecho"),
    BRAZO("Brazo"), HOMBRO("Hombro"), ABDOMEN("Abdomen");

    private final String nombre;

    GrupoMuscular(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
