package events;

import Database.Graph.Edge;
import Database.Graph.Node;
import provenanceGraph.ProvGraph;

public interface Prerequisite {
    boolean evaluate(Edge edge, ProvGraph graph);
}
