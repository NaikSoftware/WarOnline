package ua.naiksoftware.waronline;

import android.graphics.*;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import ua.naiksoftware.waronline.unit.Unit;
import ua.naiksoftware.waronline.unit.UnitCode;
import java.util.ArrayList;

public class MapEditorView extends View {

    private static final String tag = MapEditorView.class.getName();

    private Scroller scroller;
    private GestureDetector gestureDetector;

    private int w, h;
    private int scrW, scrH;
    private int scrollX, scrollY;
    private int wTiles, hTiles;
    private static Tile[][] mapTiles;
    private Rect rectSel;
    private int selXID, selYID;
    private Paint paintSel, paintMesh, paintBaseSelected, paintText;

    private int stage;
    private Rect base1, base2, base3;
    public static final int BASE_SIZE = 3;//tiles (3x3)
    private String strBase1, strBase2, strBase3;
    private Bitmap baseSelCursor;

    private Tile appendTile = new Tile(TileCode.GRASS);
    private Unit appendUnit = new Unit(UnitCode.ING_AVTO, 0, 0);

    private ArrayList<Unit> freeUnits = new ArrayList<Unit>();

    public MapEditorView(Context context) {
        super(context);
        init(context);
    }

    public MapEditorView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public MapEditorView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init(context);
    }

    private void init(Context context) {
        paintSel = new Paint();
        paintSel.setColor(0xff3333ff);
        paintSel.setStyle(Paint.Style.STROKE);
        paintSel.setStrokeWidth(3);
        rectSel = new Rect(0, 0, Tile.TILE_SIZE, Tile.TILE_SIZE);
        paintMesh = new Paint();
        paintMesh.setColor(0x33ffffff);
        paintMesh.setStyle(Paint.Style.STROKE);
        paintMesh.setPathEffect(new DashPathEffect(new float[]{15f, 5f, 1f, 5f}, 0));
        scroller = new Scroller(context);
        gestureDetector = new GestureDetector(context, new MyGestureListener());
        setVerticalScrollBarEnabled(true);
        setHorizontalScrollBarEnabled(true);
        //TypedArray a = context.obtainStyledAttributes(R.styleable.View);
        //initializeScrollbars(a);
        //a.recycle();
        wTiles = mapTiles.length;
        hTiles = mapTiles[0].length;
        w = wTiles * Tile.TILE_SIZE;
        h = hTiles * Tile.TILE_SIZE;
        int initCoord = Math.max(w, h) + 100; //over screen coordinates
        base1 = new Rect(initCoord, initCoord, initCoord + Tile.TILE_SIZE * BASE_SIZE, initCoord + Tile.TILE_SIZE * BASE_SIZE);
        base2 = new Rect(initCoord, initCoord, initCoord + Tile.TILE_SIZE * BASE_SIZE, initCoord + Tile.TILE_SIZE * BASE_SIZE);
        base3 = new Rect(initCoord, initCoord, initCoord + Tile.TILE_SIZE * BASE_SIZE, initCoord + Tile.TILE_SIZE * BASE_SIZE);
        paintText = new Paint();
        paintText.setColor(Color.BLUE);
        paintText.setTextSize(40);
        paintText.setTypeface(Typeface.MONOSPACE);
        paintText.setStrokeWidth(2);
        paintBaseSelected = new Paint(paintSel);
        paintBaseSelected.setAlpha(0x55);
        strBase1 = context.getString(R.string.base) + " 1";
        strBase2 = context.getString(R.string.base) + " 2";
        strBase3 = context.getString(R.string.base) + " 3";
        baseSelCursor = BitmapFactory.decodeResource(context.getResources(), R.drawable.cursor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
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
        int drawX = startXid * Tile.TILE_SIZE, drawY;
        for (int i = startXid; i < endXid; i++) {
            drawY = startYid * Tile.TILE_SIZE;
            for (int j = startYid; j < endYid; j++) {
                canvas.drawBitmap(mapTiles[i][j].getBitmap(), drawX, drawY, null);
                canvas.drawRect(drawX, drawY, drawX + Tile.TILE_SIZE, drawY + Tile.TILE_SIZE, paintMesh);
                drawY += Tile.TILE_SIZE;
            }
            drawX += Tile.TILE_SIZE;
        }
        if (stage == MapEditorActivity.STAGE_TILES) {
            canvas.drawRect(rectSel, paintSel);
        } else if (stage == MapEditorActivity.STAGE_BASE1) {
            canvas.drawRect(base1, paintSel);
            canvas.drawText(strBase1, base1.left + 5, base1.top + 40, paintText);
            canvas.drawBitmap(baseSelCursor, base1.centerX() - Tile.TILE_SIZE / 2,
                    base1.centerY() - Tile.TILE_SIZE / 2, null);
        } else if (stage == MapEditorActivity.STAGE_BASE2) {
            canvas.drawRect(base1, paintBaseSelected);
            canvas.drawText(strBase1, base1.left + 5, base1.top + 40, paintText);
            canvas.drawRect(base2, paintSel);
            canvas.drawText(strBase2, base2.left + 5, base2.top + 40, paintText);
            canvas.drawBitmap(baseSelCursor, base2.centerX() - Tile.TILE_SIZE / 2,
                    base2.centerY() - Tile.TILE_SIZE / 2, null);
        } else if (stage == MapEditorActivity.STAGE_BASE3) {
            canvas.drawRect(base1, paintBaseSelected);
            canvas.drawText(strBase1, base1.left + 5, base1.top + 40, paintText);
            canvas.drawRect(base2, paintBaseSelected);
            canvas.drawText(strBase2, base2.left + 5, base2.top + 40, paintText);
            canvas.drawRect(base3, paintSel);
            canvas.drawText(strBase3, base3.left + 5, base3.top + 40, paintText);
            canvas.drawBitmap(baseSelCursor, base3.centerX() - Tile.TILE_SIZE / 2,
                    base3.centerY() - Tile.TILE_SIZE / 2, null);
        } else if (stage == MapEditorActivity.STAGE_UNITS) {
            canvas.drawRect(base1, paintBaseSelected);
            canvas.drawText(strBase1, base1.left + 5, base1.top + 40, paintText);
            canvas.drawRect(base2, paintBaseSelected);
            canvas.drawText(strBase2, base2.left + 5, base2.top + 40, paintText);
            canvas.drawRect(base3, paintBaseSelected);
            canvas.drawText(strBase3, base3.left + 5, base3.top + 40, paintText);
            for (Unit unit : freeUnits) {
                unit.draw(canvas);
            }
        }
    }

    public static void setMapTiles(Tile[][] t) {
        //Log.d(tag, "setMapTiles(" + t.toString() + ")");
        //Log.d(tag, "Tile.length = " + t.length + " Tile[].length = " + t[0].length);
        mapTiles = t;
    }

    public void setStage(int stage) {
        this.stage = stage;
        int overScreen = Math.max(w, h) + 100;
        if (Rect.intersects(base1, base2)) {
            base2.offsetTo(overScreen, overScreen);
        }
        if (Rect.intersects(base1, base3)) {
            base3.offsetTo(overScreen, overScreen);
        }
        if (Rect.intersects(base2, base3)) {
            base3.offsetTo(overScreen, overScreen);
        }
        if (!availableForBase(base1.left / Tile.TILE_SIZE + BASE_SIZE / 2, base1.top / Tile.TILE_SIZE + BASE_SIZE / 2)) {
            base1.offsetTo(overScreen, overScreen);
        }
        if (!availableForBase(base2.centerX() / Tile.TILE_SIZE, base2.centerY() / Tile.TILE_SIZE)) {
            base2.offsetTo(overScreen, overScreen);
        }
        if (!availableForBase(base3.centerX() / Tile.TILE_SIZE, base3.centerY() / Tile.TILE_SIZE)) {
            base3.offsetTo(overScreen, overScreen);
        }
        if (stage == MapEditorActivity.STAGE_UNITS) {
            begin:
            while (true) {
                for (Unit unit : freeUnits) {
                    if (!freeForUnit(unit.getX(), unit.getY())) {
                        freeUnits.remove(unit);
                        continue begin;
                    }
                }
                break;
            }
        }
        invalidate();
    }

    public void changeTile(Tile tile) {
        appendTile = tile;
    }

    public void changeUnit(Unit unit) {
        appendUnit = unit;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(tag, "onTouchEvent");
        //awakenScrollBars();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
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
            //boolean scrollBeyondImage = ((getScrollX() < 0) || (getScrollX() > w) || (getScrollY() < 0) || (getScrollY() > h));
            //if (scrollBeyondImage) return false;
            scroller.fling(scrollX, scrollY, -(int) velocityX, -(int) velocityY,
                    0, w - scrW, 0, h - scrH);
			invalidate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int rawx = (int) e.getX() + scrollX;
            int rawy = (int) e.getY() + scrollY;
            selXID = rawx / Tile.TILE_SIZE;
            selYID = rawy / Tile.TILE_SIZE;
            if (stage == MapEditorActivity.STAGE_TILES) {
                rectSel.offsetTo(selXID * Tile.TILE_SIZE, selYID * Tile.TILE_SIZE);
                mapTiles[selXID][selYID] = appendTile;
            } else if (stage == MapEditorActivity.STAGE_BASE1) {
                if (availableForBase(selXID, selYID)) {
                    base1.offsetTo((selXID - BASE_SIZE / 2) * Tile.TILE_SIZE,
                            (selYID - BASE_SIZE / 2) * Tile.TILE_SIZE);
                }
            } else if (stage == MapEditorActivity.STAGE_BASE2) {
                if (availableForBase(selXID, selYID)) {
                    int dx = (selXID - BASE_SIZE / 2) * Tile.TILE_SIZE - base2.left;
                    int dy = (selYID - BASE_SIZE / 2) * Tile.TILE_SIZE - base2.top;
                    base2.offset(dx, dy);
                    if (Rect.intersects(base1, base2)) {
                        base2.offset(-dx, -dy);
                    }
                }
            } else if (stage == MapEditorActivity.STAGE_BASE3) {
                if (availableForBase(selXID, selYID)) {
                    int dx = (selXID - BASE_SIZE / 2) * Tile.TILE_SIZE - base3.left;
                    int dy = (selYID - BASE_SIZE / 2) * Tile.TILE_SIZE - base3.top;
                    base3.offset(dx, dy);
                    if (Rect.intersects(base1, base3)
                            || Rect.intersects(base2, base3)) {
                        base3.offset(-dx, -dy);
                    }
                }
            } else if (stage == MapEditorActivity.STAGE_UNITS) {
                if (freeForUnit(selXID, selYID)) {
                    boolean add = true;
                    Unit unit;
                    int len = freeUnits.size();
                    for (int i = 0; i < len; i++) {
                        unit = freeUnits.get(i);
                        if (selXID == unit.getX() && selYID == unit.getY()) {
                            freeUnits.remove(i);
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        freeUnits.add(new Unit(appendUnit.getCode(), selXID, selYID));
                    }
                }
            }
            postInvalidate();
            return true;
        }
    };

    private boolean availableForBase(int xId, int yId) {
        if (xId < BASE_SIZE / 2 || xId > wTiles - BASE_SIZE / 2 - 1
                || yId < BASE_SIZE / 2 || yId > hTiles - BASE_SIZE / 2 - 1) {
            return false;
        }
        for (int i = xId - 1; i < xId + 2; i++) {
            for (int j = yId - 1; j < yId + 2; j++) {
                int code = mapTiles[i][j].getCode();
                if (code >= TileCode.WATER) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean freeForUnit(int x, int y) {
        if (mapTiles[x][y].getCode() >= TileCode.WATER) {
            return false;
        }
        int rawx = x * Tile.TILE_SIZE;
        int rawy = y * Tile.TILE_SIZE;
        if (base1.contains(rawx, rawy)
                || base2.contains(rawx, rawy)
                || base3.contains(rawx, rawy)) {
            return false;
        }
        return true;
    }

    public Tile[][] getTiles() {
        return mapTiles;
    }
    
    public Rect getBase(int which) {
        switch(which) {
            case 1: return base1;
            case 2: return base2;
            case 3: return base3;
            default:
                throw new RuntimeException("Base number not 1, 2, or 3!");
        }
    }
    
    public ArrayList<Unit> getFreeUnits() {
        return freeUnits;
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
    }
}
