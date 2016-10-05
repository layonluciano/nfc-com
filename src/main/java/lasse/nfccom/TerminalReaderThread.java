package lasse.nfccom;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

/**
 * This class is used to create a Smart Card reading thread  
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */

public class TerminalReaderThread implements Runnable {

	private CardTerminal cardTerminal;

	private SmartCardHandler smartCardHandler;
	
	private byte[] cardDataByte;

	private CardCallback callback;
	
	public TerminalReaderThread(CardCallback callback, byte[] command) {
		TerminalConnectionHandler terminalHandler = new TerminalConnectionHandler();
		this.cardTerminal = terminalHandler.getTerminalConnection();
		this.smartCardHandler = new SmartCardHandler();
		this.callback = callback;
		this.cardDataByte = command;
	}
	
	public TerminalReaderThread(byte[] command){
		TerminalConnectionHandler terminalHandler = new TerminalConnectionHandler();
		this.cardTerminal = terminalHandler.getTerminalConnection();
		this.smartCardHandler = new SmartCardHandler();
		this.cardDataByte = command;
	}
	
	public void run() {

		while(true) {

			try {
				System.out.println("Waiting for Smart Cards....");
				cardTerminal.waitForCardPresent(0);
				System.out.println("Now reading Smart Card.....");
				SmartCard card = smartCardHandler.getCardData(cardTerminal, cardDataByte);

				if (card == null) {
					throw new SmartCardNullValueAssociatedException(
							"Smart Card has no value associated due to short time in the Terminal Reader");
				}

				System.out.println("----------------------------------");
				System.out.println("Card UID: " + card.getData());
				System.out.println("----------------------------------");

				cardTerminal.waitForCardAbsent(0);
				System.out.println("Removed Smart card.....");

				onReadCard(card);
				
			}
			catch (CardException e) {
				throw new RuntimeException(e);
			}
			catch (NullPointerException ex2) {
				System.out.println("Error due to a " + ex2.getCause() + " value to CardTerminal object");
				ex2.printStackTrace();
			}
			catch (InterruptedException ex3) {
				System.out.println("Error, Main thread was interrupted.");
				ex3.printStackTrace();
			}
			catch (SmartCardNullValueAssociatedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method sends a Smart Card object as callback to a user
	 * @param card Smart Card to be sent via callback
	 */
	private void onReadCard(SmartCard card) {
		if (callback != null) {
			callback.responseToUser(card);
		}
		
	}

}
