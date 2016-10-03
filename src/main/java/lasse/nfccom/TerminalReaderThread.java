package lasse.nfccom;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class TerminalReaderThread implements Runnable {

	private CardTerminal cardTerminal;

	private SmartCardHandler smartCardHandler;

	private volatile boolean running = true;

	private CardCallback callback;
	
	public TerminalReaderThread(CardCallback callback) {
		TerminalConnectionHandler terminalHandler = new TerminalConnectionHandler();
		this.cardTerminal = terminalHandler.getTerminalConnection();
		this.smartCardHandler = new SmartCardHandler();
		this.callback = callback;
	}
	
	public void terminate() {
		running = false;
		System.out.println("End of reading.");
	}
	
	public void run() {

		while(running) {

			try {
				System.out.println("Waiting for Smart Cards....");
				cardTerminal.waitForCardPresent(0);
				System.out.println("Now reading Smart Card.....");
				SmartCard card = smartCardHandler.getCardData(cardTerminal);

				if (card == null) {
					throw new SmartCardNullValueAssociatedException(
							"Smart Card has no value associated due to short time in the Terminal Reader");
				}

				System.out.println("----------------------------------");
				System.out.println("Card UID: " + card.getUid());
				System.out.println("----------------------------------");

				cardTerminal.waitForCardAbsent(0);
				System.out.println("Removed Smart card.....");

				callback.responseToUser(card);
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

}
