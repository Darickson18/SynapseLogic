package modelo;

import java.util.LinkedList;

public class Map<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private LinkedList<Entry<K, V>>[] buckets;
    private int size;
    private static final int DEFAULT_CAPACITY = 101;
    
    @SuppressWarnings("unchecked")
    public Map() {
        buckets = (LinkedList<Entry<K, V>>[]) new LinkedList[DEFAULT_CAPACITY];
        size = 0;
    }
    
    private int hash(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }
    
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }
        
        int index = hash(key);
        if (buckets[index] == null) {
            buckets[index] = new LinkedList<>();
        }
        
        for (Entry<K, V> entry : buckets[index]) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        
        buckets[index].add(new Entry<>(key, value));
        size++;
    }
    
    public V get(K key) {
        if (key == null) {
            return null;
        }
        
        int index = hash(key);
        if (buckets[index] == null) {
            return null;
        }
        
        for (Entry<K, V> entry : buckets[index]) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }
    
    public boolean remove(K key) {
        if (key == null) {
            return false;
        }
        
        int index = hash(key);
        if (buckets[index] == null) {
            return false;
        }
        
        for (Entry<K, V> entry : buckets[index]) {
            if (entry.key.equals(key)) {
                buckets[index].remove(entry);
                size--;
                return true;
            }
        }
        return false;
    }
    
    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
        
        int index = hash(key);
        if (buckets[index] == null) {
            return false;
        }
        
        for (Entry<K, V> entry : buckets[index]) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    public Iterable<K> keys() {
        LinkedList<K> keys = new LinkedList<>();
        for (LinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    keys.add(entry.key);
                }
            }
        }
        return keys;
    }
    
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (K key : keys()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(key).append("=").append(get(key));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
