package cliente;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.json.Json;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import servidor.Constantes;

public class VentanaChat extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9099821102113802071L;
	private Usuario usuario;
	private JTextField msg_txt;
	private static JTextArea msg_area;
	private JButton msg_send;

	public VentanaChat() {
		// Obtengo el usuario!
		usuario = Cliente.getConexionInterna().getUsuario();

		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		setTitle("Chat");

		msg_area = new JTextArea();
		msg_area.setBackground(Color.LIGHT_GRAY);
		msg_area.setBounds(new Rectangle(188, 71, 496, 310));
		msg_area.setAutoscrolls(false);
		msg_area.setMaximumSize(new Dimension(50, 50));
		msg_area.setMinimumSize(new Dimension(1, 1));
		msg_area.setPreferredSize(new Dimension(350, 350));
		msg_area.setEditable(false);
		getContentPane().add(msg_area);

		msg_txt = new JTextField();
		msg_txt.setBounds(new Rectangle(188, 401, 390, 59));
		msg_txt.setPreferredSize(new Dimension(100, 50));
		msg_txt.setMinimumSize(new Dimension(10, 10));
		msg_txt.setColumns(10);
		getContentPane().add(msg_txt);

		msg_send = new JButton("Enviar");
		msg_send.setBounds(new Rectangle(588, 400, 96, 60));
		msg_send.setPreferredSize(new Dimension(100, 100));
		getContentPane().add(msg_send);

		JLabel lblBienvenida = new JLabel("Hola " + usuario.getNombre() + "!");
		lblBienvenida.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblBienvenida.setBounds(180, 11, 380, 68);
		lblBienvenida.setHorizontalAlignment(JLabel.CENTER);
		lblBienvenida.setVerticalAlignment(JLabel.CENTER);
		getContentPane().add(lblBienvenida);
		// Sin esto, el yes/no dialog no sirve
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setBounds(0, 0, Constantes.CHAT_WIDTH, Constantes.CHAT_HEIGHT);
		this.setLocationRelativeTo(null);
		addListener();

	}

	private void addListener() {

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				int opcion = JOptionPane.showConfirmDialog(getContentPane(), "Desea cerrar la ventana?", "Atenci�n!",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (opcion == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		// FALTA TERMINAR
		// POR AHORA SE CUELGA
		// this.msg_txt.addActionListener(sendMessagePerformed());
		// this.msg_send.addActionListener(sendMessagePerformed());

	}

	private ActionListener sendMessagePerformed() {
		String msg = Cliente.getConexionInterna().enviarMensaje(this.msg_txt.getText());

		Cliente.getConexionServidor()
				.enviarAlServidor(Json.createObjectBuilder().add("type", Constantes.MESSAGE_REQUEST_SV)
						.add("username", this.usuario.getNombre()).add("message", this.msg_txt.getText()).build());

		if (msg == "") {
			this.msg_txt.setText("");
			this.msg_txt.setFocusable(true);
		} else {
			JOptionPane.showMessageDialog(null, "Error al enviar el mensaje, intente nuevamente", "Error",
					JOptionPane.ERROR_MESSAGE);
			this.msg_txt.setFocusable(true);
		}

		return null;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String comStr = ae.getActionCommand();

		if (comStr == "Salir") {
			int opcion = JOptionPane.showConfirmDialog(getContentPane(), "Desea cerrar la ventana?", "Atenci�n!",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			System.out.println(opcion);
			if (opcion == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}
}
