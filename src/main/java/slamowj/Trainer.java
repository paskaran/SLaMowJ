package slamowj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

public class Trainer {
	public static final int BIGRAM = 2;
	public static final int TRIGRAM = 3;
	public static final int TETRAGRAM = 4;
	public static final int PENTAGRAM = 5;
	public static final int HEXAGRAM = 6;
	public static final int HEPTAGRAM = 7;
	public static final int OKTOGRAM = 8;
	public static final int MULTIGRAM = Integer.MAX_VALUE;
	private static boolean debugg = false;
	private static URLCodec encoder = new URLCodec();

	private Trainer() {

	}

	public static void trainStandardEnglishMode(String dbName, String text,
			TrainerMode mode, int gram, boolean exportWithConfidence)
			throws IOException {
		String regex = "\\.|,|\\?|!";
		train(dbName, text, mode, gram, exportWithConfidence, regex);
	}

	public static void train(String dbName, String text, TrainerMode mode,
			int gram, boolean exportWithConfidence, String sentenceRegex)
			throws IOException {
		if (text == null) {
			throw new IllegalStateException(
					"Text is null! Could not train db with text!");
		}
		if (mode == null) {
			throw new IllegalStateException("TrainerMode is null!");
		}
		if (gram < 2) {
			throw new IllegalStateException(
					"Gram is to small! Gram should be at least two!");
		}
		print("Creating database with name " + dbName + ".");
		String filePathWithoutSuffix = System.getProperty("user.dir")
				+ File.separator + "databases" + File.separator + dbName;
		File file = new File(filePathWithoutSuffix + ".db");
		if (file.exists()) {
			file.delete();
		}
		int ret = createDatabase(filePathWithoutSuffix);
		dbName = filePathWithoutSuffix;
		print("Database returns " + ret + ".");
		if (ret == -1) {
			print("SQL Exception occurred! Can't create DB ! System exit!");
			System.exit(-1);
		}
		ArrayList<Gram> mainList = new ArrayList<Gram>();

		text = text.trim();
		print("InputText:\n" + text);
		print("TrainerMode is " + mode + ".");
		if (mode == TrainerMode.SENTENCE) {
			print("Splitting text on following regex [" + sentenceRegex + "].");
			String[] textSplits = text.split(sentenceRegex);
			print("Array length "
					+ ((textSplits != null) ? textSplits.length
							: "!! Split is null !!"));
			for (int i = 0; i < textSplits.length; i++) {
				textSplits[i] = cleanFullText(textSplits[i]);
			}
			print("Extracting grams...(only if text/textSplit is not empty!)...");
			for (int i = gram - 1; i < textSplits.length; i++) {
				String tmp1 = textSplits[i - (gram - 1)];
				if (!tmp1.isEmpty()
						&& !tmp1.replaceAll("\\+", " ").equalsIgnoreCase(" ")) {

					StringBuffer sb = new StringBuffer("");
					for (int j = gram - 2; j >= 0; j--) {
						sb.append(textSplits[i - j] + ". ");
					}
					String followers = sb.toString();
					mainList.add(new Gram(textSplits[i - (gram - 1)], followers));
				}
			}
		} else if (mode == TrainerMode.WORD) {
			print("Splitting text on sentence using regex [" + sentenceRegex
					+ "].");
			String[] sentenceSplit = text.split(sentenceRegex);
			print("Array length "
					+ ((sentenceSplit != null) ? sentenceSplit.length
							: "!! Split is null !!"));
			int gram2 = gram;
			print("Extracting grams...(only if text/textSplit is not empty!)...");
			for (int i = 0; i < sentenceSplit.length; i++) {
				String[] wordSplit = cleanFullText(sentenceSplit[i]).split(
						"\\+");
				if (gram == MULTIGRAM && wordSplit != null) {
					gram2 = wordSplit.length;
				}
				for (int j = gram2 - 1; j < wordSplit.length; j++) {
					StringBuffer sb = new StringBuffer("");
					for (int k = gram2 - 2; k >= 0; k--) {
						sb.append(wordSplit[j - k] + " ");
					}
					String followers = sb.toString();
					mainList.add(new Gram(wordSplit[j - (gram2 - 1)],
							(followers.substring(0, followers.length() - 1))));
				}
			}

		} else if (mode == TrainerMode.CHAR) {
			print("Splitting text on words using regex [\\s+].");
			String[] sentenceSplit = text.split(sentenceRegex);
			for (String sentence : sentenceSplit) {
				String[] wordSplit = sentence.split("\\s+");
				print("Extracting grams...(only if text/textSplit is not empty!)...");
				for (int i = 0; i < wordSplit.length; i++) {
					wordSplit[i] = wordSplit[i].trim();
					if (!wordSplit[i].isEmpty()) {
						char[] wordArray = wordSplit[i].trim().toCharArray();
						int gram2 = gram;
						for (int j = 0; j < wordArray.length - gram + 1; j++) {
							mainList.add(new Gram(cleanFullText(wordSplit[i]
									.substring(j, j + 1)),
									cleanFullText((wordSplit[i].substring(
											j + 1, j + gram2)))));
						}
					}
				}
			}

		}
		print("Creating filter list and counting duplicate grams from main list.");
		print("Main list size: "+mainList.size());
		print("Filter main list...");
		ArrayList<TextAndFollowers> filteredList = new ArrayList<TextAndFollowers>();
		for (Gram gac : mainList) {
			TextAndFollowers taf = getTextAndFollowers(gac.getFirstWord(),
					filteredList);
			if (taf == null) {
				taf = new TextAndFollowers(gac.getFirstWord());
				filteredList.add(taf);
			}
			taf.setTextAndConfidence(gac.getFollower(), null);
		}
		print("Filtered list size: "+filteredList.size());
		print("Sorting filtered list...");
		Collections.sort(filteredList);
		print("Sorting followers of every text...");
		for (TextAndFollowers textAndFollowers : filteredList) {
			textAndFollowers.sort();
		}
		print("Exporting extracted data into database with name " + dbName
				+ ".db, this will take some time...please wait...");
		exportListToDatabase(filteredList, dbName, exportWithConfidence);
		print("Exporting finished");
	}

