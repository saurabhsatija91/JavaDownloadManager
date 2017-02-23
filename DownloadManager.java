package JavaDownloadManager;

import java.net.URL;
import java.util.ArrayList;

public class DownloadManager {
	private static DownloadManager sInstance = null;
	
	private static final int DEFAULT_NUM_OF_CONN_PER_DOWNLOAD = 8;
	public static final String DEFAULT_OUTPUT_FOLDER = "/Users/Saurabh/Documents/DataTempTransfer/client/";
	
	private int numConnPerDownload;
	private ArrayList<Downloader> downloadList;
	
	protected DownloadManager() {
		numConnPerDownload = DEFAULT_NUM_OF_CONN_PER_DOWNLOAD;
		downloadList = new ArrayList<Downloader>();
	}
	
	public int getNumConnPerDownload() {
		return numConnPerDownload;
	}
	
	public void setNumConnPerDownload(int value) {
		numConnPerDownload = value;
	}
	
	public Downloader createDownload(URL verifiedURL, String outputFolder) {
		HttpDownloader fileDownloader = new HttpDownloader (verifiedURL, outputFolder, numConnPerDownload);
		downloadList.add(fileDownloader);
		
		return fileDownloader;
	}
	
	/*
	 * Return the unique instance of this class
	 */
	public static DownloadManager getInstance() {
		if (sInstance == null)
			sInstance = new DownloadManager();
		
		return sInstance;
	}
	
	/*
	 * Verify the URL
	 */
	public static URL verifyURL (String fileURL) {
		if (!fileURL.toLowerCase().startsWith("http://"))
			return null;
		
		URL verifiedURL = null;
		try {
			verifiedURL = new URL(fileURL);
		} catch (Exception e) {
			return null;
		}
		
		//Check if there is a file at this URL
		if (verifiedURL.getFile().length() < 2)
			return null;
		
		return verifiedURL;
	}
	
}
























