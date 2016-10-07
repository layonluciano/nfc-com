package lasse.nfccom;

/**
 * This class is used to converts objects to String
 *
 * @author Anderson Rodrigo Davi
 * 
 * Created on 03/10/16. 
 */
public class StringHelper {

    /**
     * This method converts a byte array to a String
     * @param src an byte array containing hexadecimal format
     * @return String converted string
     */
    public static String convertToString(byte[] src) {
    	
        String answer = "";
        
        for (byte b : src) {
            answer = answer + String.format("%02X", b);
        }
        
        return answer;
    }
}
