package cliente;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.json.Json;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import servidor.Constantes;

public class PantallaLogin extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3673971401919801676L;
	private JTextField username;
	private JButton btnLogin;
	public static JLabel lblEstado;

	/**
	 * Create the application.
	 */
	public PantallaLogin() {
		this.setTitle("Chat");

		this.setBounds(100, 100, 450, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		JLabel usernameLabel = new JLabel("Nombre");
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 39));
		usernameLabel.setToolTipText("");
		usernameLabel.setBounds(132, 22, 142, 70);
		this.getContentPane().add(usernameLabel);

		this.username = new JTextField();
		this.username.setToolTipText("Ingrese su nombre");
		this.username.setBounds(109, 103, 186, 65);
		this.username.setColumns(10);
		this.getContentPane().add(this.username);

		this.btnLogin = new JButton("Iniciar sesi\u00F3n");
		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 26));
		this.btnLogin.setBounds(85, 206, 272, 54);

		this.getContentPane().add(this.btnLogin);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0, 0, Constantes.LOGIN_WIDTH, Constantes.LOGIN_HEIGHT);
		this.setLocationRelativeTo(null);

		addListener();
	}

	protected void iniciarSession() throws IOException {

		if (this.username.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Falta ingresar el usuario!", "Error login",
					JOptionPane.WARNING_MESSAGE);
			this.username.setFocusable(true);
			return;
		}

		Usuario usuario = Cliente.getConexionInterna().logear(this.username.getText());

		Cliente.getConexionServidor().enviarAlServidor(Json.createObjectBuilder()
				.add("type", Constantes.LOGIN_REQUEST_SV).add("username", this.username.getText()).build());

		if (usuario != null && usuario.getNombre() != "") {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						VentanaChat frame = new VentanaChat();
						frame.setVisible(true);
						dispose();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else if (usuario != null && usuario.getNombre() == "") {
			JOptionPane.showMessageDialog(null, "Ingrese otro nombre", "Error login", JOptionPane.ERROR_MESSAGE);
			this.username.setText("");
			this.username.setFocusable(true);
		} else {
			JOptionPane.showMessageDialog(null, "Usted ha introducido un usuario y/o clave incorrecta", "Error login",
					JOptionPane.ERROR_MESSAGE);
			this.username.setText("");
			this.username.setFocusable(true);
		}
	}

	private void addListener() {

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(getContentPane(), "Desea cerrar la ventana?", "Atenci\u00F3n!",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		this.username.addActionListener(iniciarSessionPerformed());
		this.btnLogin.addActionListener(iniciarSessionPerformed());

	}

	private ActionListener iniciarSessionPerformed() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					iniciarSession();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}
}