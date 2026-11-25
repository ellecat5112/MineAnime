package com.animecore.tool;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

/** Minimal server tick scheduler for multi-tick moves (vortex, gliding bombers). */
public final class ToolScheduler {
    private static final List<Task> TASKS = new ArrayList<>();
    private static boolean registered;

    private record Task(IntPredicate step, int[] tick) {}

    private ToolScheduler() {}

    /** {@code step} receives the tick index and returns false to stop early; runs for at most {@code ticks}. */
    public static void run(int ticks, IntPredicate step) {
        if (!registered) {
            registered = true;
            ServerTickEvents.END_SERVER_TICK.register(server -> tick());
        }
        TASKS.add(new Task(t -> t < ticks && step.test(t), new int[]{0}));
    }

    private static void tick() {
        if (TASKS.isEmpty()) return;
        List<Task> snapshot = new ArrayList<>(TASKS);
        for (Task task : snapshot) {
            if (!task.step().test(task.tick()[0]++)) TASKS.remove(task);
        }
    }

    public static void clear() {
        TASKS.clear();
    }
}
