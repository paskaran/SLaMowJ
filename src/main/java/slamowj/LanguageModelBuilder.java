package slamowj;

import java.util.ArrayList;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

public final class LanguageModelBuilder {
	private static URLCodec codec = new URLCodec();

	private LanguageModelBuilder() {
	}

	public static LanguageModel createInstance(String dbName) {
		return new LanguageModelObject(dbName);
	}

	private static class LanguageModelObject implements LanguageModel {
		private final String dbName;
		private SQLiteJDBCConnector sql = new SQLiteJDBCConnector();

		public LanguageModelObject(String dbName) {
			this.dbName = dbName;
			sql.initConnection(dbName);
			sql.createStatement();
		}

		@Override
		public void closeLanguageModel() {
			this.sql.closeStatement();
			this.sql.close();
		}

		@Override
		public String[] getGramFromModel(String firstWord) {
			try {
				firstWord = codec.encode(firstWord);
			} catch (EncoderException e1) {
				e1.printStackTrace();
			}
			if (firstWord.contains("+")) {
				String[] fwSplit = firstWord.split("\\+");
				if (fwSplit != null) {
					firstWord = fwSplit[fwSplit.length - 1];
				}
			}
			String[] grams = this.sql.getGramByFirstWord(firstWord);
			if (grams == null) {
				return null;
			}
			try {
				for (int i = 0; i < grams.length; i++) {
					grams[i] = codec.decode(grams[i]);
				}
			} catch (DecoderException e) {
				e.printStackTrace();
			}

			return grams;
		}

		@Override
		public String[] getGramsOfGrams(String firstWord, int maxWidth,
				int maxDepth) {
			ArrayList<String> nmGrams = new ArrayList<String>();
			getGramsOfGramsHelper(firstWord, maxWidth, maxDepth, nmGrams,
					firstWord);
			return convertListToArray(nmGrams);
		}

		private String getGramsOfGramsHelper(String firstWord, int maxWidth,
				int maxDepth, ArrayList<String> nmGrams, String accu) {
			String[] followers = getGramFromModel(firstWord);
			if (followers != null && maxDepth > 1) {
				int mD = (followers.length < maxWidth) ? followers.length
						: maxWidth;
				for (int w = 0; w < mD; w++) {
					String nm = getGramsOfGramsHelper(followers[w], maxWidth,
							maxDepth - 1, nmGrams, accu + " " + followers[w]);
					if (nm != null) {
						nmGrams.add(nm);
					}
				}
			} else {
				return accu;
			}
			return null;
		}

		private static String[] convertListToArray(ArrayList<String> list) {
			String[] array = new String[list.size()];
			int i = 0;
			for (String string : list) {
				array[i] = string;
				i++;
			}
			return array;
		}
	}
}
