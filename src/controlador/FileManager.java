package controlador;

import modelo.*;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;

public class FileManager {
    
    private DirectedGraph graph;
    private Map<String, Neurotransmisor> neuroTable;
    private JFrame parent;
    
  
    public FileManager(DirectedGraph graph, Map<String, Neurotransmisor> neuroTable, JFrame parent) {
        this.graph = graph;
        this.neuroTable = neuroTable;
        this.parent = parent;
    }
    
  
    public void loadGraphFromCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Cargar red sináptica");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        
        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
                String line = br.readLine(); 
                int lineCount = 0;
                
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length < 5) {
                        System.err.println("Línea ignorada (formato incorrecto): " + line);
                        continue;
                    }
                    
                    try {
                        String origen = parts[0].trim();
                        String destino = parts[1].trim();
                        double distancia = Double.parseDouble(parts[2].trim());
                        String neuroId = parts[3].trim();
                        double k = Double.parseDouble(parts[4].trim());
                        
                        graph.addSynapse(origen, destino, distancia, neuroId, k);
                        lineCount++;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(parent, 
                            "Error de formato numérico en línea: " + line,
                            "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
                JOptionPane.showMessageDialog(parent, 
                    "Red cargada exitosamente.\n" + lineCount + " sinapsis cargadas.\n" +
                    "Neuronas: " + graph.getNeurons().size(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, 
                    "Error al cargar archivo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
  
    public void loadNeurotransmitterDict() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Cargar diccionario de neurotransmisores");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        
        if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
                String line = br.readLine(); 
                int lineCount = 0;
                
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    
                    
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    
                    if (parts.length >= 5) {
                        try {
                            String id = parts[0].trim();
                            String nombre = parts[1].trim();
                            String efecto = parts[2].trim();
                            double velocidad = Double.parseDouble(parts[3].trim());
                            String descripcion = parts[4].trim().replaceAll("^\"|\"$", "");
                            
                            Neurotransmisor nt = new Neurotransmisor(id, nombre, efecto, velocidad, descripcion);
                            neuroTable.put(id, nt);
                            lineCount++;
                        } catch (NumberFormatException e) {
                            System.err.println("Error en velocidad en línea: " + line);
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(parent, 
                    "Diccionario cargado exitosamente.\n" + lineCount + " neurotransmisores cargados.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, 
                    "Error al cargar diccionario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
  
    public void saveGraphToCSV() {
        if (graph.isEmpty()) {
            JOptionPane.showMessageDialog(parent, 
                "No hay datos para guardar. Cargue o cree una red primero.",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Guardar red sináptica");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        
        if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String path = file.getPath();
            if (!path.toLowerCase().endsWith(".csv")) {
                file = new File(path + ".csv");
            }
            
            try (PrintWriter pw = new PrintWriter(file)) {
                for (String line : graph.toCSVLines()) {
                    pw.println(line);
                }
                
                JOptionPane.showMessageDialog(parent, 
                    "Archivo guardado exitosamente en:\n" + file.getAbsolutePath(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(parent, 
                    "Error al guardar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
  
