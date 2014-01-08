package source;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import common.GuardedBy;
import common.ThreadSafe;

/**
 * an implementation of the vehicle tracker using the Java monitor pattern that uses MutablePoint 
 * for representing the vehicle locations.
 * Even though MutablePoint is not thread-safe, the tracker class is. Neither the map nor any of 
 * the mutable points it contains is ever published. When we need to a return vehicle locations to 
 * callers, the appropriate values are copied using either the MutablePoint copy constructor or 
 * deepCopy, which creates a new Map whose values are copies of the keys and values from the old Map
 * @author Administrator
 *
 */
@ThreadSafe
public class MonitorVehicleTracker {
    @GuardedBy("this")
    private final Map<String, MutablePoint> locations;

    public MonitorVehicleTracker(
            Map<String, MutablePoint> locations) {
        this.locations = deepCopy(locations);
    }

    public synchronized Map<String, MutablePoint> getLocations() {
        return deepCopy(locations);
    }

    public synchronized  MutablePoint getLocation(String id) {
        MutablePoint loc = locations.get(id);
        return loc == null ? null : new MutablePoint(loc);
    }

    public synchronized  void setLocation(String id, int x, int y) {
        MutablePoint loc = locations.get(id);
        if (loc == null)
            throw new IllegalArgumentException("No such ID: " + id);
        loc.x = x;
        loc.y = y;
    }

    private static Map<String, MutablePoint> deepCopy(
            Map<String, MutablePoint> m) {
        Map<String, MutablePoint> result =
                new HashMap<String, MutablePoint>();
        for (String id : m.keySet())
            result.put(id, new MutablePoint(m.get(id)));
        return Collections.unmodifiableMap(result);
    }
}


