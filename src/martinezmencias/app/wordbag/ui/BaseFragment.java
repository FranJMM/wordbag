package martinezmencias.app.wordbag.ui;

import martinezmencias.app.wordbag.R;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BaseFragment extends Fragment { 
	
	protected View find(int id){
		return getActivity().findViewById(id);
	}
	
	@Override
	public void onStop(){
		hideKeyboard();
		super.onStop();
	}
	
	protected void showKeyboard(View v){
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, 0);
	}
	
	protected void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		View currentFocus = this.getActivity().getCurrentFocus();
		if(currentFocus != null){
			imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
		}
	}
	
	protected Main getMainActivity(){
		return (Main)getActivity();
	}
}
