package martinezmencias.app.wordbag.database.data;

public class Word {
	
	private int id;
	private String wordName;
	private int dictionaryId;
	private int rightCount = 0;
	private int wrongCount = 0;
	private int active = 1;

	public Word(int id, String wordName, int dictionaryId, int active){
		this.id = id;
		this.wordName = wordName;
		this.dictionaryId = dictionaryId;
		this.active = active;
	}
	
	public Word(String wordName, int dictionaryId, int active){
		this.wordName = wordName;
		this.dictionaryId = dictionaryId;
		this.active = active;
	}
	
	public Word(){
		
	}
	
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public String getWordName() {
		return wordName;
	}
	public void setWordName(String wordName) {
		this.wordName = wordName;
	}
	public int getDictionaryId() {
		return dictionaryId;
	}
	public void setDictionaryId(int dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public int getRightCount() {
		return rightCount;
	}

	public void setRightCount(int rightCount) {
		this.rightCount = rightCount;
	}
	public int getWrongCount() {
		return wrongCount;
	}
	public void setWrongCount(int wrongCount) {
		this.wrongCount = wrongCount;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public boolean isActive(){
		if(this.active > 0){
			return true;
		}
		return false;
	}
}
