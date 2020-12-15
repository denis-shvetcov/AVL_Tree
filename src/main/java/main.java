import com.sun.source.tree.Tree;
import org.w3c.dom.ls.LSOutput;

import java.security.spec.RSAOtherPrimeInfo;
import java.util.*;

public class main {
    public static void main(String[] args) {

        NavigableSet<Integer> set = new TreeSet<>();


        NavigableSet<Integer> tree = new AVLTree<>();
        for (int i = 0; i < 10; i++) {
            tree.add(i*5);
        }
        Iterator iterator = tree.iterator();

//        Random rand = new Random();
        System.out.println(tree.floor(15));
//
//        NavigableSet<Integer> head = tree.headSet(35,false);
//        System.out.println(head.lower(39));

    }

}
