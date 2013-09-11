package pipe.modules.rta;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import pipe.calculations.*;
import pipe.gui.ApplicationSettings;
import pipe.models.interfaces.IDynamicMarking;
import pipe.views.PetriNetView;
import pipe.exceptions.EnterOptionsException;
import pipe.exceptions.NotConvergingException;
import pipe.exceptions.NotValidExpressionException;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.EnterOptionsPane;
import pipe.gui.widgets.JFCGraphFrame;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.io.ImmediateAbortException;
import pipe.io.NewReachabilityGraphFileHeader;
import pipe.io.NewStateRecord;
import pipe.io.NewTransitionRecord;
import pipe.views.PlaceView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing main functionality and flow of control of Response Time Analysis pipeline
 * Implements Runnable so can be run as a separate thread. Point of entry is the run method.
 *
 * @author Oliver Haggarty - 08/2007
 */
public class AnalyseResponse implements Runnable
{
    private static final boolean DEBUG = true;
    //public static final int LENGTH = 0x1FFFFFFF;//Size of MemoryMapped files
    //public static final int LENGTH = 0x1000000;
    private static int length;
    private final EnterOptionsPane options;
    private final PetriNetView _petriNetView;
    private final ButtonBar btnBar;
    private final ResultsHTMLPane feedback;
    private final RTA rta;

    private int[][] MatrixQind;//Part of sparse matrix storing Q Matrix
    private double[] MatrixQdata;//Non-zero data entries of sparse Q Matrix
    private boolean[] MatrixQTorV;//Stores whether each non-zero data entry originates in
    //a Tangible or Vanishing state

    private int[][] MatrixQTind;//Part of sparse matrix storing transpose of Q matrix
    private double[] MatrixQTdata;//Non-zero data entries of sparse QT matrix

    private double[] piMatrix;//The steady-state probability vector, denoted by pi

    private int numRows;
    private int numtransitions;//Number of rows in Q matrix
    private int numStates;
    private int numArcs;
    private int numStartStates;
    private int numTargetStates;

    //int [] startStates;
    //int [] targetStates;
    private final ArrayList<Integer> startStates = new ArrayList<Integer>();
    private final HashMap<Integer, Integer> targetStates = new HashMap<Integer, Integer>();

    /**
     * Copies in essential data for running the analysis
     *
     * @param options  Record of user's option settings
     * @param pnmlData Copy of Petri nets datalayer representation
     * @param btnBar   Button bar on dialog box so can be updated
     * @param results  results display pane so can be updated
     * @param rta      reference to calling object for callbacks
     */
    public AnalyseResponse(EnterOptionsPane options, PetriNetView pnmlData, ButtonBar btnBar,
                           ResultsHTMLPane results, RTA rta)
    {
        this.options = options;
        this._petriNetView = pnmlData;
        this.btnBar = btnBar;
        this.feedback = results;
        this.rta = rta;
        ApplicationSettings applicationSettings = new ApplicationSettings();
    }

    /**
     * runs the analysis function and enables cancel button
     */
    public void run()
    {
        Component anBtn = btnBar.getComponent(0);
        anBtn.setEnabled(false);
        try
        {
            analyse();
        }
        catch(EnterOptionsException e)
        {
            rta.changeToEnterOptions();
        }
        anBtn.setEnabled(true);
        anBtn = btnBar.getComponent(1);
        anBtn.setEnabled(false);
    }

