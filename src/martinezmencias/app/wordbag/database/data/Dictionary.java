package martinezmencias.app.wordbag.database.data;

public class Dictionary {
	
	private int id;
	private String dictionaryName;
	
	public Dictionary(int id, String dictionaryName){
		this.id = id;
		this.dictionaryName = dictionaryName;
	}
	
	public Dictionary(String dictionaryName){
		this.dictionaryName = dictionaryName;
	}
	
	public Dictionary(){
		
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getDictionaryName() {
		return dictionaryName;
	}

	public void setDictionaryName(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}
	
}
