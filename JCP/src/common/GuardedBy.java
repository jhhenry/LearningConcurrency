package common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface GuardedBy {
	public String value();
}
