package martinezmencias.app.wordbag.database.handler;

import java.util.ArrayList;

import martinezmencias.app.wordbag.database.data.Translation;
import martinezmencias.app.wordbag.database.data.Word;
import martinezmencias.app.wordbag.database.data.WordWithTranslations;
import martinezmencias.app.wordbag.database.data.Dictionary;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.UserDictionary.Words;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "WordBagManager";
    
    //Common table columns names
    private static final String KEY_ID = "id";
 
    //WORDS TABLE
    // Words table name
    private static final String TABLE_WORDS = "words";
    // Words table Columns names
    private static final String KEY_WORD_NAME = "word_name";
    private static final String KEY_DICTIONARY_ID = "dictionary_id";
    private static final String KEY_RIGHT_COUNT = "right_count";
    private static final String KEY_WRONG_COUNT = "wrong_count";
    private static final String KEY_ACTIVE = "active";
    
    //TRANSLATIONS TABLE
    //Translations table name
    private static final String TABLE_TRANSLATIONS = "translations";
    //Translations table columns names
    private static final String KEY_WORD_ID = "word_id";
    private static final String KEY_TRANSLATION = "translation";
    
    //DICTIONARIES TABLE
    //Translations table name
    private static final String TABLE_DICTIONARIES = "dictionaries";
    //Translations table columns names
    private static final String KEY_DICTIONARY_NAME = "dictionary_name";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
		String CREATE_DICTIONARIES_TABLE = "CREATE TABLE " + TABLE_DICTIONARIES
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DICTIONARY_NAME
				+ " TEXT" + ")";

		String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_WORD_NAME + " TEXT,"
				+ KEY_DICTIONARY_ID + " INTEGER," + KEY_RIGHT_COUNT
				+ " INTEGER," + KEY_WRONG_COUNT + " INTEGER, " + KEY_ACTIVE
				+ " INTEGER )";

		String CREATE_TRANSLATIONS_TABLE = "CREATE TABLE " + TABLE_TRANSLATIONS
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WORD_ID
				+ " INTEGER," + KEY_TRANSLATION + " TEXT" + ")";
        
        db.execSQL(CREATE_WORDS_TABLE);
        db.execSQL(CREATE_TRANSLATIONS_TABLE);
        db.execSQL(CREATE_DICTIONARIES_TABLE);
    }
    
    public void addDictionary(Dictionary dictionary){
    	
    	SQLiteDatabase db = this.getWritableDatabase();
        
        // Inserting Dictionary
        
        ContentValues values = new ContentValues();
        values.put(KEY_DICTIONARY_NAME, dictionary.getDictionaryName()); // Word Name
      
        long dictionary_id = db.insert(TABLE_DICTIONARIES, null, values);
        dictionary.setID((int)dictionary_id);

        db.close(); // Closing database connection
    	
    }
    
    // Adding new word with translations
    public void addWordWithTranslations(WordWithTranslations wordWithTranslations) {
        
    	SQLiteDatabase db = this.getWritableDatabase();
     
        // Inserting Word Row
        
        ContentValues values = new ContentValues();
        values.put(KEY_WORD_NAME, wordWithTranslations.getWord().getWordName()); // Word Name
        values.put(KEY_DICTIONARY_ID, wordWithTranslations.getWord().getDictionaryId()); // Word Dictionary
        values.put(KEY_RIGHT_COUNT, wordWithTranslations.getWord().getRightCount()); // Word Right count
        values.put(KEY_WRONG_COUNT, wordWithTranslations.getWord().getWrongCount()); // Word Wrong count
        values.put(KEY_ACTIVE, 1); // Word Active
        
        long word_id = db.insert(TABLE_WORDS, null, values);
        
        //Inserting Translations
        
        ArrayList<Translation> translations = wordWithTranslations.getTranslations();
        
        for(int i = 0; i < translations.size(); i++){
	        values = new ContentValues();
	        values.put(KEY_WORD_ID, word_id); // Translation Id
	        values.put(KEY_TRANSLATION, translations.get(i).getTranslationName()); // Translation Name
	        db.insert(TABLE_TRANSLATIONS, null, values);
        }

        db.close(); // Closing database connection
    }
    
    public ArrayList<Translation> getAllTranslations(){
    	String selectQuery = "SELECT * FROM " + TABLE_TRANSLATIONS;
        return readTranslationList(selectQuery);
    }
    
    // Getting all words 
    public ArrayList<Word> getAllWords() {
       String selectQuery = "SELECT * FROM " + TABLE_WORDS;
       return readWordList(selectQuery);
    }
    
    // Getting all words from some dictionary
    public ArrayList<Word> getAllWordsFromDictionary(int dictionaryId) {
       if(dictionaryId > -1){
	       String selectQuery = "SELECT * FROM " + TABLE_WORDS + " WHERE " +KEY_DICTIONARY_ID+"="+dictionaryId + " ORDER BY LOWER("+KEY_WORD_NAME+")";
	       return readWordList(selectQuery);
       }
       return new ArrayList<Word>();
    }
    
    // Getting all active words from some dictionary
    public ArrayList<Word> getAllActiveWordsFromDictionary(int dictionaryId) {
       if(dictionaryId > -1){
	       String selectQuery = "SELECT * FROM " + TABLE_WORDS + " WHERE " +KEY_DICTIONARY_ID+"="+dictionaryId + " AND "+KEY_ACTIVE+"=1";
	       return readWordList(selectQuery);
       }
       return new ArrayList<Word>();
    }
    
    public ArrayList<WordWithTranslations> getAllWordsWithTranslationsFromDictionary(int dictionaryId){
        String selectQuery = "SELECT * FROM " + TABLE_WORDS + " WHERE " +KEY_DICTIONARY_ID+"="+dictionaryId + " ORDER BY LOWER("+KEY_WORD_NAME+")";
        ArrayList<Word> words = readWordList(selectQuery);
        ArrayList<WordWithTranslations> wordsWithTranslations = new ArrayList<WordWithTranslations>();
        for(int i=0; i < words.size(); i++){
        	WordWithTranslations wordWithTranslations = new WordWithTranslations();
        	wordWithTranslations.setWord(words.get(i));
        	wordWithTranslations.setTranslations(getAllTranslationsFromWord(words.get(i).getID()));
        	wordsWithTranslations.add(wordWithTranslations);
        }
        return wordsWithTranslations;
    }
    
    public WordWithTranslations getWordWithTranslationsById(int wordId){
    	WordWithTranslations wordWithTranslations = new WordWithTranslations();
    	String selectQuery = "SELECT * FROM " + TABLE_WORDS + " WHERE " +KEY_ID+"="+wordId;
    	ArrayList<Word> wordList = readWordList(selectQuery);
    	if(wordList.size() == 1){
    		wordWithTranslations.setWord(wordList.get(0));
    		wordWithTranslations.setTranslations(getAllTranslationsFromWord(wordId));
    	}
    	return wordWithTranslations;
    }
    
    public ArrayList<Translation> getAllTranslationsFromWord(int wordId){
    	String selectQuery = "SELECT * FROM " + TABLE_TRANSLATIONS + " WHERE " +KEY_WORD_ID+"="+wordId;
        return readTranslationList(selectQuery);
    }
    
    //Getting All Dictionaries
	public ArrayList<Dictionary> getAllDictionaries(){
	    String selectQuery = "SELECT * FROM " + TABLE_DICTIONARIES;
	    return readDictionaryList(selectQuery);
	}
	
	public String getDictionaryNameById(int id){
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARIES + " WHERE "+KEY_ID+"="+id;
		ArrayList<Dictionary> dictionaryList = readDictionaryList(selectQuery);
		if(dictionaryList.size() > 0){
			return dictionaryList.get(0).getDictionaryName();
		}
		return "";
	}
	
	public Word getWordById(int id){
		String selectQuery = "SELECT * FROM " + TABLE_WORDS + " WHERE "+KEY_ID+"="+id;
		ArrayList<Word> wordList = readWordList(selectQuery);
		if(wordList.size() > 0){
			return wordList.get(0);
		}
		return null;
	}
	
	public ArrayList<String> getDistinctInitialCharactersFromDictionary(int dictionaryId){
		//TODO This sql command doesn't work on Android 2.3.4. Temporary fallback: get character by hand, instead of by sql.
		//String selectQuery = "SELECT DISTINCT LEFT ("+KEY_WORD_NAME+",1) FROM "+TABLE_WORDS+" WHERE "+KEY_DICTIONARY_ID+" = "+dictionaryId+" ORDER BY "+KEY_WORD_NAME+" ASC";
		ArrayList<String> characters = new ArrayList<String>();
		ArrayList<Word> words = getAllWordsFromDictionary(dictionaryId);
		for(int i=0; i < words.size(); i++){
			if(i==0 || !words.get(i).getWordName().substring(0,1).toLowerCase().equals(words.get(i-1).getWordName().substring(0,1).toLowerCase())){
				characters.add(words.get(i).getWordName().substring(0,1).toUpperCase());
			}
		}
		return characters;
	}

	public int getRowNumberOfFirstCharacterAppearanceFromDictionary(String character, int dictionaryId) {
		ArrayList<Word> words = getAllWordsFromDictionary(dictionaryId);
		for(int i=0; i < words.size(); i++) {
			if(words.get(i).getWordName().substring(0,1).toLowerCase().equals(character.toLowerCase())){
				return i+1;
			}
		}
		return 0;
	}

	public void deleteDictionary(int dictionaryId){
		String whereDeleteWordsQuery = KEY_DICTIONARY_ID+"="+dictionaryId;
		String whereDeleteDictionaryQuery = KEY_ID+"="+dictionaryId;
		String whereDeleteTranslationsQuery = KEY_WORD_ID + " IN (SELECT "+KEY_ID+" FROM "+TABLE_WORDS+" WHERE "+KEY_DICTIONARY_ID+" = "+dictionaryId+")"; 
		this.getWritableDatabase().delete(TABLE_TRANSLATIONS, whereDeleteTranslationsQuery, null);
		this.getWritableDatabase().delete(TABLE_WORDS, whereDeleteWordsQuery, null);
		this.getWritableDatabase().delete(TABLE_DICTIONARIES, whereDeleteDictionaryQuery, null);
	}

	public void deleteWord(int wordId){
		String whereDeleteTranslationsQuery = KEY_WORD_ID+"="+wordId;
		String whereDeleteWordsQuery = KEY_ID+"="+wordId;
		this.getWritableDatabase().delete(TABLE_TRANSLATIONS, whereDeleteTranslationsQuery, null);
		this.getWritableDatabase().delete(TABLE_WORDS, whereDeleteWordsQuery, null);
	}

	public void deleteTranslation(int translationId){ 
		String whereDeleteTranslationsQuery = KEY_ID+"="+translationId;
		this.getWritableDatabase().delete(TABLE_TRANSLATIONS, whereDeleteTranslationsQuery, null);
	}
    
    public boolean checkTranslation(Word word, String translation_p){
       
    	String selectQuery = "SELECT * FROM "+TABLE_TRANSLATIONS+" WHERE "+KEY_TRANSLATION+"='"+translation_p+"' AND "+KEY_WORD_ID+"="+word.getID();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
  
        if (cursor.moveToFirst()) {
        	this.incrementRightCount(word);
        	return true; 
        }else{
        	this.incrementWrongCount(word);
        	return false;
        }
    }
    
    public void incrementRightCount(Word word){
    	ContentValues values = new ContentValues();
    	values.put(KEY_RIGHT_COUNT, String.valueOf(word.getRightCount()+1));
    	getWritableDatabase().update(TABLE_WORDS, values, KEY_ID+"=?", new String[]{String.valueOf(word.getID())});
    }
    
    public void incrementWrongCount(Word word){
    	ContentValues values = new ContentValues();
    	values.put(KEY_WRONG_COUNT, String.valueOf(word.getWrongCount()+1));
    	getWritableDatabase().update(TABLE_WORDS, values, KEY_ID+"=?", new String[]{String.valueOf(word.getID())});
    }
    
    public void updateWordActive(Word word, int value){
    	ContentValues values = new ContentValues();
    	values.put(KEY_ACTIVE, value); 
    	this.getWritableDatabase().update(TABLE_WORDS, values, KEY_ID+"=?", new String[]{String.valueOf(word.getID())});
    }
    
    private ArrayList<Dictionary> readDictionaryList(String selectQuery){
	    Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null);
	    ArrayList<Dictionary> dictionaryList = new ArrayList<Dictionary>();
	    if (cursor.moveToFirst()) {
	    	do {
		        Dictionary dictionary = new Dictionary();
		        dictionary.setID(cursor.getInt(0));
		        dictionary.setDictionaryName(cursor.getString(1));
		        dictionaryList.add(dictionary);
		        } while (cursor.moveToNext());
	    }
	    return dictionaryList;
    }
    
    private ArrayList<Word> readWordList(String selectQuery){
    	Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null);
    	ArrayList<Word> wordList = new ArrayList<Word>();
        if (cursor.moveToFirst()) {
            do {
                Word word = new Word();
                word.setID(cursor.getInt(0));
                word.setWordName(cursor.getString(1));
                word.setDictionaryId(cursor.getInt(2));
                word.setRightCount(cursor.getInt(3));
                word.setWrongCount(cursor.getInt(4));
                word.setActive(cursor.getInt(5));
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        return wordList;
    }
    
    private ArrayList<Translation> readTranslationList(String selectQuery){
    	Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null);
		ArrayList<Translation> translationList = new ArrayList<Translation>();
	    if (cursor.moveToFirst()) {
	    	do {
		        Translation translation = new Translation();
		        translation.setID(cursor.getInt(0));
		        translation.setWordId(cursor.getInt(1));
		        translation.setTranslationName(cursor.getString(2));
		        translationList.add(translation);
		    } while (cursor.moveToNext());
	    }
	    return translationList;
    }

    private ArrayList<String> readStringList(String selectQuery){
    	Cursor cursor = this.getWritableDatabase().rawQuery(selectQuery, null);
		ArrayList<String> stringList = new ArrayList<String>();
	    if (cursor.moveToFirst()) {
	    	do {
		        stringList.add(cursor.getString(0));
		    } while (cursor.moveToNext());
	    }
	    return stringList;
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
 
        // Create tables again
        onCreate(db);
    }
}