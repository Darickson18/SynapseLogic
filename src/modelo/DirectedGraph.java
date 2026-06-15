package modelo;

import java.util.*;

public class DirectedGraph {
    private Map<String, List<Sinapsis>> adjacencies;
    private Set<String> neurons;

    public DirectedGraph() {
        adjacencies = new HashMap<>();
        neurons = new HashSet<>();
    }

    public void addNeuron(String id) {
        if (!neurons.contains(id)) {
            neurons.add(id);
            adjacencies.putIfAbsent(id, new ArrayList<>());
        }
    }

    public void addSynapse(String origen, String destino, double distancia, String neuroId, double k) {
        addNeuron(origen);
        addNeuron(destino);
        adjacencies.get(origen).add(new Sinapsis(destino, distancia, neuroId, k));
    }

    public void removeNeuron(String id) {
        neurons.remove(id);
        adjacencies.remove(id);
        for (List<Sinapsis> list : adjacencies.values())
            list.removeIf(s -> s.getDestino().equals(id));
    }

    public boolean removeSynapse(String origen, String destino) {
        List<Sinapsis> edges = adjacencies.get(origen);
        return edges != null && edges.removeIf(s -> s.getDestino().equals(destino));
    }

    public List<Sinapsis> getOutgoingEdges(String neuron) {
        return adjacencies.getOrDefault(neuron, new ArrayList<>());
    }

    public Set<String> getAllNeurons() { return new HashSet<>(neurons); }
    public boolean hasNeuron(String id) { return neurons.contains(id); }

    public Set<String> bfs(String source) {
        if (!neurons.contains(source)) return Collections.emptySet();
        Set<String> visited = new HashSet<>();
        Queue<String> q = new LinkedList<>();
        visited.add(source);
        q.add(source);
        while (!q.isEmpty()) {
            String curr = q.poll();
            for (Sinapsis s : getOutgoingEdges(curr)) {
                String neigh = s.getDestino();
                if (!visited.contains(neigh)) {
                    visited.add(neigh);
                    q.add(neigh);
                }
            }
        }
        return visited;
    }

    public Set<String> dfs(String source) {
        if (!neurons.contains(source)) return Collections.emptySet();
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();
        visited.add(source);
        stack.push(source);
        while (!stack.isEmpty()) {
            String curr = stack.pop();
            for (Sinapsis s : getOutgoingEdges(curr)) {
                String neigh = s.getDestino();
                if (!visited.contains(neigh)) {
                    visited.add(neigh);
                    stack.push(neigh);
                }
            }
        }
        return visited;
    }

    public Set<String> getUnreachableZones(String source) {
        Set<String> reachable = bfs(source);
        Set<String> all = getAllNeurons();
        Set<String> unreachable = new HashSet<>(all);
        unreachable.removeAll(reachable);
        return unreachable;
    }

    public boolean isStronglyConnected() {
        if (neurons.isEmpty()) return true;
        String start = neurons.iterator().next();
        if (bfs(start).size() != neurons.size()) return false;
        DirectedGraph rev = getReverseGraph();
        return rev.bfs(start).size() == neurons.size();
    }

    private DirectedGraph getReverseGraph() {
        DirectedGraph rev = new DirectedGraph();
        for (String u : neurons)
            for (Sinapsis s : getOutgoingEdges(u))
                rev.addSynapse(s.getDestino(), u, s.getDistancia(), s.getNeuroId(), s.getK());
        return rev;
    }

    public DijkstraResult dijkstra(String source, String target, HashTable<String, Neurotransmisor> neuroTable) {
        if (!neurons.contains(source) || !neurons.contains(target))
            return new DijkstraResult(null, Double.POSITIVE_INFINITY);
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<Pair> pq = new PriorityQueue<>(Comparator.comparingDouble(p -> p.dist));
        for (String n : neurons) dist.put(n, Double.POSITIVE_INFINITY);
        dist.put(source, 0.0);
        pq.add(new Pair(source, 0.0));
        while (!pq.isEmpty()) {
            Pair cur = pq.poll();
            String u = cur.node;
            if (u.equals(target)) break;
            if (cur.dist > dist.get(u)) continue;
            for (Sinapsis s : getOutgoingEdges(u)) {
                Neurotransmisor nt = neuroTable.get(s.getNeuroId());
                double weight = s.calcularPeso(nt);
                if (weight == Double.POSITIVE_INFINITY) continue;
                double nd = dist.get(u) + weight;
                if (nd < dist.get(s.getDestino())) {
                    dist.put(s.getDestino(), nd);
                    prev.put(s.getDestino(), u);
                    pq.add(new Pair(s.getDestino(), nd));
                }
            }
        }
        if (dist.get(target) == Double.POSITIVE_INFINITY)
            return new DijkstraResult(null, Double.POSITIVE_INFINITY);
        LinkedList<String> path = new LinkedList<>();
        for (String at = target; at != null; at = prev.get(at))
            path.addFirst(at);
        return new DijkstraResult(path, dist.get(target));
    }

    private static class Pair {
        String node; double dist;
        Pair(String n, double d) { node = n; dist = d; }
    }

    public static class DijkstraResult {
        public final List<String> path;
        public final double totalTime;
        public DijkstraResult(List<String> path, double totalTime) {
            this.path = path;
            this.totalTime = totalTime;
        }
    }

    public void applyFatigue() {
        for (List<Sinapsis> edges : adjacencies.values())
            for (Sinapsis s : edges)
                s.setK(s.getK() * 1.2);
    }

    public List<String> toCSVLines() {
        List<String> lines = new ArrayList<>();
        lines.add("origen,destino,distancia,ID_Neurotransmisor,coheficiente_eficiencia_sináptica");
        for (Map.Entry<String, List<Sinapsis>> e : adjacencies.entrySet()) {
            String origen = e.getKey();
            for (Sinapsis s : e.getValue())
                lines.add(String.format("%s,%s,%.4f,%s,%.4f", origen, s.getDestino(), s.getDistancia(), s.getNeuroId(), s.getK()));
        }
        return lines;
    }
}
