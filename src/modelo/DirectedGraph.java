package modelo;

import java.util.*;

public class DirectedGraph {
    
    private Map<String, List<Sinapsis>> adjacencies;
    private Set<String> neurons;
    

    public DirectedGraph() {
        this.adjacencies = new Map<>();
        this.neurons = new HashSet<>();
    }
    
    public void addNeuron(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de neurona inválido");
        }
        
        if (!neurons.contains(id)) {
            neurons.add(id);
            if (adjacencies.get(id) == null) {
                adjacencies.put(id, new ArrayList<>());
            }
        }
    }
    
    public void addSynapse(String origen, String destino, double distancia, 
                           String neuroId, double k) {
        
        if (distancia <= 0) {
            throw new IllegalArgumentException("La distancia debe ser positiva");
        }
        if (k <= 0 || k > 1) {
            throw new IllegalArgumentException("k debe estar entre 0 y 1");
        }
        
        addNeuron(origen);
        addNeuron(destino);
        
        List<Sinapsis> edges = adjacencies.get(origen);
        if (edges == null) {
            edges = new ArrayList<>();
            adjacencies.put(origen, edges);
        }
        
        edges.add(new Sinapsis(destino, distancia, neuroId, k));
    }
    
    public List<Sinapsis> getOutgoingEdges(String neuronId) {
        List<Sinapsis> edges = adjacencies.get(neuronId);
        return edges != null ? new ArrayList<>(edges) : new ArrayList<>();
    }
    
    public Set<String> bfs(String source) {
        if (!neurons.contains(source)) {
            return new HashSet<>();
        }
        
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        visited.add(source);
        queue.add(source);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (Sinapsis s : getOutgoingEdges(current)) {
                if (!visited.contains(s.getDestino())) {
                    visited.add(s.getDestino());
                    queue.add(s.getDestino());
                }
            }
        }
        return visited;
    }
    
    public Set<String> dfs(String source) {
        if (!neurons.contains(source)) {
            return new HashSet<>();
        }
        
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();
        
        visited.add(source);
        stack.push(source);
        
        while (!stack.isEmpty()) {
            String current = stack.pop();
            for (Sinapsis s : getOutgoingEdges(current)) {
                if (!visited.contains(s.getDestino())) {
                    visited.add(s.getDestino());
                    stack.push(s.getDestino());
                }
            }
        }
        return visited;
    }
    
    public Set<String> getUnreachableZones(String source, boolean usarBFS) {
        Set<String> alcanzables = usarBFS ? bfs(source) : dfs(source);
        Set<String> unreachable = new HashSet<>(neurons);
        unreachable.removeAll(alcanzables);
        return unreachable;
    }
    
    public boolean isStronglyConnected() {
        if (neurons.isEmpty()) return true;
        
        String first = neurons.iterator().next();
        
        Set<String> desdeFirst = bfs(first);
        if (desdeFirst.size() != neurons.size()) return false;
        
        Set<String> enGrafoInverso = bfsEnGrafoInverso(first);
        return enGrafoInverso.size() == neurons.size();
    }
    
    private Set<String> bfsEnGrafoInverso(String source) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        visited.add(source);
        queue.add(source);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String potentialSource : neurons) {
                for (Sinapsis s : getOutgoingEdges(potentialSource)) {
                    if (s.getDestino().equals(current) && !visited.contains(potentialSource)) {
                        visited.add(potentialSource);
                        queue.add(potentialSource);
                    }
                }
            }
        }
        return visited;
    }
    
    public DijkstraResult dijkstra(String source, String target, Map<String, Neurotransmisor> neuroTable) {
        if (!neurons.contains(source) || !neurons.contains(target)) {
            return new DijkstraResult(null, Double.POSITIVE_INFINITY);
        }
        
        Map<String, Double> dist = new Map<>();
        Map<String, String> prev = new Map<>();
        PriorityQueue<NodeDist> pq = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.dist));
        
        for (String n : neurons) {
            dist.put(n, Double.POSITIVE_INFINITY);
        }
        dist.put(source, 0.0);
        pq.add(new NodeDist(source, 0.0));
        
        while (!pq.isEmpty()) {
            NodeDist current = pq.poll();
            String u = current.node;
            
            if (u.equals(target)) break;
            if (current.dist > dist.get(u)) continue;
            
            for (Sinapsis s : getOutgoingEdges(u)) {
                Neurotransmisor nt = neuroTable.get(s.getNeuroId());
                double weight = s.calcularPeso(nt);
                
                if (Double.isInfinite(weight)) continue;
                
                double newDist = dist.get(u) + weight;
                if (newDist < dist.get(s.getDestino())) {
                    dist.put(s.getDestino(), newDist);
                    prev.put(s.getDestino(), u);
                    pq.add(new NodeDist(s.getDestino(), newDist));
                }
            }
        }
        
        if (Double.isInfinite(dist.get(target))) {
            return new DijkstraResult(null, Double.POSITIVE_INFINITY);
        }
        
        List<String> path = new ArrayList<>();
        String current = target;
        while (current != null) {
            path.add(0, current);
            current = prev.get(current);
        }
        
        return new DijkstraResult(path, dist.get(target));
    }
    
    public void applyFatigue() {
        for (String neuron : neurons) {
            List<Sinapsis> edges = adjacencies.get(neuron);
            if (edges != null) {
                for (Sinapsis s : edges) {
                    s.aplicarFatiga();
                }
            }
        }
    }
    
    public boolean removeNeuron(String id) {
        if (!neurons.contains(id)) return false;
        
        for (String n : neurons) {
            List<Sinapsis> edges = adjacencies.get(n);
            if (edges != null) {
                edges.removeIf(s -> s.getDestino().equals(id));
            }
        }
        
        adjacencies.remove(id);
        neurons.remove(id);
        
        return true;
    }
    
    public boolean removeSynapse(String origen, String destino) {
        List<Sinapsis> edges = adjacencies.get(origen);
        if (edges == null) return false;
        
        return edges.removeIf(s -> s.getDestino().equals(destino));
    }
    

    public Set<String> getNeurons() {
        return new HashSet<>(neurons);
    }
    
    public boolean hasNeuron(String id) {
        return neurons.contains(id);
    }
    
    public int size() {
        return neurons.size();
    }
    
    public boolean isEmpty() {
        return neurons.isEmpty();
    }
    
    public List<String> toCSVLines() {
        List<String> lines = new ArrayList<>();
        lines.add("origen, destino, distancia, ID_Neurotransmisor, coheficiente_eficiencia_sináptica");
        
        for (String origen : neurons) {
            for (Sinapsis s : getOutgoingEdges(origen)) {
                lines.add(String.format("%s, %s, %.4f, %s, %.4f",
                    origen, s.getDestino(), s.getDistancia(), 
                    s.getNeuroId(), s.getK()));
            }
        }
        return lines;
    }
    
    private static class NodeDist {
        String node;
        double dist;
        
        NodeDist(String node, double dist) {
            this.node = node;
            this.dist = dist;
        }
    }
}
