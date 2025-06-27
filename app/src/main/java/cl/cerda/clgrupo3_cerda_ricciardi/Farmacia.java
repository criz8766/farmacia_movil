package cl.cerda.clgrupo3_cerda_ricciardi;

public class Farmacia {
    private String nombre;
    private String comuna;
    private String horarioCierre;
    private String direccion;
    private String telefono;
    private double latitud;
    private double longitud;

    public Farmacia(String nombre, String comuna, String horarioCierre, String direccion, String telefono, double latitud, double longitud) {
        this.nombre = nombre;
        this.comuna = comuna;
        this.horarioCierre = horarioCierre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters para todos los campos
    public String getNombre() {
        return nombre;
    }

    public String getComuna() {
        return comuna;
    }

    public String getHorarioCierre() {
        return horarioCierre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}