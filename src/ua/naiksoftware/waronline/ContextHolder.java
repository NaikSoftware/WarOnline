package ua.naiksoftware.waronline;

import android.content.Context;
import java.io.InputStream;
import java.io.IOException;

public class ContextHolder {

    private static Context context;

    public static void setContext(Context c) {
        context = c;
    }

    public static Context getContext() {
        return context;
    }

    public static InputStream getAssetRes(String path) throws IOException {
        return context.getAssets().open(path);
    }
}
