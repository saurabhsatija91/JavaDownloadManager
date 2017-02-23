package JavaDownloadManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public abstract class Downloader implements Runnable {
	protected URL URL;
	protected String outputFolder;
	protected int numConnections;
	protected String fileName;
	protected int fileSize;
	protected int state;	//state of download
	protected int downloaded;	// in Bytes
	protected ArrayList<DownloadThread> listDownloadThread;
	
	protected static final int BLOCK_SIZE = 4096;
	protected static final int BUFFER_SIZE = 4096;
	protected static final int MIN_DOWNLOAD_SIZE = BLOCK_SIZE * 4;
	
	public static final String STATUSES[] = {"Downloading",
											"Complete",
											"Error"};
	
	public static final int DOWNLOADING = 0;
	public static final int COMPLETED = 1;
	public static final int ERROR = 2;
	
	/*
	 * Constructor
	 */
	protected Downloader (URL url, String outputFolder, int numConnections) {
		this.URL = url;
		this.outputFolder = outputFolder;
		this.numConnections = numConnections;
		
		File fileURL = new File(url.getFile());
		String file = fileURL.getName();
		fileName = file.substring(file.lastIndexOf('/') + 1);
		System.out.println(">> File name: " + fileName);
		fileSize = -1;
		state = DOWNLOADING;
		downloaded = 0;
		listDownloadThread = new ArrayList<DownloadThread>();
	}
	
	protected void download() {
		Thread t = new Thread(this);
		t.start();
	}
	
	public synchronized float getProgess() {
		return ((float) downloaded / fileSize) * 100;
	}
	public int getState() {
		return state;
	}
	protected void setState(int value) {
		state = value;
	}
	protected synchronized void downloaded(int value) {
		downloaded += value;
		System.out.println("Downloaded: " + downloaded);
	}
	
	/*
	 * Thread to download one part
	 */
	protected abstract class DownloadThread implements Runnable {
		protected int threadID;
		protected URL URL;
		protected String outputFile;
		protected int startByte;
		protected int endByte;
		protected boolean finished;
		protected Thread partThread;
		
		public DownloadThread (int threadID, URL url, String outputFile,
				int startByte, int endByte) {
			this.threadID = threadID;
			this.URL = url;
			this.outputFile = outputFile;
			this.startByte = startByte;
			this.endByte = endByte;
			this.finished = false;
			
			this.download();
		}
		
		public boolean isFinished() {
			return finished;
		}
		
		/*
		 * Function to start downloading one part.
		 */
		public void download() {
			partThread = new Thread(this);
			partThread.start();
		}
		public void waitToFinish() throws InterruptedException {
			partThread.join();
		}
	}
}






















