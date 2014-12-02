package ua.naiksoftware.waronline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseIntArray;
import android.widget.TextView;

public class GameManagerPAP extends GameManager {

    private Context context;
    private AlertDialog dialogGo;
    private GameView gameView;

    public GameManagerPAP(MapListEntry entry, SparseIntArray unitsMap, boolean three) {
        super(entry, unitsMap, three);
        context = ContextHolder.getContext();
    }

    @Override
    public void nextGamer() {
        super.nextGamer();
        if (gameView.getBlocked()) {
            return;
        }
        showGoDialog();
    }

    @Override
    public void onGameStarted(GameView gameView, TextView toastView) {
        super.onGameStarted(gameView, toastView);
        context = ContextHolder.getContext();
        this.gameView = gameView;
        showGoDialog();
    }

    public void showGoDialog() {
        gameView.setShow(false);
        if (dialogGo == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface i, int p2) {
                    gameView.setShow(true);
                }
            });
            dialogGo = builder.create();
        }
        Gamer g = getCurrentGamer();
        int gamer = g == Gamer.ONE ? 1 : g == Gamer.TWO ? 2 : 3;
        dialogGo.setMessage(context.getString(R.string.gamer_go_dialog) + " " + gamer);
        dialogGo.show();
    }
}
