package lasse.nfccom;

public class ReadingManager {
	Thread thread;
	TerminalReaderThread terminalReaderThread;
	
	public ReadingManager(CardCallback call){
		this.terminalReaderThread = new TerminalReaderThread(call);
		this.thread = new Thread(terminalReaderThread);
	}
	
	
	public void startReadingThread(){
		thread.start();
	}
	
	public void stopReadingThread(){
		terminalReaderThread.terminate();
	}
	
	public void setCardQuantity(int cardQty){
		terminalReaderThread.setQty(cardQty);
	}

	public TerminalReaderThread getTerminalReaderThread() {
		return terminalReaderThread;
	}
	
	
}
