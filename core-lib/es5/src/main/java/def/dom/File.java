package def.dom;
public class File extends Blob {
    public Object lastModifiedDate;
    public String name;
    public static File prototype;
    public File(Object[] parts, String filename, FilePropertyBag properties){}
    public File(Object[] parts, String filename){}
    protected File(){}
}

