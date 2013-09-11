package martinezmencias.app.wordbag.ui;

import org.w3c.dom.Text;

import martinezmencias.app.wordbag.R;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class Main extends FragmentActivity {
	
	private DatabaseHandler db;
	private int dictionaryIdPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = new DatabaseHandler(this);
		
		Util.setDefaultFont(R.id.header_title, this);
		Util.setDefaultFontBold(R.id.navigation_tab_selector_1_text, this);
		Util.setDefaultFontBold(R.id.navigation_tab_selector_2_text, this);
		Util.setDefaultFontBold(R.id.navigation_tab_selector_3_text, this);
		findViewById(R.id.navigation_tab_1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToAdd(-1);
			}
		});
		findViewById(R.id.navigation_tab_2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToList();
			}
		});
		findViewById(R.id.navigation_tab_3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToTest();
			}
		});
		
		updateDictionaryPreference();
		
		if(dictionaryIdPreference > -1 ){
			goToTest();
		} else {
			goToList();
		}
	}
	
	private void startFragment(Fragment fragment){
	  	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragment);
		transaction.commit();
	}
	
	public void goToAdd(int id) {
		findViewById(R.id.navigation_tab_selector_1).setVisibility(View.VISIBLE);
		findViewById(R.id.navigation_tab_selector_2).setVisibility(View.GONE);
		findViewById(R.id.navigation_tab_selector_3).setVisibility(View.GONE);
		findViewById(R.id.alphabet_button).setVisibility(View.GONE);
		Bundle arguments = new Bundle();
		arguments.putInt("id", id);
		Fragment addFragment = new AddFragment();
		addFragment.setArguments(arguments);
		startFragment(addFragment);
	}
	
	public void goToList() {
		findViewById(R.id.navigation_tab_selector_1).setVisibility(View.GONE);
		findViewById(R.id.navigation_tab_selector_2).setVisibility(View.VISIBLE);
		findViewById(R.id.navigation_tab_selector_3).setVisibility(View.GONE);
		startFragment(new ListFragment());
	}
	
	public void goToTest() {
		findViewById(R.id.navigation_tab_selector_1).setVisibility(View.GONE);
		findViewById(R.id.navigation_tab_selector_2).setVisibility(View.GONE);
		findViewById(R.id.navigation_tab_selector_3).setVisibility(View.VISIBLE);
		findViewById(R.id.alphabet_button).setVisibility(View.GONE);
		startFragment(new TestFragment());
	}
	
	public void updateDictionaryPreference() {
		dictionaryIdPreference = Util.getDictionaryIdPreference(this);
		if(dictionaryIdPreference > -1){
			((TextView)findViewById(R.id.header_title)).setText(db.getDictionaryNameById(dictionaryIdPreference));
		}
	}

}
