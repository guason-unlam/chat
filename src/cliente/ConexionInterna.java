package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.json.Json;

import com.google.gson.Gson;

import servidor.Constantes;
import servidor.Message;

public class ConexionInterna extends Thread {
	private Socket socketIn;
	private Socket socketOut;
	private Usuario usuario;
	private Message message;
	private DataOutputStream salidaDatos;
	private DataInputStream entradaDatos;

	public ConexionInterna(Socket socketOut, Socket socketIn) {
		this.socketOut = socketOut;
		this.socketIn = socketIn;
		try {
			this.salidaDatos = new DataOutputStream(this.socketOut.getOutputStream());
			this.entradaDatos = new DataInputStream(this.socketIn.getInputStream());
		} catch (IOException ex) {
			System.out.println("Error al crear el stream de salida : " + ex.getMessage());
		} catch (NullPointerException ex) {
			System.out.println("El socket no se creo correctamente. ");
		}
	}

	// Ya mando la pw encriptada

	public Usuario logear(String usuario) {

		try {
			String request = Json.createObjectBuilder().add("username", usuario).build().toString();

			this.salidaDatos.writeUTF(new Message(Constantes.LOGIN_REQUEST, request).toJson());

			this.message = (Message) new Gson().fromJson((String) entradaDatos.readUTF(), Message.class);
			switch (this.message.getType()) {
			case Constantes.CORRECT_LOGIN:
				this.usuario = new Gson().fromJson((String) message.getData(), Usuario.class);
				return this.usuario;
			case Constantes.INCORRECT_LOGIN:
				return null;
			case Constantes.DUPLICATED_LOGIN:
				return new Usuario("");
			default:
				return null;
			}
		} catch (Exception e) {
			System.out.println("[LOGIN] " + e.getMessage());
		}
		return null;
	}

	public Boolean logout(Usuario u) {

		try {

			this.salidaDatos.writeUTF(new Message(Constantes.LOGOUT_REQUEST, u).toJson());

			this.message = (Message) new Gson().fromJson((String) entradaDatos.readUTF(), Message.class);
			switch (this.message.getType()) {
			case Constantes.CORRECT_LOGOUT:
				return true;
			case Constantes.INCORRECT_LOGIN:
				return false;
			default:
				return false;
			}
		} catch (Exception e) {
			System.out.println("[LOGOUT] " + e.getMessage());
		}
		return null;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public String refreshRooms() {
		try {
			while (true) {
				this.message = (Message) new Gson().fromJson((String) entradaDatos.readUTF(), Message.class);
				if (message.getType() == Constantes.REQUEST_REFRESH) {
					return (String) message.getData();
				}
			}
		} catch (Exception ex) {
			System.out.println("[REFRESH ROOM]" + ex.getMessage());
		}
		return null;

	}

	public String enviarMensaje(String text) {
		try {
			// Le paso user y mensaje
			String request = Json.createObjectBuilder().add("username", usuario.getNombre()).add("message", text)
					.build().toString();

			this.salidaDatos.writeUTF(new Message(Constantes.MESSAGE_REQUEST, request).toJson());

			this.message = (Message) new Gson().fromJson((String) entradaDatos.readUTF(), Message.class);
			switch (this.message.getType()) {
			case Constantes.INCORRECT_MESSAGE:
				return Constantes.MESSAGE_ERROR;
			default:
				return null;
			}
		} catch (Exception e) {
			System.out.println("[MENSAJE] " + e.getMessage());
		}
		return null;
	}

}