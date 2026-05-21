package Config;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class ImageStorage {

    private static final Path RESOURCE_IMAGES_DIR = Paths.get("src", "main", "resources", "images");

    private ImageStorage() {
    }

    public static File chooseImage(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar imagen");
        chooser.setFileFilter(new FileNameExtensionFilter("Imagenes", "jpg", "jpeg", "png", "gif", "webp"));

        int result = chooser.showOpenDialog(parent);
        return result == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    public static String saveImage(File sourceFile, String folderName, String entityName) throws IOException {
        if (sourceFile == null) {
            return "";
        }

        String extension = getExtension(sourceFile.getName());
        String safeName = sanitizeFileName(entityName);
        Path targetDir = RESOURCE_IMAGES_DIR.resolve(sanitizeFileName(folderName));
        Files.createDirectories(targetDir);

        Path targetFile = targetDir.resolve(safeName + extension);
        Files.copy(sourceFile.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        return "images/" + sanitizeFileName(folderName) + "/" + safeName + extension;
    }

    private static String getExtension(String fileName) {
        int dotIndex = fileName == null ? -1 : fileName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return ".png";
        }

        return fileName.substring(dotIndex).toLowerCase();
    }

    private static String sanitizeFileName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "imagen";
        }

        return value.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9._-]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
