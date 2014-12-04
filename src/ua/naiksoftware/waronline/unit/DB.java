package ua.naiksoftware.waronline.unit;

import android.util.SparseIntArray;

/**
 *
 * @author Naik
 */
public class DB {

    public static final SparseIntArray looks = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 2);
            append(UnitCode.SOLDIER, 3);
            append(UnitCode.HORSE, 4);
            append(UnitCode.HOTCHKISS, 2);
            append(UnitCode.T34_85, 2);
            append(UnitCode.PANZER, 3);
            append(UnitCode.TIGER, 3);
            append(UnitCode.ARTILLERY, 2);
        }
    };

    public static SparseIntArray lifes = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 200);
            append(UnitCode.SOLDIER, 100);
            append(UnitCode.HORSE, 150);
            append(UnitCode.HOTCHKISS, 300);
            append(UnitCode.T34_85, 250);
            append(UnitCode.PANZER, 350);
            append(UnitCode.TIGER, 400);
            append(UnitCode.ARTILLERY, 250);
        }
    };

    public static final SparseIntArray maxCells = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 4);
            append(UnitCode.SOLDIER, 3);
            append(UnitCode.HORSE, 5);
            append(UnitCode.HOTCHKISS, 4);
            append(UnitCode.T34_85, 5);
            append(UnitCode.PANZER, 4);
            append(UnitCode.TIGER, 4);
            append(UnitCode.ARTILLERY, 3);
        }
    };

    public static final SparseIntArray maxCellsShot = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 3);
            append(UnitCode.SOLDIER, 2);
            append(UnitCode.HORSE, 2);
            append(UnitCode.HOTCHKISS, 2);
            append(UnitCode.T34_85, 3);
            append(UnitCode.PANZER, 3);
            append(UnitCode.TIGER, 2);
            append(UnitCode.ARTILLERY, 2);
        }
    };

    public static final SparseIntArray attackRadius = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 1);
            append(UnitCode.SOLDIER, 2);
            append(UnitCode.HORSE, 2);
            append(UnitCode.HOTCHKISS, 3);
            append(UnitCode.T34_85, 3);
            append(UnitCode.PANZER, 3);
            append(UnitCode.TIGER, 4);
            append(UnitCode.ARTILLERY, 5);
        }
    };

    public static final SparseIntArray shootingForce = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, -50);
            append(UnitCode.SOLDIER, -50);
            append(UnitCode.HORSE, -50);
            append(UnitCode.HOTCHKISS, -90);
            append(UnitCode.T34_85, -70);
            append(UnitCode.PANZER, -100);
            append(UnitCode.TIGER, -120);
            append(UnitCode.ARTILLERY, -200);
        }
    };

    public static final SparseIntArray armorFront = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 20);
            append(UnitCode.SOLDIER, 15);
            append(UnitCode.HORSE, 15);
            append(UnitCode.HOTCHKISS, 40);
            append(UnitCode.T34_85, 30);
            append(UnitCode.PANZER, 50);
            append(UnitCode.TIGER, 60);
            append(UnitCode.ARTILLERY, 30);
        }
    };
    public static final SparseIntArray armorSide = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 15);
            append(UnitCode.SOLDIER, 10);
            append(UnitCode.HORSE, 15);
            append(UnitCode.HOTCHKISS, 20);
            append(UnitCode.T34_85, 25);
            append(UnitCode.PANZER, 40);
            append(UnitCode.TIGER, 50);
            append(UnitCode.ARTILLERY, 15);
        }
    };
    public static final SparseIntArray armorRear = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 10);
            append(UnitCode.SOLDIER, 5);
            append(UnitCode.HORSE, 10);
            append(UnitCode.HOTCHKISS, 15);
            append(UnitCode.T34_85, 25);
            append(UnitCode.PANZER, 30);
            append(UnitCode.TIGER, 40);
            append(UnitCode.ARTILLERY, 10);
        }
    };
    
        public static final SparseIntArray soundShot = new SparseIntArray() {
        {
            append(UnitCode.ING_AVTO, 10);
            append(UnitCode.SOLDIER, 5);
            append(UnitCode.HORSE, 10);
            append(UnitCode.HOTCHKISS, 15);
            append(UnitCode.T34_85, 25);
            append(UnitCode.PANZER, 30);
            append(UnitCode.TIGER, 40);
            append(UnitCode.ARTILLERY, 10);
        }
    };
}
