package askisi1;

import input.DataSetLoader;
import input.DataSetMaker;
import static java.lang.Math.*;
import java.io.PrintWriter;
import java.util.Random;

public class MLP {

	
	private static final String TRAININPUTFILE = "MLPTrainDataSet.txt";
	private static final String TESTINPUTFILE = "MLPTestDataSet.txt";
	private static final String RESULTSFOLDER = "MLPResults/";
	private static final String RESULTSOUTPUTFILE = RESULTSFOLDER+"MLPplotData.txt";;
	private static final int TRAINSETSIZE = 4000;
	private static final int TESTSETSIZE = 4000;
	private static final int D = 2;
	public static final int K = 4;
	private static final int B = 10;
	private static final int BATCHSIZE = TRAINSETSIZE/B;
	private static final int ACTIVATIONFUNCTION = 0; // 0 = Relux, 1 = Tanh
	private static final double LEARNINGRATE = 1e-4;
	private static final double ERRORTHRESHOLD = 1e-3;
	private static final int MAXIMUMEPOCHS = 10000;
	private int H;
	private int L;
	private int[] layers;
	private double[][] trainSet = new double[TRAINSETSIZE][D];
	private double[][] testSet = new double[TESTSETSIZE][D];
	private int[] trainClasses;
	private int[] testClasses;
	private double[][] bias;
	private double[][][] weights;
	private DataSetLoader dataSetLoader = DataSetLoader.getInstance();
	private Random rand = new Random();
	
	
	public MLP(int[] hiddenLayers) {
		this.H = hiddenLayers.length;
		this.L = H+2;
		bias = new double[H+1][];
		weights = new double[H+1][][];
		
		loadDataSets();
		classifyDataSets();
		initializeArrays(hiddenLayers);
	}
	
	
	public void loadDataSets() {
		dataSetLoader.setLoader(TRAININPUTFILE, trainSet);
		dataSetLoader.setLoader(TESTINPUTFILE, testSet);
	}
	
	public void classifyDataSets() {
		SetClassifier setClassifier = new SetClassifier();
		trainClasses = setClassifier.classifyDataSet(trainSet);
		testClasses = setClassifier.classifyDataSet(testSet);
		setClassifier.noisifySet(trainClasses);
	}

