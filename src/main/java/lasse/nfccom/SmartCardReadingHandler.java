package lasse.nfccom;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class SmartCardReadingHandler {
	
	SmartCard sc;
	TerminalConnectionHandler terminalHandler ;
	CardTerminal cardTerminal; 
	SmartCardHandler smartCardHandler;
	List<SmartCard> cardList;
	
	
	public SmartCardReadingHandler() throws TerminalReaderNotFoundException {
		this.sc = null;
		this.terminalHandler = new TerminalConnectionHandler();
		this.cardTerminal = terminalHandler.getTerminalConnection();
		this.smartCardHandler = new SmartCardHandler();
		this.cardList = null;
	}
	
	public void doConcurrentReading(){
	//Parte do Guava
		ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
		ListenableFuture<SmartCard> future = service.submit(new Callable<SmartCard>() {
			public SmartCard call() throws TerminalReaderNotFoundException{
				return readCard();	
			}
		});
		Futures.addCallback(future, new FutureCallback<SmartCard>() {

			public void onFailure(Throwable arg0) {
				System.out.println("FALHA NA THREAD");
			}

			public void onSuccess(SmartCard card) {
				System.out.println("OK NA THREAD");
				System.out.println(card.getUid());
			}
			
		});
	}
	
	
	public SmartCard readCard() throws TerminalReaderNotFoundException{
		
		SmartCard smc = null;
		
		try{
			for(int i=0;i<3;i++){
			cardTerminal.waitForCardPresent(0);
			System.out.println("Now reading Smart Card.....");
			smc = smartCardHandler.getCardData(cardTerminal);
			
			if(smc == null){
				throw new SmartCardNullValueAssociatedException("Smart Card has no value associated due to short time in the Terminal Reader");
			}
			
			System.out.println("----------------------------------");
			System.out.println("Card UID: " + smc.getUid());
			System.out.println("----------------------------------");
			
			cardTerminal.waitForCardAbsent(0);
			System.out.println("Removed Smart card.....");
			}
		}catch (CardException e) {
			throw new TerminalReaderNotFoundException("Can't connect to Terminal Reader to get data");
		}catch (NullPointerException ex2){
			System.out.println("Error due to a "+ ex2.getCause() +" value to CardTerminal object");
			System.exit(0);
		}catch (InterruptedException ex3) {
			System.out.println("Error, Main thread was interrupted.");
		} catch (SmartCardNullValueAssociatedException e) {
			e.printStackTrace();
		}
		
		return smc;
	}
	
}
