package models;

/**
 * This class is used to mapping Smart Cards
 *
 * @author Layon Luciano
 *
 * Created on 03/10/16.
 */
public class SmartCard {
	
	private String uid;
	
	private String sectorData;
	
	public SmartCard(String uid) {
		this.uid = uid;
	}
	
	public SmartCard(String uid, String sectorData){
		this.uid = uid;
		this.sectorData = sectorData;
	}
	
	public String getUID(){
		return uid;
	}
	
	public String getSectorData() {
		return sectorData;
	}

	
	
}
