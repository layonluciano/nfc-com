package lasse.nfccom;
/**
 *
 * @author layonluciano
 * This class is used to mapping Smart Cards
 *
 * Created on 03/10/16.
 */
public class SmartCard {
	
	/**
	 * Generic card data
	 */
	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public SmartCard(String data) {
		this.data = data;
		
	}
	
}
