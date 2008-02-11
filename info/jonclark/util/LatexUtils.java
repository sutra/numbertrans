package info.jonclark.util;

public class LatexUtils {
	public static String replaceLatexKillers(String str) {
		String result = str;
		
		result = StringUtils.replaceFast(result, "<", "$<$");
		result = StringUtils.replaceFast(result, ">", "$>$");

		// get rid of backslashes before anything else
		// use < as temp char since we're guaranteed not to have any at this point
		result = StringUtils.replaceFast(result, "\\", "<\\backslash<");
		
		// do this before any math symbols so we don't escape real math delimiters!
		result = StringUtils.replaceFast(result, "$", " \\$ ");
		result = StringUtils.replaceFast(result, "<", " $");

		// first, replace matched quotes
		// result = StringUtils.replaceMatching(result, "\"", "\"", "``", "''");
		// then cleanup any leftovers
		result = StringUtils.replaceFast(result, "\"", "``");

		result = StringUtils.replaceFast(result, "[", " $[$ ");
		result = StringUtils.replaceFast(result, "]", " $]$ ");
		result = StringUtils.replaceFast(result, "{", " \\{ ");
		result = StringUtils.replaceFast(result, "}", " \\} ");

		result = StringUtils.replaceFast(result, "&", " \\& ");
		result = StringUtils.replaceFast(result, "#", " \\# ");
		result = StringUtils.replaceFast(result, "~", "$\\sim$");
		result = StringUtils.replaceFast(result, "^", "$\\wedge$");

		result = StringUtils.replaceFast(result, "_", " \\_ ");
		result = StringUtils.replaceFast(result, "...", " \\ldots ");

		return result;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(replaceLatexKillers("hi [] {\"how\" are you } }\""));

	}
}
