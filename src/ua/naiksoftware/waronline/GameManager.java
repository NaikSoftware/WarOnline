/*
 * Template for Game Manager's
 */
package ua.naiksoftware.waronline;

import android.graphics.Rect;
import android.util.SparseIntArray;
import java.io.IOException;
import java.util.ArrayList;
import ua.naiksoftware.waronline.unit.Unit;
import ua.naiksoftware.waronline.unit.UnitCode;
import java.util.Random;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

/**
 *
 * @author Naik
 */
public abstract class GameManager {

    private GameView gameView;
    private GameMap gameMap;
    private final boolean three;
    private final ArrayList<Unit> units;
    private SparseIntArray unitsMap;
    private final Random rnd;
    private Gamer currentGamer, looserGamer;
	private Activity activity;
	private boolean soundEffects;

    public GameManager(MapListEntry entry, SparseIntArray unitsMap, boolean three) {
        this.three = three;
        try {
            gameMap = new GameMap(entry);
        } catch (IOException e) {
            throw new RuntimeException("Err in create game map in game manager", e);
        }
        units = new ArrayList<Unit>();
        for (Unit unit : entry.getFreeUnits()) {
            unit.setFree();
            units.add(unit);
        }
        rnd = new Random();
        this.unitsMap = unitsMap;
        int bases = three ? 4 : 3;
        for (int i = 1; i < bases; i++) {
            Rect base = entry.getBaseCoords(i);
            Gamer gamer = i == 1 ? Gamer.ONE : i == 2 ? Gamer.TWO : Gamer.THREE;
            createUnits(UnitCode.ING_AVTO, base, gamer);
            createUnits(UnitCode.SOLDIER, base, gamer);
            createUnits(UnitCode.HORSE, base, gamer);
            createUnits(UnitCode.HOTCHKISS, base, gamer);
            createUnits(UnitCode.T34_85, base, gamer);
            createUnits(UnitCode.PANZER, base, gamer);
            createUnits(UnitCode.TIGER, base, gamer);
            createUnits(UnitCode.ARTILLERY, base, gamer);
        }
        this.unitsMap = null;
        currentGamer = Gamer.ONE;
        GameView.preInit(this);
    }

    private void createUnits(int id, Rect base, Gamer gamer) {
        int count = unitsMap.get(id, 0);
        for (int i = 0; i < count; i++) {
            rework:
            while (true) {
                int x = rnd.nextInt(base.right - base.left) + base.left;
                int y = rnd.nextInt(base.bottom - base.top) + base.top;
                for (Unit unit : units) {
                    if (unit.getX() == x && unit.getY() == y) {
                        continue rework;
                    }
                }
                Unit u = new Unit(id, x, y);
                u.setGamer(gamer);
                units.add(u);
                break;
            }
        }
    }

    public boolean haveThreeGamers() {
        return three;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

	// called when click "End turn" in game activity
    // override this method for online game mode
    public void nextGamer() {
        if (gameView == null || gameView.getBlocked()) {
            return;
        }
        if (currentGamer == Gamer.ONE) {
            currentGamer = looserGamer == Gamer.TWO ? Gamer.THREE: Gamer.TWO;
        } else if (currentGamer == Gamer.TWO) {
            if (three) {
                currentGamer = looserGamer == Gamer.THREE ? Gamer.ONE: Gamer.THREE;
            } else {
                currentGamer = Gamer.ONE;
            }
        } else {
            currentGamer = looserGamer == Gamer.ONE ? Gamer.TWO: Gamer.ONE;
        }
        gameView.setCurrentGamer(currentGamer);
    }

    public Gamer getCurrentGamer() {
        return currentGamer;
    }

    public void selectNextUnit() {
        if (gameView != null) {
            gameView.selectNextUnit();
        }
    }

    public void onGameStarted(GameView view, TextView toastView) {
        gameView = view;
        gameView.setToastView(toastView);
    }

	public void win(Gamer gamer) {
		Toast.makeText(ContextHolder.getContext(), "Gamer " + gamer + " win!", Toast.LENGTH_SHORT).show();
		activity.finish();
	}

	public void lose(Gamer gamer) {
		looserGamer = gamer;
		Toast.makeText(ContextHolder.getContext(), "Gamer " + gamer + " loser", Toast.LENGTH_SHORT).show();
	}
	
	public void setHoldActivity(Activity a) {
		activity = a;
	}
	
	public void setSoundEffects(boolean b) {
		soundEffects = b;
	}
	
	public boolean haveSoundEffects() {
		return soundEffects;
	}
}
