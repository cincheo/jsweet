package def.dom;
import def.js.Error;
public interface ErrorEventHandler {
    public void $apply(String message, String filename, double lineno, double colno, Error error);
    public void $apply(String message, String filename, double lineno, double colno);
    public void $apply(String message, String filename, double lineno);
    public void $apply(String message, String filename);
    public void $apply(String message);
}

