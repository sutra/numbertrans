package info.jonclark.util;

import java.text.ParseException;
import java.util.HashMap;

/**
 * A runtime evaluator for simple algebraic expressions
 */
public class Evaluator {

	public static double evaluate(String expression) throws ParseException {
		return evaluate(expression, null);
	}

	public static double evaluate(String expression, HashMap<String, Double> variables)
			throws ParseException {

		int start = expression.indexOf('(');
		if (start == -1) {
			return evaluateAddSubtract(expression, variables);
		} else {
			int end = StringUtils.findMatching(expression, start, '(', ')');

			String before = expression.substring(0, start).trim();
			String inside = expression.substring(start + 1, end).trim();
			String after = expression.substring(end < expression.length() ? end + 1 : end).trim();

			// TODO: Make this more efficient using trees
			String substituted = before + evaluate(inside, variables) + after;
			double result = evaluate(substituted, variables);
			return result;
		}
	}

	private static double evaluateAddSubtract(String expression, HashMap<String, Double> variables)
			throws ParseException {

		String[] tokens = StringUtils.tokenize(expression, "+-", Integer.MAX_VALUE, true);
		StringUtils.trimTokens(tokens);
		if (tokens.length % 2 == 0) {
			throw new ParseException("Invalid expression: " + expression + "; missing operator?",
					-1);
		}

		double result = evaluateMultiplyDivide(tokens[0], variables);
		for (int i = 1; i < tokens.length; i += 2) {
			if (tokens[i].equals("+")) {
				result += evaluateMultiplyDivide(tokens[i + 1], variables);
			} else if (tokens[i].equals("-")) {
				result -= evaluateMultiplyDivide(tokens[i + 1], variables);
			} else {
				throw new ParseException("Unexpected operator: " + tokens[i], i);
			}
		}
		return result;
	}

	private static double evaluateMultiplyDivide(String expression,
			HashMap<String, Double> variables) throws ParseException {

		String[] tokens = StringUtils.tokenize(expression, "*/", Integer.MAX_VALUE, true);
		StringUtils.trimTokens(tokens);
		if (tokens.length % 2 == 0) {
			throw new ParseException("Invalid expression: " + expression + "; missing operator?",
					-1);
		}

		double result = evaluatePower(tokens[0], variables);
		for (int i = 1; i < tokens.length; i += 2) {
			if (tokens[i].equals("*")) {
				result *= evaluatePower(tokens[i + 1], variables);
			} else if (tokens[i].equals("/")) {
				result /= evaluatePower(tokens[i + 1], variables);
			} else {
				throw new ParseException("Unexpected operator: " + tokens[i], i);
			}
		}
		return result;
	}

	private static double evaluatePower(String expression, HashMap<String, Double> variables)
			throws ParseException {

		String[] tokens = StringUtils.tokenize(expression, "^", Integer.MAX_VALUE, true);
		StringUtils.trimTokens(tokens);
		if (tokens.length % 2 == 0) {
			throw new ParseException("Invalid expression: " + expression + "; missing operator?",
					-1);
		}

		double result = evaluateVariable(tokens[tokens.length - 1], variables);

		// power is evaluated right to left
		for (int i = tokens.length - 2; i > 0; i -= 2) {
			if (tokens[i].equals("^")) {
				result = Math.pow(evaluateVariable(tokens[i - 1], variables), result);
			} else {
				throw new ParseException("Unexpected operator: " + tokens[i], i);
			}
		}
		return result;
	}

	private static double evaluateVariable(String var, HashMap<String, Double> variables)
			throws ParseException {

		if (variables == null || Character.isDigit(var.charAt(0))) {
			try {
				return Double.parseDouble(var);
			} catch (NumberFormatException e) {
				throw new ParseException(e.getMessage(), -1);
			}
		} else {
			Double value = variables.get(var);
			if (value != null) {
				return value;
			} else {
				throw new ParseException("Undefined variable: " + var, -1);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		HashMap<String, Double> vars = new HashMap<String, Double>();
		vars.put("x", 2.0);
		System.out.println(evaluate("1+2/x/x+8", vars));
		System.out.println(evaluate("((1+2 )/ x)", vars));
		System.out.println(evaluate("5^2 ^ 3"));
		System.out.println(evaluate("5^2 ^ idiot_user"));
	}
}
