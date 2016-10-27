package com.labproject.cronometrodeumapausa.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.labproject.cronometrodeumapausa.R;
import com.labproject.cronometrodeumapausa.adapter.AdapterAtividadePausa;
import com.labproject.cronometrodeumapausa.dao.CronometroDao;
import com.labproject.cronometrodeumapausa.model.Atividade;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listViewAtividades;
    private List<Atividade> listaAtividades;
    private CronometroDao cronometroDao;
    private AdapterAtividadePausa adapter;
    private static final String PREF_NAME = "CronometroPreferences";
    private Atividade atividadeSelecionada;
    private Intent intent;
    private SharedPreferences sp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listViewAtividades = (ListView)findViewById(R.id.pausa_lista_atividades);
    }

    @Override
    protected void onResume() {
        listaAtividades = new ArrayList<Atividade>();
        cronometroDao = new CronometroDao(this);
        listaAtividades = cronometroDao.getListaAtividade();
        cronometroDao.close();


        adapter = new AdapterAtividadePausa(this,listaAtividades);

        listViewAtividades.setAdapter(adapter);


        listViewAtividades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                atividadeSelecionada = (Atividade) parent.getItemAtPosition(position);
                gravaPrefPausa(atividadeSelecionada);
                intent = new Intent(MainActivity.this,PausaCronometroActivity.class);
                startActivity(intent);

            }
        });
        Log.d("TAG", "Passei no onResume() do PausaHomeFragment");

        super.onResume();
    }

    public void gravaPrefPausa(Atividade atividadeSelecionada){

        sp1 = getSharedPreferences(PREF_NAME,0);
        SharedPreferences.Editor  editor = sp1.edit();
        editor.putLong("atividade_id", atividadeSelecionada.getId());
        editor.putString("atividade_nome", atividadeSelecionada.getTituloAtividade());
        editor.putBoolean("atividade_estado", true);
        editor.putBoolean("atividade_voltando",false);
        editor.putInt("tempo_atividade_novo",atividadeSelecionada.getTempoAtividade());
        editor.putInt("tempo_atividade",atividadeSelecionada.getTempoAtividade());
        editor.putInt("tempo_pausa_novo",atividadeSelecionada.getTempoPausa());
        editor.putInt("tempo_pausa",atividadeSelecionada.getTempoPausa());
        editor.putInt("milissegundos",0);
        editor.commit();


    }
}
