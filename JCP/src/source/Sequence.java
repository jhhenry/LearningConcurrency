package source;

import common.GuardedBy;
import common.ThreadSafe;

@ThreadSafe
public class Sequence {
	@GuardedBy("this") private int nextValue;

    public synchronized int getNext() {
        return nextValue++;
    }

}
