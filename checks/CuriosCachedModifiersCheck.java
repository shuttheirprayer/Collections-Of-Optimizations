import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CuriosCachedModifiersCheck {

	record Item(String name, String targetSlot, UUID uuid, int amount, boolean viaHook) {
		static Item plain(String name) {
			return new Item(name, null, UUID.randomUUID(), 0, false);
		}
	}

	static final class Handler {
		final String id;
		final int base;
		final boolean fixed;
		final Map<UUID, Integer> modifiers = new HashMap<>();
		final Set<UUID> cached = new HashSet<>();
		final List<Item> stacks = new ArrayList<>();
		final Set<Item> previous = new HashSet<>();
		boolean loaded;
		int lost;

		Handler(String id, int base, boolean fixed) {
			this.id = id;
			this.base = base;
			this.fixed = fixed;
		}

		void addTransient(UUID uuid, int amount) {
			modifiers.put(uuid, amount);
			if (fixed) cached.remove(uuid);
		}

		void clearCached() {
			for (UUID uuid : cached) modifiers.remove(uuid);
			cached.clear();
		}

		int size() {
			int size = base;
			for (int amount : modifiers.values()) size += amount;
			return size;
		}

		void update() {
			int size = size();
			while (stacks.size() > size) {
				if (stacks.get(stacks.size() - 1) != null) lost++;
				stacks.remove(stacks.size() - 1);
			}
			while (stacks.size() < size) stacks.add(null);
		}

		boolean skipPurgeOnce() {
			if (loaded) {
				loaded = false;
				return true;
			}
			return false;
		}
	}

	static final class Wrapper {
		final Map<String, Handler> curios = new LinkedHashMap<>();
		final boolean fixed;

		Wrapper(boolean fixed) {
			this.fixed = fixed;
		}

		Handler slot(String id, int base) {
			return curios.computeIfAbsent(id, key -> new Handler(key, base, fixed));
		}

		static void collect(Map<String, Set<UUID>> into, Item item) {
			if (item != null && item.targetSlot != null && !item.viaHook) {
				into.computeIfAbsent(item.targetSlot, key -> new HashSet<>()).add(item.uuid);
			}
		}

		void clearCachedSlotModifiers() {
			if (fixed) {
				List<Handler> due = new ArrayList<>();
				for (Handler handler : curios.values()) {
					if (handler.cached.isEmpty() || handler.skipPurgeOnce()) continue;
					due.add(handler);
				}
				if (due.isEmpty()) return;
				Map<String, Set<UUID>> live = new HashMap<>();
				for (Handler handler : curios.values()) {
					for (Item item : handler.stacks) collect(live, item);
				}
				for (Handler handler : due) {
					Set<UUID> keep = live.get(handler.id);
					if (keep != null) handler.cached.removeIf(keep::contains);
					handler.clearCached();
				}
				return;
			}
			Map<String, Set<UUID>> found = new HashMap<>();
			for (Handler handler : curios.values()) {
				if (handler.cached.isEmpty()) continue;
				for (Item item : handler.stacks) collect(found, item);
			}
			for (Map.Entry<String, Set<UUID>> entry : found.entrySet()) {
				Handler handler = curios.get(entry.getKey());
				if (handler == null) continue;
				handler.cached.removeAll(entry.getValue());
				handler.clearCached();
			}
		}

		void tick() {
			clearCachedSlotModifiers();
			for (Handler handler : curios.values()) {
				handler.update();
				for (Item item : new ArrayList<>(handler.stacks)) {
					if (item == null || handler.previous.contains(item)) continue;
					if (item.targetSlot != null) curios.get(item.targetSlot).addTransient(item.uuid, item.amount);
					handler.previous.add(item);
				}
			}
		}

		void relog() {
			for (Handler handler : curios.values()) {
				Map<UUID, Integer> saved = new HashMap<>(handler.modifiers);
				handler.modifiers.clear();
				handler.cached.clear();
				handler.previous.clear();
				for (Map.Entry<UUID, Integer> entry : saved.entrySet()) {
					handler.cached.add(entry.getKey());
					handler.modifiers.put(entry.getKey(), entry.getValue());
				}
				handler.loaded = fixed && !handler.cached.isEmpty();
			}
		}

		void equip(String slot, Item item) {
			Handler handler = curios.get(slot);
			for (int i = 0; i < handler.stacks.size(); i++) {
				if (handler.stacks.get(i) == null) {
					handler.stacks.set(i, item);
					return;
				}
			}
			throw new AssertionError("no room in " + slot);
		}

		int lost() {
			int lost = 0;
			for (Handler handler : curios.values()) lost += handler.lost;
			return lost;
		}

		int size(String slot) {
			return curios.get(slot).size();
		}
	}

	static Wrapper beltAndCharms(boolean fixed) {
		Wrapper wrapper = new Wrapper(fixed);
		wrapper.slot("belt", 1);
		wrapper.slot("charm", 1);
		wrapper.tick();
		wrapper.equip("belt", new Item("belt", "charm", UUID.nameUUIDFromBytes("belt0".getBytes()), 3, false));
		wrapper.tick();
		wrapper.tick();
		assert wrapper.size("charm") == 4 : wrapper.size("charm");
		for (int i = 0; i < 3; i++) wrapper.equip("charm", Item.plain("charm" + i));
		wrapper.tick();
		return wrapper;
	}

	public static void main(String[] args) {
		Wrapper broken = beltAndCharms(false);
		broken.relog();
		broken.tick();
		broken.tick();
		assert !broken.curios.get("charm").cached.isEmpty() : "original curios never drops the cross-type cache entry";
		broken.equip("charm", new Item("forger_gem", "charm", UUID.randomUUID(), 1, false));
		broken.tick();
		broken.tick();
		assert broken.lost() == 3 : "original curios purges the belt bonus and the charms fall out: " + broken.lost();
		assert broken.size("charm") == 1 : broken.size("charm");

		Wrapper fixed = beltAndCharms(true);
		fixed.relog();
		fixed.tick();
		assert fixed.curios.get("charm").cached.isEmpty() : "the equip pass re-applied the belt bonus, so it is no longer cached";
		fixed.tick();
		fixed.equip("charm", new Item("forger_gem", "charm", UUID.randomUUID(), 1, false));
		fixed.tick();
		fixed.tick();
		assert fixed.lost() == 0 : fixed.lost();
		assert fixed.size("charm") == 5 : fixed.size("charm");

		Wrapper anklet = new Wrapper(true);
		anklet.slot("anklet", 1);
		anklet.slot("ring", 2);
		anklet.tick();
		anklet.equip("anklet", new Item("anklent", "ring", UUID.randomUUID(), 1, true));
		anklet.tick();
		assert anklet.size("ring") == 3 : anklet.size("ring");
		for (int i = 0; i < 3; i++) anklet.equip("ring", Item.plain("ring" + i));
		anklet.tick();
		anklet.relog();
		anklet.tick();
		anklet.tick();
		anklet.tick();
		assert anklet.lost() == 0 : "an equip hook slot bonus must survive a relog: " + anklet.lost();
		assert anklet.size("ring") == 3 : anklet.size("ring");
		assert anklet.curios.get("ring").cached.isEmpty();

		Wrapper gone = beltAndCharms(true);
		gone.relog();
		gone.curios.get("belt").stacks.set(0, null);
		gone.tick();
		assert gone.size("charm") == 4 : gone.size("charm");
		gone.tick();
		assert gone.size("charm") == 1 : "a bonus nobody provides any more is dropped on the second tick: " + gone.size("charm");
		assert gone.lost() == 2 : gone.lost();

		Wrapper reload = beltAndCharms(true);
		for (int i = 0; i < 3; i++) {
			reload.relog();
			reload.tick();
			reload.tick();
			reload.tick();
		}
		assert reload.lost() == 0 : reload.lost();
		assert reload.size("charm") == 4 : reload.size("charm");
		for (Handler handler : reload.curios.values()) assert handler.cached.isEmpty();

		System.out.println("CuriosCachedModifiersCheck ok");
	}
}
