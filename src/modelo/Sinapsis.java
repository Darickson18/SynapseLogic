package modelo;

public class Sinapsis {
    private String destino;
    private double distancia;
    private String neuroId;
    private double k;

    public Sinapsis(String destino, double distancia, String neuroId, double k) {
        this.destino = destino;
        this.distancia = distancia;
        this.neuroId = neuroId;
        this.k = k;
    }

    public String getDestino() { return destino; }
    public double getDistancia() { return distancia; }
    public String getNeuroId() { return neuroId; }
    public double getK() { return k; }
    public void setK(double k) { this.k = k; }

    public double calcularPeso(Neurotransmisor nt) {
        if (nt == null) return Double.POSITIVE_INFINITY;
        double vel = nt.getVelocidad();
        if (vel <= 0) return Double.POSITIVE_INFINITY;
        return distancia / (vel * k);
    }
}