	public void initializeArrays(int[] hiddenLayers) {
		
		layers = new int[L];
		layers[0] = D;
		for (int i = 1; i < H+1; i++) {
			layers[i] = hiddenLayers[i-1]; 
		}
		layers[L-1] = K;

		
		for (int i = 0; i < L-1; i++) {
			weights[i] = new double[layers[i]][layers[i+1]];
			bias[i] = new double[layers[i+1]];
		}
		

		for (int i = 0; i < L-1; i++) {
			for (int j = 0; j < layers[i]; j++) {
				for (int k = 0; k < layers[i+1]; k++) {
					weights[i][j][k] = getRandomDouble(-1,1);
				}
			}
		}
		
		for (int i = 0; i < L-1; i++) {
			for (int j = 0; j < layers[i+1]; j++) {
				bias[i][j] = getRandomDouble(-1,1);
			}
		}
	}
	
	
	public double getRandomDouble(double min, double max) {
		return  rand.nextDouble() * (max - min) + (min);	
	}
		
	
	public double runEpoch() {
		double error = 0.0;
		
		for (int b = 0; b < B; b++) {  //Train with each batch
			
			double[][][] weightDerivativeSum = new double[H+1][][];
			double[][] biasDerivativeSum = new double[H+1][];
			
			for (int i = 0; i < L-1; i++) {
				weightDerivativeSum[i] = new double[layers[i]][layers[i+1]];
				biasDerivativeSum[i] = new double[layers[i+1]];
			}
			
			
			for (int i = b*BATCHSIZE; i < (b+1)*BATCHSIZE; i++) {
				error += backProp(i, weightDerivativeSum, biasDerivativeSum);
			}
			
			//Update weights for each batch
			for (int i = 0; i < L-1; i++) {
				for (int k = 0; k < weights[i][0].length; k++) {
					for (int j = 0; j < weights[i].length; j++) {
						weights[i][j][k] -= LEARNINGRATE*weightDerivativeSum[i][j][k];
					}
					bias[i][k] -= LEARNINGRATE*biasDerivativeSum[i][k];
				}
			}
		}
		
		return error/2;
	}
	
	
	public double backProp(int xIndex, double[][][] weightDerivativeSum, double[][] biasDerivativeSum) {
		double[][] gu = forwardPassAll(trainSet[xIndex]);
		
		double[][] delta = calculateDelta(xIndex, gu);
		
		for (int a = 0; a < L-1; a++) {
			for (int x = 0; x < weights[a][0].length; x++) {
				for (int j = 0; j < weights[a].length; j++) {
					weightDerivativeSum[a][j][x] += delta[a][x] * gu[a][j];
				}
				biasDerivativeSum[a][x] += delta[a][x];
			}
		}
		
		double[] output = gu[L-1];
		
		double[] t = getClass(trainClasses[xIndex]);
		
		return getSquareError(t, output);
	}
	
	
	public double[][] forwardPassAll(double[] x){
		
		double[][] gu = new double[L][];
		
		for (int j = 0; j < L; j++) {
			gu[j] = new double[layers[j]];
		}
		
		gu[0] = x;  //Input
		
		for (int i = 0; i < L-2; i++) {
			forwardPass(gu[i], weights[i], gu[i+1], bias[i]);
		}
		
		
		forwardPassOutput(gu[L-2], weights[L-2], gu[L-1], bias[L-2]);
		
		return gu;
	}
	
	
	public void forwardPass(double[] previousArray, double[][] weight, double[] nextArray, double[] bias){
		double u;
		
		for (int j = 0; j < nextArray.length; j++) {
			u = bias[j];
			
			for (int i = 0; i < previousArray.length; i++) {
				u += previousArray[i]*weight[i][j];
			}
			
			if (ACTIVATIONFUNCTION == 0 ) {
				nextArray[j] = getReLU(u);
			}
			else {
				nextArray[j] = tanh(u);
			}
		}
	}
	
	
	public double getReLU(double u) {
		if (u > 0) {
			return u;	
		}
		return 0;
	}
	
	
	public void forwardPassOutput(double[] previousArray, double[][] weight, double[] nextArray, double[] bias){
		double u;
		for (int j = 0; j < nextArray.length; j++) {
			u = bias[j];
			
			for (int i = 0; i < previousArray.length; i++) {
				u += previousArray[i]*weight[i][j];
			}
			nextArray[j] = getLogistic(u);
		}
	}
	
	
	public double getLogistic(double u) {
		return 1/(1+exp(-u));
	}
	
	
	public double[] getClass(int i) {
		
		if (i == 0) {
	        return new double[] {1.0,0.0,0.0,0.0};
	    }
		
		
		if(i == 1) {
	        return new double[] {0.0,1.0,0.0,0.0};
	    }
		
		
		if(i == 2) {
	        return new double[] {0.0,0.0,1.0,0.0};
		}
		
		return new double[] {0.0,0.0,0.0,1.0};
	}
	
	
	public double getSquareError(double[] y, double[] output) {
		double sum = 0.0;
		
		for (int i = 0; i < y.length; i++) {
			sum += Math.pow(y[i] - output[i], 2);
		}
		return sum;
	}
	
	
	public double[][] calculateDelta(int xIndex, double[][] gu) {
		
		double[] output = gu[L-1];
		double[] t = getClass(trainClasses[xIndex]);
		
		
		double[][] delta = new double[H+1][];
		
		for (int i = 0; i < L-1; i++) {
			delta[i] = new double[layers[i+1]];
		}
		
		
		for (int i = 0; i < K; i++) {
			delta[L-2][i] = (output[i] - t[i]) * getLogisticDerivative(output[i]);
		}
		
		
		for (int i = L-2; i > 0; i--) {
			for (int j = 0; j < weights[i].length; j++) {
				
				double sum = 0;
				for (int k = 0; k < weights[i][0].length; k++) {
					sum += weights[i][j][k] * delta[i][k];
				}
				
				if (ACTIVATIONFUNCTION == 0 ) {
					delta[i-1][j] = getReLUDerivative(j)*sum;
				}
				else {
					delta[i-1][j] = getTanhDerivative(j)*sum;
				}
				
			}
		}	
		
		return delta;
	}
	
	
	public double getReLUDerivative(double gU) {
		if (gU > 0) {
			return 1;	
		}
		return 0;
	}
	
	
	public double getTanhDerivative(double gU) {
		return 1-pow(gU,2);
	}
	
	
	public double getLogisticDerivative(double gU) {
		return gU*(1-gU);
	}
	
	
	public void trainMLP() {
		int numOfEpochs = 1;
		double currentError = 1;
		double previousError = Integer.MAX_VALUE;
		double errorDifference = abs(previousError - currentError);
		
		
		while ((numOfEpochs < 700 ||  errorDifference > ERRORTHRESHOLD) && numOfEpochs < MAXIMUMEPOCHS) {

			previousError = currentError;
			currentError = runEpoch();
			errorDifference = abs(previousError - currentError);

			System.out.println("Epoch: " + numOfEpochs + ", Error = "+ currentError);
			numOfEpochs++;
		}
		
		writeDataToFile(numOfEpochs, currentError);
		testMLP();
		
	}
	
	
	public void testMLP() {
		int successfulPredictions = 0;
		boolean[] predictions = new boolean[TESTSETSIZE];
		
		for (int i = 0; i < TESTSETSIZE; i++) {
			
			double[][] gu = forwardPassAll(testSet[i]);
			
			double[] output = gu[L-1];
			
			int pred = getMaxClass(output);
			
			if (pred == testClasses[i]) {
				predictions[i] = true;
				successfulPredictions += 1; 
			}
		}
		
		
		double accuracy = getAccuracy(successfulPredictions);
		
		
		System.out.println("Successful Predictions = "+successfulPredictions);
		System.out.println("Accuracy = "+ accuracy +"%");
		System.out.println();
		
		writeAccuracy(accuracy);
		writePlotDataToFile(predictions);
	}
	
	
	public int getMaxClass(double[] output) {
			double max = output[0];
			int pos = 0;
			
			for (int i = 1; i < output.length; i++) {
				if (output[i] > max) {
					max = output[i];
					pos = i;
				}
			}
			return pos;
	}
	
	
	public double getAccuracy(int successfulPredictions) {
		return (double)successfulPredictions*100/TESTSETSIZE;
	}
	
	
	public void writeDataToFile(int numOfEpochs, double error) {
		DataSetMaker setMaker = DataSetMaker.getInstance();
		PrintWriter outputWriter = setMaker.getPrintWriter(RESULTSFOLDER+"AccuracyResults.txt", true);
		
		outputWriter.println("Neural Network Properties:");
		outputWriter.println("Learning Rate = "+LEARNINGRATE);
		
		if (ACTIVATIONFUNCTION == 0 ) {
			outputWriter.println("Activation Function = RelU");
		}
		else {
			outputWriter.println("Activation Function = Tanh");
		}
		
		outputWriter.print("Hidden Layers = [");
		for (int i = 1; i < H; i++) {
			outputWriter.print(layers[i]+", ");
		}
		outputWriter.print(layers[H]);
		outputWriter.println("]");
		outputWriter.println("Batches = "+ B + " BatchSize = " + BATCHSIZE);
		outputWriter.println("Epochs = "+numOfEpochs+" Error = "+ error);
		outputWriter.close( );
	}
	
	public void writeAccuracy(double accuracy) {
		DataSetMaker setMaker = DataSetMaker.getInstance();
		PrintWriter outputWriter = setMaker.getPrintWriter(RESULTSFOLDER+"AccuracyResults.txt", true);
		
		outputWriter.println("Accuracy = "+ accuracy +"%");
		outputWriter.close( );
	}
	
	public void writePlotDataToFile(boolean[] predictions) {
		DataSetMaker setMaker = DataSetMaker.getInstance();
		PrintWriter outputWriter = setMaker.getPrintWriter(RESULTSOUTPUTFILE);
		
		for (int i = 0; i < TESTSETSIZE; i++) {
			if (predictions[i]) {
				outputWriter.println(testSet[i][0]+" "+testSet[i][1]);
			}
		}
		outputWriter.println();
		outputWriter.println();
		
		for (int i = 0; i < TESTSETSIZE; i++) {
			if (!predictions[i]) {
				outputWriter.println(testSet[i][0]+" "+testSet[i][1]);
			}
		}
		
		outputWriter.close( );
	}
	
	
	public static void main(String[] args){
		
		MLP mlp = new MLP(new int[] {60, 30, 15});
		mlp.trainMLP();
	}
	
}
