package source.enums.samepackage;

public class VarbitCallerNotWorking
{
	public static int getVarbitId(VarbitWrapper varbitWrapper) {
		return varbitWrapper.getVarbit().getId();
	}
	
	public static void main(String[] args) {
        int i = getVarbitId(VarbitWrapper.DEFAULT);
        assert i == 1;
    }
}