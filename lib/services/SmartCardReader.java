package services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.smartcardio.CardTerminal;

import exceptions.TerminalReaderNotFoundException;
import interfaces.OnCardReadListener;
import models.SmartCard;

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
	 * This blocking method is used to read smart cards synchronously on default sector zero
	 * 
	 * @return SmartCard instance
	 * @throws InterruptedException
	 */
	public SmartCard read() throws InterruptedException {
		
		this.terminalReaderThread = new TerminalReaderThread(this);
		
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
	 * This blocking method is used to read smart cards synchronously with a chosen sector
	 * 
	 * @param sector Sector to be authenticated
	 * @return SmartCard instance
	 * @throws InterruptedException
	 */
	public SmartCard read(int sector, byte[] key) throws InterruptedException {
		
		this.terminalReaderThread = new TerminalReaderThread(this, sector, key);
		
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
	 * This non-blocking method is used to read smart cards asynchronously with a Callback on a default sector zero
	 *  
	 * @param command  Command issued to the reader
	 * @param callback Callback parameter to handle events
	 */
	public void read(OnCardReadListener callback) {
		
		this.terminalReaderThread = new TerminalReaderThread(callback, this);
		
		future = executorService.submit(terminalReaderThread);
	}
	
	/**
	 * This non-blocking method is used to read smart cards asynchronously with a Callback and chosen sector
	 * 
	 * @param callback 	Callback parameter to handle events
	 * @param sector 	Sector to be authenticated
	 */
	public void read(OnCardReadListener callback, int sector, byte[] key) {
		
		this.terminalReaderThread = new TerminalReaderThread(callback, this, sector, key, null);
		
		future = executorService.submit(terminalReaderThread);
	}
	
	/**
	 * This non-blocking method is used to write 16 bytes data to a NFC 1k card
	 * 
	 * @param callback 		Callback parameter to handle events
	 * @param sector 		Sector to be authenticated
	 * @param dataToWrite 	16 bytes data to be written on a NFC card
	 */
	public void write(OnCardReadListener callback, int sector, byte[] key, String dataToWrite){
		
		this.terminalReaderThread = new TerminalReaderThread(callback, this, sector, key, dataToWrite);
		
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
