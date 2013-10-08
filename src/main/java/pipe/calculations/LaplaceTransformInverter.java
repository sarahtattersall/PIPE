package pipe.calculations;

import pipe.exceptions.NotConvergingException;
import pipe.utilities.math.ComplexNumber;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class that inverts the Laplace transform of the passage time of a (Semi) Markov chain.
 * This super class does this as a local job, but the sub class extends the functionality
 * to a distributed job on the Hadoop MapReduce platform.
 * @author Oliver Haggarty August 2007
 *
 */
public class LaplaceTransformInverter {
	//Euler variables
	static int evaluations = 0;//Number of time an L(s) value is calculated
	
	final double[] SU= new double[14];
	final double[] C = {0,1,11,55,165,330,462,462,330,165,55,11,1};

    final int Ntr = 50;
	
	double U, X, H;
	final ArrayList<Double> results = new ArrayList<Double>();
	
	//Gauss-Seidel variables

	static ArrayList<Integer> startStates;//list of start states
	static HashMap<Integer, Integer> targetStates;//list of target states
	//Matices of the form Ax = b 
	static int[][] MAind;//Sparse matrix representing Laplace transform
	static double[] MAstore;//of passage time of Markov chain represented by Q
	static int[] MBind;
	static double[] MBstore;
	static ComplexNumber[] _matrixX;
	static ComplexNumber[] _matrixXsub1;
	static double [] alphas;//alpha values for the start states
	//private double[] MatrixB;
	static int n;//size of A matrix
	
	static boolean calcResponse;
	static boolean calcCDF;
	static boolean retDivS;
	
	//Q Matrix from which Laplace transform matrixis created
    int [][] MQind;
	double [] MQdata;
	boolean [] MQTorV;//List of which states are tangible and which are vanishing
	
	//timing variables:
	private static double LaplaceGenTime;
	
	/**
	 * Evaluates the Laplace transform for a certain s-value. Uses Gauss-Seidel iterative
	 * method for evaluating linear equation Ax = b (where A is the Laplace transform)
	 * @param s ComplexNumber s-value to evaluate Laplace transform for
	 * @return Complex value representing L(s)
	 * @throws NotConvergingException
	 */
	static ComplexNumber fnRf(ComplexNumber s) throws NotConvergingException {
		
		ComplexNumber aqii = new ComplexNumber();//just to keep compiler happy
		
		//Put you LaPlace transform here!
		
		ComplexNumber sum;
		
		int iterations = 0;
		
		while(true) {
			//One iteration through values of x
			int kB = 0;
			int BSize = MBind[kB++];
			for(int i = 0; i < n; i++) {
			
				//sum = new ComplexNumber(MatrixB[i]);
				if(kB <= BSize*2 && MBind[kB] == i) {
					sum = new ComplexNumber(MBstore[MBind[++kB]]);
					kB++;
				}
				else
					sum = new ComplexNumber();
				
				int k = 0;
				int size = MAind[i][k++];
				//System.out.println("" + size);
				while(k <= size*2) {
					if(MAind[i][k] == i) {
						double temp = MAstore[MAind[i][k + 1]];
						if(temp != 0)
							aqii = s.plus(temp);
						else//Its a vanishing state
							aqii = new ComplexNumber(1);
						//System.out.println("aqii = " + aqii);
					}
					else {
						ComplexNumber aq = new ComplexNumber(MAstore[MAind[i][k + 1]]);
						sum.minusEquals(aq.times(_matrixX[MAind[i][k]]));
						//System.out.println("aq = " + aq + " * " + " MX" + MAind[i][k] + " = " + _matrixX[MAind[i][k]]);
					}
					k += 2;
				}
				sum.overEquals(aqii);
				_matrixXsub1[i] = _matrixX[i];
				_matrixX[i] = sum;
			}
			if(isConverge()) {
				/*System.out.print("Result = [");
				printarray();*/
				ComplexNumber result = new ComplexNumber();
				evaluations++;
				int i = 0;
				/*for(int st : startStates) {
					result.plusEquals(ComplexNumber.times(alphas[i++], _matrixX[st]));
				}*/
                for(Integer startState : startStates)
                {
                    result.plusEquals(ComplexNumber.times(alphas[i++], _matrixX[startState]));
                }
				if(retDivS)
					return result.over(s);
				else
					return result;
				
			}
			if(iterations++ > 20000) {
				System.out.println("Error: Not converging");
				throw new NotConvergingException();
			}
		}
		
		//System.out.println(s.getReal() + " + j" + s.getImag() + " " + f.getReal());
	}
	
