import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
// import com.opencsv.CSVWriter;

public class MRSApp {
	private ArrayList<MovieSchedule> movies;
	private ArrayList<Reservation> reservations;
	
	// static private ArrayList<MovieSchedule> movieListByDate;
	static private MovieSchedule currentMovieSelected;
	
	private final String MOVIES = "MOVIES";
	private final String RESERVATIONS = "RESERVATIONS";
	static Scanner scan = new Scanner(System.in);

	public MRSApp() {
		movies = new ArrayList<MovieSchedule>();
		reservations = new ArrayList<Reservation>();
	}

	public boolean displayMovies() {
		ArrayList<MovieSchedule> movieListByDate = new ArrayList<>();
		// Initialize date format
		String expectedFormat = "yyyy-MM-dd";
		SimpleDateFormat expectedDateFormat = new SimpleDateFormat(expectedFormat);
		
		// LocalDateTime currentDate = getCurrentDate();
		
		LocalDateTime currentDate = generateDateTime("2020-12-01", "00:00"); 
		boolean isDateFormatValid = true;
		
		while(isDateFormatValid){
			System.out.println("\nEnter [ESC] to cancel transaction. \nEnter Date [yyyy-mm-dd]: ");
				// Store input date to date variable
				String date = scan.next();
				// Back to main menu if user Enter ESC
				if (date.equalsIgnoreCase("esc")) 
					return false;
			try {
				Date parsedDate = expectedDateFormat.parse(date);
				String formattedDate = expectedDateFormat.format(parsedDate);
				// Convert inputed date to LocalDateTime object
				LocalDateTime inputDate = generateDateTime(date, "00:00");
				
				// Check if user input matched format date
				if (date.equals(formattedDate)) {
					movieListByDate = filterMoviesByDate(date);

					if (movieListByDate.size() == 0) 
						System.out.println("\nNo Movies Available on this day\n");

					else if (inputDate.isAfter(currentDate)) {
						// Display movie schedules
						System.out.println("\nMovie Schedule ID\tTime Start\tTitle");
						for (MovieSchedule item : movieListByDate) {
							System.out.println("[" + item.getMovieScheduleId() + "]\t\t\t" + 
								generateAmPm(item.getShowingDateTime()) + "\t" + 
								item.getMovie().getMovieName());
						}
						isDateFormatValid = false;
					} else 
						System.out.println("\nCannot reserve seats on past dates\n");
				}
				
			} catch (Exception e) {
				System.out.println("Invalid Date Format");
			} 
		}
		System.out.println();
		return true;
	}

	private void displaySeatLayout(MovieSchedule movieSched) {
		
		// MovieSchedule selectedMovieSched = movies.get(movieSchedId-1);

		String movieName = movieSched.getMovie().getMovieName();
		String dateTime = generateAmPm(movieSched.getShowingDateTime());
		String duration = String.valueOf(movieSched.getMovie().getDuration());

		System.out.println("\nSeat Layout for\n" + movieName + " @ " + dateTime + "\n[" + movieSched.getMovieScheduleId()+1 + "] "
				+ movieName + ", " + dateTime + ", " + duration);

		movieSched.getSeats().displaySeatLayout();
	}

