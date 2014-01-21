package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

public class TestInterruption {

	private static final class Task implements Runnable {
		private final BlockingQueue<String> queue;
		private volatile Throwable error;

		private Task(BlockingQueue<String> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			try {
				queue.take();
			} catch (InterruptedException e) {
				setError(e);
			}
		}

		public synchronized Throwable getError() {
			return error;
		}

		public synchronized void setError(Throwable error) {
			this.error = error;
		}
	}

	/**
	 * Blocking operations can be interrupted.
	 * @throws InterruptedException
	 */
	@Test
	public void testWhatCanBeInterrupted() throws InterruptedException {
		// fail("Not yet implemented");
		// What kind(s) of operation can the Thread.interrupt() method
		// interrupt?
		final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(100);

		Task task = new Task(queue);
		Thread taskThread = new Thread(task);

		taskThread.start();
		Thread.sleep(10);
		taskThread.interrupt();
		while(taskThread.isAlive()) Thread.sleep(10); // wait for the setError getting called
		assertEquals(task.getError().getClass(), InterruptedException.class);
	}

	/**
	 * I/O operations cannot be interrupted.
	 * @throws IOException
	 */
	@Test
	public void testSocketCannotBeInterrupted() throws IOException {
		final int port = 7777;
		final ServerSocket ss = new ServerSocket(port);
		final CountDownLatch latch = new CountDownLatch(1);
		Thread serverThread = new Thread() {
			public void run() {
				try {
					latch.countDown();
					Socket so = ss.accept();

					InputStream is = so.getInputStream();
					is.read(new byte[8]);
				} catch (Exception ex) {
					
				} finally {

				}
			}
		};
		
		serverThread.start();
		
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			//just ignore it since it is not the point.
		}
		
		serverThread.interrupt();

		assertEquals(true, serverThread.isInterrupted());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(true, serverThread.isAlive());
		ss.close();
	}
	
	/**
	 * Most methods do not respond to interruption.
	 */
	@Test
	public void testNormalOperationsCannotBeInterrupted()
	{
		class Task2 implements Runnable{
			public void run() {
				BigInteger prime = BigInteger.ONE.nextProbablePrime();
				for (int i = 0; i < 10; i++) {
					prime = prime.nextProbablePrime();
					//Thread.sleep(100);
				}
			}
		};
		Thread taskThread = new Thread(new Task2() );
		
		taskThread.start();
		
		taskThread.interrupt();
	}
	
	public static void main(String[] args)
	{
		System.out.println("hello\r\n ooo");
	}

}
