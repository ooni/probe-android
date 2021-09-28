package org.openobservatory.ooniprobe.common;

import java.util.HashMap;

public class MapUtility {
    //Class replacement for HashMap's function getOrDefault that doesn't work in android < N
    public static Object getOrDefaultCompat(HashMap map, Object key, Object defaultValue) {
        return ((map.get(key) != null) ? map.get(key) : defaultValue);
    }
}
