package entities;

import utils.mathUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

public class Bot {
    // bot parameters
    // genom
    private int movement_distance;
    private int sensor_distance;
    private int side_sensor_angle;
    private int rotation_angle;
    private int traceForce;
    private int sideForce;
    private boolean avoid_overTrace;
    private int overTraceLevel;

    // custom
    private Point position;
    private int direction;
    private UUID UID;

    public Bot(){
        direction =0;
        movement_distance=0;
        sensor_distance=0;
        side_sensor_angle=0;
        rotation_angle=0;
        traceForce = 50;
        sideForce = 10;
        avoid_overTrace = false;
        overTraceLevel = 500;
        position = new Point(0,0);
        UID = UUID.randomUUID();
    }

    public Bot(Bot ref){
        direction = ref.direction;
        movement_distance= ref.movement_distance;
        sensor_distance= ref.sensor_distance;
        side_sensor_angle= ref.side_sensor_angle;
        rotation_angle=ref.rotation_angle;
        traceForce = ref.traceForce;
        sideForce = ref.sideForce;
        avoid_overTrace = ref.avoid_overTrace;
        overTraceLevel = ref.overTraceLevel;
        position = new Point(ref.position);
        UID = UUID.randomUUID();
    }

    public void setPosition(int x, int y){
        position.setLocation(x,y);
    }

    public Point getPosition(){
        return position;
    }

    public void setDirection(int dir){
        direction = dir;
    }

    public void setMovement_distance(int dist){
        movement_distance = dist;
    }

    public void setRotation_angle(int angle){
        rotation_angle = angle;
    }

    public void setSensor_distance(int dist){
        sensor_distance = dist;
    }

    public void setSide_sensor_angle(int angle) {
        side_sensor_angle = angle;
    }

    public void setTraceForce(int botTraceForce) {
        traceForce = botTraceForce;
        sideForce = Math.round(traceForce/5.f);
    }

    public void setAvoid_overTrace(boolean avoidOverTrace) {
        avoidOverTrace = avoidOverTrace;
    }

    public void setOverTraceLevel(int over_TraceLevel){
        overTraceLevel = over_TraceLevel;
    }

    public UUID getUID(){
        return UID;
    }

    public void move(){
        int[] move_angles = {direction, direction-rotation_angle, direction+rotation_angle,
                direction+180, direction-rotation_angle+180, direction+rotation_angle+180};
        int new_dir=checkRotate();
        Point new_pos=mathUtils.calcdirpoint(position, move_angles[new_dir], movement_distance);
        World_descr.getInstance().removeBot(position);
        World_descr.getInstance().putBot(new_pos, UID);
        World_descr.getInstance().getPointFeatures(new_pos).incTrace_force(traceForce);
        Point p1=new Point(new_pos);
        p1.x--;
        p1.y--;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        p1.x++;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        p1.x++;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        p1.y++;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        p1.y++;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        p1.x--;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        p1.x--;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        p1.y--;
        try {
            World_descr.getInstance().getPointFeatures(p1).incTrace_force(sideForce);
        }
        catch (Exception e){}
        position = new_pos;
        direction = move_angles[new_dir]%360;
    }

    private int checkRotate(){
        int[] trace_force = new int[3];
        // check forward trace
        Point checkPoint=mathUtils.calcdirpoint(position, direction, sensor_distance);
        trace_force[0]=World_descr.getInstance().getPointFeatures(checkPoint).getTrace_force();
        checkPoint=mathUtils.calcdirpoint(position, direction-side_sensor_angle, sensor_distance);
        trace_force[1]=World_descr.getInstance().getPointFeatures(checkPoint).getTrace_force();
        checkPoint=mathUtils.calcdirpoint(position, direction+side_sensor_angle, sensor_distance);
        trace_force[2]=World_descr.getInstance().getPointFeatures(checkPoint).getTrace_force();

        // all zeroes - go straight
        if (trace_force[0]==trace_force[1] && trace_force[0]==trace_force[2] && trace_force[0]==0)
            return 0;
        // if not avoid
        if (!avoid_overTrace){
            // find max trace
            if (trace_force[0]>trace_force[1]) {
                if (trace_force[0] > trace_force[2])
                    return 0;
                else
                    return 2;
            }
            if (trace_force[1]>trace_force[2]) {
                if (trace_force[1] >= trace_force[0])
                    return 1;
                else
                    return 0;
            }
            if (trace_force[2]>trace_force[0]) {
                if (trace_force[2] > trace_force[1])
                    return 2;
                else
                    return (int) Math.round(Math.random()+1.f);
            }
        }
        return 1;
    }


}
