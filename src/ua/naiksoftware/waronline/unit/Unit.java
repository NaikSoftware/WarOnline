package ua.naiksoftware.waronline.unit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import ua.naiksoftware.waronline.Gamer;
import ua.naiksoftware.waronline.Node;
import ua.naiksoftware.waronline.Tile;
import ua.naiksoftware.waronline.TileCode;

import java.util.ArrayList;
import java.util.Comparator;

public class Unit {

    private static final String tag = Unit.class.getName();

    public static final int UNIT_COUNT = 8;

    public static final int NOT_MOVE = 0;
    public static final int MOVE = 1;
    public static final int END_MOVE = 2;
    public static final int MOVE_TO_NEW_CELL = 3;

    private final int code;
    private int x, y;
    private int drawx, drawy;
    private int lookX, lookY;
    private Gamer gamer;
    private boolean free;
    private boolean move;
    private int dX, dY;
    private int shift;
    private static final int DELAY = 7;
    private static final int ANIM_DELAY = 200;
    private long startMove;
    private long startAnim;
    private Bitmap[] anim;
    private int passability, usedPassability;
    private ArrayList<Node> path; // move path
    private int life;
    private int lifeColor;
    private static final int COLOR_LIFE_1 = Color.BLUE;
    private static final int COLOR_LIFE_2 = Color.RED;
    private static final int COLOR_LIFE_3 = Color.YELLOW;
    private final Bitmap lifeBitmap;
    private static final int LIFE_HEIGHT = Tile.TILE_SIZE / 7;
    private static final int LIFE_WIDTH = Tile.TILE_SIZE;

    public Unit(int code, int x, int y) {
        this.lookY = -1;// изначально смотрят все вверх
        this.x = x;
        this.y = y;
        drawx = Tile.TILE_SIZE * x;
        drawy = Tile.TILE_SIZE * y;
        this.code = code;
        free = false;
        anim = Anim.get(code);
        lifeBitmap = Bitmap.createBitmap(LIFE_WIDTH, LIFE_HEIGHT, Bitmap.Config.RGB_565);
        changeLife(DB.lifes.get(code));
        reset();
    }

    public final void reset() {
        passability = DB.maxCells.get(code) * 100;
        usedPassability = 0;
    }

    public int calcPassability(int tileCode) {
        int cellsMinus = passability + 999;
        if (tileCode == TileCode.GRASS) {
            //grass
            switch (code) {
                case UnitCode.ING_AVTO:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.SOLDIER:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.HORSE:
                    cellsMinus = passability / 4;
                    break;
                case UnitCode.HOTCHKISS:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.T34_85:
                    cellsMinus = passability / 4;
                    break;
                case UnitCode.PANZER:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.TIGER:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.ARTILLERY:
                    cellsMinus = passability / 2;
                    break;
            }
        } else if (tileCode > TileCode.GRASS && tileCode < TileCode.ROAD_HORIZ) {
            //trees
            switch (code) {
                case UnitCode.ING_AVTO:
                    cellsMinus = passability / 2;
                    break;
                case UnitCode.SOLDIER:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.HORSE:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.HOTCHKISS:
                    cellsMinus = passability / 2;
                    break;
                case UnitCode.T34_85:
                    cellsMinus = passability / 2;
                    break;
                case UnitCode.PANZER:
                    cellsMinus = passability / 2;
                    break;
                case UnitCode.TIGER:
                    cellsMinus = passability / 2;
                    break;
                case UnitCode.ARTILLERY:
                    cellsMinus = passability / 1;
                    break;
            }
        } else if (tileCode > TileCode.TREES_INCORNER_LEFT_DOWN && tileCode < TileCode.WATER) {
            //road
            switch (code) {
                case UnitCode.ING_AVTO:
                    cellsMinus = passability / 4;
                    break;
                case UnitCode.SOLDIER:
                    cellsMinus = passability / 3;
                    break;
                case UnitCode.HORSE:
                    cellsMinus = passability / 5;
                    break;
                case UnitCode.HOTCHKISS:
                    cellsMinus = passability / 4;
                    break;
                case UnitCode.T34_85:
                    cellsMinus = passability / 5;
                    break;
                case UnitCode.PANZER:
                    cellsMinus = passability / 4;
                    break;
                case UnitCode.TIGER:
                    cellsMinus = passability / 4;
                    break;
                case UnitCode.ARTILLERY:
                    cellsMinus = passability / 3;
                    break;
            }
        } else if (tileCode > TileCode.BRIDGE_RIGHT && tileCode < TileCode.HATA_1) {
            //water
            switch (code) {
                case UnitCode.SOLDIER:
                    cellsMinus = passability;
                    break;
                case UnitCode.HORSE:
                    cellsMinus = passability / 2;
                    break;
            }
        }
        return cellsMinus;
    }

