package pipe.modules.steadyStateCloud;

import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.clientCommon.SocketIO;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

class ResultsReceiver
{
	private final SocketIO server;
	private final ResultsHTMLPane resultsPane;
	String statusHTML = "";
	
	
	
	public ResultsReceiver(SocketIO server, ResultsHTMLPane resultsPane, String currentStatus)
	{
		this.server = server;
		this.resultsPane = resultsPane;
	}
	
public void receive(PetriNetView pnmlData)
	{
		// Retrive output
		String results = server.receiveFileContent();

		// Parse to display in HTML format
		String resultsOutput = "<h2>Steady State Analysis Results</h2>\n";


		// Convert place / transition IDs into labels
		PlaceView currentPlaceView = null;
		TransitionView currentTransitionView = null;
		boolean inTable = false; // record whether we can currently constructing a table
		String[] lines, values = null;
		String type;



		// Divide the results into lines
		lines = results.split("\\r+|\n+");

		//	The check each line if it refers to a meaure, then check which type
        for(String line : lines)
        {

            values = line.split("\\s+");


            if(values.length >= 3 && values[1].equals("Measure"))
            {
                if(inTable)
                {
                    resultsOutput += "</table>";
                    inTable = false;
                }


                if(values[0].equals("State"))
                {
                    // Convert place Id to label
                    currentPlaceView = pnmlData.getPlaceById(values[3]);
                    values[3] = currentPlaceView.getName();

                }
                else if(values[0].equals("Count"))
                {
                    // Convert transition Id to label
                    currentTransitionView = pnmlData.getTransitionById(values[3]);
                    values[3] = currentTransitionView.getName();
                }


                // rebuild the line with the new label
                resultsOutput += "<b> ";
                for(int j = 0; j < values.length; j++)
                    resultsOutput += values[j] + " ";
                resultsOutput += " </b>\n";


            }

            // Mean or Variance
            else if(values.length == 4 && (values[1].equals("mean") || values[1].equals("variance")))
            {
                if(!inTable)
                {
                    resultsOutput += "<table width=\"300\" border=\"0\">";
                    inTable = true;
                }

                if(values[1].equals("mean"))
                    type = "Mean";
                else
                    type = "Variance";

                resultsOutput += "<tr><td>" + type + "</td> <td>&nbsp;</td> <td>" + values[2] + "</td></tr>\n";

            }

            // Standard Deviation
            else if(values.length == 5 && values[1].equals("std"))
            {
                if(!inTable)
                {
                    resultsOutput += "<table width=\"300\" border=\"0\">";
                    inTable = true;
                }

                resultsOutput += "<tr><td> Standard deviation" + "</td> <td>&nbsp;</td> <td>" + values[3] + "</td></tr>\n";

            }

            // Distribution
            else if(values.length == 3 && values[1].equals("distribution"))
            {
                if(!inTable)
                {
                    resultsOutput += "<table width=\"300\" border=\"0\">";
                    inTable = true;
                }

                resultsOutput += "<tr><td> Distribution </td> <td>&nbsp;</td> <td>&nbsp;</td></tr>\n";
            }

            // Value of distribution
            else if(values.length == 4 && Character.isDigit((values[1].charAt(0))))
            {
                // Must be within table

                resultsOutput += "<tr> <td>&nbsp;</td> <td> " + values[1] + "</td> <td>" + values[2] + "</td></tr>\n";
            }

            else
            {
                if(inTable)
                {
                    resultsOutput += "</table>";
                    inTable = false;
                }
                resultsOutput += line;
            }
        }

		// Ensure that any open table is closed
		if (inTable)
		{
			resultsOutput += "</table>";
			inTable = false;
		}


		resultsPane.setText(resultsOutput);
	}


}
