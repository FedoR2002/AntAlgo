public interface GuiCallback {
//    void drawStepChanged(int value);
    void mapGenerationStarted(int canvasWidth, int canvasHeight);
//    void seaLevelChanged(int value);
    boolean startedOrStopped();
    void viewModeBotChanged(int viewModeBot);
//    void viewModeMapChanged(int viewModeMap);
    void setWorldScale(int worldScale);
//    void setPerlin(int perlin);
    // world parameters
    void setLRMirror(boolean lrMirror);
    void setTBMirror(boolean tbMirror);
    void setBotCount(int botCount);
    void setTraceDegrade(int traceDegrade);
    // genom parameters
    void setBotMovement(int botMovement);
    void setBotRotation(int botRotation);
    void setBotSensorAngle(int botSensorAngle);
    void setBotSensorDist(int botSensorDist);
    void setBotTraceForce(int botTraceForce);
    void setBotAvoid_OverTrace(boolean botAvoid_overTrace);
    void setBotOverTrace(int botOverTrace);
}
