package kaus.testit.tapp;

import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 * Created by Ausiannikau on 24.01.2018.
 */

class Area {
    private double x;
    private double y;

    private double width;
    private double height;

    private Rect mInternalRect;

    Area(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    Rect getInternalRect() {
        return mInternalRect;
    }

    void addRect(Rect rect){

        Point middlePoint = new Point(rect.x+(rect.width)/2,rect.y+(rect.height)/2);
        boolean isPointIntoArea = ((middlePoint.x>this.x)&(middlePoint.x<(this.x+this.width)))&((middlePoint.y>this.y)&(middlePoint.y<(this.y+this.height)));

        if (isPointIntoArea) {
            if (this.mInternalRect == null) {
                this.mInternalRect = rect;
            }
            else{
                if (this.getRectArea()<rect.area()){
                    this.mInternalRect = rect;
                }
            }
        }
    }

    private double getRectArea(){
        return this.mInternalRect.area();
    }
}
