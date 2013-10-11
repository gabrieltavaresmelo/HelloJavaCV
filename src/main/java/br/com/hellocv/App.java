package br.com.hellocv;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_legacy.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import javax.swing.JFrame;

import com.googlecode.javacv.*;

/**
 * Hello world!
 * 
 */
public class App {
	
	public static final String XML_FILE = 
			"src/main/resources/haarcascade_frontalface_default.xml";
	
	/* Carrega o Detector de Faces */
	private static CvHaarClassifierCascade cascade = new 
			CvHaarClassifierCascade(cvLoad(XML_FILE));
	private static CvMemStorage storage = CvMemStorage.create();
	
	public static IplImage detect(IplImage src){
		int maxFaceSize = src.width() / 3;
		int minFaceSize = src.width() / 9; //quanto menor o minFaceSize, mais preciso ficara, pegando as faces de longe
		
		IplImage dest = src.clone();
		
		CvSeq faces = cvHaarDetectObjects(
				src,
				cascade,
				storage,
				1.1,
				3,
				CV_HAAR_DO_CANNY_PRUNING
				,cvSize(minFaceSize, minFaceSize), cvSize(maxFaceSize, maxFaceSize)
		);
		
		int total_Faces = faces.total();		
		 
		for(int i = 0; i < total_Faces; i++){
			CvRect r = new CvRect(cvGetSeqElem(faces, i));
			cvRectangle (
					dest,
					cvPoint(r.x(), r.y()),
					cvPoint(r.width() + r.x(), r.height() + r.y()),
					CvScalar.GREEN,
					2,
					CV_AA,
					0); 
		}
		
		cvClearMemStorage(storage);

		return dest;
	}
	
	private static void initialize() { 
		// 0-default camera, 1 - next...so on 
		final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		
		try {
			// Cria a Janela
			CanvasFrame canvasFrame = new CanvasFrame("Window 01");
			canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			// Inicia a captura da camera
			grabber.start();
			
			// Objeto imagem obtida a partir da captura da camera
			IplImage img = null;
						
			while (canvasFrame.isVisible() && (img = grabber.grab()) != null) {
				IplImage imageFaces = detect(img);
				
	            canvasFrame.showImage(imageFaces); // mostra a imagem na janela
//	            cvSaveImage("capture.jpg", img);
	            
	            imageFaces.release();
	        }
		
			// Desaloca os objetos
			img.release();
			grabber.release();
			canvasFrame.dispose();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		initialize();
	}
}
