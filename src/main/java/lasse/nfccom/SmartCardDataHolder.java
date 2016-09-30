package lasse.nfccom;

public class SmartCardDataHolder {
	
	SmartCard card;
	
	public SmartCardDataHolder(){
		this.card = new SmartCard();
	}

	public SmartCard getCard() {
		return card;
	}

	public void setCard(SmartCard card) {
		this.card = card;
	}
	
	
}
