package dev.amble.ait.client.sounds.flight;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.client.sounds.PositionedLoopingSound;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.sounds.flight.FlightSound;

public class InteriorFlightSound extends PositionedLoopingSound implements FlightSoundPlayer {
    private FlightSound data;
    private int ticks = 0;
    private boolean dirty = true;

    public InteriorFlightSound(FlightSound data, SoundCategory soundCategory) {
        super(data.sound(), soundCategory, new BlockPos(0,0,0), 0.25F);
        this.data = data;
    }

    @Override
    public Identifier getId() {
        return data.soundId();
    }

    @Override
    public WeightedSoundSet getSoundSet(SoundManager soundManager) {
        if (this.getId().equals(SoundManager.INTENTIONALLY_EMPTY_ID)) {
            this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
            return SoundManager.INTENTIONALLY_EMPTY_SOUND_SET;
        } else {
            WeightedSoundSet weightedSoundSet = soundManager.get(this.getId());
            if (weightedSoundSet == null) {
                this.sound = SoundManager.MISSING_SOUND;
            } else {
                this.sound = weightedSoundSet.getSound(this.random);
            }

            return weightedSoundSet;
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;

        if (this.ticks >= (this.getData().length() / this.pitch)) {
            this.refresh();
        }
    }

    @Override
    public ClientTardis tardis() {
        return ClientTardisUtil.getCurrentTardis();
    }

    @Override
    public float getProgress() {
        if (this.data == null) return 0f;
        return (float) this.ticks / (this.data.length() / this.pitch);
    }

    @Override
    public void refresh() {
        this.pitch = FlightSoundPlayer.getRandomPitch(tardis());
        this.setPosition(ClientTardisUtil.getNearestConsole());
        this.ticks = 0;

        if (this.dirty || tardis() == null) {
            MinecraftClient.getInstance().getSoundManager().stop(this);
        }

        this.dirty = true;
    }

    @Override
    public FlightSound getData() {
        if (this.data == null && ClientTardisUtil.getCurrentTardis() != null)
            this.data = ClientTardisUtil.getCurrentTardis().stats().getFlightEffects();

        return this.data;
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