	/**
	 * Returns true if the result of the Gauss-Seidel method has converged. Compares
	 * current x vector with previous x vector.
	 * @return
	 */
	static private boolean isConverge() {
		double maxNum = 0.0, maxDen = 0.0, sub;
		for(int i = 0; i < n; i++) {
			if((sub = ComplexNumber.abs(_matrixXsub1[i].minus(_matrixX[i]))) > maxNum) {
				maxNum = sub;
			}
			if((sub = ComplexNumber.abs(_matrixX[i])) > maxDen)
				maxDen = sub;
		}
		/*System.out.print("x = [");
		printarray();*/
		return maxNum / maxDen < 0.00000000000001;
	}
	
	/**
	 * Debugging function that prints the array storing the x vector
	 */
	private void printarray() {
		
		for(int i = 0; i < n; i++) {
			System.out.print(" " + _matrixX[i] + ",");
		}
		System.out.print("]\n");
	}	
	
	/**
	 * Entry point function for running the Laplace Transform inversion. Implements the
	 * Euler method of Laplace Transform inversion
	 * @param T The time point for which the Laplace Transform should be inverted
	 * @param calcRTA Boolean - true if want to calculate response time
	 * @return Probability
	 * @throws NotConvergingException
	 */
    double runEuler(double T, boolean calcRTA) throws NotConvergingException {
        double a = 19.1;
        U = (Math.exp(a /2.0))/T;
		X = a /(2.0*T);
		
		H = Math.PI/T;
		
		//System.out.println("About to calculate sum");

        //Debugging code to calculate known probabilities
		
		//double err = Math.abs(fun - fun1)/2.0;
		
		// straight exponential
		// double answer = 2.0*Math.exp(-2.0*T);
		// integrated
		// double answer = 1.0 - Math.exp(-2.0*T);
		// erlang2
		//double answer = 2.0*2.0*T*(Math.exp(-2.0*T));
		// erlang3
		//  double answer = 2.0*2.0*2.0*T*T*Math.exp(-2.0*T)/2.0;
		//erlang 10
		// double answer = Math.pow(2.0, 10.0)*Math.pow(T,9)*Math.exp(-2.0*T)/362880.0;
		//erlang 11(rate 2)
		//double answer = Math.pow(2.0, 11.0)*Math.pow(T, 10)*Math.exp(-2.0*T)/3628800.0;
		//erlang 12(rate 2)
		//double answer = Math.pow(2.0, 12.0)*Math.pow(T,11)*Math.exp(-2.0*T)/39916800.0;
		//erlang3(rate 1) + erlang12(rate 2)
		//double answer = (1.0*1.0*1.0*T*T*Math.exp(-1.0*T)/2.0 + Math.pow(2.0, 12.0)*Math.pow(T,11)*Math.exp(-2.0*T)/39916800.0)/2;
		
		//Print out results
		/*DecimalFormat TFormatter = new DecimalFormat("0.00");
		String TF = TFormatter.format(T);
		DecimalFormat fun1Formatter = new DecimalFormat("#.###############");
		String fun1F = fun1Formatter.format(fun1);
		String ansF = fun1Formatter.format(answer);
		String error = ((Double)(Math.round(((fun1 - answer)/answer*100000.0)/100))).toString();
		System.out.println(TF + ", " + fun1F + ", " + ansF + "," (" + error + "% error)");*/
		return calculateSum(T, calcRTA);
	}
	
