package martinezmencias.app.wordbag.ui;

import martinezmencias.app.wordbag2.R;
import martinezmencias.app.wordbag.database.handler.DatabaseHandler;
import martinezmencias.app.wordbag.util.Util;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class BaseFragment extends Fragment { 
	
	protected View find(int id){
		return getActivity().findViewById(id);
	}
	
	protected View find(ViewGroup viewGroup, int id){
		return viewGroup.findViewById(id);
	}
	
   @Override
    public void onStart(){
        super.onStart();
        find(R.id.loading).setVisibility(View.INVISIBLE);
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
	
	protected MainActivity getMainActivity(){
		return (MainActivity)getActivity();
	}
}
