package services;

import java.util.concurrent.Callable;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import exceptions.SmartCardNullValueAssociatedException;
import interfaces.OnCardReadListener;
import models.SmartCard;

/**
 * This class is used to create a Smart Card reading thread  
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */

public class TerminalReaderThread implements Callable<SmartCard> {

	private CardTerminal cardTerminal;

	private SmartCardHandler smartCardHandler;

	private OnCardReadListener callback;
	
	private int sector;
	
	private byte[] key;
	
	private String dataToWrite;
	
	/**
	 * Constructor with callback and authentication
	 * 
	 * @param callback 			Callback to be sent to user
	 * @param smartCardReader 	SmartCardReader instance with a CardTerminal attached
	 * @param sector 			Sector to be authenticated
	 * @param key 				key to authenticate on sector
	 * @param dataToWrite 		Data to be written on NFC card
	 */
	public TerminalReaderThread(OnCardReadListener callback, SmartCardReader smartCardReader, int sector, byte[] key, String dataToWrite) {
		this.cardTerminal = smartCardReader.getCardTerminal();
		this.smartCardHandler = new SmartCardHandler();
		this.callback = callback;
		this.sector = sector;
		this.key = key;
		this.dataToWrite = dataToWrite;
	}
	
	/**
	 * Constructor with callback but without authentication
	 * 
	 * @param callback			Callback to be sent to user
	 * @param smartCardReader	SmartCardReader instance with a CardTerminal attached
	 */
	public TerminalReaderThread(OnCardReadListener callback, SmartCardReader smartCardReader) {
		this.cardTerminal = smartCardReader.getCardTerminal();
		this.smartCardHandler = new SmartCardHandler();
		this.callback = callback;
		this.sector = -1;
		this.key = null;
	}
	/**
	 * Constructor without callback but with authentication
	 * 
	 * @param smartCardReader 	SmartCardReader instance with a CardTerminal attached
	 * @param sector 			Sector to be authenticated
	 * @param key 				key to authenticate on sector
	 */
	public TerminalReaderThread(SmartCardReader smartCardReader, int sector, byte[] key){
		this.cardTerminal = smartCardReader.getCardTerminal();
		this.smartCardHandler = new SmartCardHandler();
		this.sector = sector;
		this.key = key;
	}
	
	/**
	 * Constructor without callback and authentication
	 * 
	 * @param smartCardReader	SmartCardReader instance with a CardTerminal attached
	 */
	public TerminalReaderThread(SmartCardReader smartCardReader){
		this.cardTerminal = smartCardReader.getCardTerminal();
		this.smartCardHandler = new SmartCardHandler();
		this.sector = -1;
		this.key = null;
	}
	
	
	
	@Override
	public SmartCard call() throws Exception{
		SmartCard card;

		while(true) {

			try {
				System.out.println("Waiting for Smart Cards....");
				
				cardTerminal.waitForCardPresent(0);
				
				System.out.println("Now reading Smart Card.....");
				
				if(key == null){
					card = smartCardHandler.getCardUID(cardTerminal);
					
				}else if(dataToWrite == null){
					card = smartCardHandler.getCardData(cardTerminal, sector, key);
	
				}else{
					card = smartCardHandler.setCardData(cardTerminal, sector, key, dataToWrite);
				}

				if (card == null) {
					throw new SmartCardNullValueAssociatedException(
							"Smart Card has no value associated due to short time in the Terminal Reader");
				}

				cardTerminal.waitForCardAbsent(0);
				
				System.out.println("Smart card has been removed.....");
				
				//Returns either a Callback or a Smart Card 
				if(callback != null){
					callback.onCardRead(card);
				}else{
					return card;
				}
				
			}
			catch (CardException e) {
				throw new RuntimeException(e);
			}
			catch (NullPointerException e) {
				System.out.println("Error due to a " + e.getCause() + " value to CardTerminal object");
				e.printStackTrace();
			}
			catch (SmartCardNullValueAssociatedException e) {
				e.printStackTrace();
			}
		}
	}

}
