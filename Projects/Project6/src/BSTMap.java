import java.util.*;

/**
 * Binary Search Tree (BST) Map implementation.
 * <p>
 * This class represents a Binary Search Tree (BST) Map, providing key-value
 * mapping functionality with operations such as insertion, deletion, and
 * retrieval. It supports generic types for keys and values and allows
 * customization through a comparator.
 *
 * <p>
 * The tree is organized such that each node has at most two children: a left
 * child with a smaller key and a right child with a larger key. The class
 * provides methods for various tree operations, including traversal, depth
 * calculation, and set manipulations.
 *
 * <p>
 * Additionally, the class includes iterators for iterating over the keys, values,
 * and entries in both ascending and descending orders. It is designed to be
 * versatile, allowing users to create and manage a BST Map based on their
 * specific requirements.
 *
 * <p>
 * The BSTMap is equipped with features such as the ability to find the supremum
 * and infimum nodes (technically not supremum or infimum as they are contained
 * within the set, but close enough) for a given key, as well as methods for
 * searching and clearing the tree. It also provides an EntrySet and a KeySet
 * for convenient access to the entries and keys in the map.
 *
 * <p>
 * The implementation follows the Java Collections Framework conventions for
 * maps and sets, providing a familiar interface for users.
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * BSTMap<String, Integer> bstMap = new BSTMap<>();
 * bstMap.put("one", 1);
 * bstMap.put("two", 2);
 * System.out.println(bstMap.get("one")); // Output: 1
 * }
 * </pre>
 * <p>
 *     Also as a quick aside, I was strongly inspired by javas own implementation of the treemap,
 *     hence my implementation of so many extra methods. However, I wrote everything myself.
 * </p>
 * <p>
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * <p>
 * @version 1.0
 * <p>
 * @since 1.0
 */
public class BSTMap<K, V> implements CustomMap<K,V>, Iterable<BSTMap.Node<K,V>> {

    private final EntrySet entrySet;
    private final Comparator<K> comparator;
    private KeySet<K> keySet;
    private Node<K, V> root;
    private int size;

    /**
     * Constructs an empty BSTMap. The default constructor initializes the map with
     * a null comparator, allowing natural ordering of keys. The entry set and size
     * are set to initial values, and the comparator is initialized based on the
     * provided comparator or default comparator for the key type.
     *
     * @since 1.0
     */
    public BSTMap() {
        this(null);
    }

    /**
     * Constructs a BSTMap with the specified comparator. If the provided
     * comparator is null, a default comparator based on the natural ordering of
     * keys is used. Initializes the entry set and size, and sets the comparator
     * for key comparison.
     *
     * @param comparator the comparator to determine the order of the keys, or
     *                   null for natural ordering
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BSTMap(Comparator<K> comparator) {
        this.entrySet = new EntrySet();
        this.size = 0;
        this.comparator = Objects.requireNonNullElseGet(comparator, () -> (obj1, obj2) -> ((Comparable<K>) obj1).compareTo(obj2));
    }

    /**
     * Returns the next entry in the tree, or null if no such entry exists.
     *
     * @param current the current node for which to find the next entry
     * @return the next entry in the tree, or null if no such entry exists
     * @since 1.0
     */
    static <K, V> Node<K, V> getNext(Node<K, V> current) {
        if (current == null)
            return null;
        else if (current.getRight() != null) {
            Node<K, V> toReturn = current.getRight();
            while (toReturn.getLeft() != null)
                toReturn = toReturn.getLeft();
            return toReturn;
        } else {
            Node<K, V> toReturn = current.getParent();
            Node<K, V> rightChild = current;
            while (toReturn != null && rightChild == toReturn.getRight()) {
                rightChild = toReturn;
                toReturn = toReturn.getParent();
            }
            return toReturn;
        }
    }

    /**
     * Returns the previous entry in the tree, or null if no such entry exists.
     *
     * @param current the current node for which to find the previous entry
     * @return the previous entry in the tree, or null if no such entry exists
     * @since 1.0
     */
    static <K, V> Node<K, V> getPrev(Node<K, V> current) {
        if (current == null)
            return null;
        else if (current.getLeft() != null) {
            Node<K, V> toReturn = current.getLeft();
            while (toReturn.getRight() != null)
                toReturn = toReturn.getRight();
            return toReturn;
        } else {
            Node<K, V> toReturn = current.getParent();
            Node<K, V> leftChild = current;
            while (toReturn != null && leftChild == toReturn.getLeft()) {
                leftChild = toReturn;
                toReturn = toReturn.getParent();
            }
            return toReturn;
        }
    }

