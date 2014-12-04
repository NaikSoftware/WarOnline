package ua.naiksoftware.waronline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import ua.naiksoftware.waronline.unit.Anim;
import ua.naiksoftware.waronline.unit.DiedUnit;
import ua.naiksoftware.waronline.unit.Unit;
import ua.naiksoftware.waronline.unit.UnitCode;

import java.util.ArrayList;
import java.util.Collections;

public class GameView extends View {

    private static final String tag = GameView.class.getName();

    private Scroller scroller;
    private GestureDetector gestureDetector;

    // logic variables
    private static GameManager manager;
    private GameMap gameMap;
    private static final Unit.DrawSortComparator drawSortComparator = new Unit.DrawSortComparator();
    private ArrayList<Unit> units; // Здесь находятся юниты всех игроков
    private ArrayList<DiedUnit> diedUnits; // Убитые
    private Gamer currentGamer;
    private boolean show;
    private int idx1 = 0, idx2 = 0, idx3 = 0;// indexes in unit array for queue
    private boolean[] lose;
    private boolean init;
    private Path pathGoShot, pathGo;
    private Paint paintGoShot, paintGo;
    private Unit currentUnit;
    private final RectF selRect = new RectF(0, 0, Tile.TILE_SIZE, Tile.TILE_SIZE);
    private final ArrayList<Node> listClosedGoShot = new ArrayList<Node>();
    private final ArrayList<Node> listClosedGo = new ArrayList<Node>();
    private boolean blockTap;
    private Paint paintAttackRadius;
    private boolean showAttack;
    private Path pathShowAttack;
    private TextView toastText;
    private FrameLayout.LayoutParams lpMoveToast = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    private Animation animToast;
    private ArrayList<Mine> mines = new ArrayList<Mine>();
    private boolean soundEffects;
    private SoundPool soundPool;
    private static final int SOUND_POOL_MAX_STREAMS = 2;
    private int soundSelect;

    // tech variables (scroll, select, etc)
    private int w, h;
    private int scrW, scrH;
    private int scrollX, scrollY;
    private int wTiles, hTiles;

