package info.jonclark.io;

import info.jonclark.util.LatexUtils;
import info.jonclark.util.StringUtils;

import java.util.ArrayList;

public class StringTable {

	private final ArrayList<String[]> rows = new ArrayList<String[]>();
	private final ArrayList<Column> columns = new ArrayList<Column>();

	public enum Align {
		LEFT, RIGHT
	};

	private class Column {
		public int nMaxWidth = 0;
	}

	/**
	 * @param row
	 *            An array of columns representing one row of this table
	 */
	public void addRow(String... row) {
		for (int i = 0; i < row.length; i++) {
			if (i >= columns.size()) {
				Column column = new Column();
				column.nMaxWidth = row[i].length();
				columns.add(column);
			} else {
				Column column = columns.get(i);
				column.nMaxWidth = Math.max(column.nMaxWidth, row[i].length());
			}
		}
		rows.add(row);
	}

	public void addRow(ArrayList<String> row) {
		addRow(row.toArray(new String[row.size()]));
	}

	public static String padColumn(String column, int columnWidth, char padChar, Align align) {
		if (column.length() < columnWidth) {
			if (align == Align.RIGHT) {
				column =
						StringUtils.duplicateCharacter(padChar, columnWidth - column.length())
								+ column;
			} else if (align == Align.RIGHT) {
				column =
						column
								+ StringUtils.duplicateCharacter(padChar, columnWidth
										- column.length());
			}
		}
		return column;
	}

	public String toLatexString() {
		StringBuilder builder = new StringBuilder("\\begin{tabular}");

		builder.append("{|");
		for (int i = 0; i < columns.size(); i++)
			builder.append("c|");
		builder.append("}\n");
		builder.append("\\hline\n");
		for (final String[] row : rows) {
			for (int i = 0; i < row.length; i++) {
				builder.append(LatexUtils.replaceLatexKillers(row[i]));
				if (i < row.length - 1) {
					builder.append(" & ");
				}
			}
			builder.append("\\\\ \n \\hline \n");
		}
		builder.append("\\end{tabular}");
		return builder.toString();
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (final String[] row : rows) {
			for (int i = 0; i < row.length; i++) {
				Column column = columns.get(i);
				builder.append(padColumn(row[i], column.nMaxWidth + 1, ' ', Align.RIGHT));
			}
			builder.append("\n");
		}
		return builder.toString();
	}
}
