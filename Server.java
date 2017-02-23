package JavaDownloadManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class Server {
	public static void main(String[] args) {
		String filename = "http://localhost:4445/data.txt";
		try {
			ServerSocket server = new ServerSocket(4444);
			Socket client = server.accept();
			System.out.println("Client connected.");
			
			URL fileurl = new URL(filename);
			String urlString = fileurl.toString();
			DataOutputStream bos = new DataOutputStream(client.getOutputStream());
			bos.write(urlString.getBytes("UTF8"));
			
		} catch (IOException e) {}
	}
}
