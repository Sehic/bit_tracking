package controllers;

import models.Link;
import models.PostOffice;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by USER on 21.10.2015.
 */
public class DijkstraController extends Controller {

    public List<Vertex> vertexes = getAllVertexes();

    class Vertex implements Comparable<Vertex> {
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

    class Edge {
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

    public List<Vertex> getAllVertexes() {
        List<Vertex> allVertexes = new ArrayList<>();
        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        for (int i = 0; i < postOffices.size(); i++) {
            String officeName = postOffices.get(i).name;
            Vertex v = new Vertex(officeName);
            allVertexes.add(v);
        }
        return allVertexes;
    }

    public Vertex findVertexByName(String name) {
        for (Vertex v : vertexes) {
            if (v.name.equals(name)) {
                return v;
            }
        }
        return null;
    }

    public void getAllVertexesWithEdges(){
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

    public List<Vertex> getPath(Vertex a, Vertex b) {
        computePaths(a);
        return getShortestPathTo(b);
    }

    public Result getDjikstra() {

        getAllVertexesWithEdges();

        Vertex a = findVertexByName("Poslovnica Sarajevo");
        Vertex b = findVertexByName("Poslovnica Minhen");

        List<Vertex> path = getPath(a,b);
        Logger.info(path + "");

        return ok(path.toString());
    }
}
