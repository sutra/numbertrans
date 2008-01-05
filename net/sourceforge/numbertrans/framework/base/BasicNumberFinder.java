package net.sourceforge.numbertrans.framework.base;

import info.jonclark.lang.IntRange;
import info.jonclark.lang.LongRange;
import info.jonclark.util.StringUtils;

import java.util.Properties;

import net.sourceforge.numbertrans.framework.base.AbstractNumber.Context;

public class BasicNumberFinder implements NumberFinder {

	/**
	 * Numbers that should not be considered numbers by this finder. Example:
	 * 1-3 in Chinese since these can be parts of pronouns.
	 */
	protected final LongRange[] unsafeNumbers;

	public BasicNumberFinder(Properties props, String prefix) {
		prefix = StringUtils.forceSuffix(prefix, ".") + "numberFinder.";
		String allRanges = props.getProperty(prefix + "unsafeRanges");
		String[] rangeTokens = StringUtils.tokenize(allRanges, ",");
		unsafeNumbers = new LongRange[rangeTokens.length];
		for (int i = 0; i < rangeTokens.length; i++) {
			unsafeNumbers[i] = LongRange.parseLongRange(rangeTokens[i]);
		}
	}

	private boolean isUnsafeNumber(WholeNumber number) {
		for (int i = 0; i < unsafeNumbers.length; i++)
			if (unsafeNumbers[i].isInRange(number.getValue()))
				return true;
		return false;
	}

	public boolean isNumberAlways(String strToCheck) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNumberSometimes(String strToCheck) {
		// TODO Auto-generated method stub
		return false;
	}

	public NumberMatch nextMatch(String[] tokens, int beginIndex) {
		// TODO: Make this method a bit more intelligent
		for (int i = beginIndex; i < tokens.length; i++) {

			// TODO: Iterate over matchers?
			// or perhaps we should be able to do all matching from here?
			if (isNumberSometimes(tokens[i])) {
				return new NumberMatch(new IntRange(i, i), new String[] { tokens[i] },
						Context.UNKNOWN);
			}
		}

		return null;
	}

}