    // set used game manager for support different game modes
    public static void preInit(GameManager m) {
        manager = m;
    }

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public GameView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init(context);
    }

    private void init(Context context) {
        soundEffects = manager.haveSoundEffects();
        if (soundEffects) {
            soundPool = new SoundPool(SOUND_POOL_MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
            soundSelect = soundPool.load(context, R.raw.select, 1);
        }

        animToast = AnimationUtils.loadAnimation(context, R.anim.toast);
        animToast.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toastText.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        scroller = new Scroller(context);
        gestureDetector = new GestureDetector(context, new MyGestureListener());
        setVerticalScrollBarEnabled(true);
        setHorizontalScrollBarEnabled(true);
        gameMap = manager.getGameMap();
        wTiles = gameMap.getXTileCount();
        hTiles = gameMap.getYTileCount();
        w = wTiles * Tile.TILE_SIZE;
        h = hTiles * Tile.TILE_SIZE;
        lose = new boolean[manager.haveThreeGamers() ? 3 : 2];
        units = manager.getUnits();
        Collections.sort(units, drawSortComparator);
        diedUnits = new ArrayList<DiedUnit>();
        currentUnit = units.get(0);
        calcVisibilityForAll();
        pathShowAttack = new Path();
        pathGoShot = new Path();
        pathGo = new Path();
        paintGoShot = new Paint();
        paintGoShot.setStyle(Paint.Style.STROKE);
        paintGoShot.setStrokeWidth(1);
        paintGoShot.setColor(0x99dddd00);
        paintGo = new Paint(paintGoShot);
        paintGo.setShadowLayer(1, 1, 1, 0xff550000);
        paintGo.setColor(0xffff0000);
        paintAttackRadius = new Paint(paintGo);
        paintAttackRadius.setStrokeWidth(2);
        currentGamer = manager.getCurrentGamer();
        gameMap.setCurrentGamer(currentGamer);
        init = true;
        selectNextUnit(Gamer.ONE);
        selectNextUnit(Gamer.TWO);
        if (lose.length > 2) {
            selectNextUnit(Gamer.THREE);
        }
        init = false;
        show = true;
        selectNextUnit();// выбрать и рассчитать путь для одного из юнитов текущего игрока
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (show) {
            int startXid = scrollX / Tile.TILE_SIZE;
            int startYid = scrollY / Tile.TILE_SIZE;
            int endXid = scrW / Tile.TILE_SIZE + startXid + 2;
            int endYid = scrH / Tile.TILE_SIZE + startYid + 2;
            if (endXid > wTiles) {
                endXid = wTiles;
            }
            if (endYid > hTiles) {
                endYid = hTiles;
            }
            gameMap.draw(canvas, startXid, startYid, endXid, endYid);
            for (DiedUnit diedUnit : diedUnits) {
                if (gameMap.isVisible(diedUnit.getX(), diedUnit.getY())) {
                    diedUnit.draw(canvas);
                }
            }
            canvas.drawPath(pathGo, paintGo);
            canvas.drawPath(pathGoShot, paintGoShot);
            for (Unit unit : units) {
                if (gameMap.isVisible(unit.getX(), unit.getY())) {
                    switch (unit.draw(canvas)) {
                        case Unit.NOT_MOVE:
                            continue;// skip for iteration for skip invalidate()
                            //case Unit.MOVE: // move in process
                            //    break;
                        case Unit.MOVE_TO_NEW_CELL:
                            calcVisibility(unit);
                            unit.continueMove();
                            //haveMine(x, y);
                            break;
                        case Unit.END_MOVE: // end move
                            calcVisibility(unit);
                            calcGoing();
                            occupate(unit);
                            // сортировать юнитов здесь и при старте 1 раз
                            // для правильного наложения друг на друга полосы здоровья
                            Collections.sort(units, drawSortComparator);
                            blockTap = false;
                    }
                    postInvalidate();
                }
            }
            for (Mine mine : mines) {
                mine.draw(canvas);
            }
            if (showAttack) {
                canvas.drawPath(pathShowAttack, paintAttackRadius);
            }
        } else {
            canvas.drawColor(0);
        }
    }

    private void occupate(Unit unit) {
        int x = unit.getX();
        int y = unit.getY();
        verifyAndOccupate(x - 1, y, unit);
        verifyAndOccupate(x, y - 1, unit);
        verifyAndOccupate(x + 1, y, unit);
        verifyAndOccupate(x, y + 1, unit);
    }

    private void verifyAndOccupate(int x, int y, Unit unit) {
        Unit free = getUnit(x, y);
        if (free != null && free.isFree()) {
            free.setGamer(unit.getGamer());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //awakenScrollBars();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (showAttack) {
                showAttack = false;
                invalidate();
            }
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            scrollTo(x, y);
            if (scrollX != getScrollX() || scrollY != getScrollY()) {
                onScrollChanged(getScrollX(), getScrollY(), scrollX, scrollY);
            }
            invalidate();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        scrollX = l;
        scrollY = t;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent event) {
            if (!blockTap) {
                int rawx = (int) event.getX() + scrollX;
                int rawy = (int) event.getY() + scrollY;
                int xId = rawx / Tile.TILE_SIZE;
                int yId = rawy / Tile.TILE_SIZE;
                Unit unit = getUnit(xId, yId);
                if (unit != null && !unit.died() && !unit.isFree() && gameMap.isVisible(xId, yId)) {
                    pathShowAttack.reset();
                    pathShowAttack.addCircle(unit.getDrawX() + Tile.TILE_SIZE / 2, unit.getDrawY() + Tile.TILE_SIZE / 2, unit.getAttackRadius() * Tile.TILE_SIZE, Path.Direction.CCW);
                    for (Unit u : units) {
                        if (!u.died() && unit.getGamer() != u.getGamer() && u != unit && !u.isFree() && gameMap.isVisible(u.getX(), u.getY()) && unit.distanseTo(u.getX(), u.getY()) <= unit.getAttackRadius()) {
                            pathShowAttack.addCircle(u.getDrawX() + Tile.TILE_SIZE / 2, u.getDrawY() + Tile.TILE_SIZE / 2, Tile.TILE_SIZE / 2, Path.Direction.CCW);
                        }
                    }
                    showAttack = true;
                    invalidate();
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanseX, float distanseY) {
            if (scrollX + distanseX > 0 && scrollX + distanseX < w - getWidth()) {
                scrollBy((int) distanseX, 0);
            }
            if (scrollY + distanseY > 0 && scrollY + distanseY < h - getHeight()) {
                scrollBy(0, (int) distanseY);
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            scroller.fling(scrollX, scrollY, -(int) velocityX, -(int) velocityY,
                    0, w - scrW, 0, h - scrH);
            invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!blockTap) {
                int rawx = (int) e.getX() + scrollX;
                int rawy = (int) e.getY() + scrollY;
                int xId = rawx / Tile.TILE_SIZE;
                int yId = rawy / Tile.TILE_SIZE;
                int mapId = gameMap.getTileCode(xId, yId);
                Unit unit = getUnit(xId, yId);
                Node node = new Node(null, xId, yId, 0);
                // Ниже закоментировано для возможности открытия меню действий
                // текущего юнита, например походили инженерным авто, и хотим
                // разминировать/минировать/лечить/исправить мост.
                if (unit != null && /*unit != currentUnit &&*/ !unit.died()) {
                    if (unit.getGamer() == currentGamer) {
                        if (soundEffects) {
                            soundPool.play(soundSelect, 1, 1, 1, 0, 1);
                        }
                        currentUnit = unit;
                        if (unit.getCode() == UnitCode.ING_AVTO) {
                            Toast.makeText(ContextHolder.getContext(), "Ing select", Toast.LENGTH_SHORT).show();
                        } else {
                            calcGoing();
                        }
                    } else if (currentUnit.distanseTo(xId, yId) <= currentUnit.getAttackRadius()
                            && !unit.isFree()
                            && listClosedGoShot.contains(new Node(null, currentUnit.getX(), currentUnit.getY(), 0))
                            && gameMap.isVisible(xId, yId)) {
                        currentUnit.rotateTo(xId, yId);
                        toastText.setText(String.valueOf(unit.shot(currentUnit)));
                        if (unit.died()) {
                            die(unit);
                        } else {
                            unit.rotateTo(currentUnit.getX(), currentUnit.getY());
                        }
                        toastText.setTextColor(unit.getColor());
                        toastText.setVisibility(View.VISIBLE);
                        // API level 11..., commented
                        //toastText.setTranslationY(e.getX() - toastText.getWidth() / 2);
                        //toastText.setTranslationY(e.getY());
                        lpMoveToast.leftMargin = (int) (e.getX() - toastText.getWidth() / 2);
                        lpMoveToast.topMargin = (int) e.getY();
                        toastText.startAnimation(animToast);
                        currentUnit.changePassability(currentUnit.getPassability());
                        calcGoing();
                        gameMap.setVisible(currentUnit.getX(), currentUnit.getY(), unit.getGamer());
                    } else {
                        currentUnit.rotateTo(xId, yId);
                    }
                } else {
                    if (listClosedGo.contains(node) || listClosedGoShot.contains(node)) {
                        if (xId == currentUnit.getX() && yId == currentUnit.getY()) {
                            // нажал на уже выбранного юнита
                        } else {
                            blockTap = true;
                            goTo(xId, yId);
                        }
                    } else {
                        currentUnit.rotateTo(xId, yId);
                    }
                }
                postInvalidate();
            }
            return true;
        }
    }

    private void die(Unit unit) {
        units.remove(unit);
        diedUnits.add(new DiedUnit(unit.getX(), unit.getY(), Anim.get(unit.getCode() + UnitCode.ID_DIED)[0]));
        int count = 0;
        for (Unit u : units) {
            if (unit.getGamer() == u.getGamer()) {
                count++;
            }
        }
        if (count == 0) {// gamer lose
            if (numberOfLosers() < (lose.length - 1)) {
                switch (unit.getGamer()) {
                    case ONE:
                        lose[0] = true;
                        break;
                    case TWO:
                        lose[1] = true;
                        break;
                    case THREE:
                        lose[2] = true;
                        break;
                }
            }
            if (numberOfLosers() == (lose.length - 1)) {
                Gamer gamer = !lose[0] ? Gamer.ONE : !lose[1] ? Gamer.TWO : Gamer.THREE;
                manager.win(gamer); // Победа!!!
            }
        }
    }

    private int numberOfLosers() {
        int count = 0;
        for (boolean b : lose) {
            if (b) count++;
        }
        return count;
    }

    private void goTo(int x, int y) {
        ArrayList<Node> path = new ArrayList<Node>();
        ArrayList<Node> listAll = new ArrayList<Node>(listClosedGoShot);
        listAll.addAll(listClosedGo);
        Node curr = listAll.get(listAll.indexOf(new Node(null, x, y, 0)));
        currentUnit.changePassability(curr.getPassability());
        path.add(curr);
        Node tmp;
        while ((tmp = curr.getParent()) != null) {
            path.add(tmp);
            curr = tmp;
        }
        path.remove(path.size() - 1);// юнит уже находится в начале пути
        Collections.reverse(path);
        currentUnit.setMove(path);
    }

    private Unit getUnit(int x, int y) {
        if (x < 0 || x > wTiles || y < 0 || y > hTiles) {
            return null;
        }
        for (Unit unit : units) {
            if (unit.getX() == x && unit.getY() == y) {
                return unit;
            }
        }
        return null;
    }

    // Never used
    public void haveMine(int x, int y) {
        Mine mine = getMine(x, y);
        if (mine != null) {

        }
    }

    // TODO: Замениить на list.indexOf(...) переопределив Mine.equals
    private Mine getMine(int x, int y) {
        if (x < 0 || x > wTiles || y < 0 || y > hTiles) {
            return null;
        }
        for (Mine mine : mines) {
            if (mine.getX() == x && mine.getY() == y) {
                return mine;
            }
        }
        return null;
    }

    public void selectNextUnit() {
        selectNextUnit(currentGamer);
    }

    public void selectNextUnit(Gamer g) {
        if (blockTap) {
            return;
        }
        int idx = g == Gamer.ONE ? idx1 : g == Gamer.TWO ? idx2 : idx3;
        int size = units.size();
        Unit unit;
        for (int i = idx; i < size; i++) {
            unit = units.get(i);
            // возможно нужно добавить проверку ничейный ли юнит в NetBeans
            if (unit.getGamer() == g) {
                if (!init) {
                    currentUnit = unit;
                    scrollToCurrent();
                    //Log.d(tag, "calcGoing from selectNextUnit");
                    calcGoing();
                    postInvalidate();
                }
                switch (g) {
                    case ONE:
                        idx1 = i + 1;
                        return;
                    case TWO:
                        idx2 = i + 1;
                        return;
                    case THREE:
                        idx3 = i + 1;
                        return;
                }
            }
        }
        // not found, find from top list
        switch (g) {
            case ONE:
                idx1 = 0;
                break;
            case TWO:
                idx2 = 0;
                break;
            case THREE:
                idx3 = 0;
                break;
        }
        // доб. пррверку, если ни одного, то выбыл из игры
        selectNextUnit(g);
    }

    private void scrollToCurrent() {
        int ux = currentUnit.getDrawX();
        int uy = currentUnit.getDrawY();
        int dx = ux - scrollX - scrW / 2;
        int dy = uy - scrollY - scrH / 2;
        if (scrollX + dx < 0) {
            dx = -scrollX;
        } else if (scrollX + dx + scrW > w) {
            dx = w - scrollX - scrW;
        }
        if (scrollY + dy < 0) {
            dy = -scrollY;
        } else if (scrollY + dy + scrH > h) {
            dy = h - scrollY - scrH;
        }
        scroller.startScroll(scrollX, scrollY, dx, dy, 300);
    }

    /* Рассчитывает все возможные пути для юнита */
    private void calcGoing() {
        pathGo.reset();
        pathGoShot.reset();
        //selRect.offsetTo(currentUnit.getX() * Tile.TILE_SIZE, currentUnit.getY() * Tile.TILE_SIZE);
        listClosedGo.clear();
        listClosedGoShot.clear();
        ArrayList<Node> listOpen = new ArrayList<Node>();
        int haveP = currentUnit.getPassability();
        int currP;
        int maxShot = currentUnit.getShotMaxPassability();
        //Log.d(tag, "haveP=" + haveP);
        //Log.d(tag, "maxShot=" + maxShot);
        Node startNode = new Node(null, currentUnit.getX(), currentUnit.getY(), currentUnit.getUsedPassability());
        //Log.d(tag, "startNode: " + startNode);
        listOpen.add(startNode);
        //if (haveP > maxShot) {
        //Log.d(tag, "first add go shot");
        //listClosedGoShot.add(startNode);
        //} else {
        //Log.d(tag, "first add go");
        //listClosedGo.add(startNode);
        //}
        int x, y;
        while (!listOpen.isEmpty()) {
            //Log.d(tag, "ITERATION STARTED");
            Node n = listOpen.get(0);
            listOpen.remove(n);
            x = n.getX();
            y = n.getY();
            //Log.d(tag, "x=" + x + " y=" + y);
            currP = n.getPassability();
            //Log.d(tag, "currP=" + currP);
            if (currP > haveP) {
                //Log.d(tag, "currP > haveP");
                continue;
            }
            if (maxShot >= currP) {
                //Log.d(tag, "SHOT CELL");
                if (listClosedGoShot.contains(n)) {
                    //Log.d(tag, "contains in go shot");
                    Node exist = listClosedGoShot.get(listClosedGoShot.indexOf(n));
                    if (exist.getPassability() > n.getPassability()) {
                        //Log.d(tag, "replace go shot cell");
                        listClosedGoShot.remove(exist);
                        listClosedGoShot.add(n);
                    } else {
                        //Log.d(tag, "continue");
                        continue;
                    }
                } else if (listClosedGo.contains(n)) {
                    //Log.d(tag, "go shot contains in go");
                    listClosedGo.remove(n);
                    listClosedGoShot.add(n);
                } else {
                    //Log.d(tag, "added to shot");
                    Unit u = getUnit(x, y);
                    if (u != null && u != currentUnit) {
                        continue;
                    } else {
                        listClosedGoShot.add(n);
                    }
                }
            } else {
                //Log.d(tag, "GO CELL");
                if (listClosedGoShot.contains(n)) {
                    //listClosedGo.remove(n);
                    continue;
                }
                if (listClosedGo.contains(n)) {
                    Node exist = listClosedGo.get(listClosedGo.indexOf(n));
                    //Log.d(tag, "contains in go");
                    if (exist.getPassability() > n.getPassability()) {
                        //Log.d(tag, "replace go cell");
                        listClosedGo.remove(exist);
                        listClosedGo.add(n);
                    } else {
                        //Log.d(tag, "continue");
                        continue;
                    }
                } else {
                    //Log.d(tag, "added to go");
                    Unit u = getUnit(x, y);
                    if (u != null && u != currentUnit) {
                        continue;
                    } else {
                        listClosedGo.add(n);
                    }
                }
            }
            int[] newx = {x, x, x - 1, x + 1};
            int[] newy = {y - 1, y + 1, y, y};
            for (int i = 0; i < 4; i++) {
                int tmpx = newx[i];
                int tmpy = newy[i];
                if (tmpx < 0 || tmpx > wTiles - 1 || tmpy < 0 || tmpy > hTiles - 1) continue;
                else {
                    int addP = currentUnit.calcPassability(gameMap.getTileCode(tmpx, tmpy));
                    Node addNode = new Node(n, tmpx, tmpy, currP + addP);
                    //Log.d(tag, "add node: " + addNode);
                    listOpen.add(addNode);
                }
            }
        }
        for (Node node : listClosedGoShot) {
            selRect.offsetTo(node.getX() * Tile.TILE_SIZE, node.getY() * Tile.TILE_SIZE);
            pathGoShot.addRect(selRect, Path.Direction.CW);
            pathGoShot.close();
        }
        for (Node node : listClosedGo) {
            selRect.offsetTo(node.getX() * Tile.TILE_SIZE, node.getY() * Tile.TILE_SIZE);
            pathGo.addRect(selRect, Path.Direction.CW);
            pathGo.close();
        }
    }

    public void calcVisibility(Unit unit) {
        int look = unit.getLookRadius();
        int uX = unit.getX();
        int uY = unit.getY();
        int i2;
        for (int i = -look; i <= look; i++) {
            i2 = look - Math.abs(i);
            if (i + uY > -1 && i + uY < hTiles) {
                for (int j = -i2; j <= i2; j++) {
                    if (j + uX > -1 && j + uX < wTiles) {
                        gameMap.setVisible(j + uX, i + uY, unit.getGamer());
                    }
                }
            }
        }
    }

    private void calcVisibilityForAll() {
        for (Unit unit : units) {
            if (!unit.isFree()) {
                calcVisibility(unit);
            }
        }
    }

    public void setCurrentGamer(Gamer g) {
        currentGamer = g;
        gameMap.setCurrentGamer(g);
        Mine.setCurrentGamer(g);
        for (Mine mine : mines) {
            mine.onChange();
        }
        //int idx = currentGamer == Gamer.ONE ? idx1 : currentGamer == Gamer.TWO ? idx2 : idx3;
        selectNextUnit();
        scrollToCurrent();
        //Log.d(tag, "calcGoing from setCurrentGamer");
        for (Unit u : units) {
            if (u.getGamer() == g) {
                u.reset();
            }
        }
        calcGoing();
    }

    public void setShow(boolean b) {
        show = b;
        invalidate();
    }

    public boolean getBlocked() {
        return blockTap;
    }

    public void setToastView(TextView view) {
        toastText = view;
        toastText.setLayoutParams(lpMoveToast);
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        return scrW;
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return scrollX;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return w;
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return scrH;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return scrollY;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return h;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scrW = getWidth();
        scrH = getHeight();
        scrollToCurrent();
    }
}
