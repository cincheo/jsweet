package source.enums.samepackage;

public class VarbitWrapper {
    private final Varbits varbit;

    public VarbitWrapper(final Varbits varbit) {
        this.varbit = varbit;
    }

    public Varbits getVarbit() {
        return varbit;
    }

    public static VarbitWrapper DEFAULT = new VarbitWrapper(Varbits.TEST);
}