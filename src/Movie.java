
public class Movie {
	private short movieId;
	private String moviewName;
	private float duration;
	private byte cinemaNum;
	
	public Movie(short movieId, String moviewName, float duration, byte cinemaNum) {
		super();
		this.movieId = movieId;
		this.moviewName = moviewName;
		this.duration = duration;
		this.cinemaNum = cinemaNum;
	}

	public short getMovieId() {
		return movieId;
	}

	public String getMoviewName() {
		return moviewName;
	}

	public float getDuration() {
		return duration;
	}

	public byte getCinemaNum() {
		return cinemaNum;
	}
	
}
