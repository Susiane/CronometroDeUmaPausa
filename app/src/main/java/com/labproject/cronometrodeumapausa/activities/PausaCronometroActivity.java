package com.labproject.cronometrodeumapausa.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.labproject.cronometrodeumapausa.R;
import com.labproject.cronometrodeumapausa.receivers.CronometroResultReceiver;
import com.labproject.cronometrodeumapausa.dao.CronometroDao;
import com.labproject.cronometrodeumapausa.model.Atividade;
import com.labproject.cronometrodeumapausa.model.AtividadeSelecionadaPref;
import com.labproject.cronometrodeumapausa.services.CronometroIntentService;

public class PausaCronometroActivity extends AppCompatActivity implements CronometroResultReceiver.RecebeResultReceiver {

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private boolean isPausado;
    private boolean isSeekChange;
    private AtividadeSelecionadaPref atividadeSelecionadaPref;
    private SharedPreferences sp1;
    private static final String PREF_NAME = "CronometroPreferences";
    private int tempoAtividade;
    private int tempoPausa;
    private TextView nomeAtividade;
    private SeekBar seekPausa;
    private TextView textoPausa;
    private TextView textoAtividade;
    private SeekBar seekAtividade;
    private ImageView fundoCronometro;
    private TextView cronometro;
    private TextView botaoPausar;
    private Intent intent;
    private int count;
    private CronometroResultReceiver rr;
    private String tempo;
    private Dialog caixaDialogoSalvarAlteracoes;
    private TextView textVotar;
    private Button buttonSim, buttonNao;
    private CronometroDao cronometroDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pausa_cronometro);

        inicializaVariaveis();
        inicializaElementosTela();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakelockTag");
        wakeLock.acquire();

    }

    @Override
    protected void onStart() {
        iniciaCronometroService();
        super.onStart();
    }


    public void inicializaVariaveis(){
        isPausado = false;
        isSeekChange = false;

        atividadeSelecionadaPref = new AtividadeSelecionadaPref();
        sp1 = getSharedPreferences(PREF_NAME,0);

        atividadeSelecionadaPref.setId(sp1.getLong("atividade_id", 0));
        atividadeSelecionadaPref.setTituloAtividade(sp1.getString("atividade_nome",""));
        atividadeSelecionadaPref.setTempoAtividade(sp1.getInt("tempo_atividade",0));
        atividadeSelecionadaPref.setTempoAtividadeNovo(sp1.getInt("tempo_atividade_novo",0));
        atividadeSelecionadaPref.setTempoPausa(sp1.getInt("tempo_pausa",0));
        atividadeSelecionadaPref.setTempoPausaNovo(sp1.getInt("tempo_pausa_novo",0));

        tempoAtividade = atividadeSelecionadaPref.getTempoAtividade();
        tempoPausa = atividadeSelecionadaPref.getTempoPausa();
    }


    public void iniciaCronometroService(){
        rr = null;
        rr = new CronometroResultReceiver(null,this);
        intent = new Intent(PausaCronometroActivity.this, CronometroIntentService.class);
        intent.putExtra("receiver",rr);
        startService(intent);

    }

    public void pararCronometroService(){
        intent = new Intent(PausaCronometroActivity.this, CronometroIntentService.class);
        intent.putExtra("desligar",1);
        startService(intent);
    }
    @Override
    public void atualizaTempoAtividade(String tempoAtividade, int countInt) {
        tempo = tempoAtividade;
        this.count = countInt;
        runOnUiThread(runnableAtividade);
    }

    @Override
    public void atualizaTempoPausa(String tempoPausa, int countInt) {
        tempo = tempoPausa;
        this.count = countInt;
        runOnUiThread(runnablePausa);
    }

    public Runnable runnableAtividade = new Runnable() {
        @Override
        public void run() {
            cronometro.setText(tempo);
            fundoCronometro.setImageResource(R.drawable.pausa_circulo);

        }
    };

    public Runnable runnablePausa = new Runnable() {
        @Override
        public void run() {
            cronometro.setText(tempo);
            fundoCronometro.setImageResource(R.drawable.pausa_circulo_rosa);
        }
    };

    @Override
    public void onBackPressed() {
        if (atividadeSelecionadaPref.getTempoAtividade()!= atividadeSelecionadaPref.getTempoAtividadeNovo() || atividadeSelecionadaPref.getTempoPausa() != atividadeSelecionadaPref.getTempoPausaNovo() ){
            abreCaixaSalvarAlteracoes();
        } else {
            super.onBackPressed();
        }
    }

    public void inicializaElementosTela(){

        fundoCronometro = (ImageView)findViewById(R.id.fundo_cronometro);
        cronometro = (TextView) findViewById(R.id.cronometro_pausa);

        nomeAtividade = (TextView)findViewById(R.id.nome_atividade_crono);
        nomeAtividade.setText(atividadeSelecionadaPref.getTituloAtividade());

        seekPausa = (SeekBar)findViewById(R.id.seekBarTempoPausaCrono);
        seekPausa.setMax(60);
        seekPausa.setProgress(tempoPausa);
        seekPausa.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textoPausa.setText(seekPausa.getProgress() + " min");
                if (seekPausa.getProgress() == 60) {
                    textoPausa.setText("1 hora");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(tempoPausa != seekPausa.getProgress()){
                    if(seekPausa.getProgress()== 0 ){
                        Toast.makeText(PausaCronometroActivity.this, "O valor escolhido não pode ser zero",Toast.LENGTH_SHORT).show();
                    }else{
                        tempoPausa = seekPausa.getProgress();
                        alteraTempoPausaPref();
                        atualizaTempoPausaNovoAtividadeSelecionadaPref();
                        pararCronometroService();

                        cronometro.setText("00:00");
                        fundoCronometro.setImageResource(R.drawable.pausa_circulo_cinza);
                        isPausado = true;
                        botaoPausar.setText("Começar");
                        isSeekChange = true;
                    }
                }
            }
        });

        seekAtividade = (SeekBar)findViewById(R.id.seekBarTempoAtividadeCrono);
        seekAtividade.setMax(60);
        seekAtividade.setProgress(tempoAtividade);
        seekAtividade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textoAtividade.setText(seekAtividade.getProgress() + " min");
                if (seekAtividade.getProgress() == 60) {
                    textoAtividade.setText("1 hora");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(tempoAtividade != seekAtividade.getProgress() ){
                    if(seekAtividade.getProgress() == 0 ){
                        Toast.makeText(PausaCronometroActivity.this, "O valor escolhido não pode ser zero",Toast.LENGTH_SHORT).show();
                    }else{
                        tempoAtividade = seekAtividade.getProgress();

                        alteraTempoAtividadePref();
                        atualizaTempoAtividadeNovoAtividadeSelecionadaPref();
                        pararCronometroService();

                        cronometro.setText("00:00");
                        fundoCronometro.setImageResource(R.drawable.pausa_circulo_cinza);
                        isPausado = true;
                        botaoPausar.setText("Começar");
                        isSeekChange = true;
                    }
                }
            }
        });

        textoAtividade = (TextView)findViewById(R.id.minutosAtividadeCrono);
        if(tempoAtividade == 60) {
            textoAtividade.setText("1 hora");
        }else{
            textoAtividade.setText(tempoAtividade +" min");
        }

        textoPausa = (TextView)findViewById(R.id.minutosPausaCrono);
        if(tempoPausa == 60){
            textoPausa.setText("1 hora");
        }else{
            textoPausa.setText(tempoPausa +" min");
        }



        botaoPausar = (TextView)findViewById(R.id.botaoPausar);
        botaoPausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPausado) {
                    if(!isSeekChange) {
                        alteraPrefParar();

                    }
                    iniciaCronometroService();
                    isPausado = false;
                    botaoPausar.setText("Parar");
                } else {
                    pararCronometroService();
                    fundoCronometro.setImageResource(R.drawable.pausa_circulo_cinza);
                    isPausado = true;
                    botaoPausar.setText("Começar");
                    isSeekChange = false;

                }
            }
        });
    }

    //pq o Service utiliza essa variavel em sua inicianlização
    public void alteraTempoAtividadePref(){
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putInt("tempo_atividade_novo", tempoAtividade);
        editor.commit();
    }

    //pq o Service utiliza essa variavel em sua inicianlização
    public void alteraTempoPausaPref(){
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putInt("tempo_pausa_novo", tempoPausa);
        editor.commit();
    }

    //para comparar e decidir no onBackPressed()
    public void atualizaTempoAtividadeNovoAtividadeSelecionadaPref(){
        atividadeSelecionadaPref.setTempoAtividadeNovo(tempoAtividade);
    }

    //para comparar e decidir no onBackPressed()
    public void atualizaTempoPausaNovoAtividadeSelecionadaPref(){
        atividadeSelecionadaPref.setTempoPausaNovo(tempoPausa);
    }

    //para quando reiniciar o Service ele contiruar do minuto que parou
    public void alteraPrefParar(){
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putBoolean("parar", true);
        editor.putInt("count_que_parou",count);
        editor.commit();
    }

    public void abreCaixaSalvarAlteracoes(){
        caixaDialogoSalvarAlteracoes = new Dialog(this);
        caixaDialogoSalvarAlteracoes.setContentView(R.layout.dialogo_pausa_salvar_alteracoes);
        textVotar = (TextView) caixaDialogoSalvarAlteracoes.findViewById(R.id.botao_voltar);
        buttonSim = (Button) caixaDialogoSalvarAlteracoes.findViewById(R.id.pausa_dialogo_sim);
        buttonNao = (Button) caixaDialogoSalvarAlteracoes.findViewById(R.id.pausa_dialogo_nao);


        final Atividade atividadeAtual = new Atividade();
        atividadeAtual.setId(atividadeSelecionadaPref.getId());
        atividadeAtual.setTituloAtividade(atividadeSelecionadaPref.getTituloAtividade());
        atividadeAtual.setTempoAtividade(tempoAtividade);
        atividadeAtual.setTempoPausa(tempoPausa);

        buttonNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caixaDialogoSalvarAlteracoes.dismiss();
                pararCronometroService();
                finish();
            }
        });

        buttonSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cronometroDao = new CronometroDao(PausaCronometroActivity.this);
                cronometroDao.alteraAtividade(atividadeAtual);
                cronometroDao.close();

                Toast.makeText(PausaCronometroActivity.this,"As alterações em "+atividadeAtual.getTituloAtividade().toString()+" foram salvas.",Toast.LENGTH_SHORT).show();
                caixaDialogoSalvarAlteracoes.dismiss();
                pararCronometroService();
                finish();
            }
        });

        textVotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caixaDialogoSalvarAlteracoes.dismiss();
            }
        });

        caixaDialogoSalvarAlteracoes.show();
    }



}
