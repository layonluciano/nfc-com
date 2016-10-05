package lasse.nfccom;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
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
public class TerminalConnectionHandler {

	private static final String NONE_TYPE = "None";

	private TerminalFactory factory;

	public TerminalConnectionHandler() {
		this(TerminalFactory.getDefault());
	}

	public TerminalConnectionHandler(TerminalFactory terminalFactory) {
		this.factory = terminalFactory;
	}

	/**
	 * This method is used to establish a connection with the Terminal reader
	 * @return the CardTerminal object used to read card tags
	 */
	public CardTerminal getTerminalConnection() throws TerminalReaderNotFoundException {

		if (NONE_TYPE.equals(factory.getType())) {
			throw new TerminalCardDriversNotAvailableException(
					"No drivers available. Check if de library libpcsclite.so.1 is loaded.");
		}

		System.out.println(format("Provider: {0}", factory));

		try {
			// Returns a list containing all available Terminal readers
			List<CardTerminal> terminals = factory.terminals().list();

			System.out.println(format("NFC Terminals available ({0}):", terminals.size()));

			for (int i = 0; i < terminals.size(); i++) {
				System.out.println(format("[{0}] {1}", i, terminals.get(i).getName()));
			}

			CardTerminal terminal = terminals.get(0);

			System.out.println(format("using the: {0}", terminal.getName()));

			return terminal;
		}
		catch (CardException e) {
			throw new TerminalReaderNotFoundException(
					"No card readers available.");
		}
	}
	
}
