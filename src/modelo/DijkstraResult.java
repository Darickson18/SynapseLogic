package modelo;

import java.util.List;

public class DijkstraResult {
    private List<String> path;
    private double totalTime;

    public DijkstraResult(List<String> path, double totalTime) {
        this.path = path;
        this.totalTime = totalTime;
    }
    

    public List<String> getPath() {
        return path;
    }
    
  
    public double getTotalTime() {
        return totalTime;
    }
  
    public boolean hasPath() {
        return path != null && !path.isEmpty() && !Double.isInfinite(totalTime);
    }
    
    public String getPathString() {
        if (!hasPath()) {
            return "No hay ruta disponible";
        }
        return String.join(" → ", path);
    }
    
    @Override
    public String toString() {
        if (!hasPath()) {
            return "❌ No existe ruta entre las neuronas seleccionadas.";
        }
        return String.format("🔍 Ruta óptima: %s\n⏱ Tiempo total: %.4f unidades",
            getPathString(), totalTime);
    }
}
