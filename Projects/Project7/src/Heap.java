import java.util.*;

/**
 * Priority Heap implementation.
 * <p>
 * This class represents a Priority Heap, providing priority queue functionality
 * with operations such as insertion, deletion, and retrieval. It supports generic
 * types for elements and allows customization through a comparator.
 *
 * <p>
 * The heap is implemented as a binary tree where each node has at most two children.
 * The class provides methods for various heap operations, including traversal,
 * updating priority, and set manipulations.
 *
 * <p>
 * Additionally, the class includes methods for adding elements, checking the size,
 * peeking at the element of greatest priority, and polling the element of greatest priority.
 * It is designed to be versatile, allowing users to create and manage a Priority Heap
 * based on their specific requirements.
 *
 * <p>
 * The Priority Heap is equipped with features such as the ability to traverse to the
 * Nth node and update the priority of an item.
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * Heap<Integer> priorityHeap = new Heap<>();
 * priorityHeap.offer(1);
 * priorityHeap.offer(2);
 * System.out.println(priorityHeap.peek()); // Output: 2
 * }
 * </pre>
 * <p>
 * The class is designed to be user-friendly and efficient.
 * As such some methods are designed non-standard for e
 *
 * <p>
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * @param <T> the type of elements maintained by this priority heap
 * @version 1.0
 * @since 1.0
 */
public class Heap<T> implements PriorityQueue<T> {

    final Comparator<T> comparator;
    int size;
    final boolean maxState;
    final Node<Integer, T> root;
    Node<Integer, T> lastInsert;
    private final HashMap<Node<Integer, T>, Integer> priorityMap;
    private final HashMap<Node<Integer, T>, Node<Integer, T>> mirrorMap;
    private final ArrayList<Node<Integer, T>> nodeIndexes;

    /**
     * Constructs a new Heap with default settings.
     * <p>
     * This constructor creates a new Heap with initial size 0, using natural ordering
     * for elements, and operating in min-state (i.e., smallest elements have the highest priority).
     * The heap is initialized as an empty binary tree with a root node.
     *
     * @since 1.0
     */
    public Heap() {
        this(null, false);
    }

    /**
     * Constructs a new Heap with the specified maximum state.
     * <p>
     * This constructor creates a new Heap with initial size 0, using natural ordering
     * for elements, and operating in the specified max or min-state based on the provided parameter.
     * The heap is initialized as an empty binary tree with a root node.
     *
     * @param maxState {@code true} for max-state (largest elements have the highest priority),
     *                 {@code false} for min-state (smallest elements have the highest priority).
     * @since 1.0
     */
    public Heap(boolean maxState) {
        this(null, maxState);
    }

    /**
     * Constructs a new Heap with the specified comparator.
     * <p>
     * This constructor creates a new Heap with initial size 0, using the provided comparator
     * for ordering elements, and operating in min-state (i.e., smallest elements have the highest priority).
     * The heap is initialized as an empty binary tree with a root node.
     *
     * @param comparator the comparator to use for ordering elements, or {@code null} for natural ordering.
     * @since 1.0
     */
    public Heap(Comparator<T> comparator) {
        this(comparator, false);
    }

    /**
     * Constructs a new Heap with the specified comparator and maximum state.
     * <p>
     * This constructor creates a new Heap with initial size 0, using the provided comparator
     * for ordering elements and operating in the specified max or min-state based on the provided parameter.
     * The heap is initialized as an empty binary tree with a root node.
     *
     * @param comparator the comparator to use for ordering elements, or {@code null} for natural ordering.
     * @param maxState {@code true} for max-state (largest elements have the highest priority),
     *                 {@code false} for min-state (smallest elements have the highest priority).
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Heap(Comparator<T> comparator, boolean maxState) {
        this.size = 0;
        this.maxState = maxState;
        this.comparator = Objects.requireNonNullElseGet(comparator, () -> (obj1, obj2) -> ((Comparable<T>) obj1).compareTo(obj2));
        this.priorityMap = new HashMap<>();
        this.mirrorMap = new HashMap<>();
        this.nodeIndexes = new ArrayList<>();
        this.root = new Node<>(1, null, null);
        this.priorityMap.put(root, 1);
        this.nodeIndexes.add(root);
    }

    /**
     * Traverses to the Nth node in the binary tree.
     * <p>
     * This method performs a binary tree traversal to locate and return the Nth node.
     * The traversal is determined by the binary representation of N, where each bit
     * indicates whether to move to the left child (0) or the right child (1).
     * <p>
     * For example, if N is represented in binary as "1010", first we will remove the
     * leading 1, then the traversal will move to the left child, then the right child,
     * and finally the left child, resulting in the Nth node.
     * <p>
     * The root node is considered the 1st node. If {@code n} is 1, the method returns
     * the root node itself.
     *
     * @param n the index of the node to traverse to.
     * @return the Nth node in the binary tree, or the root if {@code n} is 1.
     * @since 1.0
     */
    public Node<Integer, T> traverseToNthNode(int n) {
        Node<Integer, T> refNode = root;
        // If n is 0, return null
        if (n == 0) return null;
        // if n is 1, return the root node
        if (n == 1) return refNode;

        // Convert the decimal index 'n' to binary representation
        ArrayList<String> binaryPriority = new ArrayList<>(List.of(Integer.toBinaryString(n).split("")));
        binaryPriority.remove(0); // Remove the leading digit from binary representation

        // Traverse the binary tree based on the binary representation of 'n'
        for (String bin : binaryPriority) {
            if (bin.equals("0")) refNode = refNode.getLeft();
            else refNode = refNode.getRight();
        }
        return refNode;
    }

