package integrity;

public class HasherTester3 {
 public static void main(String[] args) throws Exception {
        String filename= "binaryfiles";
     try {
         // Generar archivo de integridad
         //Hasher.generateIntegrityCheckerFile(filename, "sha256sum.txt");

         Hasher.generateIntegrityFile(filename, "sha256sum.txt");
     } catch (Exception e) {
         e.printStackTrace();
     }
    }
}
