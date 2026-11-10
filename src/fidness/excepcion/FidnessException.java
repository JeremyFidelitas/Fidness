package fidness.excepcion;

public class FidnessException extends Exception {
    private static final long serialVersionUID = 1L;

    public FidnessException(String mensaje) {
        super(mensaje);
    }

    public FidnessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
