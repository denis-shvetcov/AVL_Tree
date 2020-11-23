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
    void comparator() {
    }

    @Test
    void subSet() {
    }

    @Test
    void headSet() {
    }

    @Test
    void tailSet() {
    }

    @Test
    void first() {
    }

    @Test
    void last() {
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


}