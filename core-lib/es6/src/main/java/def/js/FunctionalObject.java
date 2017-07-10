package def.js;

/**
 * This interface defines the <code>$apply</code> signature that allows invoking
 * an object.
 * 
 * <p>
 * The code <code>o.$apply(args)</code> is transpiled to <code>o(args)</code>.
 * Implementing this interface is not mandatory to create a functional object.
 * Just defining the <code>$apply</code> function is enough. It is however
 * recommended to implement this interface for readbility sake.
 * 
 * @author Renaud Pawlak
 */
public interface FunctionalObject {

    <R> R $apply(Object... arguments);

}
