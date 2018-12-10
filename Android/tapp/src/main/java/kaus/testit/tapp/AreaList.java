package kaus.testit.tapp;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

/**
 * Created by Ausiannikau on 16.01.2018.
 */

class AreaList {

    private Area[][] areas;

    private Mat paper;

    private int width;
    private int height;

    AreaList(Mat paper, int width, int height) {

        double h = (double) paper.height()/(height*2+1);
        double w = (double) paper.width()/(width*2+1);

        this.areas = new Area[height][];
        for(int i=0;i<height;i++){
            this.areas[i] = new Area[width];
            for(int j = 0;j<width;j++){
                this.areas[i][j] = new Area((2*j+1)*w-w/2,(2*i+1)*h-h/2,w*2,h*2);
            }
        }

        this.paper = paper;
        this.width = width;
        this.height = height;
    }

    public Rect get(int i, int j){
        return this.areas[i][j].getInternalRect();
    }

    int[][] getResult(){
        boolean isFillData = checkState();

        int [][] result = null;

        if (isFillData) {
            result = new int[height][width];

            for(int i=0;i<height;i++){
                for (int j=0;j<width;j++){

                    Rect checkArea = new Rect((int)(this.areas[i][j].getInternalRect().x+this.areas[i][j].getInternalRect().width*0.25),
                            (int)(this.areas[i][j].getInternalRect().y+this.areas[i][j].getInternalRect().height*0.25),
                            (int)(this.areas[i][j].getInternalRect().width*0.5),
                            (int)(this.areas[i][j].getInternalRect().height*0.5));

                    result[i][j] = isChecked(new Mat(this.paper, checkArea))?1:0;
                }
            }
        }

        return result;
    }

    private boolean checkState() {
        boolean isCheckState = true;
        for(int i=0;i<this.height;i++){
            for(int j=0;j<width;j++){
                if(this.areas[i][j].getInternalRect()==null){
                    isCheckState = false;
                }
            }
        }
        return isCheckState;
    }


    void addRect(Rect rect){

        for(int i =0;i<height;i++){
            for (int j=0;j<width;j++){
                areas[i][j].addRect(rect);
            }
        }
    }

    private boolean isChecked(Mat area){

        Core.bitwise_not(area, area);
        Scalar a = Core.sumElems(area);
        //final int LEVEL_THRESHOLD = 8000;
        final int LEVEL_THRESHOLD = 10000;
        Log.d("Quiz", "Level - " + String.valueOf(a.val[0]));
        return a.val[0]> LEVEL_THRESHOLD;
    }
}
