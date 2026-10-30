package com.cleanroom.gravestones.neoforge;

import com.cleanroom.gravestones.SoulGravestonesCore;

public class SoulGravestonesNeoForge {
    private final SoulGravestonesCore core = new SoulGravestonesCore();

    public SoulGravestonesNeoForge() {
        System.out.println("[CleanRoom NeoForge] Bootstrapping " + SoulGravestonesCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
