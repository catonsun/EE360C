/*
 * Name: Anthony Weems
 * EID: amw3647
 */

import java.util.ArrayList;
import java.util.HashMap;

/* Your solution goes in this file.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */

public class Program2 extends VertexNetwork {
    /* DO NOT FORGET to add a graph representation and 
       any other fields and/or methods that you think 
       will be useful. 
       DO NOT FORGET to modify the constructors when you 
       add new fields to the Program2 class. */
    public static final double INF = Double.MAX_VALUE;
    public static final int FAILURE = -1;

    private HashMap<Vertex, HashMap<Vertex, Edge>> edgeup;

    private void setup() {
        edgeup = new HashMap<Vertex, HashMap<Vertex, Edge>>();
        for (Vertex u : location) {
            edgeup.put(u, new HashMap<Vertex, Edge>());
        }

        for (Edge e : edges) {
            Vertex u = location.get(e.getU());
            Vertex v = location.get(e.getV());
            edgeup.get(u).put(v, e);
            edgeup.get(v).put(u, e);
        }
    }

    Program2() {
        super();
        setup();
    }
    
    Program2(String locationFile) {
        super(locationFile);
        setup();
    }
    
    Program2(String locationFile, double transmissionRange) {
        super(locationFile, transmissionRange);
        setup();
    }
    
    Program2(double transmissionRange, String locationFile) {
        super(transmissionRange, locationFile);
        setup();
    }

    private double vDistance(int src, int dst) {
        Vertex vSrc = location.get(src);
        Vertex vDst = location.get(dst);
        return vSrc.distance(vDst);
    }

    private int findClosest(int src, int sink) {
        int found = FAILURE;
        double dist = vDistance(src, sink);

        for (Edge e : edges) {
            int tsrc;
            if (e.getU() == src) {
                tsrc = e.getV();
            } else if (e.getV() == src) {
                tsrc = e.getU();
            } else {
                /* not a matching edge */
                continue;
            }

            double test = vDistance(tsrc, sink);
            if (test < dist) {
                dist = test;
                found = tsrc;
            }
        }
        return found;
    }

    private Edge getEdge(Vertex u, Vertex v) {
        HashMap<Vertex, Edge> e = edgeup.get(u);
        if (e != null) {
            return e.get(v);
        } else {
            return null;
        }
    }

    public ArrayList<Vertex> gpsrPath(int sourceIndex, int sinkIndex) {
        /* This method returns a path from a source at location sourceIndex 
           and a sink at location sinkIndex using the GPSR algorithm. An empty 
           path is returned if the GPSR algorithm fails to find a path. */
        ArrayList<Vertex> path = new ArrayList<Vertex>();

        int cur = sourceIndex;
        path.add(location.get(cur));

        while (cur != sinkIndex) {
            cur = findClosest(cur, sinkIndex);
            if (cur == FAILURE) {
                return new ArrayList<Vertex>(0);
            } else {
                path.add(location.get(cur));
            }
        }
        return path;
    }

    private interface Metric {
        public double dist(Edge e);
    }

    private ArrayList<Vertex> dijkstraGeneral(int src, int dst, Metric metric) {
        HashMap<Vertex, Double> dist = new HashMap<Vertex, Double>();
        HashMap<Vertex, Vertex> prev = new HashMap<Vertex, Vertex>();

        Vertex source = location.get(src);
        Vertex sink = location.get(dst);

        for (Vertex v : location) {
            dist.put(v, INF);
            prev.put(v, null);
        }
        dist.put(source, 0.0);

        ArrayList<Vertex> Q = new ArrayList<Vertex>(location);
        while (!Q.isEmpty()) {
            /* find node in Q with minimum dist */
            Vertex u = source;
            double min = INF;
            for (Vertex v : Q) {
                if (dist.get(v) < min) {
                    u   = v;
                    min = dist.get(v);
                }
            }

            if (dist.get(u) == INF) break;
            Q.remove(u);
            if (u.equals(sink)) break;

            for (Vertex v : Q) {
                Edge e = getEdge(u, v);
                if (e != null) {
                    double alt = dist.get(u) + metric.dist(e);
                    if (alt < dist.get(v)) {
                        dist.put(v, alt);
                        prev.put(v, u);
                        /* decrease key v in Q */
                    }
                }
            }
        }

        ArrayList<Vertex> path = new ArrayList<Vertex>();
        Vertex u = sink;
        while (!u.equals(source)) {
            path.add(u);
            u = prev.get(u);
        }
        return path;
    }
    
    public ArrayList<Vertex> dijkstraPathLatency(int sourceIndex, int sinkIndex) {
        /* This method returns a path (shortest in terms of latency) from a source at
           location sourceIndex and a sink at location sinkIndex using Dijkstra's algorithm.
           An empty path is returned if Dijkstra's algorithm fails to find a path. */
        return dijkstraGeneral(sourceIndex, sinkIndex, new Metric() {
            public double dist(Edge e) {
                return e.getW();
            }
        });
    }
    
    public ArrayList<Vertex> dijkstraPathHops(int sourceIndex, int sinkIndex) {
        /* This method returns a path (shortest in terms of hops) from a source at
           location sourceIndex and a sink at location sinkIndex using Dijkstra's algorithm.
           An empty path is returned if Dijkstra's algorithm fails to find a path. */
        return dijkstraGeneral(sourceIndex, sinkIndex, new Metric() {
            public double dist(Edge e) {
                return vDistance(e.getU(), e.getV());
            }
        });
    }
    
}

