package integrity;

public class HasherTester3 {
 public static void main(String[] args) throws Exception {
        String filename= "binaryfiles";
     try {
         // Generar archivo de integridad
         Hasher.generateIntegrityCheckerFile(filename, "integrity.txt");
     } catch (Exception e) {
         e.printStackTrace();
     }
    }
}
