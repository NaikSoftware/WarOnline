package ua.naiksoftware.waronline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import ua.naiksoftware.waronline.ContextHolder;

public class BitmapUtil {

	private static final Matrix matrix = new Matrix();

	public static Bitmap rotate(Bitmap bitmap, int deg) {
        matrix.reset();
        matrix.preRotate(deg);
        Bitmap resizedbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		int diffX = (resizedbitmap.getWidth() - bitmap.getWidth()) / 2;
		int diffY = (resizedbitmap.getHeight() - bitmap.getHeight()) / 2;
		return Bitmap.createBitmap(resizedbitmap, diffX, diffY, bitmap.getWidth(), bitmap.getHeight());
    }

    public static Bitmap reflect(Bitmap bitmap, ReflectType type) {
        float w = 1, h = 1;
        switch (type) {
            case HORIZONTAL:
                w = -1;
                break;
            case VERTICAL:
                h = -1;
                break;
            case COMBINE:
                w = h = -1;
                break;
        }
        matrix.reset();
        matrix.preScale(w, h);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap load(int id) {
        return BitmapFactory.decodeResource(ContextHolder.getContext().getResources(), id);
    }
}
