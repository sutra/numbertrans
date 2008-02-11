package info.jonclark.util;

import java.text.DecimalFormat;

public class SpaceUsage {

	private long bytes;
	private static final DecimalFormat format = new DecimalFormat("#,###.###");

	public SpaceUsage(long bytes) {
		this.bytes = bytes;
	}

	public double getInGigabytes() {
		return (double) bytes / (double) (1000 * 1000 * 1000);
	}

	public double getInMegabytes() {
		return (double) bytes / (double) (1000 * 1000);
	}

	public double getInKilobytes() {
		return (double) bytes / (double) (1000);
	}

	public long getInBytes() {
		return bytes;
	}

	public String toStringSingleUnit() {
		if (getInGigabytes() > 1.0) {
			return format.format(getInGigabytes()) + "GB";
		} else if (getInMegabytes() > 1.0) {
			return format.format(getInMegabytes()) + "MB";
		} else if (getInKilobytes() > 1.0) {
			return format.format(getInKilobytes()) + "KB";
		} else {
			return format.format(getInBytes()) + " bytes";
		}
	}

	public String toString() {
		return toStringSingleUnit();
	}
}
