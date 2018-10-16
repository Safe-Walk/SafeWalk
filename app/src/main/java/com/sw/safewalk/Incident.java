package com.sw.safewalk;

import com.google.android.gms.maps.model.LatLng;

public class Incident {
    String crimeSelecionado;
    String descricao;
    Integer nivel;
    LatLng latLng;

    public Incident(String crimeSelecionado, String descricao, Integer nivel, LatLng latLng) {
        this.crimeSelecionado = crimeSelecionado;
        this.descricao = descricao;
        this.nivel = nivel;
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
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
