package lasse.nfccom;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.smartcardio.CardTerminal;

/**
 * This class provides even blocking or non-blocking access to Smart Card data
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */
public class SmartCardReader {
	
	private CardTerminal cardTerminal;
	
	private Future<?> future;

	private TerminalReaderThread terminalReaderThread;
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	/**
	 * Class constructor
	 * @param cardTerminal CardTerminal instance
	 */
	public SmartCardReader(CardTerminal cardTerminal) {
		this.cardTerminal = cardTerminal; 
	}
	
	
	/**
	 * This blocking method is used to read smart cards synchronously.
	 * 
	 * @param command Command issued to the reader					
	 * @return SmartCard instance
	 * @throws InterruptedException
	 */
	public SmartCard read(int sector) throws InterruptedException {
		
		this.terminalReaderThread = new TerminalReaderThread(this, sector);
		
		future = executorService.submit(terminalReaderThread);
		
		SmartCard smartCard = null;
		
		try {
		
			smartCard = (SmartCard) future.get();
			
		} catch (ExecutionException e) {
			throw new TerminalReaderNotFoundException(
					"Terminal Reader not found during program execution.");
		}
		
		return smartCard;
	}
	
	
	/**
	 * This non-blocking method is used to read smart cards asynchronously with a Callback
	 *  
	 * @param command  Command issued to the reader
	 * @param callback Callback parameter to handle events
	 */
	public void read(OnCardReadListener callback, int sector) {
		
		this.terminalReaderThread = new TerminalReaderThread(callback, this, sector);
		
		future = executorService.submit(terminalReaderThread);
	}
	
	public void write(OnCardReadListener callback, int sector, String dataToWrite){
		
		this.terminalReaderThread = new TerminalReaderThread(callback, this, sector, dataToWrite);
		
		future = executorService.submit(terminalReaderThread);
	}
	
	
	/**
	 * This method is used to stop the card reading thread
	 */
	public void stopReadingThread() {
		
		future.cancel(true);
	}
	
	
	/**
	 * Method to get a CardTerminal instance
	 * @return CardTerminal instance
	 */
	public CardTerminal getCardTerminal() {
		return cardTerminal;
	}
	
	

}