    /**
     * Adds the given {@code item} into this priority queue.
     * <p>
     * This method inserts a new item into the priority queue, maintaining the heap property.
     * If the priority queue is empty, the item becomes the root. Otherwise, the method
     * determines the appropriate position for the new item based on the binary representation
     * of the last node's priority. The method then updates the necessary data structures
     * and performs a bubbling-up operation to maintain the heap property.
     *
     * @param item the item to add to the priority queue.
     * @since 1.0
     */
    @Override
    public void offer(T item) {
        // Get the priority of the last node in the heap
        int lastNodePriority = size();

        // If the priority queue is empty, set the item as the root and increment the size
        if (size() == 0) {
            root.setValue(item);
            size++;
            return;
        }

        // Initialize references to the root and create a new node with incremented priority
        Node<Integer, T> refNode = root;
        Node<Integer, T> newNode = new Node<>(lastNodePriority + 1, item, refNode);
        lastInsert = newNode;

        // Update priority maps and node indexes
        priorityMap.put(newNode, lastNodePriority + 1);
        mirrorMap.put(newNode, newNode);
        nodeIndexes.add(newNode);

        // Extract binary representation of the last node's priority
        ArrayList<String> binaryPriority = new ArrayList<>(List.of(Integer.toBinaryString(lastNodePriority).split("")));

        // If the last bit is '0', traverse right; otherwise, traverse left
        if (binaryPriority.get(binaryPriority.size() - 1).equals("0")) {
            for (int i = 1; i <= binaryPriority.size() - 2; i++) {
                if (binaryPriority.get(i).equals("0")) refNode = refNode.getLeft();
                else refNode = refNode.getRight();
            }
            // Set the new node as the right child of the last node
            newNode.setParent(refNode);
            refNode.setRight(newNode);
        } else {
            // Find the last '0' in the binary representation and traverse right
            int lastLeft = binaryPriority.lastIndexOf("0");
            if (lastLeft != -1) {
                for (int i = 1; i < lastLeft; i++) {
                    if (binaryPriority.get(i).equals("0")) refNode = refNode.getLeft();
                    else refNode = refNode.getRight();
                }
                refNode = refNode.getRight();
            }
            // Traverse left until finding an empty spot and set the new node
            while (refNode.getLeft() != null) {
                refNode = refNode.getLeft();
            }
            newNode.setParent(refNode);
            refNode.setLeft(newNode);
        }

        // Increment the size and perform a bubbling-up operation
        size++;
        bubbleUp(newNode);
    }

    /**
     * Returns the number of items in the priority queue.
     *
     * @return the number of items in the priority queue.
     * @since 1.0
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     * Returns the item of greatest priority in the priority queue.
     *
     * @return the item of greatest priority in the priority queue.
     * @since 1.0
     */
    @Override
    public T peek() {
        return root.getValue();
    }

