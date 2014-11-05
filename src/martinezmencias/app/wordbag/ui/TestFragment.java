package martinezmencias.app.wordbag.ui;

import java.util.ArrayList;

import martinezmencias.app.wordbag.R;
import martinezmencias.app.wordbag.R.layout;
import martinezmencias.app.wordbag.database.data.Translation;
import martinezmencias.app.wordbag.database.data.Word;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class TestFragment extends BaseFragment { 
	
	private String Tag = "Test";
	
	private enum State {QUESTION, RESOLVED};
	
	private DatabaseHandler db;
	private int dictionaryIdPreference;
	private State state = State.QUESTION;
	private ArrayList<Word> words;
	private int selectedWordIndex = -1;
	
	private View questionContainerView;
	private TextView questionTextView;
	private TextView questionRightAnswersTextView;
	private View userAnswerContainer;
	private EditText userAnswerEditText;
	private Button checkButton;
	
	private Animation animationMoveIn;
	private Animation animationMoveOut;
	
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
			find(R.id.testNoDictionariesMessage).setVisibility(View.GONE);
			find(R.id.testNoWordsMessage).setVisibility(View.GONE);
			questionContainerView = find(R.id.questionContainer);
			questionTextView = (TextView) find(R.id.question);
			questionRightAnswersTextView = (TextView) find(R.id.questionRightAnswers);
			userAnswerContainer = find(R.id.testAnswerContainer);
			userAnswerEditText = (EditText) find(R.id.answer);
			checkButton = (Button) find(R.id.check);
			Util.setDefaultFont(R.id.check, getActivity());	
			Util.setFont(Util.DEFAULT_FONT_SERIF_BOLD, questionTextView, getActivity().getBaseContext());
			words = db.getAllActiveWordsFromDictionary(dictionaryIdPreference);
			if(words.size() > 0){
				Util.setFont(Util.DEFAULT_FONT_SERIF_BOLD, userAnswerEditText, getActivity().getBaseContext());
				userAnswerEditText.requestFocus();
				showKeyboard(userAnswerEditText);
				find(R.id.check).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						check();
					}
				});
				userAnswerEditText.setOnEditorActionListener(new OnEditorActionListener() {
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
				animationMoveOut = AnimationUtils.loadAnimation(getActivity(), R.anim.move_out);
				animationMoveOut.setAnimationListener(new Animation.AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						// Do nothing
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// Do nothing
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						setQuestion();
						questionContainerView.startAnimation(animationMoveIn);
					}
				});
				animationMoveIn = AnimationUtils.loadAnimation(getActivity(), R.anim.move_in);
				animationMoveIn.setAnimationListener(new Animation.AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						// Do nothing
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// Do nothing
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						userAnswerEditText.setText("");
					}
				});
				setQuestion();
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
		if (state == State.QUESTION) {
			String answer = userAnswerEditText.getText().toString();
			boolean success = db.checkTranslation(words.get(selectedWordIndex), answer);
			questionRightAnswersTextView.setVisibility(View.VISIBLE);
			checkButton.setText(getActivity().getResources().getString(R.string.next));
			if(success) {
				userAnswerContainer.setBackgroundResource(R.drawable.white_to_green_background);
			} else {
				userAnswerContainer.setBackgroundResource(R.drawable.white_to_red_background);
			}
			TransitionDrawable transition = (TransitionDrawable) userAnswerContainer.getBackground();
			transition.startTransition(200);
			state = State.RESOLVED;
		} else if (state == State.RESOLVED){
			state = State.QUESTION;
			questionContainerView.startAnimation(animationMoveOut);
		}
	}
	
	public void setQuestion() {
		selectedWordIndex = (int)(Math.random()*words.size());
		questionTextView.setText(words.get(selectedWordIndex).getWordName());
		ArrayList<Translation> translations = db.getAllTranslationsFromWord(words.get(selectedWordIndex).getID());
		questionRightAnswersTextView.setText(translations.get(0).getTranslationName());
		for(int i = 1; i < translations.size(); i++){
			questionRightAnswersTextView.setText(questionRightAnswersTextView.getText() + ", "+translations.get(i).getTranslationName());
		}
		questionRightAnswersTextView.setVisibility(View.INVISIBLE);
		userAnswerContainer.setBackgroundResource(R.color.white);
		checkButton.setText(getActivity().getResources().getString(R.string.check));
	}
}
