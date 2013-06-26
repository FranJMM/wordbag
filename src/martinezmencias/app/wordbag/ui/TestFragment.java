package martinezmencias.app.wordbag.ui;

import java.util.ArrayList;

import martinezmencias.app.wordbag.R;
import martinezmencias.app.wordbag.R.layout;
import martinezmencias.app.wordbag.database.data.Word;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class TestFragment extends BaseFragment { 
	
	private String Tag = "Test";
	private DatabaseHandler db;
	private int dictionaryIdPreference;
	
	private Word word;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
	}

	@Override
	public void onStart(){
		super.onStart();
		db = new DatabaseHandler(getActivity());
		dictionaryIdPreference = Util.getDictionaryIdPreference(this.getActivity());
		setLayout();
	}
	
	private void setLayout(){
		if(dictionaryIdPreference > -1){
			find(R.id.testLayout).setVisibility(View.VISIBLE);
			find(R.id.testNoDictionariesMessage).setVisibility(View.GONE);
			find(R.id.testNoWordsMessage).setVisibility(View.GONE);
			Util.setDefaultFont(R.id.check, getActivity());
			ArrayList<Word> words = db.getAllActiveWordsFromDictionary(dictionaryIdPreference);
			if(words.size() > 0){
				word = words.get((int)(Math.random()*words.size()));
				TextView question = (TextView) find(R.id.question);
				question.setText(word.getWordName());
				Util.setFont(Util.DEFAULT_FONT_SERIF_BOLD, question, getActivity().getBaseContext());
				EditText answer = (EditText) find(R.id.answer);
				Util.setFont(Util.DEFAULT_FONT_SERIF_BOLD, answer, getActivity().getBaseContext());
				answer.requestFocus();
				showKeyboard(answer);
				find(R.id.check).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						check();
					}
				});
				answer.setOnEditorActionListener(new OnEditorActionListener() {
				    @Override
				    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				        if (actionId == EditorInfo.IME_ACTION_DONE) {
				        	check();
				            return true;
				        }
				        else {
				            return false;
				        }
				    }
				});
			}else{
				find(R.id.testLayout).setVisibility(View.GONE);
				find(R.id.testNoDictionariesMessage).setVisibility(View.GONE);
				find(R.id.testNoWordsMessage).setVisibility(View.VISIBLE);
			}
		}else{
			find(R.id.testLayout).setVisibility(View.GONE);
			find(R.id.testNoDictionariesMessage).setVisibility(View.VISIBLE);
			find(R.id.testNoWordsMessage).setVisibility(View.GONE);
		}
	}
	
	public void check(){
		String answer = ((TextView)(find(R.id.answer))).getText().toString();
		boolean result = db.checkTranslation(word, answer);
		String text = result ? getString(R.string.toast_test_success) : getString(R.string.toast_test_fail);
		Toast.makeText(getActivity().getBaseContext(), text, Toast.LENGTH_SHORT).show();
		getMainActivity().goToTest();	
	}

}
