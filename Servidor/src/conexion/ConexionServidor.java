package conexion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import entidades.Usuario;
import utils.Constantes;

public class ConexionServidor extends Thread {

	private Socket socket;
	private DataInputStream entrada;
	private DataOutputStream salida;
	private Usuario usuario;

	public ConexionServidor(Socket servidorIn, Socket servidorOut) {
		this.socket = servidorIn;

		try {
			this.entrada = new DataInputStream(servidorIn.getInputStream());
			this.salida = new DataOutputStream(servidorOut.getOutputStream());

		} catch (IOException ex) {
			System.out.println("Error al crear los stream de entrada y salida : " + ex.getMessage());
		}
	}

	@Override
	public void run() {
		boolean conectado = true;

		while (conectado) {

			try {
				String entrada = (String) this.entrada.readUTF();
				JsonReader jsonReader = Json.createReader(new StringReader(entrada));
				JsonObject entradaJson = jsonReader.readObject();
				jsonReader.close();

				String tipoDeMensaje = entradaJson.getString("type");
				switch(tipoDeMensaje) {
				case Constantes.LOGIN_REQUEST_SV:
					for (Usuario u : Servidor.getUsuariosActivos()) {
						if (u.getNombre().equals(entradaJson.getString("username"))) {
							this.usuario = u;
						}
					}

					if (this.usuario != null) {
						String respuestaLogueoOk = Json.createObjectBuilder()
								.add("type", Constantes.LOGIN_REQUEST_SERVER_CORRECT).build().toString();
						System.out.println("[LOGIN]Usuario " + this.usuario.getNombre() + " se logeo correctamente.");
						this.salida.writeUTF(respuestaLogueoOk);
					}
				break;
				case Constantes.MESSAGE_REQUEST_SV:
					for(ConexionServidor u : Servidor.getServidoresConectados())
					{
						String respuestaMensajeOk = Json.createObjectBuilder()
								.add("type", Constantes.MESSAGE_REQUEST).add("username", entradaJson.getString("username")).add("message", entradaJson.getString("message")).build().toString();
						u.salida.writeUTF(respuestaMensajeOk);	
					}
				break;
				}

			} catch (IOException ex) {
				System.out.println(ex.getMessage() + "[ConexionServidor] Cliente con la IP "
						+ socket.getInetAddress().getHostAddress() + " desconectado.");
				conectado = false;

				try {
					this.entrada.close();
					this.salida.close();
				} catch (IOException ex2) {
					System.out.println("Error al cerrar los stream de entrada y salida:" + ex2.getMessage());
				}
			}
		}

		Servidor.desconectarServidor(this);
	}

	public void actualizarClientesSalaUnica(JsonObject entradaJson) {

		String tipoDeMensaje = entradaJson.getString("type");

		JsonObject paqueteAEnviar;

		// Si ento o salio un chabon, lo saco de la lista
		if (tipoDeMensaje.equals(Constantes.LOGIN_REQUEST_SV) || tipoDeMensaje.equals(Constantes.LOGOUT_REQUEST)) {

			JsonArrayBuilder usernamesConectadosALaSala = Json.createArrayBuilder();

			for (Usuario u : Servidor.getUsuariosActivos()) {
				usernamesConectadosALaSala.add(u.getNombre());
			}

			paqueteAEnviar = Json.createObjectBuilder().add("type", Constantes.REFRESH_ROOM)
					.add("usuarios", usernamesConectadosALaSala.build()).build();
		} else {
			// Aca entro para actualizar el mensaje
			paqueteAEnviar = armarPaqueteParamSala(entradaJson);
		}

		for (ConexionServidor c : Servidor.getServidoresConectados()) {
			try {
				c.salida.writeUTF(paqueteAEnviar.toString());

			} catch (IOException e) {
				System.out.println("Fallo la escritura de datos de actualizar parametros sala");
			}
		}

	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void escribirSalida(JsonObject dato) {
		try {
			this.salida.writeUTF(dato.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JsonObject armarPaqueteParamSala(JsonObject entradaJson) {
		return Json.createObjectBuilder().add("type", Constantes.REFRESH_PARAM_ROOM).build();
	}
}
