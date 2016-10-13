package lasse.nfccom;

import java.util.concurrent.Callable;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

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
	
	private String dataToWrite;
	
	/**
	 * Constructor with callback
	 * 
	 * @param callback 			Callback to be sent to user
	 * @param command  			Command issued to the reader
	 * @param smartCardReader 	SmartCardReader instance with a CardTerminal attached
	 */
	public TerminalReaderThread(OnCardReadListener callback, SmartCardReader smartCardReader, int sector, String dataToWrite) {
		this.cardTerminal = smartCardReader.getCardTerminal();
		this.smartCardHandler = new SmartCardHandler();
		this.callback = callback;
		this.sector = sector;
		this.dataToWrite = dataToWrite;
	}
	
	public TerminalReaderThread(OnCardReadListener callback, SmartCardReader smartCardReader, int sector) {
		this.cardTerminal = smartCardReader.getCardTerminal();
		this.smartCardHandler = new SmartCardHandler();
		this.callback = callback;
		this.sector = sector;
	}
	/**
	 * Constructor without callback
	 * 
	 * @param command			Callback to be sent to user
	 * @param smartCardReader 	SmartCardReader instance with a CardTerminal attached
	 */
	public TerminalReaderThread(SmartCardReader smartCardReader, int sector){
		this.cardTerminal = smartCardReader.getCardTerminal();
		this.smartCardHandler = new SmartCardHandler();
		this.sector = sector;
	}
	
	
	@Override
	public SmartCard call() throws Exception{
		SmartCard card;

		while(true) {

			try {
				System.out.println("Waiting for Smart Cards....");
				
				cardTerminal.waitForCardPresent(0);
				
				System.out.println("Now reading Smart Card.....");
				
				if(dataToWrite == null){
					card = smartCardHandler.getCardData(cardTerminal, sector);
				}else{
					card = smartCardHandler.setCardData(cardTerminal, sector, dataToWrite);
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
