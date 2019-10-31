package com.unlam.chat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public class ChatClient extends JFrame {

	private JPanel contentPane = new JPanel();
	private JTextField msg_txt = new JTextField();
	private static JTextArea msg_area = new JTextArea();
	private JButton msg_send = new JButton("Send");

	static Socket s;
	static DataInputStream din;
	static DataOutputStream dout;
	static int bandera = 0;

	static String pathFile = "";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatClient frame = new ChatClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		try {

			// Es el ip local donde se levantara el cliente
			s = new Socket("127.0.0.1", 1201);

			din = new DataInputStream(s.getInputStream());

			dout = new DataOutputStream(s.getOutputStream());

			String msgin = "";

			File file = new File("src\\main\\java\\21-10-2019.txt");

			Scanner sc = new Scanner(file);

			if (bandera == 0) {

				// we just need to use \\Z as delimiter
				sc.useDelimiter("\\Z");

				msg_area.setText(sc.next());

				bandera = 1;
			}

			sc.close();
			while (!msgin.equals("exit")) {

				msgin = din.readUTF();

				msg_area.setText(msg_area.getText().trim() + "\nServer:\t" + msgin);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Create the frame.
	 */
	public ChatClient() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 857, 514);
		contentPane = new JPanel();
		contentPane.setAutoscrolls(true);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		LocalDate date = LocalDate.now();

		this.pathFile = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString();

//		msg_txt = new JTextField();
		msg_txt.setPreferredSize(new Dimension(100, 50));
		msg_txt.setMinimumSize(new Dimension(10, 10));
		contentPane.add(msg_txt, BorderLayout.SOUTH);
		msg_txt.setColumns(10);

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

//		JTextArea msg_area = new JTextArea();
		msg_area.setAutoscrolls(false);
		msg_area.setMaximumSize(new Dimension(1000000000, 1000000000));
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

					data.writeToFile("Client: \t" + msgout);

					msg_txt.setText("");

				} catch (Exception ev) {

				}
			}
		});

//		JButton msg_send = new JButton("New button");
		msg_send.setPreferredSize(new Dimension(100, 100));
		contentPane.add(msg_send, BorderLayout.EAST);
	}

//	private void msg_sendActionPerformed(java.awt.event.ActionEvent event) {
//		
//		try {
//			
//			String msgout = "";
//			
//			msgout = msg_txt.getText().trim();
//			
//			dout.writeUTF(msgout);
//		}catch(Exception e) {
//			
//		}
//	}
}
