package lasse.nfccom;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class provides even blocking or non-blocking access to Smart Card data
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */
public class SmartCardReader {
	
	private byte[] cardDataByte;
	
	private Future<?> future;

	private TerminalReaderThread terminalReaderThread;
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	public SmartCardReader(byte[] command) {
		this.cardDataByte = command; 
	}
	
	/**
	 * This blocking method is used to read smart cards synchronously.
	 * @return Smart card instance
	 */
	public SmartCard read() {
		this.terminalReaderThread = new TerminalReaderThread(cardDataByte);
		future = executorService.submit(terminalReaderThread);
		SmartCard sm = null;
		try {
			sm = (SmartCard) future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return sm;
	}
	
	/**
	 * This non-blocking method is used to read smart cards asynchronously with a Callback
	 * @param callback callback parameter to handle events
	 */
	public void read(CardCallback callback) {
		this.terminalReaderThread = new TerminalReaderThread(callback, cardDataByte);
		future = executorService.submit(terminalReaderThread);
	}
	
	/**
	 * This method is used to stop the card reading thread
	 */
	public void stopReadingThread() {
		future.cancel(true);
	}

}
