
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.ls.LSOutput;

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

    @Override
    public T first() {
        return null;
    }

    @Override
    public T last() {
        return null;
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
        return node.left == null ? node : findMin(node.left);
    }

    private Node<T> findMax(Node<T> node) {
        return node.right == null ? node : findMax(node.right);
    }


    private class AVLIterator implements Iterator<T> {
        Deque<Node<T>> leftStack = new LinkedList<>();
        T lastReturned = null;

        private AVLIterator() {
            fillStacks(root);
        }

        @Override
        public boolean hasNext() {
            return !leftStack.isEmpty();
        }

        @Override
        public T next() {
            if (hasNext()) {
                Node<T> toReturn = leftStack.pollLast();
                fillStacks(toReturn.right);
                lastReturned = toReturn.value;
                return toReturn.value;
            } else throw new NoSuchElementException();
        }

        private void fillStacks(Node<T> node) {
            if (node != null) {
                leftStack.add(node);
                fillStacks(node.left);
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
            if (compareToT > 0)
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
        T val;
        if (root == null)
            return null;
        else
            val = findMin(root).value;
        remove(val);
        return val;
    }

    @Override
    public T pollLast() {
        T val;
        if (root == null)
            return null;
        else
            val = findMax(root).value;
        remove(val);
        return val;
    }

    @Override
    public Iterator<T> iterator() {
        return new AVLIterator();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        return null;
    }

    @Override
    public Iterator<T> descendingIterator() {
        return null;
    }

    @Override
    public NavigableSet<T> subSet(T t, boolean b, T e1, boolean b1) {
        return new SubSet(this, t, b, e1, b1);
    }

    @Override
    public NavigableSet<T> headSet(T t, boolean b) {
        return new SubSet(this, null, null, t, b);
    }

    @Override
    public NavigableSet<T> tailSet(T t, boolean b) {
        return new SubSet(this, t, b, null, null);
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T t, T e1) {
        return new SubSet(this, t, true, e1, false);
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T t) {
        return new SubSet(this, null, null, t, false);
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T t) {
        return new SubSet(this, t, true, null, null);
    }


    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        Object[] elements = new Object[size];
        Iterator<T> iter = iterator();
        for (int i = 0; i < size; i++) {
            elements[i] = iter.next();
        }
        return elements;
    }

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


    public T getRootValue() {
        return root.value;
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
            if (!contains((T) element)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
//        for (Object element : collection) {
//            if (!this.contains(element)) return false;
//        }
        for (Object element : collection) {
//            if (!add((T) element)) return false;
            add((T) element);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
//        for (Object element : collection) {
//            if (!this.contains(element)) return false;
//        }
        for (Object element : this) {
            if (!collection.contains(element)) remove(element);
        }

//        return size == collection.size();
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
//        for (Object element : collection) {
//            if (!this.contains(element)) return false;
//        }
        for (Object element : collection) {
//            if (!remove((T) element)) return false;
            remove(element);
        }
        return true;
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


    class SubSet<D extends T> implements NavigableSet {
        T from;
        T to;
        Boolean fromIncluded;
        Boolean toIncluded;
        int size;
        AVLTree<T> tree;

        public SubSet(AVLTree<T> tree, T from, Boolean fromIncluded, T to, Boolean toIncluded) {
            this.tree = tree;
            this.from = from;
            this.to = to;
            this.fromIncluded = fromIncluded;
            this.toIncluded = toIncluded;
            this.size = countSize();
        }

        private int countSize() {
            Iterator<T> iter = new AVLIterator();
            T val;
            while (iter.hasNext()) {
                val = iter.next();
                if (isValid(val)) size++;
            }
            ;
            return size;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
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
        public D lower(Object o) {
            D val = (D) o;
            if (val.compareTo(from) <= 0)
                return null;
            D result = (D) tree.lower(val);
            if (!isValid(result))
                return null;
            else return result;
        }

        @Override
        public D higher(Object o) {
            D val = (D) o;
            if (val.compareTo(to) >= 0)
                return null;
            D result = (D) tree.higher(val);
            if (!isValid(result))
                return null;
            else return result;
        }

        @Override
        public D floor(Object o) {
            D val = (D) o;
            if (val.compareTo(from) < 0)
                return null;
            D result = (D) tree.floor(val);
            if (!isValid(result))
                return null;
            else return result;
        }

        @Override
        public D ceiling(Object o) {
            D val = (D) o;
            if (val.compareTo(to) > 0)
                return null;
            D result = (D) tree.ceiling(val);
            if (!isValid(result))
                return null;
            else return result;
        }

        @Override
        public D pollFirst() {
            D val = first();
            remove(val);
            return val;
        }

        @Override
        public D pollLast() {
            D val = (D) floor(to);
            remove(val);
            return val;
        }

        @Override
        public Iterator<D> iterator() {
            return new SubSetIterator<D>();
        }

        @Override
        public boolean add(Object o) {
            T val = (T) o;
            if (isValid(val)) {
                size++;
                return tree.add(val);
            } else return false;
        }

        @NotNull
        @Override
        public Object[] toArray() {
            Object[] elements = new Object[size];
            Iterator<D> iter = new SubSetIterator<D>();
            for (int i = 0; i < size; i++) {
                elements[i] = iter.next();
            }
            return elements;
        }

        @NotNull
        @Override
        public Object[] toArray(@NotNull Object[] objects) {
            D[] elements = null;
            if (objects.length < size) elements = (D[]) new Object[size];
            Iterator<D> iter = new SubSetIterator<>();
            if (elements != null) {
                for (int i = 0; i < size; i++) {
                    elements[i] = (D) iter.next();
                }
                return elements;
            } else {
                for (int i = 0; i < size; i++) {
                    objects[i] = (D) iter.next();
                }
            }
            return objects;
        }

        @Override
        public NavigableSet<D> descendingSet() {
            return null;
        }

        @Override
        public Iterator<D> descendingIterator() {
            return null;
        }

        @NotNull
        @Override
        public NavigableSet<D> subSet(Object o, boolean b, Object e1, boolean b1) {
            return new SubSet(tree, (T) o, b, (T) e1, b1);
        }

        @NotNull
        @Override
        public NavigableSet<D> headSet(Object o, boolean b) {
            return new SubSet(tree, null, null, (T) o, b);
        }

        @NotNull
        @Override
        public NavigableSet<D> tailSet(Object o, boolean b) {
            return new SubSet(tree, (T) o, b, null, null);

        }


        @NotNull
        @Override
        public NavigableSet<D> subSet(Object o, Object e1) {
            return new SubSet(tree, (T) o, true, (T) e1, false);
        }

        @NotNull
        @Override
        public SortedSet<D> headSet(Object o) {
            return new SubSet(tree, null, null, (T) o, false);

        }

        @NotNull
        @Override
        public SortedSet<D> tailSet(Object o) {
            return new SubSet(tree, (T) o, true, null, null);

        }

        @Override
        public Comparator comparator() {
            return null;
        }

        @Override
        public D first() {
            return ceiling(from);
        }

        @Override
        public D last() {
            return floor(to);
        }

        @Override
        public boolean remove(Object o) {
            T val = (T) o;
            if (isValid(val)) {
                size--;
                return tree.remove(val);
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
            if (result) size += collection.size();
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
            if (result) size -= collection.size();
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

        private class SubSetIterator<D> implements Iterator {
            Deque<Node<T>> leftStack = new LinkedList<>();
            T lastReturned = null;

            private SubSetIterator() {
                if (isValid(root.value)) {
                    fillStacks(root);
                } else if (!isBelowCeil(root.value)) {
                    fillStacks(root.left);
                } else {
                    fillStacks(root.right);
                }
            }

            @Override
            public boolean hasNext() {
                return !leftStack.isEmpty();
            }

            @Override
            public T next() {
                if (hasNext()) {
                    Node<T> toReturn = leftStack.pollLast();
                    if (isBelowCeil(toReturn.value)) fillStacks(toReturn.right);
                    lastReturned = toReturn.value;
                    return toReturn.value;
                } else throw new NoSuchElementException();
            }

            private void fillStacks(Node<T> node) {
                if (node != null) {
                    if (!isAboveFloor(node.value)) {
                        fillStacks(node.right);
                        return;
                    }
                    if (isBelowCeil(node.value)) leftStack.add(node);
                    fillStacks(node.left);
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

