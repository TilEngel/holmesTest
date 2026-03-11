package Events;

import Database.Graph.Node;
import ProvenanceGraph.ProvGraph;

public interface Prerequisite {
    boolean evaluate(Node node, ProvGraph graph);
}
