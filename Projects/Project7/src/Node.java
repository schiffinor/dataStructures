/*
I just reused my Node class from the maps.
It works.
 */
public class Node<K, V> implements CustomMap.Entry<K, V> {

    private K key;
    private V value;
    private Node<K, V> left;
    private Node<K, V> right;
    private Node<K, V> parent;

    /**
     * Constructs a new node with the specified key, value, and parent node.
     *
     * @param key    the key to be associated with the node
     * @param value  the value to be associated with the key
     * @param parent the parent node of the new node
     */
    public Node(K key, V value, Node<K, V> parent) {
        this.key = key;
        this.value = value;
        this.parent = parent;
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
     * Replaces the value currently associated with the key with the given value.
     *
     * @param key the new value to be associated with the key
     * @return the value associated with the key before this method was called
     */
    public K setKey(K key) {
        K oldKey = this.key;
        this.key = key;
        return oldKey;
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
    public boolean mapEquals(Object obj) {
        return (obj instanceof Node<?, ?> entry && key.equals(entry.getKey()) && value.equals(entry.getValue()));
    }

    /**
     * Compares this node with another object for equality.
     *
     * @param obj the object to be compared for equality
     * @return {@code true} if the objects are equal, otherwise {@code false}
     */
    public boolean keyEquals(Object obj) {
        return (obj instanceof Node<?, ?> entry && key.equals(entry.getKey()));
    }

    /**
     * Compares this node with another object for equality.
     *
     * @param obj the object to be compared for equality
     * @return {@code true} if the objects are equal, otherwise {@code false}
     */
    public boolean equals(Object obj) {
        return (obj instanceof Node<?, ?> entry && value.equals(entry.getValue()));
    }

    /**
     * Returns the left child node of this node.
     *
     * @return the left child node
     */
    public Node<K, V> getLeft() {
        return left;
    }

    /**
     * Sets the left child node of this node.
     *
     * @param left the new left child node
     */
    public void setLeft(Node<K, V> left) {
        this.left = left;
    }

    /**
     * Returns the right child node of this node.
     *
     * @return the right child node
     */
    public Node<K, V> getRight() {
        return right;
    }

    /**
     * Sets the right child node of this node.
     *
     * @param right the new right child node
     */
    public void setRight(Node<K, V> right) {
        this.right = right;
    }

    /**
     * Returns the parent node of this node.
     *
     * @return the parent node
     */
    public Node<K, V> getParent() {
        return parent;
    }

    /**
     * Returns the parent node of this node.
     *
     */
    public void setParent(Node<K, V> parent) {
        this.parent = parent;
    }

    /**
     * Calculates and returns the depth of this node in the tree.
     *
     * @return the depth of this node
     */
    public int calculateDepth() {
        int depth = 1;
        Node<K, V> refNode = this;
        while (refNode.getParent() != null) {
            depth++;
            refNode = refNode.getParent();
        }
        return depth;
    }

    /**
     * Returns a string representation of this node.
     *
     * @return a string representation of this node
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        if (getParent() != null) {
            if (getParent().getLeft() == this) sb.append("left: ");
            else if (getParent().getRight() == this) sb.append("right: ");
            else throw new IllegalArgumentException("Invalid tree all child nodes must be left or right.");
        }
        sb.append("<").append(getKey().toString()).append(" -> ").append(getValue().toString()).append(">)");
        return sb.toString();
    }
}