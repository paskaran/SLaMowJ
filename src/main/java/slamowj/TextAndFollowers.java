package slamowj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class TextAndFollowers implements Comparable<TextAndFollowers> {
	private String text;
	private ArrayList<TextAndConfidence> followers = new ArrayList<TextAndFollowers.TextAndConfidence>();

	public TextAndFollowers(String text) {
		this.text = text;
	}

	public void setTextAndConfidence(String text, Double confidence) {
		TextAndConfidence tac = new TextAndConfidence(text, confidence);
		TextAndConfidence tacOld = getTextAndConfidenceByText(text);
		if (tacOld == null) {
			this.followers.add(tac);
			tac.count = tac.count++;
		} else {
			tacOld.count++;
		}
	}

	public void sort() {
		Collections.sort(followers, new Comparator<TextAndConfidence>() {
			@Override
			public int compare(TextAndConfidence o1, TextAndConfidence o2) {
				int compareConfidence = (-1) * o1.count.compareTo(o2.count);
				if (compareConfidence == 0) {
					return o1.text.compareTo(o2.text);
				}
				return compareConfidence;
			}
		});
	}

	protected TextAndConfidence getTextAndConfidenceByText(String text) {
		TextAndConfidence tacTmp = new TextAndConfidence(text, null);
		for (TextAndConfidence textAndConfidence : followers) {
			if (textAndConfidence.equals(tacTmp)) {
				return textAndConfidence;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return text.equals(((TextAndFollowers) obj).getText());
	}

	public String getText() {
		return text;
	}

	@Override
	public int compareTo(TextAndFollowers o) {
		return text.compareTo(o.text);
	}

	public boolean isEqualByText(String text) {
		return this.text.equals(text);
	}

	protected class TextAndConfidence implements Comparable<TextAndConfidence> {
		private String text;
		private Double confidence;
		private Integer count = 1;

		protected TextAndConfidence(String text, Double confidence) {
			this.text = text;
			this.confidence = confidence;
		}

		public String getText() {
			return text;
		}

		public Double getConfidence() {
			return confidence;
		}

		@Override
		public int compareTo(TextAndConfidence o) {
			return text.compareTo(o.text);
		}

		@Override
		public boolean equals(Object obj) {
			return text.equals(((TextAndConfidence) obj).text);
		}

		@Override
		public String toString() {
			return "[" + text + "|" + count + "]";
		}

	}

	@Override
	public String toString() {
		return "[" + text + "|" + followers + "]";
	}

	public String getFollowersForDatabaseWithoutConfidence() {
		StringBuffer sb = new StringBuffer();
		for (TextAndConfidence tac : followers) {
			sb.append(tac.text + ";");
		}
		String res = sb.toString();
		return res.substring(0, res.length() - 1);
	}

	public String getFollowersForDatabase() {
		StringBuffer sb = new StringBuffer();
		for (TextAndConfidence tac : followers) {
			sb.append(tac.text + "[" + tac.count + "];");
		}
		String res = sb.toString();
		return res.substring(0, res.length() - 1);
	}
}
