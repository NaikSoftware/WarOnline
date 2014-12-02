package ua.naiksoftware.waronline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import ua.naiksoftware.waronline.unit.Unit;

public class MapEditorActivity extends Activity {

    private static final String tag = MapEditorActivity.class.getName();

    public static final String MAP_NAME_ID = "name_id";
    public static final String MAP_WIDTH_ID = "width_id";
    public static final String MAP_HEIGHT_ID = "height_id";

    private String mapName;
    private int mapWTiles, mapHTiles;
    private static boolean preload;

    private MapEditorView mapEditorView;
    private MapEditorSelectView selectTileView;

    private TextView twInfo;
    public static final int STAGE_TILES = 0;
    public static final int STAGE_BASE1 = 1;
    public static final int STAGE_BASE2 = 2;
    public static final int STAGE_BASE3 = 3;
    public static final int STAGE_UNITS = 4;
    private int stage;

    private long timePressBack;
    private static final int DELAY_PRESS_BACK = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(tag, "________EDITOR_STARTED_______");
        ContextHolder.setContext(this);
        timePressBack = 0;
        Intent intent = getIntent();
        mapName = intent.getStringExtra(MAP_NAME_ID);
        if (mapName == null || mapName.equals("")) {
            mapName = "DefaultMapName";
        }
        mapWTiles = intent.getIntExtra(MAP_WIDTH_ID, 100);
        mapHTiles = intent.getIntExtra(MAP_HEIGHT_ID, 100);
        if (!preload) {
            Tile[][] newTiles = new Tile[mapWTiles][mapHTiles];
            for (int i = 0; i < mapWTiles; i++) {
                for (int j = 0; j < mapHTiles; j++) {
                    newTiles[i][j] = new Tile(TileCode.GRASS);
                }
            }
            MapEditorView.setMapTiles(newTiles);
        }
        setContentView(R.layout.editor_layout);
        mapEditorView = (MapEditorView) findViewById(R.id.map_editor_view);
        selectTileView = (MapEditorSelectView) findViewById(R.id.select_view);
        selectTileView.setOnSelectListener(new MapEditorSelectView.OnSelectListener() {

				@Override
				public void onSelectTile(Tile tile) {
					mapEditorView.changeTile(tile);
				}

				@Override
				public void onSelectUnit(Unit unit) {
					mapEditorView.changeUnit(unit);
				}
			});
        twInfo = (TextView) findViewById(R.id.textViewEditor);

        stage = STAGE_TILES;
        selectStage();
    }

    private void selectStage() {
        switch (stage) {
            case STAGE_TILES:
                selectTileView.viewTiles();
                selectTileView.setVisibility(View.VISIBLE);
                twInfo.setText(R.string.tile_inf);
                break;
            case STAGE_BASE1:
                twInfo.setText(R.string.base1_inf);
                break;
            case STAGE_BASE2:
                twInfo.setText(R.string.base2_inf);
                break;
            case STAGE_BASE3:
                twInfo.setText(R.string.base3_inf);
                break;
            case STAGE_UNITS:
                twInfo.setText(R.string.units_inf);
                selectTileView.viewUnits();
                selectTileView.setVisibility(View.VISIBLE);
        }
        if (stage == STAGE_BASE1 || stage == STAGE_BASE2 || stage == STAGE_BASE3) {
            selectTileView.setVisibility(View.GONE);
        }
        mapEditorView.setStage(stage);
    }

    public void clickBack(View v) {
        if (stage == STAGE_TILES) {
            // show exit dialog
            onBackPressed();
        } else {
            stage--;
            selectStage();
        }
    }

    public void clickNext(View v) {
        if (stage == STAGE_UNITS) {
            // show save dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.save_map_prompt);
            builder.setNegativeButton(getString(android.R.string.cancel), null);
            builder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						saveMap();
					}
				});
            builder.show();
        } else {
            stage++;
            selectStage();
        }
    }

    @Override
    public void onBackPressed() {
        long time = System.currentTimeMillis();
        if (time - timePressBack < DELAY_PRESS_BACK) {
            finish();
        } else {
            timePressBack = time;
            Toast.makeText(this, getString(R.string.exit_prompt), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMap() {
        int baseSize = MapEditorView.BASE_SIZE;
        Rect rect;
        rect = mapEditorView.getBase(1);
        final Rect base1 = new Rect(rect.left / Tile.TILE_SIZE, rect.top / Tile.TILE_SIZE,
									rect.left / Tile.TILE_SIZE + baseSize, rect.top / Tile.TILE_SIZE + baseSize);
        rect = mapEditorView.getBase(2);
        final Rect base2 = new Rect(rect.left / Tile.TILE_SIZE, rect.top / Tile.TILE_SIZE,
									rect.left / Tile.TILE_SIZE + baseSize, rect.top / Tile.TILE_SIZE + baseSize);
        rect = mapEditorView.getBase(3);
        final Rect base3 = new Rect(rect.left / Tile.TILE_SIZE, rect.top / Tile.TILE_SIZE,
									rect.left / Tile.TILE_SIZE + baseSize, rect.top / Tile.TILE_SIZE + baseSize);
        Rect map = new Rect(0, 0, mapWTiles, mapHTiles);
        if (!map.contains(base1) || !map.contains(base2) || !map.contains(base3)) {
            Toast.makeText(this, getString(R.string.no_base_selected), Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {

				public void run() {
					File file = new File(MapEditorActivity.this.getApplicationInfo().dataDir + "/maps/" + System.currentTimeMillis() + ".dat");
					try {
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
						dos.writeUTF(mapName);

						dos.writeInt(mapWTiles);
						dos.writeInt(mapHTiles);

						dos.writeInt(base1.left);
						dos.writeInt(base1.top);
						dos.writeInt(base1.right);
						dos.writeInt(base1.bottom);

						dos.writeInt(base2.left);
						dos.writeInt(base2.top);
						dos.writeInt(base2.right);
						dos.writeInt(base2.bottom);

						dos.writeInt(base3.left);
						dos.writeInt(base3.top);
						dos.writeInt(base3.right);
						dos.writeInt(base3.bottom);

						ArrayList<Unit> freeUnits = mapEditorView.getFreeUnits();
						dos.writeInt(freeUnits.size());
						for (Unit unit : freeUnits) {
							dos.writeInt(unit.getCode());
							dos.writeInt(unit.getX());
							dos.writeInt(unit.getY());
						}
						Tile[][] tiles = mapEditorView.getTiles();
						for (Tile[] arr : tiles) {
							for (Tile tile : arr) {
								dos.writeInt(tile.getCode());
							}
						}
						handlerSave.sendEmptyMessage(0);
					} catch (FileNotFoundException ex) {
						Log.d(tag, ex.getMessage());
						handlerSave.sendEmptyMessage(1);
					} catch (IOException ex) {
						Log.d(tag, ex.getMessage());
						handlerSave.sendEmptyMessage(1);
					}
				}
			}).start();
    }

    Handler handlerSave = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(MapEditorActivity.this, getString(R.string.map_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapEditorActivity.this, getString(android.R.string.VideoView_error_title), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    };

    public static void preLoadExistsMap(Tile[][] tilesArr) {
		if (tilesArr != null) {
			preload = true;
			MapEditorView.setMapTiles(tilesArr);
		} else {
			preload = false;
		}
    }
}
