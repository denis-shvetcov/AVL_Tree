import com.sun.source.tree.Tree;
import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class main {
    public static void main(String[] args) {

NavigableSet<Integer> set = new TreeSet<>();
        for (int i = 0; i < 30; i++) {
            set.add(i);
        }

    NavigableSet<Integer> tree = new AVLTree<>();
        for (int i = 0; i < 30; i++) {
            tree.add(i);
        }


        NavigableSet sub2 = tree.subSet(6,false,25,true);

        System.out.println(sub2.lower(50));



    }

}
