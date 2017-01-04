package def.dom;
@jsweet.lang.Interface
public abstract class MessageEventInit extends EventInit {
    @jsweet.lang.Optional
    public Object data;
    @jsweet.lang.Optional
    public String origin;
    @jsweet.lang.Optional
    public String lastEventId;
    @jsweet.lang.Optional
    public String channel;
    @jsweet.lang.Optional
    public Object source;
    @jsweet.lang.Optional
    public MessagePort[] ports;
}