	/**
	 * Performs the main Euler summation to calculate probability at time T.
	 * @param T time point to be calculated for
	 * @param calcRTA true if want to calculate response time point, otherwise returns cdf
	 * @return
	 * @throws NotConvergingException
	 */
    double calculateSum(double T, boolean calcRTA) throws NotConvergingException
    {
		//System.out.println("Local calcsum");
        retDivS = !calcRTA;
		double sum = fnRf(new ComplexNumber(X,0)).getReal()/2.0;
		
		for(int N = 1; N <= Ntr; N++) {
			double Y = N*H;
			double term = fnRf(new ComplexNumber(X,Y)).getReal();
			if((N%2) != 0)
				term = -term;
			sum += term;
		}
		
		SU[1] = sum;
		
		for(int K = 1; K <= 12; K++) {
			int N = Ntr + K;
			double Y = N*H;
			double term = fnRf(new ComplexNumber(X,Y)).getReal();
			
			//double DELTA = H;
			//ComplexNumber i  = new ComplexNumber(0, 1);
			
			//seemintly unused code commented out in Euler.cxx, not yet converted to Java:
			//double approxterm = (fnRf(X, Y-DELTA) + fnRfprime(X,Y-DELTA)*i*DELTA = 0.5*fnRfprimeprime(X,Y-DELTA)*DELTA*DELTA).real();
			
			//cout << "term = " << term << "approxterm = " << approxterm << " rel err = " << (approxterm-term)/term << " h = " << H < end;
			
			if((N%2) != 0)
				term = -term;
			SU[K+1] = SU[K] + term;			
		}
		
		double avgsu = 0.0, avgsu1 = 0.0;
		
		for(int J = 1; J <= 12; J++) {
			avgsu += C[J]*SU[J];
			avgsu1 += C[J]*SU[J+1];
		}
		
		//double fun = U*avgsu/2048.0; 
        return U*avgsu1/2048.0;
	}
	
	/**
	 * Creates the sparse matrix that represents the Laplace Transform of the passage time from 
	 * startStates to targetStates for the Markov chain represented by MatrixQ
	 * @throws IOException
	 */
    void createMatrix()
    {
		//System.out.println("Creating laplace transform matrix");
		long startMat = System.currentTimeMillis();
		int rowNZ, totalNZ = 0, SZMBdata = 0;
		int sCntA = 0, sCntB = 0, iCntA, iCntB = 1;//keeps track of how far through array we are
		boolean isTgt;
		

		n = MQind.length;
		MAind = new int[n][];
		_matrixX = new ComplexNumber[n];
		_matrixXsub1 = new ComplexNumber[n];
		
		//first calculate sizes of row matrixes needed
		for(int i = 0; i < n; i++) {
			int k = 1;
			rowNZ = 0;
			while(k < MQind[i].length) {
				isTgt = false;
				if(targetStates.containsKey(MQind[i][k]) && MQind[i][k] != i) {
					SZMBdata++;
					isTgt = true;	
				}
				if(!isTgt) {
					rowNZ++;
				}
				k += 2;
			}
			MAind[i] = new int[rowNZ*2+1];
			MAind[i][0] = rowNZ;
			totalNZ += rowNZ;
		}
		//Create matrices now we know required size
		MAstore = new double[totalNZ];
		MBstore = new double[SZMBdata];
		MBind = new int[SZMBdata*2+1];
		MBind[0] = SZMBdata;
		//now fill the matrices Q and B
		for(int i = 0; i < MQind.length; i++) {
			int k = 1;
			double sum = 0;
			iCntA = 1;
			while(k < MQind[i].length) {
				isTgt = false;
				if(targetStates.containsKey(MQind[i][k]) && MQind[i][k] != i) {
					//Move to matrix B
					sum += MQdata[MQind[i][++k]];
					isTgt = true;
				}
				if(!isTgt) {
					MAind[i][iCntA++] = MQind[i][k];
					MAind[i][iCntA++] = sCntA;
					if(!MQTorV[i] && MQind[i][k] == i) {						
						MAstore[sCntA++] = 0;
						++k;
					}
					else {
						MAstore[sCntA++] = -MQdata[MQind[i][++k]];
					}
				}

				k++;
			}
			if(Math.abs(sum) > 0) {
				MBind[iCntB++] = i;
				MBind[iCntB++] = sCntB;
				MBstore[sCntB++] = sum;
			}
		}
		
		//Fill _matrixX and _matrixXsub1
		for(int i = 0; i < n; i++) {
			_matrixX[i] = new ComplexNumber();
			_matrixXsub1[i] = new ComplexNumber();
		}	
		//Now print them out to check:
		//printMatrix();
		long finishedMat = System.currentTimeMillis();
		LaplaceGenTime = (finishedMat - startMat)/1000.0;
		//System.out.println("Created Laplace Matrices in:  " + ((finishedMat - startMat)/1000.0));
	}
	
