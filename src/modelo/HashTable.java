package modelo;

import java.util.LinkedList;

public class HashTable<K, V> {
    private static final int DEFAULT_CAPACITY = 101;
    private LinkedList<Entry<K, V>>[] buckets;
    private int size;

    private static class Entry<K, V> {
        K key;
        V value;
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = (LinkedList<Entry<K, V>>[]) new LinkedList[DEFAULT_CAPACITY];
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    public void put(K key, V value) {
        int index = hash(key);
        if (buckets[index] == null) buckets[index] = new LinkedList<>();
        for (Entry<K, V> e : buckets[index]) {
            if (e.key.equals(key)) {
                e.value = value;
                return;
            }
        }
        buckets[index].add(new Entry<>(key, value));
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        if (buckets[index] == null) return null;
        for (Entry<K, V> e : buckets[index]) {
            if (e.key.equals(key)) return e.value;
        }
        return null;
    }

    public boolean containsKey(K key) { return get(key) != null; }

    public V remove(K key) {
        int index = hash(key);
        if (buckets[index] == null) return null;
        for (Entry<K, V> e : buckets[index]) {
            if (e.key.equals(key)) {
                V old = e.value;
                buckets[index].remove(e);
                size--;
                return old;
            }
        }
        return null;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public Iterable<K> keys() {
        LinkedList<K> keys = new LinkedList<>();
        for (LinkedList<Entry<K, V>> bucket : buckets) {
            if (bucket != null) {
                for (Entry<K, V> e : bucket) keys.add(e.key);
            }
        }
        return keys;
    }
}
