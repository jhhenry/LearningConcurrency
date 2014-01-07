package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import source.Sequence;
import source.UnsafeSequence;

public class TestChar_1 {

	@Test
	public void testUnsafeSequence() throws InterruptedException, ExecutionException
	{
		ExecutorService tp = Executors.newFixedThreadPool(2);
		final UnsafeSequence testOb = new UnsafeSequence();
		final Set<Integer> collected = new ConcurrentSkipListSet<Integer>();
		final long waitTime = 10000;
		
		
		class Task implements Callable<Object> {
			volatile boolean found = false;
			volatile int result = -1;
			@Override
			public Object call() throws Exception {
				long startTime = System.currentTimeMillis();
				
				//System.out.println("thread start: " + Thread.currentThread().getName());
				while (!found && System.currentTimeMillis() - startTime < waitTime) {
					int next = testOb.getNext();
					if (collected.contains(next)) {
						result = next; 
						found = true;
						break;
					} else {
						collected.add(next);
					}
				}
				
				if (!found) {
					fail("did not get the same value.");
				} else {
					System.out.println("thread exit: " + Thread.currentThread().getName());
				}
				System.out.println("Got result: " + result);
				return result;
			}
			
		};
		
		Callable<Object> t = new Task();
		Future<Object> f1 = tp.submit(t);
		Future<Object> f2 = tp.submit(t);
		Object r1 = f1.get();
		Object r2 = f2.get();
		assertEquals(r1, r2);
	}
	
	@Test
	public void testSafeSequence() throws InterruptedException, ExecutionException
	{
		ExecutorService tp = Executors.newFixedThreadPool(2);
		final Sequence testOb = new Sequence();
		final Set<Integer> collected = new ConcurrentSkipListSet<Integer>();
		final long waitTime = 20000;
		
		
		class Task implements Callable<Object> {
			volatile boolean found = false;
			volatile int result = -1;
			@Override
			public Object call() throws Exception {
				long startTime = System.currentTimeMillis();
				
				//System.out.println("thread start: " + Thread.currentThread().getName());
				int next = -1;
				while (!found && System.currentTimeMillis() - startTime < waitTime && next <= 1000000) {
					next = testOb.getNext();
					if (collected.contains(next)) {
						result = next; 
						found = true;
						break;
					} else {
						collected.add(next);
					}
				}
				
				if (found) {
					fail("did get the same value.");
				} else {
					System.out.println("thread exit: " + Thread.currentThread().getName());
				}
				System.out.println("Got result: " + result);
				return result;
			}
			
		};
		
		Callable<Object> t = new Task();
		Future<Object> f1 = tp.submit(t);
		Future<Object> f2 = tp.submit(t);
		Object r1 = f1.get();
		Object r2 = f2.get();
		assertEquals(r1, -1);
		assertEquals(r2, -1);
	}

}
