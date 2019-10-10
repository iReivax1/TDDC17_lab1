public class StateAndReward {
	private static int angleStates = 10;
	private static double angleMin = -Math.PI/(angleStates/2)*(angleStates/2-1);
	private static double angleMax = Math.PI/(angleStates/2)*(angleStates/2-1);
	private static int vxStates = 6;
	private static int vyStates = 4;
	private static double vMin = -2;
	private static double vMax = 2;

	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */

//		String state = "OneStateToRuleThemAll";
		int discreteAngle = discretize(angle, angleStates, angleMin, angleMax); 
		String state = Integer.toString(discreteAngle);
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
		double reward = 0;
//		if (Math.abs(angle) < Math.PI/2) {
//			reward = 1-Math.abs(angle)/(Math.PI/2);
//		}else {
//			reward = -1*(Math.abs(angle)-(Math.PI/2))/(Math.PI/2);
//		}
		reward = (Math.PI - Math.abs(angle)) / Math.PI; //normalize
		

		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
		int discreteAngle = discretize(angle, angleStates, angleMin, angleMax);
		int discreteVx = discretize(vx, vxStates, vMin, vMax);
		int discreteVy = discretize(vy, vyStates, vMin, vMax);
		
		String state = Integer.toString(discreteAngle) + "," + Integer.toString(discreteVx)
						+ "," + Integer.toString(discreteVy);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
	
		double rewardAngle = getRewardAngle(angle, 0, 0);
		double rewardVx = Math.exp((vMax - Math.abs(vx)) / vMax);
		double rewardVy = Math.exp((vMax - Math.abs(vy)) / vMax); //force it to be positive
		
		//double reward = Math.exp(1)*rewardAngle + (rewardVx + rewardVy);
		double reward = sigmoid(rewardAngle + (rewardVx + rewardVy)); //make it  0-1
		return reward;
	}
	
	public static double sigmoid(double x) {
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	  }

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}
