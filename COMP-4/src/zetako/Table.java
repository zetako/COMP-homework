package zetako;

import java.util.*;

/**
 * Implement of table (2-dim to 1-dim mapping)
 * @author zetako
 * @version 1.0
 */
public class Table<K1, K2, V> {
    /**
     * Datas storage as Map
     */
    private Map<Pair<K1, K2>, V> mappingTable;
	
    /**
     * Constructor, simply add a empty map as member
     */
    public Table() {
        mappingTable = new HashMap<Pair<K1, K2>, V>();
    }

    /**
     * Put method, add mapping into table
     * @param key1 key1 for map
     * @param key2 key2 for map
     * @param value value
     */
    public void put(K1 key1, K2 key2, V value) {
        mappingTable.put(new Pair<K1, K2>(key1, key2), value);
    }

    /**
     * Get method, get value with pairof key
     * @param key1 key1 for value
     * @param key2 key2 for value
     * @return value get by keys
     */
    public V get(K1 key1, K2 key2) {
        // System.out.println("get (%s, %s)".formatted(key1, key2));
        return mappingTable.get(new Pair<K1, K2>(key1, key2));
    }
}