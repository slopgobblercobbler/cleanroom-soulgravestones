package com.cleanroom.gravestones.fabric;

import com.cleanroom.gravestones.SoulGravestonesCore;

public class SoulGravestonesFabric {
    private final SoulGravestonesCore core = new SoulGravestonesCore();

    public void onInitialize() {
        System.out.println("[CleanRoom Fabric] Bootstrapping " + SoulGravestonesCore.MOD_NAME);
        core.onInitializeCommon();
    }
}
