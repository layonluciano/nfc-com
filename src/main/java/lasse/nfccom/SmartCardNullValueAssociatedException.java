package lasse.nfccom;

/**
 * Exception class to handle Smart Card null values	
 * 
 * @author Layon Luciano
 * 
 * Created on 04/10/16.
 */
public class SmartCardNullValueAssociatedException extends Exception {
	
	private static final long serialVersionUID = 6815553845517616749L;

	public SmartCardNullValueAssociatedException(String message){
		super(message);	
	}
}