    public void setFree() {
        gamer = null;
        this.free = true;
        lifeColor = Color.WHITE;
        updateLifeBitmap();
    }

    public boolean isFree() {
        return free;
    }

    public int shot(Unit enemy) {
        int x1 = x - enemy.getX();
        int y1 = y - enemy.getY();
        int x2 = lookX;
        int y2 = lookY;
        double lenShooting = Math.hypot(x1, y1);
        double len2 = Math.hypot(x2, y2);
        double scalarMult = x1 * x2 + y1 * y2;
        double lenMult = lenShooting * len2;
        double angle = Math.toDegrees(Math.acos(scalarMult / lenMult));
        //if (x2 < 0) {
        //    angle = 360 - angle;
        //}
        int shotDirection = (int) (angle + 45) / 90;
        //if (shotDirection > 3) {
        //    shotDirection = 0;
        //}
        int result = DB.shootingForce.get(enemy.getCode());
//        Log.d("\n\n" + tag, "result=" + result);
        float percent = (float) lenShooting / DB.attackRadius.get(enemy.getCode());
        if (enemy.getCode() == UnitCode.ARTILLERY) {// артилерия наносит больший урон издалека
            percent = 1f - percent;
        }
//        Log.d(tag, "percent="+percent);
        percent /= 2f;// уменьшаем в 2 раза влияние расстояния на урон
        result *= (1f - percent);
//        Log.d(tag, "result(len)=" + result);
        int koefArmor;
        if (shotDirection == 0) {// в спину
            koefArmor = DB.armorRear.get(code);
        } else if (shotDirection == 1) {// сбоку
            koefArmor = DB.armorSide.get(code);
        } else {// спереди
            koefArmor = DB.armorFront.get(code);
        }
        result -= (result * (koefArmor / 100f));
//        Log.d(tag, "result(armor)=" + result);
//        Log.d(tag, "x1=" + x1 + " y1=" + y1);
//        Log.d(tag, "x2=" + x2 + " y2=" + y2);
//        Log.d(tag, "lenShooting=" + lenShooting);
//        Log.d(tag, "angleShooting=" + angle);
//        Log.d(tag, "directionShooting=" + shotDirection);
        changeLife(result);
        return result;
    }

    private void changeLife(int dLife) {
        life += dLife;
//		  После смерти юнит больше не не отрисовывается через класс Unit,
//        а попадает в специальный список и рисуется через класс DiedUnit.
//        if (died()) {
//            anim = Anim.get(code + UnitCode.ID_DIED);
//        }
        updateLifeBitmap();
    }

    private void updateLifeBitmap() {
        float drawLife = (life * ((float) (LIFE_WIDTH - 4) / DB.lifes.get(code))) + 2;
        Canvas canvas = new Canvas(lifeBitmap);
        canvas.drawColor(0xff100910);
        Paint lifePaint = new Paint();
        lifePaint.setColor(lifeColor);
        lifePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(2, 2, drawLife, LIFE_HEIGHT - 2, lifePaint);
    }

    // Never used
    public int getLife() {
        return life;
    }

    public boolean died() {
        return life < 1;
    }

    // return draw state
    public int draw(Canvas canvas) {
        if (life > 0) canvas.drawBitmap(lifeBitmap, drawx, drawy - LIFE_HEIGHT, null);
        if (move) {
            int time = (int) (System.currentTimeMillis() - startMove);
            shift = time / DELAY;
            if ((dX != 0 && shift >= Math.abs(dX)) || (dY != 0 && shift >= Math.abs(dY))) {
                //Log.d("Unit", "shift="+shift+" dx="+dX+" dy="+dY);
                this.x += lookX; //dX / Tile.TILE_SIZE;
                this.y += lookY; //dY / Tile.TILE_SIZE;
                this.drawx = Tile.TILE_SIZE * x;
                this.drawy = Tile.TILE_SIZE * y;
                if (path.size() > 1) {
                    path.remove(0);
                    int index = ((int) (System.currentTimeMillis() - startAnim) / ANIM_DELAY) % anim.length;

                    canvas.drawBitmap(anim[index], drawx, drawy, null);
                    return MOVE_TO_NEW_CELL;
                } else {
                    canvas.drawBitmap(anim[0], drawx, drawy, null);
                    startAnim = shift = 0;
                    this.move = false;
                    return END_MOVE; // Весь путь пройден
                }
            }
            int index = ((int) (System.currentTimeMillis() - startAnim) / ANIM_DELAY) % anim.length;
            if (dX < 0) {
                drawx = Tile.TILE_SIZE * x - shift;
            } else if (dY < 0) {
                drawy = Tile.TILE_SIZE * y - shift;
            } else if (dX > 0) {
                drawx = Tile.TILE_SIZE * x + shift;
            } else if (dY > 0) {
                drawy = Tile.TILE_SIZE * y + shift;
            }
            canvas.drawBitmap(anim[index], drawx, drawy, null);
            return MOVE;
        } else {
            canvas.drawBitmap(anim[0], drawx, drawy, null);
            return NOT_MOVE;
        }
    }

