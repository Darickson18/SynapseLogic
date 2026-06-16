package modelo;

public class Neurotransmisor {
    private String id;
    private String nombre;
    private String efecto;        
    private double velocidad;
    private String descripcion;
    
    public static final String EXCITATORIO = "Excitatorio";
    public static final String INHIBITORIO = "Inhibitorio";
    public static final String MODULADOR = "Modulador";
    
    private static final double FACTOR_EXCITATORIO = 1.0;
    private static final double FACTOR_INHIBITORIO = 1.5;  
    private static final double FACTOR_MODULADOR = 1.2;   
    
    public Neurotransmisor(String id, String nombre, String efecto, 
                           double velocidad, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.efecto = efecto;
        this.velocidad = velocidad;
        this.descripcion = descripcion;
    }
    
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEfecto() { return efecto; }
    public double getVelocidad() { return velocidad; }
    public String getDescripcion() { return descripcion; }
    
    public double getFactorEfecto() {
        switch (efecto) {
            case EXCITATORIO:
                return FACTOR_EXCITATORIO;
            case INHIBITORIO:
                return FACTOR_INHIBITORIO;
            case MODULADOR:
                return FACTOR_MODULADOR;
            default:
                return 1.0;
        }
    }
    
    public boolean isExcitatorio() {
        return EXCITATORIO.equals(efecto);
    }
    
    public boolean isInhibitorio() {
        return INHIBITORIO.equals(efecto);
    }
    
    public boolean isModulador() {
        return MODULADOR.equals(efecto);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s, v=%.1f)", nombre, efecto, velocidad);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Neurotransmisor that = (Neurotransmisor) obj;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
