/**
 * QueryException
 * 
 * This is the exception that is thrown when something goes wrong
 * in the query editor
 * 
 * @author Tamas Suto
 * @date 23/11/2007
 */

package pipe.modules.queryeditor.gui;

public class QueryException extends Exception {
	
	public QueryException(String msg) {
		super(msg);
	}

}
