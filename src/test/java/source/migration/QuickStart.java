package source.migration;

public class QuickStart {

    public String concat(String[] array) {
        String result = "";
        for (int i = 0; i < array.length; ++i) {
            result += array[i];
        }
        return result;
    }

    public static void main(String[] args) {
        java.util.function.Function<Object, Object> function = o -> o;
        System.out.println("Hi, random=" + function.apply(4));
    }
}
