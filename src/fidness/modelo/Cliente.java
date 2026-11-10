package fidness.modelo;

public class Cliente extends Usuario {
    public Cliente(int idUsuario, String nombre, String nombreUsuario, char[] clave) {
        super(idUsuario, nombre, nombreUsuario, clave);
    }

    @Override
    public boolean esAdministrador() {
        return false;
    }

    @Override
    public String getPerfil() {
        return "Cliente";
    }
}
