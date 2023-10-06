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

	public boolean isValidReservation(String seatCodes, byte numOfSenior) {
		// assumes that more than one seat code is received.
		String[] seatCode = seatCodes.split(",");
		byte validCounter;
		byte length = (byte) seatCodes.length();

		for (validCounter = 0; validCounter < length;) {
			if (!seats.get(seatCode[validCounter]).equals("**"))
				validCounter++;
			else
				return false;
		}

		if (validCounter == length) {
			if (numOfSenior <= validCounter) {
				return true;
			}
		}

		return false;
	}

	public boolean isValidCancellation(String seatCodes) {
		// assumes that more than one seat code is received.
		String[] seatCode = seatCodes.split(",");
		byte validCounter;
		byte length = (byte) seatCodes.length();

		for (validCounter = 0; validCounter < length;) {
			if (seats.get(seatCode[validCounter]).equals("**"))
				validCounter++;
			else
				return false;
		}
		if (validCounter == length)
			return true;

		return false;
	}

	public void reserveSeat(String seatCode) {
		seats.put(seatCode, "**");
	}

	public void cancelSeat(String seatCode) {
		seats.put(seatCode, seatCode);
	}

}
