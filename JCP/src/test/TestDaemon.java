package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestDaemon {

	private static final Map<String, String> cache = new HashMap<String, String>();

//	@Test
//	public void test() {
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println(cache.size());
			}
		}));
		// start a few daemon threads that do some house-cleaning work.
		for (int i = 0; i < 10; i++) {
			final int count = i;
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						cache.remove(String.valueOf(count));
					}

				}

			});
			t.setName("hello " + i);
//			t.setDaemon(true);
			t.start();
		}

		for (int i = 0; i < 10; i++) {
			cache.put(String.valueOf(i), String.valueOf(i));
		}
	}
}
