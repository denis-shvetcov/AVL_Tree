import com.sun.source.tree.Tree;
import org.w3c.dom.ls.LSOutput;

import java.util.*;

public class main {
    public static void main(String[] args) {

NavigableSet<Integer> set = new TreeSet<>();

        for (int i = 0; i < 30; i++) {
            set.add(i);
        }

//        NavigableSet<Integer> sub = set.subSet(5, true,15,false);
//        NavigableSet<Integer> subSub = sub.headSet(5, true);
//
//        System.out.println(subSub.add(5));

        NavigableSet<Integer> tree = new AVLTree<>();
        for (int i = 0; i < 10; i++) {
            tree.add(i*3);
        }
                NavigableSet<Integer> sub = tree.subSet(5, true,15,false);
        NavigableSet<Integer> subHead = sub.headSet(15,false);
        NavigableSet<Integer> des = subHead.descendingSet();
        NavigableSet<Integer> desSub = des.subSet(13,true,8,false);

        for (int el :
                desSub) {
            System.out.println(el);
        }
        NavigableSet<Integer> desSubSub = desSub.subSet(12,false,9,false);
        desSubSub.first();


    }

}
