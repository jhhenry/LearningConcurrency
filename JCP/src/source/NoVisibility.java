package source;

/**
 * Sample program in 3.1 of JCP.
 * NoVisibility could loop forever because the value of ready might never become visible to the reader thread. 
 * Even more strangely, NoVisibility could print zero because the write to ready might be made visible to the 
 * reader thread before the write to number, a phenomenon known as reordering. There is no guarantee that 
 * operations in one thread will be performed in the order given by the program, 
 * as long as the reordering is not detectable from within that threadeven 
 * if the reordering is apparent to other threads.
 * [1] When the main thread writes first to number and then to done without synchronization, the reader thread 
 * could see those writes happen in the opposite orderor not at all.
 * [1] This may seem like a broken design, but it is meant to allow JVMs to take full advantage of the performance
 *  of modern multiprocessor hardware. For example, in the absence of synchronization, the Java Memory Model permits
 * the compiler to reorder operations and cache values in registers, and permits CPUs to reorder operations 
 * and cache values in processor-specific caches. For more details, see Chapter 16.
 * @author Henry
 *
 */
public class NoVisibility {
    private static boolean ready;
    private static int number;

    private static class ReaderThread extends Thread {
        public void run() {
            while (!ready)
                Thread.yield();
            System.out.println(number);
        }
    }

    public static void main(String[] args) {
    	for (int i = 0; i < 100; i++) {
    		new ReaderThread().start();
    	}
        number = 42;
        ready = true;
    }
}