	private static void exportListToDatabase(
			ArrayList<TextAndFollowers> filteredList, String dbName,
			boolean exportWithConfidence) {
		SQLiteJDBCConnector sql = new SQLiteJDBCConnector();
		sql.initConnection(dbName);
		sql.createStatement();

		for (TextAndFollowers taf : filteredList) {
			String followers = (exportWithConfidence) ? taf
					.getFollowersForDatabase() : taf
					.getFollowersForDatabaseWithoutConfidence();
			print(" -> " + taf);
			sql.insertGram(taf.getText(), followers);
		}
		sql.closeStatement();
		sql.close();
		return;
	}

	public static TextAndFollowers getTextAndFollowers(String text,
			ArrayList<TextAndFollowers> filteredList) {
		for (TextAndFollowers taf : filteredList) {
			if (taf.isEqualByText(text)) {
				return taf;
			}
		}
		return null;
	}

	public enum TrainerMode {
		SENTENCE, WORD, CHAR
	}

	public static void initDebugger() {
		debugg = true;
	}

	public static void stopDebugger() {
		debugg = false;
	}

	private static int createDatabase(String name) {
		if (name == null || name.isEmpty()) {
			name = "default";
		}
		SQLiteJDBCConnector sql = new SQLiteJDBCConnector();
		return sql.createContentTable(name);
	}

	private static void print(String msg) {
		if (debugg) {
			System.out.println("SLaMowJ> " + msg);
		}
	}

	private static String cleanFullText(String text) {
		text = text.trim();
		try {
			text = encoder.encode(text);
		} catch (EncoderException e) {
			e.printStackTrace();
		}
		return text;
	}

}
