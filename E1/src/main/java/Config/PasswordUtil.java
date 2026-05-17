package Config;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Seguridad: utilidad encargada de generar y validar hashes PBKDF2.
public final class PasswordUtil {

    // PBKDF2: algoritmo de derivacion usado para proteger contrasenas.
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    // PBKDF2: numero de iteraciones para endurecer el hash.
    private static final int ITERATIONS = 65536;
    // PBKDF2: tamano de la llave derivada.
    private static final int KEY_LENGTH = 256;
    // Seguridad: tamano del salt aleatorio en bytes.
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
    }

    // PBKDF2: genera el hash final en formato pbkdf2$iteraciones$salt$hash.
    public static String hashPassword(char[] password) {
        try {
            // Seguridad: crea un salt distinto por cada contrasena.
            byte[] salt = generateSalt();
            // PBKDF2: deriva el hash con el algoritmo configurado.
            byte[] hash = pbkdf2(password, salt, ITERATIONS, KEY_LENGTH);

            return "pbkdf2$"
                    + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (GeneralSecurityException e) {
            throw new RuntimeException("No se pudo encriptar la contraseña.", e);
        }
    }

    // Seguridad: verifica si una contrasena plana coincide con el hash almacenado.
    public static boolean verifyPassword(char[] password, String storedPassword) {
        try {
            // PBKDF2: valida que el formato guardado sea el esperado.
            if (storedPassword == null || !storedPassword.startsWith("pbkdf2$")) {
                return false;
            }

            // POO: separa las partes del hash persistido para procesarlas.
            String[] parts = storedPassword.split("\\$");

            if (parts.length != 4) {
                return false;
            }

            // PBKDF2: recupera la configuracion y los valores originales del hash.
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] storedHash = Base64.getDecoder().decode(parts[3]);

            // PBKDF2: recalcula el hash para comparar contra el guardado.
            byte[] calculatedHash = pbkdf2(password, salt, iterations, storedHash.length * 8);

            // Seguridad: compara los hashes en tiempo constante.
            return MessageDigest.isEqual(storedHash, calculatedHash);

        } catch (Exception e) {
            return false;
        }
    }

    // Seguridad: genera un salt aleatorio para fortalecer el hash.
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // PBKDF2: aplica el algoritmo a la contrasena recibida.
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws GeneralSecurityException {

        // Seguridad: prepara la especificacion con salt, iteraciones y longitud deseada.
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        try {
            return factory.generateSecret(spec).getEncoded();
        } finally {
            // Seguridad: limpia la contrasena en memoria tan pronto como se termina de usar.
            spec.clearPassword();
        }
    }
}
