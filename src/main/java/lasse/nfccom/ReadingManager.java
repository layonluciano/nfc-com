package lasse.nfccom;

public class ReadingManager {
	Thread thread;
	TerminalReaderThread terminalReaderThread;
	
	public ReadingManager(){
		this.terminalReaderThread = new TerminalReaderThread();
		this.thread = new Thread(terminalReaderThread); 
	}
	
	public void startReadingThread(){
		thread.start();
	}
	
	public void stopReadingThread(){
		terminalReaderThread.terminate();
	}

	public TerminalReaderThread getTerminalReaderThread() {
		return terminalReaderThread;
	}
	
	
}
