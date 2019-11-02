package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import servidor.Constantes;

public class ConexionServidor extends Thread {

	private Socket socketIn;
	private Socket socketOut;

	private DataInputStream entrada;
	private DataOutputStream salida;

	@Override
	public void run() {
		boolean conectado = true;

		while (conectado) {

			try {
				String entrada;
					entrada = (String) this.entrada.readUTF();
				JsonReader jsonReader = Json.createReader(new StringReader(entrada));
				JsonObject entradaJson = jsonReader.readObject();
				jsonReader.close();

				String tipoDeMensaje = entradaJson.getString("type");
				switch(tipoDeMensaje) {
				case Constantes.MESSAGE_REQUEST:
					VentanaChat.mostrarMensaje(entradaJson.getString("username"),entradaJson.getString("message"));
				}
			} catch (IOException e) {
				System.out.println(e.getMessage() + "[ConexionServidor] Error del lado del cliente ");
				conectado = false;

				try {
					this.entrada.close();
					this.salida.close();
				} catch (IOException ex2) {
					System.out.println("Error al cerrar los stream de entrada y salida:" + ex2.getMessage());
				}
			}
		}
				
	}
	
	public ConexionServidor(Socket servidorOut, Socket servidorIn) {
		this.socketIn = servidorIn;
		this.socketOut = servidorOut;
		try {
			this.entrada = new DataInputStream(this.socketIn.getInputStream());
			this.salida = new DataOutputStream(this.socketOut.getOutputStream());

		} catch (IOException ex) {
			System.out.println("Error al crear los stream de entrada y salida : " + ex.getMessage());
		}
	}

	public void enviarAlServidor(JsonObject paquete) {
		try {
			this.salida.writeUTF(paquete.toString());
		} catch (IOException e) {
			System.out.println("Error " + paquete.toString());
		}

	}

	public DataInputStream getEntrada() {
		return entrada;
	}


}
