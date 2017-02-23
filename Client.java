package JavaDownloadManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String[] args) {
		Socket server;
		try {
			server = new Socket("localhost", 4444);
			DataInputStream din = new DataInputStream(server.getInputStream());
			byte[] urlByteArr = new byte[2000];
			din.read(urlByteArr);
			String fileurl = new String(urlByteArr);
			System.out.println("fileURL: " + fileurl);
			
			DownloadManager.getInstance().createDownload(DownloadManager.verifyURL(fileurl), 
        			DownloadManager.DEFAULT_OUTPUT_FOLDER);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
