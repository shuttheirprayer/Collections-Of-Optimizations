import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class CfbKitchenIndexCheck {

	private static final int EMPTY = 0;
	private static final int SNOWBALL = 1;
	private static final int ICE = 2;

	private static final int PLAIN = 0;
	private static final int FRIDGE = 1;
	private static final int UNKNOWN = 2;

	private static final class Provider {
		final int kind;
		final int[] slots;
		final int[] simulatedUse;
		final boolean iceUnit;

		Provider(int kind, int[] slots, boolean iceUnit) {
			this.kind = kind;
			this.slots = slots;
			this.simulatedUse = new int[slots.length];
			this.iceUnit = iceUnit;
		}

		boolean stockFind(int item) {
			if (kind == FRIDGE && iceUnit && (item == SNOWBALL || item == ICE)) {
				return true;
			}
			for (int i = 0; i < slots.length; i++) {
				if (slots[i] != EMPTY && slots[i] == item && 1 - simulatedUse[i] > 0) {
					return true;
				}
			}
			return false;
		}
	}

	private static Set<Integer> build(Provider[] inventories) {
		Set<Integer> items = new HashSet<>();
		for (Provider provider : inventories) {
			if (provider.kind == UNKNOWN) {
				return null;
			}
			if (provider.kind == FRIDGE) {
				items.add(SNOWBALL);
				items.add(ICE);
			}
			for (int slot : provider.slots) {
				if (slot != EMPTY) {
					items.add(slot);
				}
			}
		}
		return items;
	}

	private static boolean isAbsent(Set<Integer> index, int item) {
		return index != null && !index.contains(item);
	}

	private static void check(boolean condition, String what) {
		if (!condition) {
			throw new AssertionError(what);
		}
	}

	public static void main(String[] args) {
		Random random = new Random(1234);
		int skipped = 0;
		for (int round = 0; round < 20000; round++) {
			Provider[] inventories = new Provider[1 + random.nextInt(4)];
			for (int p = 0; p < inventories.length; p++) {
				int[] slots = new int[random.nextInt(6)];
				for (int i = 0; i < slots.length; i++) {
					slots[i] = random.nextInt(6);
				}
				int kind = random.nextInt(10) == 0 ? UNKNOWN : (random.nextBoolean() ? FRIDGE : PLAIN);
				inventories[p] = new Provider(kind, slots, random.nextBoolean());
				for (int i = 0; i < slots.length; i++) {
					inventories[p].simulatedUse[i] = random.nextInt(2);
				}
			}
			Set<Integer> index = build(inventories);
			for (int item = 1; item < 6; item++) {
				boolean stockFound = false;
				for (Provider provider : inventories) {
					stockFound |= provider.stockFind(item);
				}
				if (isAbsent(index, item)) {
					skipped++;
					check(!stockFound, "the index said absent for an item the stock scan would have found");
				}
			}
		}
		check(skipped > 0, "the index never skipped anything, the check is not exercising the prefilter");

		Provider unknown = new Provider(UNKNOWN, new int[]{3}, false);
		check(build(new Provider[]{unknown}) == null, "an unknown provider disables the index for the whole kitchen");

		System.out.println("CfbKitchenIndexCheck ok, skipped " + skipped + " searches");
	}
}
