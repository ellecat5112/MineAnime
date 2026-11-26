package com.animecore.quest;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Per-player, per-system story progress, stored inside the system's {@link com.animecore.component.PlayerPowerComponent}
 * (saved and synced to the owning client with it). One story quest is active at a time; its objectives run in order.
 */
public final class QuestLog {
    private final Runnable dirty;
    private final Set<Identifier> completed = new LinkedHashSet<>();
    private @Nullable Identifier active;
    private int step;
    private int progress;
    private @Nullable BlockPos site;
    private boolean stepPrepared;
    /** Story NPCs spawned for the active quest (despawned when it ends). Server-side bookkeeping, not synced. */
    private final List<UUID> spawned = new ArrayList<>();

    public QuestLog(Runnable dirty) { this.dirty = dirty; }

    public Set<Identifier> completed() { return Collections.unmodifiableSet(completed); }
    public boolean isCompleted(Identifier quest) { return completed.contains(quest); }
    public Optional<Identifier> active() { return Optional.ofNullable(active); }
    public int step() { return step; }
    public int progress() { return progress; }
    public Optional<BlockPos> site() { return Optional.ofNullable(site); }
    public boolean stepPrepared() { return stepPrepared; }
    public List<UUID> spawned() { return spawned; }

    void start(Identifier quest) {
        active = quest;
        step = 0;
        progress = 0;
        site = null;
        stepPrepared = false;
        dirty.run();
    }

    void setProgress(int value) { progress = value; dirty.run(); }
    void setSite(@Nullable BlockPos pos) { site = pos; dirty.run(); }
    void setStepPrepared(boolean prepared) { stepPrepared = prepared; dirty.run(); }

    void nextStep() {
        step++;
        progress = 0;
        stepPrepared = false;
        dirty.run();
    }

    void finish(boolean success) {
        if (success && active != null) completed.add(active);
        active = null;
        step = 0;
        progress = 0;
        site = null;
        stepPrepared = false;
        dirty.run();
    }

    /** Admin / test helper: mark a quest done without playing it. */
    public void forceComplete(Identifier quest) { completed.add(quest); dirty.run(); }

    public void reset() {
        completed.clear();
        finish(false);
    }

    public NbtCompound write() {
        NbtCompound tag = new NbtCompound();
        NbtList done = new NbtList();
        completed.forEach(id -> done.add(NbtString.of(id.toString())));
        tag.put("completed", done);
        if (active != null) {
            tag.putString("active", active.toString());
            tag.putInt("step", step);
            tag.putInt("progress", progress);
            tag.putBoolean("prepared", stepPrepared);
            if (site != null) tag.put("site", NbtHelper.fromBlockPos(site));
        }
        NbtList ids = new NbtList();
        spawned.forEach(u -> ids.add(NbtString.of(u.toString())));
        tag.put("spawned", ids);
        return tag;
    }

    public void read(NbtCompound tag) {
        completed.clear();
        for (NbtElement e : tag.getList("completed", NbtElement.STRING_TYPE)) {
            Identifier id = Identifier.tryParse(e.asString());
            if (id != null) completed.add(id);
        }
        active = tag.contains("active") ? Identifier.tryParse(tag.getString("active")) : null;
        step = tag.getInt("step");
        progress = tag.getInt("progress");
        stepPrepared = tag.getBoolean("prepared");
        site = tag.contains("site") ? NbtHelper.toBlockPos(tag, "site").orElse(null) : null;
        spawned.clear();
        for (NbtElement e : tag.getList("spawned", NbtElement.STRING_TYPE)) {
            try { spawned.add(UUID.fromString(e.asString())); } catch (IllegalArgumentException ignored) {}
        }
    }
}
