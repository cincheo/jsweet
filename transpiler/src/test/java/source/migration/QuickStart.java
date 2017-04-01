package source.migration;

import java.util.LinkedList;
import java.util.Queue;

public class QuickStart {

    public String concat(String[] array) {
        String result = "";
        for (int i = 0; i < array.length; ++i) {
            result += array[i];
        }
        return result;
    }

    public void empty() {
        System.out.println();
    }

    public void fail(String a, String b) {
        System.out.println(concat(new String[]{a, b}));
    }
    
    public Queue<String> wrap(String input){
        LinkedList<String> list = new LinkedList<>();
        list.add(input);
        return list;
    }

    public static void main(String[] args) {
        java.util.function.Function<Object, Object> function = o -> o;
        System.out.println("Hi, random=" + function.apply(4));
    }
}
