package controllers;

import models.Link;
import models.Office;
import models.PostOffice;
import play.Logger;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by USER on 20.10.2015.
 */
public class DijkstraController extends Controller {

    public static void computePaths(Office source) {
        source.minDistance = 0.;
        PriorityQueue<Office> vertexQueue = new PriorityQueue<Office>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Office u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Link e : u.links) {
                Office v = Office.findByName(e.target);
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

    public static List<Office> getShortestPathTo(Office target) {
        List<Office> path = new ArrayList<Office>();
        for (Office vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }

    public void createAllOffices() {
        List<PostOffice> allPostOffices = PostOffice.findOffice.findList();
        Office newOffice = null;
        for (int i = 0; i < allPostOffices.size(); i++) {
            newOffice = new Office(allPostOffices.get(i).name);
            newOffice.save();
        }
        Logger.info("------------Offices Created------------");
    }

    public void createListForAllOffices() {
        List<PostOffice> allPostOffices = PostOffice.findOffice.findList();

        for (int i = 0; i < allPostOffices.size(); i++) {
            PostOffice p = allPostOffices.get(i);
            Office o = Office.findByName(p.name);
            Link newLink = null;
            for (int j = 0; j < p.postOfficesA.size(); j++) {
                newLink = new Link(o, p.postOfficesA.get(j).name, Math.random()*100);
                newLink.save();
                o.links.add(newLink);
                o.update();
            }
        }
        Logger.info("------------Link For All Offices Created------------");
    }

    public Result getDjikstra() {

        Office v1 = new Office("Poslovnica Sarajevo");
        Office v2 = new Office("Poslovnica Kladanj");
        Office v3 = new Office("Poslovnica Tuzla");
        Office v4 = new Office("Poslovnica Brcko");
        Office v5 = new Office("Poslovnica Ljubljana");
        Office v6 = new Office("Poslovnica Trento");
        Office v7 = new Office("Poslovnica Salzburg");

        v1.links.add(new Link(v1, v2.name, 30));
        v1.links.add(new Link(v1, v5.name, 40));
        v2.links.add(new Link(v2, v1.name, 79.75));
        v2.links.add(new Link(v2, v3.name, 39.42));
        v3.links.add(new Link(v3, v2.name, 38.65));
        v3.links.add(new Link(v3, v4.name, 38.65));
        v4.links.add(new Link(v4, v3.name, 102.53));
        v4.links.add(new Link(v4, v5.name, 61.44));
        v4.links.add(new Link(v4, v6.name, 96.79));
        v5.links.add(new Link(v5, v1.name, 133.04));
        v5.links.add(new Link(v5, v4.name, 133.04));
        v5.links.add(new Link(v5, v7.name, 133.04));
        v6.links.add(new Link(v6, v4.name, 81.77));
        v6.links.add(new Link(v6, v7.name, 62.05));
        v7.links.add(new Link(v7, v5.name, 97.24));
        v7.links.add(new Link(v7, v6.name, 87.94));

        /*v1.adjacencies = new Edge[] { new Edge(v2, 30), new Edge(v5, 40) };
        v2.adjacencies = new Edge[] { new Edge(v1, 79.75), new Edge(v3, 39.42)};
        v3.adjacencies = new Edge[] { new Edge(v2, 38.65), new Edge(v4, 38.65) };
        v4.adjacencies = new Edge[] { new Edge(v3, 102.53), new Edge(v5, 61.44), new Edge(v6, 96.79) };
        v5.adjacencies = new Edge[] { new Edge(v1, 133.04), new Edge(v4, 133.04), new Edge(v7, 133.04) };
        v6.adjacencies = new Edge[] { new Edge(v4, 81.77), new Edge(v7, 62.05) };
        v7.adjacencies = new Edge[] { new Edge(v5, 97.24), new Edge(v6, 87.94) };
        Vertex[] vertices = { v1, v2, v3, v4, v5, v6, v7 };*/


        //createAllOffices();
        //createListForAllOffices();

        //Office start = Office.findByName("Poslovnica Kladanj");
        //Office finish = Office.findByName("Poslovnica Ljubljana");

        Office start = v1;
        Office finish = v5;

        computePaths(start);
        List<Office> path = getShortestPathTo(finish);

        Logger.info("Start: " + start.name);
        Logger.info("Finish: " + finish.name);
        Logger.info(path + "");

        Logger.info("-------------BREAK------------");

        return ok();

    }



}
