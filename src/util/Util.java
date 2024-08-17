package util;

public class Util {
    public static String byteArrayToHexString(byte[] bytes, String separator){
        String resultado = "";

        for (int i = 0; i < bytes.length; i++) {
            resultado += String.format("%02x", bytes[i]) + separator;
        }

        return resultado;
    }

    //metodo para verificar si un hash es o no hexadecimal
    public static boolean isHexadecimal(String hash){
        return hash.matches("[0-9a-fA-F]+");
    }
}
