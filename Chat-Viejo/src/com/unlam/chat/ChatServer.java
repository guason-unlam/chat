package com.unlam.chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ChatServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4917462739177590401L;
	private JPanel contentPane = new JPanel();
	private JTextField msg_txt = new JTextField();
	private static JTextArea msg_area = new JTextArea();
	private JButton msg_send = new JButton("Send");

	// this.add(textArea); // get rid of this
	static ServerSocket ss;
	static Socket s;
	static DataInputStream din;
	static DataOutputStream dout;
	static int bandera = 0;

	static String pathFile = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws FileNotFoundException {

		EventQueue.invokeLater(new Runnable() {

			public void run() {

				try {
					ChatServer frame = new ChatServer();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		String msgin = "";

		try {

			// Se inicializa el server en el puerto 1201
			ss = new ServerSocket(1201);

			// Luego aceptamos la conexion al puerto
			s = ss.accept();

			din = new DataInputStream(s.getInputStream());

			dout = new DataOutputStream(s.getOutputStream());

			if (bandera == 0) {

				File dir = new File("src\\main\\java\\");

				dir.listFiles(new FilenameFilter() {

					public boolean accept(File dir, String name) {
						// TODO Auto-generated method stub

						if (name.contains(".txt")) {

							String[] fec = name.split(".txt");

							String fecName = fec[0];

							File file = new File("src\\main\\java\\" + name);

							Scanner sc;
							try {
								sc = new Scanner(file);

								sc.useDelimiter("\\Z");

								msg_area.setText(msg_area.getText() + fecName + "\n" + sc.next() + "\n");

								sc.close();

							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						return false;
					}
				});

				bandera = 1;
			}

			while (!msgin.equals("exit")) {

				msgin = din.readUTF();

				// Se muestra el mensaje del cliente
				msg_area.setText(msg_area.getText().trim() + "\nClient:\t" + msgin);

			}

		} catch (Exception e) {

		}
	}

	/**
	 * Create the frame.
	 */
	public ChatServer() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1179, 700);

		contentPane.setAutoscrolls(true);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		LocalDate date = LocalDate.now();

		this.pathFile = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString();

		msg_txt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		msg_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

			}
		});

		msg_txt.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					msg_send.doClick();
				}
			}
		});
		// msg_txt = new JTextField();
		msg_txt.setPreferredSize(new Dimension(100, 50));
		msg_txt.setMinimumSize(new Dimension(10, 10));
		contentPane.add(msg_txt, BorderLayout.CENTER);
		msg_txt.setColumns(10);

		// msg_area = new JTextArea();
		msg_area.setAutoscrolls(false);
		msg_area.setMaximumSize(new Dimension(50, 50));
		msg_area.setMinimumSize(new Dimension(1, 1));
		msg_area.setPreferredSize(new Dimension(350, 350));

		contentPane.add(msg_area, BorderLayout.NORTH);

		msg_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msgout = "";

					msgout = msg_txt.getText().trim();

					dout.writeUTF(msgout);

					WriteFile data = new WriteFile("src\\\\main\\\\java\\\\" + pathFile + ".txt", true);

					data.writeToFile("Server: \t" + msgout);

					msg_txt.setText("");
				} catch (Exception ev) {

				}
			}
		});

		msg_send.setPreferredSize(new Dimension(100, 100));
		contentPane.add(msg_send, BorderLayout.EAST);
	}

}
