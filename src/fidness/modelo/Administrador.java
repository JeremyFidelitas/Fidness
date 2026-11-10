package fidness.modelo;

public class Administrador extends Usuario {
    public Administrador(int idUsuario, String nombre, String nombreUsuario, char[] clave) {
        super(idUsuario, nombre, nombreUsuario, clave);
    }

    @Override
    public boolean esAdministrador() {
        return true;
    }

    @Override
    public String getPerfil() {
        return "Administrador";
    }
}
