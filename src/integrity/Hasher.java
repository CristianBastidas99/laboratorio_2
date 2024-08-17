package integrity;
import util.Util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
