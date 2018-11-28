package com.sw.safewalk;

public class Incident {
    String crimeSelecionado;
    String descricao;
    Integer nivel;
    Double latitude;
    Double longitude;
    Long horario;

    public Incident() {}

    public Incident(String crimeSelecionado, String descricao, Integer nivel, Double latitude, Double longitude, Long horario) {
        this.crimeSelecionado = crimeSelecionado;
        this.descricao = descricao;
        this.nivel = nivel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.horario = horario;
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

    public Long getHorario() {
        return horario;
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

    public void setHorario(Long horario) {
        this.horario = horario;
    }

    public String toString() {
        return this.crimeSelecionado + " " + this.descricao + " " + this.nivel + " " + this.latitude + " " + this.longitude + " " + this.horario;
    }
}
