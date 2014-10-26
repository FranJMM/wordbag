package martinezmencias.app.wordbag.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.widget.TextView;

public class Util {
	
	public static String DEFAULT_FONT = "fonts/Roboto-Regular.ttf";
	public static String DEFAULT_FONT_BOLD = "fonts/Roboto-Bold.ttf";
	public static String DEFAULT_FONT_SERIF_BOLD = "fonts/RobotoSlab-Bold.ttf";
	
	public static int getDictionaryIdPreference(Activity activity){
		SharedPreferences settings = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
		return settings.getInt("dictionaryId", -1);
	}
	
	public static void setDictionaryIdPreference(int id, Activity activity){
		SharedPreferences.Editor editor = activity.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
		editor.putInt("dictionaryId", id);
		editor.commit();
	}
	
	public static boolean splashHasBeenShown(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
		return settings.getBoolean("splashShown", false);
	}
	
	public static void setSplashShown(Activity activity) {
		SharedPreferences.Editor editor = activity.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
		editor.putBoolean("splashShown", true);
		editor.commit();
	}
	
	public static void setFont(String font, TextView textView, Context context){
		textView.setTypeface(Typeface.createFromAsset(context.getAssets(), font));  
	}

	public static void setFont(String font, int textView, Activity activity){
		((TextView)activity.findViewById(textView)).setTypeface(Typeface.createFromAsset(activity.getAssets(), font));  
	}
	
	public static void setFont(String font, int textView, Fragment fragment){
		setFont(font, textView, fragment.getActivity());
	}
	
	public static void setDefaultFont(TextView textView, Context context){
		setFont(DEFAULT_FONT, textView, context);
	}
	
	public static void setDefaultFont(int textView, Activity activity){
		setFont(DEFAULT_FONT, textView, activity);
	}
	
	public static void setDefaultFont(int textView, Fragment fragment){
		setFont(DEFAULT_FONT, textView, fragment);
	}
	
	public static void setDefaultFontBold(TextView textView, Context context){
		setFont(DEFAULT_FONT_BOLD, textView, context);
	}
	
	public static void setDefaultFontBold(int textView, Activity activity){
		setFont(DEFAULT_FONT_BOLD, textView, activity);
	}
	
	public static void setDefaultFontSerifBold(TextView textView, Context context){
		setFont(DEFAULT_FONT_SERIF_BOLD, textView, context);
	}

	public static void setDefaultFontSerifBold(int textView, Activity activity){
		setFont(DEFAULT_FONT_SERIF_BOLD, textView, activity);
	}

	public static void setDefaultFontBold(int textView, Fragment fragment){
		setFont(DEFAULT_FONT_BOLD, textView, fragment);
	}
}
