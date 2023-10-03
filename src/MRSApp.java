import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
		System.out.println("Movie Schedule ID\tTime Start\tTitle");
		for (MovieSchedule item : movies) {
			LocalDateTime dateTime = item.getShowingDateTime();
			System.out.println("["+item.getMovieScheduleId()+"]\t\t\t"+dateTime.getHour()+":"+dateTime.getMinute()+"\t\t"+item.getMovie().getMovieName());
		}
	}

	public void readReservationCSV() {
		ArrayList<String> rsvData = readFromCSV("RESERVATIONS");
		
		String[] reservationParts;
		int ticketNum;
		LocalDateTime dateTime;
		byte cinema;
		String seatCodes;
		
		for (String item : rsvData) {
			reservationParts = item.substring(1, item.length() - 1).split("\",\"");

			ticketNum =  Integer.parseInt(reservationParts[0]);
			cinema = Byte.parseByte(reservationParts[2]);
			dateTime = generateDateTime(reservationParts[1], reservationParts[3]);
			seatCodes = reservationParts[4];
			
			for(MovieSchedule movieSched:movies) {
				if(dateTime.equals(movieSched.getShowingDateTime())) {
					if(cinema==movieSched.getMovie().getCinemaNum()) {
						Reservation res = new Reservation(ticketNum, seatCodes, movieSched);
						reservations.add(res);
					}
				}
			}
		}
	}

	public void readMovieCSV() {
		ArrayList<String> csvData = readFromCSV(MOVIES);
		String[] columns;

		// id counters
		short movieId = 0, movieScheduleId = 0, seatLayoutId = 0;

		// columns from CSV
		String title;
		boolean isPremiere;
		byte cinemaNum;
		float duration;
		LocalDateTime dateTime;

		Movie movieTemp;
		MovieSchedule MSTemp;

		for (String item: csvData) {
			columns = item.substring(1, item.length() - 1).split("\",\"");
			
			// mapping columns to Class attributes
			cinemaNum = Byte.parseByte(columns[1]);
			// columns[0] is date, columns[2] is time
			dateTime = generateDateTime(columns[0], columns[2]);
			isPremiere = Boolean.parseBoolean(columns[3]);
			title = columns[4];
			duration = Float.parseFloat(columns[5]);

			// object creation
			movieTemp = new Movie(++movieId, title, duration, cinemaNum);
			MSTemp = new MovieSchedule(++movieScheduleId, dateTime, movieTemp, isPremiere, ++seatLayoutId);

			movies.add(MSTemp);
		}
	}

//	helper methods
	private LocalDateTime generateDateTime(String date, String time) {

		// sample value of time in the passed parameter: 12:30
		String temp = date +" "+ time + ":00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(temp, formatter);

		return dateTime;
	}

	private ArrayList<String> readFromCSV(String file) {
		String csvFile = "";
		ArrayList<String> csvData = new ArrayList<>();

		if (file.equals(MOVIES))
			csvFile = "C:/Users/adrian.enriquez/Downloads/MovieSchedule.csv";
		else if (file.equals(RESERVATIONS))
			csvFile = "C:/Users/adrian.enriquez/Downloads/Reservations.csv";

		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFile));

			String line;
			while ((line = br.readLine()) != null) {
				csvData.add(line);
			}

		} catch (IOException e) {
			System.out.println("File not found.");
			;
		}

		return csvData;
	}

	public static void main(String args[]) {
		MRSApp app = new MRSApp();
		app.readMovieCSV();
		app.readReservationCSV();
		app.displayMovies();
	}
}
