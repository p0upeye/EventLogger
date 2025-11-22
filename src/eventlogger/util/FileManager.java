package eventlogger.util;

import java.io.File;
import java.io.IOException;

public class FileManager {

    /**
     * Перевіряє існування файлу в заданій директорії.
     * Якщо директорія або файл не існують, вони будуть створені.
     * @param directoryPath Шлях до директорії.
     * @param fileName Ім'я файлу.
     * @return Повний шлях до файлу.
     */
    public static String fileExistenceChecker(String directoryPath, String fileName) {
        String fullPath = directoryPath + File.separator + fileName;

        try {
            // Створення директорії, якщо її не існує
            File directory = new File(directoryPath);

            if(!directory.exists()) {

                if(directory.mkdirs()) {
                    System.out.println("[+] Directory created: " + directoryPath);
                } else {
                    throw new RuntimeException("Could not create directory: " + directoryPath);
                }
            }

            // Створення файлу, якщо його не існує
            File file = new File(fullPath);

            if(!file.exists()) {
                if(file.createNewFile()) {
                    System.out.println("[+] File created: " + fileName);
                }
            }

            return fullPath;
        } catch(IOException e) {
            throw new RuntimeException("Could not create file: " + fullPath, e);
        }
    }

    /**
     * Перевіряє, чи існує файл за вказаним шляхом.
     * @param filePath Шлях до файлу.
     * @return true, якщо файл існує, інакше false.
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Перевіряє, чи файл за вказаним шляхом доступний для читання.
     * @param filePath Шлях до файлу.
     * @return true, якщо файл читабельний, інакше false.
     */
    public static boolean isReadable(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.canRead();
    }

    /**
     * Перевіряє, чи файл за вказаним шляхом доступний для запису.
     * @param filePath Шлях до файлу.
     * @return true, якщо файл доступний для запису, інакше false.
     */
    public static boolean isWritable(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.canWrite();
    }
}