    public void setMove(ArrayList<Node> path) {
        this.path = path;
        this.startAnim = System.currentTimeMillis();
        move = true;
        continueMove();
    }
    
    /* Вызывается каждую пройденную клетку поля */
    public void continueMove() {
        Node node = path.get(0);
        dX = (node.getX() - this.x) * Tile.TILE_SIZE;
        dY = (node.getY() - this.y) * Tile.TILE_SIZE;
        // set anim array
        lookX = lookY = 0;
        if (dX > 0) {
            lookX = 1;
            anim = Anim.get(code + UnitCode.ID_RIGHT);
        } else if (dX < 0) {
            lookX = -1;
            anim = Anim.get(code + UnitCode.ID_LEFT);
        } else if (dY > 0) {
            lookY = 1;
            anim = Anim.get(code + UnitCode.ID_DOWN);
        } else if (dY < 0) {
            lookY = -1;
            anim = Anim.get(code + UnitCode.ID_UP);
        }
        startMove = System.currentTimeMillis();
    }

    // Never used
    public void setCoords(int x, int y) {
        move = false;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDrawX() {
        return drawx;
    }

    public int getDrawY() {
        return drawy;
    }

    public int getCode() {
        return code;
    }

    public void setGamer(Gamer g) {
        free = false;
        gamer = g;
        lifeColor = g == Gamer.ONE ? COLOR_LIFE_1 : g == Gamer.TWO ? COLOR_LIFE_2 : COLOR_LIFE_3;
        updateLifeBitmap();
    }

    public Gamer getGamer() {
        return gamer;
    }

    public int getLookRadius() {
        return DB.looks.get(code);
    }

    public int getPassability() {
        return passability;
    }

    public void changePassability(int ch) {
        usedPassability = ch;
    }

    public int getUsedPassability() {
        return usedPassability;
    }

    public int getShotMaxPassability() {
        return DB.maxCellsShot.get(code) * 100;
    }

    public int getAttackRadius() {
        return DB.attackRadius.get(code);
    }

    public double distanseTo(int x2, int y2) {
        return Math.hypot(x2 - x, y2 - y);
    }

    public void rotateTo(int xTo, int yTo) {
        int x1 = 0, y1 = 1;
        int x2 = xTo - x;
        int y2 = y - yTo;
        double len1 = Math.hypot(x1, y1);
        double len2 = Math.hypot(x2, y2);
        double scalarMult = x1 * x2 + y1 * y2;
        double lenMult = len1 * len2;
        double angle = Math.toDegrees(Math.acos(scalarMult / lenMult));
        if (x2 < 0) {
            angle = 360 - angle;
        }
        int frameShift = (int) (angle + 22.5) / 45;
        if (frameShift > 7) {
            frameShift = 0;
        }
        if (frameShift > 0 && frameShift < 4) {
            lookX = 1;
        } else if (frameShift > 4) {
            lookX = -1;
        } else {
            lookX = 0;
        }
        if (frameShift > 2 && frameShift < 6) {
            lookY = 1;
        } else if (frameShift == 2 || frameShift == 6) {
            lookY = 0;
        } else {
            lookY = -1;
        }
        anim = Anim.get(code + frameShift);
    }

    // Для отрисовки юнита как картинки (в меню например)
    public Bitmap getBitmap() {
        return anim[0];
    }

    public int getColor() {
        return lifeColor;
    }

    /** 
     * Сортировщик: чем выше координата юнита (чем меньше y),
     * тем раньше он будет отрисован.
     * Для правильного наложения выступающих за размеры тайла (спрайта)
     * элементов, например полоса жизней над юнитами.
     */
    public static class DrawSortComparator implements Comparator<Unit> {

        @Override
        public int compare(Unit lhs, Unit rhs) {
            return lhs.getDrawY() - rhs.getDrawY();
        }
    }
}
