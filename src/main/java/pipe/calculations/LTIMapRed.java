package pipe.calculations;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;

import pipe.utilities.math.ComplexNumberWritable;
import pipe.exceptions.NotConvergingException;
import pipe.utilities.math.ComplexNumber;
import pipe.utilities.math.DoubleTupleWritable;

/**
 * Extends the functifonality of the LaplaceTransformInverter class to allow it to be run
 * as a MapRedufgce job on the Hadoop platform
 * 
 * @author Oliver Haggarty
 *;
 */
public class LTIMapRed extends LaplaceTransformInverter {
    private static JobConf jobConf;
	private static Path inDir;

    //File names used for transferring matrices among nodes
	private static String tempMatrixDir;
	private static String matrixFile;
	//private static String matrixFile = "/home/ollie/JobMatrix.dat";
	private static String reconMatrix;
	private static final String matrixStore = "rtamapred/matrix/matrixStore";
	
	//HashMaps containing lists of L(s) values to be calculated, and results once they are
	private static final Map<ComplexNumber, Double> calculatedRTA  = new HashMap<ComplexNumber, Double>();
	private static final Map<ComplexNumber, Double> calculatedCDF  = new HashMap<ComplexNumber, Double>();
	private static final Map<ComplexNumber, Double> toCalculate = new HashMap<ComplexNumber, Double>();

	//private static double step, startTime, stopTime;
	//private static int numMaps;
	
	/**
	 * Helper function that creates all the relevent path names for the recovering the 
	 * matrix file
	 */
	private static void createFileNames() {
		String fileSeparator = System.getProperty("file.separator");
		StringBuilder sb = new StringBuilder(System.getProperty("java.io.tmpdir"));
		sb.append(fileSeparator);
		sb.append("pipeTmpFiles");
		sb.append(fileSeparator);
		tempMatrixDir = sb.toString();
		//Create the directory if it doesn't exist
		matrixFile = tempMatrixDir + fileSeparator + "JobMatrix.dat";
		reconMatrix = tempMatrixDir + fileSeparator + "reconMatrix.dat";
	}
	
	/**
	 * Class required by Hadoop. Contains configuration details for the map tasks performed
	 * on nodes in the cluster. Main code is the map() function that performs the map task
	 * 
	 * @author Oliver Haggarty
	 *
	 */ 
	public static class RTAMapper extends MapReduceBase implements Mapper {
		private final String fnm;//name of matrix file copied to local filesystem
		
		/**
		 * Constructor, creates filename for matrix file copied to local fs
		 */
		public RTAMapper() {
			createFileNames();
			fnm = reconMatrix;
			System.out.println(fnm);			
		}
		
