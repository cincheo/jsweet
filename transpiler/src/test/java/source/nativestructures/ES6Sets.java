package source.nativestructures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ES6Sets {
    public static void main(String[] args) {
        // add
        Set s1 = new HashSet();
        assert s1.size() == 0;
        boolean added = s1.add("1");
        assert added;
        s1.add("2");
        boolean added2 = s1.add("2");
        assert !added2;
        assert s1.size() == 2;
        s1.clear();
        assert s1.size() == 0;

        // remove
        Set<String> s2 = new HashSet<>();
        s2.add("1");
        assert s2.size() == 1;
        assert s2.remove("1");
        assert s2.size() == 0;
        s2.add("2");
        assert !s2.remove("1");
        assert s2.size() == 1;
        assert !s2.isEmpty();

        Set<Integer> s3 = new HashSet<>();
        s3.add(1);
        assert s3.size() == 1;
        assert s3.remove(1);
        assert s3.size() == 0;
        s3.add(2);
        assert !s3.remove(1);
        assert s3.size() == 1;

        // contains
        Set<String> s4A = new HashSet<>();
        Set<String> s4B = new HashSet<>();
        Set<String> s4C = new HashSet<>();
        Set<Set<String>> s4 = new HashSet<>();
        s4.add(s4A);
        s4.add(s4B);
        assert s4.size() == 2;
        assert s4.contains(s4A);
        assert s4.contains(s4B);
        assert !s4.contains(s4C);

        // addAll
        Set<String> s5A = new HashSet<>();
        s5A.add("1");
        s5A.add("2");
        Set<String> s5B = new HashSet<>();
        s5B.add("3");
        s5B.add("4");
        Set<String> s5Merged = new HashSet<>();
        boolean addedAll = s5Merged.addAll(s5A);
        assert addedAll;
        s5Merged.addAll(s5B);
        assert s5Merged.size() == 4;
        for (int i = 1; i <= 4; i++) {
            assert s5Merged.contains("" + i);
        }

        // new HashSet(Collection<>);
        Set<String> s6A = new HashSet<>();
        s6A.add("1");
        s6A.add("2");
        Set<String> s6 = new HashSet<>(s6A);
        assert s6.size() == 2;
        for (int i = 1; i <= 2; i++) {
            assert s6.contains("" + i);
        }

        // new HashSet(int initialCapacity);
        Set<String> s7 = new HashSet<>(10);
        assert s7.size() == 0;
        assert s7.isEmpty();

        // new HashSet(int initialCapacity, float loadFactor);
        Set<String> s8 = new HashSet<>(10, 11);
        assert s8.size() == 0;
        assert s8.isEmpty();

        // removeAll
        Set<String> s9A = new HashSet<>();
        s9A.add("1");
        s9A.add("3");
        s9A.add("5");
        s9A.add("7");
        Set<String> s9 = new HashSet<>();
        s9.add("1");
        s9.add("2");
        s9.add("3");
        s9.add("4");
        s9.add("5");
        assert s9.size() == 5;
        boolean removedAll = s9.removeAll(s9A);
        assert removedAll;
        assert s9.size() == 2;
        assert s9.contains("2");
        assert !s9.contains("3");
        assert s9.contains("4");

        // retainAll
        Set<String> s10A = new HashSet<>();
        s10A.add("1");
        s10A.add("3");
        s10A.add("5");
        s10A.add("7");
        Set<String> s10 = new HashSet<>();
        s10.add("1");
        s10.add("2");
        s10.add("3");
        s10.add("4");
        s10.add("5");
        assert s10.size() == 5;
        boolean retainedAll = s10.retainAll(s10A);
        assert retainedAll;
        assert s10.size() == 3;
        assert s10.contains("1");
        assert !s10.contains("2");
        assert s10.contains("3");
        assert !s10.contains("4");
        assert s10.contains("5");
        assert !s10.contains("7");

        // toArray
        Set<String> s11 = new HashSet<>();
        s11.add("1");
        s11.add("2");
        String[] ar = (String[]) s11.toArray();
        assert ar.length == 2;
        assert "1".equals(ar[0]) || "2".equals(ar[0]);
        assert "1".equals(ar[1]) || "2".equals(ar[1]);
        assert !ar[0].equals(ar[1]);

        // forEach
        List<String> iterated = new ArrayList<>();
        s11.forEach(str -> iterated.add(str));
        assert iterated.size() == 2;
        assert iterated.get(0) == "1";
        assert iterated.get(1) == "2";
    }
}
