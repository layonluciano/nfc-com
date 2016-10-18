package services;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import exceptions.TerminalCardDriversNotAvailableException;
import exceptions.TerminalReaderNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * This class is used to manage connections to the Terminal Reader
 *
 * @author Anderson Rodrigo Davi
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */
public class SmartCardReaderService {

	private static final String NONE_TYPE = "None";

	private TerminalFactory factory;
	
	/**
	 * Class constructor to get default TerminalFactory
	 */
	public SmartCardReaderService() {
		this(TerminalFactory.getDefault());
	}
	
	/**
	 * Class constructor with a customized TerminalFactory object
	 * 
	 * @param terminalFactory Selected TerminalFactory object
	 */
	public SmartCardReaderService(TerminalFactory terminalFactory) {
		this.factory = terminalFactory;
	}

	
	/**
	 * This method is used to get a connection with one Terminal Reader
	 * @return SmartCardReader object that holds a CardTerminal instance
	 * @throws TerminalReaderNotFoundException
	 */
	public SmartCardReader getOne() throws TerminalReaderNotFoundException{
		
		if (NONE_TYPE.equals(factory.getType())) {
			throw new TerminalCardDriversNotAvailableException(
					"No drivers available. Check if de library libpcsclite.so.1 is loaded.");
		}
		try {

			List<CardTerminal> terminals = factory.terminals().list();
			
			CardTerminal cardTerminal = terminals.get(0);
			
			SmartCardReader smartCardReader = new SmartCardReader(cardTerminal);

			System.out.println(format("Using the: {0}", cardTerminal.getName()));

			return smartCardReader;
		}
		catch (CardException e) {
			throw new TerminalReaderNotFoundException(
					"No card readers available.");
		}
	}
	
	
	/**
	 * This method is used to get a connection with all Terminal Readers available
	 * @return List of SmartCardReader objects , each one holding a CardTerminal instance
	 * @throws TerminalReaderNotFoundException
	 */
	public List<SmartCardReader> getAll() throws TerminalReaderNotFoundException{
		
		if (NONE_TYPE.equals(factory.getType())) {
			throw new TerminalCardDriversNotAvailableException(
					"No drivers available. Check if de library libpcsclite.so.1 is loaded.");
		}
		try {
			
			List<CardTerminal> terminals = factory.terminals().list();
			
			List<SmartCardReader> smartCardReaderList = new ArrayList<SmartCardReader>();
			
			for(CardTerminal cardTerminal:terminals){
				SmartCardReader cardReader = new SmartCardReader(cardTerminal);
				smartCardReaderList.add(cardReader);
			}

			System.out.println(format("NFC Terminals available ({0}):", terminals.size()));

			for (int i = 0; i < terminals.size(); i++) {
				System.out.println(format("[{0}] {1}", i, terminals.get(i).getName()));
			}

			return smartCardReaderList;
		}
		catch (CardException e) {
			throw new TerminalReaderNotFoundException(
					"No card readers available.");
		}
	}
		
}
