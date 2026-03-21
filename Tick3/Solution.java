import java.util.ArrayList;
import java.util.HashSet;

public class Solution implements Algs202526Tick3 {

    // Naive edge finding method (gets all edges)
    private ArrayList<Edge> getEdges(ArrayList<Point> points){
        ArrayList<Edge> edges = new ArrayList<>();

        // Simply connect every point to every other point
        for (int i = 0; i < points.size(); i++){
            for (int j = i + 1; j < points.size(); j++){
                edges.add(new Edge(points.get(i), points.get(j)));
            }
        }

        return edges;
    }

    private float distSq(Point a, Point b){
        float dx = a.x() - b.x();
        float dy = a.y() - b.y();
        return dx*dx + dy*dy;
    }

    @Override
    public ArrayList<Edge> mst(ArrayList<Point> points) {
        // The final tree we build up
        ArrayList<Edge> minimumSpanningTree = new ArrayList<>();
        if (points.isEmpty()) return minimumSpanningTree;

        // Every possible edge sorted by length
        ArrayList<Edge> edges = getEdges(points);
        edges.sort((o1, o2) -> {
            float d1 = distSq(o1.l(), o1.r());
            float d2 = distSq(o2.l(), o2.r());
            if (d1 < d2) {
                return -1;
            } else if (d1 > d2) {
                return 1;
            }
            return 0;
        });

        // The points we have already connected
        HashSet<Point> connectedPoints = new HashSet<>();
        connectedPoints.add(points.getFirst());

        // While there are still points to visit and add to the mst
        while (connectedPoints.size() != points.size()){
            for (int i = 0; i < edges.size(); i++){
                Edge edge = edges.get(i);

                // `^` is XOR, i.e. exactly one point is in the MST
                if (connectedPoints.contains(edge.l()) ^ connectedPoints.contains(edge.r())){
                    connectedPoints.add(edge.r());
                    connectedPoints.add(edge.l());
                    minimumSpanningTree.add(edge);
                    // Removing while in loop is fine here since we break immediately after
                    edges.remove(i);
                    break;
                }
            }
        }

        return minimumSpanningTree;
    }

}
