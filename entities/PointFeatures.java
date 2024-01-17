package entities;

import java.util.UUID;

public class PointFeatures {
    public int trace_force = 0;
    public UUID bot = null;

    public int getTrace_force(){
        return trace_force;
    }

    public void incTrace_force(int inc){
        trace_force+=inc;
        trace_force = Math.min(trace_force, 2000);
    }

    public void decTrace_force(int dec){
        if (dec<trace_force)
            trace_force-=dec;
        else
            trace_force=0;
    }

    public UUID getBot(){
        return bot;
    }

    public void setBot(UUID placeBot){
        bot = placeBot;
    }
}
