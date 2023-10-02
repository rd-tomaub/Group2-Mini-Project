
public class Reservation {
	private short reservationNum;
	private String seatCodes;
	private MovieSchedule movie;
	
	public Reservation(short reservationNum, String seatCodes, MovieSchedule movie) {
		super();
		this.reservationNum = reservationNum;
		this.seatCodes = seatCodes;
		this.movie = movie;
	}

	public short getReservationNum() {
		return reservationNum;
	}

	public String getSeatCodes() {
		return seatCodes;
	}

	public MovieSchedule getMovie() {
		return movie;
	}
	
}
