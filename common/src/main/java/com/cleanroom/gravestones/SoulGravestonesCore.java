package com.cleanroom.gravestones;

import com.cleanroom.common.api.CommonModInitializer;
import com.cleanroom.common.telemetry.CleanRoomTelemetry;

public class SoulGravestonesCore implements CommonModInitializer {
    public static final String MOD_ID = "cleanroom_soulgravestones";
    public static final String MOD_NAME = "Soul Gravestones CleanRoom";

    private static SoulGravestonesCore instance;

    public static SoulGravestonesCore getInstance() {
        if (instance == null) {
            instance = new SoulGravestonesCore();
        }
        return instance;
    }

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void onInitializeCommon() {
        instance = this;
        CleanRoomTelemetry.record("INIT", "Initialized core logic for " + MOD_NAME + " (" + MOD_ID + ")");
        System.out.println("[CleanRoom] Initialized core logic for " + MOD_NAME + " (" + MOD_ID + ")");
    }
}
