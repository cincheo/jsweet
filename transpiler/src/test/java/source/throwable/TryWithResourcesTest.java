package source.throwable;

public class TryWithResourcesTest {

    void singleResourceMethod() throws Exception {
        try (CloseClass one = new CloseClass()) {
        }
    }

    void multipleResourcesMethod() throws Exception {
        try (CloseClass one = new CloseClass(); CloseClass two = new CloseClass()) {
        }
    }

    /* output of this test should be equal to #multipleResourcesMethod */
    public void tryFinallyMethod() throws Exception {
        CloseClass one = new CloseClass();
        CloseClass two = new CloseClass();
        try {
        } finally {
            two.close();
            one.close();
        }
    }

    void resourcesAndTryFinallyMethod() throws Exception {
        CloseClass one = new CloseClass();
        try (CloseClass two = new CloseClass(); CloseClass three = new CloseClass()) {
        } finally {
            one.close();
        }
    }

    void nestedResourceMethod() throws Exception {
        try (CloseClass one = new CloseClass()) {
            try (CloseClass two = new CloseClass()) {
            }
        }
    }
}

/**
 * Dummy class that fulfills the minimum requirement to act as a resource for a
 * try-with-resources clause.
 */
class CloseClass implements AutoCloseable {

    @Override
    public void close() throws Exception {
    }
}
