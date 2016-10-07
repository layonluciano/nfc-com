package lasse.nfccom;

/**
 * Command interface used to retrieve card information
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */
//Renomear para outra coisa
public interface CommandUtils {

    byte[] getUIDCommand = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00,
            (byte) 0x00, (byte) 0x00 };

    byte[] getSerialNumberCommand = new byte[] {(byte) 0xFF, (byte) 0xCA, (byte) 0x00,
            (byte) 0x00, (byte) 0x04 };

    byte[] getFirmwareVersionCommand = new byte[] { (byte) 0xFF, (byte) 0x00, (byte) 0x48,
            (byte) 0x00, (byte) 0x00 };

    byte[] setBuzzerOnCommand = new byte[] { (byte) 0xFF, (byte) 0x00, (byte) 0x52,
            (byte) 0xFF, (byte) 0x00 };

    byte[] setBuzzerOffCommand = new byte[] { (byte) 0xFF, (byte) 0x00, (byte) 0x52,
            (byte) 0x00, (byte) 0x00 };

    byte[] readBinaryBlockCommand = new byte[] { (byte) 0xFF, (byte) 0xB0, (byte) 0x00,
            (byte) 0x04, (byte) 0x10 };
    
    byte[] loadAuthenticationKeytoReaderCommand = new byte[] { (byte) 0xFF, (byte) 0x82, (byte) 0x00,
            (byte) 0x00, (byte) 0x06, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
    
    byte[] authenticationCommand = new byte[] { (byte) 0xFF, (byte) 0x86, (byte) 0x00,
            (byte) 0x00, (byte) 0x05, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x60, (byte) 0x00 };
}