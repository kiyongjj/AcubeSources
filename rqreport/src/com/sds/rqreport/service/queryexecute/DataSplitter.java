package com.sds.rqreport.service.queryexecute;

import java.util.*;
import java.io.*;
import java.nio.*;

import com.sds.rqreport.util.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

public class DataSplitter {

	final static public char DEFAULT_QRY_SEP = 0x05;
	final static public char DEFAULT_COL_SEP = 0x09;
	final static public char DEFAULT_ROW_SEP = 0x0A;
	final static public char DEFAULT_BLOB_SEP = 0x06;
	final static public char DEFAULT_IMAGE_SEP = 0x06;
	
	char cQrySep = DEFAULT_QRY_SEP;
	char cColSep = DEFAULT_COL_SEP;
	char cRowSep = DEFAULT_ROW_SEP;
	char cImgSep = DEFAULT_IMAGE_SEP;
	int newlinesize = 2;
	String imgData = "";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File("c:\\test.txt");
		try
		{
			int len = (int)f.length();
			FileInputStream fi = new FileInputStream(f);
			byte[] data = new byte[len];
			fi.read(data);
			DataSplitter ds = new DataSplitter(new String(data,"UTF-8"));
			ds.getIdxArray();
			ds.splitResultSet();
			File designf = new File("c:\\RQReportMS.rqx");
			File resultfile = new File("C:\\resultfile.rqv");
			ds.makeResultFile(designf, resultfile);
			Enumeration enumer = ds.resultSet.keys();
			while(enumer.hasMoreElements())
			{
				Object key = enumer.nextElement();
				System.out.println(((Integer)key).intValue());
				byte[] endata = ds.resultSet.get(key).toString().getBytes("unicode");
				File file = new File("c:\\out_" + key + ".txt");
				FileOutputStream fo = new FileOutputStream(file);
				fo.write(endata);
				fo.close();

				System.out.println(Base64Encoder.encode(endata));
				
			}
			
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
	public DataSplitter(String dataStr)
	{
		this.dataStr = dataStr;
	}
	
	String dataStr = null;
	ArrayList qryIdxArray = new ArrayList();
	ArrayList offsetArray = new ArrayList();
	ArrayList headerLenArray = new ArrayList();
	ArrayList resultSetArray = new ArrayList();
	Hashtable resultSet = new Hashtable();
	public void setNewlineSize(int size)
	{
		this.newlinesize = size;
	}
	
	public int getIdxArray()
	{
		int start = 0;
		int oldpos = -1;//dataStr.indexOf(cQrySep);
		int headpos = 0;
		int pos = 0;
	//	if(oldpos < 0)
	//	{
	//		return -1;
	//	}
	//	else
	//	{
			//offsetArray.add(new Integer(oldpos));
	//	}
		
		boolean appendData = false;
		while((pos = dataStr.indexOf(cQrySep,oldpos + 1)) >= 0)
		{
			headpos  = dataStr.indexOf(cRowSep, pos + 1);
			if(headpos > 0)
			{
				int qryIdx = Integer.parseInt(dataStr.substring(pos + 1, headpos));
				qryIdxArray.add(new Integer(qryIdx));
				int headerlen = headpos - pos -  1;
				headerLenArray.add(new Integer(headerlen));
				offsetArray.add(new Integer(pos));
				oldpos = pos;
			}
			else
			{
				appendData = true;
				break;
			}
		}
		if(!appendData)
		{
//			Find Image Data
			pos = dataStr.indexOf(cImgSep,oldpos+1);
			if(pos > 0)
			{
				offsetArray.add(new Integer(pos));
				imgData = dataStr.substring(pos + 1);
			}
			else
			{
				offsetArray.add(new Integer(dataStr.length()));
			}
		}else
		{
			
			
		}
		
		if( oldpos > 0 )
		{
			
		}
		return 0;
	}
	
	public int splitResultSet()
	{
		int size = qryIdxArray.size();
		for(int i = 0; i < size; ++i)
		{
			Object obj = resultSet.get(qryIdxArray.get(i));
			if(obj == null)
			{
				int pos1 = ((Integer)offsetArray.get(i)).intValue();
				int pos2 = ((Integer)offsetArray.get(i + 1)).intValue();
				int headlen = ((Integer)headerLenArray.get(i)).intValue();
				String data = dataStr.substring(pos1 +  headlen + newlinesize, pos2);
				StringBuffer sb = new StringBuffer(data);
				resultSet.put(qryIdxArray.get(i), sb);
			}
			else if(obj instanceof StringBuffer)
			{
				int pos1 = ((Integer)offsetArray.get(i)).intValue();
				int pos2 = ((Integer)offsetArray.get(i + 1)).intValue();
				int headlen = ((Integer)headerLenArray.get(i)).intValue();
				String data = dataStr.substring(pos1 + headlen + newlinesize, pos2);
				((StringBuffer)obj).append(data);
			}
			else
				return -1;
		}
		return 0;
	}
	
	public int makeResultFile(File f, File resultfile)
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(f);
	    	InputSource source = new InputSource(inputStream);   

	        SAXBuilder builder = new SAXBuilder();
	        Document doc = builder.build(source);
	        Element rootel = doc.getRootElement();
	        rootel.setAttribute("Result", "1");
	        rootel.setAttribute("RunVar", "A|A");
	        
	        ArrayList firstEls = new ArrayList();
	        Element resultArray = builder.getFactory().element("ResultDataSetArray");
	        ArrayList resultSetlist = new ArrayList();
	        int rsize = resultSet.size();
	        for(int i=0; i< rsize;++i)
	        {
	        	Element rds = builder.getFactory().element("ResultDataSet");
	        	StringBuffer sb = (StringBuffer)resultSet.get(new Integer(i));
	        	
	        	// check byteorder 
	        	String lm_str = ByteOrder.nativeOrder().toString();
	        	if(lm_str.equalsIgnoreCase("BIG_ENDIAN")){
	        		
	        		////////////////////// nio /////////////////////////////
	        		//ByteBuffer bbf = ByteBuffer.allocate(size);
		        	//bbf.put(data);
		        	//bbf.flip();
		        	
		        	//ByteBuffer sbf = bbf.order(ByteOrder.LITTLE_ENDIAN);
		        	
		        	//bbf.rewind();
		        	//bbf.order().toString();
		        	///////////////////////////////////////////////////////
		        	
		        	//rds.setAttribute("Size","" + size);z
		        	//rds.setText(Base64Encoder.encode( sbf.array() ));
	        		
		        	//System.out.println(sb.toString());
		        	byte[] data = sb.toString().getBytes("unicode");
		        	int size = data.length - 2;
	        		
	        		byte[] tmp = new byte[1];
	        		
	        		tmp[0] = 0;
	        		for(int ci = 0 ; ci < data.length ; ci+=2 ){
	        			tmp[0]    = data[ci];
	        			data[ci]   = data[ci+1];
	        			data[ci+1] = tmp[0];
	        		}
	        		rds.setAttribute("Size","" + size);
		        	rds.setText(Base64Encoder.encode(data));
		        	
	        	}else{
	        		
					if( i == rsize -1)
					{
						sb.append("\n");
					}	        		
		        	//System.out.println(sb.toString());
		        	byte[] data = sb.toString().getBytes("unicode");
		        	int size = data.length - 2;

	        		rds.setAttribute("Size","" + size);
		        	rds.setText(Base64Encoder.encode(data));
	        	}
	        	resultSetlist.add(rds);
	        }
	       // resultArray.setAttribute("size","" + 0)
	        resultArray.addContent(resultSetlist);
	        Element image = builder.getFactory().element("ResultImage");
	        image.setText(imgData);
	        image.setAttribute("Size", ""+imgData.length());
	        firstEls.add(resultArray);
	        firstEls.add(image);
	        rootel.addContent(firstEls);
	        StringWriter sw = new StringWriter();
	        //File resultfile = new File("C:\\resultfile.rqv");
	        FileOutputStream fo = new FileOutputStream(resultfile);
	        OutputStreamWriter ow = new OutputStreamWriter(fo, "UTF-8");
	        XMLOutputter xmlOut = new XMLOutputter();
			
	        //Format format = Format.getCompactFormat(); // for rqx size 
			Format format = Format.getPrettyFormat();    // for script
			
			format.setEncoding("UTF-8");
			xmlOut.setFormat(format);
		//	xmlOut.output(doc , sw);
			xmlOut.output(doc , ow);
			ow.close();
			fo.close();
			System.out.println(sw);
	        
		}catch(Exception ex)
		{
			ex.printStackTrace();
		
		}
		return 0;
	}


}
