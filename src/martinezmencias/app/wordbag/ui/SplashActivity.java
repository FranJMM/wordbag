package martinezmencias.app.wordbag.ui;

import org.w3c.dom.Text;

import martinezmencias.app.wordbag.R;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.app.Activity;
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

public class SplashActivity extends Activity {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(!Util.splashHasBeenShown(this)) {
			setContentView(R.layout.activity_splash);
			Util.setDefaultFont((TextView)findViewById(R.id.splashTitle), getBaseContext());
			Util.setDefaultFont((TextView)findViewById(R.id.splashWelcome), getBaseContext());
			Util.setDefaultFont((TextView)findViewById(R.id.splashClickHere), getBaseContext());
			Util.setSplashShown(this);
			findViewById(R.id.splashClickHere).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					goToMain();
				}
			});
			findViewById(R.id.splashWelcome).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					goToMain();
				}
			});
		} else {
			goToMain();
		}
	}
	
	private void goToMain() {
		Intent intent = new Intent(this, MainActivity.class);
		this.startActivity(intent);
	}

}
