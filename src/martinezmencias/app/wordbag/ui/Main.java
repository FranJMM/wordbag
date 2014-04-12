package martinezmencias.app.wordbag.ui;

import org.w3c.dom.Text;

import martinezmencias.app.wordbag.R;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends ActionBarActivity {
	
	private DatabaseHandler db;
	private int dictionaryIdPreference;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String[] menuItems = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.drawer_icon, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.row_drawer_item, menuItems));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

		
		db = new DatabaseHandler(this);
		
		//TODO Set font of action bar and drawer list items
		/*
		Util.setDefaultFont(R.id.headerTitle, this);
		Util.setDefaultFontBold(R.id.navigation_tab_selector_1_text, this);
		Util.setDefaultFontBold(R.id.navigation_tab_selector_2_text, this);
		Util.setDefaultFontBold(R.id.navigation_tab_selector_3_text, this);
		*/
		
		updateDictionaryPreference();
		
		if(dictionaryIdPreference > -1 ){
			goToTest();
		} else {
			goToList();
		}

	}
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
	
	private void startFragment(Fragment fragment){
	    mDrawerLayout.closeDrawers();
	  	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, fragment);
		transaction.commit();
	}
	
	private void selectItem(int position) {
	    switch (position) {
        case 0:
            goToTest();
            break;
        case 1:
            goToList();
            break;
        case 2:
            goToAdd(-1);
            break;
        default:
            break;
        }
	}
	
	public void goToAdd(int id) {
//		findViewById(R.id.alphabetButton).setVisibility(View.GONE);
//		findViewById(R.id.dictionariesEditionButton).setVisibility(View.GONE);
		Bundle arguments = new Bundle();
		arguments.putInt("id", id);
		Fragment addFragment = new AddFragment();
		addFragment.setArguments(arguments);
		startFragment(addFragment);
	}
	
	public void goToList() {
//		findViewById(R.id.discardButton).setVisibility(View.GONE);
		startFragment(new ListFragment());
	}
	
	public void goToTest() {
//		findViewById(R.id.alphabetButton).setVisibility(View.GONE);
//		findViewById(R.id.dictionariesEditionButton).setVisibility(View.GONE);
//		findViewById(R.id.discardButton).setVisibility(View.GONE);
		startFragment(new TestFragment());
	}
	
	public void updateDictionaryPreference() {
		dictionaryIdPreference = Util.getDictionaryIdPreference(this);
		if(dictionaryIdPreference > -1){
//			((TextView)findViewById(R.id.headerTitle)).setText(db.getDictionaryNameById(dictionaryIdPreference));
		}
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

}