    public static void main(String[] args) {
        // this will sort the strings lexicographically (dictionary-order)
        BSTMap<String, Integer> words = new BSTMap<>();
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
        System.out.println(words.maxDepth());
        words.remove("seven");
        System.out.println(words);
        System.out.println(words.treeWalk(-1));
        System.out.println(words.treeWalk(0));
        System.out.println(words.treeWalk(1));
        System.out.println(words.treeWalk(2));
        System.out.println(words.keySet());
        System.out.println(words.entrySet());
        System.out.println(words.maxDepth());
        System.out.println(words.containsKey("ter"));

        // this will sort the strings in reverse lexicographic order
        BSTMap<String, Integer> wordsReverse = new BSTMap<>((o1, o2) -> o2.compareTo(o1));
        wordsReverse.put("ten", 10);
        wordsReverse.put("five", 5);
        wordsReverse.put("three", 3);
        System.out.println(wordsReverse);
    }

    /**
     * Returns the root node of the BSTMap.
     *
     * @return the root node of the BSTMap
     * @since 1.0
     */
    public Node<K, V> getRoot() {
        return root;
    }

    /**
     * Sets the root node of the BSTMap.
     *
     * @param root the new root node to be set
     * @since 1.0
     */
    public void setRoot(Node<K, V> root) {
        this.root = root;
    }

    /**
     * Returns the node with the minimum key value in the BSTMap.
     *
     * @return the node with the minimum key value
     * @since 1.0
     */
    final Node<K, V> getMinNode() {
        return getRelMinNode(root);
    }

    /**
     * Returns the node with the maximum key value in the BSTMap.
     *
     * @return the node with the maximum key value
     * @since 1.0
     */
    final Node<K, V> getMaxNode() {
        return getRelMaxNode(root);
    }

    /**
     * Returns the node with the minimum key value in the subtree rooted at the
     * specified node.
     *
     * @param node the root of the subtree
     * @return the node with the minimum key value in the subtree
     * @since 1.0
     */
    final Node<K, V> getRelMinNode(Node<K, V> node) {
        Node<K, V> refNode = node;
        if (refNode != null)
            while (refNode.getLeft() != null)
                refNode = refNode.getLeft();
        return refNode;
    }

    /**
     * Returns the node with the maximum key value in the subtree rooted at the
     * specified node.
     *
     * @param node the root of the subtree
     * @return the node with the maximum key value in the subtree
     * @since 1.0
     */
    final Node<K, V> getRelMaxNode(Node<K, V> node) {
        Node<K, V> refNode = node;
        if (refNode != null)
            while (refNode.getRight() != null)
                refNode = refNode.getRight();
        return refNode;
    }

    /**
     * Returns a string representation of the BSTMap, showing the keys in level
     * order along with their depth in the tree.
     *
     * @return a string representation of the BSTMap
     * @since 1.0
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        LinkedList<Node<K, V>> printQueue = levelOrderWalk(root);
        Node<K, V> prevNode = new Node<>(null, null, null);
        int count = 0;
        sb.append(count).append(": { ");
        for (Node<K, V> node : printQueue) {
            if (node.calculateDepth() != prevNode.calculateDepth()) {
                count++;
                sb.append(" } }\n").append(count).append(": { { ");
            } else if (node.getParent() != prevNode.getParent()) {
                sb.append(" } { ");
            }
            sb.append(node);
            prevNode = node;
        }
        sb.append(" } }");
        return sb.toString();
    }

    /**
     * Associates the specified value with the specified key in this map.
     * <p>
     * A major reason this is so concise is because I offset a good amount of the
     * work onto the entry set
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no mapping for the key
     * @throws IllegalStateException if the tree structure is violated during insertion
     * @since 1.0
     */
    public V put(K key, V value) {
        if (value == null) return null;
        Node<K, V> newNode = new Node<>(key, value, null);
        if (size() == 0) {
            this.root = newNode;
            size++;
            entrySetES().add(newNode);
            return null;
        }
        Node<K, V> refNode = nodeFetch(key);
        if (refNode != null) {
            V previousValue = refNode.getValue();
            refNode.setValue(value);
            return previousValue;
        }
        refNode = fetchNearest(key);
        newNode.setParent(refNode);
        int lastComp = comparator.compare(key, refNode.getKey());
        if (lastComp < 0) refNode.setLeft(newNode);
        else if (lastComp > 0) refNode.setRight(newNode);
        else throw new IllegalStateException("Improper tree, all non-root nodes must be left or right.");
        size++;
        entrySetES().add(newNode);
        return null;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with the key, or {@code null} if there was no mapping for the key
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        V toReturn = get(key);
        nodeDelete(nodeFetch((K) key));
        return toReturn;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        return nodeFetch((K) key).getValue();
    }

