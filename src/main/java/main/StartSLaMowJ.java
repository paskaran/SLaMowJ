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
//		Trainer.initDebugger();
//		try {
//			Trainer.trainStandardEnglishMode("test2", TextReader.readFromFile("train.set"),
//					TrainerMode.WORD, Trainer.BIGRAM, true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		LanguageModel lm = LanguageModelBuilder.createInstance(System
				.getProperty("user.dir") + File.separator + "databases/test2");
		String[] gr = lm.getGramsOfGrams("his", 2, 10);
		 
		if (gr != null) {
			for (String string : gr) {
				System.out.println(string);
			}
		}

	}

}
