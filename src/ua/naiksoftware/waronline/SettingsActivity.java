package ua.naiksoftware.waronline;

import android.content.*;
import android.widget.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SettingsActivity extends Activity implements OnCheckedChangeListener {

    private static final String tag = SettingsActivity.class.getName();

    private AlertDialog mapListDialog;
    private Context ctx;
    private LayoutInflater inflater;
	private CheckBox checkBoxFullscr;
	private CheckBox checkBoxSoundEf;
	private SharedPreferences prefs;
	public static final String FULLSCREEN = "fullscr";
	public static final String SOUND_EFFECTS = "soundefdects";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        ContextHolder.setContext(ctx);
        inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.settings_layout);
		checkBoxFullscr = (CheckBox)findViewById(R.id.checkBoxFullscreen);
		checkBoxFullscr.setChecked(prefs.getBoolean(FULLSCREEN, false));
		checkBoxFullscr.setOnCheckedChangeListener(this);
		checkBoxSoundEf = (CheckBox)findViewById(R.id.checkBoxSoundEffects);
		checkBoxSoundEf.setChecked(prefs.getBoolean(SOUND_EFFECTS, true));
		checkBoxSoundEf.setOnCheckedChangeListener(this);
    }

	@Override
	public void onCheckedChanged(CompoundButton btn, boolean check) {
		SharedPreferences.Editor editor = prefs.edit();
		String param = null;
		switch (btn.getId()) {
			case R.id.checkBoxFullscreen:
				param = FULLSCREEN;
				break;
			case R.id.checkBoxSoundEffects:
				param = SOUND_EFFECTS;
				break;
		}
		editor.putBoolean(param, check).commit();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // for reload map list
        if (mapListDialog != null) {
            mapListDialog.dismiss();
            mapListDialog = null;
        }
    }



    // system call
    public void editLevel(View v) {
        if (mapListDialog == null) {
            MapUtil.loadMapList(new MapLoaderCompleteListener() {

					@Override
					public void complete(final ArrayList<MapListEntry> levelItems) {
						final int size = levelItems.size();
						final String[] arr = new String[size];
						for (int i = 0; i < size; i++) {
							arr[i] = levelItems.get(i).getName();
						}
						AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
						builder.setItems(arr, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface i, int pos) {
									startMapEditor(levelItems.get(pos - 1));
								}
							});
						mapListDialog = builder.create();
						ImageButton btnNewMap = new ImageButton(ctx);
						btnNewMap.setImageResource(android.R.drawable.ic_menu_add);
						btnNewMap.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View p1) {
									startMapEditor(null);
								}
							});
						mapListDialog.getListView().addHeaderView(btnNewMap);
						mapListDialog.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

								@Override
								public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, long p4) {
									showDeleteMapDialog(levelItems.get(pos - 1));
									return false;
								}
							});
						mapListDialog.show();
					}
				});
        } else {
            mapListDialog.show();
        }
    }

    private void startMapEditor(final MapListEntry mapEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View view = inflater.inflate(R.layout.start_map_editor_dialog_view, null);
        final EditText name = (EditText) view.findViewById(R.id.name_edit_map);
        final EditText width = (EditText) view.findViewById(R.id.width_edit_map);
        final EditText height = (EditText) view.findViewById(R.id.height_edit_map);
        if (mapEntry != null) {
            width.setText(String.valueOf(mapEntry.getWidth()));
            height.setText(String.valueOf(mapEntry.getHeight()));
            width.setEnabled(false);
            height.setEnabled(false);
        }
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface interf, int p2) {
					String strName = name.getText().toString();
					int w, h;
					try {
						w = Integer.parseInt(width.getText().toString());
						h = Integer.parseInt(height.getText().toString());
					} catch (NumberFormatException e) {
						w = h = 0;
					}
					if (!strName.isEmpty() && w > 9 && w < 1001 && h > 9 && h < 1001) {
						if (mapEntry != null) {
							try {
								MapEditorActivity.preLoadExistsMap(mapEntry.getTilesMap());
							} catch (IOException e) {
								throw new RuntimeException("Err in preload exists map to editor activity: " + e.getMessage(), e);
							}
						} else {
							MapEditorActivity.preLoadExistsMap(null);
						}
						Intent intent = new Intent(ctx, MapEditorActivity.class);
						intent.putExtra(MapEditorActivity.MAP_NAME_ID, strName);
						intent.putExtra(MapEditorActivity.MAP_WIDTH_ID, w);
						intent.putExtra(MapEditorActivity.MAP_HEIGHT_ID, h);
						startActivityForResult(intent, 1);
					} else {
						Toast.makeText(ctx, getString(R.string.edit_map_not_valid), Toast.LENGTH_LONG).show();
					}
				}
			});
        builder.show();
    }

    private void showDeleteMapDialog(final MapListEntry mapEntry) {
        if (!mapEntry.isRemovable()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(getString(R.string.delete_map_question) + mapEntry.getName() + "?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface p1, int p2) {
					if (new File(mapEntry.getPath()).delete()) {
						Toast.makeText(ctx, R.string.delete_map_complete, Toast.LENGTH_SHORT).show();
					}
					mapListDialog.dismiss();
					mapListDialog = null;
					editLevel(null);// for refresh map list
				}
			});
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }
}
