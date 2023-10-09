
public class Movie {
	private short movieId;
	private String movieName;
	private float duration;
	private byte cinemaNum;

	public Movie(short movieId, String movieName, float duration, byte cinemaNum) {
		super();
		this.movieId = movieId;
		this.movieName = movieName;
		this.duration = duration;
		this.cinemaNum = cinemaNum;
	}

	public short getMovieId() {
		return movieId;
	}

	public String getMovieName() {
		return movieName;
	}

	public float getDuration() {
		return duration;
	}

	public byte getCinemaNum() {
		return cinemaNum;
	}

}
