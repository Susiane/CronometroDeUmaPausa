package com.labproject.cronometrodeumapausa.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.labproject.cronometrodeumapausa.model.Atividade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Susiane on 09/08/2016.
 */
public class CronometroDao extends SQLiteOpenHelper {

    private static final int VERSAO = 01;
    private static final String TABELA_ATIVIDADE = "Atividade";
    private  static final String DATABASE = "DadosCronometro";

    public CronometroDao (Context context){
        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create1 = "CREATE TABLE "+ TABELA_ATIVIDADE +
                " (id INTEGER PRIMARY KEY, "+
                "tituloAtividade TEXT NOT NULL, "+
                "tempoAtividade INTEGER, "+
                "tempoPausa INTEGER);";
        sqLiteDatabase.execSQL(create1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<Atividade> getListaAtividade(){
        List<Atividade> atividadesList = new ArrayList<Atividade>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM "+ TABELA_ATIVIDADE +";", null);

        while (c.moveToNext()) {
            Atividade atividade = new Atividade();

            atividade.setId(c.getLong(c.getColumnIndex("id")));
            atividade.setTituloAtividade(c.getString(c.getColumnIndex("tituloAtividade")));
            atividade.setTempoAtividade(c.getInt(c.getColumnIndex("tempoAtividade")));
            atividade.setTempoPausa(c.getInt(c.getColumnIndex("tempoPausa")));

            atividadesList.add(atividade);
        }

        c.close();
        return atividadesList;
    }

    public void deletarAtividade(int idAtividade){
        String[] args = {Integer.toString(idAtividade)};
        getWritableDatabase().delete(TABELA_ATIVIDADE, "id=?",args);
    }

    public void alteraAtividade(Atividade atividade){
        ContentValues values  = new ContentValues();

        values.put("tituloAtividade", atividade.getTituloAtividade());
        values.put("tempoAtividade", atividade.getTempoAtividade());
        values.put("tempoPausa", atividade.getTempoPausa());


        String[] idParaSerAlterado = {atividade.getId().toString()};
        getWritableDatabase().update(TABELA_ATIVIDADE, values, "id=?", idParaSerAlterado);

        Log.d("TAG", "Valores de atividade " + atividade.getTituloAtividade() + " alterados - ID: " + atividade.getId()
                + " - TempoAtividade: " + atividade.getTempoAtividade()
                + "  - TempoPausa: " + atividade.getTempoPausa());
    }
}
