package main;

import java.io.File;
import java.io.IOException;

import slamowj.LanguageModel;
import slamowj.LanguageModelBuilder;
import slamowj.TextReader;
import slamowj.Trainer;
import slamowj.Trainer.TrainerMode;

public class StartSLaMowJ {

	public static void main(String[] args) {

		// Train the database with the content of train.set - file
		// TrainMode is WORD and length of gram is 2 = BIGRAM
		try {
			Trainer.trainStandardEnglishMode("test",
					TextReader.readFromFile("train.set"), TrainerMode.WORD,
					Trainer.BIGRAM, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Instantiating a LanguageModel object pointing to the currently
		// trained database.
		// Make sure you use the full path without suffix of database.
		LanguageModel lm = LanguageModelBuilder.createInstance(System
				.getProperty("user.dir") + File.separator + "databases/test");
		// Retrieving the (n-1) grams for the word ="the"
		String[] gr = lm.getGramFromModel("the");
		if (gr != null) {
			for (String string : gr) {
				System.out.println(string);
			}
		}

	}

}
