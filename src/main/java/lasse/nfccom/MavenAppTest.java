package lasse.nfccom;

import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class MavenAppTest {

	public static void main(String[] args) throws InterruptedException {
		TerminalFactory factory = TerminalFactory.getDefault();
		String cardUID;
		String evento = args[0];
		System.out.println("Evento :" + evento);
		try {
			List<CardTerminal> terminalsList = factory.terminals().list();
			if (terminalsList.size() > 0) {
				System.out.println("Terminal reader now detected by system!");
				CardTerminal cardTerminal = terminalsList.get(0);
				while (true) {
					cardTerminal.waitForCardPresent(0);
					System.out.println("Reading Smart Tag...");
					cardUID = handleCardUID(cardTerminal);
					cardTerminal.waitForCardAbsent(0);
					System.out.println("Removed Smart Tag...");
					sendDataToRESTService(evento, cardUID);
				}
			} else {
				System.out.println("Terminal reader NOT detected by system!!");
			}

		} catch (CardException e) {
			System.out.println("Exception occured during terminal reader communication....");
			e.printStackTrace();
		}

	}

	private static String handleCardUID(CardTerminal cardTerminal) throws InterruptedException {
		Card card;
		try {
			card = cardTerminal.connect("*");
			ATR atr = card.getATR();
			CardChannel channel = card.getBasicChannel();
			CommandAPDU command = new CommandAPDU(getuid);
			ResponseAPDU response = channel.transmit(command);
			byte[] iudBytes = response.getData();
			byte[] atrBytes = atr.getBytes();
			final String cardATR = readable(atrBytes);
			final String cardUID = readable(iudBytes);
			System.out.println("------------------------------");
			System.out.println("ATR: " + cardATR);
			System.out.println("------------------------------");
			System.out.println("UID: " + cardUID);
			System.out.println("------------------------------");

			return cardUID;
		} catch (CardException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String readable(byte[] src) {
		String answer = "";
		for (byte b : src) {
			answer = answer + String.format("%02X", b);
			answer = answer + " ";
		}
		return answer;
	}

	private static void sendDataToRESTService(String evento, String cardUID) {

		try {
			
			Client client = Client.create();

			WebResource webResource = client.resource("http://localhost:5000/gravar/");

			//String input = "{\"evento\":\"Metallica\",\"card_uid\":\"EE00EE00\"}";
			String input = "{\"evento\":\""+evento.replaceAll("\\s+","")+"\",\"card_uid\":\""+cardUID.replaceAll("\\s+","")+"\"}";
			
			ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);

			if (response.getStatus() == 200) {
				System.out.println("DEU CERTO ENVIAR!");
			}else{
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			

			// System.out.println("Output from Server .... \n");
			// String output = response.getEntity(String.class);
			// System.out.println(output);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	static byte[] getuid = new byte[] { (byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

}
