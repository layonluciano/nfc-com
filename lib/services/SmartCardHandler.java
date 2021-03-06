package services;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import exceptions.SmartCardNullValueAssociatedException;
import models.SmartCard;
import utils.CommandUtils;

import static helpers.StringHelper.convertToString;

import java.math.BigInteger;

/**
 * This class is used to manage commands to be sent to Smart Cards
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */
public class SmartCardHandler {
	
	private String cardUID;
	
	private String cardSectorData;
	
	/**
	 * Class constructor
	 */
	public SmartCardHandler() {
		this.cardUID = null;
		this.cardSectorData = null;
	}
	
	
	/**
	 * Method used to get card UID without authentication
	 * 
	 * @param cardTerminal		An instance of the terminal reader
	 * @return 					SmartCard object
	 * @throws SmartCardNullValueAssociatedException
	 */
	public SmartCard getCardUID(CardTerminal cardTerminal) throws SmartCardNullValueAssociatedException{
		
		try {
			Card card = cardTerminal.connect("*");
			
			CardChannel channel = card.getBasicChannel();
			
			CommandAPDU commandToGetUID = new CommandAPDU(CommandUtils.getUIDCommand);
			
			ResponseAPDU responseUID = channel.transmit(commandToGetUID);
			
			cardUID = convertToString(responseUID.getData());
		}
		catch(CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
		return new SmartCard(cardUID);
		
	}
	
	/**
	 * Method to retrieve card data from a spacific sector with proper authentication
	 * 
	 * @param cardTerminal			An instance of the terminal reader
	 * @param sector				Sector to be authenticated
	 * @param key					key used in sector authentication
	 * @return						SmartCard object
	 * @throws InterruptedException
	 * @throws SmartCardNullValueAssociatedException
	 */
	public SmartCard getCardData(CardTerminal cardTerminal, int sector, byte[] key) throws InterruptedException, SmartCardNullValueAssociatedException{

		try {
			Card card = cardTerminal.connect("*");
			
			CardChannel channel = card.getBasicChannel();
			
			CommandAPDU commandToGetUID = new CommandAPDU(CommandUtils.getUIDCommand);
			
			ResponseAPDU responseUID = channel.transmit(commandToGetUID);
			
			cardUID = convertToString(responseUID.getData());
			
			cardSectorData = readSector(intToByteArray(sector), key, intToByteArray(16), channel);
			
		}
		catch(CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
		
		return new SmartCard(cardUID, cardSectorData);
			
	}
	
	
	/**
	 * This method is responsible to retrieve a UID regarding a Smart card 
	 * @param cardTerminal 	An instance of the terminal reader
	 * @return SmartCard 	the card read by the terminal reader
	 * @throws InterruptedException
	 * @throws SmartCardNullValueAssociatedException  
	 */
	public SmartCard setCardData(CardTerminal cardTerminal, int sector, byte[] key, String dataToWrite) throws InterruptedException, SmartCardNullValueAssociatedException{

		try {
			Card card = cardTerminal.connect("*");
			
			CardChannel channel = card.getBasicChannel();
			
			CommandAPDU commandToGetUID = new CommandAPDU(CommandUtils.getUIDCommand);
			
			ResponseAPDU responseUID = channel.transmit(commandToGetUID);
			
			cardUID = convertToString(responseUID.getData());
				
			int toFillSector = 16 - dataToWrite.length();
				
			byte[] zeros = new byte[toFillSector];
				
			//used to create hexadecimal zeros to fill until data comming completes 16 bytes
			for(int i=0;i<toFillSector;i++){
				concatenateByteArrays(zeros, new byte[]{(byte) 0x00});
			}
			
			String dataToWriteHex = asciiToHex(dataToWrite);
			byte[] dataByteToWrite = hexStringToByteArray(dataToWriteHex);
			byte[] data = concatenateByteArrays(dataByteToWrite, zeros);
							
			writeSector(intToByteArray(sector), key, intToByteArray(16),data, channel);
			
		}
		catch(CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
	
		return new SmartCard(cardUID);
			
	}
	
	/**
	 * Method used to login into a card sector
	 * 
	 * @param sector		Sector to login
	 * @param key			key used to login into sector
	 * @param channel		Channel used in communication wit Card Reader
	 * @throws SmartCardNullValueAssociatedException
	 */
	private void login(byte[] sector,byte[] key,CardChannel channel) throws SmartCardNullValueAssociatedException{
		
		byte[] keyToUse = concatenateByteArrays(CommandUtils.loadAuthenticationKeytoReader, key); 
		CommandAPDU loadKeyIntoReader = new CommandAPDU(keyToUse);
		try {
			ResponseAPDU loadKeyIntoReaderResponse = channel.transmit(loadKeyIntoReader);
			System.out.println("RESPONSE CODE LOAD: "+ Integer.toHexString(loadKeyIntoReaderResponse.getSW1()) +" "+ Integer.toHexString(loadKeyIntoReaderResponse.getSW2()));
		} catch (CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
		
		byte[] authToHeader = concatenateByteArrays(CommandUtils.authenticationHeader, sector); 
		byte[] authCommand = concatenateByteArrays(authToHeader, CommandUtils.authenticationTail); 
		CommandAPDU authenticationOnCard = new CommandAPDU(authCommand);
		try {
			ResponseAPDU authenticationOnCardResponse = channel.transmit(authenticationOnCard);
			System.out.println("RESPONSE CODE AUTH: "+ Integer.toHexString(authenticationOnCardResponse.getSW1()) +" "+ Integer.toHexString(authenticationOnCardResponse.getSW2()));
		} catch (CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
	}
	
	/**
	 * Method used to read data from a specific smart card sector 
	 * 
	 * @param sector		Sector to read
	 * @param key			Key used to login into sector
	 * @param bytesToRead	Number of bytes to be read. Up to 16 bytes
	 * @param channel		Channel used in communication wit Card Reader
	 * @return				String containing data from that sector.
	 * @throws SmartCardNullValueAssociatedException
	 */
	private String readSector(byte[] sector ,byte[] key, byte[] bytesToRead, CardChannel channel) throws SmartCardNullValueAssociatedException{
		
		login(sector,key,channel);
		String sectorData = null;
		
		byte[] readCommand = concatenateByteArrays(CommandUtils.readBinaryBlock, sector);
		readCommand = concatenateByteArrays(readCommand, bytesToRead); 
		
		CommandAPDU read = new CommandAPDU(readCommand);
		
		try {
			ResponseAPDU readResponse = channel.transmit(read);
			sectorData = convertToString(readResponse.getData());
			System.out.println("RESPONSE CODE READ: "+ Integer.toHexString(readResponse.getSW1()) +" "+ Integer.toHexString(readResponse.getSW2()));
		} catch (CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
		
		return sectorData;
	}
	
	/**
	 * This method is used to write data in a specific sector of a smart card
	 * 
	 * @param sector			Sector to write
	 * @param key				Key used to login into sector
	 * @param bytesToUpdate		Number of bytes to update. Up to 16 bytes
	 * @param data				Data to be written
	 * @param channel			Channel used in communication wit Card Reader
	 * @throws SmartCardNullValueAssociatedException
	 */
	private void writeSector(byte[] sector ,byte[] key, byte[] bytesToUpdate, byte[] data, CardChannel channel) throws SmartCardNullValueAssociatedException{
		
		login(sector,key,channel);
		
		byte[] writeCommand = concatenateByteArrays(CommandUtils.writeBinaryBlock, sector);
		writeCommand = concatenateByteArrays(writeCommand, bytesToUpdate);
		writeCommand = concatenateByteArrays(writeCommand, data);
		
		CommandAPDU write = new CommandAPDU(writeCommand);
		
		try {
			ResponseAPDU readResponse = channel.transmit(write);
			System.out.println("RESPONSE CODE WRITE: "+ Integer.toHexString(readResponse.getSW1()) +" "+ Integer.toHexString(readResponse.getSW2()));
		} catch (CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
	}
	
	/**
	 * Method used to concatenate two byte arrays into one.
	 * 
	 * @param a		First byte array 
	 * @param b		Second byte array 
	 * @return		Concatenated byte array 
	 */
	private byte[] concatenateByteArrays(byte[] a, byte[] b) {
	    byte[] result = new byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
	    System.arraycopy(b, 0, result, a.length, b.length); 
	    return result;
	} 
	
	/**
	 * Method to turn int numbers into a byte array
	 * 
	 * @param i		Entry number 
	 * @return		Byte array 
	 */
	private byte[] intToByteArray( final int i ) {
	    BigInteger bigInt = BigInteger.valueOf(i);      
	    return bigInt.toByteArray();
	}
	
	/**
	 * Method used to convert ASCII into Hexadecimal representation
	 * 	
	 * @param asciiValue	Input ASCII string
	 * @return				String as a Hexadecimal representation
	 */
	private static String asciiToHex(String asciiValue)
	{
	    char[] chars = asciiValue.toCharArray();
	    StringBuffer hex = new StringBuffer();
	    for (int i = 0; i < chars.length; i++)
	    {
	        hex.append(Integer.toHexString((int) chars[i]));
	    }
	    return hex.toString();
	}
	
	/**
	 * Method used to convert Hexadecimal strings representations 
	 * 
	 * @param s				Input Hexadecimal string representation
	 * @return				Byte array with Hexadecimal notation
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

}
