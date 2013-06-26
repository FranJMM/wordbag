package martinezmencias.app.wordbag.database.data;

public class Translation {
	
	private int id;
	private int wordId;
	private String translationName;
	
	public Translation(int id, int wordId, String translationName){
		this.id = id;
		this.wordId = wordId;
		this.translationName = translationName;
	}
	
	public Translation(int wordId, String translationName){
		this.wordId = wordId;
		this.translationName = translationName;
	}
	
	public Translation(String translationName){
		this.translationName = translationName;
	}
	
	public Translation(){
		
	}
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public int getWordId() {
		return wordId;
	}
	public void setWordId(int wordId) {
		this.wordId = wordId;
	}
	public String getTranslationName() {
		return translationName;
	}
	public void setTranslationName(String translationName) {
		this.translationName = translationName;
	}
}
