package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import util.FileSharing2020Utils;

public class Ecouteur implements ActionListener {
	private IHM ihm;

	public Ecouteur(IHM ihm, Socket socket) {
		this.ihm = ihm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ihm.getPartagerButton()) {
			JFileChooser jFileChooser = new JFileChooser();
			int resultat = jFileChooser.showOpenDialog(ihm);
			if (resultat == JFileChooser.APPROVE_OPTION) {
				try {
					DataOutputStream dataOutputStream = new DataOutputStream(ihm.getSocket().getOutputStream());
					dataOutputStream.writeUTF("partager");
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(ihm, "Erreur lors de l'obtention du flux d'écriture des données !",
							"FileSharing2020", JOptionPane.ERROR_MESSAGE);
				}
				try {
					FileSharing2020Utils.envoyerFichier(jFileChooser.getSelectedFile().getAbsolutePath(),
							ihm.getSocket());
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(ihm, "Erreur lors de l'envoi de votre fichier !", "FileSharing2020",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if (e.getSource() == ihm.getTelechargerButton()) {
			String nomFichier = ihm.getFichiers().getSelectedValue();
			if (nomFichier != null) {
				try {
					DataOutputStream dataOutputStream = new DataOutputStream(ihm.getSocket().getOutputStream());
					dataOutputStream.writeUTF(nomFichier);
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(ihm, "Erreur lors de l'obtention du flux d'écriture des données !",
							"FileSharing2020", JOptionPane.ERROR_MESSAGE);
				}
				try {
					FileSharing2020Utils.recevoirFichier(ihm.getSocket(), "C:\\FileSharing2020\\fichiers_telecharges");
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(ihm, "Erreur lors de la réception de votre fichier !",
							"FileSharing2020", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if (e.getSource() == ihm.getRafraichirButton()) {
			try {
				DataOutputStream dataOutputStream = new DataOutputStream(ihm.getSocket().getOutputStream());
				dataOutputStream.writeUTF("rafraichir");
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(ihm, "Erreur lors de l'obtention du flux d'écriture des données !",
						"FileSharing2020", JOptionPane.ERROR_MESSAGE);
			}
			try {
				DataInputStream dataInputStream = new DataInputStream(ihm.getSocket().getInputStream());
				String fichiersRecus = dataInputStream.readUTF();
				String[] fichiers = fichiersRecus.split(Pattern.quote(", "));
				fichiers[0] = fichiers[0].replace("[", "");
				fichiers[fichiers.length - 1] = fichiers[fichiers.length - 1].replace("]", "");
				ihm.getFichiers().setListData(fichiers);
			} catch (IOException exception) {
				JOptionPane.showMessageDialog(ihm, "Erreur lors de l'obtention du flux de lecture des données !",
						"FileSharing2020", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (e.getSource() == ihm.getRechercherButton()) {
			String motCle = ihm.getMotCleTextField().getText();
			if (motCle.isEmpty())
				JOptionPane.showMessageDialog(ihm, "Veuillez saisir le(s) mot(s)-cle(s) !", "FileSharing2020",
						JOptionPane.ERROR_MESSAGE);
			else {
				ihm.getMotCleTextField().setText("");
				try {
					DataOutputStream dataOutputStream = new DataOutputStream(ihm.getSocket().getOutputStream());
					dataOutputStream.writeUTF("rechercher");
					dataOutputStream.writeUTF(motCle);
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(ihm, "Erreur lors de l'obtention du flux d'écriture des données !",
							"FileSharing2020", JOptionPane.ERROR_MESSAGE);
				}
				try {
					DataInputStream dataInputStream = new DataInputStream(ihm.getSocket().getInputStream());
					String resultat = dataInputStream.readUTF();
					String[] resultats = resultat.split(Pattern.quote(", "));
					resultats[0] = resultats[0].replace("[", "");
					resultats[resultats.length - 1] = resultats[resultats.length - 1].replace("]", "");
					String resultatAffiche = "";
					for (int i = 0; i < resultats.length - 1; i++)
						resultatAffiche = resultatAffiche + resultats[i] + '\n';
					resultatAffiche += resultats[resultats.length - 1];
					JOptionPane.showMessageDialog(ihm, resultatAffiche, "FileSharing2020 - Resultat de la recherche",
							JOptionPane.PLAIN_MESSAGE);
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(ihm, "Erreur lors de l'obtention du flux de lecture des données !",
							"FileSharing2020", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}