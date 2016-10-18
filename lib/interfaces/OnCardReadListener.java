package interfaces;

import models.SmartCard;

/**
 * This interface define a callback response
 * 
 * @author Layon Luciano
 * 
 * Created on 03/10/16.
 */
public interface OnCardReadListener {
	
	/**
	 * Event related to Smart Card reading detection 
	 * @param smartCard SmartCard instance
	 */
	void onCardRead(SmartCard smartCard);
}
