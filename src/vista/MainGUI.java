package vista;

import controlador.FileManager;
import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class MainGUI extends JFrame {
    private DirectedGraph graph;
    private Map<String, Neurotransmisor> neuroTable;
    private FileManager fileManager;
    private GraphVisualizer visualizer;
    private boolean graphLoaded = false;

    private JButton btnLoadGraph, btnLoadNeuro, btnShowGraph, btnDetectIsolated, btnDijkstra;
    private JButton btnFatigue, btnAddNeuron, btnRemoveNeuron, btnSaveGraph;
    private JTextField txtSource, txtTarget, txtNeuronId;
    private JTextArea txtResult;

    public MainGUI() {
        setTitle("SynapseLogic - Análisis de Conectividad Neuronal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initComponents();
        graph = new DirectedGraph();
        neuroTable = new Map<>();
        fileManager = new FileManager(graph, neuroTable, this);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnLoadGraph = new JButton("Cargar red sináptica");
        btnLoadNeuro = new JButton("Cargar diccionario NT");
        btnShowGraph = new JButton("Mostrar grafo");
        btnDetectIsolated = new JButton("Zonas aisladas (BFS)");
        btnDijkstra = new JButton("Ruta más rápida");
        btnFatigue = new JButton("Simular fatiga (x1.2)");
        btnAddNeuron = new JButton("Agregar neurona");
        btnRemoveNeuron = new JButton("Eliminar neurona");
        btnSaveGraph = new JButton("Guardar grafo");
        top.add(btnLoadGraph); top.add(btnLoadNeuro); top.add(btnShowGraph);
        top.add(btnDetectIsolated); top.add(btnDijkstra); top.add(btnFatigue);
        top.add(btnAddNeuron); top.add(btnRemoveNeuron); top.add(btnSaveGraph);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4,2,5,5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Parámetros"));
        inputPanel.add(new JLabel("Neurona origen:"));
        txtSource = new JTextField(); inputPanel.add(txtSource);
        inputPanel.add(new JLabel("Neurona destino:"));
        txtTarget = new JTextField(); inputPanel.add(txtTarget);
        inputPanel.add(new JLabel("ID neurona (agregar/eliminar):"));
        txtNeuronId = new JTextField(); inputPanel.add(txtNeuronId);
        inputPanel.add(new JLabel("")); inputPanel.add(new JLabel(""));
        center.add(inputPanel, BorderLayout.NORTH);

        txtResult = new JTextArea(10,40);
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Monospaced", Font.PLAIN,12));
        JScrollPane sp = new JScrollPane(txtResult);
        sp.setBorder(BorderFactory.createTitledBorder("Resultados"));
        center.add(sp, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        btnLoadGraph.addActionListener(e -> loadGraph());
        btnLoadNeuro.addActionListener(e -> loadNeuro());
        btnShowGraph.addActionListener(e -> showGraph());
        btnDetectIsolated.addActionListener(e -> detectIsolated());
        btnDijkstra.addActionListener(e -> runDijkstra());
        btnFatigue.addActionListener(e -> fatigue());
        btnAddNeuron.addActionListener(e -> addNeuron());
        btnRemoveNeuron.addActionListener(e -> removeNeuron());
        btnSaveGraph.addActionListener(e -> saveGraph());

        txtResult.append("Bienvenido a SynapseLogic.\nCargue red y diccionario.\n");
    }

    private void loadGraph() {
        if (graphLoaded) {
            int opt = JOptionPane.showConfirmDialog(this, "¿Guardar antes de cargar otra red?", "Confirmar", JOptionPane.YES_NO_CANCEL_OPTION);
            if (opt == JOptionPane.YES_OPTION) { if (!fileManager.saveGraphToCSV()) return; }
            else if (opt == JOptionPane.CANCEL_OPTION) return;
            graph = new DirectedGraph();
            neuroTable = new Map<>();
            fileManager = new FileManager(graph, neuroTable);
            graphLoaded = false;
        }
        if (fileManager.loadGraphFromCSV()) {
            graphLoaded = true;
            txtResult.append("Red cargada. Neuronas: " + graph.getAllNeurons().size() + "\n");
        } else txtResult.append("No se cargó red.\n");
    }

    private void loadNeuro() {
        if (fileManager.loadNeurotransmitterDict())
            txtResult.append("Diccionario cargado. (" + neuroTable.size() + " registros)\n");
        else txtResult.append("No se cargó diccionario.\n");
    }

    private void showGraph() {
        if (!graphLoaded || graph.getAllNeurons().isEmpty()) { txtResult.append("No hay grafo cargado.\n"); return; }
        if (visualizer == null) visualizer = new GraphVisualizer(graph);
        visualizer.updateGraph(null);
        visualizer.showInFrame();
        txtResult.append("Grafo mostrado.\n");
    }

    private void detectIsolated() {
        if (!graphLoaded) { txtResult.append("No hay grafo.\n"); return; }
        String src = JOptionPane.showInputDialog(this, "Neurona fuente:", "Origen estímulo", JOptionPane.QUESTION_MESSAGE);
        if (src == null || src.trim().isEmpty()) return;
        src = src.trim();
        if (!graph.hasNeuron(src)) { txtResult.append("Neurona " + src + " no existe.\n"); return; }
        Set<String> unreachable = graph.getUnreachableZones(src);
        if (unreachable.isEmpty()) txtResult.append("Desde " + src + " todas son alcanzables.\n");
        else txtResult.append("Zonas inalcanzables: " + unreachable + "\n");
        txtResult.append("Fuertemente conexo? " + (graph.isStronglyConnected() ? "Sí" : "No") + "\n");
        if (visualizer != null) { visualizer.updateGraph(unreachable); visualizer.showInFrame(); }
    }

    private void runDijkstra() {
        if (!graphLoaded) { txtResult.append("No hay grafo.\n"); return; }
        String src = txtSource.getText().trim();
        String dst = txtTarget.getText().trim();
        if (src.isEmpty() || dst.isEmpty()) { txtResult.append("Ingrese origen y destino.\n"); return; }
        if (!graph.hasNeuron(src)) { txtResult.append("Origen no existe.\n"); return; }
        if (!graph.hasNeuron(dst)) { txtResult.append("Destino no existe.\n"); return; }
        DirectedGraph.DijkstraResult res = graph.dijkstra(src, dst, neuroTable);
        if (res.path == null) txtResult.append("No hay camino.\n");
        else txtResult.append("Ruta: " + String.join(" → ", res.path) + "\nTiempo: " + String.format("%.4f", res.totalTime) + "\n");
    }

    private void fatigue() {
        if (!graphLoaded) { txtResult.append("No hay grafo.\n"); return; }
        graph.applyFatigue();
        txtResult.append("Fatiga aplicada (k x1.2).\n");
    }

    private void addNeuron() {
        if (!graphLoaded) { txtResult.append("No hay grafo.\n"); return; }
        String id = txtNeuronId.getText().trim();
        if (id.isEmpty()) { txtResult.append("Ingrese ID.\n"); return; }
        if (graph.hasNeuron(id)) { txtResult.append("Ya existe.\n"); return; }
        graph.addNeuron(id);
        txtResult.append("Neurona " + id + " agregada.\n");
        if (visualizer != null) visualizer.updateGraph(null);
    }

    private void removeNeuron() {
        if (!graphLoaded) { txtResult.append("No hay grafo.\n"); return; }
        String id = txtNeuronId.getText().trim();
        if (id.isEmpty()) { txtResult.append("Ingrese ID.\n"); return; }
        if (!graph.hasNeuron(id)) { txtResult.append("No existe.\n"); return; }
        graph.removeNeuron(id);
        txtResult.append("Neurona " + id + " eliminada.\n");
        if (visualizer != null) visualizer.updateGraph(null);
    }

    private void saveGraph() {
        if (!graphLoaded) { txtResult.append("No hay grafo.\n"); return; }
        if (fileManager.saveGraphToCSV()) txtResult.append("Grafo guardado.\n");
        else txtResult.append("Error al guardar.\n");
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true)); }
}
