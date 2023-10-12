import java.util.*;

public class LinkedList<E> 
        extends AbstractList<E>
        implements List<E>, Iterable<E>, Cloneable {

    public static class Node<F> {
        F data;
        Node<F> next;
        Node<F> prev;
        private LinkedList<F> parent;

        public Node(F data) {
            this(data, null, null);
        }

        public Node(F item, Node<F> nextNode, Node<F> prevNode){
            data = item;
            next = nextNode;
            prev = prevNode;
        }

        public F getData() {
            return data;
        }

        public void setNext(Node<F> next) {
            this.next = next;
        }

        public Node<F> getNext() {
            return next;
        }

        public void setPrev(Node<F> prev) {
            this.prev = prev;
        }

        public Node<F> getPrev() {
            return prev;
        }

        public void setData(F data) {
            this.data = data;
        }

        public void setParent(LinkedList<F> parent) {
            this.parent = parent;
        }

        public LinkedList<F> getParent() {
            return parent;
        }
    }

    public static class LLIterator<E>
            implements ListIterator<E> {

        private Node<E> current;  // Node class is assumed to be previously defined.
        private int index;

        public LLIterator(int index, Node<E> head) {
            this.current = head;
            this.index = index;
        }
        /**
         * Returns {@code true} if this list iterator has more elements when
         * traversing the list in the forward direction. (In other words,
         * returns {@code true} if {@link #next} would return an element rather
         * than throwing an exception.)
         *
         * @return {@code true} if the list iterator has more elements when
         * traversing the list in the forward direction
         */
        @Override
        public boolean hasNext() {
            if (current == null) {
                return false;
            }
            return current.getNext() != null;
        }

        /**
         * Returns the next element in the list and advances the cursor position.
         * This method may be called repeatedly to iterate through the list,
         * or intermixed with calls to {@link #previous} to go back and forth.
         * (Note that alternating calls to {@code next} and {@code previous}
         * will return the same element repeatedly.)
         *
         * @return the next element in the list
         */
        @Override
        public E next() {
            Node<E> next = this.current.getNext();
            this.index = hasNext() ? (this.index + 1) : this.index;
            E dataLoad = hasNext() ? next.getData() : null;
            this.current = hasNext() ? next : this.current;
            return dataLoad;
        }

        /**
         * Returns {@code true} if this list iterator has more elements when
         * traversing the list in the reverse direction.  (In other words,
         * returns {@code true} if {@link #previous} would return an element
         * rather than throwing an exception.)
         *
         * @return {@code true} if the list iterator has more elements when
         * traversing the list in the reverse direction
         */
        @Override
        public boolean hasPrevious() {
            if (current == null) {
                return false;
            }
            return current.getPrev() != null;
        }

        /**
         * Returns the previous element in the list and moves the cursor
         * position backwards.  This method may be called repeatedly to
         * iterate through the list backwards, or intermixed with calls to
         * {@link #next} to go back and forth.  (Note that alternating calls
         * to {@code next} and {@code previous} will return the same
         * element repeatedly.)
         *
         * @return the previous element in the list
         */
        @Override
        public E previous() {
            Node<E> prev = this.current.getPrev();
            this.index = hasPrevious() ? (this.index - 1) : this.index;
            E dataLoad = hasPrevious() ? prev.getData() : null;
            this.current = hasPrevious() ? prev : this.current;
            return dataLoad;
        }

        /**
         * Returns the index of the element that would be returned by a
         * subsequent call to {@link #next}. (Returns list size if the list
         * iterator is at the end of the list.)
         *
         * @return the index of the element that would be returned by a
         * subsequent call to {@code next}, or list size if the list
         * iterator is at the end of the list
         */
        @Override
        public int nextIndex() {
            return hasNext() ? (index + 1) : index;
        }

        /**
         * Returns the index of the element that would be returned by a
         * subsequent call to {@link #previous}. (Returns -1 if the list
         * iterator is at the beginning of the list.)
         *
         * @return the index of the element that would be returned by a
         * subsequent call to {@code previous}, or -1 if the list
         * iterator is at the beginning of the list
         */
        @Override
        public int previousIndex() {
            return hasPrevious() ? -1 : (index - 1);
        }

        /**
         * Removes from the list the last element that was returned by {@link
         * #next} or {@link #previous} (optional operation).  This call can
         * only be made once per call to {@code next} or {@code previous}.
         * It can be made only if {@link #add} has not been
         * called after the last call to {@code next} or {@code previous}.
         *
         * @throws UnsupportedOperationException if the {@code remove}
         *                                       operation is not supported by this list iterator
         * @throws IllegalStateException         if neither {@code next} nor
         *                                       {@code previous} have been called, or {@code remove} or
         *                                       {@code add} have been called after the last call to
         *                                       {@code next} or {@code previous}
         */
        @Override
        public void remove() {
            Node<E> next = this.current.getNext();
            Node<E> prev = this.current.getPrev();
            if (next != null) {
                next.setPrev(prev);

            }
            if (prev != null) {
                prev.setNext(next);

            }
            this.current.getParent().size--;
        }

        /**
         * Replaces the last element returned by {@link #next} or
         * {@link #previous} with the specified element (optional operation).
         * This call can be made only if neither {@link #remove} nor {@link
         * #add} have been called after the last call to {@code next} or
         * {@code previous}.
         *
         * @param e the element with which to replace the last element returned by
         *          {@code next} or {@code previous}
         * @throws UnsupportedOperationException if the {@code set} operation
         *                                       is not supported by this list iterator
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws IllegalArgumentException      if some aspect of the specified
         *                                       element prevents it from being added to this list
         * @throws IllegalStateException         if neither {@code next} nor
         *                                       {@code previous} have been called, or {@code remove} or
         *                                       {@code add} have been called after the last call to
         *                                       {@code next} or {@code previous}
         */
        @Override
        public void set(E e) {
            this.current.setData(e);
        }

        /**
         * Inserts the specified element into the list (optional operation).
         * The element is inserted immediately before the element that
         * would be returned by {@link #next}, if any, and after the element
         * that would be returned by {@link #previous}, if any.  (If the
         * list contains no elements, the new element becomes the sole element
         * on the list.)  The new element is inserted before the implicit
         * cursor: a subsequent call to {@code next} would be unaffected, and a
         * subsequent call to {@code previous} would return the new element.
         * (This call increases by one the value that would be returned by a
         * call to {@code nextIndex} or {@code previousIndex}.)
         *
         * @param e the element to insert
         * @throws UnsupportedOperationException if the {@code add} method is
         *                                       not supported by this list iterator
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws IllegalArgumentException      if some aspect of this element
         *                                       prevents it from being added to this list
         */
        @Override
        public void add(E e) {
            Node<E> newNode = new Node<>(e);
            if (this.current.getPrev() != null) {
                this.current.getPrev().setNext(newNode);
            }
            this.current.setPrev(newNode);
            newNode.setNext(this.current);
            this.current.getParent().size++;

        }
    }

    private int size;
    private Node<E> head;
    private Node<E> tail;

    public LinkedList() {
        size = 0;
        head = null;
        tail = null;
    }

    public LinkedList(Collection<? extends E> list) {
        this();
        addAll(list);
    }


    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<E> clone() {
        LinkedList<E> clone;
        try {
            clone = (LinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        clone.head = null;
        clone.tail = null;
        clone.size = 0;
        for (Node<E> node = head; node!= null; node = node.getNext()) {
            clone.add(node.getData());
        }
        return clone;
    }

    public int size(){
        return size;
    }

    public int indexFetch(Object obj){
        return indexOf(obj);
    }

    public int indexFetch(Node<E> node) {
        int curIndex = 0;
        for(Node<E> node_i = head; node_i != node; node_i = node_i.getNext()) {
            curIndex++;
        }
        return curIndex;
    }

    public Node<E> nodeFetch(int index){
        Node<E> fetchNode = this.head;

        for(int i = 0; i < index; i++){
            fetchNode = fetchNode.getNext();
        }

        return fetchNode;
    }


    public boolean add(E item){
        addFirst(item);
        return true;
    }

    public void addFirst(E item){
        Node<E> newNode = new Node<>(item, head, null);
        newNode.setParent(this);
        if (size == 0){
            tail = newNode;
        } else {
            head.setPrev(newNode);
        }
        size++;
        head = newNode;
    }

    public void addLast(E item){
        Node<E> newNode = new Node<>(item, null, tail);
        newNode.setParent(this);
        if (size == 0){
            head = newNode;
        } else {
            tail.setNext(newNode);
        } 
        size++;
        tail = newNode;
    }

    // this will add item into the list at the given index, meaning everything
    // after will be now 1 index later.
    public void add(int index, E item){
        // If index is 0, let's just use addFirst, which updates head accordingly
        if (index == 0) {
            addFirst(item);
            return;
        } if (index == size) {
            addLast(item);
            return;
        }
        Node<E> curr = nodeFetch(index);

        Node<E> newNode = new Node<>(item, curr.getPrev(), curr);
        newNode.setParent(this);
        newNode.getPrev().setNext(newNode);
        newNode.getNext().setPrev(newNode);

        size++;
    }

    /**
     * @return
     */
    public E peek() {
        return peekFirst();
    }

    /**
     * @return
     */
    public E peekFirst() {
        final Node<E> node = head;
        return (node == null) ? null : node.getData();
    }

    /**
     * @return
     */
    public E peekLast() {
        final Node<E> node = tail;
        return (node == null) ? null : node.getData();
    }

    /**
     * @return
     */
    public E poll() {
        return pollFirst();
    }

    /**
     * @return
     */
    public E pollFirst() {
        final Node<E> node = head;
        E dataLoad = (node == null) ? null : node.getData();
        remove();
        return dataLoad;
    }

    /**
     * @return
     */
    public E pollLast() {
        final Node<E> node = tail;
        E dataLoad = (node == null) ? null : node.getData();
        removeLast();
        return dataLoad;
    }

    public E get(int index){
        return nodeFetch(index).getData();
    }


    /**
     *
     * @param index
     * @return the item stored at the given index
     */
    public E remove(int index){
        Node<E> node = nodeFetch(index);
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
        return new LLIterator<>(index, this.head);
    }

    public E remove(){
        E dataLoad = head.getData();

        head = head.getNext();
        if (size > 1) {
            head.setPrev(null);
        } else {
            head = null;
        }

        size--;
        return dataLoad;
    }

    public E remove(Node<E> node){
        E dataLoad = node.getData();
        Node<E> prev = node.getPrev();
        Node<E> next = node.getNext();

        if (prev!= null) {
            prev.setNext(next);
        }
        else {
            this.head = next;
        }
        if (next!= null) {
            next.setPrev(prev);
        }
        else {
            this.tail = prev;
        }
        size--;
        return dataLoad;
    }

    public E removeLast(){
        E dataLoad = tail.getData();

        tail = tail.getPrev();
        if (size > 1) {
            tail.setNext(null);
        } else {
            head = null;
        }

        size--;
        return dataLoad;
    }

    /**
     * @param obj element to be removed from this list, if present
     * @return
     */
    public boolean removeFirstOccurrence(Object obj) {
        if (obj == null) {
            for (Node<E> node = head; node != null; node = node.getNext()) {
                if (node.getData() == null) {
                    remove(node);
                    return true;
                }
            }
        } else {
            for (Node<E> node = head; node != null; node = node.getNext()) {
                if (obj.equals(node.getData())) {
                    remove(node);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param obj element to be removed from this list, if present
     * @return
     */
    public boolean removeLastOccurrence(Object obj) {
        if (obj == null) {
            for (Node<E> node = tail; node != null; node = node.getPrev()) {
                if (node.getData() == null) {
                    remove(node);
                    return true;
                }
            }
        } else {
            for (Node<E> node = tail; node != null; node = node.getPrev()) {
                if (obj.equals(node.getData())) {
                    remove(node);
                    return true;
                }
            }
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass()!= obj.getClass()) return false;

        LinkedList<E> list = (LinkedList<E>) obj;

        if (size!= list.size()) return false;
        Node<E> curNode = list.head;
        for (Node<E> node = head; node != null; node = node.getNext()) {
            if (!node.getData().equals(curNode.getData())) return false;
            curNode = curNode.getNext();
        }

        return true;
    }
}