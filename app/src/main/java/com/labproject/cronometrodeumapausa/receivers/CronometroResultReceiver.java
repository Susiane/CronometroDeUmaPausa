package com.labproject.cronometrodeumapausa.receivers;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

/**
 * Created by Susiane on 09/08/2016.
 */
@SuppressLint("ParcelCreator")
public class CronometroResultReceiver extends ResultReceiver {
    private RecebeResultReceiver recebeResultReceiver;
    private String count;
    private int countInt;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public CronometroResultReceiver(Handler handler, RecebeResultReceiver recebeResultReceiver) {
        super(handler);
        this.recebeResultReceiver = recebeResultReceiver;
    }

    public interface RecebeResultReceiver{
        void atualizaTempoAtividade(String tempoAtividade, int countInt);
        void atualizaTempoPausa(String tempoPausa, int countInt);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle bundle) {
        Log.d("TAG","onReceiveResult - resultCode: "+resultCode);

        if(resultCode == 1){
            count = bundle.getString("count");
            countInt = bundle.getInt("countInt");
            Log.d("TAG","VALOR DE COUNT no onReceiveResult(): "+count);
            this.recebeResultReceiver.atualizaTempoAtividade(count,countInt);
        }
        if(resultCode == 2){
            count = bundle.getString("count");
            countInt = bundle.getInt("countInt");
            this.recebeResultReceiver.atualizaTempoPausa(count,countInt);
        }

        super.onReceiveResult(resultCode, bundle);
    }
}
