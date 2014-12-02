package ua.naiksoftware.waronline;

import android.widget.*;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import filelog.Log;

import java.io.File;
import java.util.ArrayList;

import ua.naiksoftware.utils.bind.ParcelableBinder;
import ua.naiksoftware.waronline.unit.UnitCode;

/**
 * @author Naik
 */
public class MainActivity extends ListActivity {

    private static final String tag = MainActivity.class.getName();

    public static final String PARCELABLE_BINDER = "binder";

    private Context ctx;
    private Spinner mapList;
    private AlertDialog dialogStartGame;
    private RadioButton radioTwo, radioThree;
    private View dialogView;
    private GameManager gameManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "_______APP_STARTED________");
        ctx = this;
        ContextHolder.setContext(ctx);
        File f = new File(getApplicationInfo().dataDir, "/maps/");
        f.mkdir();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.start_game_dialog_view, null);
        mapList = (Spinner) dialogView.findViewById(R.id.spinnerMaps);
        mapList.setPromptId(R.string.select_map);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setView(dialogView);
        dialogStartGame = builder.create();
        radioTwo = (RadioButton) dialogView.findViewById(R.id.radioButtonTwo);
        radioThree = (RadioButton) dialogView.findViewById(R.id.radioButtonThree);
        radioTwo.setChecked(true);
        setContentView(R.layout.main_list_layout);
        setListAdapter(new ArrayAdapter<String>(ctx, R.layout.list_item, getResources().getStringArray(R.array.main_menu_arr)));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        switch (position) {
            case 0:// Pass-and-Play
                showStartGameDialog();
                break;
            case 1:// Play Online
                break;
            case 2:// Settings
                startActivityForResult(new Intent(ctx, SettingsActivity.class), 1);
                break;
            case 3:// Info
                startActivityForResult(new Intent(ctx, InfoActivity.class), 1);
                break;
        }
    }

    private void showStartGameDialog() {
        MapUtil.loadMapList(new MapLoaderCompleteListener() {

            int selId;

            @Override
            public void complete(final ArrayList<MapListEntry> levelItems) {
                final int size = levelItems.size();
                final String[] arr = new String[size];
                for (int i = 0; i < size; i++) {
                    arr[i] = levelItems.get(i).getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, arr);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mapList.setAdapter(adapter);
                mapList.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selId = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
                dialogStartGame.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (levelItems.size() < 1) {
                            Toast.makeText(MainActivity.this, R.string.maps_not_found, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SparseIntArray arr = new SparseIntArray();
                        try {
                            int max = 9; // hardcoded temporiary (hm.. forever?)
                            int avto = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twAvtoCount)).getText().toString()));
                            int soldier = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twSoldierCount)).getText().toString()));
                            int horse = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twHorseCount)).getText().toString()));
                            int hotchkiss = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twHotchkissCount)).getText().toString()));
                            int t34_85 = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twT34_85Count)).getText().toString()));
                            int panzer = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twPanzerCount)).getText().toString()));
                            int tiger = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twTigerCount)).getText().toString()));
                            int artilerry = Math.abs(Integer.parseInt("0" + ((EditText) dialogView.findViewById(R.id.twArtilleryCount)).getText().toString()));
                            int all = avto + soldier + horse + hotchkiss + t34_85 + panzer + tiger + artilerry;
                            if (all > max) {
                                throw new Exception(getString(R.string.max_units_limit));
                            }
                            arr.append(UnitCode.ING_AVTO, avto);
                            arr.append(UnitCode.SOLDIER, soldier);
                            arr.append(UnitCode.HORSE, horse);
                            arr.append(UnitCode.HOTCHKISS, hotchkiss);
                            arr.append(UnitCode.T34_85, t34_85);
                            arr.append(UnitCode.PANZER, panzer);
                            arr.append(UnitCode.TIGER, tiger);
                            arr.append(UnitCode.ARTILLERY, artilerry);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        GameManager gm = new GameManagerPAP(levelItems.get(selId), arr, radioThree.isChecked());
                        Intent i = new Intent(ctx, GameActivity.class);
                            i.putExtra(PARCELABLE_BINDER, new ParcelableBinder<GameManager>(gm));
                        startActivityForResult(i, 1);
                    }
                });
                dialogStartGame.show();
            }
        });
    }
}
