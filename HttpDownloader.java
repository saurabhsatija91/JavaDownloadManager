package JavaDownloadManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloader extends Downloader {
	public HttpDownloader (URL url, String outputFolder, int numOfConnectionsPerDownload) {
		super(url, outputFolder, numOfConnectionsPerDownload);
		download();
	}
	
	private void error() {
		System.out.println("ERROR");
		setState(ERROR);
	}
	
	@Override
	public void run() {
		HttpURLConnection connection = null;
		try {
			//Open connection to URL
			connection = (HttpURLConnection) URL.openConnection();
			connection.setConnectTimeout(10000);
			
			//Connect to Server
			connection.connect();
			
			//Check if connection was established
			if (connection.getResponseCode() / 100 != 2)
				error();
			
			//Check content length validity
			int contentLength = connection.getContentLength();
			if (contentLength < 1)
				error();
			
			if (fileSize == -1) {
				fileSize = contentLength;
				System.out.println(">> Total FileSize: " + fileSize + " bytes");
			}
			
			//If no error, State DOWNLOADING
			if (state == DOWNLOADING) {
				//If download list is empty, Initiate DOWNLOADING
				if (listDownloadThread.size() == 0) {
					if (fileSize > MIN_DOWNLOAD_SIZE) {
						int partSize = Math.round(((float) fileSize / numConnections) / BLOCK_SIZE) *
								BLOCK_SIZE;
						System.out.println(">> Part Size: " + partSize + " bytes");
						
						int startByte = 0;
						int endByte = partSize - 1;
						HttpDownloadThread thread = new HttpDownloadThread(1, URL, outputFolder +
								fileName, startByte, endByte);
						listDownloadThread.add(thread);
						
						int threadIndex = 2;
						while (endByte < fileSize) {
							startByte = endByte + 1;
							endByte += Math.min(partSize, fileSize - startByte + 1);
							thread = new HttpDownloadThread (threadIndex, URL, outputFolder + fileName,
									startByte, endByte);
							listDownloadThread.add(thread);
							threadIndex++;
						}
						System.out.println("$$ Number of parallel download streams: " + (threadIndex - 1));
						
					} else {
						HttpDownloadThread thread = new HttpDownloadThread (1, URL, outputFolder +
								fileName, 0, fileSize);
						System.out.println("File is not big enough for parallel download");
						listDownloadThread.add(thread);
					}
				}
				for (int i = 0; i < listDownloadThread.size(); i++) {
					listDownloadThread.get(i).waitToFinish();
				}
				
				if (state == DOWNLOADING)	//All downloads were successful
					setState(COMPLETED);
			}
		} catch (Exception e) {
			error();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	private class HttpDownloadThread extends DownloadThread {
		public HttpDownloadThread (int threadID, URL url, String outputFile, int startByte,
				int endByte) {
			super (threadID, url, outputFile, startByte, endByte);
		}
		
		@Override
		public void run() {
			BufferedInputStream in = null;
			RandomAccessFile raf = null;
			
			try {
				HttpURLConnection connection = (HttpURLConnection) URL.openConnection();
				
				String byteRange = startByte + "-" + endByte;
				connection.setRequestProperty("Range", "bytes=" + byteRange);
				System.out.println(">> bytes = " + byteRange);
				
				//Connect to server
				connection.connect();
				
				in = new BufferedInputStream (connection.getInputStream());
				
				//Open output file and seek to start position for this part
				raf = new RandomAccessFile (outputFile, "rw");
				raf.seek(startByte);
				
				int readingSize = Math.min(BUFFER_SIZE, fileSize - startByte);
				byte[] data = new byte[readingSize];
				int bytesRead;
				
				while ((state == DOWNLOADING) && ((bytesRead = in.read(data, 0, readingSize)) != -1)) {
					raf.write(data, 0, bytesRead);
					startByte += bytesRead;
				}
				
				if (state == DOWNLOADING) //No error while downloading this part.
					finished = true;
			} catch (IOException e) {
				error();
			} finally {
				if (raf != null) {
					try {
						raf.close();
					} catch (IOException e) {}
				}
				
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {}
				}
			}
			
			System.out.println(">> End of thread" + threadID);
		}
	}
}


















