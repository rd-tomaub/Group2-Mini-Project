import java.util.HashMap;
import java.util.Map;

public class SeatLayout {
	private short seatLayoutId;
	private Map<String, String> seats;

	public SeatLayout(short seatLayoutId) {
		this.seatLayoutId = seatLayoutId;

		seats = new HashMap<String, String>();
		String key;

		// initially, seats are all vacant.
		for (int row = 1; row <= 8; row++) {
			for (int column = 1; column <= 5; column++) {
				key = getKey(row) + "" + column;
				seats.put(key, key);
			}
		}
	}	

	public void displaySeatLayout() {
		String key;
		String value = "";
		for (int row = 1; row <= 8; row++) {
			System.out.print("   |   ");

			for (int column = 1; column <= 5; column++) {
				key = getKey(row) + "" + column;

				try {
					value = seats.get(key);
				} catch (Exception e) {
					System.out.print("Key " + key + "doesn't exists");
				}

				if (value != "")
					System.out.print("[" + value + "] ");
			}
			System.out.println();
		}
	}

	private String getKey(int index) {
		String[] alpha = new String[] { "-1", "A", "B", "C", "D", "E", "F", "G", "H" };
		return alpha[index];
	}

	public boolean validSeatFormat(String seatCode){
		if(seatCode.length() != 2)
			return false;
		if(!(seatCode.charAt(0) >= 'A' && seatCode.charAt(0) <= 'H'))
			return false;
		if(!(seatCode.charAt(1) >= '1' && seatCode.charAt(1) <= '5'))
			return false;
	
		return true;
	}

	public boolean isValidReservation(String seatCodes, byte numOfSenior) {
		// assumes that more than one seat code is received.

		String[] temp = seatCodes.split(",");
		int i=0, j;
		byte validCounter=0;

		for(String seatCode : temp){
			seatCode = seatCode.trim();
			if(validSeatFormat(seatCode) && !(seats.get(seatCode).equals("**")))
				validCounter++;
			else
				return false;
			
			for(j=i+1; j<temp.length; j++){
				if(seatCode.equals(temp[j].trim()))
					return false;
			}
			i++;
		}

		if (validCounter == (byte)temp.length) {
			if (numOfSenior <= validCounter && numOfSenior >= 0) 
				return true;
		}

		return false;
	}

	public boolean isValidCancellation(String seatCodes) {
		// assumes that more than one seat code is received.
		String[] temp = seatCodes.split(",");
		byte validCounter = 0;
		
		for(String seatCode: temp) {
			seatCode = seatCode.trim();
			if (validSeatFormat(seatCode) && seats.get(seatCode).equals("**"))
				validCounter++;
			else 
				break;
		}
		if(validCounter == (byte) temp.length)
			return true;

		return false;
	}

	public void reserveSeat(String seatCode) {
		seats.put(seatCode.trim(), "**");
	}

	public void cancelSeat(String seatCode) {
		seats.put(seatCode.trim(), seatCode);
	}

}
