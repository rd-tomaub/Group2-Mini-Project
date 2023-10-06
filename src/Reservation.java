import java.io.FileWriter;

public class Reservation {
	private int reservationNum;
	private String seatCodes;
	private MovieSchedule movie;
	private float totalPrice;

	public Reservation(int reservationNum, String seatCodes, MovieSchedule movie, float totalPrice) {
		super();
		this.reservationNum = reservationNum;
		this.seatCodes = seatCodes;
		this.movie = movie;
		this.totalPrice = totalPrice;
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

	public float getTotalPrice() {
		return totalPrice;
	}

	public String toString() {
		String dateTime = movie.getShowingDateTime() + "";
		String[] splittedDateTime = dateTime.split(" ");
		String data = reservationNum + "," + splittedDateTime[0] + "," + movie.getMovie().getCinemaNum() + ","
				+ splittedDateTime[1] + "," + "\"" + seatCodes + "\"" + "," + totalPrice + "";

		return data;
	}

}
