import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;

public class ClienteUDP {

	public static void main(String[] args) {
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress direccionServer = InetAddress.getByName("localhost");
			Scanner scanner = new Scanner(System.in);
			System.out.print("Ingrese su nombre: ");
			String nombreCliente = scanner.nextLine();
			enviarMensaje(socket, "REGISTRO:" + nombreCliente, direccionServer, 12345);
			Thread recibirMensajes = new Thread(() -> {
				try {
					while (true) {
						byte[] Buffer = new byte[1024];
						DatagramPacket packetRecibido = new DatagramPacket(Buffer, Buffer.length);
						socket.receive(packetRecibido);
						String mensajeRecibido = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
						System.out.println(mensajeRecibido);
					}
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			});
			recibirMensajes.start();

			while (true) {
				String input = scanner.nextLine();
				if (input.toLowerCase().equals("salir")) {
					// Enviar mensaje de desconexión
					enviarMensaje(socket, "salir", direccionServer, 12345);
					socket.close();
				} else {
					// Encriptar el mensaje antes de enviarlo
					String mensajeEncriptado = encriptar(input);
					enviarMensaje(socket, mensajeEncriptado, direccionServer, 12345);
				}
			}

		} catch (IOException e) {
			System.out.println("Error en el cliente: " + e.getMessage());
		}
	}

	public static void enviarMensaje(DatagramSocket socket, String message, InetAddress address, int port)
			throws IOException {

		byte[] DatosAEnviar = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(DatosAEnviar, DatosAEnviar.length, address, port);
		socket.send(sendPacket);
	}

	public static String encriptar(String message) {
		try {
			Key llave = new SecretKeySpec("AntonioApruebamePorfa".getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, llave);
			byte[] encriptadoBytes = cipher.doFinal(message.getBytes());
			return Base64.getEncoder().encodeToString(encriptadoBytes);
		} catch (Exception e) {
			System.out.println("Error de encriptación: " + e.getMessage());
		}
		return null;
	}

}