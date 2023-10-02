
public class MRSApp {
	private ArrayList<MovieSchedule> movies;
	private ArrayList<Reservation> reservations;
	private final String MOVIES = "MOVIES";
	private final String RESERVATIONS = "RESERVATIONS";
	
	public MRSApp() {
		movies = new ArrayList<MovieSchedule>();
		reservations = new ArrayList<Reservation>();
	} 
	public void displayMovies() {
		
	}
	public void readReservationFromCSV() {
		
	}

	public void readMovieFromCSV() {

	}

//	helper methods
	private LocalDateTime generateDateTime(String date, String time) {

			// sample value of time in the passed parameter: 12:30
			String temp = date + time + ":00";
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        LocalDateTime dateTime = LocalDateTime.parse(temp, formatter);
        
        return dateTime;
	}

	private ArrayList<String> readFromCSV(String file){
		String csvFile="";
		ArrayList<String> csvData = new ArrayList<>();

		if(file.equals(MOVIES))
			csvFile = "C:/Users/rodrigo.tomaub/Downloads/MovieSchedule.csv";
		else if(file.equals(RESERVATIONS))
			csvFile = "C:/Users/rodrigo.tomaub/Downloads/Reservations.csv";
			
        try{
        	BufferedReader br = new BufferedReader(new FileReader(csvFile));
        	
        	String line;
            while ((line = br.readLine()) != null) {
                csvData.add(line);
            }
            
        } catch (IOException e) {
            System.out.println("File not found.");;
        }
        
        return csvData;
	}
	
	public static void main(String args[]) {
		
	}
}
