package test;

import org.junit.Test;

public class TestShutdownHook {

	@Test
	public void test()
	{
		class ShutdownTask implements Runnable
		{
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("cleanup work");
			}
			
		}
		Thread hookThread = new Thread(new ShutdownTask());
		System.out.println(hookThread.isDaemon());
		hookThread.setDaemon(true);
		Runtime.getRuntime().addShutdownHook(hookThread);
	}
}
