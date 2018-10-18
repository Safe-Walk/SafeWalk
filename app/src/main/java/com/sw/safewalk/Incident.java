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
}
