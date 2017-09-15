package gd.sender;

import gd.tailer.state.State;

import java.util.*;
import java.util.stream.Collectors;

public class OffsetMapping {
    private final HashMap<Long, Long> mapping = new HashMap<>();

    public State getStateByTotalOffset(long offset) {
        List<Map.Entry<Long, Long>> collect = mapping.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Map.Entry<Long, Long> prev = null;
        for (Map.Entry<Long, Long> entry : collect) {
            if (entry.getValue() >= offset) {
                if (prev != null) {
                    return new State(offset - prev.getValue(), entry.getKey());
                } else {
                    return new State(offset, entry.getKey());
                }
            }
            prev = entry;
        }
        //can't determine which iNode corresponds to this offset
        return null;
    }

    public void put(long inode, long offset) {
        mapping.put(inode, offset);
    }

    @Override
    public String toString() {
        return mapping.toString();
    }
}
