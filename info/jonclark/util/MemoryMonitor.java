package info.jonclark.util;

public class MemoryMonitor {

	private long baseline;

	public MemoryMonitor() {
		recordBaseline();
	}

	public long getGlobalUsageInBytes() {
		Runtime r = Runtime.getRuntime();
		long usage = r.totalMemory() - r.freeMemory();
		return usage;
	}

	public SpaceUsage getGlobalUsage() {
		return new SpaceUsage(getGlobalUsageInBytes());
	}

	/**
	 * Need not be called upon creation
	 */
	public void recordBaseline() {
		System.gc();
		baseline = getGlobalUsageInBytes();
	}

	private long getUsageDeltaInBytes() {
		long current = getGlobalUsageInBytes();
		return current - baseline;
	}

	public SpaceUsage getUsageDelta() {
		return new SpaceUsage(getUsageDeltaInBytes());
	}

	private long getMemoryUsagePerElementInBytes(int nElements) {
		long per = getUsageDeltaInBytes() / nElements;
		return per;
	}

	public SpaceUsage getMemoryUsagePerElement(int nElements) {
		return new SpaceUsage(getMemoryUsagePerElementInBytes(nElements));
	}
}
