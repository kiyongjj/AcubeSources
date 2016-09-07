package com.sds.rqreport.util;

import java.util.*;
import org.apache.log4j.*;

public class BlockingQueue
{
  //private LinkedList elements = new LinkedList();
  private Vector elements = new Vector();
  private boolean closed = false;
  //protected static Logger L = Logger.getLogger("Engine");


  public synchronized final void enqueue( Object new_element )
  throws BlockingQueue.Closed
  {
    if( closed )
      throw new Closed();
    //elements.addLast( new_element );
    elements.addElement(new_element);
    notify();
  }

  public synchronized final Object dequeue() throws InterruptedException,
  BlockingQueue.Closed
  {
    try {
      while( elements.size() <= 0 ) {
        wait();
        if( closed )
          throw new Closed();
      }
      //return elements.removeFirst();
      Object ob = (Object) elements.firstElement();
      elements.removeElementAt(0);
      return ob;
    }
    catch( NoSuchElementException e ) { // Shouldn't happen
      
      System.out.println("Internal error (asynch.BlockingQueue)");
      throw new Error("Internal error (asynch.BlockingQueue)");
    }
  }

  public final boolean is_empty()
  {
  	if ( elements.size() <= 0 )
  		return true;
  	else
  		return false;
  }

  public class Closed extends Exception
  {
    public Closed() {
    	System.out.println("BlockingQueue Closed Exception happened...");
    }
  }
}
