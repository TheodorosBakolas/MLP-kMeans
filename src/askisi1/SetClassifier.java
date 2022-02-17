package askisi1;

import static java.lang.Math.pow;
import java.util.Random;

public class SetClassifier {
	
	private Random rand = new Random();	
	
	public int[] classifyDataSet(double[][] dataSet) {
		int[] classes = new int[dataSet.length];
		
		for (int i = 0; i < dataSet.length; i++ ) {			
			classes[i] = classify(dataSet[i]);
		}
		
		return classes;
	}
	
	
	public int classify(double[] point) {
		double x1 = point[0], x2 = point[1];
		
		double check = pow(x1 - 0.5, 2) + pow(x2 - 0.5, 2);
		if (check < 0.16) {
			return 0;
		}

		
		check = pow(x1 + 0.5, 2) + pow(x2 + 0.5, 2);
		if (check < 0.16) {
		    return 0;
		}
		
		
		check = pow(x1 - 0.5, 2) + pow(x2 + 0.5, 2);
		if (check < 0.16) {
		    return 1;
		}
		
		
		check = pow(x1 + 0.5, 2) + pow(x2 - 0.5, 2);
		if (check < 0.16) {
		    return 1;
		}
		
		
		check = x1*x2;    //Positive product means 1st or 3rd Quadrant
		if (check >= 0) {
			return 2;
		}
		
		return 3;
	}
	
	
	public void noisifySet(int[] classes) {
		for (int i = 0; i < classes.length; i++ ) {			
			if (getProbability(0.1)) {
				classes[i] = getRandomInt(classes[i]);
			}
		}
	}
	
	
	public boolean getProbability(double probability) {
		return rand.nextDouble() <= probability;
	}
	
	public int getRandomInt(int exclude) {
		int random = rand.nextInt(MLP.K-1);
		while (random == exclude) {
			random = rand.nextInt(MLP.K-1);
		}
		return random;
	}
}
