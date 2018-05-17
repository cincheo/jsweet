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

    void singleResourceWithCatchMethod() throws Exception {
        try (CloseClass one = new CloseClass()) {
        }
        catch (Exception e) {
        }
    }

    void singleResourceWithFinallyMethod() throws Exception {
        try (CloseClass one = new CloseClass()) {
        }
        finally {
        }
    }

    void singleResourceWithCatchAndFinallyMethod() throws Exception {
        try (CloseClass one = new CloseClass()) {
        }
        catch (Exception e) {
        }
        finally {
        }
    }
    
    /* output of this test should be equal to #multipleResourcesMethod */
    public void regularTryFinallyMethod() throws Exception {
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
