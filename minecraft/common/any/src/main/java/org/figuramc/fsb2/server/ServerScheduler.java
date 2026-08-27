package org.figuramc.fsb2.server;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerScheduler {
    @FunctionalInterface
    public interface Ticker {

        /**
         * Perform the action.
         *
         * @return {@code true}, to keep this ticker in the schedule, or {@code false} to dequeue it.
         */
        boolean act();
    }
    private final ConcurrentLinkedQueue<Ticker> targets = new ConcurrentLinkedQueue<>();

    public ServerScheduler() {

    }

    public void tick() {
        targets.removeIf(next -> !next.act());
    }

    public void enqueue(Ticker t) {
        targets.add(t);
    }

    public void once(Runnable action) {
        enqueue(makeOnce(action));
    }

    public void forever(Runnable action) {
        enqueue(makeForever(action));
    }

    public void countDown(Runnable action, int numTicks) {
        enqueue(new CountDownTicker(action, numTicks));
    }

    public static Ticker makeOnce(Runnable action) {
        return () -> {
            action.run();
            return false;
        };
    }

    public static Ticker makeForever(Runnable action) {
        return () -> {
            action.run();
            return true;
        };
    }

    /**
     * Run an action after a specified number of ticks.
     */
    public static class CountDownTicker implements Ticker {
        protected final Runnable action;
        protected int ticks;

        /**
         * Run an action after a specified number of ticks.
         */
        public CountDownTicker(Runnable action, int ticks) {
            this.action = action;
            this.ticks = ticks;
        }

        @Override
        public boolean act() {
            if (--ticks <= 0) {
                action.run();
                return false;
            } else return true;
        }
    }

    public static class IntervalTicker extends CountDownTicker {
        protected final int durationBetween;
        /**
         * Run an action after a specified number of ticks.
         */
        public IntervalTicker(Runnable action, int ticks) {
            super(action, ticks);
            this.durationBetween = ticks;
        }

        @Override
        public boolean act() {
            if (--ticks <= 0) {
                action.run();
                ticks = durationBetween;
            }
            return true;
        }
    }

    /**
     * Run an action after at least the specified duration in seconds.
     */
    public static class RealTimeCountDownTicker implements Ticker {
        protected final Runnable action;
        protected final long scheduledCompletion;

        /**
         * Run an action after at least the specified duration in seconds.
         */
        public RealTimeCountDownTicker(Runnable action, double seconds) {
            this.action = action;
            scheduledCompletion = System.nanoTime() + (long) (seconds * 1e9);
        }

        @Override
        public boolean act() {
            if (System.nanoTime() > scheduledCompletion) {
                action.run();
                return false;
            } else return true;
        }
    }
}
