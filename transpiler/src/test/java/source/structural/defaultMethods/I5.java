package source.structural.defaultMethods;

public interface I5 {

    @MyAnnotation
    default void o2(boolean b, CC1 cc1, II1 ii1) {
        System.out.println("o2-s");
    }

    @MyAnnotation
    default void o2(boolean b, CC2 cc2, II1 ii1) {
        System.out.println("o2-i");
    }

    @MyAnnotation
    default void o3(boolean b) {
        System.out.println("o3");
    }

}