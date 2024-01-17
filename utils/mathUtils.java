package utils;

import entities.World_descr;

import java.awt.*;

public class mathUtils {
    public static Point calcdirpoint(Point ref, int dirAngle, int distance){
        double dx=1.f*distance*Math.cos(Math.PI*dirAngle/180);
        double dy=1.f*distance*Math.sin(Math.PI*dirAngle/180);

        // check if mirror or cicle
        int newx = ref.x+(int)dx;
        int newy = ref.y+(int)dy;

        if (newx>=World_descr.getInstance().getWidth()){
            if (World_descr.getInstance().isLR_mirror())
                newx = 2*World_descr.getInstance().getWidth()-newx-1;
            else
                newx -= World_descr.getInstance().getWidth();
        }
        if (newx<0){
            if (World_descr.getInstance().isLR_mirror())
                newx = -newx;
            else
                newx = World_descr.getInstance().getWidth()+newx;
        }


        if (newy>=World_descr.getInstance().getHeight()){
            if (World_descr.getInstance().isTB_mirror())
                newy = 2*World_descr.getInstance().getHeight()-newy-1;
            else
                newy -= World_descr.getInstance().getHeight();
        }
        if (newy<0){
            if (World_descr.getInstance().isTB_mirror())
                newy = -newy;
            else
                newy = World_descr.getInstance().getHeight()+newy;
        }

        return new Point(newx, newy);
    }
}
