import java.util.Iterator;
import java.util.Random;

public class main {
    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();

        tree.insert(20);
        tree.insert(29);
        tree.insert(24);
        tree.insert(45);
        tree.insert(54);
        tree.insert(50);
        tree.insert(61);
        tree.insert(15);
        tree.insert(10);
        tree.insert(28);
        tree.insert(35);

        tree.remove(24);
        tree.remove(50);
        tree.remove(61);
        tree.remove(10);
    }
}
