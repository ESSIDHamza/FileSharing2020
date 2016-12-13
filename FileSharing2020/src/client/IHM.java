package client;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class IHM extends JFrame {
	private JButton partagerButton = new JButton("Partager");
	private JButton telechargerButton = new JButton("Telecharger");
	private JButton rafraichirButton = new JButton("Rafraichir");
	private JList<String> fichiers = new JList<String>();
	private Socket socket;
	private JTextField motCleTextField = new JTextField();
	private JButton rechercherButton = new JButton("Rechercher");
	private JLabel motsClesLabel = new JLabel("Mot(s)-cle(s) : ");

	public IHM(Socket socket) {
		this.socket = socket;
		setTitle("FileSharing2020");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		JPanel sudPanel = new JPanel();
		Ecouteur ecouteur = new Ecouteur(this, socket);
		partagerButton.addActionListener(ecouteur);
		sudPanel.add(partagerButton);
		telechargerButton.addActionListener(ecouteur);
		sudPanel.add(telechargerButton);
		sudPanel.add(motsClesLabel);
		motCleTextField.setColumns(10);
		sudPanel.add(motCleTextField);
		rechercherButton.addActionListener(ecouteur);
		sudPanel.add(rechercherButton);
		rafraichirButton.addActionListener(ecouteur);
		add(sudPanel, BorderLayout.SOUTH);
		add(new JScrollPane(fichiers), BorderLayout.CENTER);
		add(rafraichirButton, BorderLayout.NORTH);
		setVisible(true);
		setLocationRelativeTo(null);
		try {
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataOutputStream.writeUTF("rafraichir");
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(this, "Erreur lors de l'obtention du flux d'écriture des données !",
					"FileSharing2020", JOptionPane.ERROR_MESSAGE);
		}
		try {
			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			String fichiersRecus = dataInputStream.readUTF();
			String[] fichiers = fichiersRecus.split(Pattern.quote(", "));
			fichiers[0] = fichiers[0].replace("[", "");
			fichiers[fichiers.length - 1] = fichiers[fichiers.length - 1].replace("]", "");
			this.fichiers.setListData(fichiers);
		} catch (IOException exception) {
			JOptionPane.showMessageDialog(this, "Erreur lors de l'obtention du flux de lecture des données !",
					"FileSharing2020", JOptionPane.ERROR_MESSAGE);
		}
	}

	public JButton getPartagerButton() {
		return partagerButton;
	}

	public JButton getTelechargerButton() {
		return telechargerButton;
	}

	public JList<String> getFichiers() {
		return fichiers;
	}

	public Socket getSocket() {
		return socket;
	}

	public JButton getRafraichirButton() {
		return rafraichirButton;
	}

	public JTextField getMotCleTextField() {
		return motCleTextField;
	}

	public JButton getRechercherButton() {
		return rechercherButton;
	}
}