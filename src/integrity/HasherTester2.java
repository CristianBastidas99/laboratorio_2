package integrity;

public class HasherTester2 {
    public static void main(String[] args) throws Exception {
        String filename= "scan.pdf";
        String hash = Hasher.getHashFile(filename, "SHA-256");

        System.out.println(hash);

    }
}
