package lasse.nfccom;

import javax.smartcardio.*;

public class TerminalReaderThread implements Runnable{
	
	TerminalConnectionHandler terminalHandler ;
	CardTerminal cardTerminal; 
	SmartCardHandler smartCardHandler;
	SmartCardDataHolder smartCardDataHolder;
	SmartCard card;
	int qty;
	volatile boolean running = true;
	CardCallback cardCallback;
	
	public TerminalReaderThread(CardCallback cardCalback) {
		this.terminalHandler = new TerminalConnectionHandler();
		this.cardTerminal = terminalHandler.getTerminalConnection();
		this.smartCardHandler = new SmartCardHandler();
		this.smartCardDataHolder = new SmartCardDataHolder();
		this.card = null;
		this.qty = 0;
		this.cardCallback = cardCalback;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}

	public void terminate(){
		running = false;
		System.out.println("End of reading.");
	}
	
	public void run() {
		//if(qty == 0){
			while(running){
				card = readCard();
				cardCallback.responseToUser(card);//smartCardDataHolder.setCard(card);
			}
		//}else{
		//	for(int i=0;i<qty;i++){
		//		card = readCard();
		//		smartCardDataHolder.setCard(card);
		//	}
			
		//}
	}
	
	public SmartCardDataHolder getSmartCardDataHolder() {
		return smartCardDataHolder;
	}

	public SmartCard readCard(){
		
		SmartCard card = null;
		
		try{
			System.out.println("Waiting for Smart Cards....");
			cardTerminal.waitForCardPresent(0);
			System.out.println("Now reading Smart Card.....");
			card = smartCardHandler.getCardData(cardTerminal);
			
			if(card == null){
				throw new SmartCardNullValueAssociatedException("Smart Card has no value associated due to short time in the Terminal Reader");
			}
			
			System.out.println("----------------------------------");
			System.out.println("Card UID: " + card.getUid());
			System.out.println("----------------------------------");
			
			cardTerminal.waitForCardAbsent(0);
			System.out.println("Removed Smart card.....");
			
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
		return card;
	}


}
