
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AVLTree<T extends Comparable<T>> implements NavigableSet<T> {

    private Node<T> root;
    private int size = 0;

    private class Node<T> {
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

    T getRootValue() { return root.value; }

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
        add(root, value);
        size++;
        return true;
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
        } else {
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

        @Override
        public void remove() {
            if (lastReturned != null) {
                AVLTree.this.remove(lastReturned);
                lastReturned = null;
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
        T val = first();
        remove(val);
        return val;
    }

    @Override
    public T pollLast() {
        T val = last();
        remove(val);
        return val;
    }

    @Override
    public T first() {
        return findMin(root).value;
    }

    @Override
    public T last() {
        return findMax(root).value;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new AVLIterator(false);
    }

    @NotNull
    @Override
    public NavigableSet<T> descendingSet() {
        return new SubSet(this, null, null, null, null, true);
    }

    @NotNull
    @Override
    public Iterator<T> descendingIterator() {
        return new AVLIterator(true);
    }

    @NotNull
    @Override
    public NavigableSet<T> subSet(T t, boolean b, T e1, boolean b1) {
        return new SubSet(this, t, b, e1, b1, false);
    }

    @NotNull
    @Override
    public NavigableSet<T> headSet(T t, boolean b) {
        return new SubSet(this, null, null, t, b, false);
    }

    @NotNull
    @Override
    public NavigableSet<T> tailSet(T t, boolean b) {
        return new SubSet(this, t, b, null, null, false);
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T t, T e1) {
        return new SubSet(this, t, true, e1, false, false);
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T t) {
        return new SubSet(this, null, null, t, false, false);
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T t) {
        return new SubSet(this, t, true, null, null, false);
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
        for (Object element : this) {
            if (!collection.contains(element)) remove(element);
        }
        return oldSize < size;
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
    public int size() { return size; }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    class SubSet implements NavigableSet<T> {
        T from;
        T to;
        Boolean fromIncluded;
        Boolean toIncluded;
        int size;
        AVLTree<T> tree;
        boolean descending;

        public SubSet(AVLTree<T> tree, T from, Boolean fromIncluded, T to, Boolean toIncluded, boolean descending) {
            this.tree = tree;
            this.from = from;
            this.to = to;
            this.fromIncluded = fromIncluded;
            this.toIncluded = toIncluded;
            this.descending = descending;
            countSize();
        }

        private void countSize() {
            Iterator<T> iter = iterator();

            int counter = 0;
            while (iter.hasNext()) {
                iter.next();
                counter++;
            }
            size = counter;
        }

        @Override
        public int size() {
            countSize();
            return size;
        }

        @Override
        public boolean isEmpty() {
            countSize();
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            if (isValid((T) o))
                return tree.contains((T) o);
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
            if (!descending)
                return privFloor(val);
            else
                return privCeiling(val);
        }

        @Override
        public T ceiling(T val) {
            if (!descending)
                return privCeiling(val);
            else
                return privFloor(val);
        }

        private T privLower(T val) {
            T result = tree.lower(val);
            if (!isValid(result))
                return null;
            else return result;
        }

        private T privHigher(T val) {
            T result = tree.higher(val);
            if (!isValid(result))
                return null;
            else return result;
        }

        private T privFloor(T val) {
            T result = (T) tree.floor(val);
            if (!isValid(result)) {
                if (!isValid(result = tree.lower(val)))
                    return null;
            }
            return result;
        }

        private T privCeiling(T val) {
            T result = tree.ceiling(val);
            if (!isValid(result)) {
                if (!isValid(result = tree.higher(val)))
                    return null;
            }
            return result;
        }

        @Override
        public T first() {
            if (from == to && to == null && !descending)
                return tree.first();
            else if (!descending)
                return ceiling(from);
            else return privFloor(to);
        }

        @Override
        public T last() {
            if (from == to && to == null && !descending)
                return tree.last();
            else if (!descending)
                return floor(to);
            else return privCeiling(from);
        }

        @Override
        public T pollFirst() {
            T val = first();
            remove(val);
            return val;
        }

        @Override
        public T pollLast() {
            T val = floor(to);
            remove(val);
            return val;
        }

        @Override
        public Iterator<T> iterator() {
            return new SubSetIterator(descending);
        }

        @Override
        public boolean add(T val) {
            if (isValid(val)) {
                boolean res = tree.add(val);
                if (res) countSize();
                return res;
            } else return false;
        }

        @NotNull
        @Override
        public Object[] toArray() {
            countSize();
            Object[] elements = new Object[size];
            Iterator<T> iter = new SubSetIterator(descending);
            for (int i = 0; i < size; i++) {
                elements[i] = iter.next();
            }
            return elements;
        }

        @NotNull
        @Override
        public T[] toArray(@NotNull Object[] objects) {
            countSize();
            T[] elements = null;
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
            return new SubSet(tree, null, null, null, null, !descending);
        }

        @NotNull
        @Override
        public Iterator<T> descendingIterator() {
            return new SubSetIterator(!descending);
        }

        @NotNull
        @Override
        public NavigableSet<T> subSet(T o, boolean b, T e1, boolean b1) {
            return new SubSet(tree, o, b, e1, b1, descending);
        }

        @NotNull
        @Override
        public NavigableSet<T> headSet(T o, boolean b) {
            if (!descending)
                return new SubSet(tree, null, null, o, b, descending);
            else
                return new SubSet(tree, o, b, null, null, descending);
        }

        @NotNull
        @Override
        public NavigableSet<T> tailSet(T o, boolean b) {
            if (!descending)
                return new SubSet(tree, o, b, null, null, descending);
            else
                return new SubSet(tree, null, null, o, b, descending);

        }

        @NotNull
        @Override
        public NavigableSet<T> subSet(T o, T e1) {
            return new SubSet(tree, o, true, e1, false, descending);
        }

        @NotNull
        @Override
        public SortedSet<T> headSet(T o) {
            if (!descending)
                return new SubSet(tree, null, null, o, false, descending);
            else
                return new SubSet(tree, o, false, null, null, descending);

        }

        @NotNull
        @Override
        public SortedSet<T> tailSet(T o) {
            if (!descending)
                return new SubSet(tree, (T) o, true, null, null, descending);
            else
                return new SubSet(tree, null, null, (T) o, true, descending);
        }

        @Override
        public Comparator comparator() {
            return (Comparator<T>) (o, t1) -> o.compareTo(t1);
        }

        @Override
        public boolean remove(Object o) {
            T val = (T) o;
            if (isValid(val)) {
                boolean res = tree.remove(val);
                if (res) countSize();
                return res;
            } else return false;
        }

        @Override
        public boolean addAll(Collection collection) {
            T val;
            for (Object element : collection) {
                val = (T) element;
                if (!isValid(val)) return false;
            }
            boolean result = tree.addAll(collection);
            if (result) countSize();
            return result;
        }

        @Override
        public boolean retainAll(Collection collection) {
            T val;
            for (Object element : collection) {
                val = (T) element;
                if (!isValid(val)) return false;
            }
            boolean result = tree.retainAll(collection);
            if (result) size = collection.size();
            return tree.retainAll(collection);
        }

        @Override
        public boolean removeAll(Collection collection) {
            T val;
            for (Object element : collection) {
                val = (T) element;
                if (!isValid(val)) return false;
            }
            boolean result = tree.removeAll(collection);
            if (result) countSize();
            return result;
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
            tree.clear();
            size = 0;
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
                            fillStacks(root);
                        else if (!isBelowCeil(root.value)) {
                            fillStacks(root.left);
                        } else {
                            fillStacks(root.right);
                        }
                    } else {
                        if (isValid(root.value))
                            fillStacks(root);
                        else if (isBelowCeil(root.value)) {
                            fillStacks(root.right);
                        } else {
                            fillStacks(root.left);
                        }
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
                        if (isBelowCeil(toReturn.value)) fillStacks(toReturn.right);
                        lastReturned = toReturn.value;
                        return toReturn.value;
                    } else {
                        if (isAboveFloor(toReturn.value)) fillStacks(toReturn.left);
                        lastReturned = toReturn.value;
                        return toReturn.value;
                    }
                } else throw new NoSuchElementException();
            }

            private void fillStacks(Node<T> node) {
                if (node != null) {
                    if (!isDecsending) {
                        if (!isAboveFloor(node.value)) {
                            fillStacks(node.right);
                            return;
                        }
                        if (isBelowCeil(node.value)) stack.add(node);
                        fillStacks(node.left);
                    } else {
                        if (!isBelowCeil(node.value)) {
                            fillStacks(node.left);
                            return;
                        }
                        if (isAboveFloor(node.value)) stack.add(node);
                        fillStacks(node.right);
                    }
                }
            }

            @Override
            public void remove() {
                if (lastReturned != null) {
                    SubSet.this.remove(lastReturned);
                    lastReturned = null;
                } else throw new IllegalStateException();
            }
        }
    }

}

