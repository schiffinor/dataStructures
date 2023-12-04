import java.util.*;

/**
 * Represents a custom implementation of a HashMap.
 * <p>
 * This HashMap allows for the storage and retrieval of key-value pairs. It uses a custom
 * structure combining doubly-linked chaining and a TreeSet structure for more efficient
 * data storage and retrieval operations.
 * <p>
 * The implementation provides various functionalities such as adding, removing, and retrieving
 * entries, as well as iterators for traversing the keys, values, and entries in ascending or
 * descending order. It also includes methods to calculate the size, check for emptiness, and
 * perform operations on the underlying data structure. The implementation also has a lot of
 * extra functionality and features to bring it more in line with Java's implementations.
 * <p>
 * The implementation is not thread-safe, and care should be taken when used in a concurrent
 * environment. It supports generics for flexible key and value types.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class HashMap<K, V> implements CustomMap<K, V>, Iterable<HashMap.Node<K, V>> {

    private final int initialCapacity;
    private int size;
    private BaseArray<K, V> nodes;
    private double maxLoadFactor;
    private KeySet<K> keySet;


    /**
     * Constructs an empty HashMap with an initial capacity of 16 and a default load factor of 0.75.
     * <p>
     * This constructor initializes a new HashMap with a default capacity of 16 and a load factor of 0.75.
     * The load factor determines when the underlying array is resized to maintain efficiency.
     * </p>
     *
     * @since 1.0
     */
    public HashMap() {
        this(16);
    }

    /**
     * Constructs an empty HashMap with the specified initial capacity and a default load factor of 0.75.
     * <p>
     * This constructor allows the user to specify the initial capacity of the HashMap while using the default
     * load factor of 0.75. The initial capacity should be a positive power of 2; otherwise, an
     * IllegalArgumentException is thrown.
     * </p>
     *
     * @param capacity the initial capacity of the HashMap, must be a power of 2
     * @throws IllegalArgumentException if the specified capacity is not a power of 2
     * @since 1.0
     */
    public HashMap(int capacity) {
        this(capacity, .75);
    }

    /**
     * Constructs an empty HashMap with the specified initial capacity and load factor.
     * <p>
     * This constructor allows the user to specify both the initial capacity and the load factor of the HashMap.
     * The initial capacity should be a positive power of 2; otherwise, an IllegalArgumentException is thrown.
     * </p>
     *
     * @param capacity   the initial capacity of the HashMap, must be a power of 2
     * @param loadFactor the load factor, determining when the underlying array is resized
     * @throws IllegalArgumentException if the specified capacity is not a power of 2
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public HashMap(int capacity, double loadFactor) {
        if (capacity % 2 != 0) throw new IllegalArgumentException("Capacity must be power of 2, BANNED!");
        this.initialCapacity = capacity;
        this.maxLoadFactor = loadFactor;
        this.nodes = (BaseArray<K, V>) new BaseArray();
    }

    public static void main(String[] args) {
        HashMap<String, Integer> words = new HashMap<>();
        words.put("ten", 10);
        words.put("five", 5);
        words.put("three", 3);
        words.put("seven", 10);
        words.put("four", 5);
        words.put("eight", 3);
        words.put("twelve", 10);
        words.put("ninety", 5);
        words.put("zedd", 3);
        words.put("ter", 7);
        words.put("teq", 7);
        words.put("terra", 7);
        System.out.println(words);
        System.out.println(words.getFirst());
        System.out.println(words.getLast());
        System.out.println(words.maxDepth());
        words.put("teb", 10);
        words.put("fivbe", 5);
        words.put("threbe", 3);
        words.put("sevenb", 10);
        words.put("fourb", 5);
        words.put("eighbt", 3);
        words.put("twelbve", 10);
        words.put("ninetby", 5);
        words.put("zeddb", 3);
        words.put("terb", 7);
        words.put("teqb", 7);
        words.put("terbra", 7);
        System.out.println(words);
        System.out.println(words.getFirst());
        System.out.println(words.getLast());
        System.out.println(words.maxDepth());
        words.remove("seven");
        System.out.println(words);
        System.out.println(words.keySet());
        System.out.println(words.entrySet());
        System.out.println(words.maxDepth());
        words.remove("teb");
        words.remove("fivbe");
        words.remove("threbe");
        words.remove("sevenb");
        words.remove("fourb");
        words.remove("eighbt");
        words.remove("twelbve");
        words.remove("ninetby");
        words.remove("zeddb");
        words.remove("terb");
        words.remove("teqb");
        words.remove("terbra");
        System.out.println(words);
        System.out.println(words.keySet());
        System.out.println(words.entrySet());
        System.out.println(words.maxDepth());
        System.out.println(words.containsKey("ter"));
    }

    /**
     * Returns the number of key-value mappings in this HashMap.
     *
     * @return the number of key-value mappings in this HashMap
     * @since 1.0
     */
    public int size() {
        return size;
    }

    /**
     * Checks if this HashMap contains no key-value mappings.
     *
     * @return {@code true} if this HashMap is empty, {@code false} otherwise
     * @since 1.0
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns a string representation of this HashMap.
     *
     * @return a string representation of this HashMap
     * @since 1.0
     */
    public String toString() {
        return entrySet().toString();
    }

    /**
     * Removes all key-value mappings from this HashMap.
     *
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void clear() {
        this.nodes = (BaseArray<K, V>) new BaseArray();
    }

    /**
     * Fetches the node associated with the specified key in the HashMap.
     *
     * @param key the key whose associated node is to be fetched
     * @return the node associated with the specified key, or null if the key is not present
     * @since 1.0
     */
    private Node<K, V> nodeFetch(K key) {
        BaseArray<K, V> nodeArray = getNodes();
        Node<K, V> currNode = nodeArray.getNodes()[hash(key)];
        if (currNode == null) return null;
        if (currNode.getKey().equals(key)) return currNode;
        Node<K, V> nextNode = currNode.getNext();
        while (nextNode != null && !currNode.getKey().equals(key)) {
            currNode = nextNode;
            nextNode = currNode.getNext();
        }
        if (!currNode.getKey().equals(key)) return null;
        return currNode;
    }

    /**
     * Returns a Set view of the keys contained in this HashMap.
     *
     * @return a Set view of the keys contained in this HashMap
     * @since 1.0
     */
    public Set<K> keySet() {
        if (this.keySet == null) keySet = new KeySet<>(this);
        return keySet;
    }

    /**
     * Returns a list of all values in the HashMap.
     *
     * @return an ArrayList containing all values in the HashMap
     * @since 1.0
     */
    public ArrayList<V> values() {
        ArrayList<V> refList = new ArrayList<>();
        keySet().forEach(entry -> refList.add(get(entry)));
        return refList;
    }

    /**
     * Returns a list of all entries (nodes) in the HashMap.
     *
     * @return an ArrayList containing all entries (nodes) in the HashMap
     * @since 1.0
     */
    public ArrayList<Node<K, V>> entrySet() {
        ArrayList<Node<K, V>> refList = new ArrayList<>();
        keySet().forEach(entry -> refList.add(nodeFetch(entry)));
        return refList;
    }


    /**
     * Returns the entry set as a Set of nodes.
     *
     * @return the entry set as a Set of nodes
     * @since 1.0
     */
    public Set<Node<K, V>> entrySetAsSet() {
        HashSet<Node<K, V>> refSet = new HashSet<>();
        keySet().forEach(entry -> refSet.add(nodeFetch(entry)));
        return refSet;
    }


    /**
     * Retrieves the value associated with the specified key in this HashMap.
     * <p>
     * Returns the value to which the specified key is mapped, or {@code null} if this
     * HashMap contains no mapping for the key. The key comparison is performed using
     * the {@code equals} method.
     * </p>
     *
     * @param key the key whose associated value is to be retrieved
     * @return the value to which the specified key is mapped, or {@code null} if no mapping exists
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Node<K, V> node = nodeFetch((K) key);
        return (node == null) ? null : node.getValue();
    }

    /**
     * Checks if this HashMap contains a mapping for the specified key.
     * <p>
     * Returns {@code true} if this HashMap contains a mapping for the specified key,
     * otherwise {@code false}. The key comparison is performed using the {@code equals}
     * method.
     * </p>
     *
     * @param key the key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key, {@code false} otherwise
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return nodeFetch((K) key) != null;
    }

    /**
     * Checks if this HashMap contains the specified value.
     * <p>
     * Returns {@code false} since the implementation does not currently support value
     * comparison for presence in the map.
     * </p>
     *
     * @param value value whose presence in this map is to be tested
     * @return always returns {@code false}
     * @since 1.0
     */
    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    /**
     * Removes the mapping for the specified key from this HashMap, if present.
     * <p>
     * Returns the value to which the specified key was mapped, or {@code null} if this
     * HashMap contains no mapping for the key. The key comparison is performed using
     * the {@code equals} method.
     * </p>
     * <p>
     * If the size of the HashMap falls below a certain threshold, the capacity may be reduced
     * to optimize space usage.
     * </p>
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the value to which the specified key was mapped, or {@code null} if no mapping exists
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        BaseArray<K, V> refNodes = getNodes();
        V returnValue = refNodes.remove(nodeFetch((K) key));
        setSize(refNodes.getSize());
        if (size() >= getInitialCapacity()) {
            if (size() < (getMaxLoadFactor() * capacity() / 4)) {
                getNodes().halveCapacity();
            }
        }
        return returnValue;
    }

    /**
     * Associates the specified value with the specified key in this HashMap.
     * <p>
     * If the map previously contained a mapping for the key, the old value is replaced
     * by the specified value. The key comparison is performed using the {@code equals} method.
     * </p>
     * <p>
     * If the size of the HashMap exceeds a certain threshold, the capacity may be increased
     * to optimize time complexity.
     * </p>
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no mapping
     * @since 1.0
     */
    public V put(K key, V value) {
        Node<K, V> newNode = new Node<>(key, value);
        BaseArray<K, V> refNodes = getNodes();
        V returnValue = refNodes.add(newNode);
        setSize(refNodes.getSize());
        if (size() >= getInitialCapacity()) {
            if (size() > getMaxLoadFactor() * capacity()) {
                getNodes().doubleCapacity();
            }
        }
        return returnValue;
    }

    /**
     * @param m mappings to be stored in this map
     */
    @Override
    public void putAll(CustomMap<? extends K, ? extends V> m) {
        m.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
    }

    /**
     * Calculates and returns the maximum depth of the HashMap by checking the max depth for each node
     * in the base BaseArray node array.
     *
     * @return the maximum depth of the HashMap
     * @since 1.0
     */
    public int maxDepth() {
        BaseArray<K, V> refNodes = getNodes();
        TreeSet<Integer> refSet = refNodes.getFilledIndices();
        int maxDepth = 0;
        for (Integer currIndex : refSet) {
            int currDepth = refNodes.getMaxDepth(currIndex);
            maxDepth = Math.max(currDepth, maxDepth);
        }
        return maxDepth;
    }

    /**
     * Calculates and returns the depth of a specific node in the hashmap, starting from the given node.
     *
     * @param node the starting node for calculating the depth
     * @return the depth from the given node
     * @since 1.0
     */
    public int nodeDepth(Node<K, V> node) {
        Objects.requireNonNull(node, "node is null");
        Node<K, V> currNode = node;
        int depth = 1;
        Node<K, V> prevNode = currNode.getPrev();
        while (prevNode != null) {
            depth++;
            currNode = prevNode;
            prevNode = currNode.getPrev();
        }
        return depth;
    }

    /**
     * Returns the current capacity of the underlying node array in this HashMap.
     *
     * @return the current capacity of the node array
     * @since 1.0
     */
    public int capacity() {
        return getNodes().getCapacity();
    }


    /**
     * Computes the hash code of the specified key using the underlying node array's hash function.
     *
     * @param key the key whose hash code is to be computed
     * @return the hash code of the specified key
     * @since 1.0
     */
    private int hash(K key) {
        return getNodes().hash(key);
    }

    /**
     * Returns the first node in the HashMap, i.e., the node with the smallest index.
     *
     * @return the first node in the HashMap
     * @since 1.0
     */
    private Node<K, V> getFirst() {
        return getNodes().getFirst();
    }

    /**
     * Returns the last node in the HashMap, i.e., the node with the largest index.
     *
     * @return the last node in the HashMap
     * @since 1.0
     */
    private Node<K, V> getLast() {
        return getNodes().getLast();
    }

    /**
     * Returns the previous node of the specified node in the HashMap.
     *
     * @param tempNode the node whose previous node is to be retrieved
     * @return the previous node of the specified node
     * @since 1.0
     */
    private Node<K, V> getPrev(Node<K, V> tempNode) {
        return getNodes().getPrev(tempNode);
    }

    /**
     * Returns the next node of the specified node in the HashMap.
     *
     * @param tempNode the node whose next node is to be retrieved
     * @return the next node of the specified node
     * @since 1.0
     */
    private Node<K, V> getNext(Node<K, V> tempNode) {
        return getNodes().getNext(tempNode);
    }

    /**
     * Returns the underlying node array (BaseArray) of this HashMap.
     *
     * @return the underlying node array
     * @since 1.0
     */
    public BaseArray<K, V> getNodes() {
        return nodes;
    }

    /**
     * Sets the underlying node array of this HashMap to the specified node array.
     *
     * @param nodes the new node array to be set
     * @since 1.0
     */
    public void setNodes(BaseArray<K, V> nodes) {
        this.nodes = nodes;
    }

    /**
     * Sets the size of this HashMap to the specified size.
     *
     * @param size the new size of the HashMap
     * @since 1.0
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Returns the maximum load factor allowed for this HashMap.
     *
     * @return the maximum load factor
     * @since 1.0
     */
    public double getMaxLoadFactor() {
        return maxLoadFactor;
    }

    /**
     * Sets the maximum load factor allowed for this HashMap to the specified value.
     *
     * @param maxLoadFactor the new maximum load factor
     * @since 1.0
     */
    public void setMaxLoadFactor(double maxLoadFactor) {
        this.maxLoadFactor = maxLoadFactor;
    }

    /**
     * Returns an iterator over the entries (nodes) of this HashMap.
     *
     * @return an iterator over the entries of the HashMap
     * @since 1.0
     */
    @Override
    public Iterator<Node<K, V>> iterator() {
        return new EntryIterator(getFirst());
    }

    /**
     * Returns an iterator over the keys of the HashMap in ascending order.
     *
     * @return an iterator over the keys in ascending order
     * @since 1.0
     */
    Iterator<K> keyIterator() {
        return new KeyIterator(getFirst());
    }

    /**
     * Returns an iterator over the keys of the HashMap in descending order.
     *
     * @return an iterator over the keys in descending order
     * @since 1.0
     */
    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(getLast());
    }

    /**
     * Returns the initial capacity of the HashMap.
     *
     * @return initial capacity of the HashMap
     * @since 1.0
     */
    public int getInitialCapacity() {
        return initialCapacity;
    }

    /**
     * Represents a node in the binary search tree used by the HashMap.
     *
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of mapped values
     * @since 1.0
     */
    public static final class Node<K, V> implements CustomMap.Entry<K, V> {

        private final K key;
        private V value;
        private Node<K, V> next;
        private Node<K, V> prev;

        /**
         * Constructs a new node with the specified key and value.
         *
         * @param key   the key to be associated with the node
         * @param value the value to be associated with the key
         */
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns the key associated with this node.
         *
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the value associated with the key of this node.
         *
         * @return the value associated with the key
         */
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value currently associated with the key with the given value.
         *
         * @param value the new value to be associated with the key
         * @return the value associated with the key before this method was called
         */
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        /**
         * Compares this node with another object for equality.
         *
         * @param obj the object to be compared for equality
         * @return {@code true} if the objects are equal, otherwise {@code false}
         */
        public boolean equals(Object obj) {
            return (obj instanceof Node<?, ?> entry && key.equals(entry.getKey()) && value.equals(entry.getValue()));
        }

        /**
         * Returns the next node of this node.
         *
         * @return the next node
         */
        public Node<K, V> getNext() {
            return next;
        }

        /**
         * Sets the next node of this node.
         *
         * @param next the new next node
         */
        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        /**
         * Returns the previous node of this node.
         *
         * @return the previous node
         */
        public Node<K, V> getPrev() {
            return prev;
        }

        /**
         * Sets the next node of this node.
         *
         * @param prev the new prev node
         */
        public void setPrev(Node<K, V> prev) {
            this.prev = prev;
        }

        /**
         * Returns a string representation of this node.
         *
         * @return a string representation of this node
         */
        public String toString() {
            return "<" + getKey().toString() + " -> " + getValue().toString() + ">";
        }
    }

    /**
     * A resizable array-based storage structure to hold nodes in the HashMap.
     * This class manages the capacity, indices, and operations on the nodes.
     *
     * @param <K> the type of keys maintained by this array
     * @param <V> the type of values mapped by this array
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public static class BaseArray<K, V> {

        private int capacity;
        private Node<K, V>[] nodes;
        private TreeSet<Integer> filledIndices;
        private int size;


        /**
         * Constructs a BaseArray with the default initial capacity of 16.
         *
         * @since 1.0
         */
        BaseArray() {
            this(16);
        }

        /**
         * Constructs a BaseArray with the specified initial capacity.
         *
         * @param capacity the initial capacity of the BaseArray
         * @since 1.0
         */
        BaseArray(int capacity) {
            this.capacity = capacity;
            this.size = 0;
            nodes = (Node<K, V>[]) new Node[capacity];
            filledIndices = new TreeSet<>();
        }

        /**
         * Returns the current capacity of the BaseArray.
         *
         * @return the current capacity of the BaseArray
         * @since 1.0
         */
        public int getCapacity() {
            return capacity;
        }

        /**
         * Sets the capacity of the BaseArray to the specified value.
         *
         * @param capacity the new capacity of the BaseArray
         * @since 1.0
         */
        private void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        /**
         * Computes the hash code of the specified key using the current capacity of the BaseArray.
         *
         * @param key the key whose hash code is to be computed
         * @return the hash code of the specified key
         * @since 1.0
         */
        private int hash(K key) {
            return Math.abs(key.hashCode() % getCapacity());
        }

        /**
         * Computes the hash code of the specified key using the specified modulus.
         *
         * @param key     the key whose hash code is to be computed
         * @param modulus the modulus to be used in hash computation
         * @return the hash code of the specified key
         * @since 1.0
         */
        private int hash(K key, int modulus) {
            return Math.abs(key.hashCode() % modulus);
        }

        /**
         * Returns the first node in the BaseArray, i.e., the node with the smallest index.
         *
         * @return the first node in the BaseArray
         * @since 1.0
         */
        private Node<K, V> getFirst() {
            TreeSet<Integer> filledIndices = getFilledIndices();
            if (filledIndices == null) return null;
            if (filledIndices.isEmpty()) return null;
            int minIndex = filledIndices.getFirst();
            return getNodes()[minIndex];
        }

        /**
         * Returns the last node in the BaseArray, i.e., the node with the largest index and last in its chain.
         *
         * @return the last node in the BaseArray
         * @since 1.0
         */
        private Node<K, V> getLast() {
            TreeSet<Integer> filledIndices = getFilledIndices();
            if (filledIndices == null) return null;
            if (filledIndices.isEmpty()) return null;
            int maxIndex = filledIndices.getLast();
            Node<K, V> returnNode = getNodes()[maxIndex];
            Node<K, V> nextNode = returnNode.getNext();
            while (nextNode != null) {
                returnNode = nextNode;
                nextNode = returnNode.getNext();
            }
            return returnNode;
        }

        /**
         * Returns the previous node of the specified node in the BaseArray.
         *
         * @param tempNode the node whose previous node is to be retrieved
         * @return the previous node of the specified node
         * @since 1.0
         */
        private Node<K, V> getPrev(Node<K, V> tempNode) {
            if (tempNode == null) return null;
            Node<K, V> prevNode = tempNode.getPrev();
            if (prevNode != null) return prevNode;
            TreeSet<Integer> tempSet = getFilledIndices();
            if (hash(tempNode.getKey()) == tempSet.getFirst()) return null;
            Integer previousIndex = tempSet.lower(hash(tempNode.getKey()));
            if (previousIndex == null) return null;
            Node<K, V> returnNode = getNodes()[previousIndex];
            prevNode = returnNode.getNext();
            while (prevNode != null) {
                returnNode = prevNode;
                prevNode = returnNode.getNext();
            }
            return returnNode;
        }

        /**
         * Returns the next node of the specified node in the BaseArray.
         *
         * @param tempNode the node whose next node is to be retrieved
         * @return the next node of the specified node
         * @since 1.0
         */
        private Node<K, V> getNext(Node<K, V> tempNode) {
            if (tempNode == null) return null;
            Node<K, V> nextNode = tempNode.getNext();
            if (nextNode != null) return nextNode;
            TreeSet<Integer> tempSet = getFilledIndices();
            if (hash(tempNode.getKey()) == tempSet.getLast()) return null;
            Integer nextIndex = tempSet.higher(hash(tempNode.getKey()));
            if (nextIndex == null) return null;
            return getNodes()[nextIndex];
        }

        /**
         * Doubles the current capacity of the BaseArray.
         *
         * @since 1.0
         */
        public void doubleCapacity() {
            int newCapacity = getCapacity() * 2;
            changeCapacity(newCapacity);
        }

        /**
         * Halves the current capacity of the BaseArray.
         *
         * @since 1.0
         */
        public void halveCapacity() {
            int newCapacity = getCapacity() / 2;
            changeCapacity(newCapacity);
        }

        /**
         * Changes the capacity of the BaseArray to the specified new capacity and rehashes everything.
         * <p>
         * This method performs a resize operation on the underlying array, adjusting its capacity
         * and redistributing existing nodes based on the new hash calculations.
         *
         * @param newCapacity the new capacity of the BaseArray
         * @since 1.0
         */
        private void changeCapacity(int newCapacity) {
            // Create a temporary array and set to hold new nodes and indices
            Node<K, V>[] tempArray = new Node[newCapacity];
            TreeSet<Integer> tempSet = new TreeSet<>();
            size = 0;

            // Traverse the existing nodes and rehash them into the new array
            Node<K, V> refNode = getFirst();
            Node<K, V> prevNode = null;
            while (refNode != null) {
                Node<K, V> tempNode = new Node<>(refNode.getKey(), refNode.getValue());
                add(tempNode, tempArray, tempSet, newCapacity);
                prevNode = refNode;
                refNode = getNext(refNode);
            }

            // Update the BaseArray properties with the newly resized array and indices
            filledIndices = tempSet;
            nodes = tempArray;
            setCapacity(newCapacity);
        }

        /**
         * Adds a node to the BaseArray, updating the nodes, indices, and size.
         * <p>
         * If a node with the same key already exists, its value is updated.
         *
         * @param node the node to be added
         * @return the previous value associated with the specified key, or null if there was no mapping for the key
         * @since 1.0
         */
        public V add(Node<K, V> node) {
            return add(node, getNodes(), getFilledIndices());
        }

        /**
         * Adds a node to the node list while updating the specified filled index set,
         * with a hash function based on current capacity.
         *
         * @param node        the node to be added
         * @param nodeList    the array of nodes to which the new node is added
         * @param nodeIndices the set of indices representing filled positions in the nodeList
         * @return the previous value associated with the specified key, or null if there was no mapping for the key
         * @since 1.0
         */
        public V add(Node<K, V> node, Node<K, V>[] nodeList, TreeSet<Integer> nodeIndices) {
            return add(node, nodeList, nodeIndices, getCapacity());
        }

        /**
         * Adds a node to the BaseArray with the specified nodes, indices, and hash modulus.
         * If a node with the same key already exists, its value is updated.
         *
         * @param node        the node to be added
         * @param nodeList    the array of nodes to which the new node is added
         * @param nodeIndices the set of indices representing filled positions in the nodeList
         * @param hashModulus the modulus used in hash computation
         * @return the previous value associated with the specified key, or null if there was no mapping for the key
         * @since 1.0
         */
        public V add(Node<K, V> node, Node<K, V>[] nodeList, TreeSet<Integer> nodeIndices, int hashModulus) {
            if (node == null) return null;
            if (node.getKey() == null) return null;

            // Calculate the index for the new node based on hashModulus
            K key = node.getKey();
            int nodeIndex = hash(key, hashModulus);
            Node<K, V> currNode = nodeList[nodeIndex];

            // If the index is empty, add the node directly
            if (currNode == null) {
                nodeList[nodeIndex] = node;
                nodeIndices.add(nodeIndex);
                size++;
                return null;
            }

            // If a node with the same key exists, update its value
            Node<K, V> prevNode = null;
            while (currNode != null) {
                if (currNode.getKey().equals(key)) {
                    V prevValue = currNode.getValue();
                    currNode.setValue(node.getValue());
                    return prevValue;
                }
                prevNode = currNode;
                currNode = currNode.getNext();
            }

            // If no node with the same key is found, add the node to the end of the linked list
            prevNode.setNext(node);
            node.setPrev(prevNode);
            size++;
            return null;
        }

        /**
         * Removes the specified node from the BaseArray, updating nodes, indices, and size.
         *
         * @param node the node to be removed
         * @return the value associated with the specified key, or null if there was no mapping for the key
         * @since 1.0
         */
        public V remove(Node<K, V> node) {
            if (node == null) return null;
            int nodeIndex = hash(node.getKey());
            Node<K, V> prevNode = node.getPrev();
            Node<K, V> nextNode = node.getNext();

            // Update the links in the linked list
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                // If the removed node was the first in the linked list, update the array
                getNodes()[nodeIndex] = nextNode;
            }

            // If the index becomes empty, remove it from the set of filled indices
            if (getNodes()[nodeIndex] == null) {
                getFilledIndices().remove(nodeIndex);
            }

            size--;
            return node.getValue();
        }

        /**
         * Computes the maximum depth of the nodes at the specified index in the BaseArray.
         *
         * @param index the index for which to compute the maximum depth
         * @return the maximum depth of the nodes at the specified index
         * @since 1.0
         */
        public int getMaxDepth(int index) {
            Node<K, V> refNode = getNodes()[index];
            if (refNode == null) return 0;
            return nodeDepth(refNode);
        }

        /**
         * Returns the set of indices representing filled positions in the BaseArray.
         *
         * @return the set of filled indices in the BaseArray
         * @since 1.0
         */
        public TreeSet<Integer> getFilledIndices() {
            return filledIndices;
        }

        /**
         * Returns the array of nodes in the BaseArray.
         *
         * @return the array of nodes in the BaseArray
         * @since 1.0
         */
        public Node<K, V>[] getNodes() {
            return nodes;
        }

        /**
         * Computes the depth of the specified node in the BaseArray.
         *
         * @param refNode the node for which to compute the depth
         * @return the depth of the specified node
         * @throws NullPointerException if refNode is null
         * @since 1.0
         */
        public int nodeDepth(Node<K, V> refNode) {
            Objects.requireNonNull(refNode, "refNode is null");
            Node<K, V> currNode = refNode;
            int depth = 1;
            Node<K, V> nextNode = currNode.getNext();
            while (nextNode != null) {
                depth++;
                currNode = nextNode;
                nextNode = currNode.getNext();
            }
            return depth;
        }

        /**
         * Returns the current size of the BaseArray, i.e., the number of nodes.
         *
         * @return the current size of the BaseArray
         * @since 1.0
         */
        public int getSize() {
            return size;
        }
    }

    /**
     * Represents a set view of the keys contained in a HashMap.
     *
     * @param <T> the type of keys in the set
     * @since 1.0
     */
    public static class KeySet<T> extends AbstractSet<T> {

        private final HashMap<T, ?> refMap;

        /**
         * Constructs a new KeySet with a reference to the specified HashMap.
         *
         * @param map the HashMap to be associated with this KeySet
         */
        KeySet(HashMap<T, ?> map) {
            refMap = map;
        }

        /**
         * Returns an iterator over the keys in the HashMap.
         *
         * @return an iterator over the keys
         */
        public Iterator<T> iterator() {
            return refMap.keyIterator();
        }

        /**
         * Returns a descending iterator over the keys in the HashMap.
         *
         * @return a descending iterator over the keys
         */
        public Iterator<T> descendingIterator() {
            return refMap.descendingKeyIterator();
        }

        /**
         * Returns the number of keys in the HashMap.
         *
         * @return the number of keys
         */
        public int size() {
            return refMap.size();
        }

        /**
         * Checks if the KeySet is empty.
         *
         * @return {@code true} if the KeySet is empty, otherwise {@code false}
         */
        public boolean isEmpty() {
            return refMap.isEmpty();
        }

        /**
         * Checks if the KeySet contains a specified key.
         *
         * @param obj the key to be checked for containment
         * @return {@code true} if the KeySet contains the specified key, otherwise {@code false}
         */
        public boolean contains(Object obj) {
            return refMap.containsKey(obj);
        }

        /**
         * Removes all keys from the HashMap associated with this KeySet.
         */
        public void clear() {
            refMap.clear();
        }
    }

    abstract class BaseIterator<T> implements Iterator<T> {
        Node<K, V> currentNext;
        Node<K, V> previousFetch;

        /**
         * Constructs a new BaseIterator starting at the given node.
         *
         * @param first the starting node for the iterator
         */
        BaseIterator(Node<K, V> first) {
            previousFetch = null;
            currentNext = first;
        }

        /**
         * Checks if there is a next element in the iteration.
         *
         * @return {@code true} if there is a next element, otherwise {@code false}
         */
        public final boolean hasNext() {
            return currentNext != null;
        }

        /**
         * Returns the next entry in the iteration.
         *
         * @return the next entry in the iteration
         */
        final Node<K, V> nextEntry() {
            Node<K, V> tempNode = currentNext;
            if (tempNode == null)
                throw new NoSuchElementException("This node is null/D.N.E.");
            currentNext = getNext(tempNode);
            previousFetch = tempNode;
            return previousFetch;
        }

        /**
         * Returns the previous entry in the iteration.
         *
         * @return the previous entry in the iteration
         */
        final Node<K, V> prevEntry() {
            Node<K, V> tempNode = currentNext;
            if (tempNode == null)
                throw new NoSuchElementException("This node is null/D.N.E.");
            currentNext = getPrev(tempNode);
            previousFetch = tempNode;
            return previousFetch;
        }
    }


    /**
     * Iterator over the entries of a HashMap.
     */
    final class EntryIterator extends BaseIterator<Node<K, V>> {
        EntryIterator(Node<K, V> first) {
            super(first);
        }

        public Node<K, V> next() {
            return nextEntry();
        }
    }

    /**
     * Iterator over the values of a HashMap.
     */
    final class ValueIterator extends BaseIterator<V> {
        ValueIterator(Node<K, V> first) {
            super(first);
        }

        public V next() {
            return nextEntry().getValue();
        }
    }

    /**
     * Iterator over the keys of a HashMap.
     */
    final class KeyIterator extends BaseIterator<K> {
        KeyIterator(Node<K, V> first) {
            super(first);
        }

        public K next() {
            return nextEntry().getKey();
        }
    }

    /**
     * Iterator over the keys of a HashMap in descending order.
     */
    final class DescendingKeyIterator extends BaseIterator<K> {
        DescendingKeyIterator(Node<K, V> first) {
            super(first);
        }

        public K next() {
            return prevEntry().key;
        }
    }
}
