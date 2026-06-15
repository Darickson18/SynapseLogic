package vista;

import modelo.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import javax.swing.*;
import java.util.Set;

public class GraphVisualizer {
    private Graph graphStreamGraph;
    private DirectedGraph modelGraph;
    private Viewer viewer;

    public GraphVisualizer(DirectedGraph modelGraph) {
        this.modelGraph = modelGraph;
        System.setProperty("org.graphstream.ui", "swing");
        graphStreamGraph = new SingleGraph("Red Neuronal");
        graphStreamGraph.addAttribute("ui.stylesheet",
                "node { fill-color: #68a0b4; size: 25px; text-size: 12; text-alignment: under; } " +
                "edge { fill-color: #555; text-size: 10; } " +
                "node.isolated { fill-color: #d9534f; }");
    }

    public void updateGraph(Set<String> isolatedZones) {
        graphStreamGraph.clear();
        for (String neuron : modelGraph.getAllNeurons()) {
            Node n = graphStreamGraph.addNode(neuron);
            n.setAttribute("ui.label", neuron);
            if (isolatedZones != null && isolatedZones.contains(neuron))
                n.addAttribute("ui.class", "isolated");
        }
        for (String origen : modelGraph.getAllNeurons()) {
            for (Sinapsis s : modelGraph.getOutgoingEdges(origen)) {
                String eid = origen + "->" + s.getDestino();
                Edge e = graphStreamGraph.addEdge(eid, origen, s.getDestino(), true);
                e.setAttribute("ui.label", String.format("%.2f", s.getDistancia()));
            }
        }
        graphStreamGraph.addAttribute("ui.layout", "force");
    }

    public void showInFrame() {
        if (viewer == null) {
            viewer = graphStreamGraph.display();
            viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        } else {
            viewer.enableAutoLayout();
        }
    }

    public void close() {
        if (viewer != null) viewer.close();
    }
}
