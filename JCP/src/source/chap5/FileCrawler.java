package source.chap5;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;

public class FileCrawler implements Runnable {
    private final BlockingQueue<File> fileQueue;
    private final FileFilter fileFilter;
    private final File root;
    private volatile boolean finished;
    
    public FileCrawler(BlockingQueue<File> fileQueue, FileFilter ff, File r) {
    	root = r;
    	fileFilter = ff;
    	this.fileQueue = fileQueue;
    }
    
    public void run() {
        try {
            crawl(root);
            finished = true; //null indicates end of crawl
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public boolean isFinished()
    {
    	return finished;
    }

    private void crawl(File root) throws InterruptedException {
        File[] entries = root.listFiles(fileFilter);
        if (entries != null) {
            for (File entry : entries)
                if (entry.isDirectory()) {
                    crawl(entry);
                } else if (!alreadyIndexed(entry)) {
                    fileQueue.put(entry);
                }
        }
    }

	private boolean alreadyIndexed(File entry) {
		return false; 
	}
	
	public static void main(String[] args)
	{
		
	}
}