		/**
		 * Configures the local node - extracts the matrices and other data from the matrix
		 * file and stores in class variables.
		 * @param job The current MapReduce jobs JobConf (Job configuration details)
		 */
		public void configure(JobConf job) {
			//recreate matrices need for map task
			jobConf = job;
			try {
				reconstructMatrix();
				}
			catch (EOFException e) {
				waitForFile();
			}
			catch (NullPointerException e) {
				waitForFile();
			}
			catch (Exception e) {
				System.out.println("Error reconstructing matrix");
				e.printStackTrace();
			}
	    }	
		private void waitForFile() {
			try {
				Thread.sleep(5000);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			try {
				reconstructMatrix();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Calculates the value of L(s) for the s-value passed in as the key. Copies the
		 * key,result pai r to the output collector, ready for processing by the reduce task
		 * 
		 * @param key The s-value with which to calculate L(s)
		 * @param val A null valued place-holder for the result of L(s)
		 * @param out The output collector that collect the output of the map function
		 * @param reporter A reporter class that allows communication with the JobTracker
		 */
		public void map(WritableComparable key, Writable val,  OutputCollector out, Reporter reporter) throws IOException {
			double rtaRes, cdfRes;
			ComplexNumber res;
			
			//Put you LaPlace transform here!
			//Calculate the result:
			ComplexNumber s = ((ComplexNumberWritable) key).get();
			try {
				 res = fnRf(s);
				 rtaRes = res.getReal();
				 cdfRes = res.over(s).getReal();
			}
			catch (NotConvergingException e) {
				e.printStackTrace();
				reporter.setStatus("Converging error");
				throw new IOException();
			}
			
			//Simulate a long task here:
//			long counter = 64000000;//6 0's about 0.25 seconds on ray10; 7 0's = ~2.3 secs
//			while(counter > 0) counter--;
			
			//Pass the results to the output collector
			out.collect(key, new DoubleTupleWritable(rtaRes, cdfRes));
			
		}		
		
		/**
		 * Does nothing
		 */
		public void close() {
		}
		
		/**
		 * Reconstructs the matrices and other data need to calculate the L(s) values locally
		 * @throws IOException
		 */
		private void reconstructMatrix() throws IOException {
			if(!(new File(fnm).exists())) {
				FileSystem fileSys = FileSystem.get(jobConf);
				fileSys.copyToLocalFile(new Path(matrixStore), new Path(fnm));
			}
			/*try {
				System.out.println("Attempting to retrieve matrix from HDFS");
				new FsShell().doMain(new Configuration(), args);
			}
			catch (Exception e) {
				System.out.println("Error getting matrix out of HDFS");
			}*/
			DataInputStream in = new DataInputStream(
					new BufferedInputStream(new FileInputStream(fnm)));
			int size;
			n = in.readInt();
			//Construct MAind
			MAind = new int[n][];
			for(int i = 0; i < n; i++) {
				size = in.readInt();
				MAind[i] = new int[size*2+1];
				MAind[i][0] = size;
				for(int j = 1; j < size*2+1; j++) {
					MAind[i][j] = in.readInt();
				}
			}
			//Construct MAstore
			size = in.readInt();
			MAstore = new double[size];
			for(int i = 0; i < size; i++) {
				MAstore[i] = in.readDouble();
			}
			//Construct MBind
			size = in.readInt();
			MBind = new int[size*2+1];
			MBind[0] = size;
			for(int i = 1; i < size*2+1; i++) {
				MBind[i] = in.readInt();
			}
			//Construct MBstore
			size = in.readInt();
			MBstore = new double[size];
			for(int i = 0; i < size; i++) {
				MBstore[i] = in.readDouble();
			}
			//create _matrixX and _matrixXsub1

			_matrixX = new ComplexNumber[n];
			_matrixXsub1 = new ComplexNumber[n];
			for(int i = 0; i < n; i++) {
				_matrixX[i] = new ComplexNumber();
				_matrixXsub1[i] = new ComplexNumber();
			}
			//copy in startstates, target states, and alpha array
			startStates = new ArrayList<Integer>();
			int startStateslen = in.readInt();
			for(int i = 0; i < startStateslen; i++) {
				startStates.add(in.readInt());
			}
			targetStates = new HashMap<Integer, Integer>();
			int targetStateslen = in.readInt();
			for(int i = 0; i < targetStateslen; i++) {
				targetStates.put(in.readInt(), 0);
			}
			alphas = new double[in.readInt()];
			for(int i = 0; i < alphas.length; i++) {
				alphas[i] = in.readDouble();
			}
			calcResponse = in.readBoolean();
			in.close();
			/*printMatrix();
			for(int s : startStates) {
				System.out.println(s);
			}
			Iterator it = targetStates.keySet().iterator();
			while(it.hasNext())
				System.out.println(it.next());*/
		}
		
	}
	
	
		
	
	/**
	 * Reducer class required by Hadoop. Collects all the calculated L(s) values from 
	 * the HDFS and puts them in a local file on the machine running the JobTracker
	 * 
	 * @author Oliver Haggary August 2007
	 *
	 */
	public static class RTAReducer extends MapReduceBase implements Reducer {
		double rtaRes =0, cdfRes;
		ComplexNumber _s = null;
		
		JobConf conf;
	      
	      /** Reducer configuration.
	       *
	       */
	      public void configure(JobConf job) {
	          conf = job;
	      }
	      
	    /**
	     * Simply passes the results through to the output collector
	     * 
	     * @param key s-value for which L(s) has been calculated
	     * @param value result of L(s)
	     * @param output OutputCollector for copying results
	     * @param reporter Reporter class for communicating with JobTracker
	     */  
		public void reduce(WritableComparable key, Iterator values, OutputCollector output, Reporter reporter) throws IOException {
			_s = ((ComplexNumberWritable)key).get();
			DoubleTupleWritable temp;
			while (values.hasNext()) {
				temp = (DoubleTupleWritable)values.next();
		          rtaRes = temp.get1();
		          cdfRes = temp.get2();
			}
			output.collect(key, new DoubleTupleWritable(rtaRes, cdfRes));
		}				
	}
	
	/**
	 * Method that performs configuration of Hadoop for this MapReduce Job, and then runs
	 * job. When job is finished then runs Euler algorithm to calculate response time density
	 * @param step Step interval between T points to be caluclated
	 * @param startTime start T point
	 * @param stopTime final T point
	 * @param numMaps number of map tasks for Hadoop
	 * @throws IOException
	 * @throws pipe.exceptions.NotConvergingException
     * @throws InterruptedException
	 */
	private  void launch(final double step, final double startTime, final double stopTime,
			final int numMaps) throws IOException, NotConvergingException,
				InterruptedException {
		/*this.step = step;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.numMaps = numMaps;*/
		System.out.println("Starting Job");
        long timeBefore = System.currentTimeMillis();
      //Get a handle on the jar file containing all necessary classes to run the map
		//reduce job
		File jobJar = new File("job.jar");
		jobJar = jobJar.getAbsoluteFile();
		System.out.println(jobJar);
		//Create a new MapReduce configuration
        Configuration conf = new Configuration();
	    jobConf = new JobConf(conf, LTIMapRed.class);
	    
	    jobConf.setJobName("rta");//set name of job
	    
//	  turn off speculative execution, because DFS doesn't handle
	    // multiple writers to the same file.
	    jobConf.setSpeculativeExecution(false);
	    
	    //set type of file containing input data
	    jobConf.setInputFormat(SequenceFileInputFormat.class);
	    //set type of key and value objects 
	    jobConf.setOutputKeyClass(ComplexNumberWritable.class);
	    jobConf.setOutputValueClass(DoubleTupleWritable.class);
	    //set type of file containing results data
	    jobConf.setOutputFormat(SequenceFileOutputFormat.class);
	    //set Mapper and Reducer classes
	    jobConf.setMapperClass(RTAMapper.class);
	    jobConf.setReducerClass(RTAReducer.class);
	    
	    //set the Maximum number of attempts per map task to 2, so don't unnecessarily
	    //retry jobs that don't converge, but gives some leeway to io errors
	    jobConf.setMaxMapAttempts(2);
	    //set jar file that contains classes required for map and reduce tasks
	    jobConf.setJar(jobJar.toString());
	    
	  //Create necessary directories for input and output and set them in configuration
        Path tmpDir = new Path("rtamapred");
	    inDir = new Path(tmpDir, "in");
        Path outDir = new Path(tmpDir, "out");
	    FileSystem fileSys = FileSystem.get(jobConf);
	    fileSys.delete(tmpDir);
	    if (!fileSys.mkdirs(inDir)) {
	      throw new IOException("Mkdirs failed to create " + inDir.toString());
	    }
	    
	    jobConf.setInputPath(inDir);
	    jobConf.setOutputPath(outDir);
	    
	    //set number of reduce tasks to one, so a single results file is created
	    jobConf.setNumReduceTasks(1);
	    
	    //create an a file containing all s-values that need to have their L(s)
	    //calculated, record number of
	    int numRecords = createSValues(fileSys, jobConf, step, startTime, stopTime, numMaps);
    
	    //Create the Laplace Transform matrix and save as a data file
	    createMatrix();
		//createMatrix("/homes/ojh06/Project/matrixSIG.txt");
		saveMatrix();
	    //Run the mapreduce job
		try {
	        
    	    
	        //runs the job
	        JobClient.runJob(jobConf);
	        	        
	        System.out.println("Job Finished in "+
	                (float)(System.currentTimeMillis() - timeBefore)/1000.0 + " seconds");

	        //System.out.println("Results:");
	        
	        //Open results file
	        
	        Path inFile = new Path(outDir, "part-00000");
		    SequenceFile.Reader reader = new SequenceFile.Reader(fileSys, inFile,
		                                                             jobConf);
		    //Read results in to a Hashmap    
		    ComplexNumberWritable complexNumberWritable = new ComplexNumberWritable();
		    DoubleTupleWritable answer = new DoubleTupleWritable();
		    while(reader.next(complexNumberWritable, answer)) {
		    	//System.out.println("_s = " + complexNumberWritable.get() + " ans = " + answer.get());
		    	//System.out.println(complexNumberWritable.get() + ", " + answer.get());
		    	if(calcResponse) {
		    		calculatedRTA.put(complexNumberWritable.get(), answer.get1());
		    	}
		    	if(calcCDF) {
		    		calculatedCDF.put(complexNumberWritable.get(), answer.get2());
		    	}
		    }
		    
		    reader.close();
		    //System.out.println("Read results in");
		    
		    //Now perform the summation part of the process:
		    if(calcResponse) {
		    	for(double T = startTime; T < stopTime; T += step) {
			    	results.add(runEuler(T, true));
			    }
		    }
		    if(calcCDF) {
		    	for(double T = startTime; T < stopTime; T += step) {
			    	results.add(runEuler(T, false));
			    }
		    }
		    evaluations = toCalculate.size();
			//System.out.println("# evaluations = " + evaluations); 
			
	      } finally {
	    	  //delete mapreduce files in HDFS
	        fileSys.delete(tmpDir);
	        //delete local reconstructed matrix files on each slave
	        Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("./cleaner");
			InputStream stderr = proc.getErrorStream();
	        InputStreamReader isr = new InputStreamReader(stderr);
	        BufferedReader br = new BufferedReader(isr);
	        String line;
	        
	        while ( (line = br.readLine()) != null)
	            System.out.println(line);
	        
	        int exitVal = proc.waitFor();
	        //System.out.println("Successfully deleted temp files from slave nodes");
	      }
	    
	    
	}
	
	/**
	 * Overrides method in Laplace Transform Inverter. 0Method performs the Gauss-Seidel
	 *  iterative method to solve linear equations of the for Ax=b. Rather than calculating
	 *  each L(s) value when required, it looks them up in an array of results calculated
	 *  by the distributed MapReduce job. 
	 * @param T Time 
	 * @param calcRTA true if want to calculate response time, otherwise calculates cdf
	 * @return probability
	 * @throws pipe.exceptions.NotConvergingException
	 */
	protected double calculateSum(double T, boolean calcRTA) throws NotConvergingException
    {
		//System.out.println("LTIMapRed calcsum");
		//Check that MapReduce has produced correct number of results:
		//System.out.println("Checking size");
        retDivS = !calcRTA;
		//System.out.println("toCalculate: " + toCalculate.size());
		//System.out.println("calculatedRTA: " + calculatedRTA.size());
		if(toCalculate.size() != calculatedRTA.size())
			throw new NotConvergingException();
		//System.out.println("Correct number of results: " + calculated.size());
		//Calculate the Euler sum
		double sum = !retDivS ? calculatedRTA.get(new ComplexNumber(X,0))/2.0 : calculatedCDF.get(new ComplexNumber(X,0))/2.0;
		
		
		for(int N = 1; N <= Ntr; N++) {
			double Y = N*H;
			double term = !retDivS ? calculatedRTA.get(new ComplexNumber(X,Y)) : calculatedCDF.get(new ComplexNumber(X,Y));
			//System.out.println("Want: " + new ComplexNumber(X,Y));
			if((N%2) != 0)
				term = -term;
			sum += term;
		}
		
		SU[1] = sum;
		
		for(int K = 1; K <= 12; K++) {
			int N = Ntr + K;
			double Y = N*H;
			double term = !retDivS ? calculatedRTA.get(new ComplexNumber(X,Y)) : calculatedCDF.get(new ComplexNumber(X,Y));
			//System.out.println("Want: " + new ComplexNumber(X,Y));
			
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
        //System.out.println("Returning: " + fun1);
		return U*avgsu1/2048.0;
	}
	
	/**
	 * Creates a a Hashmap indexed by the s-values for which the MapReduce job needs to
	 * calculate L(s) values. Copies this array into a file which is then copied into
	 * the HDFS input directory.
	 * 
	 * @param fileSys Reference to HDFS
	 * @param jobConf Current job's configuration
	 * @param step Interval between T points
	 * @param startTime start T point
	 * @param stopTime Final T point
	 * @param numMaps The number of map tasks required for the MapReduce job
	 * @return
	 * @throws IOException
	 */
	private int createSValues(FileSystem fileSys, JobConf jobConf, double step, double startTime, double stopTime,
			int numMaps) throws IOException {
				
//		Create input file for MapReduce job
		String baseFilename = "part-00";
		DecimalFormat NameFormatter = new DecimalFormat("000");
		String filename;
		//Cycle through Euler summation algorithm for each T point and add required s-values
		//to Hashmap and input file
		for(double T = startTime; T <= stopTime; T += step) {
			double A = 19.1; //19.1; //e^{-A} is the discretization error
			int Ntr = 50;
			double X = A/(2.0*T);
			
			double H = Math.PI/T;
			//Always check that Hashmap doesn't already contain s-value - don't need
			//repetitions
			if(!toCalculate.containsKey(new ComplexNumber(X,0))) {
				//writer.append(new ComplexNumberWritable(X,0), new DoubleTupleWritable(0));
				toCalculate.put(new ComplexNumber(X, 0), null);
			}
			
			for(int N = 1; N <= Ntr; N++) {
				double Y = N*H;
				if(!toCalculate.containsKey(new ComplexNumber(X,Y))) {
					//writer.append(new ComplexNumberWritable(X,Y), new DoubleTupleWritable(0));
					toCalculate.put(new ComplexNumber(X, Y), null);
				}
			}
			
			for(int K = 1; K <= 12; K++) {
				int N = Ntr + K;
				double Y = N*H;
				if(!toCalculate.containsKey(new ComplexNumber(X,Y))) {
					//writer.append(new ComplexNumberWritable(X,Y), new DoubleTupleWritable(0));
					toCalculate.put(new ComplexNumber(X, Y), null);
				}
			}
		}
		
		//Code to split the s-values equally among a number of files specified by the user
		//Each file equates to a map task. Some files will have 1 more value than others
		//unless the number of s-values is a multiple of the number of map tasks
		int totalmaps = toCalculate.size();
		int mapsPerSplit = totalmaps/numMaps;//Identical number of s-values per file
		int leftover = totalmaps - (mapsPerSplit * numMaps);//There are this many extra s-values to fit in to the files
		if(leftover > 0)									//will always be < numMaps
			mapsPerSplit++;//So increase the number of maps per split by one, until all leftover values are used up
		//System.out.println("leftover = " + leftover);
		Iterator it = toCalculate.keySet().iterator();
		//copy hashmapp into arraylist for now - should prob change all to arraylist
		ArrayList<ComplexNumber> svals = new ArrayList<ComplexNumber>();
		while(it.hasNext()) {
			svals.add((ComplexNumber)it.next());
		}
		int record = 0;
		for(int i = 0; i < numMaps; i++) {

			filename = NameFormatter.format(i);
			Path file = new Path(inDir, (baseFilename + filename));//part-00001 etc
			SequenceFile.Writer writer = SequenceFile.createWriter(fileSys, jobConf, 
	            file, ComplexNumberWritable.class, DoubleTupleWritable.class, CompressionType.NONE);
			int numCopiedSoFar = record;
			if(i < numMaps-1) {
				if(leftover-- == 0)//Once the extra ones are used up, reduce the number of maps per split by one
					mapsPerSplit--;
				while((record - numCopiedSoFar) < mapsPerSplit) {
					writer.append(new ComplexNumberWritable(svals.get(record).getReal(), svals.get(record).getImag()), new DoubleTupleWritable(0, 0));
					record++;
				}
			}
			else {//last time there may be less than the correct number of maps per task
				while(record < totalmaps) {
					writer.append(new ComplexNumberWritable(svals.get(record).getReal(), svals.get(record).getImag()), new DoubleTupleWritable(0, 0));
					record++;
				}
			}
			writer.close();	
		}
		
		return toCalculate.size();
		
	}
	
	
	/**
	 * Saves the Laplace Transform matrix as a data file, and copies into HDFS so that
	 * it can be accessed by all nodes that compute L(s) values
	 * 
	 * @throws IOException
	 */
	 private void saveMatrix() throws IOException {
		File tempMatrixDirFile = new File(tempMatrixDir);
		if(!tempMatrixDirFile.exists()) {
			tempMatrixDirFile.mkdir();
		}
		
		DataOutputStream out = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(matrixFile)));
		out.writeInt(n);//first write size of nxn matrix
		//System.out.print(n);
		//write matrix MAind
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < MAind[i].length; j++) {
				out.writeInt(MAind[i][j]);			
				//System.out.print(MAind[i][j]);
			}
		}
		//write matrix MAstore
		int size = MAstore.length;
		out.writeInt(size);
		for(int i = 0; i < size; i++) {
			out.writeDouble(MAstore[i]);
			//System.out.print(MAstore[i]);
		}
         for(int aMBind : MBind)
         {
             out.writeInt(aMBind);
             //System.out.print(MBind[i]);
         }
		size = MBstore.length;
		out.writeInt(size);
		for(int i = 0; i < size; i++) {
			out.writeDouble(MBstore[i]);
			//System.out.print(MBstore[i]);
		}
		out.writeInt(startStates.size());
		for(int i : startStates) {
			out.writeInt(i);
		}
		out.writeInt(targetStates.size());
		Iterator tgtIt = targetStates.keySet().iterator();
		while(tgtIt.hasNext()) {
			out.writeInt((Integer)tgtIt.next());
		}
		out.writeInt(alphas.length);
		for(double d : alphas) {
			out.writeDouble(d);
		}
		out.writeBoolean(calcResponse);
		//System.out.println();
		out.close();
		//Copy into HDFS
		FileSystem fileSys = FileSystem.get(jobConf);
		fileSys.copyFromLocalFile(new Path(matrixFile), new Path(matrixStore));
	}
	
