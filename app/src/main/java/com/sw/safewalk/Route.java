package com.sw.safewalk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Route {
    String allPaths[][];
    GoogleMap map;

    Route(GoogleMap map){
        this.map = map;
    }

    public void sendRequest(ArrayList<Marker> markersArray) {

        int sizeArray = markersArray.size();
        //criando array de tamanho n*n para guardar a rota de todo mundo para todo mundo
        allPaths = new String[sizeArray][sizeArray];

        for(int i=0; i<sizeArray; i++){
            for(int j=0; j<sizeArray; j++){
               allPaths[i][j] = new String();
            }
        }

        for(int i=0; i<sizeArray; i++){
            for(int j=0; j<sizeArray; j++){
                //evitando auto-rota
                if(i != j){
                    try {
                        getRoute(markersArray.get(i), markersArray.get(j), i, j);
                    }catch(JSONException js){}
                }
            }
        }
        try {
            TimeUnit.SECONDS.sleep(5);
        }catch (java.lang.InterruptedException e){}
        try {
            printDistances(sizeArray);
        }catch(JSONException j){}
    }

    private void getRoute(final Marker start, final Marker end, final int startPos, final int endPos) throws JSONException{

        JSONObject jsonStartPos = new JSONObject();
        JSONObject jsonEndPos   = new JSONObject();
        JSONObject auxStart     = new JSONObject();
        JSONObject auxEnd       = new JSONObject();
        JSONObject options      = new JSONObject();

        ArrayList<JSONObject> locArray = new ArrayList<JSONObject>();
        final JSONObject finalJSON = new JSONObject();

        try {
            auxStart.put("lat", start.getPosition().latitude);
            auxStart.put("lng", start.getPosition().longitude);
            auxEnd.put("lat", end.getPosition().latitude);
            auxEnd.put("lng", end.getPosition().longitude);

            jsonStartPos.put("latLng", auxStart);
            jsonEndPos.put("latLng", auxEnd);

            options.put("generalize", 0);
            options.put("unit", "m");

            locArray.add(jsonStartPos);
            locArray.add(jsonEndPos);
            JSONArray jsonLocations = new JSONArray(locArray);
            finalJSON.put("locations", jsonLocations);
            finalJSON.put("options", options);
        }catch(JSONException j){}

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //setando configurações de rede NÃO MEXER
                    URL url = new URL("https://www.mapquestapi.com/directions/v2/route?key=itGoXMuE2XALZ9TDBSUBk5exvgOYgzw6");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    //NÃO MEXER

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(finalJSON.toString());

                    os.flush();
                    os.close();

                    String jsonDeResposta = new Scanner(conn.getInputStream()).nextLine();
                    if(startPos != endPos)allPaths[startPos][endPos] = jsonDeResposta;
                    conn.disconnect();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    void printDistances(int sizeArray) throws JSONException {
        JSONObject responseObject = new JSONObject();
        JSONArray arrayShapePoints = new JSONArray();
        ArrayList<Double> arrayPoints = new ArrayList<Double>();
        int countColor = 0;
        int[] colorArray = {Color.RED, Color.BLUE, Color.BLACK, Color.GREEN, Color.YELLOW, Color.CYAN};



        for(int i=0; i<sizeArray; i++){
            for(int j=0; j<sizeArray; j++){
                //evitando auto-rota
                if(i != j) {
                    Log.d("TESTE", allPaths[i][j]);
                    try{
                        responseObject = new JSONObject(allPaths[i][j]);
                     }catch(JSONException exc){
                        Log.d("VISH", exc.getMessage());
                    }
                    Log.d("Rota", "Rota["+Integer.toString(i)+"]"+"["+Integer.toString(j)+"] = " + responseObject.getJSONObject("route").getString("distance"));
                    arrayShapePoints = responseObject.getJSONObject("route").getJSONObject("shape").getJSONArray("shapePoints");
                    String arrayString = new String();
                    arrayString = arrayShapePoints.toString();
                    arrayString = arrayString.replace("[", "");
                    arrayString = arrayString.replace("]", "");
                    Log.d("Caminho", arrayString);

                    String arrayPath[] = arrayString.split(",");
                    for(int k=0; k<arrayPath.length; k += 2){
                        if(k + 4 < arrayPath.length) {
                            PolylineOptions line =
                                    new PolylineOptions().add(
                                            new LatLng(Double.parseDouble(arrayPath[k]),
                                                    Double.parseDouble(arrayPath[k+1])),
                                            new LatLng(Double.parseDouble(arrayPath[k+2]),
                                                    Double.parseDouble(arrayPath[k+3])))
                                            .width(5).color(colorArray[countColor]);
                            map.addPolyline(line);
                        }
                    }
                    countColor++;
                }
            }
        }
    }
}