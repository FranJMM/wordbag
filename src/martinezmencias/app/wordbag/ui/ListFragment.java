package martinezmencias.app.wordbag.ui;

import java.util.ArrayList;
import java.util.HashMap;

import martinezmencias.app.wordbag.R;
import martinezmencias.app.wordbag.R.layout;
import martinezmencias.app.wordbag.database.data.Dictionary;
import martinezmencias.app.wordbag.database.data.Translation;
import martinezmencias.app.wordbag.database.data.Word;
import martinezmencias.app.wordbag.database.data.WordWithTranslations;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ListFragment extends BaseFragment {
	
	private DatabaseHandler db;
	private ArrayList<WordWithTranslations> words;
	private ListView list;
	private ListAdapter adapter;
	private int visibleEditWordId;
	private View lastVisibleEditWordRow;
	private SetWordListAsyncTask mSetWordListAsyncTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		db = new DatabaseHandler(getActivity());
		words = new ArrayList<WordWithTranslations>();
		visibleEditWordId = -1;
		initLayout();
		setLayout();
	}
	
   @Override
    public void onStop(){
        super.onStop();
        cancelWordListAsyncTask();
    }

	public void initLayout(){
		list = (ListView) find(R.id.list);
		if(list.getAdapter() == null) {
			list.addHeaderView(getActivity().getLayoutInflater().inflate(R.layout.list_header, null));
		}
		adapter = new ListAdapter(getActivity().getBaseContext(), 0, words);
		list.setAdapter(adapter); 
		ImageButton add = (ImageButton)find(R.id.dictionariesEdition).findViewById(R.id.addButton);
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addDictionary();
			}
		});
	}

	public void setLayout(){
		final int dictionaryPreference = Util.getDictionaryIdPreference(getActivity());
		if(dictionaryPreference > -1 ){
			find(R.id.noDictionariesMessage).setVisibility(View.GONE);
			find(R.id.noWordsMessage).setVisibility(View.GONE);
			find(R.id.firstWordMessage).setVisibility(View.GONE);
			getMainActivity().updateActionBarTitle();
			find(R.id.dictionariesEdition).setVisibility(View.GONE);
			ArrayList<Dictionary> dictionaries = db.getAllDictionaries();
			LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout dictionariesList = (LinearLayout)find(R.id.dictionariesList);
			dictionariesList.removeAllViews();
			for(int i=0; i < dictionaries.size(); i++){
				if(dictionaries.get(i).getID() != dictionaryPreference){
					LinearLayout dictionaryRow = (LinearLayout)inflater.inflate(R.layout.row_dictionary, dictionariesList, false);
					TextView dictionary = (TextView)dictionaryRow.findViewById(R.id.dictionary);
					dictionary.setText(dictionaries.get(i).getDictionaryName());
					Util.setDefaultFont(dictionary, getActivity());
					
					dictionaryRow.findViewById(R.id.dictionary).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							toggleEditDictionary(v);
						}
					});
					TextView select = (TextView)dictionaryRow.findViewById(R.id.selectDictionary);
					Util.setDefaultFont(select, getActivity());
					select.setTag(Integer.valueOf(dictionaries.get(i).getID()));
					select.findViewById(R.id.selectDictionary).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							selectDictionary(v);
						}
					});
					TextView delete = (TextView)dictionaryRow.findViewById(R.id.deleteDictionary);
					Util.setDefaultFont(delete, getActivity());
					delete.setTag(Integer.valueOf(dictionaries.get(i).getID()));
					delete.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							deleteDictionary(v);
						}
					});
					dictionariesList.addView(dictionaryRow);
				}
			}
			
			startSetWordListAsyncTask(dictionaryPreference);
			
			//Set alphabet scroll view
			/*
			if(words.size() > 0) {
				find(R.id.alphabetButton).setVisibility(View.VISIBLE);
				Util.setDefaultFontSerifBold(R.id.alphabetButton, getActivity());
				find(R.id.alphabetButton).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						find(R.id.listHeaderAlphabetContainer).setVisibility(View.VISIBLE);
						setAlphabet();
					}
				});
			} else {
				find(R.id.alphabetButton).setVisibility(View.GONE);
			}
			*/

		} else {
			//TODO No dictionary
			find(R.id.alphabetButton).setVisibility(View.GONE);
			find(R.id.dictionariesEdition).setVisibility(View.VISIBLE);
			find(R.id.noDictionariesMessage).setVisibility(View.VISIBLE);
			find(R.id.noWordsMessage).setVisibility(View.GONE);
			Util.setDefaultFont(R.id.noDictionariesMessage, this);
		}
		
		View dictionariesEdition = find(R.id.dictionariesEdition);
		((EditText)dictionariesEdition.findViewById(R.id.addDictionaryEditText)).setText("");
		((EditText)dictionariesEdition.findViewById(R.id.addDictionaryEditText)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					list.smoothScrollToPosition(0);
				}
			}
		});
	}
	
	private void startSetWordListAsyncTask(int dictionary) {
	    cancelWordListAsyncTask();
	    find(R.id.loading).setVisibility(View.VISIBLE);
	    mSetWordListAsyncTask = new SetWordListAsyncTask();
	    mSetWordListAsyncTask.execute(dictionary);
	}
	
	private void cancelWordListAsyncTask() {
	    if(mSetWordListAsyncTask != null && !mSetWordListAsyncTask.isCancelled()) {
	        mSetWordListAsyncTask.cancel(true);
	    }
	}
	
	 private class SetWordListAsyncTask extends AsyncTask<Integer, Integer, ArrayList<WordWithTranslations>> {
	     protected ArrayList<WordWithTranslations> doInBackground(Integer... dictionaryPreference) {
	         words.clear();
	         return db.getAllWordsWithTranslationsFromDictionary(dictionaryPreference[0]);
	     }
	     protected void onPostExecute(ArrayList<WordWithTranslations> wordsWithTranslations) {
	          if(wordsWithTranslations.size() > 0) {
	                if(wordsWithTranslations.size() == 1){
	                    find(R.id.firstWordMessage).setVisibility(View.VISIBLE);
	                }
	                words.addAll(wordsWithTranslations);
	            }else{
	                find(R.id.noWordsMessage).setVisibility(View.VISIBLE);
	            }
	            adapter.notifyDataSetChanged();
	            find(R.id.loading).setVisibility(View.INVISIBLE);
	     }
	 }
	
	public void toggleDictionariesList(){
		if(find(R.id.dictionariesEdition).getVisibility() == View.VISIBLE){
			find(R.id.dictionariesEdition).setVisibility(View.GONE);
		}else{
			list.setSelection(0);
			find(R.id.dictionariesEdition).setVisibility(View.VISIBLE);
		}
	}
	
	public void toggleEditDictionary(View dictionary){
		View rowDictionary = (View)dictionary.getParent();
		if(rowDictionary.findViewById(R.id.dictionaryEdition).getVisibility() == View.VISIBLE){
			rowDictionary.findViewById(R.id.dictionaryEdition).setVisibility(View.GONE);
		}else{
			rowDictionary.findViewById(R.id.dictionaryEdition).setVisibility(View.VISIBLE);
		}
	}
	
	public void toggleEditWord(View viewRowWord){
		
		int editWordVisibility = viewRowWord.findViewById(R.id.wordEdition).getVisibility();
		
		if(lastVisibleEditWordRow != null){
			lastVisibleEditWordRow.findViewById(R.id.wordEdition).setVisibility(View.GONE);
		}
		if(editWordVisibility == View.GONE){
			visibleEditWordId = (Integer) viewRowWord.getTag();
			setEditWordLayout(viewRowWord, db.getWordWithTranslationsById((Integer)viewRowWord.getTag()));
		}else{
			visibleEditWordId = -1;
		}
	}
	
	public void toggleActive(View v){
		Word word = db.getWordById((Integer)v.getTag());
		if(word.isActive()){
			db.updateWordActive(word, 0);
			((View)v.getParent()).findViewById(R.id.activate).setVisibility(View.VISIBLE);
			((View)v.getParent()).findViewById(R.id.deactivate).setVisibility(View.GONE);
		}else{
			db.updateWordActive(word, 1);
			((View)v.getParent()).findViewById(R.id.deactivate).setVisibility(View.VISIBLE);
			((View)v.getParent()).findViewById(R.id.activate).setVisibility(View.GONE);
		}
	}
	
	private void addDictionary(){
		String dictionaryName = ((EditText)find(R.id.dictionariesEdition).findViewById(R.id.addDictionaryEditText)).getText().toString();
		Dictionary dictionary = new Dictionary(dictionaryName);
		db.addDictionary(dictionary);
		Util.setDictionaryIdPreference(dictionary.getID(), getActivity());
		getMainActivity().updateActionBarTitle();
		setLayout();
		Toast.makeText(getActivity().getBaseContext(), "Dicionary added: " +dictionary.getID() + " " +dictionary.getDictionaryName(), Toast.LENGTH_SHORT).show();
	}
	
	private void selectDictionary(View v){
		Util.setDictionaryIdPreference((Integer)v.getTag(), getActivity());
		setLayout();
	}
	
	private void deleteDictionary(View v){
		db.deleteDictionary((Integer)v.getTag());
		setLayout();
	}
	
	private void editWord(View v){
		((Main)getActivity()).goToAdd((Integer)v.getTag());
	}
	
	private void deleteWord(View v){
		db.deleteWord((Integer)v.getTag());
		setLayout();
	}
	
	private void setEditWordLayout(View viewRowWord, WordWithTranslations wordWithTranslations){
		
		Word word = wordWithTranslations.getWord();
		
		TextView activate = (TextView) viewRowWord.findViewById(R.id.activate);
		TextView deactivate = (TextView) viewRowWord.findViewById(R.id.deactivate);
		TextView delete = (TextView) viewRowWord.findViewById(R.id.delete);
		TextView edit = (TextView)viewRowWord.findViewById(R.id.edit);
		
		if(word.isActive()){
			deactivate.setVisibility(View.VISIBLE);
			activate.setVisibility(View.GONE);
		}else{
			activate.setVisibility(View.VISIBLE);
			deactivate.setVisibility(View.GONE);
		}
		
		activate.setTag(Integer.valueOf(word.getID()));
		activate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleActive(v);
			}
		});
		
		deactivate.setTag(Integer.valueOf(word.getID()));
		deactivate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleActive(v);
			}
		});
		
		edit.setTag(Integer.valueOf(word.getID()));
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editWord(v);
			}
		});
		
		delete.setTag(Integer.valueOf(word.getID()));
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteWord(v);
			}
		});
		
		ArrayList<Translation> translations = wordWithTranslations.getTranslations();
		TextView translationsList = (TextView) viewRowWord.findViewById(R.id.translationsList);
		translationsList.setText(translations.get(0).getTranslationName());
		for(int i = 1; i < translations.size(); i++){
			translationsList.setText(translationsList.getText() + ", "+translations.get(i).getTranslationName());
		}
		Util.setDefaultFont(activate, getActivity().getBaseContext());
		Util.setDefaultFont(deactivate, getActivity().getBaseContext());
		Util.setDefaultFont(edit, getActivity().getBaseContext());
		Util.setDefaultFont(delete, getActivity().getBaseContext());
		Util.setDefaultFont(translationsList, getActivity().getBaseContext());
		
		lastVisibleEditWordRow = viewRowWord;
		
		viewRowWord.findViewById(R.id.wordEdition).setVisibility(View.VISIBLE);
	}
	
	public void showAlphabet() {
	    find(R.id.listHeaderAlphabetContainer).setVisibility(View.VISIBLE);
		list.setSelection(0);
		final int dictionaryPreference = Util.getDictionaryIdPreference(getActivity());
		ArrayList<String> characters = db.getDistinctInitialCharactersFromDictionary(dictionaryPreference);
		ViewGroup alphabetLayout = (ViewGroup)find(R.id.listHeaderAlphabetLayout);
		alphabetLayout.removeAllViews();
		for(int i=0; i < characters.size(); i++){
			ViewGroup characterLayout = (ViewGroup)this.getActivity().getLayoutInflater().inflate(R.layout.alphabet_list_character, null);
			Button characterView = (Button)find(characterLayout, R.id.listHeaderAlphabetCharacter);
			characterView.setText(characters.get(i));
			Util.setDefaultFontBold(characterView, getActivity().getBaseContext());
			characterView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = db.getRowNumberOfFirstCharacterAppearanceFromDictionary(((TextView)v).getText().toString(), dictionaryPreference);
					list.setSelection(position);
				}
			});
			alphabetLayout.addView(characterLayout);
		}
	}

	public class ListAdapter extends ArrayAdapter<WordWithTranslations> {
		private final Context context;
		private final ArrayList<WordWithTranslations> wordsWithTranslations;
		
		public ListAdapter(Context context, int id, ArrayList<WordWithTranslations> wordsWithTranslations) {
			super(context, id, wordsWithTranslations);
			this.context = context;
			this.wordsWithTranslations = wordsWithTranslations;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			  	
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(view == null){
			    view = inflater.inflate(R.layout.row_word, parent, false);
			}
			
			WordWithTranslations wordWithTranslations = this.wordsWithTranslations.get(position);
			Word word = wordWithTranslations.getWord();

			view.setTag(Integer.valueOf(word.getID()));
			  
			TextView wordName = (TextView)view.findViewById(R.id.wordName);
			wordName.setText(word.getWordName());
			Util.setDefaultFont(wordName, context);
			view.findViewById(R.id.word).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					toggleEditWord((View)v.getParent());
				}
			});

			TextView testCount = (TextView)view.findViewById(R.id.testCount);
			testCount.setText(word.getRightCount() + "/" +word.getWrongCount());
			Util.setDefaultFont(testCount, context);

			if(word.isActive()) {
				find((ViewGroup)view,R.id.word).setBackgroundResource(R.drawable.row_background);
				find((ViewGroup)view,R.id.rowDivider).setBackgroundColor(getResources().getColor(R.color.gray_2));
			} else {
				find((ViewGroup)view,R.id.word).setBackgroundResource(R.drawable.row_background_marked);
				find((ViewGroup)view,R.id.rowDivider).setBackgroundColor(getResources().getColor(R.color.white));
			}

			if(visibleEditWordId == word.getID()){
				setEditWordLayout(view, wordWithTranslations);
			}else{
				view.findViewById(R.id.wordEdition).setVisibility(View.GONE);
			}
		
			return view;
		}
	}
}
