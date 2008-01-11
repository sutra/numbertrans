package info.jonclark.util;

public class LatexUtils {
	public static String replaceLatexKillers(String str) {
		String result = str;

		// do this first so we don't escape real math delimiters!
		result = StringUtils.replaceFast(result, "$", " \\$ ");

		// first, replace matched quotes
		// result = StringUtils.replaceMatching(result, "\"", "\"", "``", "''");
		// then cleanup any leftovers
		result = StringUtils.replaceFast(result, "\"", "``");

		result = StringUtils.replaceFast(result, "[", " \\[ ");
		result = StringUtils.replaceFast(result, "]", " \\] ");
		result = StringUtils.replaceFast(result, "{", " \\{ ");
		result = StringUtils.replaceFast(result, "}", " \\} ");

		result = StringUtils.replaceFast(result, "\\", "$\\backslash$");
		result = StringUtils.replaceFast(result, "%", " \\% ");
		result = StringUtils.replaceFast(result, "&", " \\& ");
		result = StringUtils.replaceFast(result, "#", " \\# ");
		result = StringUtils.replaceFast(result, "~", "$\\sim$");
		result = StringUtils.replaceFast(result, "^", "$\\wedge$");
		result = StringUtils.replaceFast(result, "<", "$<$");
		result = StringUtils.replaceFast(result, ">", "$>$");

		result = StringUtils.replaceFast(result, "_", " \\_ ");
		result = StringUtils.replaceFast(result, "...", " \\ldots ");

		return result;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(replaceLatexKillers("hi [] {\"how\" are you } }\""));

	}
}
