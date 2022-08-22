package org.openobservatory.ooniprobe.common;

import java.util.List;

public class ListUtility {
    public static int sum(List<Integer> list) {
        int sum = 0;
        for (int i : list) {
            sum = sum + i;
        }
        return sum;
    }
}
