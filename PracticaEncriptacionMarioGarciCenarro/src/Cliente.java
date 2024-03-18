import java.net.InetAddress;

public class Cliente {
    private InetAddress address;
    private int port;
    private String nombre;
    private String horaConexion;

    public Cliente(InetAddress address, int port, String nombre, String horaConexion) {
        this.address = address;
        this.port = port;
        this.nombre = nombre;
        this.horaConexion = horaConexion;
    }

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getHoraConexion() {
		return horaConexion;
	}

	public void setHoraConexion(String horaConexion) {
		this.horaConexion = horaConexion;
	}

	@Override
	public String toString() {
		return "Cliente [address=" + address + ", port=" + port + ", nombre=" + nombre + ", horaConexion="
				+ horaConexion + "]";
	}

    
}