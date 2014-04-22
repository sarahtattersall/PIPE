package pipe.modules.steadyStateCloud;

import pipe.common.AnalysisType;
import pipe.common.PerformanceMeasure;
import pipe.common.SimplePlaces;
import pipe.common.SimpleTransitions;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.clientCommon.HTMLPane;
import pipe.modules.clientCommon.SocketIO;
import pipe.views.PetriNetView;

import javax.swing.*;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.UnknownHostException;

/**
 * This class performs the transfers the _pnmlData to the server
 * while updating the user of job progress
 *
 * @author Barry Kearns
 * @date August 2007
 */

public class Analyse implements Runnable {
    private final HTMLPane progressPane;

    private final ResultsHTMLPane resultsPane;

    private String serverAddr = "";

    private int serverPort = 0;

    private PerformanceMeasure performanceMeasure;

    private PetriNetView _pnmlData;

    private JTabbedPane tabbedPane;

    private String status = "<h2>Steady State Analysis Progress</h2>";


    public Analyse(PetriNetView pnmlData, HTMLPane progress, ResultsHTMLPane results) {
        this._pnmlData = pnmlData;
        progressPane = progress;
        resultsPane = results;
    }

    public void setServer(String serverAddr, int serverPort) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
    }

    public void setStateMeasure(PerformanceMeasure performanceMeasure) {
        this.performanceMeasure = performanceMeasure;
    }

    public void run() {

        _pnmlData = checkColour(_pnmlData);


        // Convert the PNML data into a serialisable form for transmission
        SimplePlaces splaces = new SimplePlaces(_pnmlData);
        SimpleTransitions sTransitions = new SimpleTransitions(_pnmlData);


        try {
            updateUI("Opening Connection");
            SocketIO serverSock = new SocketIO(serverAddr, serverPort);
            serverSock.send(AnalysisType.STEADYSTATE); // Inform server of the process to be performed

            updateUI("Sending data");
            serverSock.send(splaces);
            serverSock.send(sTransitions);
            serverSock.send(performanceMeasure);

            updateUI("Server Scheduling Process");
            StatusListener serverListener = new StatusListener(serverSock, progressPane, status);
            status = serverListener.listen();

            updateUI("Receiving Results");
            ResultsReceiver resultsReceiver = new ResultsReceiver(serverSock, resultsPane, status);
            resultsReceiver.receive(_pnmlData);

            updateUI("Closing Connection");
            serverSock.close();


            // Slow the transition between progress tab and results tab
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
            }

            // Add results pane and set active
            tabbedPane.addTab("Results", resultsPane);
            tabbedPane.setSelectedComponent(resultsPane);
        } catch (StreamCorruptedException sce) {
            updateUI("Stream Corrupted Exception" + sce.getMessage());
        } catch (UnknownHostException uhe) {
            updateUI("Unknown host exception " + uhe.getMessage());
        } catch (OptionalDataException ode) {
            updateUI("Data Exception" + ode.getMessage());
        } catch (IOException ioe) {
            updateUI("Unable to connect to server " + serverAddr + " : " + serverPort + ": " + ioe.getMessage());
        }


    }

    public PetriNetView checkColour(PetriNetView pnmlData) {

        // Check if this net is a CGSPN. If it is, then this
        // module won't work with it and we must convert it.

        if (pnmlData.getTokenViews().size() > 1) {
            //			  updateUI("Coloured Petri Net detected. Converting...");
            //			  Expander expander = new Expander(pnmlData);
            //			  pnmlData = expander.unfoldOld();
            //			  updateUI("Conversion successful.");
        }

        return pnmlData;

    }

    private void updateUI(String update) {
        // setText is a thread safe operation so we can freely use it within this thread
        status += update + "<br>";
        progressPane.setText(status);
    }

    public void setTabbedPane(JTabbedPane inputPane) {
        tabbedPane = inputPane;
    }

}
