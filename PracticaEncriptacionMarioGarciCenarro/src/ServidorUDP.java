import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class ServidorUDP {
    private static List<Cliente> listaClientes = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(12345);
            System.out.println("Esperando a clientes...");

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                    mensajeCliente(socket, packet, listaClientes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void mensajeCliente(DatagramSocket socket, DatagramPacket packet, List<Cliente> listaClientes) throws IOException {
        InetAddress direccion = packet.getAddress();
        int puerto = packet.getPort();
        String mensaje = new String(packet.getData(), 0, packet.getLength());

        if (mensaje.equals("salir")) {
            String nombreClienteDesconectado = obtenerNombreCliente(direccion, puerto, listaClientes);
            eliminarCliente(direccion, puerto, listaClientes);
            for (Cliente cliente : listaClientes) {
                enviarMensaje(socket, nombreClienteDesconectado + " se ha desconectado", cliente.getAddress(), cliente.getPort());
            }
            
            LocalDateTime hora = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
            String horaConexion = hora.format(formato);
            System.out.println("Cliente " + nombreClienteDesconectado + " se ha desconectado del chat. - Hora de desconexión: " + horaConexion);
        } else {
        	//Ver si clietne existe
        	boolean estaResgistrado = false;
            for (Cliente cliente : listaClientes) {
                if (cliente.getAddress().equals(direccion) && cliente.getPort() == puerto) {
                    estaResgistrado = true;
                    break;
                }
            }

            if (!estaResgistrado) {
                
                String[] subMensaje = mensaje.split(":");
                String nombreCliente = (subMensaje.length >= 2) ? subMensaje[1] : "Cliente " + (listaClientes.size() + 1);

                // Registrar cliente
                LocalDateTime hora = LocalDateTime.now();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
                String horaConexion = hora.format(formato);
                listaClientes.add(new Cliente(direccion, puerto, nombreCliente, horaConexion));
                System.out.println("Nuevo cliente registrado: " + nombreCliente + " - " + direccion.toString() + ":" + puerto + " - Hora de conexión: " + horaConexion);

                for (Cliente cliente : listaClientes) {
                    
                    enviarMensaje(socket, nombreCliente + " se ha conectado", cliente.getAddress(), cliente.getPort());
                }
            } else {
                
                String mensajeDesencriptado = desencriptar(mensaje);

                String nombreRemitente = obtenerNombreCliente(direccion, puerto, listaClientes);

                LocalDateTime horaActual = LocalDateTime.now();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
                String horaEnvio = horaActual.format(formato);

                String mensajeServidor = nombreRemitente + " (" + horaEnvio + "): " + mensaje;

                System.out.println("Mensaje recicido proveniente de " + mensajeServidor);

                // Enviar el mensaje a todos los clientes conectados
                String mensajeConInfo = obtenerMensajeConInformacion(mensajeDesencriptado, obtenerNombreCliente(direccion, puerto, listaClientes));
                for (Cliente cliente : listaClientes) {
                    enviarMensaje(socket, mensajeConInfo, cliente.getAddress(), cliente.getPort());
                }
            }
        }
    }
    
    private static String obtenerNombreCliente(InetAddress direccion, int puerto, List<Cliente> listaClientes) {
        for (Cliente cliente : listaClientes) {
            if (cliente.getAddress().equals(direccion) && cliente.getPort() == puerto) {
                return cliente.getNombre();
            }
        }
        return "No se podido obtener el nombre del cliente";
    }

    // Método para obtener un mensaje con información adicional (nombre del remitente y hora de envío)
    private static String obtenerMensajeConInformacion(String mensaje, String nombreRemitente) {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String horaEnvio = ahora.format(formato);
        return nombreRemitente + " (" + horaEnvio + "): " + mensaje;
    }

    // Método para enviar un mensaje a un cliente específico
    public static void enviarMensaje(DatagramSocket socket, String mensaje, InetAddress direccion, int puerto) throws IOException {
        byte[] DatosAEnviar = mensaje.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(DatosAEnviar, DatosAEnviar.length, direccion, puerto);
        socket.send(sendPacket);
    }

    private static void eliminarCliente(InetAddress direccion, int puerto, List<Cliente> listaClientes) {
        listaClientes.removeIf(cliente -> cliente.getAddress().equals(direccion) && cliente.getPort() == puerto);
    }
    
    public static String desencriptar(String encryptedMessage) {
        try {
            Key key = new SecretKeySpec("AntonioApruebamePorfa".getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encriptadoBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] desencriptadoBytes = cipher.doFinal(encriptadoBytes);
            return new String(desencriptadoBytes);
        } catch (Exception e) {
            System.out.println("Error al desencriptar: " + e.getMessage());
        }
        return null;
    }
    
}