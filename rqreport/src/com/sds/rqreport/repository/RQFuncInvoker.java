package com.sds.rqreport.repository;
import java.io.*;
import java.util.*;
import com.sds.rqreport.util.*;

public class RQFuncInvoker {
	DocRepository docRep = null;
	Writer out = null;
	public RQFuncInvoker(Writer out)
	{
		this.out = out;
	}
	public String invoke(String func, String args)
	{
		if("getfolders".equalsIgnoreCase(func))
		{
			//String[] argarr = args.split(",");
			getFolders(Integer.parseInt(args));
			
		}else if("getdocs".equalsIgnoreCase(func))
		{
			//String[] argarr = args.split(",");
			getDocs(Integer.parseInt(args));
		}else if("download".equalsIgnoreCase(func))
		{
			//String[] argarr = args.split(",");
			downloadDoc(Integer.parseInt(args));
		}else if("adddoc".equalsIgnoreCase(func))
		{
			String[] argarr = args.split(",");
			addDoc(Decrypter.decrypt26(argarr[0]), Decrypter.decrypt26(argarr[1]), Decrypter.decrypt26(argarr[2]), Decrypter.decrypt26(argarr[3]));
		}else if("download2".equalsIgnoreCase(func))
		{
			String[] argarr = args.split(",");
			downloadDoc(Decrypter.decrypt26(argarr[0]));
		}else if("rmvtmp".equalsIgnoreCase(func))
		{
			String[] argarr = args.split(",");
			removeTmp(Decrypter.decrypt26(argarr[0]));
		}
		
		return null;
	}
	public static void main(String[] args)
	{
	//	RQFuncInvoker invoker = new RQFuncInvoker(System.out.println(x));
	}
	
	public String getFolders(int id)
	{
		ArrayList list = null;
		DocInfo di = null;
		JDBCHelper jdbc = null;
		try {
			if(docRep == null)
			{
				docRep = new DocRepository();
				
			}
			//int id = docRep.getID(path);
			if(id == 1)
			{
				id = docRep.getDocID("/");
			}
			jdbc = new JDBCHelper();
			jdbc.connect();
			docRep.jdbc = jdbc;
			list = docRep.getMutiDocInfo(new Integer(id));
			Iterator it = list.iterator();
			out.write("<RESULTDATA>");
			while(it.hasNext())
			{
				di = (DocInfo)it.next();
				if(di.doc_fg == 'F')
				{
					out.write("" + di.idx);
					out.write("^");
					//out.write(Encrypter.encrypt26(di.name));
					out.write(Encrypter.encrypt26(di.name));
					out.write("^");
					out.write(Encrypter.encrypt26(di.fullPath));
					out.write("^");
					out.write("" + di.pdocID);
					out.write("\n");
				}
			}
			out.write("</RESULTDATA>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally
		{
			if(jdbc != null)
				jdbc.close();
		}
		
		return null;
	}

	public String getDocs(int id)
	{
		ArrayList list = null;
		DocInfo di = null;
		JDBCHelper jdbc = null;
		try {
			if(docRep == null)
			{
				docRep = new DocRepository();
				
			}
			//int id = docRep.getID(path);
			if(id == 1)
			{
				id = docRep.getDocID("/");
			}
			jdbc = new JDBCHelper();
			jdbc.connect();
			docRep.jdbc = jdbc;
			list = docRep.getMutiDocInfo(new Integer(id));
			Iterator it = list.iterator();
			out.write("<RESULTDATA>");
			while(it.hasNext())
			{
				di = (DocInfo)it.next();
				if(di.doc_fg == 'D')
				{
					out.write("" + di.idx);
					out.write("^");
					out.write(Encrypter.encrypt26(di.name));
					//out.write(Encrypter.encrypt26(di.name));
					out.write("^");
					out.write("" + di.pdocID);
					out.write("^");
					out.write(Encrypter.encrypt26(di.fullPath));
					out.write("^");
					out.write("4");
					out.write("^");
					out.write("5");
					out.write("^");
					out.write(di.createUserID);
					out.write("^");
					out.write("dsname");
					out.write("^");			
					out.write("9");
					out.write("^");
					out.write("10");
					out.write("^");
					out.write("\n");
				}
			}
			out.write("</RESULTDATA>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally
		{
			if(jdbc != null)
				jdbc.close();
		}
		return null;
	}
	
	public String downloadDoc(int docID)
	{
		JDBCHelper jdbc = null;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			if(docRep == null)
			{
				docRep = new DocRepository();
				
			}			
			docRep.jdbc = jdbc;		
			DocInfo di = (DocInfo)docRep.getInfo(new Integer(docID));
			File f = docRep.getFile(di.fullPath);
			if(f != null && f.exists() && f.isFile())
			{
				byte[] fileData;
				fileData = JDBCHelper.readFile(f);
				out.write("<RESULTDATA>");
				out.write(Base64Encoder.encode(fileData));
				out.write("</RESULTDATA>");
			}
		}catch(Exception ex)
		{
			
		}finally
		{
			if(jdbc != null)
				jdbc.close();
		}
		return null;
	}
	
	public String downloadDoc(String doc)
	{
		JDBCHelper jdbc = null;
		try {
			docRep = new DocRepository();
			File f = docRep.getFile(doc);
			if(f != null && f.exists() && f.isFile())
			{
				byte[] fileData;
				fileData = JDBCHelper.readFile(f);
				out.write("<RESULTDATA>");
				out.write(Base64Encoder.encode(fileData));
				out.write("</RESULTDATA>");
				
				if(doc.endsWith("TMP"))
				{
					f.delete();
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{

		}
		return null;	
	}
	
	public String removeTmp(String tmpName)
	{
		JDBCHelper jdbc = null;
		try {
			docRep = new DocRepository();
			File f = docRep.getFile("/" + tmpName);
			if(f != null && f.exists() && f.isFile() && tmpName.endsWith("TMP"))
			{
				f.delete();
			}
		}catch(Exception ex)
		{
			
		}finally
		{

		}
		return null;	
	}
	public String addDoc(String resultFile, String fileName, String id, String desc)
	{
		JDBCHelper jdbc = null;
		try {
			jdbc = new JDBCHelper();
			jdbc.connect();
			if(docRep == null)
			{
				docRep = new DocRepository();
				
			}			
			docRep.jdbc = jdbc;
			File srcFile = docRep.getFile("/"+resultFile);
			byte[] data = jdbc.readFile(srcFile);
			if(data != null)
			{
				File outfile = docRep.getFile(fileName);
				File dir = new File(outfile.getParent());
				// 부모디렉토리가 존재하지 않을경우 만들어준다.
				if(!dir.exists()) dir.mkdirs();
				FileOutputStream fo = new FileOutputStream(outfile);
				fo.write(data);
				fo.close();
				int res = docRep.addDocInfo(outfile, id, desc);
				if(res == 0)
				{
					srcFile.delete();
				}
			}
			
			
		}catch(Exception ex)
		{
			
		}finally
		{
			if(jdbc != null)
				jdbc.close();
		}
		return null;
	}
}
