import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MRSApp {
	private ArrayList<MovieSchedule> movies;
	private ArrayList<Reservation> reservations;
	
	static private ArrayList<MovieSchedule> movieListByDate;
	static private MovieSchedule currentMovieSelected;
	
	private final String MOVIES = "MOVIES";
	private final String RESERVATIONS = "RESERVATIONS";
	static Scanner scan = new Scanner(System.in);

	public MRSApp() {
		movies = new ArrayList<MovieSchedule>();
		reservations = new ArrayList<Reservation>();
	}

	public boolean displayMovies() {

		// Initialize date format
		String expectedFormat = "yyyy-MM-dd";
		SimpleDateFormat expectedDateFormat = new SimpleDateFormat(expectedFormat);
		// Get current date
		// LocalDateTime currentDate = LocalDateTime.now();
		LocalDateTime currentDate = generateDateTime("2020-12-01", "00:00"); // Assume current date is 2020-12-01
		boolean isCheckSchedule = true;
		do {

			System.out.println("\nEnter Date: \n[ESC] to cancel transaction");
			try {
				// Store input date to date variable
				String date = scan.next();
				// Back to main menu if user Enter ESC
				if (date.equalsIgnoreCase("esc")) {
					return false;
				} else {
					// Parse the date string to a Date object
					Date parsedDate = expectedDateFormat.parse(date);
					// Format the parsed date back to a string to check if it matches the original
					// string
					String formattedDate = expectedDateFormat.format(parsedDate);
					// Convert inputed date to LocalDateTime object
					LocalDateTime inputDate = generateDateTime(date, "00:00");
					movieListByDate = filterMoviesByDate(date);
					// Check if user input matched format date
					if (date.equals(formattedDate)) {
						if (movieListByDate.size() != 0) {
							if (inputDate.isAfter(currentDate)) {
								// Display movie schedules
								System.out.println("\nMovie Schedule ID\tTime Start\tTitle");
								for (MovieSchedule item : movieListByDate) {
									LocalDateTime dateTime = item.getShowingDateTime();
									System.out.println("[" + item.getMovieScheduleId() + "]\t\t\t" + dateTime.getHour()
											+ ":" + dateTime.getMinute() + "\t\t" + item.getMovie().getMovieName());

								}
								isCheckSchedule = false;
								System.out.println();
							} else {
								System.out.println("\nCannot Reserve seats on past dates\n");
							}
						} else {
							System.out.println("\nNo Movies Available on this day\n");
						}
					}
				}
			} catch (ParseException e) {
				System.out.println("Invalid Date Format");
			} catch (DateTimeParseException e) {
				System.out.println("Invalid Date Format");
			}
		} while (isCheckSchedule);
		System.out.println();
		return true;
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
		String temp = date + " " + time + ":00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(temp, formatter);
		return dateTime;
	}

	private ArrayList<String> readFromCSV(String file) {
		String csvFile = "";
		ArrayList<String> csvData = new ArrayList<>();

		if (file.equals(MOVIES))
			csvFile = "C:/Users/adrian.enriquez/Downloads/MovieSchedule1.csv";
		else if (file.equals(RESERVATIONS))
			csvFile = "C:/Users/adrian.enriquez/Downloads/Reservations1.csv";

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
		if (hours > 12) {
			newHourFormat = hours - 12 + ":" + time.getMinute() + " PM";
		} else {
			newHourFormat = hours + ":" + time.getMinute() + " AM";
		}
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

	private void displaySeatLayout(short movieSchedId) {
		
		
		String movieName = currentMovieSelected.getMovie().getMovieName();
		String dateTime = generateAmPm(currentMovieSelected.getShowingDateTime());
		String duration = String.valueOf(currentMovieSelected.getMovie().getDuration());

		System.out.println("\nSeat Layout for\n" + movieName + " @ " + dateTime + "\n[" + movieSchedId + "] "
				+ movieName + ", " + dateTime + ", " + duration + ", " + availableSeats(currentMovieSelected));
		currentMovieSelected.getSeats().displaySeatLayout();
	}


	public static void main(String args[]) {
		MRSApp app = new MRSApp();
		app.readMovieCSV();
		app.readReservationCSV();

		boolean runApp = true;

		do {
			System.out.print("Main Menu\n[1] Reserve Seat\n[2] Cancel Reservation\n\nPick Option: ");
			String response = scan.next();
			switch (response) {
			case "1":
				if (!app.displayMovies()) {
					break;
				}
				// Initialize movieId
				short parsedMovieId;
				boolean isRunMovieSched = true, isRunSeatCodes = true;
				while (isRunMovieSched) {
					System.out.print("Enter Movie Schedule ID: ");
					String movieId = scan.next();
					try {
						parsedMovieId = Short.parseShort(movieId);
						for (MovieSchedule item : movieListByDate) {
							if (parsedMovieId == item.getMovieScheduleId()) {
								currentMovieSelected = item;
								break;
							}
						}
						if (currentMovieSelected != null) {
//							currentMovieSelected.getSeats().displaySeatLayout();
							app.displaySeatLayout(parsedMovieId);
							isRunMovieSched = false;
							System.out.println();
						} else {
							System.out.println("\nInvalid Movie Number\n");
						}
					} catch (NumberFormatException e) {
						System.out.println("\nInvalid Movie Number\n");
					}
				}
				
				while (isRunSeatCodes) {
					
					System.out.println("\nPlease input seats to be reserved for this transaction: ");
					String seatCodesInput = scan.next();
					try {
					System.out.println("\nHow many senior citizens? ");
					int numCitizen = scan.nextInt();
					}catch(InputMismatchException e) {
						System.out.println("\nInvalid input");
					}
//					if()
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
