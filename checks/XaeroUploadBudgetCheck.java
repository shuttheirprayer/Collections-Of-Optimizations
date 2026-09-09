public final class XaeroUploadBudgetCheck {

	private static final long STOCK_HEADROOM_NANOS = 3_000_000L;

	private static final class Budget {
		final long uploadWindowNanos;
		final long gpuLimitNanos;

		Budget(long uploadWindowNanos, long gpuLimitNanos) {
			this.uploadWindowNanos = uploadWindowNanos;
			this.gpuLimitNanos = gpuLimitNanos;
		}
	}

	private static Budget budget(long framePeriodNanos, long spentThisFrameNanos, long headroomFloorNanos,
								 boolean mapScreenOpen) {
		long totalTime = framePeriodNanos;
		long timeAvailable = Math.max(headroomFloorNanos, totalTime - spentThisFrameNanos);
		long uploadWindow = timeAvailable / 4L;
		long gpuLimit = Math.max(1_000_000L,
				mapScreenOpen ? totalTime * 5L / 12L : Math.min(totalTime / 5L, timeAvailable));
		return new Budget(uploadWindow, gpuLimit);
	}

	private static void check(boolean condition, String what) {
		if (!condition) {
			throw new AssertionError(what);
		}
	}

	public static void main(String[] args) {
		long patched = 1_000_000L;

		long frame200 = 5_000_000L;
		long spent200 = 4_500_000L;
		Budget stock200 = budget(frame200, spent200, STOCK_HEADROOM_NANOS, false);
		Budget fixed200 = budget(frame200, spent200, patched, false);
		check(stock200.uploadWindowNanos == 750_000L, "stock grants 750us on a saturated 5ms frame");
		check(fixed200.uploadWindowNanos == 250_000L, "patched grants 250us on a saturated 5ms frame");
		check(stock200.uploadWindowNanos * 100L / frame200 == 15L, "stock budget is 15% of a 5ms frame");
		check(fixed200.gpuLimitNanos == stock200.gpuLimitNanos, "gpu estimate budget is untouched at 200fps");

		long frame60 = 16_666_666L;
		long spent60 = 8_000_000L;
		Budget stock60 = budget(frame60, spent60, STOCK_HEADROOM_NANOS, false);
		Budget fixed60 = budget(frame60, spent60, patched, false);
		check(stock60.uploadWindowNanos == fixed60.uploadWindowNanos, "60fps frame with headroom left is unchanged");
		check(stock60.gpuLimitNanos == fixed60.gpuLimitNanos, "60fps gpu estimate budget is unchanged");

		long spent60Saturated = 16_000_000L;
		Budget stock60Sat = budget(frame60, spent60Saturated, STOCK_HEADROOM_NANOS, false);
		Budget fixed60Sat = budget(frame60, spent60Saturated, patched, false);
		check(stock60Sat.uploadWindowNanos == 750_000L, "the floor binds on a saturated 60fps frame too");
		check(fixed60Sat.uploadWindowNanos == 250_000L, "and the patched floor cuts it there as well");
		check(stock60Sat.gpuLimitNanos == 3_000_000L, "a saturated 60fps frame reaches the floor through the gpu estimate too");
		check(fixed60Sat.gpuLimitNanos == 1_000_000L, "where the patched floor drops it to its own hard minimum");

		Budget stockMap = budget(frame200, spent200, STOCK_HEADROOM_NANOS, true);
		check(stockMap.gpuLimitNanos > stock200.gpuLimitNanos, "the map screen gets a larger gpu estimate budget");

		for (int micros = 100; micros <= 3000; micros += 100) {
			Budget fast = budget(frame200, spent200, micros * 1000L, false);
			check(fast.uploadWindowNanos <= stock200.uploadWindowNanos, "no config value exceeds the stock window");
			check(fast.gpuLimitNanos == stock200.gpuLimitNanos, "no config value moves the gpu estimate budget at 200fps");
			Budget slow = budget(frame60, spent60Saturated, micros * 1000L, false);
			check(slow.gpuLimitNanos >= 1_000_000L, "the gpu estimate budget never falls under its own floor");
			check(slow.uploadWindowNanos >= 25_000L, "the smallest window still admits an upload attempt");
		}

		Budget atStock = budget(frame200, spent200, 3000 * 1000L, false);
		check(atStock.uploadWindowNanos == stock200.uploadWindowNanos, "3000 restores stock exactly");

		System.out.println("XaeroUploadBudgetCheck ok");
	}
}
