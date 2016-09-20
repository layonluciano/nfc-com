package lasse.nfccom;
/**
 *
 * @author layonluciano
 * This class is used to mapping Smart Cards
 *
 */
public class SmartCard {
	
	/**
	 * Card Unique ID
	 */
	String uid;
	
	/**
	 * Answer To Reset(ATR) information about the card
	 */
	String atr;
	
	public SmartCard(String uid, String atr){
		this.uid = uid;
		this.atr = atr;
	}
	
	public String getUid() {
		return uid;
	}
	
	public String getAtr(){
		return atr;
	}
	
}
