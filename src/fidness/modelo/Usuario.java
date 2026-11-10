package fidness.modelo;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class Usuario {
    private final int idUsuario;
    private final String nombre;
    private final String nombreUsuario;
    private final byte[] sal;
    private final byte[] claveHash;

    public Usuario(int idUsuario, String nombre, String nombreUsuario, char[] clave) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        claveHash = calcularHash(clave);
    }

    private byte[] calcularHash(char[] clave) {
        PBEKeySpec especificacion = new PBEKeySpec(clave, sal, 120000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacion).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("No se pudo preparar la contraseña.", ex);
        } finally {
            especificacion.clearPassword();
        }
    }

    public boolean verificarClave(char[] clave) {
        if (clave == null) {
            return false;
        }
        byte[] resultado = calcularHash(clave);
        try {
            return MessageDigest.isEqual(claveHash, resultado);
        } finally {
            Arrays.fill(resultado, (byte) 0);
        }
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public abstract boolean esAdministrador();
    public abstract String getPerfil();
}