	 /**
	  * Main Entry point to distributed Laplace Transform Inverter. Overloads method in
	  * LaplaceTransformInverter class
	  * @param pstartStates
	  * @param ptargetStates
	  * @param startTime
	  * @param stopTime
	  * @param step
	  * @param MatrixQind
	  * @param MatrixQdata
	  * @param MatrixQTorV
	  * @param numMaps Requested number of map tasks
	  * @param pi The steady state vector of the Petri net
	  * @param res Boolean - want to calculate response time distribution
	  * @param cdf Boolean - want to calculate cumulative distribution function
	  * @return
	  * @throws IOException
	  * @throws NotConvergingException
      * @throws InterruptedException
	  */
	public  ArrayList<Double> getResponseTime(ArrayList<Integer> pstartStates, HashMap<Integer, Integer> ptargetStates, double startTime,
			double stopTime, double step, int[][] MatrixQind, double[] MatrixQdata, boolean[] MatrixQTorV, 
			int numMaps, double [] pi, boolean res, boolean cdf) throws IOException, NotConvergingException, InterruptedException {
		
		createFileNames();
		
		startStates = pstartStates;
		targetStates = ptargetStates;

		MQind = MatrixQind;
		MQdata = MatrixQdata;
		MQTorV = MatrixQTorV;
		calcResponse = res;
		calcCDF = cdf;

		calculateAlphas(pi);
		
		launch(step, startTime, stopTime, numMaps);
		return results;
	}
	
}
