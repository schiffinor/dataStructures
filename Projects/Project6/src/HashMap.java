import java.util.*;

public class HashMap<K, V> implements CustomMap<K,V>, Iterable<HashMap.Node<K, V>> {

    private int size;
    private BaseArray<K, V> nodes;
    private double maxLoadFactor;
    private final int initialCapacity;
    private KeySet<K> keySet;


    public HashMap() {
        this(16);
    }

    public HashMap(int capacity) {
        this(capacity, .75);
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public HashMap(int capacity, double loadFactor) {
        if (capacity % 2 != 0) throw new IllegalArgumentException("Capacity must be power of 2, BANNED!");
        this.initialCapacity = capacity;
        this.maxLoadFactor = loadFactor;
        this.nodes = (BaseArray<K, V>) new BaseArray();
    }


    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public String toString() {
        return entrySet().toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void clear() {
        this.nodes = (BaseArray<K, V>) new BaseArray();
    }

    /**
     * Fetches the node associated with the specified key in the BSTMap.
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
     * Returns a Set view of the keys contained in this BSTMap.
     *
     * @return a Set view of the keys contained in this BSTMap
     * @since 1.0
     */
    public Set<K> keySet() {
        if (this.keySet == null) keySet = new KeySet<>(this);
        return keySet;
    }

    /**
     * Returns a list of all values in the BSTMap.
     *
     * @return an ArrayList containing all values in the BSTMap
     * @since 1.0
     */
    public ArrayList<V> values() {
        ArrayList<V> refList = new ArrayList<>();
        keySet().forEach(entry -> refList.add(get(entry)));
        return refList;
    }

    /**
     * Returns a list of all entries (nodes) in the BSTMap.
     *
     * @return an ArrayList containing all entries (nodes) in the BSTMap
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

    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Node<K, V> node = nodeFetch((K) key);
        return (node == null) ? null : node.getValue();
    }

    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return nodeFetch((K) key) != null;
    }

    /**
     * @param value value whose presence in this map is to be tested
     * @return
     */
    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        BaseArray<K,V> refNodes = getNodes();
        V returnValue = refNodes.remove(nodeFetch((K) key));;
        setSize(refNodes.getSize());
        if (size() >= getInitialCapacity()) {
            if (size() < (getMaxLoadFactor()*capacity()/4)) {
                getNodes().halveCapacity();
            }
        }
        return returnValue;
    }

    public V put(K key, V value) {
        Node<K,V> newNode = new Node<>(key, value);
        BaseArray<K,V> refNodes = getNodes();
        V returnValue = refNodes.add(newNode);
        setSize(refNodes.getSize());
        if (size() >= getInitialCapacity()) {
            if (size() > getMaxLoadFactor()*capacity()) {
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
     * Calculates and returns the maximum depth of the BSTMap by performing a level order traversal.
     *
     * @return the maximum depth of the BSTMap
     * @since 1.0
     */
    public int maxDepth() {
        BaseArray<K,V> refNodes = getNodes();
        TreeSet<Integer> refSet = refNodes.getFilledIndices();
        int maxDepth = 0;
        for (Integer currIndex : refSet) {
            int currDepth = refNodes.getMaxDepth(currIndex);
            maxDepth = Math.max(currDepth, maxDepth);
        }
        return maxDepth;
    }

    /**
     * Calculates and returns the maximum depth of the BSTMap, starting from the given node,
     * by performing a level order traversal.
     *
     * @param node the starting node for calculating the maximum depth
     * @return the maximum depth from the given node
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

    public int capacity() {
        return getNodes().getCapacity();
    }


    private int hash(K key) {
        return getNodes().hash(key);
    }

    private Node<K, V> getFirst() {
        return getNodes().getFirst();
    }

    private Node<K, V> getLast() {
        return getNodes().getLast();
    }

    private Node<K, V> getPrev(Node<K, V> tempNode) {
        return getNodes().getPrev(tempNode);
    }

    private Node<K, V> getNext(Node<K, V> tempNode) {
        return getNodes().getNext(tempNode);
    }

    public BaseArray<K, V> getNodes() {
        return nodes;
    }

    public void setNodes(BaseArray<K, V> nodes) {
        this.nodes = nodes;
    }


    public void setSize(int size) {
        this.size = size;
    }

    public double getMaxLoadFactor() {
        return maxLoadFactor;
    }

    public void setMaxLoadFactor(double maxLoadFactor) {
        this.maxLoadFactor = maxLoadFactor;
    }

    /**
     * @return
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

    public int getInitialCapacity() {
        return initialCapacity;
    }

    /**
     * Represents a node in the binary search tree used by the BSTMap.
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


    @SuppressWarnings("unchecked")
    public static class BaseArray<K, V> {

        private int capacity;
        private Node<K, V>[] nodes;
        private TreeSet<Integer> filledIndices;
        private int size;


        BaseArray() {
            this(16);
        }

        BaseArray(int capacity) {
            this.capacity = capacity;
            this.size = 0;
            nodes = (Node<K, V>[]) new Node[capacity];
            filledIndices = new TreeSet<>();
        }

        public int getCapacity() {
            return capacity;
        }

        private void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        private int hash(K key) {
            return Math.abs(key.hashCode() % getCapacity());
        }

        private int hash(K key, int modulus) {
            return Math.abs(key.hashCode() % modulus);
        }

        private Node<K, V> getFirst() {
            TreeSet<Integer> filledIndices = getFilledIndices();
            if (filledIndices == null) return null;
            if (filledIndices.isEmpty()) return null;
            int minIndex = filledIndices.getFirst();
            return getNodes()[minIndex];
        }

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

        public void doubleCapacity() {
            int newCapacity = getCapacity() * 2;
            changeCapacity(newCapacity);
        }

        public void halveCapacity() {
            int newCapacity = getCapacity() / 2;
            changeCapacity(newCapacity);
        }

        private void changeCapacity(int newCapacity) {
            Node<K, V>[] tempArray = new Node[newCapacity];
            TreeSet<Integer> tempSet = new TreeSet<>();
            size = 0;
            Node<K,V> refNode = getFirst();
            Node<K,V> prevNode = null;
            while (refNode != null) {
                Node<K,V> tempNode = new Node<>(refNode.getKey(), refNode.getValue());
                add(tempNode,tempArray,tempSet, newCapacity);
                prevNode = refNode;
                refNode = getNext(refNode);
            }
            filledIndices = tempSet;
            nodes = tempArray;
            setCapacity(newCapacity);
        }

        public V add(Node<K, V> node) {
            return add(node, getNodes(), getFilledIndices());
        }

        public V add(Node<K, V> node, Node<K, V>[] nodeList, TreeSet<Integer> nodeIndices) {
            return add(node, nodeList, nodeIndices, getCapacity());
        }
        public V add(Node<K, V> node, Node<K, V>[] nodeList, TreeSet<Integer> nodeIndices, int hashModulus) {
            if (node == null) return null;
            if (node.getKey() == null) return null;
            K key = node.getKey();
            int nodeIndex = hash(key, hashModulus);
            Node<K, V> currNode = nodeList[nodeIndex];
            if (currNode == null) {
                nodeList[nodeIndex] = node;
                nodeIndices.add(nodeIndex);
                size++;
                return null;
            }
            Node<K, V> prevNode = null;
            while (currNode != null) {
                if (currNode.getKey() == key) {
                    V prevValue = currNode.getValue();
                    currNode.setValue(node.getValue());
                    return prevValue;
                }
                prevNode = currNode;
                currNode = currNode.getNext();
            }
            prevNode.setNext(node);
            node.setPrev(prevNode);
            size++;
            return null;
        }

        public V remove(Node<K, V> node) {
            if (node == null) return null;
            int nodeIndex = hash(node.getKey());
            Node<K, V> prevNode = node.getPrev();
            Node<K, V> nextNode = node.getNext();
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            } else {
                getNodes()[nodeIndex] = nextNode;
            }
            if (getNodes()[nodeIndex] == null) {
                getFilledIndices().remove(nodeIndex);
            }
            size--;
            return node.getValue();
        }

        public int getMaxDepth(int index) {
            Node<K, V> refNode = getNodes()[index];
            if (refNode == null) return 0;
            return nodeDepth(refNode);
        }
        public TreeSet<Integer> getFilledIndices() {
            return filledIndices;
        }

        public Node<K, V>[] getNodes() {
            return nodes;
        }

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

        public int getSize() {
            return size;
        }
    }

    /**
     * Represents a set view of the keys contained in a BSTMap.
     *
     * @param <T> the type of keys in the set
     * @since 1.0
     */
    public static class KeySet<T> extends AbstractSet<T> {

        private final HashMap<T, ?> refMap;

        /**
         * Constructs a new KeySet with a reference to the specified BSTMap.
         *
         * @param map the BSTMap to be associated with this KeySet
         */
        KeySet(HashMap<T, ?> map) {
            refMap = map;
        }

        /**
         * Returns an iterator over the keys in the BSTMap.
         *
         * @return an iterator over the keys
         */
        public Iterator<T> iterator() {
            return refMap.keyIterator();
        }

        /**
         * Returns a descending iterator over the keys in the BSTMap.
         *
         * @return a descending iterator over the keys
         */
        public Iterator<T> descendingIterator() {
            return refMap.descendingKeyIterator();
        }

        /**
         * Returns the number of keys in the BSTMap.
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
         * Removes all keys from the BSTMap associated with this KeySet.
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
     * Iterator over the entries of a BSTMap.
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
     * Iterator over the values of a BSTMap.
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
     * Iterator over the keys of a BSTMap.
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
     * Iterator over the keys of a BSTMap in descending order.
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
