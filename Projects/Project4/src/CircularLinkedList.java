/*
One thing I should make clear is that I wrote the project before I wrote this class.
Thus, I recreated a bunch of features from the java.util implementation of the `LinkedList` class.
This is to ensure maximum compatibility with my code.

On a side note, I learned how to use ternary operators to make this work so that's cool.
 */

import java.util.*;

/**
 * The `LinkedList` class represents a doubly-linked list data structure.
 * It extends `AbstractList` and implements various list-related interfaces,
 * such as `List`, `Iterable`, and `Cloneable`.
 * This class allows you to create and manipulate a list of elements of type `E`.
 * The list supports adding, removing, and retrieving elements,
 * and it provides methods for iterating through the list.
 *
 * @param <E> The type of elements stored in the list.
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class CircularLinkedList<E>
        extends AbstractList<E>
        implements List<E>, Iterable<E>, Cloneable {

    private int size;
    private Node<E> head;
    private Node<E> tail;
    private String name;
    private String identifier;

    public CircularLinkedList() {
        size = 0;
        head = null;
        tail = null;
    }

    /**
     * Creates a new `LinkedList` containing elements from the specified collection.
     * The elements are added in the order they appear in the collection.
     *
     * @param list A collection of elements to add to the `LinkedList`.
     */
    public CircularLinkedList(Collection<? extends E> list) {
        this();
        addAll(list);
    }

    public static void main(String[] args) throws InterruptedException {

        CircularLinkedList<Integer> test = new CircularLinkedList<>();
        LinkedList<Integer> test2 = new LinkedList<>();

        for (int i = 5; i > 0; i--) {
            test.addFirst(i);
            test2.addFirst(i);
        }
        for (int i = 6; i < 10; i++) {
            test.addLast(i);
            test2.addLast(i);
        }
        System.out.println(test);
        System.out.println(test.size());
        System.out.println(test2);
        System.out.println(test2.size());

        Integer dataLoad = test.peekFirst();
        for (Node<Integer> node = test.head; node != null; node = node.getNext()) {
            System.out.println(node.getData());
            Thread.sleep(300);
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Creates a new `LinkedList` with no elements.
     */
    @SuppressWarnings("unchecked")
    @Override
    public CircularLinkedList<E> clone() {
        CircularLinkedList<E> clone;
        try {
            clone = (CircularLinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        clone.head = null;
        clone.tail = null;
        clone.size = 0;

        for (CircularLinkedList.Node<E> node = this.head; node != this.tail; node = node.getNext()) {
            clone.addLast(node.getData());
        }
        clone.addLast(this.tail.getData());
        return clone;
    }

    /**
     * Returns the size of the `LinkedList`, which is the number of elements it contains.
     *
     * @return The size of the `LinkedList`.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the index of the first occurrence of the specified object in the `LinkedList`,
     * or -1 if the object is not found.
     *
     * @param obj The object to search for.
     * @return The index of the first occurrence of the object, or -1 if not found.
     */
    public int indexFetch(Object obj) {
        return indexOf(obj);
    }

    public int getFrequency(E obj) {
        int output = 0;
        for (CircularLinkedList.Node<E> node = head; node != tail; node = node.getNext()) {
            if (node.getData().equals(obj)) output++;
        }
        if (tail.getData().equals(obj)) output++;
        return output;
    }

    /**
     * Returns the index of the specified node within the `LinkedList`.
     * This method finds the index of the given node in the list.
     *
     * @param node The node whose index is to be determined.
     * @return The index of the specified node, or -1 if the node is not found in the list.
     */
    public int indexFetch(CircularLinkedList.Node<E> node) {
        int curIndex = 0;
        for (CircularLinkedList.Node<E> node_i = head; node_i != node; node_i = node_i.getNext()) {
            curIndex++;
        }
        return curIndex;
    }

    /**
     * Returns the node at the specified index in the `LinkedList`.
     * This method retrieves the node at the given index.
     *
     * @param index The index of the node to retrieve.
     * @return The node at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of the valid range.
     */
    public CircularLinkedList.Node<E> nodeFetch(int index) {
        CircularLinkedList.Node<E> fetchNode = this.head;

        for (int i = 0; i < index; i++) {
            fetchNode = fetchNode.getNext();
        }

        return fetchNode;
    }

    /**
     * Adds an element to the beginning of the `LinkedList`.
     * This method adds the specified item to the front of the list.
     *
     * @param item The item to add to the list.
     * @return Always returns true to indicate success.
     */
    public boolean add(E item) {
        addFirst(item);
        return true;
    }

    public void addFirst(E item) {
        Node<E> newNode = new Node<>(item, head, tail);
        newNode.setParent(this);
        if (size == 0) {
            tail = newNode;
            newNode.setNext(newNode);
            newNode.setPrev(newNode);
        } else {
            head.setPrev(newNode);
            tail.setNext(newNode);
        }
        size++;
        head = newNode;
    }

    public void addLast(E item) {
        Node<E> newNode = new Node<>(item, head, tail);
        newNode.setParent(this);
        if (size == 0) {
            head = newNode;
            newNode.setNext(newNode);
            newNode.setPrev(newNode);
        } else {
            tail.setNext(newNode);
            head.setPrev(newNode);
        }
        size++;
        tail = newNode;
    }

    /**
     * Inserts an element at the specified index in the `LinkedList`.
     * This method inserts the specified item at the given index.
     *
     * @param index The index at which to insert the item.
     * @param item  The item to insert into the list.
     */
    public void add(int index, E item) {
        if (index == 0) {
            addFirst(item);
            return;
        }
        if (index == size) {
            addLast(item);
            return;
        }
        CircularLinkedList.Node<E> curr = nodeFetch(index);

        CircularLinkedList.Node<E> newNode = new CircularLinkedList.Node<>(item, curr, curr.getPrev());
        newNode.setParent(this);
        newNode.getPrev().setNext(newNode);
        newNode.getNext().setPrev(newNode);

        size++;
    }

    /**
     * Retrieves the first element in the `LinkedList` without removing it.
     * This method is equivalent to calling `peekFirst()`.
     *
     * @return The first element in the list or null if the list is empty.
     */
    public E peek() {
        return peekFirst();
    }

    /**
     * Retrieves the first element in the `LinkedList` without removing it.
     *
     * @return The first element in the list or null if the list is empty.
     */
    public E peekFirst() {
        final CircularLinkedList.Node<E> node = head;
        return (node == null) ? null : node.getData();
    }

    /**
     * Retrieves the last element in the `LinkedList` without removing it.
     *
     * @return The last element in the list or null if the list is empty.
     */
    public E peekLast() {
        final CircularLinkedList.Node<E> node = tail;
        return (node == null) ? null : node.getData();
    }

    /**
     * Retrieves and removes the first element in the `LinkedList`. This method is equivalent to calling `pollFirst()`.
     *
     * @return The first element in the list or null if the list is empty.
     */
    public E poll() {
        return pollFirst();
    }

    /**
     * Retrieves and removes the first element in the `LinkedList`.
     *
     * @return The first element in the list or null if the list is empty.
     */
    public E pollFirst() {
        final CircularLinkedList.Node<E> node = head;
        E dataLoad = (node == null) ? null : node.getData();
        remove();
        return dataLoad;
    }

    /**
     * Retrieves and removes the last element in the `LinkedList`.
     *
     * @return The last element in the list or null if the list is empty.
     */
    public E pollLast() {
        final CircularLinkedList.Node<E> node = tail;
        E dataLoad = (node == null) ? null : node.getData();
        removeLast();
        return dataLoad;
    }

    /**
     * Removes and returns an element from the beginning of the `LinkedList`.
     * <p>
     * This method removes and returns from the front of the list.
     * Equivalent to calling `pollFirst()`, or `poll`.
     *
     * @return
     */
    public E pop() {
        return pollFirst();
    }

    @Override
    public E set(int index, E item) {
        final CircularLinkedList.Node<E> node = nodeFetch(index);
        if (node == null) throw new IndexOutOfBoundsException();
        E dataLoad = node.getData();
        node.setData(item);
        return dataLoad;
    }

    /**
     * Adds an element to the beginning of the `LinkedList`.
     * <p>
     * This method adds the specified item to the front of the list.
     * Equivalent to calling `addFirst(item)`, or `add(item.toString())`.
     *
     * @param input
     * @return
     */
    public boolean push(E input) {
        return add(input);
    }

    /**
     * Returns the element at the specified index in the `LinkedList`.
     *
     * @param index The index of the element to retrieve.
     * @return The element at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of the valid range.
     */
    public E get(int index) {
        return nodeFetch(index).getData();
    }

    /**
     * Removes and returns the element at the specified index in the `LinkedList`.
     *
     * @param index The index of the element to remove.
     * @return The element that was removed.
     * @throws IndexOutOfBoundsException If the index is out of the valid range.
     */
    public E remove(int index) {
        CircularLinkedList.Node<E> node = nodeFetch(index);
        E dataLoad = node.getData();
        remove(node);
        return dataLoad;
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * @return a list iterator over the elements in this list (in proper
     * sequence)
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return new CircularLinkedList.LLIterator<>(index, this.head);
    }

    /**
     * Retrieves and removes the first element in the `LinkedList`.
     *
     * @return The first element in the list or null if the list is empty.
     */

    /**
     * Removes and returns the element stored in the specified node in the `LinkedList`.
     *
     * @param node The node containing the element to be removed.
     * @return The element that was removed.
     */
    public E remove(CircularLinkedList.Node<E> node) {
        E dataLoad = node.getData();
        CircularLinkedList.Node<E> prev = node.getPrev();
        CircularLinkedList.Node<E> next = node.getNext();

        if (prev != null) {
            prev.setNext(next);
        } else {
            this.head = next;
        }
        if (next != null) {
            next.setPrev(prev);
        } else {
            this.tail = prev;
        }
        size--;
        return dataLoad;
    }

    /**
     * Removes and returns the first element in the `LinkedList`.
     *
     * @return The first element that was removed.
     */
    public E remove() {
        E dataLoad = head.getData();

        head = head.getNext();
        if (size > 1) {
            head.setPrev(tail);
            tail.setNext(head);
        } else {
            head = null;
            tail = null;
        }
        size--;
        return dataLoad;
    }

    /**
     * Retrieves and removes the last element in the `LinkedList`.
     *
     * @return The last element in the list or null if the list is empty.
     */
    public E removeLast() {
        E dataLoad = tail.getData();

        tail = tail.getPrev();
        if (size > 1) {
            tail.setNext(head);
            head.setPrev(tail);
        } else {
            head = null;
            tail = null;
        }

        size--;
        return dataLoad;
    }

    /**
     * Removes the first occurrence of the specified element from the `LinkedList`, if present.
     *
     * @param obj The element to be removed from this list.
     * @return `true` if the element was found and removed, `false` otherwise.
     */
    public boolean removeFirstOccurrence(Object obj) {
        if (obj == null) {
            for (CircularLinkedList.Node<E> node = head; node != null; node = node.getNext()) {
                if (node.getData() == null) {
                    remove(node);
                    return true;
                }
            }
        } else {
            for (CircularLinkedList.Node<E> node = head; node != null; node = node.getNext()) {
                if (obj.equals(node.getData())) {
                    remove(node);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes the last occurrence of the specified element from the `LinkedList`, if present.
     *
     * @param obj The element to be removed from this list.
     * @return `true` if the element was found and removed, `false` otherwise.
     */
    public boolean removeLastOccurrence(Object obj) {
        if (obj == null) {
            for (CircularLinkedList.Node<E> node = tail; node != null; node = node.getPrev()) {
                if (node.getData() == null) {
                    remove(node);
                    return true;
                }
            }
        } else {
            for (CircularLinkedList.Node<E> node = tail; node != null; node = node.getPrev()) {
                if (obj.equals(node.getData())) {
                    remove(node);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Compares the `LinkedList` with another object for equality.
     *
     * @param obj The object to compare with this `LinkedList`.
     * @return `true` if the objects are equal, `false` otherwise.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CircularLinkedList<E> list = (CircularLinkedList<E>) obj;

        if (size != list.size()) return false;
        CircularLinkedList.Node<E> curNode = list.head;
        for (CircularLinkedList.Node<E> node = head; node != tail; node = node.getNext()) {
            if (!node.getData().equals(curNode.getData())) return false;
            curNode = curNode.getNext();
        }

        return true;
    }

    /**
     * A node within the `LinkedList`.
     * Each node contains an element of type `F` and maintains references to the next and previous nodes in the list.
     * This inner class is used to construct the doubly-linked list.
     *
     * @param <F> The type of elements stored in the node.
     */
    public static class Node<F> implements Cloneable {
        F data;
        CircularLinkedList.Node<F> next;
        CircularLinkedList.Node<F> prev;
        private CircularLinkedList<F> parent;

        /**
         * Creates a new node with the specified data and no next or previous nodes.
         *
         * @param data The data to be stored in the node.
         */
        public Node(F data) {
            this(data, null, null);
        }

        /**
         * Creates a new node with the specified data, next, and previous nodes.
         *
         * @param item     The data to be stored in the node.
         * @param nextNode The node that follows this node.
         * @param prevNode The node that precedes this node.
         */
        public Node(F item, CircularLinkedList.Node<F> nextNode, CircularLinkedList.Node<F> prevNode) {
            data = item;
            next = nextNode;
            prev = prevNode;
        }

        /**
         * Returns the data stored in this node.
         *
         * @return The data in this node.
         */
        public F getData() {
            return data;
        }

        /**
         * Sets the data stored in this node.
         *
         * @param data The new data to be stored in the node.
         */
        public void setData(F data) {
            this.data = data;
        }

        /**
         * Returns the node that follows this node.
         *
         * @return The next node.
         */
        public CircularLinkedList.Node<F> getNext() {
            return next;
        }

        /**
         * Sets the node that follows this node.
         *
         * @param next The new next node.
         */
        public void setNext(CircularLinkedList.Node<F> next) {
            this.next = next;
        }

        /**
         * Returns the node that precedes this node.
         *
         * @return The previous node.
         */
        public CircularLinkedList.Node<F> getPrev() {
            return prev;
        }

        /**
         * Sets the node that precedes this node.
         *
         * @param prev The new previous node.
         */
        public void setPrev(CircularLinkedList.Node<F> prev) {
            this.prev = prev;
        }

        /**
         * Returns the parent `LinkedList` to which this node belongs.
         *
         * @return The parent `LinkedList`.
         */
        public CircularLinkedList<F> getParent() {
            return parent;
        }

        /**
         * Sets the parent `LinkedList` to which this node belongs.
         *
         * @param parent The new parent `LinkedList`.
         */
        public void setParent(CircularLinkedList<F> parent) {
            this.parent = parent;
        }

        public Node<F> clone() {
            Node<F> clone;
            try {
                clone = (Node<F>) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            return clone;
        }
    }

    /**
     * An iterator for the `LinkedList` that allows you to traverse the list in both forward and backward directions.
     * This iterator is returned by the `listIterator` method.
     *
     * @param <E> The type of elements stored in the `LinkedList`.
     */
    public static class LLIterator<E>
            implements ListIterator<E> {

        private CircularLinkedList.Node<E> current;  //Node class is assumed to be previously defined.
        private int index;

        /**
         * Creates a new `LLIterator` with the specified starting index and the head node of the `LinkedList`.
         *
         * @param index The starting index for the iterator.
         * @param head  The head node of the `LinkedList`.
         */
        public LLIterator(int index, CircularLinkedList.Node<E> head) {
            this.current = head;
            this.index = index;
        }

        /**
         * Returns {@code true} if {@link #next} returns an element not null.
         *
         * @return {@code true} if there is a next element in the list, {@code false} otherwise.
         */
        @Override
        public boolean hasNext() {
            if (current == null) {
                return false;
            }
            return current.getNext() != null;
        }

        /**
         * Returns the next element in the list and increments the cursor index by 1.
         *
         * @return the next element in the list
         */
        @Override
        public E next() {
            CircularLinkedList.Node<E> next = this.current.getNext();
            if (hasNext()) {
                this.index = (this.index == this.current.getParent().size() - 1) ? 0 : this.index + 1;
            }
            E dataLoad = hasNext() ? next.getData() : null;
            this.current = hasNext() ? next : this.current;
            return dataLoad;
        }

        /**
         * Returns true if {@link #previous} returns an element not null.
         *
         * @return true if there is a next element in the list, false otherwise.
         */
        @Override
        public boolean hasPrevious() {
            if (current == null) {
                return false;
            }
            return current.getPrev() != null;
        }

        /**
         * Returns the previous element in the list and decrements the cursor index by 1.
         *
         * @return the previous element in the list
         */
        @Override
        public E previous() {
            CircularLinkedList.Node<E> prev = this.current.getPrev();
            if (hasPrevious()) {
                this.index = (this.index == 0) ? this.current.getParent().size() - 1 : this.index - 1;
            }
            E dataLoad = hasPrevious() ? prev.getData() : null;
            this.current = hasPrevious() ? prev : this.current;
            return dataLoad;
        }

        /**
         * Returns the index of the element that would be returned by {@link #next}.
         *
         * @return the index of the element that would be returned  {@code next}.
         */
        @Override
        public int nextIndex() {
            return hasNext() ? ((this.index == this.current.getParent().size() - 1) ? 0 : this.index + 1) : index;
        }

        /**
         * Returns the index of the element that would be returned by {@link #previous}.
         *
         * @return the index of the element that would be returned  {@code previous}.
         */
        @Override
        public int previousIndex() {
            return hasPrevious() ? ((this.index == 0) ? this.current.getParent().size() - 1 : this.index - 1) : index;
        }

        /**
         * Removes the last element returned by {@link #next} or {@link #previous}.
         * This call can be made once per {@code next} or {@code previous}.
         * Usage mutually exclusive with {@link #add}, should only call one per
         * {@code next} or {@code previous} usage.
         * <p>
         * Use replace if combined usage necessary.
         */
        @Override
        public void remove() {
            CircularLinkedList.Node<E> next = this.current.getNext();
            CircularLinkedList.Node<E> prev = this.current.getPrev();
            if (next != null) {
                next.setPrev(prev);
            }
            if (prev != null) {
                prev.setNext(next);
            }
            this.current.getParent().size--;
        }

        /**
         * Replaces the last element (current node) returned by {@link #next} or {@link #previous}
         * with the specified element.
         * Similarly, mutually exclusive with {@link #add} and {@link #remove}, should only
         * call one per {@code next} or {@code previous} usage.
         *
         * @param e the element to replace with.
         */
        @Override
        public void set(E e) {
            this.current.setData(e);
        }

        /**
         * Adds a node containing e between the node returned by {@link #next} and the current node.
         * Similarly, mutually exclusive with {@link #set} and {@link #remove}, should only
         * call one per {@code next} or {@code previous} usage.
         *
         * @param e the element to add in.
         */
        @Override
        public void add(E e) {
            CircularLinkedList.Node<E> newNode = new CircularLinkedList.Node<>(e);
            if (this.current.getPrev() != null) {
                this.current.getPrev().setNext(newNode);
            }
            this.current.setPrev(newNode);
            newNode.setNext(this.current);
            this.current.getParent().size++;

        }
    }
}
