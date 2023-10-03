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
		for(int row=1; row<=8; row++) {
			for(int column=1; column<=5; column++) {
				key = getKey(row) + "" +column;
				seats.put(key, key);
			}
		}
	}
	
	public void displaySeatLayout(){
		String key;
		String value = "";
		for(int row=1; row<=8; row++) {
			System.out.println("\t|\t");
			
			for(int column=1; column<=5; column++) {
				key = getKey(row) + "" +column;
				
				try {
					value = seats.get(key);
				}
				catch(Exception e) {
					System.out.println("Key " + key + "doesn't exists");
				}
		
				if(value != "")
					System.out.println("[" + value + "] ");
			}
			System.out.println();
		}
	}	
	
	private String getKey(int index) {
		String[] alpha = new String[]{"-1","A", "B", "C", "D", "E", "F", "G", "H"};
		return alpha[index];
	}
}
