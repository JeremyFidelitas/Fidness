package fidness.datos;

import fidness.excepcion.FidnessException;
import fidness.modelo.Ejercicio;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ArchivoEjercicios {
    private final Path ruta;

    public ArchivoEjercicios(Path ruta) {
        this.ruta = ruta.toAbsolutePath();
    }

    public boolean existe() { return Files.exists(ruta); }

    public List<Ejercicio> cargar() throws FidnessException {
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ruta))) {
            entrada.setObjectInputFilter(info -> {
                if (info.depth() > 10 || info.references() > 100000 || info.arrayLength() > 100000) {
                    return java.io.ObjectInputFilter.Status.REJECTED;
                }
                Class<?> clase = info.serialClass();
                if (clase == null) {
                    return java.io.ObjectInputFilter.Status.UNDECIDED;
                }
                if (clase == ArrayList.class || clase == Ejercicio.class
                        || clase == fidness.modelo.GrupoMuscular.class || clase == Enum.class
                        || clase == String.class || clase == Object[].class) {
                    return java.io.ObjectInputFilter.Status.ALLOWED;
                }
                return java.io.ObjectInputFilter.Status.REJECTED;
            });
            Object objeto = entrada.readObject();
            if (!(objeto instanceof List<?>)) {
                throw new IOException("El contenido no es una lista de ejercicios.");
            }
            List<Ejercicio> ejercicios = new ArrayList<>();
            Set<Integer> ids = new HashSet<>();
            Set<String> nombres = new HashSet<>();
            for (Object elemento : (List<?>) objeto) {
                if (!(elemento instanceof Ejercicio)) {
                    throw new IOException("Hay un registro que no es un ejercicio.");
                }
                Ejercicio ejercicio = (Ejercicio) elemento;
                new Ejercicio(ejercicio.getIdEjercicio(), ejercicio.getNombre(),
                        ejercicio.getGrupoMuscular(), ejercicio.getInstrucciones());
                String clave = ejercicio.getNombre().strip().toLowerCase(Locale.ROOT)
                        + "|" + ejercicio.getGrupoMuscular().name();
                if (!ids.add(ejercicio.getIdEjercicio()) || !nombres.add(clave)) {
                    throw new IOException("Hay ejercicios repetidos.");
                }
                ejercicios.add(ejercicio);
            }
            return ejercicios;
        } catch (IOException | ClassNotFoundException ex) {
            throw new FidnessException("No se pudo leer el archivo de ejercicios. "
                    + "Conserve una copia del archivo antes de intentar recuperarlo.", ex);
        }
    }

    public void guardar(List<Ejercicio> ejercicios) throws FidnessException {
        Path temporal = null;
        try {
            Files.createDirectories(ruta.getParent());
            temporal = Files.createTempFile(ruta.getParent(), "ejercicios", ".tmp");
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(temporal))) {
                salida.writeObject(new ArrayList<>(ejercicios));
            }
            try {
                Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new FidnessException("No se pudo guardar el catálogo. Revise los permisos "
                    + "y el espacio disponible. El cambio no se aplicó.", ex);
        } finally {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ex) {
                    temporal.toFile().deleteOnExit();
                }
            }
        }
    }
}
