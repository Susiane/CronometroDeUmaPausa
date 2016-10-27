package com.labproject.cronometrodeumapausa.model;

import java.io.Serializable;

/**
 * Created by Susiane on 09/08/2016.
 */
public class Atividade implements Serializable {
    private Long id;
    private String tituloAtividade;
    private int tempoAtividade;
    private int tempoPausa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTituloAtividade() {
        return tituloAtividade;
    }

    public void setTituloAtividade(String tituloAtividade) {
        this.tituloAtividade = tituloAtividade;
    }

    public int getTempoAtividade() {
        return tempoAtividade;
    }

    public void setTempoAtividade(int tempoAtividade) {
        this.tempoAtividade = tempoAtividade;
    }

    public int getTempoPausa() {
        return tempoPausa;
    }

    public void setTempoPausa(int tempoPausa) {
        this.tempoPausa = tempoPausa;
    }
}
