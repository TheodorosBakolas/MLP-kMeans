package askisi2;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;


import input.DataSetLoader;
import input.DataSetMaker;


public class Kmeans {

	
	private static final int DATASETSIZE = 1200;
	private static final int ITERATIONS = 20;
	private static final String RESULTSFOLDER = "kMeansResults/";
	private static final String INPUTFILE = "kMeansDataSet.txt";
	private static final String ERRORSOUTPUTFILE = RESULTSFOLDER+"errorPlotData.txt";
	private int M; //MUST BE MADE STATIC FINAL
	private DataSetLoader dataSetLoader = DataSetLoader.getInstance();
	private String RESULTSOUTPUTFILE;
	private double[][] centroids;
	private double[][] dataSet = new double[DATASETSIZE][2];
	private int[] clusters;
	private Random rand = new Random();
	
	
	public Kmeans(int M) {
		this.M = M;
		this.RESULTSOUTPUTFILE = RESULTSFOLDER+"kMeansM"+M+"plotData.txt";
		dataSetLoader.setLoader(INPUTFILE, dataSet);
	}
	
	
	public int getRandomInt() {
		return  rand.nextInt(DATASETSIZE);	
	}
	
	
	public void runKMeans() {
		
		centroids = new double[M][2];
		clusters = new int[DATASETSIZE];
		double[] distances = new double[M];
		double[][] previousCentroids = new double[M][2];
		
		//Initialize Centroids
		for (int i = 0; i < M; i++) {	
			centroids[i] = Arrays.copyOf(dataSet[getRandomInt()], 2);
		}
		
		
		while (!arrayEquals(centroids, previousCentroids)) {
			
			previousCentroids = arrayCopy(centroids);
			
			for (int i = 0; i < DATASETSIZE; i++) {
				for (int j = 0; j < M; j++) {
					distances[j] = getEuclideanDistance(dataSet[i], centroids[j]);
				}
				clusters[i] = getMinPosition(distances);
			}
			
			calculateNewCentroids();
		}
	}
	
	
	public boolean arrayEquals(double[][] array1, double[][] array2) {
		for (int i = 0; i < array1.length; i++) {
			for (int j = 0; j <  array1[0].length; j++) {
				if (array1[i][j] != array2[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	public double[][] arrayCopy(double[][] oldArray){
		double[][] newArray = new double[oldArray.length][oldArray[0].length];
		
		for (int i = 0; i < oldArray.length; i++) {
			for (int j = 0; j < oldArray[0].length; j++) {
				newArray[i][j] = oldArray[i][j];
			}
		}
		
		return newArray;
	}
	
	
	public double getEuclideanDistance(double[] a, double[] b) {
		return Math.pow(a[0]-b[0], 2) + Math.pow(a[1] - b[1], 2);
	}
	
	
	public int getMinPosition(double[] array) {
		double min = array[0];
		int position = 0;
		
		for (int i = 1; i < array.length; i++) {
			if (array[i] < min) {
				min = array[i];
				position = i;
			}
		}
		
		return position;
	}
	
	
	public void calculateNewCentroids() {
		double[][] centroidSum = new double[M][2];
		int[] clusterSize = new int[M];
		
		int currentClass;
		
		for (int i = 0; i < DATASETSIZE; i++) {
			currentClass = clusters[i];
			addArrays(centroidSum[currentClass], dataSet[i]);
			clusterSize[currentClass]++;
		}
		
		
		for (int i = 0; i < M; i++) {
			
			//In case an empty cluster is created re-initialize it
			if (clusterSize[i] == 0) {
				centroids[i] = Arrays.copyOf(dataSet[getRandomInt()], 2);
			}
			else {
				centroids[i][0] =  centroidSum[i][0] / (double) clusterSize[i];
				centroids[i][1] =  centroidSum[i][1] / (double) clusterSize[i];
			}
		}	
	}
	
	
	public void addArrays(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			a[i] += b[i];
		}
	}
	
	
	public void testKMeans() {
		
		double[] errors = new double[ITERATIONS];
		double[][][] iterCentroids = new double[ITERATIONS][M][2];
		int[][] iterClusters = new int[ITERATIONS][DATASETSIZE];
		
		for (int i = 0; i < ITERATIONS; i++) {
			runKMeans();
			
			errors[i] = calculateError();
			
			System.out.println("Iteration "+ (i+1) + ", M = "+ M + ", Error = " + errors[i]);
			
			iterCentroids[i] = arrayCopy(centroids);
			iterClusters[i] = Arrays.copyOf(clusters, DATASETSIZE);
		}
		
		
		int minErrorPosition = getMinPosition(errors);
		
		writeErrorToFile(errors[minErrorPosition]);		
		writePlotDataToFile(iterCentroids[minErrorPosition], iterClusters[minErrorPosition]);
	}
	
	
	public double calculateError() {
		
		double distanceSum = 0.0;
		int currentClass;
		
		for (int i = 0; i < DATASETSIZE; i++) {
			currentClass = clusters[i];
			distanceSum += getEuclideanDistance(dataSet[i], centroids[currentClass]);
		}
		
		return distanceSum;
	}
	
	
	public void writeErrorToFile(double error) {
		DataSetMaker setMaker = DataSetMaker.getInstance();
		PrintWriter outputWriter = setMaker.getPrintWriter(ERRORSOUTPUTFILE, true);
		outputWriter.println(M+" "+error);
		outputWriter.close( );
	}
	
	
	public void writePlotDataToFile(double[][] centroids, int[] clusters) {
		DataSetMaker setMaker = DataSetMaker.getInstance();
		PrintWriter outputWriter = setMaker.getPrintWriter(RESULTSOUTPUTFILE);
		
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < DATASETSIZE; j++) {
				if (centroids[clusters[j]] == centroids[i]) {
					outputWriter.println(dataSet[j][0]+" "+dataSet[j][1]);
				}
			}
			outputWriter.println();   //Double empty line so the clusters can be identified when creating the plot
			outputWriter.println();
		}
		
		for (int i = 0; i < M-1; i++) {
			outputWriter.println(centroids[i][0]+" "+centroids[i][1]);
			outputWriter.println();			//Double empty line so the clusters can be identified when creating the plot
			outputWriter.println();
		}
		outputWriter.println(centroids[M-1][0]+" "+centroids[M-1][1]);
		
		outputWriter.close( );
	}
	
	
	public static void main(String[] args){
		
		Kmeans kMeans;
		int[] numberOfClusters = {3,5,7,9,11,13};
		
		for (int M : numberOfClusters) {
			kMeans = new Kmeans(M);
			kMeans.testKMeans();
		}
	}
}
