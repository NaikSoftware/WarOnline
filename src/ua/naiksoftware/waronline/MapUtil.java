package ua.naiksoftware.waronline;

import java.io.*;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;

public class MapUtil {

    private static final String tag = MapUtil.class.getName();

    public static void loadMapList(final MapLoaderCompleteListener listener) {
        final ArrayList<MapListEntry> result = new ArrayList<MapListEntry>();
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                listener.complete(result);
            }
        };
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String internalDir = ContextHolder.getContext().getApplicationInfo().dataDir + "/maps/";
                    String[] mapsAssets = ContextHolder.getContext().getAssets().list("maps");
                    String[] mapsInternal = new File(internalDir).list();
                    DataInputStream stream;
                    for (String path : mapsAssets) {
                        stream = new DataInputStream(ContextHolder.getAssetRes("maps/" + path));
                        result.add(new MapListEntry(stream, null, false));
                    }
                    for (String path : mapsInternal) {
                        stream = new DataInputStream(new FileInputStream(internalDir + path));
                        result.add(new MapListEntry(stream, internalDir + path, true));
                    }
                    handler.sendEmptyMessage(1);
                } catch (IOException e) {
                    Log.e(tag, "Err in load map: ", e);
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }
}
