package fidness.servicio;

import fidness.datos.ArchivoEjercicios;
import fidness.excepcion.FidnessException;
import fidness.modelo.Administrador;
import fidness.modelo.Cliente;
import fidness.modelo.Ejercicio;
import fidness.modelo.GrupoMuscular;
import fidness.modelo.Usuario;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SistemaFidness {
    private final List<Usuario> usuarios;
    private final List<Ejercicio> ejercicios;
    private final ArchivoEjercicios archivo;
    private Usuario usuarioActual;

    public SistemaFidness(ArchivoEjercicios archivo) throws FidnessException {
        this.archivo = archivo;
        usuarios = new ArrayList<>();
        prepararUsuarios();
        if (archivo.existe()) {
            ejercicios = archivo.cargar();
        } else {
            ejercicios = new ArrayList<>();
            prepararEjercicios();
            archivo.guardar(ejercicios);
        }
    }

    private void prepararUsuarios() {
        char[] claveAdmin = "Admin123".toCharArray();
        char[] claveCliente = "Fidness123".toCharArray();
        try {
            usuarios.add(new Administrador(1, "Administrador", "admin", claveAdmin));
            usuarios.add(new Cliente(2, "Jeremy", "jeremy", claveCliente));
        } finally {
            Arrays.fill(claveAdmin, '\0');
            Arrays.fill(claveCliente, '\0');
        }
    }

    private void prepararEjercicios() throws FidnessException {
        ejercicios.add(new Ejercicio(1, "Sentadilla", GrupoMuscular.PIERNA,
                "Flexionar las rodillas y llevar la cadera hacia atrás.\n"
                + "Volver a la posición inicial de forma controlada."));
        ejercicios.add(new Ejercicio(2, "Zancada", GrupoMuscular.PIERNA,
                "Dar un paso al frente y flexionar ambas rodillas.\n"
                + "Volver a la posición inicial y cambiar de pierna."));
        ejercicios.add(new Ejercicio(3, "Remo sentado", GrupoMuscular.ESPALDA,
                "Sentarse con la espalda recta. Llevar el agarre hacia el abdomen "
                + "y regresar de forma controlada."));
        ejercicios.add(new Ejercicio(4, "Curl de bíceps", GrupoMuscular.BRAZO,
                "Mantener los codos cerca del cuerpo. Flexionar los brazos "
                + "y bajar el peso de forma controlada."));
        ejercicios.add(new Ejercicio(5, "Elevación de talones", GrupoMuscular.PIERNA,
                "Elevar los talones sin perder el equilibrio. Bajar lentamente."));
        ejercicios.add(new Ejercicio(6, "Press de pecho", GrupoMuscular.PECHO,
                "Apoyar la espalda en el banco. Empujar las mancuernas hacia arriba "
                + "y regresar de forma controlada."));
        ejercicios.add(new Ejercicio(7, "Elevación lateral", GrupoMuscular.HOMBRO,
                "Elevar los brazos hacia los lados hasta la altura de los hombros. "
                + "Bajar lentamente."));
        ejercicios.add(new Ejercicio(8, "Crunch abdominal", GrupoMuscular.ABDOMEN,
                "Acostarse con las rodillas flexionadas. Elevar ligeramente el torso "
                + "sin tirar del cuello y regresar."));
    }

    public Usuario iniciarSesion(String nombreUsuario, char[] clave) throws FidnessException {
        usuarioActual = null;
        if (nombreUsuario == null || nombreUsuario.isBlank() || clave == null || clave.length == 0) {
            throw new FidnessException("Escriba su usuario y contraseña.");
        }
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario.strip())
                    && usuario.verificarClave(clave)) {
                usuarioActual = usuario;
                return usuario;
            }
        }
        throw new FidnessException("Usuario o contraseña incorrectos.");
    }

    public void cerrarSesion() { usuarioActual = null; }
    public Usuario getUsuarioActual() { return usuarioActual; }

    private void exigirSesion() throws FidnessException {
        if (usuarioActual == null) {
            throw new FidnessException("Inicie sesión para continuar.");
        }
    }

    private void exigirAdministrador() throws FidnessException {
        exigirSesion();
        if (!usuarioActual.esAdministrador()) {
            throw new FidnessException("Solo el administrador puede modificar el catálogo.");
        }
    }

    public List<Ejercicio> listarEjercicios() throws FidnessException {
        exigirAdministrador();
        return List.copyOf(ejercicios);
    }

    public List<Ejercicio> buscarEjercicios(String nombre, GrupoMuscular grupo)
            throws FidnessException {
        exigirSesion();
        String filtro = nombre == null ? "" : nombre.strip().toLowerCase(Locale.ROOT);
        List<Ejercicio> encontrados = new ArrayList<>();
        for (Ejercicio ejercicio : ejercicios) {
            if (ejercicio.isActivo()
                    && ejercicio.getNombre().toLowerCase(Locale.ROOT).contains(filtro)
                    && (grupo == null || grupo == ejercicio.getGrupoMuscular())) {
                encontrados.add(ejercicio);
            }
        }
        return encontrados;
    }

    private void revisarDuplicado(Ejercicio candidato) throws FidnessException {
        for (Ejercicio ejercicio : ejercicios) {
            if (ejercicio.getIdEjercicio() != candidato.getIdEjercicio()
                    && ejercicio.getGrupoMuscular() == candidato.getGrupoMuscular()
                    && ejercicio.getNombre().equalsIgnoreCase(candidato.getNombre())) {
                throw new FidnessException("Ya existe un ejercicio con ese nombre en el grupo.");
            }
        }
    }

    private Ejercicio encontrar(int id) throws FidnessException {
        for (Ejercicio ejercicio : ejercicios) {
            if (ejercicio.getIdEjercicio() == id) {
                return ejercicio;
            }
        }
        throw new FidnessException("El ejercicio seleccionado ya no está disponible.");
    }

    private List<Ejercicio> conReemplazo(Ejercicio candidato) {
        List<Ejercicio> copia = new ArrayList<>();
        for (Ejercicio ejercicio : ejercicios) {
            copia.add(ejercicio.getIdEjercicio() == candidato.getIdEjercicio()
                    ? candidato : ejercicio);
        }
        return copia;
    }

    public Ejercicio registrarEjercicio(String nombre, GrupoMuscular grupo, String instrucciones)
            throws FidnessException {
        exigirAdministrador();
        int id = 1;
        for (Ejercicio ejercicio : ejercicios) {
            id = Math.max(id, ejercicio.getIdEjercicio() + 1);
        }
        Ejercicio nuevo = new Ejercicio(id, nombre, grupo, instrucciones);
        revisarDuplicado(nuevo);
        List<Ejercicio> copia = new ArrayList<>(ejercicios);
        copia.add(nuevo);
        archivo.guardar(copia);
        ejercicios.add(nuevo);
        return nuevo;
    }

    public void actualizarEjercicio(int id, String nombre, GrupoMuscular grupo, String instrucciones)
            throws FidnessException {
        exigirAdministrador();
        Ejercicio original = encontrar(id);
        Ejercicio candidato = new Ejercicio(id, nombre, grupo, instrucciones);
        if (!original.isActivo()) { candidato.desactivar(); }
        revisarDuplicado(candidato);
        archivo.guardar(conReemplazo(candidato));
        original.actualizarDatos(nombre, grupo, instrucciones);
    }

    public void desactivarEjercicio(int id) throws FidnessException {
        exigirAdministrador();
        Ejercicio original = encontrar(id);
        if (!original.isActivo()) {
            throw new FidnessException("El ejercicio ya está inactivo.");
        }
        Ejercicio candidato = new Ejercicio(id, original.getNombre(),
                original.getGrupoMuscular(), original.getInstrucciones());
        candidato.desactivar();
        archivo.guardar(conReemplazo(candidato));
        original.desactivar();
    }
}
