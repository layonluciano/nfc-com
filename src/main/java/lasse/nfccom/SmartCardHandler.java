package lasse.nfccom;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
/**
 * 
 * @author layonluciano
 * This class is used to manage commands to be sent to Smart Cards
 */
public class SmartCardHandler {
	
	String cardUID;
	String cardATR;
	
	public SmartCardHandler(){
		this.cardUID = null;
		this.cardATR = null;
	}
	
	/**
	 * Command used to retrieve Cards UID
	 */
	static byte[] getUIDCommand = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00,
            (byte) 0x00, (byte) 0x00 };
	
	/**
	 * This method is responsible to retrieve a UID regarding a Smart card 
	 * @param cardTerminal 	an instance of the terminal reader
	 * @return SmartCard 	the card read by the terminal reader
	 * @throws InterruptedException
	 */
	public SmartCard getCardData(CardTerminal cardTerminal) throws InterruptedException{
		Card card;
		try{
			//Connects using any available protocol
			card = cardTerminal.connect("*");
			ATR atr = card.getATR();
			
			//A logical channel connection to a Smart Card.
			CardChannel channel = card.getBasicChannel();
			
			//A command APDU following the structure defined in ISO/IEC 7816-4
			CommandAPDU command = new CommandAPDU(getUIDCommand);
			
			//A response APDU as defined in ISO/IEC 7816-4.
			ResponseAPDU response = channel.transmit(command);
			
			byte[] iudBytes = response.getData();
			byte[] atrBytes = atr.getBytes();
			
			cardUID= convertToString(iudBytes);
			cardATR = convertToString(atrBytes);
			
		}catch(CardException e){
			e.printStackTrace();
			return null;
		}
		return new SmartCard(cardUID,cardATR);
	}
	
	
	/**
	 * This method converts a byte array to a String
	 * @param src		an byte array containing hexadecimal format
	 * @return String 	converted string 
	 */
	public String convertToString(byte[] src) {
        String answer = "";
        for (byte b : src) {
            answer = answer + String.format("%02X", b);
        }
        return answer;
    }
}
