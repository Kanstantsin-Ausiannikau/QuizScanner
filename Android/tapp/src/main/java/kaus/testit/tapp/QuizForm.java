package kaus.testit.tapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class QuizForm {
	
	private Mat _quizNumber;
	private Mat _respondentNumber;
	private Mat _quizAnswerArea1;
	private Mat _quizAnswerArea2;
	private Mat _quizAnswerArea3;

	private  Mat _image;

	private Answer answer = null;

	private boolean isValid = true;

	private  int CURRENT_WIDTH;


	public QuizForm(byte[] data){

		Mat ocvImg1 = Highgui.imdecode(new MatOfByte(data), Highgui.IMREAD_UNCHANGED);

		//Core.flip(ocvImg1, ocvImg1,0);

		Core.transpose(ocvImg1,ocvImg1);

		Core.flip(ocvImg1,ocvImg1,1);

		Mat mGray1 = new Mat();

		Imgproc.cvtColor(ocvImg1, mGray1, Imgproc.COLOR_RGB2GRAY);

		ParseForm(mGray1);

		String quizNumber = "";
		String respondentNumber = "";
		int[][] answers = null;

		if (isValid) {
			quizNumber = getQuizNumber();
		}
		if (isValid) {
			respondentNumber = getRespondentNumber();
		}
		if (isValid) {
			answers = getAnswers();

			Log.d("Quiz", "Really answers null");
		}
		if (isValid) {
			answer = new Answer(
					-1,
					quizNumber,
					respondentNumber,
					answers,
					false,
					Calendar.getInstance().getTime().toString(),
					_image
			);
		}
		else {
			answer = null;
		}
	}

	Answer getAnswer(){
		return this.answer;
	}

	private void ParseForm(Mat paper){
		int CURRENT_HEIGHT = (int) paper.size().height;
	    CURRENT_WIDTH = (int)paper.size().width;
		double DEFAULT_HEIGHT = 2560.0;
		double SCALE_KOEF = DEFAULT_HEIGHT / CURRENT_HEIGHT;

		paper = normalizeMat(paper);
		Mat normalizedPaper = paper.clone();

		int BIG_PAPER_AREA = 2000;
		int BIG_PAPER_HEIGHT = 100;
		List<Rect> mainAreas = getAreaRectangles(normalizedPaper, BIG_PAPER_AREA / SCALE_KOEF, BIG_PAPER_HEIGHT / SCALE_KOEF);
		mainAreas = RemoveInternalRects(mainAreas);

		if(mainAreas.size()<5){
			isValid = false;
			return;
		}

		List<Rect> foundAreas =  findAreas(mainAreas);

		if (foundAreas==null){
			isValid = false;
			return;
		}

		Mat result = affineCorrection(paper, foundAreas.get(0), foundAreas.get(1), foundAreas.get(2), foundAreas.get(4));
		Mat preparedResult = result.clone();

		//_image = preparedResult.clone();

		mainAreas = getAreaRectangles(preparedResult, BIG_PAPER_AREA / SCALE_KOEF, BIG_PAPER_HEIGHT / SCALE_KOEF);
		mainAreas = RemoveInternalRects(mainAreas);

		if(mainAreas.size()<5){
			isValid = false;
			return;
		}

		foundAreas =  findAreas(mainAreas);

		if (foundAreas==null){
			isValid = false;
			return;
		}

		_quizNumber = new Mat(result, foundAreas.get(0));
		_respondentNumber = new Mat(result, foundAreas.get(1));
		_quizAnswerArea1 = new Mat(result, foundAreas.get(2));
		_quizAnswerArea2 = new Mat(result, foundAreas.get(3));
		_quizAnswerArea3 = new Mat(result, foundAreas.get(4));

		_image = _quizNumber.clone();
	}

	private Mat affineCorrection(Mat paper, Rect quizNumberRect, Rect respondentNumberRect, Rect answerArea1, Rect answerArea3) {
		//int highWidth = quizNumberRect.x + respondentNumberRect.x + respondentNumberRect.width;
		//int lowWidth = answerArea1.x + answerArea3.x + answerArea3.width;

		MatOfPoint2f dst;
		MatOfPoint2f src;

		Mat affineResult = new Mat(paper.size(), CvType.CV_8UC1);

		//if (highWidth < lowWidth) {
			dst = new MatOfPoint2f(
					new Point(quizNumberRect.x, quizNumberRect.y),
					new Point(respondentNumberRect.x + respondentNumberRect.width, quizNumberRect.y),
					new Point(quizNumberRect.x, answerArea1.y + answerArea1.height),
					new Point(respondentNumberRect.x + respondentNumberRect.width, answerArea3.y + answerArea3.height));

			src = new MatOfPoint2f(
					new Point(quizNumberRect.x, quizNumberRect.y),
					new Point(respondentNumberRect.x + respondentNumberRect.width, respondentNumberRect.y),
					new Point(answerArea1.x, answerArea1.y + answerArea1.height),
					new Point(answerArea3.x + answerArea3.width, answerArea3.y + answerArea3.height));

			Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);
			Imgproc.warpPerspective(paper, affineResult, warpMat, paper.size());
		//}
		return affineResult;
	}

	private List<Rect> findAreas(List<Rect> mainAreas) {

		List<Rect> foundAreas = new ArrayList<>();
		foundAreas.add(new Rect());
		foundAreas.add(new Rect());
		foundAreas.add(new Rect());
		foundAreas.add(new Rect());
		foundAreas.add(new Rect());

		int countFoundAreas = 0;

		for(int i=0;i<mainAreas.size();i++) {
			if (checkArea(mainAreas.get(i), 0.6, 0.2)) {
				//_quizNumber = new Mat(paper, mainAreas.get(i));
				if (mainAreas.get(i).x < CURRENT_WIDTH / 2) {
					foundAreas.set(0, mainAreas.get(i));
					countFoundAreas++;
				} else {
					foundAreas.set(1, mainAreas.get(i));
					countFoundAreas++;
				}
			}

			if (checkArea(mainAreas.get(i), 2.8, 0.5)) {
				//_respondentNumber = new Mat(paper, mainAreas.get(i));

				if (mainAreas.get(i).x < CURRENT_WIDTH / 4) {
					foundAreas.set(2, mainAreas.get(i));
					countFoundAreas++;
				} else {
					if (mainAreas.get(i).x > CURRENT_WIDTH / 2) {
						foundAreas.set(4, mainAreas.get(i));
						countFoundAreas++;
					} else {
						foundAreas.set(3, mainAreas.get(i));
						countFoundAreas++;
					}
				}
			}
		}

		boolean isAllFoundAreas = true;

		for(int i=0;i<5;i++){
			if (foundAreas.get(i)==null){
				isAllFoundAreas = false;
				break;
			}
		}

		return isAllFoundAreas?foundAreas:null;
	}

	private boolean checkArea(Rect rect, double ratio, double error) {
		boolean isCheck = false;
		if (Math.abs(((float)rect.height/rect.width)-ratio)<error){
			isCheck = true;
		}
		return isCheck;
	}


	private List<Rect> RemoveInternalRects(List<Rect> mainAreas) {
		for(int i = 0;i<mainAreas.size();i++){
			for(int j=0;j<mainAreas.size();j++){
				if (IsIdentical(mainAreas.get(i), mainAreas.get(j))){
					mainAreas.remove(j);
				}
			}
		}
		return mainAreas;
	}

	private String getQuizNumber(){

		final int height = 4;
		final int width = 7;

		AreaList list = findCheckBoxes(_quizNumber,width,height);

		int[][] result = list.getResult();

		if (result!=null) {

			StringBuilder id = new StringBuilder();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (result[i][j] == 1) {
						switch (i){
							case 0:id.append("A");break;
							case 1:id.append("B");break;
							case 2:id.append("C");break;
							case 3:id.append("D");break;
							default:break;
						}
						id.append(j + 1);
					}
				}
			}

			Log.d("Quiz", id.toString());

			return id.toString();
		}
		else {
			isValid = false;
			return "";
		}
	}

	private AreaList findCheckBoxes(Mat area, int width, int height) {

		List<MatOfPoint> contours = new ArrayList<>();

		Mat image = new Mat();

		area.copyTo(image);

		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

		AreaList list = new AreaList(area, width,height);

		final double minContourArea = area.width()/(width*2+1)*area.width()/(width*2+1)*0.4;
		final double maxContourArea = area.width()/(width*2+1)*area.width()/(width*2+1)*2;

		for(int i=0; i< contours.size();i++){

			double contourArea = Imgproc.contourArea(contours.get(i));
			if (contourArea > minContourArea & contourArea < maxContourArea){ //200
				Rect rect = Imgproc.boundingRect(contours.get(i));
				list.addRect(rect);
			}

		}

		return list;
	}

	private String getRespondentNumber(){

		final int height = 4;
		final int width = 7;

		AreaList list = findCheckBoxes(_respondentNumber,width,height);

		int[][] result = list.getResult();

		if(result!=null) {

			StringBuilder id = new StringBuilder();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (result[i][j] == 1) {
						switch (i){
							case 0:id.append("A");break;
							case 1:id.append("B");break;
							case 2:id.append("C");break;
							case 3:id.append("D");break;
							default:break;
						}
						id.append(j + 1);
					}
				}
			}

			Log.d("Quiz", id.toString());

			return id.toString();
		}
		else {
			isValid = false;
			return  "";
		}
	}

	private int[][] getAnswers(){

		final int width = 5;
		final int height = 15;

		int [][] answers = new int[height*3][width];

		AreaList list1 = findCheckBoxes(_quizAnswerArea1,width,height);
		AreaList list2 = findCheckBoxes(_quizAnswerArea2,width,height);
		AreaList list3 = findCheckBoxes(_quizAnswerArea3,width,height);

		int[][] result1 = list1.getResult();
		int[][] result2 = list2.getResult();
		int[][] result3 = list3.getResult();

		if((result1!=null)&(result2!=null)&(result3!=null)) {

			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 5; j++) {
					if (result1[i][j] == 1) {
						answers[i][j] = result1[i][j];
					}
					if (result2[i][j] == 1) {
						answers[i + 15][j] = result2[i][j];
					}

					if (result3[i][j] == 1) {
						answers[i + 30][j] = result3[i][j];
					}
				}
			}

			return answers;
		}
		else {
			isValid = false;
			return null;
		}
	}
	
	private Mat normalizeMat(Mat picture){
		
	//	Imgproc.cvtColor(picture, picture, Imgproc.COLOR_RGB2GRAY);


        Imgproc.GaussianBlur(picture, picture, new Size(5,5),2,2);
        //Imgproc.adaptiveThreshold(picture,picture,256,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,13,5);
		//Imgproc.threshold(picture,picture,128,256,Imgproc.THRESH_BINARY);

		Imgproc.adaptiveThreshold(picture,picture,256,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,35,5);

		_image = picture.clone();

		return picture;
	}

	private List<Rect> getAreaRectangles(Mat picture, double contourArea, double height) {
		
		List<MatOfPoint> contours = new ArrayList<>();
		
		Imgproc.findContours(picture, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
		
		List<Rect> rects = new ArrayList<>();

		for(int i=0; i< contours.size();i++){
            if (Imgproc.contourArea(contours.get(i)) > contourArea ){ //200
                Rect rect = Imgproc.boundingRect(contours.get(i));
                
                if (rect.height > height){  //100
               		rects.add(rect);
               	}
            }
                	
        }

        return rects;
	}
	
	private boolean IsIdentical(Rect r1, Rect r2){
		
		return (r1.x<r2.x)&&(r1.y<r2.y)&&((r1.x+r1.width)>(r2.x+r2.width))&&((r1.y+r1.height)>(r2.y+r2.height))||((r2.x<r1.x)&&(r2.y<r1.y)&&((r2.x+r2.width)>(r1.x+r1.width))&&((r2.y+r2.height)>(r1.y+r1.height)));
	}
	
}
