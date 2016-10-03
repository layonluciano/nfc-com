package lasse.nfccom;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import static lasse.nfccom.StringHelper.convertToString;

/**
 * 
 * @author layonluciano
 * This class is used to manage commands to be sent to Smart Cards
 */
public class SmartCardHandler {
	
	private String cardUID;

	private String cardATR;
	
	public SmartCardHandler() {
		this.cardUID = null;
		this.cardATR = null;
	}

	/**
	 * This method is responsible to retrieve a UID regarding a Smart card 
	 * @param cardTerminal 	an instance of the terminal reader
	 * @return SmartCard 	the card read by the terminal reader
	 * @throws InterruptedException
	 * @throws SmartCardNullValueAssociatedException 
	 */
	public SmartCard getCardData(CardTerminal cardTerminal) throws InterruptedException, SmartCardNullValueAssociatedException {

		try {
			//Connects using any available protocol
			Card card = cardTerminal.connect("*");
			ATR atr = card.getATR();
			
			//A logical channel connection to a Smart Card.
			CardChannel channel = card.getBasicChannel();
			
			//A command APDU following the structure defined in ISO/IEC 7816-4
			CommandAPDU command = new CommandAPDU(CardCommands.getUIDCommand);
			
			//A response APDU as defined in ISO/IEC 7816-4.
			ResponseAPDU response = channel.transmit(command);
			
			byte[] iudBytes = response.getData();
			byte[] atrBytes = atr.getBytes();
			
			cardUID = convertToString(iudBytes);
			cardATR = convertToString(atrBytes);

			return new SmartCard(cardUID, cardATR);
		}
		catch(CardException e) {
			throw new SmartCardNullValueAssociatedException("Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
		
	}

}
