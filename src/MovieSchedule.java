import java.time.LocalDateTime;

public class MovieSchedule {
	private short movieSchedule;
	private LocalDateTime showingDateTime;
	private Movie movie;
	private boolean isPremiereShow;
	private SeatLayout seats;
	
	public MovieSchedule(short movieSchedule, LocalDateTime showingDateTime, Movie movie, boolean isPremiereShow,
			SeatLayout seats) {
		super();
		this.movieSchedule = movieSchedule;
		this.showingDateTime = showingDateTime;
		this.movie = movie;
		this.isPremiereShow = isPremiereShow;
		this.seats = seats;
	}

	public short getMovieSchedule() {
		return movieSchedule;
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
