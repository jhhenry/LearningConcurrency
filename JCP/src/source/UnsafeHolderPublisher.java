package source;

import java.util.concurrent.CountDownLatch;

public class UnsafeHolderPublisher
{
	// Unsafe publication
	public Holder holder;
	private volatile boolean isInit;
	
	public void initialize() {
	    holder = new Holder(42);
	    isInit = true;
	}

	
	
	public static void main(String[] args)
	{
		final UnsafeHolderPublisher p = new UnsafeHolderPublisher();
		final CountDownLatch l = new CountDownLatch(1);
//		while (p.holder == null) {
//			System.out.println("holder is null");
//			//h = p.holder;
//		}
		Runnable r2 = new Runnable() {

			@Override
			public void run()
			{
				/* When countdownLatch is used, NPE is thrown.*/
				try {
					l.await();
				} catch (InterruptedException e) {
				}
				p.initialize();
			}
		}; //initialize the holder field in a separate thread to verify if it can be seen from other threads.
		
		Runnable r = new Runnable() {

			@Override
			public void run()
			{
				try {
					l.await();
				} catch (InterruptedException e) {
				}
				final Holder h = p.holder;
				int i = 0;
				int npeCount = 0;
				while (i++ < 10) {
					if (p.isInit) {
						if (h == null) { //expected to reach here.
							System.out.println("inconsistent view of holder instance: still null but actually initialized in another thread");
						} else {
							System.out.println("consistent view of holder instance");
						}
					}
					if (h != null) {
						h.assertSanity(); //expect to see error here but failed.
					} else {
						npeCount++;
					}
				}
				System.out.println("npeCount: " + npeCount);
			}
			
		}; //get the "holder" instance from the "UnsafeHolderPublisher" object, hoping to see null and inconsistent view of the "n" inside the "holder" instance.
		
		Thread t2 = new Thread(r2);
		t2.start();
		for (int i = 0; i < 100; i++) {
			Thread t = new Thread(r);
			t.start();
		}
		l.countDown();
	}

}
