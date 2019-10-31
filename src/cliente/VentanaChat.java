package cliente;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import servidor.Constantes;

public class VentanaChat extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9099821102113802071L;
	private Usuario usuario;

	public VentanaChat() {
		// Obtengo el usuario!
		usuario = Cliente.getConexionInterna().getUsuario();

		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		setTitle("Chat");

		JLabel lblBienvenida = new JLabel("Hola " + usuario.getNombre() + "!");
		lblBienvenida.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblBienvenida.setBounds(34, 56, 380, 68);
		lblBienvenida.setHorizontalAlignment(JLabel.CENTER);
		lblBienvenida.setVerticalAlignment(JLabel.CENTER);
		getContentPane().add(lblBienvenida);
		// Sin esto, el yes/no dialog no sirve
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setBounds(0, 0, Constantes.LOGIN_WIDTH, Constantes.LOGIN_HEIGHT);
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

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String comStr = ae.getActionCommand();

		if (comStr == "Salir") {
			int opcion = JOptionPane.showConfirmDialog(getContentPane(), "Desea cerrar la ventana?", "Atención!",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			System.out.println(opcion);
			if (opcion == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}

		System.out.println(comStr + " Selected");
	}
}
