package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Cliente extends Thread {
	private Socket socket;
	private DataInputStream entrada;
	private DataOutputStream salida;
	private Usuario usuario;

	public Cliente(Socket clienteIn, Socket clienteOut) {
		this.socket = clienteIn;
		try {
			entrada = new DataInputStream(clienteIn.getInputStream());

			salida = new DataOutputStream(clienteOut.getOutputStream());
		} catch (IOException ex) {
			System.out.println("Error al crear los stream de entrada y salida : " + ex.getMessage());

		}
	}

	@Override
	public void run() {
		boolean conectado = true;
		Properties properties;

		while (conectado) {
			try {
				String cadena = this.entrada.readUTF();
				Message message = (Message) new Gson().fromJson(cadena, Message.class);
				switch (message.getType()) {
				// LOGIN
				case Constantes.LOGIN_REQUEST:
					properties = new Gson().fromJson((String) message.getData(), Properties.class);

					usuario = new Usuario(properties.getProperty("username"));

					if (usuario.getNombre() == "") {
						this.salida.flush();
						this.salida.writeUTF(new Message(Constantes.INCORRECT_LOGIN, null).toJson());
						System.out.println("[LOGIN] El usuario " + properties.getProperty("username")
								+ " fracaso en su inicio de sesion.");
					} else {
						boolean usuarioDuplicado = false;
						for (Usuario usuarioActivo : Servidor.getUsuariosActivos()) {

							if (usuarioActivo.getNombre() == usuario.getNombre()) {
								this.salida.flush();
								this.salida.writeUTF(new Message(Constantes.DUPLICATED_LOGIN, null).toJson());
								usuarioDuplicado = true;
								System.out.println("[LOGIN] El usuario " + properties.getProperty("username")
										+ " intento loguearse sin haber cerrado sesion.");
								break;
							}

						}
						if (!usuarioDuplicado) {
							Servidor.agregarAUsuariosActivos(usuario);
							this.salida.flush();
							this.salida.writeUTF(
									new Message(Constantes.CORRECT_LOGIN, new Gson().toJson(usuario)).toJson());
							System.out.println("[LOGIN] El usuario " + properties.getProperty("username")
									+ " ingreso correctamente.");
						}
					}
					break;
				case Constantes.LOGOUT_REQUEST:
					usuario = new Gson().fromJson((String) message.getData(), Usuario.class);
					for (Usuario usuarioEnServer : Servidor.getUsuariosActivos()) {
						if (usuario != null && usuarioEnServer.getNombre() == usuario.getNombre()) {
							Servidor.removerUsuarioActivo(usuarioEnServer);
							this.salida.flush();
							this.salida.writeUTF(new Message(Constantes.CORRECT_LOGOUT, null).toJson());
							break;
						} else {
							this.salida.flush();
							this.salida.writeUTF(new Message(Constantes.INCORRECT_LOGOUT, usuario).toJson());
						}
					}
					break;
				default:
					break;
				}
			} catch (IOException ex) {
				String mensaje = "";
				if (this.usuario != null) {
					mensaje = "[CLIENTE] El usuario " + this.usuario.getNombre() + " se ha desconectado.";
				} else {
					mensaje = "[CLIENTE] Cliente con la IP " + socket.getInetAddress().getHostName() + " desconectado.";
				}

				System.out.println(mensaje);
				conectado = false;
				for (Usuario usuarioActivo : Servidor.getUsuariosActivos()) {

					if (usuarioActivo.getNombre() == usuario.getNombre()) {
						Servidor.getUsuariosActivos().remove(usuario);
						break;
					}

				}
				try {
					entrada.close();
					salida.close();
				} catch (IOException ex2) {
					String mensajeError2 = "Error al cerrar los stream de entrada y salida :" + ex2.getMessage();
					System.out.println(mensajeError2);
				}
			} catch (JsonSyntaxException e) {
				System.out.println("Error de sintaxis en el json " + e.getMessage());
			}
		}
		Servidor.desconectar(this);
	}

	public DataOutputStream getSalida() {
		return this.salida;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

}
