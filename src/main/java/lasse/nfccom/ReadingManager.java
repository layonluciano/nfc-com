package lasse.nfccom;

public class ReadingManager {

	private Thread thread;

	private TerminalReaderThread terminalReaderThread;
	
	public ReadingManager(CardCallback callback) {
		this.terminalReaderThread = new TerminalReaderThread(callback);
		this.thread = new Thread(terminalReaderThread);
	}

	public void startReadingThread() {
		thread.start();
	}
	
	public void stopReadingThread() {
		terminalReaderThread.terminate();
	}

}
