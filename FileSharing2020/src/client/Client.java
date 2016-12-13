package client;

import java.net.Socket;

public class Client {
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("localhost", 2016);
		new IHM(socket);
		System.gc();
	}
}