    /**
     * Returns the supremum node (the smallest node greater than or equal to the given key) in the BSTMap.
     *
     * @param key the key for which the supremum node is to be found
     * @return the supremum node for the given key
     * @since 1.0
     */
    public Node<K, V> supremum(K key) {
        return bound(key, 1);
    }

    /**
     * Returns the infimum node (the largest node smaller than or equal to the given key) in the BSTMap.
     *
     * @param key the key for which the infimum node is to be found
     * @return the infimum node for the given key
     * @since 1.0
     */
    public Node<K, V> infimum(K key) {
        return bound(key, -1);
    }

    /**
     * Returns the node nearest to the given key in the BSTMap.
     *
     * @param key the key for which the nearest node is to be found
     * @return the nearest node for the given key
     * @since 1.0
     */
    public Node<K, V> fetchNearest(K key) {
        Node<K, V> currNode = root;
        if (currNode == null) return null;
        Node<K, V> refNode = null;
        while (currNode != null) {
            int comparisonResult = comparator.compare(key, currNode.getKey());
            refNode = currNode;
            if (comparisonResult < 0) {
                currNode = currNode.getLeft();
            } else if (comparisonResult > 0) {
                currNode = currNode.getRight();
            } else {
                break;
            }
        }
        return refNode;
    }

