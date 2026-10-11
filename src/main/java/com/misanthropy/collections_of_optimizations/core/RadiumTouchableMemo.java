package com.misanthropy.collections_of_optimizations.core;

public final class RadiumTouchableMemo {

    private static final byte UNKNOWN = 0;
    private static final byte TOUCHABLE = 1;
    private static final byte UNTOUCHABLE = 2;

    private static final ClassValue<Slot> SLOTS = new ClassValue<>() {
        @Override
        protected Slot computeValue(Class<?> type) {
            return new Slot();
        }
    };

    private RadiumTouchableMemo() {
    }

    public static Slot slot(Class<?> blockClass) {
        return SLOTS.get(blockClass);
    }

    public static final class Slot {
        private volatile byte state = UNKNOWN;

        public boolean known() {
            return this.state != UNKNOWN;
        }

        public boolean value() {
            return this.state == TOUCHABLE;
        }

        public void store(boolean touchable) {
            this.state = touchable ? TOUCHABLE : UNTOUCHABLE;
        }
    }
}
