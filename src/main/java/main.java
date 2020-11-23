import com.sun.source.tree.Tree;
import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class main {
    public static void main(String[] args) {
//        AVLTree<Integer> tree = new AVLTree<>();

//        tree.insert(20);
//        tree.insert(29);
//        tree.insert(24);
//        tree.insert(45);
//        tree.insert(54);
//        tree.insert(50);
//        tree.insert(61);
//        tree.insert(15);
//        tree.insert(10);
//        tree.insert(28);
//        tree.insert(35);
//
//        tree.remove(24);
//        tree.remove(50);
//        tree.remove(61);
//        tree.remove(10);


        AVLTree<Integer> tree = new AVLTree<>();
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
//            tree.add(rand.nextInt(100));
            tree.add(i*3);

        }


        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(6);
        list.add(16);

        Set<Integer> set = new HashSet<>();

        set.add(3);
        set.add(55);
        set.add(12);

        System.out.println(  set.retainAll(list));
        System.out.println(set.size());


    }

}
