package helpers;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import models.Link;
import models.Location;
import models.PostOffice;
import org.json.JSONArray;
import org.json.JSONObject;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by Edin on 21.10.2015.
 */
public class DijkstraHelper {

    public static List<Vertex> vertexes = getAllVertexes();

    private static class Vertex implements Comparable<Vertex> {
        public final String name;
        public List<Edge> adjacencies = new ArrayList<>();
        public double minDistance = Double.POSITIVE_INFINITY;
        public Vertex previous;

        public Vertex(String argName) {
            name = argName;
        }

        public String toString() {
            return name;
        }

        public int compareTo(Vertex other) {
            return Double.compare(minDistance, other.minDistance);
        }
    }

    public static class Edge {
        public final Vertex target;
        public final double weight;

        public Edge(Vertex argTarget, double argWeight) {
            target = argTarget;
            weight = argWeight;
        }
    }

    public static void computePaths(Vertex source) {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies) {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }

    public static List<Vertex> getAllVertexes() {
        List<Vertex> allVertexes = new ArrayList<>();
        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        for (int i = 0; i < postOffices.size(); i++) {
            String officeName = postOffices.get(i).name;
            Vertex v = new Vertex(officeName);
            allVertexes.add(v);
        }
        return allVertexes;
    }

    public static Vertex findVertexByName(String name) {
        for (Vertex v : vertexes) {
            if (v.name.equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static void getAllVertexesWithEdges(){
        for (int i = 0; i < vertexes.size(); i++) {
            Vertex v = vertexes.get(i);
            List<Link> startOfficeLinks = Link.findByStartOffice(v.name);
            for (int j = 0; j < startOfficeLinks.size(); j++) {
                String target = startOfficeLinks.get(j).target;
                double distance = startOfficeLinks.get(j).distance;
                Edge e = new Edge(findVertexByName(target), distance);
                v.adjacencies.add(e);
            }
        }
    }

    public static List<Vertex> getPath(Vertex a, Vertex b) {
        computePaths(a);
        return getShortestPathTo(b);
    }

    public static double getDistance(String address1, String address2) {
        InputStream is = null;
        double distance = 0;
        StringBuilder read = new StringBuilder();
        String googleApi = "AIzaSyCziHIWI7MbOma8E65aQo9bqPiIPmeefCg";
        String urlAddress = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + address1 + "&destinations=" + address2 + "&key=" + googleApi;
        try {
            URL url = new URL(urlAddress);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while((line = reader.readLine()) != null) {
                read.append(line + "\n");
            }

            reader.close();

            JSONObject json = new JSONObject(read.toString());
            JSONArray jsonArray = json.getJSONArray("rows");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONArray jsonArray2 = jsonObject.getJSONArray("elements");
            JSONObject jsonObject2 = jsonArray2.getJSONObject(0);
            JSONObject jsonObject3 = jsonObject2.getJSONObject("distance");

            distance = ((double)jsonObject3.getInt("value"))/1000;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return distance;
    }

    public static List<String> getStringPath(String initialAddress, String targetAddress) {

        getAllVertexesWithEdges();

        Vertex a = findVertexByName(initialAddress);
        Vertex b = findVertexByName(targetAddress);

        List<Vertex> path = getPath(a,b);
        List<String> pathStrings = new ArrayList<>();

        for (int i = 0; i < path.size(); i++) {
            String name = path.get(i).name;
            pathStrings.add(name);
        }

        return pathStrings;
    }
}
