package controlador;

import modelo.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileManager {
    private DirectedGraph graph;
    private HashTable<String, Neurotransmisor> neuroTable;

    public FileManager(DirectedGraph graph, HashTable<String, Neurotransmisor> neuroTable) {
        this.graph = graph;
        this.neuroTable = neuroTable;
    }

    public boolean loadGraphFromCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar red sináptica (CSV)");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; }
                    String[] p = line.split(",");
                    if (p.length < 5) continue;
                    String origen = p[0].trim();
                    String destino = p[1].trim();
                    double dist = Double.parseDouble(p[2].trim());
                    String nid = p[3].trim();
                    double k = Double.parseDouble(p[4].trim());
                    graph.addSynapse(origen, destino, dist, nid, k);
                }
                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    public boolean loadNeurotransmitterDict() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar diccionario de neurotransmisores (CSV)");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; }
                    String[] p = line.split(",");
                    if (p.length < 5) continue;
                    String id = p[0].trim();
                    String nombre = p[1].trim();
                    String efecto = p[2].trim();
                    double vel = Double.parseDouble(p[3].trim());
                    String desc = p[4].trim();
                    neuroTable.put(id, new Neurotransmisor(id, nombre, efecto, vel, desc));
                }
                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    public boolean saveGraphToCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar red sináptica como CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".csv")) file = new File(file.getPath() + ".csv");
            try {
                Files.write(file.toPath(), graph.toCSVLines());
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
}
