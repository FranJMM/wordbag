package martinezmencias.app.wordbag.ui;

import java.util.ArrayList;

import martinezmencias.app.wordbag.R;
import martinezmencias.app.wordbag.R.layout;
import martinezmencias.app.wordbag.database.data.Translation;
import martinezmencias.app.wordbag.database.data.Word;
import martinezmencias.app.wordbag.database.data.WordWithTranslations;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class AddFragment extends BaseFragment {

	private DatabaseHandler db;
	private int dictionaryIdPreference;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		db = new DatabaseHandler(getActivity());
		dictionaryIdPreference = Util.getDictionaryIdPreference(getActivity());
		setLayout();
	}
	
	public void setLayout(){
		if(dictionaryIdPreference > -1 ){
			find(R.id.addNoDictionariesMessage).setVisibility(View.GONE);
			find(R.id.scrollView).setVisibility(View.VISIBLE);
			int id = getArguments().getInt("id",-1);
			if(id > -1){
				WordWithTranslations wordWithTranslations = db.getWordWithTranslationsById(id);
				((EditText)find(R.id.word)).setText(wordWithTranslations.getWord().getWordName());
				for(int i=0; i < wordWithTranslations.getTranslations().size(); i++){
					addTranslation(wordWithTranslations.getTranslations().get(i).getTranslationName());
				}
				((Button)find(R.id.addWordWithTranslations)).setText("Update");
				find(R.id.discardButton).setVisibility(View.VISIBLE);
			}
			Util.setDefaultFontBold((TextView)find(R.id.wordTitle), getActivity().getBaseContext());
			Util.setDefaultFont((TextView)find(R.id.word), getActivity().getBaseContext());
			Util.setDefaultFontBold((TextView)find(R.id.translationsTitle), getActivity().getBaseContext());
			Util.setDefaultFont((TextView)find(R.id.translation), getActivity().getBaseContext());
			//Util.setDefaultFont((TextView)find(R.id.addTranslation), getActivity().getBaseContext());
			Util.setDefaultFont((TextView)find(R.id.addWordWithTranslations), getActivity().getBaseContext());
			find(R.id.addTranslation).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					addTranslationFromInput();
				}
			});
			find(R.id.addWordWithTranslations).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					addWordWithTranslations();
				}
			});
			find(R.id.discardButton).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clearAddForm();
				}
			});
			((EditText)find(R.id.translation)).setOnEditorActionListener(new OnEditorActionListener() {
			    @Override
			    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			        if (actionId == EditorInfo.IME_ACTION_DONE) {
			        	addWordWithTranslations();
			            return true;
			        }
			        else {
			            return false;
			        }
			    }
			});
			((EditText)find(R.id.word)).addTextChangedListener(new TextWatcher() {	
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					setDiscardButton();
				}
				@Override
				public void afterTextChanged(Editable s) {}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			});
			((EditText)find(R.id.translation)).addTextChangedListener(new TextWatcher() {	
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					setDiscardButton();
				}
				@Override
				public void afterTextChanged(Editable s) {}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			});
		}else{
			find(R.id.addNoDictionariesMessage).setVisibility(View.VISIBLE);
			find(R.id.scrollView).setVisibility(View.GONE);
		}
	}
	
	public void addTranslation(String translation){
		View addedTranslation = getActivity().getLayoutInflater().inflate(R.layout.added_translation, null);
		TextView addedTranslationText = (TextView)addedTranslation.findViewById(R.id.addedTranslationText);
		addedTranslationText.setText(translation);
		Util.setDefaultFont(addedTranslationText, getActivity().getBaseContext());
		addedTranslation.findViewById(R.id.removeTranslationButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				removeTranslation(v);
			}
		});
		((LinearLayout)find(R.id.added_translations)).addView(addedTranslation);
	}

	public void addTranslationFromInput(){
		TextView translationView = ((TextView)find(R.id.translation));
		String translation = translationView.getText().toString();
		translationView.setText("");
		addTranslation(translation);
		find(R.id.discardButton).setVisibility(View.VISIBLE);
		final ScrollView scrollView = (ScrollView)find(R.id.scrollView);
		scrollView.post(new Runnable() {
		    @Override
		    public void run() {
		        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
		    }
		});
	}
	
	public void removeTranslation(View v){
		((LinearLayout)v.getParent().getParent()).removeView((View)v.getParent());
		setDiscardButton();
	}
	
	public void addWordWithTranslations(){

		int id = getArguments().getInt("id",-1);
		String wordName = ((TextView)find(R.id.word)).getText().toString();
		if(!wordName.equals("")){
			WordWithTranslations wordWithTranslations = new WordWithTranslations();
			wordWithTranslations.setWord(new Word(wordName, Util.getDictionaryIdPreference(getActivity()), 1));
			wordWithTranslations.getWord().setDictionaryId(dictionaryIdPreference);
			if(id > -1){
				WordWithTranslations oldWordWithTranslations = db.getWordWithTranslationsById(id);
				wordWithTranslations.getWord().setRightCount(oldWordWithTranslations.getWord().getRightCount());
				wordWithTranslations.getWord().setWrongCount(oldWordWithTranslations.getWord().getWrongCount());
			}
		
			int numTranslations = ((LinearLayout)find(R.id.added_translations)).getChildCount();
			if(numTranslations > 0){
				for(int i = 0; i < numTranslations; i++){
					TextView translation = ((TextView)((LinearLayout)find(R.id.added_translations)).getChildAt(i).findViewById(R.id.addedTranslationText));
					wordWithTranslations.getTranslations().add(new Translation(translation.getText().toString()));
				}
				//Delete preexisting word (if exists)
				if(id > -1){
					db.deleteWord(id);
				}
				//Insert new word
				db.addWordWithTranslations(wordWithTranslations);
				Toast.makeText(getActivity().getBaseContext(), getString(R.string.toast_added_word, wordWithTranslations.getWord().getWordName()), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity().getBaseContext(), getString(R.string.toast_add_no_translations), Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getActivity().getBaseContext(), getString(R.string.toast_add_no_word), Toast.LENGTH_LONG).show();
		}
	}
	
	public void setDiscardButton() {
		String word = ((TextView)find(R.id.word)).getText().toString();
		String translation = ((TextView)find(R.id.translation)).getText().toString();
		int numTranslations = ((LinearLayout)find(R.id.added_translations)).getChildCount();
		if(word.length() == 0 && translation.length() == 0 && numTranslations == 0) {
			find(R.id.discardButton).setVisibility(View.GONE);
		} else {
			find(R.id.discardButton).setVisibility(View.VISIBLE);
		}
	}
	
	public void clearAddForm() {
		((TextView)find(R.id.word)).setText("");
		find(R.id.word).requestFocus();
		((TextView)find(R.id.translation)).setText("");
		((LinearLayout)find(R.id.added_translations)).removeAllViews();
		find(R.id.discardButton).setVisibility(View.GONE);
	}
}
