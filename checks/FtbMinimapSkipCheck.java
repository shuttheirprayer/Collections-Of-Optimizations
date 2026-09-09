public final class FtbMinimapSkipCheck {

	private static boolean cancelsRenderHud(boolean screenOpen, boolean renderDebug, boolean minimapEnabled,
											int minimapVisibility, boolean shouldShowMinimap) {
		if (screenOpen) {
			return false;
		}
		return renderDebug || !minimapEnabled || minimapVisibility == 0 || !shouldShowMinimap;
	}

	private static void check(boolean condition, String what) {
		if (!condition) {
			throw new AssertionError(what);
		}
	}

	public static void main(String[] args) {
		for (int mask = 0; mask < 16; mask++) {
			boolean renderDebug = (mask & 1) != 0;
			boolean minimapEnabled = (mask & 2) != 0;
			boolean visible = (mask & 4) != 0;
			boolean shouldShow = (mask & 8) != 0;
			check(!cancelsRenderHud(true, renderDebug, minimapEnabled, visible ? 255 : 0, shouldShow),
					"an open screen must never cancel the render that refreshes the shared minimap texture");
		}

		check(!cancelsRenderHud(false, false, true, 255, true), "a visible minimap still renders");
		check(cancelsRenderHud(false, true, true, 255, true), "F3 hides the minimap");
		check(cancelsRenderHud(false, false, false, 255, true), "the minimap toggle hides the minimap");
		check(cancelsRenderHud(false, false, true, 0, true), "zero visibility hides the minimap");
		check(cancelsRenderHud(false, false, true, 255, false), "a server that forbids the minimap hides it");

		System.out.println("FtbMinimapSkipCheck ok");
	}
}
