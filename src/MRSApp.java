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

public class MRSApp {
	private ArrayList<MovieSchedule> movieSchedules;
	private ArrayList<Reservation> reservations;
	
	private final String MOVIES = "MOVIES";
	private final String RESERVATIONS = "RESERVATIONS";
	private final String MOVIESCHED_CSV_PATH ="C:/Users/Lenovo/Downloads/MovieSchedule.csv";
	private final String RESERVATION_CSV_PATH ="C:/Users/Lenovo/Downloads/Reservations.csv";
	
	static Scanner scan = new Scanner(System.in);

	public MRSApp() {
		movieSchedules = new ArrayList<MovieSchedule>();
		reservations = new ArrayList<Reservation>();
	}

	public boolean displayMovieSchedules() {
		ArrayList<MovieSchedule> movieListByDate = new ArrayList<>();
		// Initialize date format
		String expectedFormat = "yyyy-MM-dd";
		SimpleDateFormat expectedDateFormat = new SimpleDateFormat(expectedFormat);

		// LocalDateTime currentDate = getCurrentDate();

		LocalDateTime currentDate = generateDateTime("2020-12-01", "00:00");
		boolean isDateFormatValid = true;

		while (isDateFormatValid) {
			System.out.print("\nEnter [ESC] to cancel transaction. \nEnter Date [yyyy-mm-dd]: ");
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
						System.out.println("\nNo Movies Available on this day");

					else if (inputDate.isAfter(currentDate)) {
						// Display movie schedules
						System.out.println("\nMovie Schedule ID\tTime Start\tTitle");
						for (MovieSchedule item : movieListByDate) {
							System.out.println("[" + item.getMovieScheduleId() + "]\t\t\t"
									+ generateAmPm(item.getShowingDateTime()) + "\t" + item.getMovie().getMovieName());
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

		System.out.println("\nSeat Layout for\n" + movieName + " @ " + dateTime + "\n["
				+ movieSched.getMovieScheduleId() + "] " + movieName + ", " + dateTime + ", " + duration);

		movieSched.getSeats().displaySeatLayout();
	}

	public void addReservationCSV(MovieSchedule movieSched, String seatCodes, float price) {
		int lastObj = reservations.size() - 1;
		int reservationNum = reservations.get(lastObj).getReservationNum() + 1;

		Reservation reservationObj = new Reservation(reservationNum, seatCodes, movieSched, price);
		reservations.add(reservationObj);
		// String dateTime = movieSched.getShowingDateTime() + "";
		// String[] date = dateTime.split("T");
		// String csvFilePath = RESERVATION_CSV_PATH;

		// write to CSV
		try {
			FileWriter fileWriter = new FileWriter(RESERVATION_CSV_PATH, true);
			// Convert the object fields to an array of strings
			// String data = reservationNum + "," + date[0] + "," + movieSched.getMovie().getCinemaNum() + "," + date[1]
					// + "," + "\"" + seatCodes + "\"" + "," + price + "";

			// Append the line to the CSV file
			fileWriter.append(System.lineSeparator()); // Use system-specific line separator
			fileWriter.append(reservationObj.toString());

			fileWriter.close();
		} catch (Exception e) {

		}

		// updating the seat layout
		String[] seatCode = seatCodes.split(",");

		for(String s : seatCode)
			movieSched.getSeats().reserveSeat(s);

		movieSched.getSeats().displaySeatLayout();
		System.out.println("\nReservation ID: " + reservationNum);
	}

	public void readReservationCSV() {
		ArrayList<String> rsvData = readFromCSV(RESERVATIONS);

		String[] columns;
		LocalDateTime dateTime=null;
		String seatCodes="";
		int ticketNum=-1;
		byte cinema=-1;
		float price = -1f;
		for (String item : rsvData) {
			columns = item.substring(1, item.length() - 1).split("\",\"");
			try{			
				ticketNum = Integer.parseInt(columns[0]);
				cinema = Byte.parseByte(columns[2]);
				price = Float.parseFloat(columns[5]);
								// columns[1] is date, columns[3] is time
				dateTime = generateDateTime(columns[1], columns[3]);
				seatCodes = columns[4];
			
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			for (MovieSchedule movieSched : movieSchedules) {
				if (dateTime.equals(movieSched.getShowingDateTime())) {
					if (cinema == movieSched.getMovie().getCinemaNum()) {
						Reservation res = new Reservation(ticketNum, seatCodes, movieSched, price);
						reservations.add(res);
						break;
					}
				}
			}
		}
		// initializing reserved seats.
		int length = reservations.size();
		for (int i = 0; i < length; i++) {
			String[] seatCode = reservations.get(i).getSeatCodes().split(",");
			for (String seat : seatCode) {
				reservations.get(i).getMovie().getSeats().reserveSeat(seat);
			}
		}
	}
	
	public void removeReservationCSV(Reservation reservationObj) {
		String[] seatCodes = reservationObj.getSeatCodes().split(",");

		for (String item : seatCodes) {
			reservationObj.getMovie().getSeats().cancelSeat(item);
		}
		System.out.println("Ticket " + reservationObj.getReservationNum() + " is cancelled.");
		reservations.remove(reservationObj);

		// write to CSV
		try {
			FileWriter fileWriter = new FileWriter(RESERVATION_CSV_PATH, false);
			// Convert the object fields to an array of strings
			for (Reservation reservation : reservations) {
				// Append the line to the CSV file
				fileWriter.write(reservation.toString());
				fileWriter.write(System.lineSeparator()); // Use system-specific line separator
			}
			
			fileWriter.close();
		} catch (Exception e) {

		}

		
	}

	public void readMovieScheduleCSV() {
		ArrayList<String> csvData = readFromCSV(MOVIES);
		String[] columns;

		// id counters
		short movieId = 0, movieScheduleId = 0, seatLayoutId = 0;

		// columns from CSV
		String title;
		boolean isPremiere=false;
		byte cinemaNum=-1;
		float duration=-1f;
		LocalDateTime dateTime;

		Movie movieTemp;
		MovieSchedule MSTemp;

		for (String item : csvData) {
			columns = item.substring(1, item.length() - 1).split("\",\"");
			title = columns[4];
			dateTime = generateDateTime(columns[0], columns[2]);
				
			try{
				// mapping columns to Class attributes
				cinemaNum = Byte.parseByte(columns[1]);
				// columns[0] is date, columns[2] is time
				isPremiere = Boolean.parseBoolean(columns[3]);
				duration = Float.parseFloat(columns[5]);
		
				// if error happens, the loop would just iterate.
				// object creation
				movieTemp = new Movie(++movieId, title, duration, cinemaNum);
				MSTemp = new MovieSchedule(++movieScheduleId, dateTime, movieTemp, isPremiere, ++seatLayoutId);

				movieSchedules.add(MSTemp);
			}catch(Exception e){

			}
		}
	}

//	helper methods
	private LocalDateTime getCurrentDate() {
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDate = currentDate.format(formatter);

		return LocalDateTime.parse(formattedDate, formatter);
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
			csvFile = MOVIESCHED_CSV_PATH;
		else if (file.equals(RESERVATIONS))
			csvFile = RESERVATION_CSV_PATH;

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
		for (MovieSchedule item : movieSchedules) {
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
		if (min < 10)
			minute = "0" + min;

		if (hours >= 12) {
			if (hours != 12)
				hours -= 12;
			newHourFormat = hours + ":" + minute + " PM";
		} else
			newHourFormat = hours + ":" + minute + " AM";

		if (hours < 10)
			newHourFormat = "0" + newHourFormat;

		return newHourFormat;
	}

	private float calculateTotalPrice(byte numOfWatchers, byte numOfSenior, boolean isPremiere) {
		float price = (isPremiere ? 500 : 350);
		
		if(isPremiere)
			return (price * numOfWatchers);
		else
			return (price * numOfWatchers) - (float) (price * .20 * numOfSenior);
	}

	private Reservation getReservationTicketNumber(int ticketNumber) {
		Reservation reservationItem = null;

		for (Reservation item : reservations) {
			if (ticketNumber == (item.getReservationNum())) {
				reservationItem = item;
				break;
			} 
		}

		return reservationItem;
	}
	
	public static void main(String args[]) {
		MRSApp app = new MRSApp();
		app.readMovieScheduleCSV();
		app.readReservationCSV();
		MovieSchedule selectedMovieSched = null;
		Reservation reservationObj = null;
		String seatCodesInput="";
		boolean invalidInput;

		boolean runApp = true;

		do {
			System.out.print("Main Menu\n[1] Reserve Seat\n[2] Cancel Reservation\n\nPick Option: ");
			String response = scan.next();
			
			switch (response) {

			case "1":
				if (!app.displayMovieSchedules())
					break;

				// Initialize movieId
				short parsedMovieId = -1;
				invalidInput = true;

				while (invalidInput) {
					System.out.print("Enter [ESC] to cancel transaction.\nEnter movie schedule id: ");
					String movieId = scan.next();

					if (movieId.equalsIgnoreCase("ESC")) {
						response = "-1";
						invalidInput = false;
					} else {
						try {
							parsedMovieId = Short.parseShort(movieId);
							selectedMovieSched = app.movieSchedules.get(parsedMovieId - 1);
							app.displaySeatLayout(selectedMovieSched);
							response = "1";
							invalidInput = false;
						} catch (Exception e) {
							System.out.println("\nInvalid movie number\n");
							response = "-1";
						}
					}
				}

				invalidInput = true;

				while (invalidInput && !(response.equals("-1"))) {
					System.out.print("\nPlease input seats to be reserved for this transaction: ");
					seatCodesInput = scan.next().toUpperCase();
					
					if(seatCodesInput.equalsIgnoreCase("esc")) 
						break;

					String[] viewerCount = seatCodesInput.split(",");

					System.out.println("\nHow many senior citizens? ");
					response = scan.next();
					
					if(response.equalsIgnoreCase("esc")) 
						break;
					
					byte numOfSenior = 0;
					try {
						numOfSenior = Byte.parseByte(response);
					} catch (Exception e) {
						System.out.println("\nInvalid input for seat codes or number of seniors.");
					}

					if((selectedMovieSched.getSeats().isValidReservation(seatCodesInput, numOfSenior))){
						System.out.print("\nDo you want to proceed with reservation? [Y/N]: ");
						response = scan.next();

						if(response.equalsIgnoreCase("y")){
							app.addReservationCSV(selectedMovieSched, seatCodesInput, app.calculateTotalPrice((byte)viewerCount.length,
												numOfSenior, selectedMovieSched.isPremiereShow()));
							invalidInput = false;
						}
						else if (response.equalsIgnoreCase("n")|| response.equalsIgnoreCase("esc"))
							break;
						else
							System.out.println("Invalid input.");	

					}
					else System.out.println("Invalid input for seat codes or number of seniors." );
				}
				System.out.println();
				break;

			case "2":

				invalidInput = true;

				while (invalidInput) {
					System.out.print("Enter [ESC] to cancel transaction. \nInput ticket number: ");
					response = scan.next();

					if (response.equalsIgnoreCase("ESC"))
						invalidInput = false;
					else {
						int ticketNumber = 0;

						try {
							ticketNumber = Integer.parseInt(response);
							reservationObj = app.getReservationTicketNumber(ticketNumber);
						} catch (Exception e) {
							System.out.println("\nInvalid input.");
							response = "-1"; 
							// if input is invalid, it shouldn't enter the next if statment.
							// just to prevent printing ticketNumber doesn't exists 
						}

						if (reservationObj != null && !(response.equals("-1"))) {
							app.displaySeatLayout(reservationObj.getMovie());
							while(invalidInput){
								System.out.println("\nDo you want to proceed on the cancellation? [Y/N]: ");
								String inputResponse = scan.next();
							
								if (inputResponse.equalsIgnoreCase("y")) {
									String seatCodes = reservationObj.getSeatCodes();
									if (reservationObj.getMovie().getSeats().isValidCancellation(seatCodes))
										app.removeReservationCSV(reservationObj);
								
									else 
										System.out.println("Invalid ticket cancellation");
									
									invalidInput = false;	
								}
								else System.out.println("Invalid input.");
							}
						}
						else	System.out.println("\nTicket number doesn't exists.\n");
					}
				}
				System.out.println();
				break;
			default:
				System.out.println("\nPlease input valid response\n");
				break;
			}
		} while (runApp);

		scan.close();
	}

}
