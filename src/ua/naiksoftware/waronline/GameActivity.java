package ua.naiksoftware.waronline;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.view.*;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ua.naiksoftware.utils.bind.ParcelableBinder;
import ua.naiksoftware.widget.IconSpinnerAdapter;

public class GameActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final String tag = GameActivity.class.getName();

    private static GameManager gameManager;
    private Spinner gameMenu;
    private GameView gameView;
    private SharedPreferences prefs;

    // init from start game menu
    public static void init(GameManager manager) {
        gameManager = manager;
        GameView.preInit(manager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameManager = (GameManager) ((ParcelableBinder)getIntent().getParcelableExtra(MainActivity.PARCELABLE_BINDER)).getObj();
        gameManager.setHoldActivity(this);
        ContextHolder.setContext(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(SettingsActivity.FULLSCREEN, false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        gameManager.setSoundEffects(prefs.getBoolean(SettingsActivity.SOUND_EFFECTS, true));
        setContentView(R.layout.game_layout);
        gameMenu = (Spinner) findViewById(R.id.spinnerGameMenu);
        gameMenu.setOnItemSelectedListener(this);
        gameMenu.setBackgroundDrawable(null);// clear default drawable
        gameMenu.setPadding(0, 0, 0, 0);
        IconSpinnerAdapter adapter = new IconSpinnerAdapter(getResources().getStringArray(R.array.game_menu_arr), this);
        gameMenu.setAdapter(adapter);
        gameView = (GameView) findViewById(R.id.gameView);
        gameManager.onGameStarted(gameView, (TextView) findViewById(R.id.textView_toast));
    }

    // select next unit for current gamer
    public void clickNext(View v) {
        gameManager.selectNextUnit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 1) { // end turn
            gameManager.nextGamer();
        }
        gameMenu.setSelection(0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
