package com.labproject.cronometrodeumapausa.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.labproject.cronometrodeumapausa.R;
import com.labproject.cronometrodeumapausa.activities.PausaCronometroActivity;

import java.text.SimpleDateFormat;

/**
 * Created by Susiane on 09/08/2016.
 */
public class CronometroIntentService extends IntentService {
    private boolean atividade;
    private int count;
    private long countMult;
    private boolean ativo;
    private ResultReceiver rr;
    public static final int ID_NOTIFICACAO = 631050;
    private SharedPreferences sp1;
    private static final String PREF_NAME = "CronometroPreferences";
    private int tempoAtividade, tempoPausa;
    private long tempoAtividadeLong, tempoPausaLong, tempoAtividadeRestante, tempoPausaRestante;
    private MediaPlayer mp;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private boolean pausa;
    private boolean restanteAtividade;
    private boolean restantePausa;
    private long tempoQuePassou;
    private long tempoAtividadeRestanteAnterior;
    private long timeMillisInicioCiclo;
    private long timeMillisInicioMinuto;
    private long tempoPausaRestanteAnterior;
    private String statusCiclo;
    private Bundle b;
    private String tempo;
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    public CronometroIntentService() {
        super("IntentServiceThread");
        count = 0;
        countMult = 0;
        ativo = true;
        atividade = true;
        Log.d("TAG", "Construtor CronometroIntentService()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "onStartCommand() do CronometroIntentService()");

        rr = intent.getParcelableExtra("receiver");
        Intent intentNotificacao = new Intent(this, PausaCronometroActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNotificacao, 0);

        Notification notificacao = new Notification.Builder(this)
                .setContentTitle("Dê uma pausa - ProFibro")
                .setContentText("Cronômetro")
                .setSmallIcon(R.drawable.de_uma_pausa_menu)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(ID_NOTIFICACAO, notificacao);
        setIntentRedelivery(true);

        sp1 = getSharedPreferences(PREF_NAME,0);
        tempoAtividade = sp1.getInt("tempo_atividade_novo",0);
        tempoPausa = sp1.getInt("tempo_pausa_novo",0);
        tempoAtividadeLong = tempoAtividade * 60;
        tempoPausaLong = tempoPausa * 60;

        Log.d("TAG","tempoAtividade: "+tempoAtividade);
        Log.d("TAG","tempoPausa: "+tempoPausa);

        if(sp1.getBoolean("parar",false)){
            Log.d("TAG","sp1.getBoolean \"parar\" TRUE no onStartCommand() do CronometroIntentService()");
            configuraParaContinuarDeOndeParou();
        }

        mp = MediaPlayer.create(this, R.raw.msg);
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakelockTag");
        wakeLock.acquire();

        Bundle bundle = intent.getExtras();
        if(bundle != null){
            int desligar = bundle.getInt("desligar");
            if(desligar == 1){
                ativo = false;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void configuraParaContinuarDeOndeParou(){
        Log.d("TAG","configuraParaContinuarDeOndeParou() do CronometroIntentService()");
        alteraPrefPararFalse();
        switch (sp1.getString("status_ciclo_que_parou","")){
            case "CICLO_ATIVIDADE":{
                atividade = false;
                pausa = false;
                restanteAtividade = true;
                restantePausa = false;
                tempoQuePassou = sp1.getLong("time_millis_que_parou", 0 );
                tempoAtividadeRestante = (tempoAtividadeLong*1000) - tempoQuePassou;
                gravaTempoAtividadeRestante();
                count = sp1.getInt("count_que_parou",0);
                break;
            }
            case "CICLO_PAUSA":{
                atividade = false;
                pausa = false;
                restanteAtividade = false;
                restantePausa = true;
                tempoQuePassou = sp1.getLong("time_millis_que_parou", 0 );
                tempoPausaRestante = (tempoPausaLong*1000) - tempoQuePassou;
                gravaTempoPausaRestante();
                count = sp1.getInt("count_que_parou",0);
                break;
            }
            case "CICLO_RESTANTE_ATIVIDADE":{
                atividade = false;
                pausa = false;
                restanteAtividade = true;
                restantePausa = false;
                tempoQuePassou = sp1.getLong("time_millis_que_parou", 0 );
                tempoAtividadeRestanteAnterior = sp1.getLong("time_millis_tempo_restante_atividade",0);
                tempoAtividadeRestante = tempoAtividadeRestanteAnterior - tempoQuePassou;
                gravaTempoAtividadeRestante();
                count = sp1.getInt("count_que_parou",0);
                break;
            }
            case "CICLO_RESTANTE_PAUSA":{
                atividade = false;
                pausa = false;
                restanteAtividade = false;
                restantePausa = true;
                tempoQuePassou = sp1.getLong("time_millis_que_parou", 0 );
                tempoPausaRestanteAnterior = sp1.getLong("time_millis_tempo_restante_pausa",0);
                tempoPausaRestante = tempoPausaRestanteAnterior - tempoQuePassou;
                gravaTempoPausaRestante();
                count = sp1.getInt("count_que_parou",0);
                break;
            }
        }

    }

    public void gravaPrefTimeStatusQueParou(){
        Log.d("TAG","gravaPrefTimeStatusQueParou() do CronometroIntentService()");
        long timeMillisQueParou = System.currentTimeMillis() - timeMillisInicioCiclo;
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putLong("time_millis_que_parou", timeMillisQueParou);
        editor.putString("status_ciclo_que_parou", statusCiclo);
        editor.commit();
    }
    public void alteraPrefPararFalse(){
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putBoolean("parar", false);
        editor.commit();
    }
    public void gravaTempoAtividadeRestante(){
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putLong("time_millis_tempo_restante_atividade", tempoAtividadeRestante);
        editor.commit();
    }
    public void gravaTempoPausaRestante(){
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putLong("time_millis_tempo_restante_pausa", tempoPausaRestante);
        editor.commit();
    }
    
    
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("TAG", "onHandleIntent() do CronometroIntentService() ");

        b = new Bundle();

        timeMillisInicioCiclo = System.currentTimeMillis();
        timeMillisInicioMinuto = System.currentTimeMillis();

        while (ativo){

            if (atividade) {
                statusCiclo = "CICLO_ATIVIDADE";
                atividade = false;
                pausa = true;
                count = 0;
                timeMillisInicioCiclo = System.currentTimeMillis();
                timeMillisInicioMinuto = System.currentTimeMillis();

                while (ativo && (System.currentTimeMillis() - timeMillisInicioCiclo) < tempoAtividadeLong*1000) {
                    if ((System.currentTimeMillis() - timeMillisInicioMinuto) == 1000) {
                        timeMillisInicioMinuto = System.currentTimeMillis();
                        count++;
                        countMult = count*1000;
                        tempo = sdf.format(countMult);
                        b.putString("count",tempo);
                        b.putInt("countInt",count);
                        rr.send(1, b);
                        countMult = 0;
                        Log.d("TAG", "COUNT - Atividade : " + count);
                    }
                }

            }
            else if(pausa){
                statusCiclo = "CICLO_PAUSA";
                atividade = true;
                pausa = false;
                count = 0;
                timeMillisInicioCiclo = System.currentTimeMillis();
                timeMillisInicioMinuto = System.currentTimeMillis();

                while (ativo && (System.currentTimeMillis() - timeMillisInicioCiclo) < tempoPausaLong*1000) {
                    if ((System.currentTimeMillis() - timeMillisInicioMinuto) == 1000) {
                        timeMillisInicioMinuto = System.currentTimeMillis();
                        count++;
                        countMult = count*1000;
                        tempo = sdf.format(countMult);
                        b.putString("count",tempo);
                        b.putInt("countInt",count);
                        rr.send(2, b);
                        countMult = 0;
                        Log.d("TAG", "COUNT - Pausa : " + count);
                    }
                }
            }else if(restanteAtividade){
                statusCiclo = "CICLO_RESTANTE_ATIVIDADE";
                pausa = true;
                restanteAtividade = false;
                timeMillisInicioCiclo = System.currentTimeMillis();
                timeMillisInicioMinuto = System.currentTimeMillis();

                while (ativo && (System.currentTimeMillis() - timeMillisInicioCiclo) < tempoAtividadeRestante) {
                    if ((System.currentTimeMillis() - timeMillisInicioMinuto) == 1000) {
                        timeMillisInicioMinuto = System.currentTimeMillis();
                        count++;
                        countMult = count*1000;
                        tempo = sdf.format(countMult);
                        b.putString("count",tempo);
                        b.putInt("countInt",count);
                        countMult = 0;
                        rr.send(1, b);
                        Log.d("TAG", "COUNT - Atividade Restante : " + count);
                    }
                }

            }else if(restantePausa){
                statusCiclo = "CICLO_RESTANTE_PAUSA";
                atividade = true;
                restantePausa = false;
                timeMillisInicioCiclo = System.currentTimeMillis();
                timeMillisInicioMinuto = System.currentTimeMillis();

                while (ativo && (System.currentTimeMillis() - timeMillisInicioCiclo) < tempoPausaRestante) {
                    if ((System.currentTimeMillis() - timeMillisInicioMinuto) == 1000) {
                        timeMillisInicioMinuto = System.currentTimeMillis();
                        count++;
                        countMult = count*1000;
                        tempo = sdf.format(countMult);
                        b.putString("count",tempo);
                        b.putInt("countInt",count);
                        countMult = 0;
                        rr.send(2, b);
                        Log.d("TAG", "COUNT - Pausa Restante: " + count);
                    }
                }
            }

            if(ativo){
                mp.start();

            }else {
                gravaPrefTimeStatusQueParou();
            }
        }
    }
}
