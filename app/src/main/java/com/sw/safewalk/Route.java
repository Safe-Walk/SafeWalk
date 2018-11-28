package com.sw.safewalk;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Route {
    String allPaths[][];
    GoogleMap map;
    ArrayList<LatLng> avoidArray = new ArrayList<LatLng>();
    Double distanceMatrix[][];
    int sizeGraph, parent[][];
    ArrayList<Marker> markersArray;
    ArrayList<Polyline> arrayLine;
    ArrayList<Long> crimeTime;
    ArrayList<Integer> crimeWeight;
    ExecutorService execThreads;
    Context activityContext;

    Route(GoogleMap map, Context activityContext){
        this.map = map;
        this.activityContext = activityContext;
    }

    public void sendRequest(ArrayList<Marker> markersArray, ArrayList<LatLng> avoidArray, ArrayList<Long> crimeTime, ArrayList<Integer> crimeWeight) {
        this.avoidArray = avoidArray;
        int sizeArray = markersArray.size();
        this.sizeGraph = sizeArray;
        this.markersArray = markersArray;
        this.crimeTime = crimeTime;
        this.crimeWeight = crimeWeight;
        if(arrayLine == null)arrayLine = new ArrayList<Polyline>();
        execThreads = Executors.newCachedThreadPool();

        allPaths = new String[sizeArray][sizeArray];            //rota entre todos os pontos
        distanceMatrix = new Double[sizeArray][sizeArray];      //apenas a distancia entre todos os pontos

        for(int i=0; i<sizeArray; i++){
            for(int j=0; j<sizeArray; j++){
               allPaths[i][j] = new String();
               distanceMatrix[i][j] = 0.0;
            }
        }

        for(int i=0; i<avoidArray.size(); i++){
            LatLng m = avoidArray.get(i);
            Log.d("JSONNEW", Double.toString(m.latitude) + ',' + Double.toString(m.longitude));
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
        execThreads.shutdown();
        try{
            execThreads.awaitTermination(10,TimeUnit.SECONDS);
        }catch(InterruptedException ie){
            //Toast.makeText(activityContext, "Tempo limite excedido!",Toast.LENGTH_LONG).show();
        }
        /* try {
           TimeUnit.SECONDS.sleep(20);//TODO SINCRONIZAR THREADS
       }catch (java.lang.InterruptedException e){}*/

        for(int i=0; i<sizeArray; i++){                             //pegando matriz de distancias
            for(int j=0; j<sizeArray; j++){
                if(i==j){
                    distanceMatrix[i][j] = 0.0;
                    Log.d("distancia", Double.toString(distanceMatrix[i][j]));
                }
                else{
                    try{
                        JSONObject JSONDistance = new JSONObject(allPaths[i][j]);
                        distanceMatrix[i][j] = JSONDistance.getJSONObject("route").getDouble("distance");
                        Log.d("distancia", Double.toString(distanceMatrix[i][j]));
                    }catch(JSONException js){}
                }
            }
        }
        getTspRoute();
        Log.d("asdfggg", "cabo");
        ArrayList<Integer> path = getPath();
        try {
            printPath(path);
        }catch(JSONException js){}

        int j = 2;
        markersArray.get(0).setTitle("1");
        for(int i: path){
            markersArray.get(i).setTitle(Integer.toString(j));
            j++;
        }
    }

    private void getRoute(final Marker start, final Marker end, final int startPos, final int endPos) throws JSONException{

        JSONObject jsonStartPos = new JSONObject();
        JSONObject jsonEndPos   = new JSONObject();
        JSONObject auxStart     = new JSONObject();
        JSONObject auxEnd       = new JSONObject();
        JSONObject options      = new JSONObject();

        ArrayList<JSONObject> locArray = new ArrayList<JSONObject>();
        ArrayList<JSONObject> routeControl = new ArrayList<JSONObject>();
        final JSONObject finalJSON = new JSONObject();

        try {
            auxStart.put("lat", start.getPosition().latitude);
            auxStart.put("lng", start.getPosition().longitude);
            auxEnd.put("lat", end.getPosition().latitude);
            auxEnd.put("lng", end.getPosition().longitude);

            jsonStartPos.put("latLng", auxStart);
            jsonEndPos.put("latLng", auxEnd);

            options.put("generalize", 0);
            options.put("unit", "k");
            options.put("routeType", "pedestrian");
            options.put("shapeFormat", "raw");

            for(int i=0; i<avoidArray.size(); i++){
                Timestamp aux = new Timestamp(crimeTime.get(i));
                long days = Math.abs(Calendar.getInstance().getTime().getTime() - aux.getTime()) / (1000*60*60*24);

                if(days <= 14) {
                    JSONObject routeCtrlOp = new JSONObject();
                    LatLng m = avoidArray.get(i);
                    routeCtrlOp.put("lat", m.latitude);
                    routeCtrlOp.put("lng", m.longitude);
                    routeCtrlOp.put("weight", Math.max(1.1, 4.0 - (Double.valueOf(days)*0.3 )));  //cada dia desconta 0.3
                    routeCtrlOp.put("radius", crimeWeight.get(i)*0.01);
                    Log.d("Raio", "Raio: " + Double.valueOf(0.01*crimeWeight.get(i)).toString());
                    Log.d("Peso", "Peso: " + Double.valueOf(Math.max(1.1, 4.0 - (Double.valueOf(days)*0.3 ))).toString());
                    routeControl.add(routeCtrlOp);
                }
            }

            locArray.add(jsonStartPos);
            locArray.add(jsonEndPos);
            JSONArray jsonLocations = new JSONArray(locArray);

            JSONArray routeControlArray = new JSONArray(routeControl);
            options.put("routeControlPointCollection", routeControlArray);

            finalJSON.put("locations", jsonLocations);
            finalJSON.put("options", options); 
            Log.d("AAAAA", finalJSON.toString());
        }catch(JSONException j){}

        execThreads.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //setando configurações de rede NÃO MEXER
                    URL url = new URL("https://www.mapquestapi.com/directions/v2/route?key=3FVbEMC55uCn6aYDFh8LGPfWnGaZskmA");
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
                    Log.d("Resposta", jsonDeResposta);
                    if(startPos != endPos)allPaths[startPos][endPos] = jsonDeResposta;
                    conn.disconnect();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void printPath(ArrayList<Integer> path) throws JSONException {
        JSONObject responseObject = new JSONObject();
        JSONArray arrayShapePoints = new JSONArray();
        ArrayList<Double> arrayPoints = new ArrayList<Double>();
        int countColor = 0;
        int[] colorArray = {Color.BLUE, Color.BLACK, Color.GREEN, Color.YELLOW, Color.CYAN};

        int current = 0;
        for(Integer i: path){
            Log.d("pontos", Integer.toString(current) + " -> " +Integer.toString(i));
            try{
                responseObject = new JSONObject(allPaths[current][i]);
            }catch(JSONException exc){
                Log.d("VISH", exc.getMessage());
            }
            arrayShapePoints = responseObject.getJSONObject("route").getJSONObject("shape").getJSONArray("shapePoints");
            String arrayString = new String();
            arrayString = arrayShapePoints.toString();
            arrayString = arrayString.replace("[", "");
            arrayString = arrayString.replace("]", "");


            arrayString =   Double.toString(markersArray.get(current).getPosition().latitude) + ',' +
                            Double.toString(markersArray.get(current).getPosition().longitude) + ',' + arrayString + ',' +
                            Double.toString(markersArray.get(i).getPosition().latitude) + ',' +
                            Double.toString(markersArray.get(i).getPosition().longitude);

            Log.d("Caminho", arrayString);

            String arrayPath[] = arrayString.split(",");
            for(int k=0; k<arrayPath.length; k += 2){
                if(k + 4 <= arrayPath.length) {
                    PolylineOptions line =
                            new PolylineOptions().add(
                                    new LatLng(Double.parseDouble(arrayPath[k]),
                                            Double.parseDouble(arrayPath[k+1])),
                                    new LatLng(Double.parseDouble(arrayPath[k+2]),
                                            Double.parseDouble(arrayPath[k+3])))
                                    .width(10).color(Color.CYAN);

                    Polyline lineAux = map.addPolyline(line);
                    arrayLine.add(lineAux);
                }

                //TODO TROCAR CORES
            }
            current = i;
            countColor++;
        }
    }

    void getTspRoute(){
        double dp[][] = new double[sizeGraph][1<<sizeGraph];            //n*2^n subproblemas
        parent = new int[sizeGraph][1<<sizeGraph];

        for(int i=0; i<sizeGraph; i++) for(int j=0; j<(1<<sizeGraph); j++) dp[i][j] = Double.MAX_VALUE;
        for(int i=0; i<sizeGraph; i++){
            dp[i][0] = 0.0;                             //casos base
        }
        for(int bitmask=1; bitmask<(1<<sizeGraph); bitmask++){ //para cada subconjunto de vértices
            for(int j=0; j<sizeGraph; j++){     //para cada vértice
                double minValue = Double.MAX_VALUE;
                int minVertice;

                if((bitmask&(1<<j)) == 0){            // se o elemento j não está em i
                    for(int k=1; k<sizeGraph; k++){
                        if((bitmask&(1<<k)) != 0){    // se k está em i
                            if(distanceMatrix[j][k] + dp[k][(bitmask&(~(1<<k)))] < dp[j][bitmask]){
                                dp[j][bitmask]     = distanceMatrix[j][k] + dp[k][(bitmask&(~(1<<k)))];
                                parent[j][bitmask] = k;
                            }
                        }
                    }
                }
            }
        }
        //printf("%d\n", dp[0][254]);
    }
    ArrayList<Integer> getPath(){
        ArrayList<Integer> path = new ArrayList<Integer>();
        int i = 0, bitmask = (1<<sizeGraph) - 2;                //todos os vertices exceto a origem

        while(bitmask > 0){
            i = parent[i][bitmask];
            bitmask = (bitmask&(~(1<<i)));
            path.add(i);
        }
        return path;
    }

    void clearLine(){
        if(arrayLine != null) {
            for (Polyline aux : arrayLine) {
                aux.remove();
            }
            arrayLine.clear();
        }
        arrayLine = new ArrayList<Polyline>();
    }
}
