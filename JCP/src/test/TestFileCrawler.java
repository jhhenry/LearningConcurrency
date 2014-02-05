package test;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import source.chap5.FileCrawler;

public class TestFileCrawler {

	@Test
	public void test() throws InterruptedException { //When your code calls a method that throws InterruptedException, then your method is a blocking method too, and must have a plan for responding to interruption. 
		//test-specific variable:
		FileFilter ff = new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f != null && f.getName().length() < 10;
			}
		
		};
		File root = new File("D:/");
		
		
		//core of consumer-producer pattern: blockingQueue
		BlockingQueue<File> queue = new LinkedBlockingQueue<File>(100);
		
		//producer
		FileCrawler producer = new FileCrawler(queue, ff, root);
		new Thread(producer).start();
		
		//consumer
		File getFile = null;
		int count = 0;
		while(!producer.isFinished() ) {
			getFile = queue.poll(100, TimeUnit.MILLISECONDS);
			if (getFile != null) {
				//System.out.println(getFile.getAbsolutePath());
				count++;
			}
		}
		
		if (!queue.isEmpty()) {
			System.out.println("still files in queue to be consumed.");
			while((getFile = queue.poll()) != null) {
				count++;
			}
		}
		
		
		System.out.println("total count of files: " + count);
		
	}

}
