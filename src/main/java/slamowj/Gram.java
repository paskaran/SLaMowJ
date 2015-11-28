package slamowj;

public class Gram {

	private final String firstWord;
	private final String followers;

	protected Gram(String firstWord, String followers) {
		if (followers == null) {
			throw new IllegalArgumentException("First word is nulL!");
		}
		this.firstWord = firstWord;
		this.followers = followers;
	}

	public String getFollower() {
		return followers;
	}

	public String getFirstWord() {
		return firstWord;
	}

}
