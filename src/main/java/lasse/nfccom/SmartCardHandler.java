package lasse.nfccom;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import static lasse.nfccom.StringHelper.convertToString;

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
	 * This method is responsible to retrieve a UID regarding a Smart card 
	 * @param cardTerminal 	an instance of the terminal reader
	 * @return SmartCard 	the card read by the terminal reader
	 * @throws InterruptedException
	 * @throws SmartCardNullValueAssociatedException  
	 */
	public SmartCard getCardData(CardTerminal cardTerminal, byte[] command, int sector, String dataToWrite) throws InterruptedException, SmartCardNullValueAssociatedException{

		try {
			//Connects using any available protocol
			Card card = cardTerminal.connect("*");
			
			//A logical channel connection to a Smart Card.
			CardChannel channel = card.getBasicChannel();
			
			CommandAPDU commandToGetUID = new CommandAPDU(CommandUtils.getUIDCommand);
			
			ResponseAPDU responseUID = channel.transmit(commandToGetUID);
			
			cardUID = convertToString(responseUID.getData());
			
			if(command.equals(CommandUtils.readSectorCommand)){
				
				cardSectorData = readSector(bigIntToByteArray(sector),new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF}, bigIntToByteArray(16), channel);
			
			}else if(command.equals(CommandUtils.writeSectorCommand)){
				
				int toFillSector = 16 - dataToWrite.length();
				
				byte[] zeros = new byte[toFillSector];
				
				//used to create hexadecimal zeros to fill until data comming completes 16 bytes
				for(int i=0;i<toFillSector;i++){
					concatenateByteArrays(zeros, new byte[]{(byte) 0x00});
				}
				String dataToWriteHex = asciiToHex(dataToWrite);
				byte[] dataByteToWrite = hexStringToByteArray(dataToWriteHex);
				byte[] data = concatenateByteArrays(dataByteToWrite, zeros);
				
						
				writeSector(bigIntToByteArray(sector),new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF}, bigIntToByteArray(16),data, channel);
			}
			/*
			if(command.equals(CommandUtils.readSectorCommand)){
				cardSectorData = readSector(bigIntToByteArray(2),new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF}, bigIntToByteArray(16), channel);
			}else if(command.equals(CommandUtils.writeSectorCommand)){
				writeSector(bigIntToByteArray(2),new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF}, bigIntToByteArray(16),
						new byte[] { (byte) 0x6C, (byte) 0x61, (byte) 0x79, (byte) 0x6F, (byte) 0x6E, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}, channel);
			}
			*/
			
			/*
			//A command APDU following the structure defined in ISO/IEC 7816-4
			///CommandAPDU commandAPDU = new CommandAPDU(command);
			
			//A response APDU as defined in ISO/IEC 7816-4.
			///ResponseAPDU responseAPDU = channel.transmit(commandAPDU);
			
			///cardUID = convertToString(responseAPDU.getData());
			
			///System.out.println("RESPONSE CODE : "+ Integer.toHexString(responseAPDU.getSW1()) +" "+ Integer.toHexString(responseAPDU.getSW2()));
			*/
		}
		catch(CardException e) {
			throw new SmartCardNullValueAssociatedException(
					"Terminal Read wasn't able to get a response from Smart Card. Null value associated");
		}
		
		if(cardSectorData==null){
			return new SmartCard(cardUID);
		}else{
			return new SmartCard(cardUID, cardSectorData);
		}	
	}
	

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
	
	
	private byte[] concatenateByteArrays(byte[] a, byte[] b) {
	    byte[] result = new byte[a.length + b.length]; 
	    System.arraycopy(a, 0, result, 0, a.length); 
	    System.arraycopy(b, 0, result, a.length, b.length); 
	    return result;
	} 
	
	private byte[] bigIntToByteArray( final int i ) {
	    BigInteger bigInt = BigInteger.valueOf(i);      
	    return bigInt.toByteArray();
	}
	
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
