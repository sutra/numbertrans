package info.jonclark.util;


public class GetOpts {

    // private final HashMap<String, String> opts = new HashMap<String,
    // String>();
    // private final String[] remainingArgs;
    private final String[] args;

    public GetOpts(String requiredArgs, String[] args) throws UsageException {
	// TODO: XXX: Make this more flexible like perl

	this.args = args;
    }

    private static String getParam(String name, String[] args) {
	int idx = ArrayUtils.findInUnsortedArray(args, name);
	if (idx == -1 || idx == args.length)
	    return null;
	return args[idx + 1];
    }
    
    private static boolean getSwitch(String name, String[] args) {
	return ArrayUtils.unsortedArrayContains(args, name);
    }

    public boolean getOptionBool(String name) throws UsageException {
	return getSwitch(name, args);
    }

    public String getOptionStr(String name) throws UsageException {
	String str = getParam(name, args);
	if(str == null)
	    throw new UsageException("Switch not found: -" + name);
	return str;
    }

    public int getOptionInt(String name) throws UsageException {
	return Integer.parseInt(getOptionStr(name));
    }

    public double getOptionDouble(String name) throws UsageException {
	return Double.parseDouble(getOptionStr(name));
    }

    /**
     * Get the remaining arguments that were not part of a switch
     */
    public String[] getRemainingArgs(int nExpectedArgs) throws UsageException {
	// TODO: Check for having already used some of these args as switches

	String[] remaining = new String[nExpectedArgs];
	System.arraycopy(args, args.length - nExpectedArgs, args.length - 1, 0, nExpectedArgs);
	return remaining;
    }
}
