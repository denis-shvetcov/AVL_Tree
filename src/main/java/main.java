import com.sun.source.tree.Tree;
import org.w3c.dom.ls.LSOutput;

import java.security.spec.RSAOtherPrimeInfo;
import java.util.*;

public class main {
    public static void main(String[] args) {

        NavigableSet<Integer> set = new TreeSet<>();
//        for (int i = 0; i < 30; i++) {
//            set.add(i);
//        }

        NavigableSet<Integer> tree = new AVLTree<>();
        for (int i = 0; i < 30; i++) {
            tree.add(i);
        }
        Iterator iterator = tree.iterator();

        while (iterator.hasNext()) {
            int val = (int) iterator.next();
            System.out.println(val);
            if (val % 3 == 0) iterator.remove();
        }

        System.out.println();
        System.out.println();
        System.out.println();

        iterator = tree.iterator();

        while (iterator.hasNext()) {
            int val = (int) iterator.next();
            System.out.println(val);
            if (val % 3 == 0) iterator.remove();
        }


    }

}