	public void addReservationCSV(MovieSchedule movieSched, String seatCodes, float price){
		int lastObj = reservations.size()-1;
		int reservationNum = reservations.get(lastObj).getReservationNum() + 1;

		Reservation reservationObj = new Reservation(reservationNum, seatCodes, movieSched, price);
		reservations.add(reservationObj);
		String dateTime = movieSched.getShowingDateTime() + "";
		String[] date = dateTime.split("T");
		String csvFilePath = "C:/Users/Lenovo/Downloads/Reservations.csv";

		// write to CSV
		try {
            FileWriter fileWriter = new FileWriter(csvFilePath, true);
            // Convert the object fields to an array of strings
            String data = reservationNum+"," + 
								date[0] +","+
								movieSched.getMovie().getCinemaNum()+","+
								date[1]+","+
								"\"" + seatCodes + "\"" + "," +
								price+"";
			 
			 // Append the line to the CSV file
			fileWriter.append(System.lineSeparator()); // Use system-specific line separator
			fileWriter.append(data);
			
			fileWriter.close();
	    } catch (Exception e) {
            
        }
		
		// updating the seat layout
		String[] seatCode = seatCodes.split(",");
		int length = seatCode.length;
		
		for(int i=0; i<length; i++)
			movieSched.getSeats().reserveSeat(seatCode[i]);

		movieSched.getSeats().displaySeatLayout();
		System.out.println("Reservation ID: "+ reservationNum);
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
			float price = Float.parseFloat(reservationParts[5]);

			for (MovieSchedule movieSched : movies) {
				if (dateTime.equals(movieSched.getShowingDateTime())) {
					if (cinema == movieSched.getMovie().getCinemaNum()) {
						Reservation res = new Reservation(ticketNum, seatCodes, movieSched, price);
						reservations.add(res);
					}
				}
			}
		}
		// initializing reserved seats.
		int length = reservations.size();
		for(int i=0; i<length; i++){
			String[] seatCode = reservations.get(i).getSeatCodes().split(",");
			for(String seat : seatCode){
				reservations.get(i).getMovie().getSeats().reserveSeat(seat);
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
	private LocalDateTime getCurrentDate(){
		LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = currentDate.format(formatter);

		return LocalDateTime.parse(formattedDate, formatter);
	}

	private Date getDatePart(String date){
		String expectedFormat = "yyyy-MM-dd";
		SimpleDateFormat expectedDateFormat = new SimpleDateFormat(expectedFormat);
		
		Date parsedDate = null;
		try{
			expectedDateFormat.parse(date);
		}catch(Exception e){
			
		}

		return parsedDate;
	}

	private LocalDateTime generateDateTime(String date, String time) {
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
			br.close();

		} catch (IOException e) {
			System.out.println("File not found.");
		}

		return csvData;
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
		int min = time.getMinute();

		String minute = "" + min;
		String newHourFormat = "";
		if(min < 10)
			minute = "0" + min;

		if (hours >= 12) {
			if(hours != 12)	hours-=12;
			newHourFormat = hours + ":" + minute + " PM";
		} 
		else	newHourFormat = hours + ":" + minute + " AM";
		
		if(hours < 10)
			newHourFormat = "0" + newHourFormat;

		return newHourFormat;
	}

	private String availableSeats(MovieSchedule movieSched) {
		String[] reservedSeat = new String[] { "A1", "A2" };
		String seatsAvailable = "";
		for (Reservation item : reservations) {
			String seats = item.getSeatCodes();
			if (item.getMovie() == movieSched) {
				reservedSeat = seats.split(",");
			}
		}
		if (reservedSeat == null) {
			seatsAvailable = "40";
		} else {
			if (reservedSeat.length == 40) {
				seatsAvailable = "Full";
			} else {
				seatsAvailable = String.valueOf(40 - reservedSeat.length);
			}
		}
		for (String items : reservedSeat) {
			System.out.println(items);
		}
		return seatsAvailable;
	}

	private float calculateTotalPrice(byte numOfWatchers, byte numOfSenior, boolean isPremiere){
		float price = (isPremiere? 500:350);
		float totalPrice = (price * numOfWatchers) - (float)(price*.20*numOfSenior);
		
		return totalPrice;
	}
	public static void main(String args[]) {
		MRSApp app = new MRSApp();
		app.readMovieCSV();
		app.readReservationCSV();
		MovieSchedule selectedMovieSched = null;

		boolean runApp = true;

		do {
			System.out.print("Main Menu\n[1] Reserve Seat\n[2] Cancel Reservation\n\nPick Option: ");
			String response = scan.next();
			switch (response) {
			case "1":
				if (!app.displayMovies()) 
					break;
				
				// Initialize movieId
				short parsedMovieId=-1;
				boolean invalidInput = true, isRunSeatCodes = true;
				while (invalidInput) {
					System.out.print("Enter [ESC] to cancel transaction. \nEnter Movie Schedule ID: ");
					String movieId = scan.next();
					try {
						parsedMovieId = Short.parseShort(movieId);
						selectedMovieSched = app.movies.get(parsedMovieId-1);
						app.displaySeatLayout(selectedMovieSched);
						invalidInput = false;
					} catch (Exception e) {
						System.out.println("\nInvalid Movie Number\n");
					}
				}
				invalidInput = true;
				
				while (invalidInput) {
					System.out.println("\nPlease input seats to be reserved for this transaction: ");
					String seatCodesInput = scan.next();
					String[] viewerCount = seatCodesInput.split(",");
					System.out.println("\nHow many senior citizens? ");
					byte numCitizen =-1;
					try {
						numCitizen = scan.nextByte();
					}catch(Exception e) {
						System.out.println("\nInvalid seat codes.");
					}
					
					app.addReservationCSV(selectedMovieSched, 
										seatCodesInput, 
										app.calculateTotalPrice((byte)viewerCount.length, numCitizen, selectedMovieSched.isPremiereShow()));
					invalidInput = false;
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
