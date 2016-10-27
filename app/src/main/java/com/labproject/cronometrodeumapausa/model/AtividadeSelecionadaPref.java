package com.labproject.cronometrodeumapausa.model;

import java.io.Serializable;

/**
 * Created by Susiane on 09/08/2016.
 */
public class AtividadeSelecionadaPref implements Serializable {
    private Long id;
    private String tituloAtividade;
    private int tempoAtividade;
    private int tempoAtividadeNovo;
    private int tempoPausa;
    private int tempoPausaNovo;
    private int milissegundos;
    private boolean atividadeEstado;
    private boolean atividadeVoltando;


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

    public int getTempoAtividadeNovo() {
        return tempoAtividadeNovo;
    }

    public void setTempoAtividadeNovo(int tempoAtividadeNovo) {
        this.tempoAtividadeNovo = tempoAtividadeNovo;
    }

    public int getTempoPausa() {
        return tempoPausa;
    }

    public void setTempoPausa(int tempoPausa) {
        this.tempoPausa = tempoPausa;
    }

    public int getTempoPausaNovo() {
        return tempoPausaNovo;
    }

    public void setTempoPausaNovo(int tempoPausaNovo) {
        this.tempoPausaNovo = tempoPausaNovo;
    }

    public int getMilissegundos() {
        return milissegundos;
    }

    public void setMilissegundos(int milissegundos) {
        this.milissegundos = milissegundos;
    }

    public boolean isAtividadeEstado() {
        return atividadeEstado;
    }

    public void setAtividadeEstado(boolean atividadeEstado) {
        this.atividadeEstado = atividadeEstado;
    }

    public boolean isAtividadeVoltando() {
        return atividadeVoltando;
    }

    public void setAtividadeVoltando(boolean atividadeVoltando) {
        this.atividadeVoltando = atividadeVoltando;
    }
}
