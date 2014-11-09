package martinezmencias.app.wordbag.ui;

import org.w3c.dom.Text;

import martinezmencias.app.wordbag2.R;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    
    private static final int FRAGMENT_TEST = 0;
    private static final int FRAGMENT_LIST = 1;
    private static final int FRAGMENT_ADD = 2;
    
	
	private DatabaseHandler db;
	//private int mDictionaryIdPreference;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private Fragment mFragment;
	private int mCurrentFragment = 0;

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
                updateActionBarTitle();
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
                // invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.row_drawer_item, menuItems));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

		db = new DatabaseHandler(this);
		
		//TODO Set font of action bar and drawer list items
		
		if(Util.getDictionaryIdPreference(this) > -1 ){
			goToTest();
		} else {
			goToList();
		}
		
		updateActionBarTitle();
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
   public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
       switch (mCurrentFragment) {
       case FRAGMENT_TEST:
            inflater.inflate(R.menu.menu_test, menu);
            break;
        case FRAGMENT_LIST:
            inflater.inflate(R.menu.menu_list, menu);
            if(db.getAllWordsFromDictionary(Util.getDictionaryIdPreference(this)).size() > 0) {
            	menu.findItem(R.id.action_alphabet).setVisible(true);
            } else {
            	menu.findItem(R.id.action_alphabet).setVisible(false);
            }
           break;
        case FRAGMENT_ADD:
            inflater.inflate(R.menu.menu_add, menu);
           break;
       default:
           break;
        }
        return super.onCreateOptionsMenu(menu);
     }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // Pass the event to ActionBarDrawerToggle, if it returns
       // true, then it has handled the app icon touch event
       if (mDrawerToggle.onOptionsItemSelected(item)) {
        return true;
       }
       // Handle presses on the action bar items
       switch (item.getItemId()) {
           case R.id.action_discard:
               ((AddFragment) mFragment).clearAddForm();
               return true;
           case R.id.action_alphabet:
               ((ListFragment) mFragment).toggleAlphabet();
               return true;
           case R.id.action_dictionaries:
               ((ListFragment) mFragment).toggleDictionariesList();
               return true;
           default:
               return super.onOptionsItemSelected(item);
        }
   }
	
	private void startFragment(Fragment fragment){
	    mFragment = fragment;
	    mDrawerLayout.closeDrawers();
	    supportInvalidateOptionsMenu();
	  	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, mFragment);
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
	    mCurrentFragment = FRAGMENT_ADD;
		Bundle arguments = new Bundle();
		arguments.putInt("id", id);
		Fragment addFragment = new AddFragment();
		addFragment.setArguments(arguments);
		startFragment(addFragment);
	}
	
	public void goToList() {
	    mCurrentFragment = FRAGMENT_LIST;
		startFragment(new ListFragment());
	}
	
	public void goToTest() {
	    mCurrentFragment = FRAGMENT_TEST;
		startFragment(new TestFragment());
	}
	
	public void updateActionBarTitle() {
	    int dictionaryIdPreference = Util.getDictionaryIdPreference(this);
        String fragmentTitle = getResources().getStringArray(R.array.menu_items)[mCurrentFragment];
        String dictionaryPreferredName = "";
        if(dictionaryIdPreference >= 0) {
            dictionaryPreferredName = db.getDictionaryNameById(dictionaryIdPreference);
        }
        getSupportActionBar().setTitle(fragmentTitle + " " + dictionaryPreferredName);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}

}