    /**
     * Returns the bound node based on the given key and upperLower flag.
     *
     * @param key         the key for which the bound node is to be found
     * @param upperLower  a flag indicating whether to find the upper (1) or lower (-1) bound
     * @return the bound node for the given key and upperLower flag
     * @throws IllegalArgumentException if the upperLower flag is neither 1 nor -1
     * @since 1.0
     */
    public Node<K, V> bound(K key, int upperLower) {
        Node<K, V> refNode = fetchNearest(key);
        int lastComp = comparator.compare(key, refNode.getKey());
        switch (upperLower) {
            case 1 -> {
                if (lastComp < 0) return getPrev(refNode);
                else return refNode;
            }
            case -1 -> {
                if (lastComp > 0) return getNext(refNode);
                else return refNode;
            }
            default -> throw new IllegalArgumentException("Only 1 and -1 are acceptable inputs.");
        }
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
     * Returns the internal EntrySet object used for maintaining entries in the BSTMap.
     *
     * @return the EntrySet object
     * @since 1.0
     */
    public EntrySet entrySetES() {
        return entrySet;
    }

    /**
     * Returns the entry set as a Set of nodes.
     *
     * @return the entry set as a Set of nodes
     * @since 1.0
     */
    public Set<Node<K, V>> entrySetAsSet() {
        return entrySetES();
    }

    /**
     * @param m mappings to be stored in this map
     */
    @Override
    public void putAll(CustomMap<? extends K, ? extends V> m) {
        m.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
    }

    /**
     * Returns the number of key-value mappings in this BSTMap.
     *
     * @return the number of key-value mappings in this BSTMap
     * @since 1.0
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this BSTMap contains no key-value mappings.
     *
     * @return {@code true} if this BSTMap is empty, {@code false} otherwise
     * @since 1.0
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns {@code true} if this BSTMap contains a mapping for the specified key.
     *
     * @param key the key whose presence in this BSTMap is to be tested
     * @return {@code true} if this BSTMap contains a mapping for the specified key, {@code false} otherwise
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        return nodeFetch((K) key) != null;
    }

    /**
     * Returns {@code true} if this BSTMap contains a mapping for the specified value.
     *
     * @param value the value whose presence in this BSTMap is to be tested
     * @return {@code true} if this BSTMap contains a mapping for the specified value, {@code false} otherwise
     * @since 1.0
     */
    public boolean containsValue(Object value) {
        return valueSearch(value, -1) != null;
    }

    /**
     * Removes all the mappings from this BSTMap.
     * <p>
     * Goes in-order to ensure all deleted nodes are leaf nodes when deleted to
     * ensure the fastest deletions.
     *
     * @since 1.0
     */
    public void clear() {
        LinkedList<Node<K, V>> postorder = inorderWalk(root);
        for (Node<K, V> node : postorder) {
            nodeDelete(node);
        }
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
     * Deletes the specified node from the Binary Search Tree (BSTMap), adjusting the tree structure accordingly.
     * If the node to delete has two children, it is replaced with its in-order successor.
     *
     * @param nodeToDelete the node to be deleted from the BSTMap
     * @throws IllegalArgumentException if the inputted node is null
     * @implNote This method removes a node from the BST while maintaining the BST properties.
     *          It handles three cases:
     *          <ol>
     *              <li>
     *                  The node to be deleted has no children: It is simply removed from the tree.
     *              </li>
     *              <li>
     *                  The node to be deleted has one child: The child replaces the deleted node.
     *              </li>
     *              <li>
     *                  The node to be deleted has two children: It is replaced by its in-order successor,
     *                  and the in-order successor's original position is adjusted.
     *              </li>
     *          </ol>
     *          <p>
     *          The method performs the following steps:
     *          <ol>
     *              <li>
     *                  If the input node is null, an IllegalArgumentException is thrown.
     *              </li>
     *              <li>
     *                  The right and left child nodes of the node to be deleted are retrieved.
     *              </li>
     *              <li>
     *                  The parent node of the node to be deleted is also retrieved.
     *              </li>
     *              <li>
     *                  If the node has both left and right children, it is replaced with its in-order successor.
     *                  <ul>
     *                      <li>
     *                          The direction to traverse to find the in-order successor is determined.
     *                      </li>
     *                      <li>
     *                          The successor is then swapped with the node to be deleted.
     *                      </li>
     *                      <li>
     *                          The parent node, direction, and references are updated accordingly.
     *                      </li>
     *                  </ul>
     *              </li>
     *              <li>
     *                  The new parent-child relationship is established, and the size of the BST is decreased.
     *              </li>
     *          </ol>
     *          <p>
     *          This method assumes that the inputted node belongs to the same BSTMap instance it is called on.
     *          Modifying nodes that belong to a different tree may lead to unexpected behavior.
     *          Not tremendously clear, but very efficient.
     *
     * @since 1.0
     */
    private void nodeDelete(Node<K, V> nodeToDelete) {
        if (nodeToDelete == null) {
            throw new IllegalArgumentException("Inputted node does not exist.");
        }
        Node<K, V> rightNode = nodeToDelete.getRight();
        Node<K, V> leftNode = nodeToDelete.getLeft();
        Node<K, V> parentNode = nodeToDelete.getParent();
        Node<K, V> refNode = nodeToDelete;
        int refDirection = 0;

        // Case: Node has two children
        if (leftNode != null && rightNode != null) {
            int primeDirection = directionDeterminer(parentNode, nodeToDelete);
            Node<K, V> replacer = getNext(refNode);

            // If the in-order successor is the right child, then that child has no left child.
            // Given no left child, its right child will remain its right child.
            if (replacer == rightNode) {
                parentNode = replacer;
                refDirection = 1;
            } else {
                // If the in-order successor is not the right child, then the successor is a left child.
                // Given it is a left child, it cannot have a left child, it must have a right child if anything.
                // Thus, the successor nodes parent must become the successors new right child.
                // Thus, the successor nodes former right child must become the successor's new right child's left child.
                parentNode = replacer.getParent();
                refDirection = -1;
            }

            // Replace the node with its in-order successor
            setter(primeDirection, refNode, replacer);
            replacer.setParent(refNode.getParent());
            refNode = replacer;
        }

        // Find the refNode's child, if the above operation took place, the refNode is the former replacer.
        // Otherwise, the given nodeToDelete.
        Node<K, V> newParentChild = (refNode.getLeft() != null) ? refNode.getLeft() : refNode.getRight();
        if (newParentChild != null) {
            // Highly dependent on above operation. If refNode is nodeToDelete, parentNode is just that node's parent.
            // refNode could also be the replacer. In which case, parentNode is either itself or replacers parent.
            newParentChild.setParent(parentNode);
        }

        int secDirection;
        // Update the references based on the direction
        if (refDirection != 0) {
            // If nodeToDelete had two children, the replacer node must have its left child set to nodeToDelete's left child.
            secDirection = refDirection;
            refNode.setLeft(leftNode);
            if (refDirection == -1) {
                // If replacer was left child its parent must have its left child set as replacers right child.
                refNode.setRight(rightNode);
            }
        } else {
            // Simple single or no children solutions.
            secDirection = directionDeterminer(parentNode, refNode);
        }
        directSetter(secDirection, parentNode, newParentChild);
        size--;
    }

    /**
     * Determines the direction of the child node relative to its parent.
     *
     * @param parentNode the parent node
     * @param refNode    the child node
     * @return -1 if the child node is the left child, 1 if it is the right child, 0 if there is no parent (root)
     * @throws IllegalStateException if the child node is not equal to the parent's left or right child
     * @since 1.0
     */
    private int directionDeterminer(Node<K, V> parentNode, Node<K, V> refNode) {
        int direction = 0;
        if (parentNode != null) {
            direction = (parentNode.getLeft() == refNode) ? -1 : 1;
            if (direction == 1 && parentNode.getRight() != refNode)
                throw new IllegalStateException("BST construction error, child not equal to parent left or right.");
        }
        return direction;
    }

    /**
     * Sets the specified replacer node in the correct position relative to its parent, based on the given direction.
     *
     * @param direction the direction in which to set the replacer node: -1 for left, 0 for root, 1 for right
     * @param refNode   the node to be replaced
     * @param replacer  the node to replace refNode
     * @throws IllegalStateException if the direction is not a valid value
     * @since 1.0
     */
    private void setter(int direction, Node<K, V> refNode, Node<K, V> replacer) {
        switch (direction) {
            case -1 -> refNode.getParent().setLeft(replacer);
            case 0 -> root = replacer;
            case 1 -> refNode.getParent().setRight(replacer);
            default -> throw new IllegalStateException("Direction disallowed value.");
        }
    }

    /**
     * Sets the specified replacer node as the left or right child of the reference node,
     * based on the given direction.
     *
     * @param direction the direction in which to set the replacer node: -1 for the left,
     *                 0 for the root, 1 for the right
     * @param refNode   the reference node
     * @param replacer  the node to replace the corresponding child of refNode
     * @throws IllegalStateException if the direction is not a valid value
     * @since 1.0
     */
    private void directSetter(int direction, Node<K, V> refNode, Node<K, V> replacer) {
        switch (direction) {
            case -1 -> refNode.setLeft(replacer);
            case 0 -> root = replacer;
            case 1 -> refNode.setRight(replacer);
            default -> throw new IllegalStateException("Direction disallowed value.");
        }
    }

    /**
     * Fetches the node associated with the specified key in the BSTMap.
     *
     * @param key the key whose associated node is to be fetched
     * @return the node associated with the specified key, or null if the key is not present
     * @since 1.0
     */
    private Node<K, V> nodeFetch(K key) {
        Node<K, V> currNode = root;
        while (currNode != null) {
            int comparisonResult = comparator.compare(key, currNode.getKey());
            if (comparisonResult < 0) {
                currNode = currNode.getLeft();
            } else if (comparisonResult > 0) {
                currNode = currNode.getRight();
            } else {
                return currNode;
            }
        }
        return currNode;
    }

    /**
     * Fetches the node associated with the specified value in the BSTMap.
     *
     * @param value the value whose associated node is to be fetched
     * @return the node associated with the specified value, or null if the value is not present
     * @since 1.0
     */
    private Node<K, V> nodeValFetch(V value) {
        Node<K, V> currNode = root;
        return valueSearch(value, -1);
    }

    /**
     * Searches for a node based on the specified value and search type in the BSTMap.
     *
     * @param value      the value to search for
     * @param searchType the type of search to perform: -1 for inorder, 0 for preorder, 1 for postorder
     * @return the node found during the specified search, or null if the value is not present
     * @throws IllegalArgumentException if the search type is unknown
     * @since 1.0
     */
    public Node<K, V> valueSearch(Object value, int searchType) {
        return switch (searchType) {
            case -1 -> inorderSearch(value);
            case 0 -> preorderSearch(value);
            case 1 -> postorderSearch(value);
            default -> throw new IllegalArgumentException("Unknown search type: " + searchType);
        };
    }

    /**
     * Performs an inorder search for a node with the specified value in the BSTMap.
     *
     * @param value the value to search for
     * @return the node found during the inorder search, or null if the value is not present
     * @since 1.0
     */
    private Node<K, V> inorderSearch(Object value) {
        return inorderSearch(root, value);
    }

    /**
     * Performs an inorder search for a node with the specified value starting from the given node.
     *
     * @param node  the starting node for the inorder search
     * @param value the value to search for
     * @return the node found during the inorder search, or null if the value is not present
     * @since 1.0
     */
    private Node<K, V> inorderSearch(Node<K, V> node, Object value) {
        if (node != null) {
            inorderSearch(node.getLeft(), value);
            if (node.getValue().equals(value)) return node;
            inorderSearch(node.getRight(), value);
        }
        return null;
    }

    /**
     * Performs a preorder search for a node with the specified value in the BSTMap.
     *
     * @param value the value to search for
     * @return the node found during the preorder search, or null if the value is not present
     * @since 1.0
     */
    private Node<K, V> preorderSearch(Object value) {
        return preorderSearch(root, value);
    }

    /**
     * Performs a preorder search for a node with the specified value starting from the given node.
     *
     * @param node  the starting node for the preorder search
     * @param value the value to search for
     * @return the node found during the preorder search, or null if the value is not present
     * @since 1.0
     */
    private Node<K, V> preorderSearch(Node<K, V> node, Object value) {
        if (node != null) {
            if (node.getValue().equals(value)) return node;
            preorderSearch(node.getLeft(), value);
            preorderSearch(node.getRight(), value);
        }
        return null;
    }

    /**
     * Performs a postorder search for a node with the specified value in the BSTMap.
     *
     * @param value the value to search for
     * @return the node found during the postorder search, or null if the value is not present
     * @since 1.0
     */
    private Node<K, V> postorderSearch(Object value) {
        return postorderSearch(root, value);
    }

    /**
     * Performs a postorder search for a node with the specified value starting from the given node.
     *
     * @param node  the starting node for the postorder search
     * @param value the value to search for
     * @return the node found during the postorder search, or null if the value is not present
     * @since 1.0
     */
    private Node<K, V> postorderSearch(Node<K, V> node, Object value) {
        if (node != null) {
            postorderSearch(node.getLeft(), value);
            postorderSearch(node.getRight(), value);
            if (node.getValue().equals(value)) return node;
        }
        return null;
    }

    /**
     * Performs a tree walk of the specified type starting from the root of the BSTMap.
     *
     * @param walkType the type of tree walk to perform: -1 for inorder, 0 for preorder, 1 for postorder, 2 for level order
     * @return a linked list containing nodes visited during the tree walk
     * @throws IllegalArgumentException if the walk type is unknown
     * @since 1.0
     */
    public LinkedList<Node<K, V>> treeWalk(int walkType) {
        return treeWalk(getRoot(), walkType);
    }

    /**
     * Performs a tree walk of the specified type starting from the given node.
     *
     * @param node     the starting node for the tree walk
     * @param walkType the type of tree walk to perform: -1 for inorder, 0 for preorder, 1 for postorder, 2 for level order
     * @return a linked list containing nodes visited during the tree walk
     * @throws IllegalArgumentException if the walk type is unknown
     * @since 1.0
     */
    public LinkedList<Node<K, V>> treeWalk(Node<K, V> node, int walkType) {
        return switch (walkType) {
            case -1 -> inorderWalk(node);
            case 0 -> preorderWalk(node);
            case 1 -> postorderWalk(node);
            case 2 -> levelOrderWalk(node);
            default -> throw new IllegalArgumentException("Unknown search type: " + walkType);
        };
    }

    /**
     * Performs an inorder tree walk starting from the given node.
     *
     * @param node the starting node for the inorder tree walk
     * @return a linked list containing nodes visited during the inorder tree walk
     * @since 1.0
     */
    private LinkedList<Node<K, V>> inorderWalk(Node<K, V> node) {
        return inorderWalk(node, null);
    }

    /**
     * Performs an inorder tree walk starting from the given node and appends to the specified list.
     *
     * @param node the starting node for the inorder tree walk
     * @param list the list to which nodes are appended
     * @return a linked list containing nodes visited during the inorder tree walk
     * @since 1.0
     */
    private LinkedList<Node<K, V>> inorderWalk(Node<K, V> node, LinkedList<Node<K, V>> list) {
        LinkedList<Node<K, V>> tempList = (list == null) ? new LinkedList<>() : list;
        if (node != null) {
            inorderWalk(node.getLeft(), tempList);
            tempList.addLast(node);
            inorderWalk(node.getRight(), tempList);
        }
        return tempList;
    }

    /**
     * Performs a preorder tree walk starting from the given node.
     *
     * @param node the starting node for the preorder tree walk
     * @return a linked list containing nodes visited during the preorder tree walk
     * @since 1.0
     */
    private LinkedList<Node<K, V>> preorderWalk(Node<K, V> node) {
        return preorderWalk(node, null);
    }

    /**
     * Performs a preorder tree walk starting from the given node and appends to the specified list.
     *
     * @param node the starting node for the preorder tree walk
     * @param list the list to which nodes are appended
     * @return a linked list containing nodes visited during the preorder tree walk
     * @since 1.0
     */
    private LinkedList<Node<K, V>> preorderWalk(Node<K, V> node, LinkedList<Node<K, V>> list) {
        LinkedList<Node<K, V>> tempList = (list == null) ? new LinkedList<>() : list;
        if (node != null) {
            tempList.addLast(node);
            preorderWalk(node.getLeft(), tempList);
            preorderWalk(node.getRight(), tempList);
        }
        return tempList;
    }

    /**
     * Performs a postorder tree walk starting from the given node.
     *
     * @param node the starting node for the postorder tree walk
     * @return a linked list containing nodes visited during the postorder tree walk
     * @since 1.0
     */
    private LinkedList<Node<K, V>> postorderWalk(Node<K, V> node) {
        return postorderWalk(node, null);
    }

    /**
     * Performs a postorder tree walk starting from the given node and appends to the specified list.
     *
     * @param node the starting node for the postorder tree walk
     * @param list the list to which nodes are appended
     * @return a linked list containing nodes visited during the postorder tree walk
     * @since 1.0
     */
    private LinkedList<Node<K, V>> postorderWalk(Node<K, V> node, LinkedList<Node<K, V>> list) {
        LinkedList<Node<K, V>> tempList = (list == null) ? new LinkedList<>() : list;
        if (node != null) {
            postorderWalk(node.getLeft(), tempList);
            postorderWalk(node.getRight(), tempList);
            tempList.addLast(node);
        }
        return tempList;
    }

    /**
     * @return
     */
    @Override
    public Iterator<Node<K, V>> iterator() {
        return new EntryIterator(getMinNode());
    }

    /**
     * Performs a level order tree walk starting from the given node.
     * <p>
     * Not recursive because I couldn't think of any better way to do this.
     *
     * @param node the starting node for the level order tree walk
     * @return a linked list containing nodes visited during the level order tree walk
     * @since 1.0
     */
    private LinkedList<Node<K, V>> levelOrderWalk(Node<K, V> node) {
        LinkedList<Node<K, V>> outList = new LinkedList<>();
        Queue<Node<K, V>> handleQueue = new LinkedList<>();
        handleQueue.offer(node);
        while (!handleQueue.isEmpty()) {
            Node<K, V> refNode = handleQueue.poll();
            outList.addLast(refNode);
            if (refNode.getLeft() != null) handleQueue.offer(refNode.getLeft());
            if (refNode.getRight() != null) handleQueue.offer(refNode.getRight());
        }
        return outList;
    }

    /**
     * Calculates and returns the maximum depth of the BSTMap by performing a level order traversal.
     *
     * @return the maximum depth of the BSTMap
     * @since 1.0
     */
    public int maxDepth() {
        return maxDepth(getRoot());
    }

    /**
     * Calculates and returns the maximum depth of the BSTMap, starting from the given node,
     * by performing a level order traversal.
     *
     * @param node the starting node for calculating the maximum depth
     * @return the maximum depth from the given node
     * @since 1.0
     */
    public int maxDepth(Node<K, V> node) {
        Queue<Node<K, V>> handleQueue = new LinkedList<>();
        int currentDepth = 0;
        handleQueue.offer(node);
        handleQueue.offer(null);
        while (!handleQueue.isEmpty()) {
            Node<K, V> refNode = handleQueue.poll();
            if (refNode == null) currentDepth++;
            else {
                if (refNode.getLeft() != null) handleQueue.offer(refNode.getLeft());
                if (refNode.getRight() != null) handleQueue.offer(refNode.getRight());
            }
            if (refNode == null && !handleQueue.isEmpty()) {
                handleQueue.offer(null);
            }
        }
        return currentDepth;
    }

    /**
     * Returns an iterator over the keys of the BSTMap in ascending order.
     *
     * @return an iterator over the keys in ascending order
     * @since 1.0
     */
    Iterator<K> keyIterator() {
        return new BSTMap<K, V>.KeyIterator(getMinNode());
    }

    /**
     * Returns an iterator over the keys of the BSTMap in descending order.
     *
     * @return an iterator over the keys in descending order
     * @since 1.0
     */
    Iterator<K> descendingKeyIterator() {
        return new BSTMap<K, V>.DescendingKeyIterator(getMaxNode());
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
            return (obj instanceof Node<?,?> entry && key.equals(entry.getKey()) && value.equals(entry.getValue()));
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
         * @return the parent node
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

    /**
     * Represents a set view of the keys contained in a BSTMap.
     *
     * @param <T> the type of keys in the set
     * @since 1.0
     */
    public static class KeySet<T> extends AbstractSet<T> {

        private final BSTMap<T, ?> refMap;

        /**
         * Constructs a new KeySet with a reference to the specified BSTMap.
         *
         * @param map the BSTMap to be associated with this KeySet
         */
        KeySet(BSTMap<T, ?> map) {
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

    /**
     * Abstract base class for iterators over the elements of a BSTMap.
     * Basic format derived from the LLIterators from the linked list project.
     *
     * @param <T> the type of elements in the iterator
     * @since 1.0
     */
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

        /**
         * Removes the last-fetched entry from the underlying BSTMap.
         *
         * @throws IllegalStateException if attempting to remove without a prior fetch
         */
        public void remove() {
            if (previousFetch == null)
                throw new IllegalStateException("Cannot remove node prior to fetch. " +
                        "Also one remove call per fetch.");
            if (previousFetch.left != null && previousFetch.right != null)
                currentNext = previousFetch;
            nodeDelete(previousFetch);
            previousFetch = null;
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

        public void remove() {
            if (previousFetch == null)
                throw new IllegalStateException();
            nodeDelete(previousFetch);
            previousFetch = null;
        }
    }

    /**
     * A set view of the entries contained in a BSTMap, implemented as an AbstractSet.
     *
     * @since 1.0
     */
    public class EntrySet extends AbstractSet<Node<K, V>> {

        private final ArrayList<Node<K, V>> entryList = new ArrayList<>();
        private V currentPreviousNodeValue = null;
        private int size = 0;

        /**
         * Returns the value of the previous node during the current operation.
         *
         * @return the value of the previous node
         */
        public V getCurrPrevNodeVal() {
            return currentPreviousNodeValue;
        }

        private void setCurrPrevNodeVal(V val) {
            currentPreviousNodeValue = val;
        }

        /**
         * Checks if the set contains the specified object.
         *
         * @param obj the object to check for presence in the set
         * @return {@code true} if the set contains the object, otherwise {@code false}
         */
        public boolean contains(Object obj) {
            return entryList.contains(obj);
        }

        /**
         * Checks if the set contains an entry with the specified key.
         *
         * @param key the key to check for presence in the set
         * @return {@code true} if the set contains an entry with the key, otherwise {@code false}
         */
        public boolean containsKey(K key) {
            for (Node<K, V> node : entryList) {
                if (node.getKey().equals(key)) return true;
            }
            return false;
        }

        /**
         * Adds the specified entry to the set.
         *
         * @param node the entry to be added
         * @return {@code true} if the entry was added, {@code false} if it already existed (value is updated)
         */
        @Override
        public boolean add(Node<K, V> node) {
            size++;
            return entryList.add(node);
        }

        /**
         * Removes the specified entry from the set.
         *
         * @param node the entry to be removed
         * @return {@code true} if the entry was removed, otherwise {@code false}
         */
        public boolean remove(Node<K, V> node) {
            boolean removed = super.remove(node);
            size -= removed ? 1 : 0;
            return removed;
        }

        /**
         * Returns an iterator over the entries in the set.
         *
         * @return an iterator over the entries in the set
         */
        @Override
        public Iterator<Node<K, V>> iterator() {
            return new EntryIterator(root);
        }


        /**
         * Returns the size of the set.
         *
         * @return the size of the set
         */
        @Override
        public int size() {
            return size;
        }
    }
}
