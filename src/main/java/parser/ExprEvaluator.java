package parser;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import pipe.gui.ApplicationSettings;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

public class ExprEvaluator{
	
	private PetriNetView _pnmldata;
	
	
	public ExprEvaluator(){
		_pnmldata=ApplicationSettings.getApplicationView().getCurrentPetriNetView();
	}
	
	public Double parseAndEvalExprForTransition(String expr) throws EvaluationException{
		String lexpr=new String(expr.replaceAll("\\s",""));
		Iterator iterator = _pnmldata.getPetriNetObjects();
		Object pn;
		String name;
		if (iterator != null) {
			while (iterator.hasNext()) {
				pn = iterator.next();
				
				//we parse the places with their number of tokens
				if (pn instanceof PlaceView) {
					lexpr = findAndReplaceCapacity(lexpr, pn);
					name=((PlaceView) pn).getName().replaceAll("\\s","");
					name = ("#("+name+")");
					if(lexpr.toLowerCase().contains(name.toLowerCase())){
						int numOfToken=((PlaceView) pn).getTotalMarking();
						do{
							lexpr=lexpr.toLowerCase().replace(name.toLowerCase(),numOfToken+"");
						}while(lexpr.toLowerCase().contains(name.toLowerCase()));
					}
				}
			}
		}
		Evaluator evaluator = new Evaluator();
		String result = null;
		try {
			result = evaluator.evaluate(lexpr);
		} catch (EvaluationException e) {
			throw e;
			//e.printStackTrace();
		}
		
		Double dresult = Double.parseDouble(result);
		return dresult;
	}
	
	/**
	 * 
	 * @param expr
	 * @param tokenId
	 * @return -1 indicates the result value is not an integer
	 * @throws Exception 
	 */
	public int parseAndEvalExpr(String expr, String tokenId) throws Exception{
		
		if(!validFloatAndDivision(expr)){
			return -2;
		}
		
		String lexpr=new String(expr.replaceAll("\\s",""));
		Iterator iterator = _pnmldata.getPetriNetObjects();
		Object pn;
		String name;
		if (iterator != null) {
			while (iterator.hasNext()) {
				pn = iterator.next();
				
				//we parse the places with their number of tokens
				if (pn instanceof PlaceView) {
					lexpr = findAndReplaceCapacity(lexpr, pn);
					name=((PlaceView) pn).getName().replaceAll("\\s","");
					name = ("#("+name+")");
					
					
//					if(lexpr.toLowerCase().contains(name.toLowerCase()+"/") ||lexpr.toLowerCase().contains(name.toLowerCase()+" /")||
//							lexpr.toLowerCase().contains("/"+name.toLowerCase()) || lexpr.toLowerCase().contains("/ "+name.toLowerCase())){
//						throw new MarkingDividedByNumberException();
//					}
					
					
					if(lexpr.toLowerCase().contains(name.toLowerCase())){
						LinkedList<MarkingView> markings = ((PlaceView) pn).getCurrentMarkingView();
						int numOfToken=0; 
						for(MarkingView marking : markings){
							if (marking.getToken().getID().equals(tokenId)){
								numOfToken=marking.getCurrentMarking();
							}
						}
						do{
							lexpr=lexpr.toLowerCase().replace(name.toLowerCase(),numOfToken+"");
						}while(lexpr.toLowerCase().contains(name.toLowerCase()));
					}
				}
			}
		}
		Evaluator evaluator = new Evaluator();
		String result = null;
		try {
			result = evaluator.evaluate(lexpr);
		} catch (EvaluationException e) {
			throw e;
			//e.printStackTrace();
		}	catch(Exception ee){
			throw ee;
		}
//		if(dresult % 1 ==0){
//			
//		}
		Double dresult = Double.parseDouble(result);
//		if(!(dresult % 1 == 1)){
//			
//		}
//		if(dresult<1){
//			return 1;
//		}
//		System.out.println("   ->"+dresult % 1 == 1+"");
//		if(dresult%1 != 0)
//		{
//			return -1;
//		}
		//return Math.ceil(dresult);
		return (int)Math.round(dresult); //dresult.intValue();
	}
	
	private String findAndReplaceCapacity(String expr, Object pn){
		String temp = "cap("+((PlaceView) pn).getName().replaceAll("\\s","")+")";
		if(expr.toLowerCase().contains(temp.toLowerCase())){
			int capacity = ((PlaceView) pn).getCapacity();
			expr=expr.toLowerCase().replace(temp.toLowerCase(),capacity+"");
		}
		return expr;
	}
	
	private boolean validFloatAndDivision(String raw) {
		
	    Pattern p = Pattern.compile(".*ceil\\(.*[0-9]*\\.+[0-9]+.*\\).*");
	    Pattern p2 = Pattern.compile(".*[0-9]*\\.+[0-9]+.*");
	    Matcher m = p.matcher(raw);
	    Matcher m1 = p2.matcher(raw);
	    Pattern p3 = Pattern.compile(".*floor\\(.*[0-9]*\\.+[0-9]+.*\\).*");
	    Matcher m3 = p3.matcher(raw);



	    if((m1.find() && !m.find() && !m3.find())){
	    	return false;
	    }	    
	   
		Pattern p1 = Pattern.compile(".*ceil\\(.*/.*\\).*");
	    m=p1.matcher(raw);
	    Pattern p5 = Pattern.compile(".*floor\\(.*/.*\\).*");
	    m1=p5.matcher(raw);
	    if(!m.find()&&!m1.find()&&raw.contains("/")){
	    	return false;
	    }
	    
	    	    

	    return true;
	}
	
}
