package com.sds.rqreport.util;

public class ThreadPool extends Thread {

  private final BlockingQueue pool = new BlockingQueue();
  private /*final*/ int maximum_size;
  private int pool_size;
  private boolean has_closed = false;
  private static int group_number = 0;
  private static int thread_id = 0;



  public ThreadPool(int initial_thread_count, int maximum_thread_count)
  {
	String aa = "ThreadPool" + group_number++ ;
 //   super(  );
    maximum_size = (maximum_thread_count > 0) ? maximum_thread_count : Integer.MAX_VALUE;
    pool_size = Math.min(initial_thread_count, maximum_size);
    for(int i = pool_size; --i >= 0 ;)
      new Pooled_thread().start();
  }

  public ThreadPool() {
//    super( "ThreadPool" + group_number++ );
		group_number++;
    this.maximum_size = 0;
  }

  public void run()
  {
    try {
      while( !has_closed )
      {
        ((Runnable)( pool.dequeue() )).run();
      }
    } catch(InterruptedException e) {
    /* ignore it, stop thread */
    } catch(BlockingQueue.Closed e) {
    /* ignore it, stop thread */
    } catch(Exception e) {
      e.printStackTrace();
      //L.debug("Exception ThreadPool run...");
    }
  }

  public synchronized void execute( Runnable action ) throws Closed
  {
    if( has_closed )
      throw new Closed();
    if( pool_size < maximum_size )
      synchronized( pool )
      {
        if( pool.is_empty() )
        {
          ++pool_size;
          new Pooled_thread().start(); // Add thread to pool
        }
      }
    try
    {
    	pool.enqueue( action ); // Attach action to it.
    }
    catch(BlockingQueue.Closed e) {
    /* ignore it, stop thread */
    	e.printStackTrace();
    //L.debug("Exception ThreadPool execute bc...");
    }
    catch(Exception e){
   // 	throws Closed();
    	e.printStackTrace();
    //L.debug("Exception ThreadPool execute...");
    }
  }

  private class Pooled_thread extends Thread
  {
    public Pooled_thread() {
      super( ThreadPool.this, "T" + thread_id );
    }
  }

  private class Closed extends Exception
  {
    public Closed() {
      System.out.println("ThreadPool Closed Exception happened...");
    }
  }

}

