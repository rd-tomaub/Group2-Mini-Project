import java.time.LocalDateTime;

public class MovieSchedule {
	private short movieScheduleId;
	private LocalDateTime showingDateTime;
	private Movie movie;
	private boolean isPremiereShow;
	private SeatLayout seats;
	
	public MovieSchedule(short movieScheduleId, LocalDateTime showingDateTime, Movie movie, boolean isPremiereShow,
			short seats) {
		super();
		this.movieScheduleId = movieScheduleId;
		this.showingDateTime = showingDateTime;
		this.movie = movie;
		this.isPremiereShow = isPremiereShow;
		this.seats = new SeatLayout(seats);
	}

	public short getMovieScheduleId() {
		return movieScheduleId;
	}

	public LocalDateTime getShowingDateTime() {
		return showingDateTime;
	}

	public Movie getMovie() {
		return movie;
	}

	public boolean isPremiereShow() {
		return isPremiereShow;
	}

	public SeatLayout getSeats() {
		return seats;
	}
	
}
