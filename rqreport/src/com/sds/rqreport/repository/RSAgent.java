package com.sds.rqreport.repository;

import java.util.*;
import java.io.*;

import com.sds.rqreport.util.*;

import com.sds.rqreport.common.RQDispatch;


public class RSAgent implements RQDispatch {

	DocRepository docRep = null;

	public RSAgent()
	{
		try {
			docRep = new DocRepository();
		} catch (Exception e) {
			e.printStackTrace();
			docRep = null;
		}
	}

	/* (non-Javadoc)
	 * @see com.sds.rqreport.common.RQDispatch#callByDispatch(int, java.util.List, java.util.List)
	 */
	public int callByDispatch(int functionID, List argarray, List ret)
	{
		int res = 0;
		switch(functionID)
		{
		case 1:
			if(writeFile((String)argarray.get(0), (String)argarray.get(1)))
				res = 0;
			else
				res = -1;
			break;
		case 2:
			if(deleteFile((String)argarray.get(0)))
				res = 0;
			else
				res = -1;
			break;
		case 3:
			if(writeAll((String)argarray.get(0)))
				res = 0;
			else
				res = -1;
		case 99:
				res = -9999;
		}
		return res;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public boolean writeFile(String path, String data)
	{
		File f = docRep.getFile(path);
		byte[] fdata = Base64Decoder.decodeToBytes(data);
		try {
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(fdata);
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		//return null;
		return true;
	}

	public boolean deleteFile(String path)
	{
		File f = docRep.getFile(path);
		f.delete();
		return true;
	}

	public boolean writeAll(String data)
	{
		byte[] fdata = Base64Decoder.decodeToBytes(data);

		try {
			File f = File.createTempFile("RQRS", "TMP");
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(fdata);
			fo.close();
			docRep.extractZip(f.getPath(), docRep.rootPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}
}
