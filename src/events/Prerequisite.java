package events;

import Database.Graph.Node;
import provenanceGraph.ProvGraph;

public interface Prerequisite {
    boolean evaluate(Node node, ProvGraph graph);
}
