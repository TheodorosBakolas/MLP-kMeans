package input;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;


public class DataSetMaker {

	private static DataSetMaker SETMAKER_INSTANCE;
	private Random rand = new Random();
	
	
	public static DataSetMaker getInstance() {
        if (SETMAKER_INSTANCE == null) {
        	SETMAKER_INSTANCE = new DataSetMaker();
        }
        return SETMAKER_INSTANCE;
    }
	
	
	public PrintWriter getPrintWriter(String filePath) {
		PrintWriter printWriter = new PrintWriter(getFileOutputStream(filePath));
	
		return printWriter;
	}
	
	
	public FileOutputStream getFileOutputStream(String filePath) {
		
		FileOutputStream outputStream = null;
		
		try
		{
			outputStream = new FileOutputStream(filePath);
		}
			catch(FileNotFoundException e)
		{
			System.out.println("Error opening the file"+filePath);
			System.exit(0);
		}
		
		return outputStream;
	}
	
	
	public PrintWriter getPrintWriter(String filePath, boolean bool) {
		PrintWriter printWriter = new PrintWriter(getFileOutputStream(filePath, bool));
	
		return printWriter;
	}
	
	
	public FileOutputStream getFileOutputStream(String filePath, boolean bool) {
		
		FileOutputStream outputStream = null;
		
		try
		{
			outputStream = new FileOutputStream(filePath, bool);
		}
			catch(FileNotFoundException e)
		{
			System.out.println("Error opening the file"+filePath);
			System.exit(0);
		}
		
		return outputStream;
	}
	
	
	public double getRandomDouble(double max, double min) {
		return  rand.nextDouble() * (max - min) + (min);	
	}
	
	public void createMLPDataSet() {
		PrintWriter outputWriter = getPrintWriter("DataSets/MLPTrainDataSet.txt");
		double x,y, min = -1, max = 1;
		
		for (int i = 0; i < 4000; i++) {
			x = getRandomDouble(min,max);
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		outputWriter.close( );
		
		outputWriter = getPrintWriter("DataSets/MLPTestDataSet.txt");
		
		
		for (int i = 0; i < 4000; i++) {
			x = getRandomDouble(min,max);
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		outputWriter.close( );
	}
	
	
	public void createkMeansDataSet() {
		
		PrintWriter outputWriter = getPrintWriter("DataSets/kMeansDataSet.txt");
		double x,y, min, max;
		
		
		//Category1
		for (int i = 0; i < 150; i++) {
			min = 0.75;
			max = 1.25;
			x = getRandomDouble(min,max);
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		//Category2
		for (int i = 0; i < 150; i++) {
			min = 0.0;
			max = 0.5;
			x = getRandomDouble(min,max);
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		//Category3
		for (int i = 0; i < 150; i++) {
			min = 0.0;
			max = 0.5;
			x = getRandomDouble(min,max);
			min = 1.5;
			max = 2.0;
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		//Category4
		for (int i = 0; i < 150; i++) {
			min = 1.5;
			max = 2.0;
			x = getRandomDouble(min,max);
			min = 0.0;
			max = 0.5;
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		//Category5
		for (int i = 0; i < 150; i++) {
			min = 1.5;
			max = 2.0;
			x = getRandomDouble(min,max);
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		//Category6
		for (int i = 0; i < 75; i++) {
		    min = 0.6;
		    max = 0.8;
		    x = getRandomDouble(min,max);
		    min = 0.0;
		    max = 0.4;
		    y = getRandomDouble(min,max);
		    outputWriter.println(x+" "+y);
		}
		
		//Category7
		for (int i = 0; i < 75; i++) {
		    min = 0.6;
		    max = 0.8;
		    x = getRandomDouble(min,max);
		    min = 1.6;
		    max = 2.0;
		    y = getRandomDouble(min,max);
		    outputWriter.println(x+" "+y);
		}
		
		//Category8
		for (int i = 0; i < 75; i++) {
		    min = 1.2;
		    max = 1.4;
		    x = getRandomDouble(min,max);
		    min = 0.0;
		    max = 0.4;
		    y = getRandomDouble(min,max);
		    outputWriter.println(x+" "+y);
		}
		
		//Category9
		for (int i = 0; i < 75; i++) {
			min = 1.2;
			max = 1.4;
			x = getRandomDouble(min,max);
			min = 1.6;
			max = 2.0;
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		//Category10
		for (int i = 0; i < 150; i++) {
			min = 0.0;
			max = 2.0;
			x = getRandomDouble(min,max);
			y = getRandomDouble(min,max);
			outputWriter.println(x+" "+y);
		}
		
		outputWriter.close( );

	}
	
	
	public static void main(String[] args){
		
		DataSetMaker setMaker = new DataSetMaker();
		
		//setMaker.createMLPDataSet();
		//setMaker.createkMeansDataSet();
	}
	
}
