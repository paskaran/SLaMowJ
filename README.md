SLaMowJ = Simple Language Model with Java 
=========================================
(MAVEN-Project)

Version v1.0
------------

SLaMowJ is a simple n-gram language model framework. 
With SLaMowJ a user has the possibility to create a  database which 
contains words and their possible (n-1) following words. 
(For more information about n-gram model see: https://en.wikipedia.org/wiki/N-gram)

0. How to use
----------

Before using the language model, you should train the database with text files.

English/German languages are good for training of the database.
But you can use any language you want, make sure that every sentence ends with 
one of these characters " **!**, **?**, **,** , **.** ". 
Every word within the text has to be **seperated** by a **whitespace**.  

1. Train a database
-------------------
For training the database you have to use the Trainer.java Utility-Class:

`Trainer.trainStandardEnglishMode("PATH_TO_DATABASE_WITHOUT_SUFFIX", TextReader.readFromFile("train.set"),
					TrainerMode.WORD, Trainer.BIGRAM, true);`
With this short code, you can train a database with the content of the "train.set" file.
The created database is located within the database folder.

You can use three TrainerMode's:
- SENTENCE
- WORD
- CHAR

For displaying some informations while trainig the database call `Trainer.initDebugger();`

While training, the following (n-1)-grams of a word are always sorted by its count of occurrence. 
That means the result you will get by calling the following methods, are always sorted by the most possible word.

2. Using the database
---------------------
After the training you can use the interface LanguageModel to access the trained database.
For instantiating a LanguageModel-Object you have to use this following code:
`LanguageModel lm = LanguageModelBuilder.createInstance("PATH_TO_DATABASE_WITHOUT_SUFFIX");`
after this line of code you can access the database using the two methods:

`public String[] getGramFromModel(String firstWord);`

The method `String[] getGramFromModel(String first Word);` retrieves  if exists the  
the following (n-1)- words of the given firstWord, otherwise the method returns null.


`public String[] getGramsOfGrams(String firstWord, int maxWidth, int maxDepth);`

This method returns all possible word combinations from the given word and its maxDepth and maxWidth followers.
Take care by using this method with high numbers for maxDepth and/or maxWidth!

Make sure to close the connection to the database by using this method: `closeLanguageModel()` 

3. Example
----------
An example code can be found within the main folder.




