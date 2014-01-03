package source;

import common.NotThreadSafe;

@NotThreadSafe
public class UnsafeSequence {
	private int value;
	
	public int getNext() {
		return value++;
	}
}
