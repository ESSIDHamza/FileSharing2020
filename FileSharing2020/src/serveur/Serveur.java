package serveur;

import java.net.ServerSocket;
import java.net.Socket;

import util.FileSharing2020Utils;

public class Serveur {
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(2016);
		FileSharing2020Utils.initIndex();
		System.out.println("Serveur démarré !");
		while (true) {
			Socket socket = serverSocket.accept();
			System.out.println("Un nouveau client vient de se connecter ! " + socket.getInetAddress());
			new Thread(new ServeurThread(socket)).start();
			System.gc();
		}
	}
}