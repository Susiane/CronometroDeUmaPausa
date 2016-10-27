package com.labproject.cronometrodeumapausa.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.labproject.cronometrodeumapausa.R;
import com.labproject.cronometrodeumapausa.dao.CronometroDao;
import com.labproject.cronometrodeumapausa.model.Atividade;

import java.util.List;

/**
 * Created by Susiane on 09/08/2016.
 */
public class AdapterAtividadePausa extends BaseAdapter {
    private Context context;
    private List<Atividade> itensLista;
    private Dialog caixaDialogoDeletar;
    private TextView textDesejaDeletar;
    private Button botaoSim, botaoNao;
    private int posicaoAux;
    private CronometroDao cronometroDao;

    public AdapterAtividadePausa(Context context, List<Atividade> itens){
        this.context = context;
        this.itensLista = itens;

    }
    private static class ViewHolder{
        private TextView textoTitulo;
        private ImageView deletar;
    }

    @Override
    public int getCount() {
        return itensLista.size();
    }

    @Override
    public Object getItem(int position) {
        return itensLista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_atividades_pausa,parent,false);
            holder = new ViewHolder();

            holder.textoTitulo = (TextView) convertView.findViewById(R.id.titulo_atividade_pausa);
            holder.deletar = (ImageView) convertView.findViewById(R.id.deletar_atividade_pausa);


            convertView.setTag(holder);

        }else{

            holder = (ViewHolder) convertView.getTag();
        }

        final Atividade itemAtividade = (Atividade) getItem(position);


        holder.textoTitulo.setText(itemAtividade.getTituloAtividade());

        holder.deletar.setTag(position);
        //posicaoAux = position;

        holder.deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posicaoAux = (Integer) v.getTag();
                caixaDialogoDeletar = new Dialog(context);
                caixaDialogoDeletar.setContentView(R.layout.dialogo_deletar);

                textDesejaDeletar = (TextView)caixaDialogoDeletar.findViewById(R.id.deseja_deletar_text);
                textDesejaDeletar.setText(context.getResources().getString(R.string.dialogo_deletar_pausa_atividade));
                botaoNao = (Button) caixaDialogoDeletar.findViewById(R.id.dialogo_nao_deletar);
                botaoSim = (Button) caixaDialogoDeletar.findViewById(R.id.dialogo_sim_deletar);

                botaoSim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cronometroDao = new CronometroDao(context);
                        cronometroDao.deletarAtividade(Integer.valueOf(itemAtividade.getId().toString()));
                        cronometroDao.close();
                        itensLista.remove(posicaoAux);
                        notifyDataSetChanged();
                        caixaDialogoDeletar.dismiss();

                    }
                });

                botaoNao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        caixaDialogoDeletar.dismiss();
                    }
                });

                caixaDialogoDeletar.show();



            }
        });

        return convertView;
    }

}
