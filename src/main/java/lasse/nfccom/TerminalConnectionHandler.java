package lasse.nfccom;
import java.util.List;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
/**
 * 
 * @author layonluciano
 * This class is used to manage connections to the Terminal Reader 
 */
public class TerminalConnectionHandler {
	TerminalFactory factory;
	List<CardTerminal> terminalsList;
	CardTerminal cardTerminal;
	
	public TerminalConnectionHandler(){
		this.factory = null;
		this.terminalsList = null;
		this.cardTerminal = null;
	}
	
	/**
	 * This method is used to establish a connection with the Terminal reader
	 * @return CardTerminal		card terminal used to read card tags
	 */
	public CardTerminal getTerminalConnection(){
		try{
			factory = TerminalFactory.getDefault();
			
			//Returns a list containing all available Terminal readers
			terminalsList = factory.terminals().list();	
			
			if(terminalsList.size() > 0){
				System.out.println("Terminal reader detected!");
				cardTerminal = terminalsList.get(0);
			}else{
				System.out.println("Terminal reader NOT detected!!");
				return null;
			}	
		}catch(Exception e){
			
		}
		return cardTerminal;	
	}
	
}
