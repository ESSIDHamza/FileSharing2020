package serveur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import util.FileSharing2020Utils;

public class ServeurThread implements Runnable {
	private Socket socket;

	public ServeurThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		boolean running = true;
		while (running) {
			String action = "";
			try {
				DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
				action = dataInputStream.readUTF();
			} catch (IOException exception) {
				running = false;
			}
			if (action.equals("partager"))
				try {
					String nomFichier = FileSharing2020Utils.recevoirFichier(socket,
							"C:\\FileSharing2020\\fichiers_partages");
					FileSharing2020Utils.indexerFichier(nomFichier);
				} catch (Exception e) {
					running = false;
				}
			else if (action.equals("rafraichir")) {
				File file = new File("C:\\FileSharing2020\\fichiers_partages");
				File[] files = file.listFiles();
				List<String> nouveauxFichiers = new ArrayList<String>();
				for (int i = 0; i < files.length; i++) {
					String cheminFichier = files[i].getPath();
					String nomFichier = cheminFichier
							.split(Pattern.quote("\\"))[cheminFichier.split(Pattern.quote("\\")).length - 1];
					nouveauxFichiers.add(nomFichier);
				}
				try {
					DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
					dataOutputStream.writeUTF(nouveauxFichiers.toString());
				} catch (IOException e) {
				}
				System.gc();
			} else if (action.equals("rechercher"))
				try {
					DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
					String motCle = dataInputStream.readUTF();
					List<String> resultat = FileSharing2020Utils.rechercherFichiers(motCle);
					DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
					dataOutputStream.writeUTF(resultat.toString());
				} catch (Exception exception) {
					running = false;
				}
			else
				try {
					FileSharing2020Utils.envoyerFichier("C:\\FileSharing2020\\fichiers_partages\\" + action, socket);
				} catch (Exception exception) {
					running = false;
				}
		}
	}
}