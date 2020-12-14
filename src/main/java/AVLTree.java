
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class AVLTree<T extends Comparable<T>> implements NavigableSet<T> {

    private Node<T> root;
    private int size = 0;

    private static class Node<T> {
        private T value;
        private int height;
        private Node<T> left;
        private Node<T> right;

        private Node(T value) {
            this.value = value;
            this.height = 1;
            this.left = null;
            this.right = null;
        }
    }

    T getRootValue() {
        return root.value;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(value, root);
    }

    private Node<T> find(T value, Node<T> current) {
        int comparision = current.value.compareTo(value);
        if (comparision == 0)
            return current;
        else if (comparision > 0) {
            if (current.left != null) return find(value, current.left);
            else return current;
        } else {
            if (current.right != null) return find(value, current.right);
            else return current;
        }
    }

    @Override
    public boolean contains(Object value) {
        T val = (T) value;
        Node<T> closest = find(val);
        return closest != null && closest.value.compareTo(val) == 0;
    }

    @Override
    public boolean add(T value) {
        if (contains(value)) {
            return false;
        }
        if (root == null) {
            root = new Node<T>(value);
            size++;
            return true;
        }
        Node<T> result = add(root, value);
        if (result != null) {
            size++;
            return true;
        } else return false;
    }

    @Override
    public boolean remove(Object o) {
        T value = (T) o;
        if (root == null || !contains(value)) return false;
        Node<T> node = remove(root, value);
        if (value == root.value) root = node;
        size--;
        return true;
    }

    private Node<T> add(Node<T> parent, T value) {
        if (parent == null) {
            return new Node<T>(value);
        }
        int comparision = parent.value.compareTo(value);
        if (comparision > 0) {
            parent.left = add(parent.left, value);
        } else if (comparision < 0) {
            parent.right = add(parent.right, value);
        }
        fixHeight(parent);
        return balance(parent);
    }

    private Node<T> balance(Node<T> toBalance) {
        int balanceFactor = balanceFactor(toBalance);
        fixHeight(toBalance);
        if (balanceFactor == 2) {
            if (balanceFactor(toBalance.right) < 0) toBalance.right = rotateRight(toBalance.right);
            toBalance = rotateLeft(toBalance);
        } else if (balanceFactor == -2) {
            if (balanceFactor(toBalance.left) > 0) toBalance.left = rotateLeft(toBalance.left);
            toBalance = rotateRight(toBalance);
        }
        return toBalance;
    }

    private int balanceFactor(Node<T> check) {
        return height(check.right) - height(check.left);
    }

    private int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }

    private Node<T> rotateLeft(Node<T> pivot) {
        boolean rootFlag = pivot.value == root.value; //  если вращаем корень, то он поменяется
        Node<T> pivotRight = pivot.right;
        pivot.right = pivotRight.left;
        pivotRight.left = pivot;
        fixHeight(pivot);
        fixHeight(pivotRight);
        if (rootFlag) root = pivotRight;
        return pivotRight;
    }

    private Node<T> rotateRight(Node<T> pivot) {
        boolean rootFlag = false;//  если вращаем корень, то он поменяется
        if (pivot.value == root.value) rootFlag = true;
        Node<T> pivotLeft = pivot.left;
        pivot.left = pivotLeft.right;
        pivotLeft.right = pivot;
        fixHeight(pivot);
        fixHeight(pivotLeft);
        if (rootFlag) root = pivotLeft;
        return pivotLeft;
    }

    private void fixHeight(Node<T> toFix) { //высота ущла равна высоте наибольшего поддерева + 1
        int leftTreeH = height(toFix.left);
        int rightTreeH = height(toFix.right);
        toFix.height = (Math.max(leftTreeH, rightTreeH)) + 1;
    }

    private Node<T> removeMin(Node<T> current) {
        if (current.left == null) return current.right;
        current.left = removeMin(current.left);
        return balance(current);
    }

    private Node<T> remove(Node<T> current, T value) {
        if (current == null) return null;
        if (value.compareTo(current.value) < 0)
            current.left = remove(current.left, value);
        else if (value.compareTo(current.value) > 0)
            current.right = remove(current.right, value);
        else {
            Node<T> left = current.left;
            Node<T> right = current.right;
            current = null;
            if (right == null) return left; // если правого поддерева нет, то присваиваем предку левое поддерево
            Node<T> min = findMin(right);
            min.right = removeMin(right);
            min.left = left;
            return balance(min);
        }
        return balance(current);
    }

    private Node<T> findMin(Node<T> node) {
        return root == null ? null : node.left == null ? node : findMin(node.left);
    }

    private Node<T> findMax(Node<T> node) {
        return root == null ? null : node.right == null ? node : findMax(node.right);
    }

    private Node<T> highest(Deque<Node<T>> list) {
        int height = -1;
        Node<T> res = null;
        for (Node<T> el : list)
            if (el.height > height) {
                height = el.height;
                res = el;
            }
        return res;
    }

    private class AVLIterator implements Iterator<T> {
        Deque<Node<T>> stack = new LinkedList<>();
        T lastReturned = null;
        boolean isDescending;

        private AVLIterator(boolean isDescending) {
            this.isDescending = isDescending;
            fillStacks(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public T next() {
            if (hasNext()) {
                Node<T> toReturn = stack.pollLast();
                if (!isDescending) {
                    fillStacks(toReturn.right);
                    lastReturned = toReturn.value;
                    return toReturn.value;
                } else {
                    fillStacks(toReturn.left);
                    lastReturned = toReturn.value;
                    return toReturn.value;
                }
            } else throw new NoSuchElementException();
        }

        private void fillStacks(Node<T> node) {
            if (node != null) {
                stack.add(node);
                if (!isDescending)
                    fillStacks(node.left);
                else
                    fillStacks(node.right);
            }
        }

        private void fillStackAfterRemove(Node<T> node, T stop) {
            if (node != null) {
                if (!isDescending) {
                    if (node.value.compareTo(stop) < 0) {
                        fillStackAfterRemove(node.right, stop);
                    } else {
                        stack.add(node);
                        fillStackAfterRemove(node.left, stop);
                    }
                } else {
                    if (node.value.compareTo(stop) > 0) {
                        fillStackAfterRemove(node.left, stop);
                    } else {
                        stack.add(node);
                        fillStackAfterRemove(node.right, stop);
                    }
                }
            }
        }

        @Override
        public void remove() {
            if (lastReturned != null) {
                if (!stack.isEmpty()) {
                    AVLTree.this.remove(lastReturned);
                    stack.clear();
                    T stop = lastReturned;
                    lastReturned = null;
                    fillStackAfterRemove(root, stop);
                } else {
                    AVLTree.this.remove(lastReturned);
                    lastReturned = null;
                }
            } else throw new IllegalStateException();
        }
    }

    @Override
    public T lower(T t) {
        if (root == null) return null;
        Node<T> current = root;
        T biggest = root.value.compareTo(t) >= 0 ? findMin(root).value : root.value;
        T currentVal;
        while (current != null) {
            currentVal = current.value;
            int compareToT = currentVal.compareTo(t);
            if (compareToT < 0 && currentVal.compareTo(biggest) > 0) biggest = currentVal;
            if (compareToT >= 0)
                current = current.left;
            else
                current = current.right;
        }
        if (biggest.compareTo(t) >= 0)
            return null;
        else
            return biggest;
    }

    @Override
    public T higher(T t) {

        if (root == null) return null;
        Node<T> current = root;
        T least = root.value.compareTo(t) <= 0 ? findMax(root).value : root.value;
        T currentVal;
        while (current != null) {
            currentVal = current.value;
            int compareToT = currentVal.compareTo(t);

            if (compareToT > 0 && currentVal.compareTo(least) < 0) least = currentVal;
            if (compareToT > 0)
                current = current.left;
            else
                current = current.right;
        }
        if (least.compareTo(t) <= 0)
            return null;
        else
            return least;
    }

    @Override
    public T floor(T t) {
        if (root == null) return null;
        if (t == null) return last();
        Node<T> current = root;
        T biggest = root.value.compareTo(t) > 0 ? findMin(root).value : root.value;
        T currentVal;
        while (current != null) {
            currentVal = current.value;
            int compareToT = currentVal.compareTo(t);

            if (compareToT <= 0 && currentVal.compareTo(biggest) > 0) biggest = currentVal;
            if (compareToT >= 0)
                current = current.left;
            else
                current = current.right;
        }
        if (biggest.compareTo(t) > 0)
            return null;
        else
            return biggest;
    }

    @Override
    public T ceiling(T t) {
        if (root == null) return null;
        if (t == null) return first();

        Node<T> current = root;
        T least = root.value.compareTo(t) < 0 ? findMax(root).value : root.value;
        T currentVal;
        while (current != null) {
            currentVal = current.value;
            int compareToT = currentVal.compareTo(t);

            if (compareToT >= 0 && currentVal.compareTo(least) < 0) least = currentVal;
            if (compareToT >= 0)
                current = current.left;
            else
                current = current.right;
        }
        if (least.compareTo(t) < 0)
            return null;
        else
            return least;
    }

    @Override
    public T pollFirst() {
        try {
            T val = first();
            remove(val);
            return val;
        } catch (NoSuchElementException exc) {
            return null;
        }
    }

    @Override
    public T pollLast() {
        try {
            T val = last();
            remove(val);
            return val;
        } catch (NoSuchElementException exc) {
            return null;
        }
    }

    @Override
    public T first() {
        if (size != 0)
            return findMin(root).value;
        else throw new NoSuchElementException();
    }

    @Override
    public T last() {
        if (size != 0)
            return findMax(root).value;
        else throw new NoSuchElementException();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new AVLIterator(false);
    }

    @NotNull
    @Override
    public NavigableSet<T> descendingSet() {
        return new SubSet(null, null, null, null, true);
    }

    @NotNull
    @Override
    public Iterator<T> descendingIterator() {
        return new AVLIterator(true);
    }

    @NotNull
    @Override
    public NavigableSet<T> subSet(T t, boolean b, T e1, boolean b1) {
        return new SubSet(t, b, e1, b1, false);
    }

    @NotNull
    @Override
    public NavigableSet<T> headSet(T t, boolean b) {
        return new SubSet(null, null, t, b, false);
    }

    @NotNull
    @Override
    public NavigableSet<T> tailSet(T t, boolean b) {
        return new SubSet(t, b, null, null, false);
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T t, T e1) {
        return new SubSet(t, true, e1, false, false);
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T t) {
        return new SubSet(null, null, t, false, false);
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T t) {
        return new SubSet(t, true, null, null, false);
    }


    @Override
    public Comparator<? super T> comparator() {
        return (Comparator<T>) (o, t1) -> o.compareTo(t1);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        Object[] elements = new Object[size];
        Iterator<T> iter = iterator();
        for (int i = 0; i < size; i++) {
            elements[i] = iter.next();
        }
        return elements;
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        T1[] elements = null;
        if (t1s.length < size) elements = (T1[]) new Object[size];
        Iterator<T> iter = iterator();
        if (elements != null) {
            for (int i = 0; i < size; i++) {
                elements[i] = (T1) iter.next();
            }
            return elements;
        } else {
            for (int i = 0; i < size; i++) {
                t1s[i] = (T1) iter.next();
            }
        }
        return t1s;
    }


    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    public int height() {
        return height(root);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object element : collection) {
            if (!contains(element)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        int oldSize = size;
        for (Object element : collection) {
            add((T) element);
        }
        return size > oldSize;
    }

    @NotNull
    @Override
    public boolean retainAll(Collection<?> collection) {
        int oldSize = size;
        Set<Object> retain = new HashSet<>();
        Set<Object> remove = new HashSet<>();
        for (Object element : collection) {
            if (contains(element)) retain.add(element);
        }
        for (T element : this) {
            if (!retain.contains(element)) remove.add(element);
        }
        return oldSize > size;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        int oldSize = size;
        for (Object element : collection) {
            remove(element);
        }
        return oldSize > size;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    class SubSet implements NavigableSet<T> {
        T from;
        T to;
        Boolean fromIncluded;
        Boolean toIncluded;

        boolean descending;

        public SubSet(T from, Boolean fromIncluded, T to, Boolean toIncluded, boolean descending) {
            if (!descending) {
                if (to == null || from == null || to.compareTo(from) > 0) {
                    this.from = from;
                    this.to = to;
                    this.fromIncluded = fromIncluded;
                    this.toIncluded = toIncluded;
                } else throw new IllegalArgumentException();
            } else {
                if (to == null || from == null || to.compareTo(from) < 0) {
                    this.from = to;
                    this.to = from;
                    this.fromIncluded = toIncluded;
                    this.toIncluded = fromIncluded;
                } else throw new IllegalArgumentException();
            }
            this.descending = descending;
        }

        private int countSize() {
            Iterator<T> iter = iterator();
            int counter = 0;
            while (iter.hasNext()) {
                iter.next();
                counter++;
            }
            return counter;
        }

        @Override
        public int size() {
            return countSize();
        }

        @Override
        public boolean isEmpty() {
            return countSize() == 0;
        }

        @Override
        public boolean contains(Object o) {
            if (isValid((T) o))
                return AVLTree.this.contains((T) o);
            else
                return false;
        }

        @Override
        public T lower(T val) {
            if (!descending)
                return privLower(val);
            else
                return privHigher(val);
        }

        @Override
        public T higher(T val) {
            if (!descending)
                return privHigher(val);
            else
                return privLower(val);
        }

        @Override
        public T floor(T val) {
            if (contains(val)) {
                return val;
            }
            if (!descending)
                return privFloor(val);
            else
                return privCeiling(val);
        }

        @Override
        public T ceiling(T val) {
            if (contains(val)) {
                return val;
            }
            if (!descending)
                return privCeiling(val);
            else
                return privFloor(val);
        }

        private T privLower(T val) {
            int compare = to == null ? -1 : val.compareTo(to);
            if (compare > 0) {
                if (toIncluded != null && toIncluded)
                    return to;
                else
                    return AVLTree.this.lower(to);
            } else if (compare == 0) {
                return AVLTree.this.lower(to);
            } else {
                compare = from == null ? 1 : val.compareTo(from);
                if (compare <= 0)
                    return null;
                else return AVLTree.this.lower(val);
            }
        }

        private T privHigher(T val) {
            int compare = from == null ? 1 : val.compareTo(from);
            if (compare < 0) {
                if (fromIncluded != null && fromIncluded)
                    return from;
                else
                    return AVLTree.this.higher(from);
            } else if (compare == 0) {
                return AVLTree.this.higher(from);
            } else {
                compare = to == null ? -1 : val.compareTo(to);
                if (compare >= 0)
                    return null;
                else return AVLTree.this.higher(val);
            }
        }

        private T privFloor(T val) {
            int compare = to == null ? -1 : val.compareTo(to);
            if (compare >= 0) {
                if (toIncluded != null && toIncluded)
                    return to;
                else
                    return AVLTree.this.lower(to);
            } else {
                compare = from == null ? 1 : val.compareTo(from);
                if (compare < 0)
                    return null;
                else if (compare == 0 && fromIncluded != null && fromIncluded)
                    return from;
                else return AVLTree.this.lower(val);
            }
        }

        private T privCeiling(T val) {
            int compare = from == null ? 1 : val.compareTo(from);
            if (compare <= 0) {
                if (fromIncluded != null && fromIncluded)
                    return from;
                else
                    return AVLTree.this.higher(from);
            } else {
                compare = to == null ? -1 : val.compareTo(to);
                if (compare > 0)
                    return null;
                else {
                    if (compare == 0 && toIncluded != null && toIncluded)
                        return to;
                    else return AVLTree.this.higher(val);
                }
            }
        }

        @Override
        public T first() {
            if (countSize() == 0) throw new NoSuchElementException();
            if (from == null && !descending)
                return AVLTree.this.first();
            else if (to == null && descending)
                return AVLTree.this.last();
            else if (!descending)
                return ceiling(from);
            else return privFloor(to);
        }

        @Override
        public T last() {
            if (countSize() == 0) throw new NoSuchElementException();
            if (to == null && !descending)
                return AVLTree.this.last();
            else if (from == null && descending)
                return AVLTree.this.first();
            else if (!descending)
                return floor(to);
            else return privCeiling(from);
        }

        @Override
        public T pollFirst() {
            try {
                T val = first();
                remove(val);
                return val;
            } catch (NoSuchElementException exc) {
                return null;
            }
        }

        @Override
        public T pollLast() {
            try {
                T val = floor(to);
                remove(val);
                return val;
            } catch (NoSuchElementException exc) {
                return null;
            }
        }

        @Override
        public Iterator<T> iterator() {
            return new SubSetIterator(descending);
        }

        @Override
        public boolean add(T val) {
            if (isValid(val)) {
                boolean res = AVLTree.this.add(val);
                return res;
            } else throw new IllegalArgumentException();
        }

        @NotNull
        @Override
        public Object[] toArray() {
            Object[] elements = new Object[countSize()];
            Iterator<T> iter = new SubSetIterator(descending);
            for (int i = 0; i < elements.length; i++) {
                elements[i] = iter.next();
            }
            return elements;
        }

        @NotNull
        @Override
        public T[] toArray(@NotNull Object[] objects) {

            T[] elements = null;
            int size = countSize();
            if (objects.length < size) elements = (T[]) new Object[size];
            Iterator<T> iter = new SubSetIterator(descending);
            if (elements != null) {
                for (int i = 0; i < size; i++) {
                    elements[i] = iter.next();
                }
                return elements;
            } else {
                for (int i = 0; i < size; i++) {
                    objects[i] = iter.next();
                }
            }
            return (T[]) objects;
        }

        @NotNull
        @Override
        public NavigableSet<T> descendingSet() {
            return new SubSet(to, toIncluded, from, fromIncluded, !descending);
        }

        public boolean isInRange(T fromV, Boolean fromInc, T toV, Boolean toInc) {
            if (from != null && fromV != null) {

                int fromComp = fromV.compareTo(from);
                if (fromComp < 0)
                    return false;
                else if ((fromComp == 0 && fromInc == true && fromIncluded == false))
                    return false;
            }
            if (to != null && toV != null) {
                int toComp = toV.compareTo(to);
                if (toComp > 0)
                    return false;
                else if (toComp == 0 && toIncluded == false && toInc == true)
                    return false;
            }
            return true;
        }

        @NotNull
        @Override
        public Iterator<T> descendingIterator() {
            return new SubSetIterator(!descending);
        }

        @NotNull
        @Override
        public NavigableSet<T> subSet(T o, boolean b, T e1, boolean b1) {
            boolean isInRange = !descending ? isInRange(o, b, e1, b1) : isInRange(e1, b1, o, b);
            if (isInRange)
                return new SubSet(o, b, e1, b1, descending);
            else throw new IllegalArgumentException();
        }

        @NotNull
        @Override
        public NavigableSet<T> headSet(T o, boolean b) {
            boolean isInRange = !descending ? isInRange(null, null, o, b) : isInRange(o, b, null, null);
            if (isInRange) {
                if (!descending)
                    return new SubSet(from, fromIncluded, o, b, descending);
                else
                    return new SubSet(to, toIncluded, o, b, descending);
            } else throw new IllegalArgumentException();
        }

        @NotNull
        @Override
        public NavigableSet<T> tailSet(T o, boolean b) {
            boolean isInRange = !descending ? isInRange(o, b, null, null) : isInRange(null, null, o, b);
            if (isInRange) {
                if (!descending)
                    return new SubSet(o, b, to, toIncluded, descending);
                else
                    return new SubSet(o, b, from, fromIncluded, descending);
            } else throw new IllegalArgumentException();
        }

        @NotNull
        @Override
        public SortedSet<T> subSet(T o, T e1) {
            boolean isInRange = !descending ? isInRange(o, true, e1, false) : isInRange(e1, false, o, true);
            if (isInRange)
                return new SubSet(o, true, e1, false, descending);
            else throw new IllegalArgumentException();
        }

        @NotNull
        @Override
        public SortedSet<T> headSet(T o) {
            boolean isInRange = !descending ? isInRange(null, null, o, false) : isInRange(o, false, null, null);
            if (isInRange) {
                if (!descending)
                    return new SubSet(from, fromIncluded, o, false, descending);
                else
                    return new SubSet(to, toIncluded, o, false, descending);
            } else throw new IllegalArgumentException();
        }

        @NotNull
        @Override
        public SortedSet<T> tailSet(T o) {
            boolean isInRange = !descending ? isInRange(o, true, null, null) : isInRange(null, null, o, true);
            if (isInRange) {
                if (!descending)
                    return new SubSet(o, true, to, toIncluded, descending);
                else
                    return new SubSet(o, true, from, fromIncluded, descending);
            } else throw new IllegalArgumentException();
        }

        @Override
        public Comparator comparator() {
            return (Comparator<T>) (o, t1) -> o.compareTo(t1);
        }

        @Override
        public boolean remove(Object o) {
            T val = (T) o;
            if (isValid(val)) {
                boolean res = AVLTree.this.remove(val);
                return res;
            } else return false;
        }

        @Override
        public boolean addAll(Collection collection) {
            int oldSize = countSize();
            for (Object element : collection) {
                add((T) element);
            }
            return oldSize < countSize();
        }

        @Override
        public boolean retainAll(Collection collection) {
            int oldSize = AVLTree.this.size;
            Set<Object> retain = new HashSet<>();
            Set<Object> remove = new HashSet<>();
            for (Object element : collection) {
                if (contains(element)) retain.add(element);
            }
            for (T element : this) {
                if (!retain.contains(element)) AVLTree.this.remove(element);
            }
            removeAll(remove);
            return oldSize < AVLTree.this.size;
        }

        @Override
        public boolean removeAll(Collection collection) {
            int oldSize = AVLTree.this.size;
            for (Object element : collection) {
                if (isValid((T) element)) remove(element);
            }
            return oldSize > AVLTree.this.size;
        }

        @Override
        public boolean containsAll(Collection collection) {
            for (Object element : collection) {
                T val = (T) element;
                if (!contains(val)) return false;
            }
            return true;
        }

        @Override
        public void clear() {
            Iterator<T> iter = iterator();
            while (iter.hasNext()) {
                iter.next();
                iter.remove();
            }
        }

        private boolean isValid(T val) {
            if (val == null)
                return false;
            else
                return isBelowCeil(val) && isAboveFloor(val);
        }

        private boolean isBelowCeil(T value) {
            if (to == null) return true;
            int comparision = value.compareTo(to);
            if (comparision == 0) {
                if (toIncluded)
                    return true;
                else return false;
            }
            if (comparision < 0) return true;
            else return false;
        }

        private boolean isAboveFloor(T value) {
            if (from == null) return true;
            int comparision = value.compareTo(from);
            if (comparision == 0) {
                if (fromIncluded)
                    return true;
                else return false;
            }
            if (comparision > 0) return true;
            else return false;
        }

        private class SubSetIterator implements Iterator {
            Deque<Node<T>> stack = new LinkedList<>();
            T lastReturned = null;
            boolean isDecsending;

            private SubSetIterator(boolean isDecsending) {
                this.isDecsending = isDecsending;
                if (root != null) {
                    if (!isDecsending) {
                        if (isValid(root.value))
                            fillStack(root);
                        else if (!isBelowCeil(root.value)) {
                            fillStack(root.left);
                        } else {
                            fillStack(root.right);
                        }
                    } else {
                        if (isValid(root.value))
                            fillStack(root);
                        else if (isBelowCeil(root.value)) {
                            fillStack(root.right);
                        } else {
                            fillStack(root.left);
                        }
                    }
                }
            }

            private void fillStackAfterRemove(Node<T> node, T stop) {
                if (node != null) {
                    if (!isDecsending) {
                        if (node.value.compareTo(stop) <= 0) {
                            fillStackAfterRemove(node.right, stop);
                            return;
                        }
                        if (isBelowCeil(node.value)) stack.add(node);
                        fillStackAfterRemove(node.left, stop);
                    } else {
                        if (node.value.compareTo(stop) >= 0) {
                            fillStackAfterRemove(node.left, stop);
                            return;
                        }
                        if (isAboveFloor(node.value)) stack.add(node);
                        fillStackAfterRemove(node.right, stop);
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    Node<T> toReturn = stack.pollLast();
                    if (!isDecsending) {
                        if (isBelowCeil(toReturn.value)) fillStack(toReturn.right);
                        lastReturned = toReturn.value;
                        return toReturn.value;
                    } else {
                        if (isAboveFloor(toReturn.value)) fillStack(toReturn.left);
                        lastReturned = toReturn.value;
                        return toReturn.value;
                    }
                } else {
                    throw new NoSuchElementException();
                }
            }

            private void fillStack(Node<T> node) {
                if (node != null) {
                    if (!isDecsending) {
                        if (!isAboveFloor(node.value)) {
                            fillStack(node.right);
                            return;
                        }
                        if (isBelowCeil(node.value)) stack.add(node);
                        fillStack(node.left);
                    } else {
                        if (!isBelowCeil(node.value)) {
                            fillStack(node.left);
                            return;
                        }
                        if (isAboveFloor(node.value)) stack.add(node);
                        fillStack(node.right);
                    }
                }
            }

            @Override
            public void remove() {
                if (lastReturned != null) {
                    if (!stack.isEmpty()) {
                        AVLTree.this.remove(lastReturned);
                        Node<T> start = highest(stack);
                        stack.clear();
                        T stop = lastReturned;
                        lastReturned = null;
                        fillStackAfterRemove(start, stop);
                    } else {
                        AVLTree.this.remove(lastReturned);
                        lastReturned = null;
                    }
                } else throw new IllegalStateException();
            }
        }
    }

}

