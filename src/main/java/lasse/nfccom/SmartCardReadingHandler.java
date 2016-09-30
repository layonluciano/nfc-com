package lasse.nfccom;


import java.util.ArrayList;
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


@Deprecated
public class SmartCardReadingHandler {
	
	SmartCard sc;
	TerminalConnectionHandler terminalHandler ;
	CardTerminal cardTerminal; 
	SmartCardHandler smartCardHandler;
	//List<SmartCard> cardList;
	
	
	public SmartCardReadingHandler() throws TerminalReaderNotFoundException {
		this.sc = null;
		this.terminalHandler = new TerminalConnectionHandler();
		this.cardTerminal = terminalHandler.getTerminalConnection();
		this.smartCardHandler = new SmartCardHandler();
		//this.cardList = new ArrayList<SmartCard>();
	}
	
	public void doConcurrentReading(/*final String[] masterTagList*/){
	//Parte do Guava
		ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
		ListenableFuture <SmartCard> future = service.submit(new Callable<SmartCard>() {	
			public SmartCard call() throws TerminalReaderNotFoundException{
				//for(int i=0;i<3;i++)
				return readCard(/*masterTagList*/);
			}
		});
		Futures.addCallback(future, new FutureCallback<SmartCard>() {

			public void onFailure(Throwable arg0) {
				System.out.println("FALHA NA THREAD");
				//lançar uma exception
			}

			public void onSuccess(SmartCard cardListReturned) {
				System.out.println("OK NA THREAD");
				//System.out.println(cardListReturned.get(2).getUid());
			}
			
		});
	}
	
	
	
	public SmartCard readCard(/*String[] masterTagList*/) throws TerminalReaderNotFoundException{
		
		SmartCard smc = null;
		boolean flag = false;
		
		try{
			//while(true){
				cardTerminal.waitForCardPresent(0);
				System.out.println("Now reading Smart Card.....");
				smc = smartCardHandler.getCardData(cardTerminal);
				
				if(smc == null){
					throw new SmartCardNullValueAssociatedException("Smart Card has no value associated due to short time in the Terminal Reader");
				}
				/*for(String tag:masterTagList){
					if(smc.getUid().equals(tag)){
						System.out.println("pegou uma MasterTag");
						flag = true;
						break;
					}
				}
				if (flag){
					break;
				}*/
				System.out.println("----------------------------------");
				System.out.println("Card UID: " + smc.getUid());
				System.out.println("----------------------------------");
				
				cardTerminal.waitForCardAbsent(0);
				System.out.println("Removed Smart card.....");
				
				//Thread.sleep(100);
				//addCardToList(smc);
				//Thread.sleep(100);
			//}
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

	//private void addCardToList(SmartCard smc) {
	//	cardList.add(smc);
	//	
	//}
	
}