    /**
     * Returns and removes the item of greatest priority in the priority queue.
     * <p>
     * This method retrieves and removes the item with the highest priority from the priority queue,
     * maintaining the heap property. If the priority queue is empty, the method returns null.
     * <p>
     * If the priority queue has only one element, it is removed, and the root value is set to null,
     * reducing the size to zero.
     * <p>
     * For a priority queue with more than one element, the method first determines the last node's
     * priority. It then removes the last node, updates priority maps, node indexes, and mirror maps.
     * The last node's value is assigned to the root, and the last node's parent is adjusted to
     * maintain the heap property. Finally, a bubbling-down operation is performed to restore the
     * heap property.
     *
     * @return the item of greatest priority in the priority queue, or null if the queue is empty.
     * @since 1.0
     */
    @Override
    public T poll() {
        // If the priority queue is empty, return null
        if (size() == 0) return null;

        // Retrieve the item with the highest priority (peek)
        T returnValue = peek();

        // If there is only one element, remove it and set the root value to null
        if (size() == 1) {
            size--;
            root.setValue(null);
            return returnValue;
        }

        // Get the priority of the last node in the heap
        int lastNodePriority = size();

        // Initialize references to the root and the last node
        Node<Integer, T> refNode = root;
        Node<Integer, T> lastNode = traverseToNthNode(lastNodePriority);

        // Remove the last node and update data structures
        priorityMap.remove(lastNode);
        nodeIndexes.remove(lastNodePriority - 1);
        mirrorMap.remove(lastNode);

        // Set the root value to the last node's value
        refNode.setValue(lastNode.getValue());

        // Get the parent node of the last node
        Node<Integer, T> parentNode = lastNode.getParent();

        // Adjust the parent's child reference based on the last node's priority

        if (lastNodePriority % 2 == 1) parentNode.setRight(null);
        else parentNode.setLeft(null);

        // Decrement the size and perform a bubbling-down operation
        size--;
        bubbleDown(refNode);

        return returnValue;
    }

    /**
     * Compares two nodes based on their priorities.
     * <p>
     * This compare method accounts for the desired max or min heap state.
     * This is achieved through the ternary operator and the 1,-1.
     *
     * @param node1 the first node to compare.
     * @param node2 the second node to compare.
     * @return a negative integer, zero, or a positive integer as the first node is less than,
     * equal to, or greater than the second node.
     * @since 1.0
     */
    public int compare(Node<Integer, T> node1, Node<Integer, T> node2) {
        return (((maxState) ? 1 : -1) * comparator.compare(node1.getValue(), node2.getValue()));
    }


    /**
     * Updates the priority of the given item, ensuring its position in the priority queue is adjusted
     * based on its new priority.
     * <p>
     * Assumes all other items' priorities in this Priority Queue have not changed.
     *
     * @param item the item whose priority has been updated.
     * @since 1.0
     */
    @Override
    public void updatePriority(T item) {
        Node<Integer, T> checkNode = new Node<>(null, item, null);
        Node<Integer, T> refNode = mirrorMap.get(checkNode);
        bubbleDown(refNode);
        bubbleUp(refNode);
    }

    /**
     * Swaps the values of two nodes.
     *
     * @param node1 the first node to swap.
     * @param node2 the second node to swap.
     * @since 1.0
     */
    private void swap(Node<Integer, T> node1, Node<Integer, T> node2) {
        T node1Value = node1.getValue();
        T node2Value = node2.getValue();
        node1.setValue(node2Value);
        node2.setValue(node1Value);
    }

    /**
     * Moves the given node up in the heap until it satisfies the heap property.
     *
     * @param curNode the node to bubble up in the heap.
     * @since 1.0
     */
    private void bubbleUp(Node<Integer, T> curNode) {
        if (curNode.getParent() != null && compare(curNode, curNode.getParent()) > 0) {
            swap(curNode, curNode.getParent());
            bubbleUp(curNode.getParent());
        }
    }

    /**
     * Moves the given node down in the heap until it satisfies the heap property.
     *
     * @param curNode the node to bubble down in the heap.
     * @since 1.0
     */
    private void bubbleDown(Node<Integer, T> curNode) {
        if (curNode.getLeft() == null) {
            // then we know curNode has no children, so we can just end
            return;
        } else if (curNode.getRight() == null) {
            // then we know curNode has exactly one child, just its left
            // so we just need to determine if we need to swap to the left
            if (compare(curNode, curNode.getLeft()) < 0) {
                swap(curNode, curNode.getLeft());
                bubbleDown(curNode.getLeft());
            }
        } else {
            // then we know that curNode has both a left and right child
            // so we first have to determine which child is of greater priority
            // then determine if we have to swap with that child
            if (compare(curNode.getLeft(), curNode.getRight()) > 0) {
                if (compare(curNode, curNode.getLeft()) < 0) {
                    swap(curNode, curNode.getLeft());
                    bubbleDown(curNode.getLeft());
                }
            } else {
                if (compare(curNode, curNode.getRight()) < 0) {
                    swap(curNode, curNode.getRight());
                    bubbleDown(curNode.getRight());
                }
            }
        }
    }

}
