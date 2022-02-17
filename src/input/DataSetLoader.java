package input;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataSetLoader {

	
	private static DataSetLoader DATASETLOADER_INSTANCE;
	public static String DATASET_PATH = "DataSets/";
	
	public static DataSetLoader getInstance() {
        if (DATASETLOADER_INSTANCE == null) {
        	DATASETLOADER_INSTANCE = new DataSetLoader();
        }
        return DATASETLOADER_INSTANCE;
    }
	
	
	public void setLoader(String filename, double[][] dataSet) {
		readFile(getScanner(DATASET_PATH+filename), dataSet);
	}
	
	
	public Scanner getScanner(String fileName) {
		Scanner scanner = null;
		
		try {
			return new Scanner(new FileInputStream(fileName));
			
		} catch (FileNotFoundException e) {
			String errorMessage = "Missing "+fileName+" which is necessary for the program to be functional.";
			System.err.println(errorMessage);
		}
		return scanner;
	}
	
	
	public void readFile(Scanner scanner, double[][] dataSet) {
		int i = 0;
		
		while (scanner.hasNextLine()){
			String[] arrOfStr = scanner.nextLine().split(" ");
			dataSet[i][0] = Double.parseDouble(arrOfStr[0]);
			dataSet[i++][1] = Double.parseDouble(arrOfStr[1]);
		}
	}
}