    /**
     * This method controls the main flow of control of the RTA analyser module. It
     * is called when the user presses the Calculate Response Time button.
     * @throws pipe.exceptions.EnterOptionsException
     */
    private void analyse() throws EnterOptionsException
    {
        //Timing variables
        long start = new Date().getTime();
        long finSS, finMatrix, finRTA, startRTA;
        double LapGenTime = 0;
        //Check for any problems in the numerical fields
        try
        {
            checkNumericalFields();
        }
        catch(Exception e)
        {
            options.setErrorMessage("Error in numerical fields.\nOne or more numbers invalid");
            throw new EnterOptionsException();
        }
        String resText2;
        String resText = "<h2>Response Time Analysis Results</h2>";
        feedback.setText(resText);
        options.setErrorMessage("");
        length = options.getBufferSize() * 1024 * 1024;

        String startExp = options.getStartStates();
        String targetExp = options.getTargetStates();

        System.out.print("startExp: " + startExp);
        System.out.print("targetExp: " + targetExp);
        //translate them from Label Names to PIPE2's internal respresentation
        try
        {
            startExp = translateExp(startExp);
        }
        catch(NotValidExpressionException e)
        {
            options.setErrorMessage("Error in start state expression\n" + e.getMessage());
            throw new EnterOptionsException();
        }
        try
        {
            targetExp = translateExp(targetExp);
        }
        catch(NotValidExpressionException e)
        {
            options.setErrorMessage("Error in target state expression\n" + e.getMessage());
            throw new EnterOptionsException();
        }
        //Debug stuff
        //System.out.println(startExp);
        //System.out.println(targetExp);

        // This will be used to store the reachability graph data
        // It may be massive so put in temp dir so don't go over any network file quotas
        StringBuilder sb = new StringBuilder(System.getProperty("java.io.tmpdir"));
        sb.append(System.getProperty("file.separator"));
        sb.append("pipeTmpFiles");
        sb.append(System.getProperty("file.separator"));
        //Create the directory if it doesn't exist
        File pipeTmpDir = new File(sb.toString());
        if(!pipeTmpDir.exists())
            pipeTmpDir.mkdir();
        sb.append("res.rg");
        File reachabilityGraph = new File(sb.toString());

        DynamicMarkingCompiler d = new DynamicMarkingCompiler();
        IDynamicMarking marking = null;
        //Rewrite the sourcecode for DynamicMarkingImpl with the user inputed expressions
        //for defining start and target expressions
        d.setLogicalExpression(startExp, targetExp);

        //This section will recompile the DynamicMarkingClass to get the changes made
        //to the isTargetState/isStartState methods.
        d.compileDynamicMarking();
        try
        {
            marking = d.getDynamicMarking();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //Generate a reachability graph file containing all possible states for the Petri-Net
        //and the transitions between them.
        resText2 = resText + "<br>Generating State Space - this could take some time...";
        feedback.setText(resText2);
        try
        {
            LargeStateSpaceGen.generate(_petriNetView, reachabilityGraph);
            finSS = new Date().getTime();
        }
        catch(BufferOverflowException e)
        {
            options.setErrorMessage("State space is too big for selected buffer size.\nPlease increase to a larger value.");
            throw new EnterOptionsException();
        }
        catch(IOException e)
        {
            options.setErrorMessage("Could not allocate buffer.\nPlease select smaller size.");
            throw new EnterOptionsException();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            options.setErrorMessage("Statespace generation failed");
            throw new EnterOptionsException();
        }
        resText2 += "<br>Finished generating StateSpace";
        resText2 += "<br>About to generate sparse Q matrix";
        feedback.setText(resText2);

        //Generate a sparse Q Matrix from the reachability graph
        try
        {
            //System.out.println("Creating sparse Q Matrix");
            createSparseMatrix(reachabilityGraph);
            //System.out.println("");
            finMatrix = new Date().getTime();
            //printMatrix(MatrixQind, MatrixQdata);
        }
        catch(Exception e)
        {
            options.setErrorMessage("Error creating sparse matrix");
            throw new EnterOptionsException();
        }

        //Report timings
        resText2 += "<br>Finished generating sparse Q matrix";
        feedback.setText(resText2);

        //Set the step and time range for the RTA
        double step = options.getStepSize();
        double TStart = options.getTStart();
        double TStop = options.getTStop();

        //Work out which states are start states and which are target states. Put in
        //startStates and targetStates arrays
        try
        {
            getStates(reachabilityGraph, marking);
        }
        catch(NotValidExpressionException e)
        {
            options.setErrorMessage(e.getMessage());
            throw new EnterOptionsException();
        }
        //Start and Target State arrays now initialised
        /*System.out.print("Start States: ");
          printArray(startStates);
          System.out.println("Target States: ");
          printArray(targetStates);*/

        //Check if multiple start states. If so, calculate transpose of Q matrix and
        //the steady state probability vector, pi
        if(startStates.size() > 1)
        {
            //Find out if there are any vanishing states
            boolean isVanishing = false;
            for(boolean isTan : MatrixQTorV)
            {
                if(!isTan)
                    isVanishing = true;
            }
            //Generate to correct matrix to calculate the steady state probability matrix
            try
            {
                if(isVanishing)
                    createSparsePTMatrix(reachabilityGraph);
                else
                    createSparseQTmatrix(reachabilityGraph);
            }
            catch(ImmediateAbortException e)
            {
                options.setErrorMessage("Error creating matrix for\n steady-state analysis");
                throw new EnterOptionsException();
            }
            //printMatrix(MatrixQTind, MatrixQTdata);
            if(DEBUG) piMatrix = NewSteadyStateSolver.solve(MatrixQTind, MatrixQTdata);
            else
                piMatrix = NewSteadyStateSolver.solve(MatrixQTind, MatrixQTdata);

            //printArray(piMatrix);
            //if(DEBUG) return;
        }

        //Do we want to calculate the RT or the CDF
        boolean isRTJob = options.isRT();
        //System.out.println("RTJob: " + isRTJob);
        boolean isCDFJob = options.isCDF();
        //System.out.println("CDFJOb: " + isCDFJob);
        //Run the Laplace Transform inverter
        ArrayList<Double> results = new ArrayList<Double>();//store results in this
        boolean linuxAndHadoop = options.isMapRedJob();

        resText2 += "<br>About to calculate response time - this could take some time...";
        feedback.setText(resText2);
        startRTA = new Date().getTime();
        try
        {
            if(linuxAndHadoop)
            {
                //Do it the mapreduce way:
                int numMaps = options.getNumMaps();//Get number of maps
                LTIMapRed rta = new LTIMapRed();
                results = rta.getResponseTime(startStates, targetStates, TStart, TStop, step, MatrixQind, MatrixQdata,
                                              MatrixQTorV, numMaps, piMatrix, isRTJob, isCDFJob);
                finRTA = new Date().getTime();
                LapGenTime = rta.getLaplaceGenTime();
            }
            else
            {
                //Do it the local way:
                LaplaceTransformInverter eul = new LaplaceTransformInverter();
                //Remember - systgem names states differently to how you might
                //System.out.println(targetStates);
                results = eul.getResponseTime(startStates, targetStates, TStart, TStop, step, MatrixQind, MatrixQdata,
                                              MatrixQTorV, piMatrix, isRTJob, isCDFJob);
                LapGenTime = eul.getLaplaceGenTime();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            options.setErrorMessage("Sorry, the response time couldn't be calculated due to an IO error");
            throw new EnterOptionsException();
        }
        catch(NotConvergingException e)
        {
            e.printStackTrace();
            options.setErrorMessage("Sorry, the response time couldn't be calculated.\n" + e.getMessage());
            throw new EnterOptionsException();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            options.setErrorMessage("There was a problem deleting files from the slave nodes.\nPlease do this manually");
        }

        finRTA = new Date().getTime();
        resText = resText + "<br>" + numStates + " states found, with " + numArcs + " arcs";
        resText = resText + "<br>" + numStartStates + " start states identified";
        resText = resText + "<br>" + numTargetStates + " target states identified";
        resText = resText + "<br><br>Generating StateSpace took: " + ((finSS - start) / 1000.0) + " secs";
        resText = resText + "<br>Generating Q Matrix took: " + (finMatrix - finSS) / 1000.0 + " secs";
        resText = resText + "<br>Generating Laplace Transform matrix took: " + LapGenTime + " secs";
        resText = resText + "<br>Calculating Response Time took: " + (((finRTA - startRTA) / 1000.0) - LapGenTime) + " secs";
        feedback.setText(resText);
        //Create ArrayList of T points that results were calculated for to pass to graphing package
        ArrayList<Double> Tpts = new ArrayList<Double>();
        for(double i = TStart; i < TStop; i += step)
        {
            Tpts.add(i);
        }
        //Draw a graph of the results
        drawGraph(Tpts, results);

    }

    /**
     * Creates the transpose of the Q matrix of the petri-net. Stores it in a sparse matrix format -
     * a 2D matrix of ints (MatrixQTind) lists for each row of QT matrix, which column has a non zero element and which
     * index its value is stored in an array of doubles (MatrixQTdata)
     *
     * @param rgGraph A file containing representation of reachability graph of GSPN
     * @throws ImmediateAbortException
     */
    private void createSparseQTmatrix(File rgGraph) throws ImmediateAbortException
    {
        NewReachabilityGraphFileHeader reachabilityGraphHeader;
        FileChannel ipfc = null;
        //Open reachability graph file for reading
        MappedByteBuffer inputBuf = null;
        try
        {
            ipfc = new FileInputStream(rgGraph).getChannel();
            inputBuf = ipfc.map(FileChannel.MapMode.READ_ONLY, 0, length);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        /*//Temporary ArrayLists for reading each row in as we don't know how much data there is.
          //This is later copied to an array for lower storage sizes
          ArrayList<Integer> tempRow = new ArrayList<Integer>();
          ArrayList<Double> tempData = new ArrayList<Double>();*/
        int row, column, dataIndex = 0;

        NewTransitionRecord currentTrans = new NewTransitionRecord();

        //Read in header of file to get data about contents
        reachabilityGraphHeader = new NewReachabilityGraphFileHeader();
        try
        {
            reachabilityGraphHeader.read(inputBuf);
        }
        catch(IOException e)
        {
            System.out.println("IO error!2");
            throw new ImmediateAbortException("IO error");
        }

        //NOTE: Array is constructed with column access ie MatrixQTind[ref to columns][contents of column]
        try
        {
            // Calculate how large each column of MatrixQTind needs to be
            int numColumns = reachabilityGraphHeader.getNumStates();//dimension of nxn array
            numtransitions = reachabilityGraphHeader.getNumTransitions();//number of non zero elements in array

            int[] colSize = new int[numColumns];//array to store size of columns in MatrixATind
            MatrixQTind = new int[numColumns][];
            MatrixQTdata = new double[numtransitions + numColumns];
            double[] rowSum = new double[numColumns];
            inputBuf.position((int) reachabilityGraphHeader.getOffsetToTransitions());
            for(int record = 0; record < numtransitions; record++)
            {
                currentTrans.read(inputBuf);
                //System.out.println("From: " + (current.getFromState()) + ":(" + ")" + " To: " + current.getToState() + " via " + current.getTransitionNo()+ " Rate: " + current.getRate());

                if(currentTrans.getFromState() != currentTrans.getToState())
                {
                    //row = currentTrans.getFromState();
                    column = currentTrans.getToState();
                    colSize[column]++;
                }
            }
            //Now create rows of sparse matrix:
            for(int i = 0; i < numColumns; i++)
            {
                MatrixQTind[i] = new int[(colSize[i] + 1) * 2 + 1];//increment as starts counting at 0, most efficient to do here
            }

            //Now go back though file and fill sparse matrix
            inputBuf.position((int) reachabilityGraphHeader.getOffsetToTransitions());
            for(int record = 0; record < numtransitions; record++)
            {
                currentTrans.read(inputBuf);
                if(currentTrans.getFromState() != currentTrans.getToState())
                {
                    row = currentTrans.getFromState();
                    column = currentTrans.getToState();
                    MatrixQTind[column][++MatrixQTind[column][0]] = row;
                    MatrixQTind[column][++MatrixQTind[column][0]] = dataIndex;
                    rowSum[row] += MatrixQTdata[dataIndex++] = currentTrans.getRate();

                }
            }
            //Now add the diagonals at the end of each row - these are negative sum of all other non-zero elements
            //of the column
            for(int i = 0; i < numColumns; i++)
            {
                MatrixQTind[i][++MatrixQTind[i][0]] = i;
                MatrixQTind[i][++MatrixQTind[i][0]] = dataIndex;
                //MatrixQTdata[dataIndex++] = sumCol(MatrixQTind, MatrixQTdata, i);
                MatrixQTdata[dataIndex++] = -rowSum[i];
            }
            //Now set first element of each MatrixQind row to = num of transitions
            for(int i = 0; i < numColumns; i++)
            {
                MatrixQTind[i][0] = colSize[i];
            }
            ipfc.close();
            //printMatrix(MatrixQTind, MatrixQTdata);
        }
        catch(Exception e)
        {
            System.out.println("Error filling matixes");
            e.printStackTrace();
        }
    }

    /**
     * Creates the Q Matrix of the GSPN from a file containing the GSPN's reachability graph. Stores it in a
     * sparse matrix format - a 2D matrix of ints (MatrixQind) lists for each row of Q matrix, which column
     * has a non zero element and which index its value is stored in an array of doubles (MatrixQdata)
     *
     * @param rGraph File containing reachability graph of GSPN
     * @throws ImmediateAbortException
     */
    private void createSparseMatrix(File rGraph) throws ImmediateAbortException
    {

        NewReachabilityGraphFileHeader rgheader;
        rgheader = new NewReachabilityGraphFileHeader();
        FileChannel ipfc = null;
        MappedByteBuffer inputBuf = null;
        //Open reachability graph file for reading
        try
        {
            ipfc = new FileInputStream(rGraph).getChannel();
            inputBuf = ipfc.map(FileChannel.MapMode.READ_ONLY, 0, length);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        int row, column, dataIndex = 0;

        NewTransitionRecord current = new NewTransitionRecord();
        NewStateRecord currentState = new NewStateRecord();
        //Read in header of file to get data about contents
        try
        {
            rgheader.read(inputBuf);
        }
        catch(IOException e)
        {
            System.out.println("IO error!2");
            throw new ImmediateAbortException("IO error");
        }

        // Populate the sparse matrix
        numRows = rgheader.getNumStates();
        numtransitions = rgheader.getNumTransitions();
        int[] rowsize = new int[numRows];
        MatrixQind = new int[numRows][];
        MatrixQdata = new double[numtransitions + numRows];//entry for each transition + calculated diagonals
        MatrixQTorV = new boolean[numRows];

        try
        {
            //First go through states and fill QMatrixTorV
            for(int i = 0; i < numRows; i++)
            {
                currentState.read(rgheader.getStateArraySize(), inputBuf);
                MatrixQTorV[i] = currentState.getTangible();
            }
            //Now go through and calculate size of each row in matrix
            inputBuf.position((int) rgheader.getOffsetToTransitions());
            for(int record = 0; record < numtransitions; record++)
            {
                current.read(inputBuf);
                //System.out.println("From: " + (current.getFromState()) + ":(" + ")" + " To: " + current.getToState() + " via " + current.getTransitionNo()+ " Rate: " + current.getRate());

                if(current.getFromState() != current.getToState())
                {
                    row = current.getFromState();
                    column = current.getToState();
                    rowsize[row]++;
                }
            }
            //Now create rows of sparse matrix:
            for(int i = 0; i < numRows; i++)
            {
                MatrixQind[i] = new int[(rowsize[i] + 1) * 2 + 1];//increment as starts counting at 0, most efficient to do here
            }

            //Now go back though file and fill sparse matrix
            inputBuf.position((int) rgheader.getOffsetToTransitions());
            for(int record = 0; record < numtransitions; record++)
            {
                current.read(inputBuf);
                if(current.getFromState() != current.getToState())
                {
                    row = current.getFromState();
                    column = current.getToState();
                    MatrixQind[row][++MatrixQind[row][0]] = column;
                    MatrixQind[row][++MatrixQind[row][0]] = dataIndex;
                    MatrixQdata[dataIndex++] = current.getRate();
                }
            }
            //Now add the diagonals at the end of each row
            double rowSumData;
            for(int i = 0; i < numRows; i++)
            {
                rowSumData = sumRow(MatrixQind, MatrixQdata, i);
                if(!MatrixQTorV[i])
                {//Its a vanishing state
                    //System.out.println("Vanishing state: " + i);
                    int k = 1;
                    int len = MatrixQind[i].length - 2;
                    while(k < (len))
                    {//subtract 2 as haven't added diagonal yet
                        MatrixQdata[MatrixQind[i][k + 1]] /= -rowSumData;
                        k += 2;
                    }
                    MatrixQind[i][++MatrixQind[i][0]] = i;
                    MatrixQind[i][++MatrixQind[i][0]] = dataIndex;
                    MatrixQdata[dataIndex++] = -1;//We've normalised row so will always be -1
                }
                else
                {
                    MatrixQind[i][++MatrixQind[i][0]] = i;
                    MatrixQind[i][++MatrixQind[i][0]] = dataIndex;
                    MatrixQdata[dataIndex++] = rowSumData;
                }

            }
            //Now set first element of each MatrixQind row to = num of transitions
            for(int i = 0; i < numRows; i++)
            {
                MatrixQind[i][0] = rowsize[i];
            }
            ipfc.close();
            //printMatrix(MatrixQind, MatrixQdata);
        }
        catch(Exception e)
        {
            System.out.println("Error filling matixes");
            e.printStackTrace();
        }
    }

    /**
     * Utility function that sums the elements in one row of a sparse matrix and returns the negative
     * of its sum.
     *
     * @param Mind  Index data part of sparse matrix
     * @param Mdata Value data part of sparse matrix
     * @param row
     * @return
     */
    private double sumRow(int[][] Mind, double[] Mdata, int row)
    {
        int i = 1;
        double sum = 0;
        while(i < Mind[row].length - 2)
        {
            sum += Mdata[Mind[row][++i]];
            i++;
        }
        return -sum;
    }

    /**
     * Utility function that sums the elements in one column of the sparse QT Matrix. It does this by summing
     * the rows of the Q Matrix.
     *
     * @param Mind  Not used
     * @param Mdata Not used
     * @param col   column to be summed
     * @return
     */
    private double sumCol(int[][] Mind, double[] Mdata, int col)
    {
        int i = 1;
        double sum = 0;
        while(i < MatrixQind[col].length - 2)
        {
            sum += MatrixQdata[MatrixQind[col][++i]];
            i++;
        }
        return -sum;
    }

    /**
     * Utility method used during debugging. Prints a textual representation of a sparse matrix
     *
     * @param Mind  Index part of sparse matrix
     * @param Mdata Data part of sparse matrix
     */
    private void printMatrix(int[][] Mind, double[] Mdata)
    {
        int kB = 1;
        for(int i = 0; i < numRows; i++)
        {
            int k = 1;
            for(int j = 0; j < numRows; j++)
            {
                //For each column in this row, cycle through sparse array row to see if
                //it contains an entry for this column. If it does, print it; if not, print
                //0. A slow method but ok for just testing.
                boolean printed = false;
                int n = 1;
                while(n < Mind[i].length)
                {
                    if(Mind[i][n++] == j)
                    {
                        DecimalFormat matrixF = new DecimalFormat("' '0.0;-0.0");
                        String op = matrixF.format(Mdata[Mind[i][n]]);
                        System.out.print(op + ", ");
                        printed = true;
                        break;
                    }
                    n++;
                }
                if(!printed)
                    System.out.print("   0, ");
            }
            System.out.println();
        }
    }

    /**
     * Takes a logical expression identifying a set of markings by the number of tokens on each place
     * in that state. This will be entered by the user, so will use place labels to identify places.
     * This function translates the expression into one using PIPE2's internal representation of the places.
     *
     * @param origExp Logical expression entered by user
     * @return The input logical expression translated into format PIPE2 can understand
     * @throws NotValidExpressionException
     */
    private String translateExp(String origExp) throws NotValidExpressionException
    {
        System.out.println(origExp);
        //TODO: Only need to map from place name to position in array, don't need 2 arraylists
        StringBuilder outputExp = new StringBuilder();
        String[] origExpAsArray;
        Map<String, String> nameToId = new HashMap<String, String>();
        Map<String, Integer> idToIndex = new HashMap<String, Integer>();
        PlaceView[] placeViews = _petriNetView.places();//Array that PIPE2 stores list of places of petri-net
        PlaceView p;
        //Go through all place in net - map label name to Id and Id to position in places array
        //This loop doesn't seem to be retreiving ids properly
        for(int i = 0; i < placeViews.length; i++)
        {
            p = placeViews[i];
            nameToId.put(p.getName(), p.getId());
            idToIndex.put(p.getId(), i);
        }
        System.out.println("nameToId: " + nameToId);
        System.out.println("idToIndex: " + idToIndex);
        //Go through origExp, whenever get to # get the string up to the next bracket and
        //use placeNameTOArrayIndex to convert it
        origExpAsArray = origExp.split("\\s");//Split origExp into each sub-part
        int numTerms = origExpAsArray.length;
        String name;
        if(numTerms < 3)//A valid expression must have at least three sub-parts
            throw new NotValidExpressionException("There are not enough arguments");
        for(int i = 0; i < numTerms; i++)
        {
            //Check for "(#APlaceName)"
            if(origExpAsArray[i].matches("#\\([\\w]*\\)"))
            {
                name = origExpAsArray[i].substring(2, origExpAsArray[i].lastIndexOf(")"));
                outputExp.append(" ");
                //Convert to PIPE2 representation
                outputExp.append(placeNameToArrayIndex(name, nameToId, idToIndex));
                i++;
            }
            else
                throw new NotValidExpressionException("Expression formatting not valid");
            //Check for "==|<|>|<=|>=|!="
            if(origExpAsArray[i].matches("==|<|>|<=|>=|!="))
            {
                outputExp.append(" ");
                outputExp.append(origExpAsArray[i]);
                i++;
            }
            else
                throw new NotValidExpressionException("Expression formatting not valid");
            //Check for a number or another place
            if(origExpAsArray[i].matches("\\d*"))
            {
                outputExp.append(" ");
                outputExp.append(origExpAsArray[i]);
                i++;
            }
            else if(origExpAsArray[i].matches("#\\([\\w]*\\)"))
            {
                name = origExpAsArray[i].substring(2, origExpAsArray[i].lastIndexOf(")"));
                outputExp.append(" ");
                outputExp.append(placeNameToArrayIndex(name, nameToId, idToIndex));
                i++;
            }
            else
            {
                throw new NotValidExpressionException("Expression formatting not valid");
            }
            //Check for "&&" or "||" - this is optional, only check if there are more terms in list
            if(i < numTerms)
            {
                if(origExpAsArray[i].matches("&&|\\|\\|"))
                {
                    outputExp.append(" ");
                    outputExp.append(origExpAsArray[i]);
                }
                else
                    throw new NotValidExpressionException("Expression formatting not valid");
            }
        }

        return outputExp.toString();
    }

    /**
     * Converts the label name of a place object to its position in the places array (called state array in
     * (New)MarkingRecord
     *
     * @param name      Label name of place object
     * @param nameToId  Mapping of names to ID of place object
     * @param idToIndex Mapping of ID to Index in places array
     * @return String of format p[i], representing index in places array
     * @throws NotValidExpressionException
     */
    private String placeNameToArrayIndex(String name, Map<String, String> nameToId,
                                         Map<String, Integer> idToIndex) throws NotValidExpressionException
    {
        //DynamicMarkingImp stores list of places as array p
        StringBuilder result = new StringBuilder("p[");
        String id = nameToId.get(name);
        if(id == null)
            throw new NotValidExpressionException("Place id does not exist");
        Integer index = idToIndex.get(id);
        if(index == null)
            throw new NotValidExpressionException("Place index does not exist");
        result.append(index);
        result.append("]");
        return result.toString();
    }

    /**
     * Uses a dynamically compiled class to identify which states are start states, and which are target
     * states from the two logical expressions entered by the user.
     *
     * @param rgFile  reachability graph file containing list of all states of petri net
     * @param marking dynamically compiled class
     * @throws NotValidExpressionException
     */
    private void getStates(File rgFile, IDynamicMarking marking) throws NotValidExpressionException
    {
        //List<Integer> startStList = new ArrayList<Integer>();
        //List<Integer> targetStList= new ArrayList<Integer>();
        startStates.clear();
        targetStates.clear();
        FileChannel rgfc;
        MappedByteBuffer rgBuf = null;
        //DEBUG breakpoint here - force to use old file by manual deletion.

        try
        {
            rgfc = new FileInputStream(rgFile).getChannel();
            rgBuf = rgfc.map(FileChannel.MapMode.READ_ONLY, 0, length);


            NewReachabilityGraphFileHeader reachabilityGraphHeader = new NewReachabilityGraphFileHeader();

            reachabilityGraphHeader.read(rgBuf);
            numStates = reachabilityGraphHeader.getNumStates();
            System.out.println("States: " + reachabilityGraphHeader.getNumStates());
            numArcs = reachabilityGraphHeader.getNumTransitions();
            System.out.println("Transitions: " + reachabilityGraphHeader.getNumTransitions());
            int stateSize = reachabilityGraphHeader.getStateArraySize();
            System.out.println("StateArraySize: " + reachabilityGraphHeader.getStateArraySize());
            NewStateRecord mk = new NewStateRecord();

            //Read each state in rgFile and test if it is a targetState or Start State
            for(int i = 0; i < numStates; i++)
            {

                    mk.read(stateSize, rgBuf);
                /*System.out.println("state array " + i);
                       printArray(mk.getState());*/
                if(marking.isStartMarking(mk))
                {
                    startStates.add(mk.getID());
                }
                if(marking.isTargetMarking(mk))
                {
                    targetStates.put(mk.getID(), 0);
                }
            }

            //Error if there are no states identified by start or target expressions
            if(startStates.size() == 0)
            {
                throw new NotValidExpressionException("There are no states that match the start expression");
            }
            // This always gets thrown
            //System.out.println(targetStates.size());
            if(targetStates.size() == 0)
            {
                throw new NotValidExpressionException("There are no states that match the target expression");
            }

            //System.out.println("Number of start states: " + startStates.size());
            //System.out.println("Number of target states: " + targetStates.size());
            numStartStates = startStates.size();
            numTargetStates = targetStates.size();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Creates a new JFCGraphFrame to display the results of the response time analysis
     *
     * @param Tpoints
     * @param results
     */
    private void drawGraph(ArrayList<Double> Tpoints, ArrayList<Double> results)
    {
        if(options.isRT())
        {
            XYSeries resSeries = new XYSeries("Response Time");
            for(int i = 0; i < Tpoints.size(); i++)
            {
                resSeries.add(Tpoints.get(i), results.get(i));
            }
            XYDataset resDataset = new XYSeriesCollection(resSeries);
            JFreeChart resChart = ChartFactory.createXYLineChart(
                    "Response Time Distribution",
                    "Time (secs)",
                    "Probability Density",
                    resDataset,
                    PlotOrientation.VERTICAL,
                    false, true, false);

            JFCGraphFrame frame = new JFCGraphFrame(_petriNetView.getPNMLName(), resChart);
            frame.setIconImage(new ImageIcon(ApplicationSettings.getImagePath() + "icon.png").getImage());
            frame.pack();
            frame.setLocation(0, 0);
            frame.setVisible(true);
        }

        if(options.isCDF())
        {
            int offset = 0;
            if(options.isRT())
                offset += Tpoints.size();
            XYSeries resSeries = new XYSeries("CDF of Response Time");
            for(int i = 0; i < Tpoints.size(); i++)
            {
                resSeries.add(Tpoints.get(i), results.get(i + offset));
            }
            XYDataset resDataset = new XYSeriesCollection(resSeries);
            JFreeChart resChart = ChartFactory.createXYLineChart(
                    "Cumulative Distribution",
                    "Time (secs)",
                    "Cumulative Density",
                    resDataset,
                    PlotOrientation.VERTICAL,
                    false, true, false);

            JFCGraphFrame frame = new JFCGraphFrame(_petriNetView.getPNMLName(), resChart);
            frame.setIconImage(new ImageIcon(ApplicationSettings.getImagePath() + "icon.png").getImage());
            frame.pack();
            frame.setLocation(100, 100);
            frame.setVisible(true);
        }

    }


    /**
     * Creates the transpose of the P matrix of the EMC of a GSPN. The matrix is generates
     * is later passed into the steady state solver to obtain the steady state distribution
     * of the petri net. Takes the reachability graph file created by the LargeStateSpaceGenerator
     * and constructs PT matrix from data in that
     *
     * @param rgGraph
     * @throws ImmediateAbortException
     */
    private void createSparsePTMatrix(File rgGraph) throws ImmediateAbortException
    {
        NewReachabilityGraphFileHeader reachabilityGraphHeader;
        FileChannel ipfc = null;
        //Open reachability graph file for reading
        MappedByteBuffer inputBuf = null;
        try
        {
            ipfc = new FileInputStream(rgGraph).getChannel();
            inputBuf = ipfc.map(FileChannel.MapMode.READ_ONLY, 0, length);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        int row, column, dataIndex = 0;

        NewTransitionRecord currentTrans = new NewTransitionRecord();
        NewStateRecord currentState = new NewStateRecord();

        //Read in header of file to get data about contents
        reachabilityGraphHeader = new NewReachabilityGraphFileHeader();
        try
        {
            reachabilityGraphHeader.read(inputBuf);
        }
        catch(IOException e)
        {
            System.out.println("IO error!2");
            throw new ImmediateAbortException("IO error");
        }

        //NOTE: Array is constructed with column access ie MatrixQTind[ref to columns][contents of column]
        try
        {
            // Calculate how large each column of MatrixQTind needs to be
            int numColumns = reachabilityGraphHeader.getNumStates();//dimension of nxn array
            numtransitions = reachabilityGraphHeader.getNumTransitions();//number of non zero elements in array

            int[] colSize = new int[numColumns];//array to store size of columns in MatrixATind
            MatrixQTind = new int[numColumns][];
            MatrixQTdata = new double[numtransitions + numColumns];
            double[] rowSum = new double[numColumns];
            inputBuf.position((int) reachabilityGraphHeader.getOffsetToTransitions());
            for(int record = 0; record < numtransitions; record++)
            {
                currentTrans.read(inputBuf);
                //System.out.println("From: " + (current.getFromState()) + ":(" + ")" + " To: " + current.getToState() + " via " + current.getTransitionNo()+ " Rate: " + current.getRate());

                if(currentTrans.getFromState() != currentTrans.getToState())
                {
                    //row = currentTrans.getFromState();
                    column = currentTrans.getToState();
                    colSize[column]++;
                }
            }
            //Now create rows of sparse matrix:
            for(int i = 0; i < numColumns; i++)
            {
                MatrixQTind[i] = new int[(colSize[i] + 1) * 2 + 1];//increment as need to add diagonal
            }

            //Now go back though file and fill sparse matrix
            inputBuf.position((int) reachabilityGraphHeader.getOffsetToTransitions());
            for(int record = 0; record < numtransitions; record++)
            {
                currentTrans.read(inputBuf);
                if(currentTrans.getFromState() != currentTrans.getToState())
                {
                    row = currentTrans.getFromState();
                    column = currentTrans.getToState();
                    MatrixQTind[column][++MatrixQTind[column][0]] = row;
                    MatrixQTind[column][++MatrixQTind[column][0]] = dataIndex;
                    rowSum[row] += MatrixQTdata[dataIndex++] = currentTrans.getRate();
                    /*if(currentTrans.getIsFromTan()) {
                         rowSum[row] += MatrixQTdata[dataIndex++] = currentTrans.getRate();
                     }
                     else {
                         rowSum[row] += MatrixQTdata[dataIndex++] = 20000;
                     }*/
                }
            }
            //Now divide all the data elements by the row sum
            for(int i = 0; i < numColumns; i++)
            {
                int k = 1;
                while(k < (colSize[i] + 1) * 2 - 2)
                {//subtract 2 as haven't added diagonal yet
                    MatrixQTdata[MatrixQTind[i][k + 1]] /= rowSum[MatrixQTind[i][k]];
                    k += 2;
                }
            }
            //Now add the diagonals at the end of each row - these are negative sum of all other non-zero elements
            //of the column
            for(int i = 0; i < numColumns; i++)
            {
                MatrixQTind[i][++MatrixQTind[i][0]] = i;
                MatrixQTind[i][++MatrixQTind[i][0]] = dataIndex;
                //MatrixQTdata[dataIndex++] = sumCol(MatrixQTind, MatrixQTdata, i);
                MatrixQTdata[dataIndex++] = -1;// 0 diagonal minus identity matrix
            }
            //Now set first element of each MatrixQind row to = num of transitions
            for(int i = 0; i < numColumns; i++)
            {
                MatrixQTind[i][0] = colSize[i];
            }
            ipfc.close();
            //printMatrix(MatrixQTind, MatrixQTdata);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Helper function called to ensure the user has entered viable values into the
     * text boxes. If not, throws exception to be handled elsewhere
     *
     * @throws EnterOptionsException
     */
    private void checkNumericalFields() throws EnterOptionsException
    {
        double n1, n2, n3;
        int n4, n5;
        n1 = options.getTStart();
        n2 = options.getTStop();
        n3 = options.getStepSize();
        n4 = options.getNumMaps();
        n5 = options.getBufferSize();
        if(n1 <= 0 || n2 <= n1 || n3 < 0 || n3 > (n2 - n1) || n4 < 1 || n5 < 1)
            throw new EnterOptionsException();
    }

    /**
     * Returns the length of the buffer the user selected
     *
     * @return
     */
    public static int getBufferLength()
    {
        return length;
    }

    //Helper functions for debugging purposes
    public static void printArray(int[] array)
    {
        for(int anArray : array)
        {
            System.out.println(anArray + ", ");
        }
    }

    public static void printArray(double[] array)
    {
        for(double anArray : array)
        {
            System.out.print(anArray + ", ");
        }
    }

}