	/**
	 * Method used to identify whether a state in Matrix Q is a target state defined in
	 * the array targetStates
	 * @param j A state corresponding to a row in Matrix Q
	 * @return
	 */
	private boolean isTgtState(int j) {
		return targetStates.containsKey(j);
	}
	
	/**
	 * Debug method - prints a textual representation of Matrix Q
	 */
	protected static void printMatrix() {
		int kB = 1;
		for(int i = 0; i < n; i++) {
			int k = 1;
			for(int j = 0; j < n; j++) {
				//For each column in this row, cycle through sparse array row to see if 
				//it contains an entry for this column. If it does, print it; if not, print
				//0. A slow method but ok for just testing.
				boolean printed = false;
				int n = 1;
				while(n < MAind[i].length) {
					if(MAind[i][n++] == j) {
						DecimalFormat matrixF = new DecimalFormat("' '0.0;-0.0");
						String op = matrixF.format(MAstore[MAind[i][n]]);
						System.out.print(op + ", ");
						printed = true;
						break;
					}						
					n++;
				}
				if(!printed)
					System.out.print("   0, ");
			}
			System.out.print("                ");
			if(kB <= MBind[0]*2 && MBind[kB] == i) {
				System.out.print(MBstore[MBind[++kB]] + ",  ");
				kB++;
			}
			else
				System.out.print("0.0,");
			System.out.println();
		}
	}
	
	/**
	 * This method calculates the alpha values for each start state listed in startStates.
	 * @param pi steady state probability density vector for Markov chain
	 */
    void calculateAlphas(double[] pi) {
		if(pi == null) {
			//Case when there is only one start state
			alphas = new double[1];
			alphas[0] = 1;
			return;
		}
		double sum = 0;
		for(int st : startStates) {
			sum += pi[st];
		}
		alphas = new double[startStates.size()];
		int i = 0;
		for(int st : startStates) {
			alphas[i++] = pi[st]/sum;
		}
		/*System.out.println("Alpha array:");
		RTA.printArray(alphas);*/
		
	}
	
	public double getLaplaceGenTime() {
		return LaplaceGenTime;
	}
	
	
	/**
	 * Main entry point to the Laplace Transform inverter. Calls all relevant helper functions.
	 * @param pstartStates 
	 * @param ptargetStates
	 * @param startTime
	 * @param stopTime
	 * @param step
	 * @param MatrixQind
	 * @param MatrixQdata
	 * @param MatrixQTorV
	 * @param pi
	 * @param res
     * @param cdf
     * @return
	 * @throws NotConvergingException
	 * @throws IOException
	 */
	public ArrayList<Double> getResponseTime(ArrayList<Integer> pstartStates, HashMap<Integer, Integer> ptargetStates, double startTime,
			double stopTime, double step, int[][] MatrixQind, double[] MatrixQdata, boolean[] MatrixQTorV, 
			double[] pi, boolean res, boolean cdf) throws NotConvergingException
    {
		
		
		startStates = pstartStates;
		targetStates = ptargetStates;
		MQind = MatrixQind;
		MQdata = MatrixQdata;
		MQTorV = MatrixQTorV;
		calcResponse = res;
		calcCDF = cdf;
		evaluations = 0;
		
		calculateAlphas(pi);
		createMatrix();
		
		//System.out.println("calcResponse: " + calcResponse);
		//System.out.println("calcCDF: " + calcCDF);
		if(calcResponse) {
			for(double T = startTime; T < stopTime; T += step)
				results.add(runEuler(T, true));
		}
		
		if(calcCDF) {
			for(double T = startTime; T < stopTime; T += step)
				results.add(runEuler(T, false));
		}
		
		
		
		//System.out.println("# evaluations = " + evaluations + " " + "new"); 
		return results;
	}
	
}


