package in.eldhopj.chitchat.others;

import android.content.Context;
import android.content.SharedPreferences;

import in.eldhopj.chitchat.ModelClass.AccountSettings;

/**SharedPrefsManager is to save an retrieve data from shared prefs*/

public class SharedPrefsManager {
    private static SharedPrefsManager mInstance;
    public static final String SHARED_PREF_NAME = "my_shared_prefs";
    private Context mCtx;

    public SharedPrefsManager(Context mCtx) { // constructor
        this.mCtx = mCtx;
    }

    // synchronised because we only wants a single instance, and return an instance of this class
    public static synchronized SharedPrefsManager getInstance(Context mCtx) {
        if (mInstance == null) { // Object is not yet created
            mInstance = new SharedPrefsManager(mCtx); // instance created
        }
        return mInstance;
    }

    //Saving status and name
    public void saveSettings(AccountSettings settings){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("status",settings.getStatus());
        editor.putString("name",settings.getName());
        editor.apply();
    }

    //Clearing users prefs when logged out
    public void clear() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}
