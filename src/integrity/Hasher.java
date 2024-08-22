package integrity;
import util.Util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Hasher {


    public static String getHash(String input, String algorithm) throws NoSuchAlgorithmException {
        byte[] inputBA = input.getBytes();

        MessageDigest hasher = MessageDigest.getInstance(algorithm);
        hasher.update(inputBA);

        return Util.byteArrayToHexString(hasher.digest(), "");
    }


    public static String getHashFile(String filename, String algorithm) throws Exception {
        MessageDigest hasher = MessageDigest.getInstance(algorithm);

        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];

        int in;
        while ((in = fis.read(buffer)) != -1) {
            hasher.update(buffer, 0, in);
        }
        fis.close();
        return Util.byteArrayToHexString(hasher.digest(), "");
    }

    public static void generateIntegrityCheckerFile(String folderName, String outputFileName) throws Exception {
        File folder = new File(folderName);
        File outputFile = new File(outputFileName);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("La carpeta no existe o no es un directorio.");
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("La carpeta está vacía.");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            for (File file : files) {
                if (file.isFile()) {
                    String hash = getHashFile(file.getPath(), "SHA-256");
                    String fileIndicator = isTextFile(file) ? " " : "*";
                    writer.println(hash + " " + fileIndicator + file.getName());
                }
            }
        }
    }

    private static boolean isTextFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int character;
            while ((character = reader.read()) != -1) {
                if (Character.isISOControl(character) && character != '\n' && character != '\r') {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void generateIntegrityFile(String folderName, String integrityFileName) throws Exception {
        File folder = new File(folderName);
        File integrityFile = new File(integrityFileName);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("La carpeta no existe o no es un directorio.");
        }

        if (!integrityFile.exists()) {
            throw new IllegalArgumentException("El archivo de integridad no existe.");
        }

        int missingFilesCount = 0;
        int checksumMismatchCount = 0;
        int improperlyFormattedLinesCount = 0;
        List<String> missingFiles = new ArrayList<>();
        List<String> checksumMismatchFiles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(integrityFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.contains("*") ? line.split("\\*", 2) : line.split("  ", 2);

                if (parts.length < 2) {
                    improperlyFormattedLinesCount++;
                    System.out.println(line + ": improperly formatted");
                    continue;
                }

                String expectedHash = parts[0].trim();
                String fileName = parts[1].trim();

                // Validaciones del hash
                if (expectedHash.length() != 64 || !Util.isHexadecimal(expectedHash)) {
                    improperlyFormattedLinesCount++;
                    checksumMismatchCount++;
                    System.out.println(line + ": improperly formatted");
                    continue;
                }

                File file = new File(folder, fileName);

                if (!file.exists()) {
                    missingFilesCount++;
                    missingFiles.add(fileName);
                    System.out.println(fileName + ": No such file or directory");
                    System.out.println(fileName + ": FAILED open or read");
                    continue;
                }

                String actualHash = getHashFile(file.getPath(), "SHA-256");
                if (!actualHash.equals(expectedHash)) {
                    checksumMismatchCount++;
                    checksumMismatchFiles.add(fileName);
                    System.out.println(fileName + ": FAILED");
                } else {
                    System.out.println(fileName + ": OK");
                }
            }
        }

        // Mostrar errores resumidos al final del proceso
        if (improperlyFormattedLinesCount > 0) {
            System.out.println("WARNING: " + improperlyFormattedLinesCount + (improperlyFormattedLinesCount == 1 ? " line is improperly formatted" : " lines are improperly formatted"));
        }
        if (missingFilesCount > 0) {
            System.out.println("WARNING: " + missingFilesCount + (missingFilesCount == 1 ? " listed file could not be read" : " listed files could not be read"));
        }
        if (checksumMismatchCount > 0) {
            System.out.println("WARNING: " + checksumMismatchCount + (checksumMismatchCount == 1 ? " computed checksum did NOT match" : " computed checksums did NOT match"));
        }

        if (missingFilesCount == 0 && checksumMismatchCount == 0 && improperlyFormattedLinesCount == 0) {
            System.out.println("Todos los archivos han pasado la verificación.");
        }
    }


}
