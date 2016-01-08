package martinezmencias.app.wordbag.database.data;

import java.util.ArrayList;

public class WordWithTranslations {
	private Word word;
	private ArrayList<Translation> translations;
	
	public WordWithTranslations(){
		this.translations = new ArrayList<Translation>();
	}
	
	public Word getWord() {
		return word;
	}
	public void setWord(Word word) {
		this.word = word;
	}
	public ArrayList<Translation> getTranslations() {
		return translations;
	}
	public void setTranslations(ArrayList<Translation> translations) {
		this.translations = translations;
	}
}
