package ua.naiksoftware.waronline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import ua.naiksoftware.waronline.unit.Unit;
import ua.naiksoftware.waronline.unit.UnitCode;

public class MapEditorSelectView extends View {

    private static final String tag = MapEditorSelectView.class.getName();

    private Context context;
    private Tile[] tiles;
    private Unit[] units;
    private Paint paintMesh, paintSel;
    private Rect selRect;
    private GestureDetector gestureDetector;
    private OnSelectListener listener;
    private boolean drawUnits;

    public MapEditorSelectView(Context context) {
        super(context);
        init(context);
    }

    public MapEditorSelectView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public MapEditorSelectView(Context context, AttributeSet attr, int style) {
        super(context, attr, style);
        init(context);
    }

    public void viewTiles() {
        setMeasuredDimension(Tile.TILE_SIZE * Tile.TILE_COUNT, Tile.TILE_SIZE);
        drawUnits = false;
    }

    public void viewUnits() {
        setMeasuredDimension(Tile.TILE_SIZE * Unit.UNIT_COUNT, Tile.TILE_SIZE);
        drawUnits = true;
    }

    private void init(Context c) {
        context = c;
        tiles = new Tile[Tile.TILE_COUNT];
        for (int i = 0; i < Tile.TILE_COUNT; i++) {
            tiles[i] = new Tile(i);
        }
        units = new Unit[Unit.UNIT_COUNT];
        for (int i = 0; i < Unit.UNIT_COUNT; i++) {
            units[i] = new Unit(i * UnitCode.STEP_ANIM + 1, 0, 0);
        }
        paintMesh = new Paint();
        paintMesh.setColor(0xff000000);
        paintMesh.setStrokeWidth(3);
        selRect = new Rect(2, 2, Tile.TILE_SIZE, Tile.TILE_SIZE - 2);
        paintSel = new Paint();
        paintSel.setStyle(Paint.Style.STROKE);
        paintSel.setColor(0xff555555);
        paintSel.setStrokeWidth(3);
        gestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int drawX = 0;
        if (drawUnits) {
            for (Unit unit : units) {
                canvas.drawBitmap(unit.getBitmap(), drawX, 0, null);
                canvas.drawLine(drawX, 0, drawX, Tile.TILE_SIZE, paintMesh);
                drawX += Tile.TILE_SIZE;
            }
        } else { // tiles
            for (Tile tile : tiles) {
                canvas.drawBitmap(tile.getBitmap(), drawX, 0, null);
                canvas.drawLine(drawX, 0, drawX, Tile.TILE_SIZE, paintMesh);
                drawX += Tile.TILE_SIZE;
            }
        }
        canvas.drawRect(selRect, paintSel);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (drawUnits) {
            setMeasuredDimension(Tile.TILE_SIZE * Unit.UNIT_COUNT, Tile.TILE_SIZE);
        } else {
            setMeasuredDimension(Tile.TILE_SIZE * Tile.TILE_COUNT, Tile.TILE_SIZE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int rawx = (int) e.getX();
            int selid = rawx / Tile.TILE_SIZE;
            selRect.offsetTo(selid * Tile.TILE_SIZE + 2, 2);
            postInvalidate();
            if (drawUnits) {
                listener.onSelectUnit(units[selid]);
            } else {
                listener.onSelectTile(tiles[selid]);
            }
            return true;
        }

    };

    public void setOnSelectListener(OnSelectListener l) {
        listener = l;
    }

    public interface OnSelectListener {

        void onSelectTile(Tile tile);

        void onSelectUnit(Unit unit);
    }
}
