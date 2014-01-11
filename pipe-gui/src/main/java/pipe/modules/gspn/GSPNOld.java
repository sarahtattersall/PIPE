/*
 * This was the original GSPN analysis module which has now
 * been replaced by the work carried out by Nadeem Akharware.
 * The code has been left here just in case it contains
 * something useful for later project developments.
 */


/*
 * Created on Feb 6, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pipe.modules.gspn;


/**
 * @author Matthew Cook
 *
 *
 */
class GSPNOld{}/* extends GSPN implements IModule{
   
   private static final String MODULE_NAME = "GSPN Old IModule";
   
   
//######################################################################################################################
   public void run(PetriNet _pnmlData) {
      // Build interface
      JDialog guiDialog = new JDialog(Pipe.getApplicationView(),MODULE_NAME,true);
      
      // 1 Set layout
      Container contentPane=guiDialog.getContentPane();
      contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
      
      // 2 Add file browser
      contentPane.add(sourceFilePanel=new PetriNetChooserPanel("Source net",_pnmlData));
      
      // 3 Add results pane
      contentPane.add(results=new ResultsHTMLPane(_pnmlData.getPNMLName()));
      
      // 4 Add button
      contentPane.add(new ButtonBar("Analyse GSPN",runAnalysis));
      //contentPane.add(new ButtonBar("Play a tune!",playMidi));
      // 5 Make window fit contents' preferred size
      guiDialog.pack();
      
      // 6 Move window to the middle of the screen
      guiDialog.setLocationRelativeTo(null);
      
      guiDialog.setVisible(true);
   }
   
   
//######################################################################################################################
   /**
    * Analyse button click handler
    * /
   ActionListener runAnalysis=new ActionListener() {
      
      public void actionPerformed(ActionEvent arg0) {
         Date start = new Date();
         Date efinished = null;
         Date ssdfinished = null;
         Date allfinished = null;
         
         PetriNet sourceDataLayer=sourceFilePanel.getDataLayer();
         String s="<h2>GSPN Passage Time Analysis Results</h2>";
         if(sourceDataLayer==null){
            return;
         }
         if (!hasTimedTransitions(sourceDataLayer)){
            s+= "This Petri net has no timed transitions, so GSPN analysis cannot be performed.";
         }
                        /*else if (!hasImmediateTransitions(sourceDataLayer)){
                                s+= "This Petri net has no immediate transitions, so GSPN analysis cannot be performed.";
                        }
                        else if (!testEqualConflict(sourceDataLayer)) {
                                s+= "Condition Equal Conflict is not satisfied.  GSPN analysis cannot continue.";
                        }* /
         else {
            //set up data for display
            try {
               StateList reachSet = getReachabilitySet(sourceDataLayer);
               efinished = new Date();
               System.out.println("State Space Exploration complete.");
               int reachSize = reachSet.size();
               //getTransitionProbabilityMatrix(sourceDataLayer, reachSet);
               //int placeCount = sourceDataLayer.places().length;
               
               StateList vanishing = new StateList();
               StateList tangible = new StateList();
               
               getVanishingAndTangible(sourceDataLayer, reachSet, vanishing, tangible);
               if (tangible.size() == 0) {
                  s+= "This petri net has no tangible states. " + 
                           " GSPN analysis cannot continue";
               } else if (vanishing.size() == 0) {
                  s+= "This petri net has no vanishing states. " + "" +
                           " GSPN analysis cannot continue";
               } else {
                  double[][] c = new double [vanishing.size()][vanishing.size()];
                  double[][] d = new double [vanishing.size()][tangible.size()];
                  double[][] e = new double [tangible.size()][vanishing.size()];
                  double[][] f = new double [tangible.size()][tangible.size()];
                  c = probabilityMatrix(sourceDataLayer, vanishing, vanishing);
                  d = probabilityMatrix(sourceDataLayer, vanishing, tangible);
                  e = probabilityMatrix(sourceDataLayer, tangible, vanishing);
                  f = probabilityMatrix(sourceDataLayer, tangible, tangible);
                  Matrix cM = new Matrix(c);
                  Matrix dM = new Matrix(d);
                  Matrix eM = new Matrix(e);
                  Matrix fM = new Matrix(f);
                  int cSize = cM.getColumnDimension();
                  Matrix iD = new Matrix(cSize, cSize);
                  for (int i = 0; i<cSize; i++){
                     for (int j = 0; j < cSize; j++){
                        if (i==j) {
                           iD.set(i,j,1.0);
                        } else {
                           iD.set(i,j,0.0);
                        }
                     }
                  }
                  
                  Matrix iMinusCInverse = new Matrix(cSize, cSize);
                  iMinusCInverse = iD.minus(cM);
                  iMinusCInverse = iMinusCInverse.inverse();
                  double[] sojournTimes = calcSojournTime(sourceDataLayer, tangible);
                  double[] embeddedMarkovChainDist = getEmbeddedMarkovChainSteadyStateDistribution(cM,dM,eM,fM);
                  Matrix meanVisits = calcMeanNumVisits(embeddedMarkovChainDist);
                  double xHat = xHat(sojournTimes, embeddedMarkovChainDist);
                  double[] meanCycleTimes = calcMeanCycleTimes(embeddedMarkovChainDist, xHat);
                  int meanCycleLength = meanCycleTimes.length;
                  double[] steadyStateDistribution = getSteadyStateDistribution(meanCycleTimes, sojournTimes);
                  ssdfinished = new Date();
                  double[][] rates = rateMatrix(sourceDataLayer, tangible, vanishing);
                  Matrix eTwiddle = new Matrix(rates);
                  Matrix eTwiddleIMinusCInverse = eTwiddle.times(iMinusCInverse);
                  Matrix steadyStateDistrib = new Matrix(1,meanCycleLength);
                  for (int i=0; i< meanCycleLength; i++) {
                     steadyStateDistrib.set(0,i,steadyStateDistribution[i]);
                  }
                  Matrix vanishingSteadyState = steadyStateDistrib.times(eTwiddleIMinusCInverse);
                  double[] throughput = getTransitionThroughput(sourceDataLayer ,vanishing, tangible, vanishingSteadyState, steadyStateDistribution);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Entire reachability set",
                     renderStateSpaceLinked(sourceDataLayer, reachSet)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Set of vanishing markings",
                     renderStateSpace(sourceDataLayer, vanishing)
                  },1, false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Set of tangible markings",
                     renderStateSpace(sourceDataLayer, tangible)
                  },1, false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Probability of transition between two vanishing states",
                     renderProbabilities(c, vanishing, vanishing)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Probability of transition from a vanishing state to a tangible state",
                     renderProbabilities(d, vanishing, tangible)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Probability of transition from a tangible state to a vanishing state",
                     renderProbabilities(e, tangible, vanishing)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Probability of transition from a tangible state to a tangible state",
                     renderProbabilities(f, tangible, tangible)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Steady state distribution  of the embedded Markov chain",
                     renderLists(embeddedMarkovChainDist, tangible)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Mean number of visits between tangible states",
                     renderProbabilities(meanVisits.getArrayCopy(), tangible, tangible)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Sojourn times for tangible states",
                     renderLists(sojournTimes, tangible)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Mean cycle times for tangible states",
                     renderLists(meanCycleTimes, tangible)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Steady state distribution for tangible states",
                     renderLists(steadyStateDistribution, tangible)
                  },1,false,false,true,false);
                  
                  s+=ResultsHTMLPane.makeTable(new String[]{
                     "Throughput",
                     renderThroughput(sourceDataLayer, throughput)
                  },1,false,false,true,false);
               }
            } catch (TreeTooBigException e) {
               s+= e.getMessage();
            }
         }
         allfinished = new Date();
         DecimalFormat f=new DecimalFormat();
         f.setMaximumFractionDigits(5);
         double totaltime = (allfinished.getTime() - start.getTime())/1000.0;
         if(efinished != null){
            double explorationtime = (efinished.getTime() - start.getTime())/1000.0;
            double steadystatetime = (ssdfinished.getTime() - efinished.getTime())/1000.0;
            
            s+= "<br>State space exploration took " + f.format(explorationtime) + "s";
            s+= "<br>Steady state equations took " + f.format(steadystatetime) + "s";
         }
         s+= "<br>Total time was " + f.format(totaltime) + "s";
         results.setText(s);
      }
   };

   
//######################################################################################################################
   /**This function takes a reachability set
    * and splits it into subsets of tangible and vanishing states
    * @param PetriNet - the net to be processed
    * @param StateList - the entire reachability set
    * @param StateList - the list to be populated with vanishing states
    * @param StateList - the list to be populated with tangible states
    *
    * /
   private void getVanishingAndTangible(PetriNet _pnmlData, StateList reachabilitySet, StateList vanishing, StateList tangible) {
      int size = reachabilitySet.size();
      for (int i = 0; i < size; i++) {
         int id = reachabilitySet.getIDNum(i);
         if (isTangibleState(_pnmlData, reachabilitySet.get(i)))
            tangible.add(reachabilitySet.get(i),id);
         else
            vanishing.add(reachabilitySet.get(i),id);
      }
   }
   
   
//######################################################################################################################
   /**This function calculates the mean number of visits at a particular transition, given a particular embedded Markov Process
    * @param double[] - the embedded Markov Process
    * @return Matrix
    *
    * /
   private Matrix calcMeanNumVisits(double[] embeddedMarkovProcess) {
      int size = embeddedMarkovProcess.length;
      Matrix result = new Matrix(size,size);
      for (int i = 0; i<size; i++) {
         for (int j = 0; j <size; j++){
            result.set(i,j,embeddedMarkovProcess[i]/embeddedMarkovProcess[j]);
         }
      }
      return result;
   }
   
   
//######################################################################################################################
   /**This function determines the sojourn time for each state in a specified set of states.
    * @param DataLater - the net to be analysed
    * @param StateList - the list of tangible markings
    * @return double[] - the array of sojourn times for each specific state
    *
    * /
   private double[] calcSojournTime(PetriNet pnmldata, StateList tangibleStates) {
      int numStates = tangibleStates.size();
      int numTrans = pnmldata.getTransitions().length;
      Transition[] trans = pnmldata.getTransitions();
      double[] sojournTime = new double[numStates];
      
      for (int i = 0; i < numStates; i++) {
         boolean[] transStatus = areTransitionsEnabled(pnmldata, tangibleStates.get(i));
         double weights = 0;
         for (int j = 0; j <numTrans; j++) {
            if (transStatus[j] == true){
               weights += trans[j].getRate();
            }
         }
         sojournTime[i] = 1/weights;
      }
      return sojournTime;
   }


//######################################################################################################################
   //This is an intemediate calculation used to determine steady state distributions for tangible states.
   private double xHat(double[] piBar, double[] sojournTimes) {
      int size = piBar.length;
      double xHat = 0;
      
      for (int i = 0; i< size; i++) {
         xHat += (piBar[i] *sojournTimes[i]);
      }
      return xHat;
   }
   
   
//######################################################################################################################
   //This calculates the mean cycle times Tc(Mi) for tangible markings Mi
   private double[] calcMeanCycleTimes(double[] embeddedMarkovChain, double xHat) {
      int size = embeddedMarkovChain.length;
      double[] meanCycleTimes = new double[size];
      
      for (int i = 0; i < size; i++) {
         meanCycleTimes[i] = xHat/embeddedMarkovChain[i];
      }
      return meanCycleTimes;
      
   }
   
   
//######################################################################################################################
   private double[] getSteadyStateDistribution(double[] meanCycleTimes, double[] sojournTimes) {
      int size = meanCycleTimes.length;
      double[] steadyStateDistribution = new double[size];
      
      for (int i = 0; i<size; i++){
         steadyStateDistribution[i] = (sojournTimes[i]/meanCycleTimes[i]);
      }
      return steadyStateDistribution;
   }
   
   
//######################################################################################################################
   /**Calculate the probability of changing from one marking to another
    * Works out the intersection of transitions enabled to fire at a particular
    * marking, transitions that can be reached from a particular marking and the
    * intersection of the two.  Then sums the firing rates of the intersection
    * and divides it by the sum of the firing rates of the enabled transitions.
    * @param PetriNet
    * @param int[] - marking1
    * @param int[] - marking2
    * @return double - the probability
    * /
   private double probMarkingAToMarkingB(PetriNet _pnmlData, int[] marking1, int[] marking2){
      int markSize = marking1.length;
      int[][] incidenceMatrix = _pnmlData.getIncidenceMatrix();
      int transCount = _pnmlData.getTransitions().length;
      boolean[] marking1EnabledTransitions = new boolean[transCount];// = isTransitionEnabled(_pnmlData, marking1); //get list of transitions enabled at marking1
      boolean[] matchingTransition = new boolean[transCount];
      
      for (int i = 0; i <transCount; i++) {
         marking1EnabledTransitions[i] = isTransitionEnabled(_pnmlData, marking1, i);
      }
      
      //**************************************************** *************************************************
      for (int j = 0; j <transCount; j ++) {
         matchingTransition[j] = true;  //initialise matrix of potential transition values to true
      }
      //*****************************************************************************************************
      //get transition needed to fire to get from marking1 to marking2
      for (int i = 0; i < transCount; i++) {
         for (int k = 0; k <markSize; k++) {
            //if the sum of the incidence matrix and marking 1 doesn't equal marking 2,
            //set that candidate transition possibility to be false
            if (((int)marking1[k] + (int)incidenceMatrix[k][i])!= (int)marking2[k]){
               matchingTransition[i] = false;
            }
         }
      }
      //if the state is tangible, all transitions will be timed,
      //so all can be considered in the probability calculation.
      //Otherwise, reset the enabled status of timed transitions to false, as immediate transitions
      //will always fire first.
      
      if (isTangibleState(_pnmlData, marking1)== false) {
         for (int i = 0; i <transCount; i++) {
            if (_pnmlData.getTransitions()[i].getTimed() == true) {
               marking1EnabledTransitions[i] = false;
            }
         }
      }
      //*****************************************************************************************************
      //check if there are any potential transitions from marking 1 to marking 2 - if not return probability 0
      boolean hasAChance = false;
      for (int i = 0; i < transCount; i++) {
         if (matchingTransition[i] == true){
            hasAChance = true;
         }
      }
      if (hasAChance == false) {
         return 0.0;
      }
      //*****************************************************************************************************
      //if a transition is needed to get to marking 2 but isn't enabled at marking 1, return probability 0
      boolean enabledAndMatching = false;
      for (int i = 0; i <transCount; i++) {
         if ((matchingTransition[i] == true) && (marking1EnabledTransitions[i] == true)) {
            enabledAndMatching  = true;
         }
      }
      if (enabledAndMatching = false) {
         return 0.0;
      }
      //******************************************************************************************************
      //work out the sum of firing weights of input transitions
      double candidateTransitionWeighting = 0.0;
      for (int i = 0; i < transCount; i++) {
         if((matchingTransition[i] == true) && (marking1EnabledTransitions[i] == true)){
            candidateTransitionWeighting += _pnmlData.getTransitions()[i].getRate();
         }
      }
      //*****************************************************************************************************
      //work out the sum of firing weights of enabled transitions
      double enabledTransitionWeighting = 0.0;
      for (int i = 0; i < transCount; i++) {
         if (marking1EnabledTransitions[i] == true) {
            enabledTransitionWeighting += _pnmlData.getTransitions()[i].getRate();
         }
      }
      return (candidateTransitionWeighting/enabledTransitionWeighting);
   }
   
   
//######################################################################################################################
   //This function is used to generate E-twiddle, the matrix specifying the rates for a specific state change leaving
   //a tangible Mi and entering a vanishing state Mj.
   private double getRateForSpecificStateChange(PetriNet _pnmlData, int[] marking1, int[] marking2) {
      int markSize = marking1.length;
      int[][] incidenceMatrix = _pnmlData.getIncidenceMatrix();
      int transCount = _pnmlData.getTransitions().length;
      boolean[] marking1EnabledTransitions = new boolean[transCount];// = isTransitionEnabled(_pnmlData, marking1); //get list of transitions enabled at marking1
      boolean[] matchingTransition = new boolean[transCount];
      
      for (int i = 0; i <transCount; i++) {
         marking1EnabledTransitions[i] = isTransitionEnabled(_pnmlData, marking1, i);
      }
      
      //**************************************************** *************************************************
      for (int j = 0; j <transCount; j ++) {
         matchingTransition[j] = true;  //initialise matrix of potential transition values to true
      }
      //*****************************************************************************************************
      //get transition needed to fire to get from marking1 to marking2
      for (int i = 0; i < transCount; i++) {
         for (int k = 0; k <markSize; k++) {
            //if the sum of the incidence matrix and marking 1 doesn't equal marking 2,
            //set that candidate transition possibility to be false
            if (((int)marking1[k] + (int)incidenceMatrix[k][i])!= (int)marking2[k]){
               matchingTransition[i] = false;
            }
         }
      }
      //if the state is tangible, all transitions will be timed,
      //so all can be considered in the probability calculation.
      //Otherwise, reset the enabled status of timed transitions to false, as immediate transitions
      //will always fire first.
      
      if (isTangibleState(_pnmlData, marking1)== false) {
         for (int i = 0; i <transCount; i++) {
            if (_pnmlData.getTransitions()[i].getTimed() == true) {
               marking1EnabledTransitions[i] = false;
            }
         }
      }
      //*****************************************************************************************************
      //check if there are any potential transitions from marking 1 to marking 2 - if not return probability 0
      boolean hasAChance = false;
      for (int i = 0; i < transCount; i++) {
         if (matchingTransition[i] == true){
            hasAChance = true;
         }
      }
      if (hasAChance == false) {
         return 0.0;
      }
      //*****************************************************************************************************
      //if a transition is needed to get to marking 2 but isn't enabled at marking 1, return probability 0
      boolean enabledAndMatching = false;
      for (int i = 0; i <transCount; i++) {
         if ((matchingTransition[i] == true) && (marking1EnabledTransitions[i] == true)) {
            enabledAndMatching  = true;
         }
      }
      if (enabledAndMatching = false) {
         return 0.0;
      }
      //******************************************************************************************************
      //work out the sum of firing weights of input transitions
      double candidateTransitionWeighting = 0.0;
      for (int i = 0; i < transCount; i++) {
         if((matchingTransition[i] == true) && (marking1EnabledTransitions[i] == true)){
            candidateTransitionWeighting += _pnmlData.getTransitions()[i].getRate();
         }
      }
      
      return candidateTransitionWeighting;
   }


//######################################################################################################################
   //This function generates a matrix of e-twiddles - used in calculation of throughput.
   private double[][] rateMatrix(PetriNet _pnmlData, StateList list1, StateList list2) {
      int rows = list1.size();
      int cols = list2.size();
      double[][] result = new double[rows][cols];
      
      for (int i = 0; i<rows; i++){
         for (int j = 0; j < cols; j++){
            result[i][j] = getRateForSpecificStateChange(_pnmlData, list1.get(i),list2.get(j));
         }
      }
      return result;
   }

   
//######################################################################################################################
   //This function works out the throughput of an immediate transition for a vanishing state.
   private double getVanishingStateThroughput(PetriNet pnmldata, StateList list1, int transitionNumber, Matrix rateForSpecificState) {
      int length = list1.size();
      double result = 0;
      
      for (int i = 0; i< length; i++){
         double enabledTransitionRates = 0;
         double specifiedTransitionRate = 0;
         boolean[] transStatus = getTangibleTransitionEnabledStatusArray(pnmldata, list1.get(i));
         if (transStatus[transitionNumber]==true){
            int transCount = transStatus.length;
            for (int j = 0; j<transCount; j++){
               if (transStatus[j]==true){
                  enabledTransitionRates += pnmldata.getTransitions()[j].getRate();
               }
            }
            specifiedTransitionRate = pnmldata.getTransitions()[transitionNumber].getRate();
            result+= (specifiedTransitionRate/enabledTransitionRates)*rateForSpecificState.get(0,i);
         }
         
      }
      return result;
   }

   
//######################################################################################################################
   private double getTransitionThroughputSPN(PetriNet pnmldata, StateList list, double[] steadyStateDistrib, int transitionNumber){
      int length = list.size();
      double result = 0;
      
      for (int i = 0; i< length; i++){
         double specifiedTransitionRate = 0;
         boolean[] transStatus = areTransitionsEnabled(pnmldata, list.get(i));
         //System.out.println(transStatus[0] + " " + transStatus[1]+ " "+ transStatus[2]+ " "+ transStatus[3]+ " "+ transStatus[4]+ " transStatuses" );
         if (transStatus[transitionNumber]==true){
            specifiedTransitionRate = pnmldata.getTransitions()[transitionNumber].getRate();
            //System.out.println(specifiedTransitionRate +" specified transition rate");
            result+= (specifiedTransitionRate*steadyStateDistrib[i]);
         }
      }
      return result;
   }

   
//######################################################################################################################
   private double[] getTransitionThroughput(PetriNet pnmldata, StateList vanishing, StateList tangible, Matrix rateForSpecificChange, double[] steadyStateDistribution) {
      Transition[] transitions = pnmldata.getTransitions();
      int transCount = transitions.length;
      double[] result = new double[transCount];
      
      for (int i = 0; i<transCount; i++) {
         if (transitions[i].getTimed()==true){
            result[i] = getTransitionThroughputSPN(pnmldata, tangible, steadyStateDistribution, i);
         } else{
            result[i] = getVanishingStateThroughput(pnmldata, vanishing, i, rateForSpecificChange);
         }
      }
      return result;
   }
   
   
//######################################################################################################################
   /**Constructs a matrix of probabilities of changing from one marking to another.
    * Uses the reachability set to determine all markings, and applies the
    * probMarkingAToMarkingB function to calculate probability for each pair of markings
    * @param _pnmlData
    * @return
    * /
   private double[][]getTransitionProbabilityMatrix(PetriNet _pnmlData, StateList reachabilitySet) {
      int setLength = reachabilitySet.size();
      double [][] transitionProbabilityMatrix = new double[setLength][setLength];
      int recordSize = reachabilitySet.get(0).length;
      int [] stateSpace1 = new int[recordSize];
      int [] stateSpace2 = new int [recordSize];
      
      for (int i = 0; i< setLength; i++){
         stateSpace1 = reachabilitySet.get(i);
         for (int j = 0; j < setLength; j++) {
            stateSpace2 = reachabilitySet.get(j);
            transitionProbabilityMatrix[i][j] = probMarkingAToMarkingB(_pnmlData, stateSpace1, stateSpace2);
         }
      }
      return transitionProbabilityMatrix;
   }
   
   
//######################################################################################################################
   //This is a debugging function for viewing results in the console - not part of analysis
   private void print(boolean[] transitions) {
      int size = transitions.length;
      
      for (int i = 0; i < size ; i++) {
         System.out.print( transitions[i] +" ");
      }
      System.out.println();
   }
   
   
//######################################################################################################################
   public String getName() {
      return MODULE_NAME;
   }
   
   
//######################################################################################################################
   /**
    *
    * @param Matrix c where C is the matrix of transition probabilities from vanishing to vanishing states
    * @param Matrix d where D is the matrix of transition probabilities from vanishing to tangible states
    * @param Matrix e where E is the matrix of transition probabilities from tangible to vanishing states
    * @param Matrix f where F is the matrix of transition probabilities from tangible to tangible states
    * @return double[] - the steady state distributions for Markov chain embedded in tangible states
    * This function produces a solution for the following equations:
    * P' = F + E*((I-C)^-1)*D
    * &pi; bar * P' = &pi; bar
    * &Sigma; (&pi; bar (i)) &forall; i == 1
    * where &pi; bar is the vector of the embedded Markov chain steady state distribution.
    * See Falko Bause - Stochastic Petri Nets - An Introduction to the Theory p181.
    *
    * /
   private double[] getEmbeddedMarkovChainSteadyStateDistribution(Matrix c, Matrix d, Matrix e, Matrix f){
      //Part one - generate P'  (= F + E*((I-C)^-1)*D)
      
      int cSize = c.getRowDimension();
      Matrix iMinusC  = new Matrix(cSize, cSize); //initialise as an identity matrix
      for (int i = 0; i<cSize; i++){
         for (int j = 0; j < cSize; j++) {
            if (i==j) {
               iMinusC.set(i,j,1.0);
            } else {
               iMinusC.set(i,j,0);
            }
         }
      }
      iMinusC.minusEquals(c);
      Matrix iMinusCInverse = iMinusC.inverse();
      Matrix iMinusCInverseD = new Matrix(iMinusCInverse.getRowDimension(),d.getColumnDimension());
      iMinusCInverseD = iMinusCInverse.times(d);
      Matrix eIMinusCInverseD = new Matrix(e.getRowDimension(),iMinusCInverseD.getColumnDimension());
      eIMinusCInverseD = e.times(iMinusCInverseD);
      f.plusEquals(eIMinusCInverseD);
      
      //Part two - rearrange P' and pi_bar to prepare them for Gaussian reduction
      Matrix piBarM = new Matrix(1,f.getColumnDimension());
      for (int i = 0; i <f.getColumnDimension(); i++){
         piBarM.set(0,i,1);
      }
      
      Matrix inverseF = f.transpose();
      for (int j = 0; j <inverseF.getRowDimension(); j ++) {
         inverseF.set(j, j, (inverseF.get(j,j)) - 1);
      }
      int row = inverseF.getColumnDimension();
      
      Matrix solutionMatrix = new Matrix(row+1, row+1);
      
      for (int i = 0; i <= row; i++){
         solutionMatrix.set(0, i, 1);
      }
      for (int i = 1; i<= row; i++) {
         for(int j = 0; j < row; j ++) {
            solutionMatrix.set(i, j, inverseF.get(i-1,j));
         }
      }
      
      for (int i = 1; i <= row; i++) {
         solutionMatrix.set(i, row, 0);
      }
      double[] embeddedMarkovSteadyStateDistrib = reduction(solutionMatrix);
      return embeddedMarkovSteadyStateDistrib;
   }

   
//######################################################################################################################
   private double[][] probabilityMatrix(PetriNet pnmldata, StateList list1, StateList list2) {
      int list1Length = list1.size();
      int list2Length = list2.size();
      double[][] result = new double[list1Length][list2Length];
      
      for(int i = 0; i <list1Length; i++){
         for(int j = 0; j <list2Length; j++){
            result[i][j] = probMarkingAToMarkingB(pnmldata, list1.get(i), list2.get(j));
            //System.out.println(result[i][j]+ "  probability " + i + " " + j);
         }
      }
      
      return result;
      
   }
   
}
                       ***/