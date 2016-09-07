package com.sds.rqreport.model.querynode;

import org.apache.log4j.Logger;

import com.sds.rqreport.service.queryexecute.RQGetDataIf;

public class RQHierarchyBindingSource {

    //private static RQHierarchyBindingSource rqHierarchyBindval = new RQHierarchyBindingSource();
    private StringBuffer stfBlob = null;
    private int iOffset = 0;
    private Logger L = Logger.getLogger("RQREPORT");
    /*
	public static RQHierarchyBindingSource getInstance(){
		if(rqHierarchyBindval == null){
			rqHierarchyBindval = new RQHierarchyBindingSource();
		}
		return rqHierarchyBindval;
	}
	*/
    //default Constructor
    public RQHierarchyBindingSource(){
        stfBlob = new StringBuffer(500); //50M
        stfBlob.append(RQGetDataIf.BLOB_SEP);
    }

    public StringBuffer getStfBlob(){ //
        return this.stfBlob;
    }

    public void setStfBlobEmpty(){ //
        stfBlob = null;
        iOffset = 0;
    }

    public StringBuffer getStfResult(){
    	try{
            if(stfBlob.length() == 1 && stfBlob.toString().equals(""+RQGetDataIf.BLOB_SEP)){
            	return null;
            	// stfBlob.setLength(0);
            }
    	}catch(Exception e){
    		L.debug("" + e);
    	}

        return this.stfBlob;
    }

    public void setIOffset(int iOffset){
        this.iOffset = iOffset;
    }
    public int getIOffset(){
        return this.iOffset;
    }

}
