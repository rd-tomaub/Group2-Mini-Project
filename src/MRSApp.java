import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MRSApp {
	private ArrayList<MovieSchedule> movies;
	private ArrayList<Reservation> reservations;
	private final String MOVIES = "MOVIES";
	private final String RESERVATIONS = "RESERVATIONS";
	static Scanner scan = new Scanner(System.in);

	public MRSApp() {
		movies = new ArrayList<MovieSchedule>();
		reservations = new ArrayList<Reservation>();
	}

	public void displayMovies(String date) {
		System.out.println("\nMovie Schedule ID\tTime Start\tTitle");
		for (MovieSchedule item : filterMoviesByDate(date)) {
			LocalDateTime dateTime = item.getShowingDateTime();
			System.out.println("[" + item.getMovieScheduleId() + "]\t\t\t" + dateTime.getHour() + ":"
					+ dateTime.getMinute() + "\t\t" + item.getMovie().getMovieName());
		}
		System.out.println();
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

			ticketNum = Integer.parseInt(reservationParts[0]);
			cinema = Byte.parseByte(reservationParts[2]);
			dateTime = generateDateTime(reservationParts[1], reservationParts[3]);
			seatCodes = reservationParts[4];

			for (MovieSchedule movieSched : movies) {
				if (dateTime.equals(movieSched.getShowingDateTime())) {
					if (cinema == movieSched.getMovie().getCinemaNum()) {
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

		for (String item : csvData) {
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
		String temp = date + " " + time + ":00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(temp, formatter);

		return dateTime;
	}

	private ArrayList<String> readFromCSV(String file) {
		String csvFile = "";
		ArrayList<String> csvData = new ArrayList<>();

		if (file.equals(MOVIES))
			csvFile = "C:/Users/Lenovo/Downloads/MovieSchedule.csv";
		else if (file.equals(RESERVATIONS))
			csvFile = "C:/Users/Lenovo/Downloads/Reservations.csv";

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

	private void reservationFunction(ArrayList<MovieSchedule> list) {
		System.out.print("Enter Movie Schedule ID: ");
		short movieId = scan.nextShort();
		MovieSchedule currentMovieSched = null;
		for(MovieSchedule item:list) {
			if(movieId==item.getMovieScheduleId()) {
				currentMovieSched = item;
				break;
			}
		}
		String movieName  = currentMovieSched.getMovie().getMovieName();
		int movieSchedId = currentMovieSched.getMovieScheduleId();
		String dateTime = generateAmPm(currentMovieSched.getShowingDateTime());
		String duration = String.valueOf(currentMovieSched.getMovie().getDuration());
		
		System.out.println("\nSeat Layout for\n"
				+ movieName+" @ "+dateTime
				+"\n["+movieSchedId+"] "+movieName+", "+dateTime+", "+duration+", "+availableSeats(currentMovieSched) );
		currentMovieSched.getSeats().displaySeatLayout();
		System.out.println();
	}

	private ArrayList<MovieSchedule> filterMoviesByDate(String date) {
		ArrayList<MovieSchedule> arr = new ArrayList<>();
		for (MovieSchedule item : movies) {
			LocalDateTime dateTime = item.getShowingDateTime();
			String temp = dateTime + "";
			String[] dateTimeStr = temp.split("T");
			if (date.equals(dateTimeStr[0])) {
				arr.add(item);
			}
		}
		return arr;
	}
	
	private String generateAmPm(LocalDateTime time) {
		int hours = time.getHour();
		String newHourFormat = "";
		if(hours>12) {
			newHourFormat = hours - 12 + ":" + time.getMinute() +" PM";
		}else {
			newHourFormat = hours + ":" + time.getMinute() + " AM";
		}
		return newHourFormat;
	}
	
	private String availableSeats(MovieSchedule movieSched) {
		String[] reservedSeat = null;
		String seatsAvailable = "";
		for(Reservation item:reservations) {
			String seats = item.getSeatCodes();
			if(item.getMovie()==movieSched) {
				reservedSeat = seats.split(",");
			}
		}
		if(reservedSeat==null) {
			seatsAvailable = "40";
		}else {
			if(reservedSeat.length==40) {
				seatsAvailable = "Full";
			}else {
				seatsAvailable = String.valueOf(reservedSeat.length);
			}
		}
		return seatsAvailable;
	}

	public static void main(String args[]) {
		MRSApp app = new MRSApp();
		app.readMovieCSV();
		app.readReservationCSV();

		boolean runApp = true;

		do {
			System.out.print("Main Menu\n[1] Reserve Seat\n[2] Cancel Reservation\n\nPick Option: ");
			String response = scan.next();
			String expectedFormat  = "yyyy-MM-dd";
			SimpleDateFormat expectedDateFormat = new SimpleDateFormat(expectedFormat);
//			LocalDateTime currentDate = LocalDateTime.now();
			LocalDateTime currentDate = app.generateDateTime("2020-12-01", "00:00");   // Assume current date is 2020-12-01
			switch (response) {
			case "1":
				System.out.print("\nEnter Date: ");
				try {
					String date = scan.next();
		            // Parse the date string to a Date object
		            Date parsedDate = expectedDateFormat.parse(date);
		            // Format the parsed date back to a string to check if it matches the original string
		            String formattedDate = expectedDateFormat.format(parsedDate);
		            // Compare the formatted date with the original string
		            LocalDateTime inputDate = app.generateDateTime(date, "00:00");
		            ArrayList<MovieSchedule> movieListByDate = app.filterMoviesByDate(date);
		            if (date.equals(formattedDate)) {
						if (movieListByDate.size()!=0) {
							if(currentDate.isAfter(inputDate)) {
								System.out.println("Cannot reserve seats on passed date");
							}else {
								app.displayMovies(date);
								app.reservationFunction(movieListByDate);
							}
						}else {
							System.out.println("\nNo Movies Available on this day\n");
						}
		            } else {
		                System.out.println("Invalid Date Format");
		            }
		        } catch (ParseException e) {
		            System.out.println("Invalid Date Format");
		        }
				break;
			case "2":
				System.out.println("Run Cancel Reservation Method\n");
				break;
			default:
				System.out.println("Please input valid OPTION\n");
				break;
			}
		} while (runApp);

		scan.close();
	}
}
