package slamowj;

public interface LanguageModel {
	
	public String[] getGramFromModel(String firstWord);
	
	public String[] getGramsOfGrams(String firstWord, int maxWidth, int maxDepth);

	public void closeLanguageModel();
	 
}
