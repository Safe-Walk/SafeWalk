package com.sw.safewalk;

public class Incident {
    String crimeSelecionado;
    String descricao;
    Integer nivel;
    Double latitude;
    Double longitude;

    public Incident() {}

    public Incident(String crimeSelecionado, String descricao, Integer nivel, Double latitude, Double longitude) {
        this.crimeSelecionado = crimeSelecionado;
        this.descricao = descricao;
        this.nivel = nivel;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Integer getNivel() {
        return nivel;
    }

    public String getCrimeSelecionado() {
        return crimeSelecionado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setCrimeSelecionado(String crimeSelecionado) {
        this.crimeSelecionado = crimeSelecionado;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String toString() {
        return this.crimeSelecionado + " " + this.descricao + " " + this.nivel + " " + this.latitude + " " + this.longitude;
    }

    public int getPhoto() {
        return 1;
    }
}
