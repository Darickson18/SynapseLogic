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
    
    private JTextField txtSource, txtTarget, txtNeuronId;
    private JTextArea txtResult;
    private JRadioButton rdBFS, rdDFS;
    private ButtonGroup bgAlgorithm;
    private boolean graphLoaded = false;
    
    public MainGUI() {
        initModel();
        initComponents();
    }
    
    private void initModel() {
        graph = new DirectedGraph();
        neuroTable = new Map<>();
        fileManager = new FileManager(graph, neuroTable, this);
        visualizer = null;
    }
    
    private void initComponents() {
        setTitle("SynapseLogic - Analisis de Conectividad Neuronal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton btnLoadGraph = new JButton("Cargar Red");
        JButton btnLoadDict = new JButton("Cargar Diccionario");
        JButton btnShowGraph = new JButton("Visualizar Grafo");
        JButton btnDetect = new JButton("Detectar Zonas Aisladas");
        JButton btnFatigue = new JButton("Simular Fatiga");
        JButton btnSave = new JButton("Guardar Red");
        
        topPanel.add(btnLoadGraph);
        topPanel.add(btnLoadDict);
        topPanel.add(btnShowGraph);
        topPanel.add(btnDetect);
        topPanel.add(btnFatigue);
        topPanel.add(btnSave);
        
        JPanel algoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel dijkstraPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        dijkstraPanel.add(new JLabel("Origen:"));
        txtSource = new JTextField(10);
        dijkstraPanel.add(txtSource);
        dijkstraPanel.add(new JLabel("Destino:"));
        txtTarget = new JTextField(10);
        dijkstraPanel.add(txtTarget);
        JButton btnDijkstra = new JButton(" Calcular Ruta Optima");
        dijkstraPanel.add(btnDijkstra);
        
        JPanel bfsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        rdBFS = new JRadioButton("BFS", true);
        rdDFS = new JRadioButton("DFS");
        bgAlgorithm = new ButtonGroup();
        bgAlgorithm.add(rdBFS);
        bgAlgorithm.add(rdDFS);
        bfsPanel.add(new JLabel("Algoritmo para deteccion:"));
        bfsPanel.add(rdBFS);
        bfsPanel.add(rdDFS);
        
        algoPanel.add(dijkstraPanel);
        algoPanel.add(bfsPanel);
        
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        editPanel.add(new JLabel("ID Neurona:"));
        txtNeuronId = new JTextField(10);
        editPanel.add(txtNeuronId);
        JButton btnAddNeuron = new JButton("Agregar Neurona");
        JButton btnRemoveNeuron = new JButton("Eliminar Neurona");
        JButton btnRemoveSynapse = new JButton("Eliminar Sinapsis");
        editPanel.add(btnAddNeuron);
        editPanel.add(btnRemoveNeuron);
        editPanel.add(btnRemoveSynapse);
        
        txtResult = new JTextArea(15, 80);
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtResult);
        
        JPanel northPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        northPanel.add(topPanel);
        northPanel.add(algoPanel);
        northPanel.add(editPanel);
        
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        btnLoadGraph.addActionListener(e -> loadGraph());
        btnLoadDict.addActionListener(e -> loadDictionary());
        btnShowGraph.addActionListener(e -> showGraph());
        btnDetect.addActionListener(e -> detectIsolated());
        btnFatigue.addActionListener(e -> simulateFatigue());
        btnSave.addActionListener(e -> saveGraph());
        btnDijkstra.addActionListener(e -> runDijkstra());
        btnAddNeuron.addActionListener(e -> addNeuron());
        btnRemoveNeuron.addActionListener(e -> removeNeuron());
        btnRemoveSynapse.addActionListener(e -> removeSynapse());
    }
    
    private void loadGraph() {
        if (graphLoaded) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Ya hay una red cargada. Desea guardar antes de cargar una nueva?",
                "Confirmar", JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                fileManager.saveGraphToCSV();
            } else if (confirm == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        fileManager.loadGraphFromCSV();
        graphLoaded = true;
        txtResult.append("Red cargada correctamente. Neuronas: " + graph.getNeurons().size() + "\n");
        
        if (visualizer != null) {
            visualizer.close();
            visualizer = null;
        }
    }
    
    private void loadDictionary() {
        fileManager.loadNeurotransmitterDict();
        txtResult.append("Diccionario cargado. Neurotransmisores: " + neuroTable.size() + "\n");
    }
    
    private void showGraph() {
        if (!graphLoaded) {
            txtResult.append("Primero cargue una red.\n");
            return;
        }
        
        if (visualizer == null) {
            visualizer = new GraphVisualizer(graph);
        }
        visualizer.updateGraph(null);
        visualizer.showInFrame();
    }
    
    private void detectIsolated() {
        if (!graphLoaded) {
            txtResult.append("Primero cargue una red.\n");
            return;
        }
        
        String source = JOptionPane.showInputDialog(this, 
            "Ingrese ID de neurona fuente:", "Origen");
        
        if (source == null || source.trim().isEmpty()) return;
        
        if (!graph.hasNeuron(source)) {
            txtResult.append("Neurona '" + source + "' no existe.\n");
            return;
        }
        
        boolean usarBFS = rdBFS.isSelected();
        Set<String> unreachable = graph.getUnreachableZones(source, usarBFS);
        
        if (unreachable.isEmpty()) {
            txtResult.append("Desde '" + source + "' se alcanzan TODAS las neuronas.\n");
        } else {
            txtResult.append("Zonas inalcanzables desde '" + source + "': " + unreachable + "\n");
            txtResult.append("   Tamanio: " + unreachable.size() + " neuronas aisladas\n");
            
            if (graph.isStronglyConnected()) {
                txtResult.append("El grafo es fuertemente conexo.\n");
            } else {
                txtResult.append("El grafo NO es fuertemente conexo.\n");
            }
        }
        
        if (visualizer != null) {
            visualizer.updateGraph(unreachable);
        }
    }
    
    private void simulateFatigue() {
        if (!graphLoaded) {
            txtResult.append("Primero cargue una red.\n");
            return;
        }
        
        graph.applyFatigue();
        txtResult.append("FATIGA APLICADA: Todos los coeficientes k fueron multiplicados por 1.2\n");
        txtResult.append("Las sinapsis con k > 1.8 se consideran inaccesibles.\n");
        
        if (visualizer != null) {
            visualizer.close();
            visualizer = null;
        }
    }
    
    private void saveGraph() {
        if (!graphLoaded) {
            txtResult.append("No hay datos para guardar.\n");
            return;
        }
        fileManager.saveGraphToCSV();
    }
    
    private void runDijkstra() {
        if (!graphLoaded) {
            txtResult.append("Primero cargue una red.\n");
            return;
        }
        
        if (neuroTable.isEmpty()) {
            txtResult.append("No hay neurotransmisores cargados. Cargue un diccionario primero.\n");
            return;
        }
        
        String source = txtSource.getText().trim();
        String target = txtTarget.getText().trim();
        
        if (source.isEmpty() || target.isEmpty()) {
            txtResult.append("Complete origen y destino.\n");
            return;
        }
        
        if (!graph.hasNeuron(source)) {
            txtResult.append("Neurona origen '" + source + "' no existe.\n");
            return;
        }
        
        if (!graph.hasNeuron(target)) {
            txtResult.append("Neurona destino '" + target + "' no existe.\n");
            return;
        }
        
        DijkstraResult result = graph.dijkstra(source, target, neuroTable);
        
        if (result.hasPath()) {
            txtResult.append("Ruta optima: " + result.getPathString() + "\n");
            txtResult.append("Tiempo total: " + String.format("%.4f", result.getTotalTime()) + "\n");
        } else {
            txtResult.append("No existe ruta de '" + source + "' a '" + target + "'\n");
            txtResult.append("(puede deberse a desconexion o fatiga)\n");
        }
    }
    
    private void addNeuron() {
        String id = txtNeuronId.getText().trim();
        if (id.isEmpty()) {
            txtResult.append("Ingrese un ID para la nueva neurona.\n");
            return;
        }
        
        if (graph.hasNeuron(id)) {
            txtResult.append("La neurona '" + id + "' ya existe.\n");
            return;
        }
        
        graph.addNeuron(id);
        txtResult.append("Neurona '" + id + "' agregada.\n");
        
        if (visualizer != null) {
            visualizer.updateGraph(null);
        }
    }
    
    private void removeNeuron() {
        String id = txtNeuronId.getText().trim();
        if (id.isEmpty()) {
            txtResult.append("Ingrese el ID de la neurona a eliminar.\n");
            return;
        }
        
        if (!graph.hasNeuron(id)) {
            txtResult.append("La neurona '" + id + "' no existe.\n");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Eliminar neurona '" + id + "' y todas sus conexiones?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            graph.removeNeuron(id);
            txtResult.append("Neurona '" + id + "' eliminada.\n");
            
            if (visualizer != null) {
                visualizer.updateGraph(null);
            }
        }
    }
    
    private void removeSynapse() {
        if (!graphLoaded) {
            txtResult.append("Primero cargue una red.\n");
            return;
        }
        
        String origen = JOptionPane.showInputDialog(this, "Ingrese ID de neurona origen:", "Eliminar Sinapsis");
        if (origen == null || origen.trim().isEmpty()) return;
        
        if (!graph.hasNeuron(origen)) {
            txtResult.append("Neurona origen '" + origen + "' no existe.\n");
            return;
        }
        
        String destino = JOptionPane.showInputDialog(this, "Ingrese ID de neurona destino:", "Eliminar Sinapsis");
        if (destino == null || destino.trim().isEmpty()) return;
        
        if (!graph.hasNeuron(destino)) {
            txtResult.append("Neurona destino '" + destino + "' no existe.\n");
            return;
        }
        
        boolean eliminado = graph.removeSynapse(origen, destino);
        if (eliminado) {
            txtResult.append("Sinapsis de '" + origen + "' a '" + destino + "' eliminada.\n");
            if (visualizer != null) {
                visualizer.updateGraph(null);
            }
        } else {
            txtResult.append("No existe sinapsis de '" + origen + "' a '" + destino + "'.\n");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}
