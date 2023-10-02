
public class Reservation {
	private int reservationNum;
	private String seatCodes;
	private MovieSchedule movie;
	
	public Reservation(int reservationNum, String seatCodes, MovieSchedule movie) {
		super();
		this.reservationNum = reservationNum;
		this.seatCodes = seatCodes;
		this.movie = movie;
	}

	public int getReservationNum() {
		return reservationNum;
	}

	public String getSeatCodes() {
		return seatCodes;
	}

	public MovieSchedule getMovie() {
		return movie;
	}
	
}
