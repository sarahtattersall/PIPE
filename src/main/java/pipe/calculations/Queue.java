/*
 * Created on 11-Jul-2005
 */
package pipe.calculations;

import java.util.ArrayList;


/**
 * @author Nadeem
 * Implements a Queue data structure.
 */
class Queue {
   
   private final ArrayList queueList;
   
   
   public Queue(){
      queueList = new ArrayList();
   }
   
   
   /**
    * enqueue()
    * Adds an object to the END of the queue.
    * @param object		The object to be queued
    */
   public void enqueue(Object object){
      queueList.add(object);
   }
   
   
   /**
    * dequeue()
    * Removes an object from the FRONT of the queue.
    * @return		The object removed from the queue
    */
   public Object dequeue(){
      Object temp = queueList.get(0);
      queueList.remove(0);
      return temp;
   }
   
   
   public boolean isEmpty(){
      return queueList.isEmpty();
   }
   
}
