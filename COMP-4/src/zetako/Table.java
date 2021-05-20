package zetako;

import java.util.*;

public class Table<K1, K2, V> {
    private Map<Pair<K1, K2>, V> mappingTable;
	
    public Table() {
        mappingTable = new HashMap<Pair<K1, K2>, V>();
    }

    public void put(K1 key1, K2 key2, V value) {
        mappingTable.put(new Pair<K1, K2>(key1, key2), value);
    }
    public V get(K1 key1, K2 key2) {
        // System.out.println("get (%s, %s)".formatted(key1, key2));
        return mappingTable.get(new Pair<K1, K2>(key1, key2));
    }
}