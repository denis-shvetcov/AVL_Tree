import org.junit.jupiter.api.Test;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class AVLTreeTest {

    @Test
    void height() {
        AVLTree<Integer> tree = new AVLTree<>();

        tree.add(20);
        assertEquals(1, tree.height());
        assertEquals(20, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(29);
        assertEquals(2, tree.height());
        assertEquals(20, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(24); // большое левое вращение от 20, корень сменился на 24
        assertEquals(2, tree.height());
        assertEquals(24, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(45);
        assertEquals(3, tree.height());
        assertEquals(24, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(54); // дерево перебалансировалось, был совершен левый поворот от 29, сохранилась высота 3
        assertEquals(3, tree.height());
        assertEquals(24, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(50); // перебалансировка, левый поворот от корня = 24, корень сменился на 45
        assertEquals(3, tree.height());
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(61);
        assertEquals(3, tree.height());
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(15);
        assertEquals(4, tree.height()); // увеличилась высота
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(10); // произошла перебалансировка 0 правый поворот от 24
        assertEquals(4, tree.height());
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(28);
        assertEquals(4, tree.height());
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.add(35);
        assertEquals(4, tree.height());
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.remove(24);
        assertEquals(4, tree.height());
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.remove(50);
        assertEquals(4, tree.height());
        assertEquals(45, tree.getRootValue(), "Сменился корень, хотя не должен был.");

        tree.remove(61); //перебалансировка -> новый корень
        assertEquals(4, tree.height());
        assertEquals(28, tree.getRootValue(), "Сменился корень, хотя не должен был.");
    }

    @Test
    void subSet() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> controlSet = new TreeSet<>();
            NavigableSet<Integer> tree = new AVLTree<>();
            for (int j = 0; j < 30; j++) {
                controlSet.add(random.nextInt(100));
            }
            for (int element : controlSet) {
                tree.add(element);
            }
            System.out.println("Контрольный сет: " + controlSet);
            int fromElement = random.nextInt(50);
            int toElement = random.nextInt(50) + 50;
            SortedSet<Integer> subSet = tree.subSet(fromElement, toElement);
            for (int element : controlSet) {
                assertEquals(
                        element >= fromElement && element < toElement, subSet.contains(element),
                        String.format("Элемент %d %s в subSet . Он там %s быть.", element, subSet.contains(element) ? "" : "не",
                                element >= fromElement && element < toElement ? "должен" : "не должен")
                );
                if (element >= fromElement && element < toElement) {
                    assertTrue(
                            subSet.remove(element),
                            "Элемент из subSet не был удален."
                    );
                }
            }
            int validAddition = toElement - 1;
            assertTrue(() -> subSet.add(validAddition), "Элемент, вмещающийся в интервал subSet не был добавлен в него.");

            int invalidAddition = fromElement - 1;
            assertThrows(IllegalArgumentException.class, () -> subSet.add(invalidAddition), "Элемент,не вмещающийся в интервал subSet был успешно добавлен в него.");
        }
    }

    @Test
    void headSet() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> controlSet = new TreeSet<>();
            NavigableSet<Integer> tree = new AVLTree<>();
            for (int j = 0; j < 30; j++) {
                controlSet.add(random.nextInt(100));
            }
            for (int element : controlSet) {
                tree.add(element);
            }
            System.out.println("Контрольный сет: " + controlSet);
            int toElement = random.nextInt(100);
            SortedSet<Integer> headSet = tree.headSet(toElement);
            for (int element : controlSet) {
                assertEquals(
                        element < toElement, headSet.contains(element),
                        String.format("Элемент %d %s в headSet . Он там %s быть.", element, headSet.contains(element) ? "" : "не",
                                headSet.contains(element) ? "" : "не должен")
                );
                if (element < toElement) {
                    assertTrue(
                            headSet.remove(element),
                            "Элемент не был удален из headSet."
                    );
                } else {
                    assertFalse(headSet.remove(element), "Удален элемент, не который не принадлежит headSet");
                }
            }
            int validAddition = toElement - 1;
            assertTrue(headSet.add(validAddition), "Элемент, входящий в интервал headSet не был добавлен");

            int invalidAddition = toElement + 1;
            assertThrows(IllegalArgumentException.class, () -> headSet.add(invalidAddition), "Элемент, не входящий в интервал headSet был добавлен");
        }
    }

    @Test
    void doHeadSetRelationTest() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> tree = new AVLTree<Integer>();
            int toElement = random.nextInt(100);
            NavigableSet<Integer> headSet = tree.headSet(toElement, false);
            var allElementCounter = 0;
            var validElementCounter = 0;
            for (int j = 0; j < 50; j++) {
                int value = random.nextInt(100);
                if (value < toElement) {
                    if (random.nextBoolean()) {
                        if (tree.add(value)) {
                            allElementCounter++;
                            validElementCounter++;
                        }
                        assertTrue(
                                headSet.contains(value),
                                "A headset doesn't contain a valid element of the initial set."
                        );
                    } else {
                        if (headSet.add(value)) {
                            allElementCounter++;
                            validElementCounter++;
                        }
                        assertTrue(
                                tree.contains(value),
                                "The initial set doesn't contain an element of the headset."
                        );
                    }
                } else {
                    if (tree.add(value)) {
                        allElementCounter++;
                    }
                    assertFalse(
                            headSet.contains(value),
                            "A headset contains an illegal element of the initial set."
                    );
                }
            }
            assertEquals(
                    allElementCounter, tree.size(),
                    "The size of the initial set is not as expected."
            );
            assertEquals(
                    validElementCounter, headSet.size(),
                    "The size of the headset is not as expected."
            );

        }
    }

    @Test
    void tailSet() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> tree = new AVLTree<Integer>();
            int toElement = random.nextInt(100);
            NavigableSet<Integer> controlSet = new TreeSet<>();
            for (int j = 0; j < 30; j++) {
                controlSet.add(random.nextInt(100));
            }
            tree.addAll(controlSet);

            System.out.println("Control set: " + controlSet);
            int fromElement = random.nextInt(100);
            NavigableSet<Integer> tailSet = tree.tailSet(fromElement, true);

            for (int element : controlSet) {
                assertEquals(
                        element >= fromElement, tailSet.contains(element),
                        String.format("%d is %s in the tailset when it should %s be", element, tailSet.contains(element) ? "" : "not",
                                tailSet.contains(element) ? "not" : "")
                );
                if (element >= fromElement) {
                    assertTrue(
                            tailSet.remove(element),
                            "An element of the tailset was not removed."
                    );
                } else {
                    assertFalse(tailSet.remove(element), "An illegal argument was passed to remove().");

                }
            }
            int validAddition = fromElement + 1;
            assertTrue(tailSet.add(validAddition), "A valid element wasn't added.");

            int invalidAddition = fromElement - 1;
            assertThrows(IllegalArgumentException.class, () -> tailSet.add(invalidAddition), "An illegal argument was passed to add()");
        }
    }

    @Test
    void doTailSetRelationTest() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> tree = new AVLTree<Integer>();
            int fromElement = random.nextInt(100);
            NavigableSet<Integer> tailSet = tree.tailSet(fromElement, true);

            int allElementCounter = 0;
            int validElementCounter = 0;
            for (int j = 0; j < 50; j++) {
                int value = random.nextInt(100);
                if (value >= fromElement) {
                    if (random.nextBoolean()) {
                        if (tree.add(value)) {
                            allElementCounter++;
                            validElementCounter++;
                        }
                        assertTrue(
                                tailSet.contains(value),
                                "A tailset doesn't contain a valid element of the initial set."
                        );
                    } else {
                        if (tailSet.add(value)) {
                            allElementCounter++;
                            validElementCounter++;
                        }
                        assertTrue(
                                tree.contains(value),
                                "The initial set doesn't contain an element of the tailset."
                        );
                    }
                } else {
                    if (tree.add(value)) {
                        allElementCounter++;
                    }
                    assertFalse(
                            tailSet.contains(value),
                            "A tailset contains an illegal element of the initial set."
                    );
                }
            }
            assertEquals(
                    allElementCounter, tree.size(),
                    "The size of the initial set is not as expected."
            );
            assertEquals(
                    validElementCounter, tailSet.size(),
                    "The size of the tailset is not as expected."
            );
        }
    }

    @Test
    void first() {
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> tree = new AVLTree<>();
            NavigableSet<Integer> treeDes = tree.descendingSet();
            Random rand = new Random();
            int min = 101;
            int max = -1;
            int val = 0;
            for (int j = 0; j < 50; j++) {
                val = rand.nextInt(100);
                tree.add(val);
                if (val > max) max = val;
                if (val < min) min = val;
            }
            assertTrue(tree.first() == min);
            assertTrue(treeDes.first() == max);

            tree.clear();
            int step = rand.nextInt(10) + 1;

            for (int j = 0; j < 50; j++) {
                tree.add(step * j);
            }

            NavigableSet<Integer> subSet1 = tree.subSet(step * 10, true, step * 30, true);
            NavigableSet<Integer> subSet2 = tree.subSet(step * 10, false, step * 30, false);


            NavigableSet<Integer> desSubSet1 = treeDes.subSet(step * 30, true, step * 10, true);
            NavigableSet<Integer> desSubSet2 = treeDes.subSet(step * 30, false, step * 10, false);
            System.out.println(subSet1.first() == null);
            assertTrue(subSet1.first() == step * 10);

            assertTrue(subSet2.first() == step * 11);
            System.out.println(desSubSet1.first());
            System.out.println(step * 49);

            assertTrue(desSubSet1.first() == step * 30);
            assertTrue(desSubSet2.first() == step * 29);
        }
    }

    @Test
    void last() {
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> tree = new AVLTree<>();
            NavigableSet<Integer> treeDes = tree.descendingSet();
            Random rand = new Random();
            int min = 101;
            int max = -1;
            int val = 0;
            for (int j = 0; j < 50; j++) {
                val = rand.nextInt(100);
                tree.add(val);
                if (val > max) max = val;
                if (val < min) min = val;
            }
            assertTrue(tree.last() == max);
            assertTrue(treeDes.last() == min);

            tree.clear();
            int step = rand.nextInt(10) + 1;
            int mid;
            for (int j = 0; j < 50; j++) {
                tree.add(step * j);
            }

            NavigableSet<Integer> subSet1 = tree.subSet(step * 10, true, step * 30, true);
            NavigableSet<Integer> subSet2 = tree.subSet(step * 10, false, step * 30, false);


            NavigableSet<Integer> desSubSet1 = treeDes.subSet(step * 30, true, step * 10, true);
            NavigableSet<Integer> desSubSet2 = treeDes.subSet(step * 30, false, step * 10, true);

            assertTrue(subSet1.last() == step * 30);
            assertTrue(subSet2.last() == step * 29);
            assertTrue(desSubSet1.last() == step * 10);
            assertTrue(desSubSet2.last() == step * 11);
        }
    }

    @Test
    void contains() {
        Random random = new Random();
        Set<Integer> controlSet = new HashSet<>();
        AVLTree<Integer> tree = new AVLTree<>();
        int value;
        for (int i = 0; i < 100; i++) {
            value = random.nextInt(100);
            controlSet.add(value);
            tree.add(value);
        }
        for (int element : controlSet)
            assertTrue(tree.contains(element), "Дерево не содержит " + element);
        assertTrue(tree.size() == controlSet.size(), "В дереве не хватает элементов. Размер дерева: " + tree.size() + ", необходимый размер -  " + controlSet.size());
    }

    @Test
    void insert() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            Set<Integer> controlSet = new HashSet<>();
            for (int j = 0; j < 10; j++) {
                controlSet.add(random.nextInt(100));
            }
            AVLTree<Integer> tree = new AVLTree<>();
            for (int element : controlSet) {
                assertTrue(
                        tree.add(element),
                        "Элемент не был добавлен, хотя должен был."
                );
                assertTrue(
                        tree.contains(element),
                        "В дереве нет добавленного элемента: " + element
                );
                assertTrue(
                        tree.checkInvariant(),
                        "Нарушена инвариантность дерева."
                );
                assertFalse(
                        tree.add(element),
                        "Элемент был добавлен дважды."
                );
            }
            assertEquals(
                    controlSet.size(), tree.size(),
                    "Неверный размер дерева. Должен быть " + controlSet.size() + ", в действительности - " + tree.size()
            );
            for (int element : controlSet) {
                assertTrue(
                        tree.contains(element), "Дерево не содержит элемент " + element + " из контрольного сета."

                );
            }
        }
    }

    @Test
    void remove() {
        Random random = new Random();
        int newNumber;
        AVLTree<Integer> tree;
        for (int i = 0; i < 100; i++) {
            Set<Integer> controlSet = new HashSet<>();

            int removeIndex = random.nextInt(20) + 1;
            var toRemove = 0;
            for (int j = 0; j < 100; j++) {
                newNumber = random.nextInt(100);
                controlSet.add(newNumber);
                if (j == removeIndex) {
                    toRemove = newNumber;
                }
            }
            System.out.println("Исходный набор: " + controlSet);
            tree = new AVLTree<>();
            for (int element : controlSet) {
                tree.add(element);
            }
            controlSet.remove(toRemove);
            System.out.println("Контрольный сет: " + controlSet.toString());
            int expectedSize = tree.size() - 1;
            int maxHeight = tree.height();
            System.out.println("Удаление элемента " + toRemove + " из дерева...");
            assertTrue(
                    tree.remove(toRemove),
                    "Элемент не был удален, хотя должен был."
            );
            assertTrue(
                    !tree.contains(toRemove),
                    "Дерево содержит удаленный элемент."
            );
            assertTrue(
                    tree.checkInvariant(),
                    "После удаления нарушилась инвариантность дерева."
            );
            assertTrue(
                    tree.height() <= maxHeight,
                    "После удаления увеличилась высота дерева."
            );
            assertFalse(
                    tree.remove(toRemove),
                    "Элемент, который удален, был удален повторно."
            );
            assertEquals(
                    expectedSize, tree.size(),
                    "Неверный размер дерева. Должен быть " + expectedSize + ", в действительности - " + tree.size()
            );
            for (int element : controlSet) {
                assertTrue(
                        tree.contains(element),
                        "Дерево не содержит элемент " + element + " из контрольного сета."
                );
            }
            System.out.println("All clear!");
        }
    }

    @Test
    void iteratorTest() {
        Random random = new Random();
        Set<Integer> controlSet;
        AVLTree<Integer> tree;
        for (int i = 0; i < 100; i++) {
            controlSet = new TreeSet<>();
            for (int j = 0; j < 20; j++) {
                controlSet.add(random.nextInt(100));
            }
            System.out.println("Контрольный сет: " + controlSet.toString());
            tree = new AVLTree<>();
            assertFalse(
                    tree.iterator().hasNext(),
                    "Итератор пустого дерева не должен иметь ни один элемент."
            );
            for (int element : controlSet) {
                tree.add(element);
            }
            Iterator<Integer> iterator1 = tree.iterator();
            Iterator<Integer> iterator2 = tree.iterator();
            System.out.println("Проверка, меняет ли hasNext() текущее положение итератора.");
            while (iterator1.hasNext()) {
                assertEquals(
                        iterator2.next(), iterator1.next(),
                        "Вызов AVLIterator.hasNext() изменяет текущее положение итератора."
                );
            }
            Iterator<Integer> controlIter = controlSet.iterator();
            Iterator<Integer> avlIter = tree.iterator();
            System.out.println("Проверка, правильно ли перебирается дерево.");
            while (controlIter.hasNext()) {
                assertEquals(
                        controlIter.next(), avlIter.next(),
                        "Итератор обходит дерево неверно."
                );
            }
            assertThrows(NoSuchElementException.class, avlIter::next,
                    "Итератор содержит элементы, после полного перебора.");
        }
    }

    @Test
    void iteratorRemoveTest() {
        Random random = new Random();
        AVLTree<Integer> tree;
        for (int i = 0; i < 100; i++) {
            Set<Integer> controlSet = new TreeSet<Integer>();
            int removeIndex = random.nextInt(20) + 1;
            var toRemove = 0;
            for (int j = 0; j < 20; j++) {
                int newNumber = random.nextInt(100);
                controlSet.add(newNumber);
                if (i == removeIndex) {
                    toRemove = newNumber;
                }
            }
            System.out.println("Начальный сет: " + controlSet.toString());
            tree = new AVLTree<>();
            for (int element : controlSet) {
                tree.add(element);
            }
            controlSet.remove(toRemove);
            System.out.println("Контрольный сет: " + controlSet.toString());
            System.out.println("Удаление элемента " + toRemove + " из дерева через итератор...");
            Iterator<Integer> iterator = tree.iterator();
            assertThrows(IllegalStateException.class, () -> iterator.remove(), "Был удален элемент до начала итерации.");

            int counter = tree.size();
            System.out.println("Итерация...");
            while (iterator.hasNext()) {
                int element = iterator.next();
                System.out.print(element + ", ");
                counter--;
                if (element == toRemove) {
                    iterator.remove();
                    assertThrows(IllegalStateException.class, () -> iterator.remove(),
                            "Удаление было произведено два раза подряд...");
                }
            }
            assertTrue(
                    tree.checkInvariant(),
                    "Дерево потеряло свойство инвариантность после удаления."
            );
            assertEquals(
                    controlSet.size(), tree.size(),
                    "Размер дерева неверный. Должен быть - " + controlSet.size() + ", а на деле - " + tree.size()
            );
            for (int element : controlSet) {
                assertTrue(
                        tree.contains(element),
                        "В дереве нет элемента " + element + " из контрольного сета."
                );
            }

            for (int element : tree) {
                assertTrue(
                        controlSet.contains(element),
                        "The tree has the element $element that is not in control set."
                );
            }
        }
    }

    @Test
    void descendingIteratorTest() {
        Random random = new Random();
        NavigableSet<Integer> controlSet;
        AVLTree<Integer> tree;
        for (int i = 0; i < 100; i++) {
            controlSet = new TreeSet<>();
            for (int j = 0; j < 20; j++) {
                controlSet.add(random.nextInt(100));
            }
            System.out.println("Контрольный сет: " + controlSet.toString());
            tree = new AVLTree<>();
            assertFalse(
                    tree.iterator().hasNext(),
                    "Итератор пустого дерева не должен иметь ни один элемент."
            );
            for (int element : controlSet) {
                tree.add(element);
            }
            Iterator<Integer> iterator1 = tree.iterator();
            Iterator<Integer> iterator2 = tree.iterator();
            System.out.println("Проверка, меняет ли hasNext() текущее положение итератора.");
            while (iterator1.hasNext()) {
                assertEquals(
                        iterator2.next(), iterator1.next(),
                        "Вызов AVLIterator.hasNext() изменяет текущее положение итератора."
                );
            }
            Iterator<Integer> controlIter = controlSet.descendingIterator();
            Iterator<Integer> avlDescendingIter = tree.descendingIterator();
            System.out.println("Проверка, правильно ли перебирается дерево в обратном порядке.");
            while (controlIter.hasNext()) {
                assertEquals(
                        controlIter.next(), avlDescendingIter.next(),
                        "Итератор обходит дерево неверно."
                );
            }
            assertThrows(NoSuchElementException.class, avlDescendingIter::next,
                    "Итератор содержит элементы, после полного перебора.");
        }
    }

    @Test
    void size() {
        AVLTree<Integer> tree = new AVLTree<>();

        for (int i = 0; i < 10; i++) {
            tree.add(i * 2);
        }
        assertEquals(10, tree.size());
        for (int i = 10; i > 0; i--) {
            tree.remove(2 * i);
            assertEquals(i, tree.size());
        }


    }

    @Test
    void toArray() {
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> tree = new AVLTree<>();
            int arr[] = new int[50];
            int step = rand.nextInt(10) + 1;

            for (int j = 0; j < 50; j++) {
                int val = j * step;
                arr[j] = val;
                tree.add(val);
            }

            Object[] treeArr = tree.toArray();

            for (int j = 0; j < 50; j++) {
                assertEquals(treeArr[j], arr[j]);
            }
        }
    }

    @Test
    void subSetIterator() {
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            NavigableSet<Integer> tree = new AVLTree<>();
            NavigableSet<Integer> controlSet = new TreeSet<>();

            int val;
            for (int j = 0; j < 50; j++) {
                val = rand.nextInt(200);
                controlSet.add(val);
                tree.add(val);
            }

            int fromValue = rand.nextInt(50);
            int toValue = rand.nextInt(50) + 50;

            NavigableSet<Integer> subTree = tree.subSet(fromValue, true, toValue, false);
            NavigableSet<Integer> subControl = controlSet.subSet(fromValue, true, toValue, false);

            Iterator<Integer> treeIter = subTree.iterator();
            Iterator<Integer> treeIterDes = subTree.descendingIterator();
            Iterator<Integer> subControlIter = subControl.iterator();
            Iterator<Integer> subControlIterDes = subControl.descendingIterator();

            while (subControlIter.hasNext()) {
                assertEquals(
                        treeIter.next(), subControlIter.next(),
                        "Итератор обходит subSet неверно."
                );
            }
            assertThrows(NoSuchElementException.class, treeIter::next,
                    "Итератор содержит элементы, после полного перебора.");
            while (subControlIterDes.hasNext()) {
                assertEquals(
                        treeIterDes.next(), subControlIterDes.next(),
                        "Итератор обходит subSet неверно."
                );
            }
            assertThrows(NoSuchElementException.class, treeIterDes::next,
                    "Итератор содержит элементы, после полного перебора.");
        }
    }

    @Test
    void descendingTest() {
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            AVLTree<Integer> tree = new AVLTree<>();
            NavigableSet<Integer> des = tree.descendingSet();

            int min = 201;
            int max = -1;

            for (int j = 0; j < 50; j++) {
                int val = rand.nextInt(200);
                if (val > max) max = val;
                if (val < min) min = val;
                tree.add(val);
            }

            assertEquals(des.first(), max);
            assertEquals(des.last(), min);
            System.out.println(tree.lower(tree.getRootValue()));
            System.out.println(tree.higher(tree.getRootValue()));

            assertTrue(tree.lower(tree.getRootValue()) < tree.higher(tree.getRootValue()));
            assertTrue(des.lower(tree.getRootValue()) > des.higher(tree.getRootValue()));
        }
    }
}