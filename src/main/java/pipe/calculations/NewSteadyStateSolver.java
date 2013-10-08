package pipe.calculations;

import pipe.modules.rta.AnalyseResponse;

/**
 * Class that uses the Gauss-Seidel method for solving linear equations of the form Ax=b
 * to find the steady state probability distribution vector for a Markov chain, or semi-
 * Markov process.
 * 
 * @author Oliver Haggarty August 2007
 *
 */
public class NewSteadyStateSolver {
	/**
	 * Method that uses the Gauss-Seidel method to calculate the steady-state probability
	 * vector for a Markov chain extracted from a Petri net
	 * 
	 * @param matrixQTInd
	 * @param matrixQTData
	 * @return
	 */
	public static double [] solve(int[][] matrixQTInd, double[] matrixQTData) {
		int n;
		double [] pi = new double [n = matrixQTInd.length];
		double [] pisub1 = new double [n];
		
		for(int i = 0; i < n; i++) {
			pi[i] = 1;
			pisub1[i] = 1;
		}
		//Remember, matrix B is all 0's so don't need to create
		double Aqii;//just to keep compiler happy
		
		//Put you LaPlace transform here!
		//ComplexNumber f = new ComplexNumber(ComplexNumber.square((_s.plus(2.0)).transposedOver(2.0)));
		
		double sum;
		
		int iterations = 0;
		
		while(true) {
			/*//One iteration through values of x
			int kB = 0;
			int BSize = MBind[kB++];*/
			for(int i = 0; i < n; i++) {
			
				//sum = new ComplexNumber(MatrixB[i]);
				/*if(kB <= BSize*2 && MBind[kB] == i) {
					sum = new ComplexNumber(MBstore[MBind[++kB]]);
					kB++;
				}
				else
					sum = new ComplexNumber();*/
				sum = 0;
				
				int k = 0;
				int size = matrixQTInd[i][k++];
				//System.out.println("" + size);
				while(k <= size*2 - 1) {//Don't want to add diagonals
				/*	
					ComplexNumber Aq = new ComplexNumber(MAstore[matrixQTInd[i][k + 1]]);
					sum.minusEquals(Aq.times(_matrixX[matrixQTInd[i][k]]));
					//System.out.println("Aq = " + Aq + " * " + " MX" + MAind[i][k] + " = " + _matrixX[MAind[i][k]]);
					*/
					sum -= matrixQTData[matrixQTInd[i][k+1]] * pi[matrixQTInd[i][k]];
					
					k += 2;
				}
				sum /= (matrixQTData[matrixQTInd[i][k+1]]);
				pisub1[i] = pi[i];
				pi[i] = sum;
			}
			//Test of converged:
			double maxNum = 0.0, maxDen = 0.0, sub = 0.0;
			for(int i = 0; i < n; i++) {
				if((sub = Math.abs(pisub1[i] - pi[i])) > maxNum) {
					maxNum = sub;
				}
				if((sub = Math.abs(pisub1[i])) > maxDen)
					maxDen = sub;
			}
			if(maxNum / maxDen < 0.00000000001) {
				/*System.out.print("Result = [");
				printarray();*/
				
				return normalise(pi);
			}
			if(iterations++ > 2000000) {
				System.out.println("Error: Not converging");
				System.exit(1);
			}
		}
	}
	
	/**
	 * Method that uses the Gauss-Seidel method to calculate the steady-state probability
	 * vector for the Markov process that represents a GSPN
	 * 
	 * @param matrixQTInd Actually the P matrix now
	 * @param matrixQTData Actually the P matrix now
	 * @return
	 */
	private static double [] solveP(int[][] matrixQTInd, double[] matrixQTData) {
		int n;
		double [] pi = new double [n = matrixQTInd.length];
		double [] pisub1 = new double [n];
		double [] pit0 = new double[n];
		
		for(int i = 0; i < n; i++) {
			pi[i] = 1;
			pisub1[i] = 1;
		}
		
		
		double sum;
		
		int iterations = 0;
		
		while(true) {
			for(int i = 0; i < n; i++) {
			
				sum = 0;
				
				int k = 0;
				int size = matrixQTInd[i][k++];
				//System.out.println("" + size);
				while(k <= size*2 - 1) {//don't wnat to add diagonals
					double mq = matrixQTData[matrixQTInd[i][k+1]];
					double mpi = pi[matrixQTInd[i][k]];
				
					sum -=  mq * mpi; 
					//System.out.println("mq = " + mq + " * " + " MX" + matrixQTInd[i][k] + " = " + pi[matrixQTInd[i][k]]);
					k += 2;
				}
				double divisor = (matrixQTData[matrixQTInd[i][k+1]]);
				sum /= divisor;
				pisub1[i] = pi[i];
				pi[i] = sum;
			}
			//Test of converged:
			double maxNum = 0.0, maxDen = 0.0, sub = 0.0;
			for(int i = 0; i < n; i++) {
				if((sub = Math.abs(pisub1[i] - pi[i])) > maxNum) {
					maxNum = sub;
				}
				if((sub = Math.abs(pisub1[i])) > maxDen)
					maxDen = sub;
			}
			if(maxNum / maxDen < 0.00000000000001) {
				/*System.out.print("Result = [");
				RTA.printArray(pi);*/
				
				return normalise(pi);
			}
			if(iterations++ > 2000000) {
				System.out.println("Error: Not converging");
				System.exit(1);
			}
		}
	}
	
	/**
	 * Method that takes the x solution of the linear equation Ax=b and normalises it
	 * so that its elements add up to one.
	 * 
	 * @param pi Steady-state probability vector
	 * @return
	 */
	private static double[] normalise(double[] pi) {
		double sum = 0;

        for(double aPi : pi)
        {
            sum += aPi;
        }
		for(int i = 0; i < pi.length; i++) {
			pi[i] = pi[i]/sum;
		}
		return pi;
	}
	
	/**
	 * Test method
	 * @param args
	 */
	public static void main(String [] args) {
		int [][] P = { {2, 1, 1, 2, 2, 0, 0}, {2, 0, 3, 1, 5, 1, 4}, {2, 0, 6, 1, 7, 1, 8}};
		double [] Pdata = {-1, 0.4, 0.0,   0.7, -1.0, 0.5,   0.2, 0.6, -1};//P-I now
		double [] pi;
		pi = solveP(P, Pdata);
		AnalyseResponse.printArray(pi);
	}
	
}
