package cliente;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.json.Json;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultStyledDocument;

import servidor.Constantes;

public class VentanaChat extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9099821102113802071L;
	private Usuario usuario;
	private JTextArea msg_txt;
	private static JTextArea msg_area;
	private JButton msg_send;
	private InputMap inputMap;
	private ActionMap actionMap;
	private DefaultStyledDocument doc;

	public VentanaChat() {
		getContentPane().setBackground(SystemColor.scrollbar);
		// Obtengo el usuario!
		usuario = Cliente.getConexionInterna().getUsuario();

		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		setTitle("Chat");
		msg_area = new JTextArea();
		msg_area.setBackground(new Color(176, 224, 230));
		msg_area.setEditable(false);
		msg_area.setLineWrap(true);
		DefaultCaret caret = (DefaultCaret) msg_area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroller = new JScrollPane(msg_area);
		scroller.setBackground(Color.LIGHT_GRAY);
		scroller.setBounds(new Rectangle(18, 71, 496, 310));
		scroller.setMaximumSize(new Dimension(50, 50));
		scroller.setMinimumSize(new Dimension(1, 1));
		scroller.setPreferredSize(new Dimension(350, 350));
		getContentPane().add(scroller);

		msg_txt = new JTextArea();
		msg_txt.setBackground(Color.WHITE);
		msg_txt.setBounds(new Rectangle(18, 401, 390, 59));
		msg_txt.setPreferredSize(new Dimension(100, 50));
		msg_txt.setMinimumSize(new Dimension(10, 10));
		msg_txt.setColumns(10);
		msg_txt.setLineWrap(true);
		int condition = JComponent.WHEN_FOCUSED;
		inputMap = msg_txt.getInputMap(condition);
		actionMap = msg_txt.getActionMap();
		msg_txt.setDocument(new JTextFieldLimit(150));

		getContentPane().add(msg_txt);

		msg_send = new JButton("Enviar");
		msg_send.setFont(new Font("Tahoma", Font.BOLD, 17));
		msg_send.setForeground(new Color(0, 0, 0));
		msg_send.setBackground(new Color(255, 204, 153));
		msg_send.setBounds(new Rectangle(418, 400, 96, 60));
		msg_send.setPreferredSize(new Dimension(100, 100));
		this.msg_send.setFocusPainted(false);
		getContentPane().add(msg_send);

		JLabel lblBienvenida = new JLabel("Hola " + usuario.getNombre() + "!");
		lblBienvenida.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblBienvenida.setBounds(72, 0, 380, 68);
		lblBienvenida.setHorizontalAlignment(JLabel.CENTER);
		lblBienvenida.setVerticalAlignment(JLabel.CENTER);
		getContentPane().add(lblBienvenida);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setBounds(0, 0, Constantes.CHAT_WIDTH, Constantes.CHAT_HEIGHT);
		this.setLocationRelativeTo(null);
		addListener();

	}

	private void addListener() {

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				int opcion = JOptionPane.showConfirmDialog(getContentPane(), "Desea cerrar la ventana?", "Atención!",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (opcion == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		this.msg_send.addActionListener(sendMessagePerformed());
		KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		inputMap.put(enterKey, enterKey.toString());
		actionMap.put(enterKey.toString(), new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -9148351933123006114L;

			@Override
			public void actionPerformed(ActionEvent e) {
				enviarTexto();
			}
		});

	}

	private ActionListener sendMessagePerformed() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enviarTexto();
			}
		};
	}

	protected void enviarTexto() {
		String text = msg_txt.getText();

		if (text.equals("")) {
			return;
		}
		String msg = Cliente.getConexionInterna().enviarMensaje(text);

		Cliente.getConexionServidor()
				.enviarAlServidor(Json.createObjectBuilder().add("type", Constantes.MESSAGE_REQUEST_SV)
						.add("username", usuario.getNombre()).add("message", msg_txt.getText()).build());

		if (msg == null) {
			msg_txt.setText("");
			msg_txt.setFocusable(true);
		} else {
			JOptionPane.showMessageDialog(null, "Error al enviar el mensaje, intente nuevamente", "Error",
					JOptionPane.ERROR_MESSAGE);
			msg_txt.setFocusable(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String comStr = ae.getActionCommand();

		if (comStr == "Salir") {
			int opcion = JOptionPane.showConfirmDialog(getContentPane(), "Desea cerrar la ventana?", "Atención!",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (opcion == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}

	public static void mostrarMensaje(String usuario, String mensaje) {
		msg_area.append(usuario + ": " + mensaje + '\n');
	}
}
