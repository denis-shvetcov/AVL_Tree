
import java.util.*;

public class AVLTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {

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

    public T getRootValue() {return root.value;}

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
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public SortedSet<T> subSet(T t, T e1) {
        return null;
    }

    @Override
    public SortedSet<T> headSet(T t) {
        return null;
    }

    @Override
    public SortedSet<T> tailSet(T t) {
        return null;
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

    public boolean contains(T value) {
        Node<T> closest = find(value);
        return closest != null && closest.value.compareTo(value) == 0;
    }

    public boolean insert(T value) {
        if (contains(value)) {
            return false;
        }
        if (root == null) {
            root = new Node<T>(value);
            size++;
            return true;
        }
        insert(root, value);
        size++;
        return true;
    }

    private Node<T> insert(Node<T> parent, T value) {
        if (parent == null) {
            return new Node<T>(value);
        }
        int comparision = parent.value.compareTo(value);
        if (comparision > 0) {
            parent.left = insert(parent.left, value);
        } else {
            parent.right = insert(parent.right, value);
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

    public boolean remove(T value) {
        if (root == null || !contains(value)) return false;
        Node<T> node = remove(root, value);
        if (value == root.value) root = node;
        size--;
        return true;
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
            if (lastReturned!= null) {
                AVLTree.this.remove(lastReturned);
                lastReturned = null;
            }
            else throw new IllegalStateException();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new AVLIterator();
    }

    @Override
    public int size() {
        return size;
    }


}
