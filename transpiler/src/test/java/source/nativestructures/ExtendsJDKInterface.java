package source.nativestructures;

import java.util.*;

public class ExtendsJDKInterface {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
         MyTypeOfList<Integer> integers = new MyTypeOfListImp<>();
    }

    public interface MyTypeOfList<T> extends List<T> {
        int allwaysReturnsOne();
    }

    public static class MyTypeOfListImp<T> implements MyTypeOfList<T> {
        @Override
        public int size() {
            return 0;
        }
        @Override
        public boolean isEmpty() {
            return false;
        }
        @Override
        public boolean contains(Object o) {
            return false;
        }
        @Override
        public Iterator<T> iterator() {
            return null;
        }
        @Override
        public Object[] toArray() {
            return new Object[0];
        }
        @Override
        public <T1> T1[] toArray(T1[] a) {
            return null;
        }
        @Override
        public boolean add(T t) {
            return false;
        }
        @Override
        public boolean remove(Object o) {
            return false;
        }
        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }
        @Override
        public boolean addAll(Collection<? extends T> c) {
            return false;
        }
        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            return false;
        }
        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }
        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }
        @Override
        public void clear() {
        }
        @Override
        public T get(int index) {
            return null;
        }
        @Override
        public T set(int index, T element) {
            return null;
        }
        @Override
        public void add(int index, T element) {

        }
        @Override
        public T remove(int index) {
            return null;
        }
        @Override
        public int indexOf(Object o) {
            return 0;
        }
        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }
        @Override
        public ListIterator<T> listIterator() {
            return null;
        }
        @Override
        public ListIterator<T> listIterator(int index) {
            return null;
        }
        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            return null;
        }
        @Override
        public int allwaysReturnsOne() {
            return 1;
        }
    }
}