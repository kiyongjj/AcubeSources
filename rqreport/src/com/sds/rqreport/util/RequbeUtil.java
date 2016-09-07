package com.sds.rqreport.util;

import java.util.*;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
import java.io.*;
import java.text.*;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Connection;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import com.sap.mw.jco.*;
import com.sds.rqreport.Environment;
import com.sds.rqreport.service.web.RQLSingleton;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipOutputStream;

public final class RequbeUtil {

	public static void main(String[] arg)
	{
//		File f = new File("G:\\REQUBEWeb6.0\\reqube2006\\defaultRoot");
//		zipaddDir(f);

//		ArrayList a = new ArrayList();
//		ArrayList b = new ArrayList();
//		SplitRuntimeVar("adfa|test|bsfg|dfadf|gggg|adfad", a, b);
//		for(int i =0; i < a.size(); ++i)
//		{
//			System.out.println(a.get(i) + "|" + b.get(i));
//			System.out.println();
//		}
		/*
		System.out.println(replaceCondVarWithValue("select * from 사원 where id=$[idnum] $[this] .$[hello]", "idnum|11134|hello|223"));
		Hashtable runvars = new Hashtable();
		SplitRuntimeVar("AbcDef|'hello \\|\\| test'|fIne|'aaa \\|\\| test'",runvars);
		*/
	
		String hostname = "miette";
		String ip = "269.254.2.2";
		String physicalCPU = "1";
		String logicalCPU = "1";
		String RQversion = "6.0";

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 30); //trial
		String trialDate = "" + calendar.getTimeInMillis();

		String strKey = hostname    + RQLSingleton.STRDELIMITER +
						ip          + RQLSingleton.STRDELIMITER +
						physicalCPU + RQLSingleton.STRDELIMITER +
						logicalCPU  + RQLSingleton.STRDELIMITER +
						RQversion   + RQLSingleton.STRDELIMITER +
		                trialDate;

		//Encrypt
		Encrypter enc = new Encrypter(RQLSingleton.KEYSTR);
		String encStr = enc.encrypt(strKey);
		System.out.println(encStr);

		try {
			FileWriter fw = new FileWriter("license.txt");
			fw.write(encStr);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Decrypt
		Decrypter dec = new Decrypter(RQLSingleton.KEYSTR);
		String decStr = dec.decrypt(encStr);
		System.out.println(decStr);
		
		//CDC DEV. code
		/*
		RequbeUtil test = new RequbeUtil();
		test
				.mSExec(
						"C:/temp/EXTRACT /A C:/temp/RQViewer.cab RQViewer.inf /L C:/temp/ ",
						"");
		test.readFileContentByLine("C:/temp/RQViewer.inf", "[RQViewer.ocx]",
				"FileVersion");
		test.changeFileContent("C:/temp/rqviewer.js", "version=", "\"",
				"1,0,0,1");
		test.del("C:/temp/DOCS");
		*/
	}

    public static String replaceCondVarWithValue(String sql, String runvar)
	{
//		ArrayList names = new ArrayList();
//		ArrayList values = new ArrayList();
		Hashtable runvars = new Hashtable();
		SplitRuntimeVar(runvar, runvars);
		int size = runvars.size();
		int length = sql.length();
		StringBuffer replacedQry = new StringBuffer(length);
		int pos = 0, lastPos = 0, pos2 = 0, pos3 = 0;
		String name;
		String value;
		while((pos = sql.indexOf("$[",lastPos)) > 0)
		{
			//first Part
			replacedQry.append(sql.substring(lastPos, pos));

			//name
			pos2 = sql.indexOf("]",pos);
			if(pos2 > 0)
			{
				name = sql.substring(pos + 2, pos2);
//				get Value
				value = (String)runvars.get(name.toLowerCase());
				if(value != null && value.length() > 0)
				{
					replacedQry.append(value);
				}
				lastPos = pos2 + 1;
			}
			else
				break; //Parsing Error

		}
		if(lastPos > sql.length())
		{
			lastPos = sql.length() - 1;
		}
		replacedQry.append(sql.substring(lastPos));

//
//		for(int i =0; i < names.size(); ++i)
//		{
//			sql = replaceRunvar(sql, (String)names.get(i), (String)values.get(i));//sql.replaceAll("\\$\\["+ (String)names.get(i) +"\\]", (String)values.get(i));
//		}

//		System.out.println(sql.length());
//		while((pos2 = sql.indexOf("$[", pos)) > 0)
//		{
//			pos3 = sql.indexOf("]", pos2);
//			if(pos3 > 0)
//			{
//
//				sql = setNull(sql,pos2, pos3);//sql.substring(0,  pos2) + sql.substring(pos3 + 1);
//				System.out.println(sql.length());
//				pos = pos2;
//			}
//		}
		return replacedQry.toString();
	}
	public static String replaceRunvar(String sql, String name, String value)
	{
		return replaceAll(sql, "\\$\\["+ name +"\\]", value);
	}

	public static String setNull(String sql, int pos2, int pos3)
	{
		return sql.substring(0,  pos2) + sql.substring(pos3 + 1);
	}

	public static void SplitRuntimeVar(String runvar, Hashtable runvars)
	{
		String name = "";
		String value = "";
		String var = runvar;
		int idx;
		int pos0 = 0;
		do
		{
			pos0 = 0;
			while((idx = var.indexOf('|',pos0)) > 0)
			{
				if(var.charAt(idx - 1) == '\\' )
				{
					pos0 = idx + 1;
				}
				else
					break;
			}
			if(idx < 0)
			{
				name = var;
				var = "";
			}
			else
			{
				name = var.substring(0,idx);
				var = var.substring(idx+1);
			}

			if(var.length() < 0)
			{
				value = var;
				var = "";
			}
			else
			{
				pos0 = 0;
				while((idx = var.indexOf('|',pos0)) > 0)
				{
					if(var.charAt(idx - 1) == '\\' )
					{
						pos0 = idx + 1;
					}
					else
						break;
				}

				if(idx >= 0)
				{
					value = var.substring(0,idx);
					var = var.substring(idx+1);
				}
				else
				{
					value = var;
					var = "";
				}
			}
			name.trim();
			//name = name.toLowerCase();
			String strTemp = value;
			//value.trim();

			if(value.trim().length() < 1)
			{
				value = strTemp;
			}

		//	System.out.println(name+ " : " + convertEsc(value));
			//runvars.put(name.toLowerCase(), convertEsc(value).trim());
			runvars.put(name.toLowerCase(), convertEsc(value));
		}while(var.length() > 0);


	}

	public RequbeUtil() {
		super();
	}
	public static String[] split(String arg, String regex)
	{

		int pos = 0, oldpos = 0;
		int num = 0;
		Vector strarray = new Vector();
		while((pos = arg.indexOf(regex,pos)) > -1){
			strarray.add(arg.substring(oldpos,pos));
			oldpos = ++pos;
		}
		strarray.add(arg.substring(oldpos,arg.length()));
		int size = strarray.size();
		String[] result = new String[size];
		for(int i = 0; i < size; ++i)
		{
			result[i] = (String)strarray.get(i);
		}
		return result;
	}

	  public static String replaceAll(String src, String from, String to)
	  {
	    StringBuffer sb = new StringBuffer(src);
	    int pos = 0;
	    int num = 0;

	    int dis = to.length() - from.length();
	    while((pos = src.indexOf(from,pos)) > -1){


	        sb.replace(pos , pos + from.length(),to);
	        src = sb.toString();
	      pos += dis + 1;
	    }
	    String str = sb.toString();
	    sb = null;
	    return str;
	  }

	public static String convertForSQL(String x) {
		if (x != null) {
			return replaceAll(x, "'", "''");
	    }
	    else
	      return null;

	}
	public static String convertEsc(String arg)
	{
		int pos, pos2, len;

		pos = 0;
		pos2 = 0;
		len = arg.length();
		StringBuffer sb = new StringBuffer(len);
		do
		{
			pos2 = arg.indexOf('\\',pos);
			if(pos2 >= pos)
			{
				sb.append(arg.substring(pos,pos2));
				sb.append(arg.charAt(pos2 + 1));
				pos = pos2 + 2;
			}
			else
			{
				sb.append(arg.substring(pos));
				break;
			}
		}while(pos < len);
		return sb.toString();
	}
	public static int zipaddDoc(File f)
	{
		if(!f.getName().endsWith("bak"))
			return -1;
		String path = f.getPath();
		String name = f.getName();
		path = path.substring(0,path.length() - name.length()) + name.substring(0,name.length() - 3) + "jsp";
		File dest = new File(path);
		System.out.println(f.getPath() + "," + dest.getPath());
	  //  f.renameTo(dest);

	    try {
	    	System.out.println("native2ascii -encoding euc-kr " +  f.getPath() + " " + dest.getPath());
	    	dest.delete();
			Runtime.getRuntime().exec("native2ascii -encoding euc-kr " + f.getPath()+  " " + dest.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
//
		return 1;
	}

	public static int  zipaddDir(File f )
	{
		File[] names = f.listFiles();
		File temp = null;
		for (int i = 0; i < names.length; ++i)
		{

		  if (names[i].isDirectory() && !(names[i].getName().equals(".") || names[i].getName().equals("..")))
		  {

			System.out.println("dir:" + names[i]);

			zipaddDir(names[i]);
		  }
		  else {
				zipaddDoc(names[i]);
		  }

		}
		return names.length;
	}


	public static String makeDateString(Calendar cal, String format) {

		   // makedateString
		   // select to_date('2004 9 30 9 10 11','YYYY MM DD HH24 MI SS') from dual; for Oracle
		   // mysql
		   // dateFormat = "'%Y-%M-%D %H:%m:%S'"
		   // access "#%Y/%M/%D %H:%m:%S#";

		if( cal == null)
			cal = Calendar.getInstance();
		    String dateFormat = format;
		    dateFormat = replaceAll(dateFormat, "%Y", makeFormat(cal.get(Calendar.YEAR),4));
		    dateFormat = replaceAll(dateFormat, "%M", makeFormat(cal.get(Calendar.MONTH) + 1, 2));
		    dateFormat = replaceAll(dateFormat, "%D", makeFormat(cal.get(Calendar.DAY_OF_MONTH),2));
		    dateFormat = replaceAll(dateFormat, "%H", makeFormat(cal.get(Calendar.HOUR_OF_DAY),2));
		    dateFormat = replaceAll(dateFormat, "%m", makeFormat(cal.get(Calendar.MINUTE),2));
		    dateFormat = replaceAll(dateFormat, "%S", makeFormat(cal.get(Calendar.SECOND),2));
		    return dateFormat;
	}
	
	public static String makeDataformat(String dformat){
		String year  = dformat.substring(0,4);
		String month = dformat.substring(4,6);
		String day   = dformat.substring(6,8);
		String hour  = dformat.substring(8,10);
		String min   = dformat.substring(10,12);
		String sec   = dformat.substring(12,14);
		return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
	}

	public static String makeFormat(int num, int line)
	{
		String format = "0000000000" + num;
		return format.substring(format.length() - line, format.length());

	}

	/**
	 * SQL Data Type 을 REQUBE Data Type 으로 변환 한다.
	 *
	 * Numeric Type : DS_TYPE_REAL, DS_TYPE_INT, DS_TYPE_BOOL
	 * Character Type : DS_TYPE_CHAR
	 * Binary / Bit Type : DS_TYPE_BLOB, DS_TYPE_CLOB
	 * Date Type : DS_TYPE_DATE, DS_TYPE_TIME, DS_TYPE_TIMESTAMP
	 * Other Type : DS_TYPE_UNKNOWN
	 *
	 ******************************************************
	 * Numeric Type
	 ******************************************************
	 * DS_TYPE_REAL		= 0x1000,
	 * DS_TYPE_INT			= 0x1100,
	 * DS_TYPE_BOOL		= 0x1200,
	 ******************************************************
	 * Character Type
	 ******************************************************
	 * DS_TYPE_CHAR		= 0x2000,
	 ******************************************************
	 * Binary / Bit Type
	 ******************************************************
	 * DS_TYPE_BLOB		= 0x3000,
	 * DS_TYPE_CLOB		= 0x3100,
	 *****************************************************
	 * Date Type
	 ******************************************************
	 * DS_TYPE_DATE		= 0x4000,
	 * DS_TYPE_TIME		= 0x4100,
	 * DS_TYPE_TIMESTAMP	= 0x4200,
	 ******************************************************
	 * Other Type
	 ******************************************************
	 *DS_TYPE_UNKNOWN		= 0x6000
	 *
	 * @param p_DsType SQL Data Type
	 */
	public static int getDsType(int p_DsType){

		int iODBCDataType_NCHAR = -8;
		int iODBCDataType_NTEXT = -10;
		int iODBCDataType_NVARCHAR = -9;

		//System.out.println("++++++++++DSTpye++++++++++:" + p_DsType);

		//String strDsType = "DS_TYPE_UNKNOWN";
		int iDsType = 0x6000;
		if(p_DsType == Types.ARRAY){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.BIGINT){
			//strDsType = "DS_TYPE_INT";
			iDsType = 0x1100;
		}else if(p_DsType == Types.BINARY){
			//strDsType = "DS_TYPE_BLOB";
			iDsType = 0x3000;
		}else if(p_DsType == Types.BIT){
			//strDsType = "DS_TYPE_BLOB";
			iDsType = 0x3000;
		}else if(p_DsType == Types.BLOB){
			//strDsType = "DS_TYPE_BLOB";
			iDsType = 0x3000;
		}else if(p_DsType == Types.BOOLEAN){
			//strDsType = "DS_TYPE_BOOL";
			iDsType = 0x1200;
		}else if(p_DsType == Types.CHAR){
			//strDsType = "DS_TYPE_CHAR";
			iDsType = 0x2000;
		}else if(p_DsType == Types.CLOB){
			//strDsType = "DS_TYPE_CLOB";
			iDsType = 0x3100;
		}else if(p_DsType == Types.DATALINK){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.DATE){
			//strDsType = "DS_TYPE_DATE";
			iDsType = 0x4000;
		}else if(p_DsType == Types.DECIMAL){
			//strDsType = "DS_TYPE_REAL";
		  iDsType = 0x1000;
		}else if(p_DsType == Types.DISTINCT){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.DOUBLE){
			//strDsType = "DS_TYPE_REAL";
			iDsType = 0x1000;
		}else if(p_DsType == Types.FLOAT){
			//strDsType = "DS_TYPE_REAL";
			iDsType = 0x1000;
		}else if(p_DsType == Types.INTEGER){
			//strDsType = "DS_TYPE_INT";
			iDsType = 0x1100;
		}else if(p_DsType == Types.JAVA_OBJECT){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.LONGVARBINARY){
			//strDsType = "DS_TYPE_BLOB";
			iDsType = 0x3000;
		}else if(p_DsType == Types.LONGVARCHAR){
			//strDsType = "DS_TYPE_CLOB";
			iDsType = 0x3100;
		}else if(p_DsType == Types.NULL){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.NUMERIC){
			//strDsType = "DS_TYPE_REAL";
			iDsType = 0x1000;
		}else if(p_DsType == Types.OTHER){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.REAL){
			//strDsType = "DS_TYPE_REAL";
			iDsType = 0x1000;
		}else if(p_DsType == Types.REF){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.SMALLINT){
			//strDsType = "DS_TYPE_INT";
			iDsType = 0x1100;
		}else if(p_DsType == Types.STRUCT){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == Types.TIME){
			//strDsType = "DS_TYPE_TIME";
			iDsType = 0x4100;
		}else if(p_DsType == Types.TIMESTAMP){
			//strDsType = "DS_TYPE_TIMESTAMP";
			iDsType = 0x4200;
		}else if(p_DsType == Types.TINYINT){
			//strDsType = "DS_TYPE_INT";
			iDsType = 0x1100;
		}else if(p_DsType == Types.VARBINARY){
			//strDsType = "DS_TYPE_BLOB";
			iDsType = 0x3000;
		}else if(p_DsType == Types.VARCHAR){
			//strDsType = "DS_TYPE_CHAR";
			iDsType = 0x2000;
		}else if(p_DsType == iODBCDataType_NCHAR){
			//strDsType = "DS_TYPE_CHAR";
			iDsType = 0x2000;
		}else if(p_DsType == iODBCDataType_NTEXT){
			//strDsType = "DS_TYPE_BLOB";
			iDsType = 0x3000;
		}else if(p_DsType == iODBCDataType_NVARCHAR){
			//strDsType = "DS_TYPE_CHAR";
			iDsType = 0x2000;
		}else{
			throw new IllegalArgumentException("Not Supported Type : " + p_DsType);
		}
		return iDsType;
	}

	/**
	 * JCO Field Type 을 REQUBE Data Type 으로 반환한다.
	 * spec. 은 getDsType 과 같다.
	 * @param p_DsType 	field 의 Type
	 * @return REQUBE Data Type을 반환
	 */
	public static int getDsTypeJCO(int p_DsType){

		int iDsType = 0x6000;
		if(p_DsType == JCO.UNINITIALIZED){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == JCO.TYPE_INVALID){
			//strDsType = "DS_TYPE_UNKNOWN";
			iDsType = 0x6000;
		}else if(p_DsType == JCO.TYPE_CHAR){
			//strDsType = "DS_TYPE_CHAR";
			iDsType = 0x2000;
		}else if(p_DsType == JCO.TYPE_DATE){
			//strDsType = "DS_TYPE_DATE";
			iDsType = 0x4000;
		}else if(p_DsType == JCO.TYPE_BCD){
			iDsType = 0x1000;
		}else if(p_DsType == JCO.TYPE_TIME){
			iDsType = 0x4100;
		}else if(p_DsType == JCO.TYPE_BYTE){
			iDsType = 0x3000;
		}else if(p_DsType == JCO.TYPE_ITAB){
			iDsType = 0x6000;
		}else if(p_DsType == JCO.TYPE_NUM){
			iDsType = 0x1000;
		}else if(p_DsType == JCO.TYPE_FLOAT){
			iDsType = 0x1000;
		}else if(p_DsType == JCO.TYPE_INT){
			iDsType = 0x1100;
		}else if(p_DsType == JCO.TYPE_INT2){
			iDsType = 0x1100;
		}else if(p_DsType == JCO.TYPE_INT1){
			iDsType = 0x3000;
		}else if(p_DsType == JCO.TYPE_STRING){
			iDsType = 0x2000;
		}else if(p_DsType == JCO.TYPE_XSTRING){
			iDsType = 0x3000;
		}else if(p_DsType == JCO.TYPE_STRUCTURE){
			iDsType = 0x6000;
		}else if(p_DsType == JCO.TYPE_TABLE){
			iDsType = 0x6000;
		}else{
			throw new IllegalArgumentException("Not Supported Type : " + p_DsType);
		}
		return iDsType;
	}

	public static boolean verifyParam(String arg, int length, String chars)
	{
		if(arg == null || arg.length() > length)
			return false;
		if(chars != null && chars.length() > 0)
		{
			int len = chars.length();
			for(int i =0; i < len; ++i)
			{
				if(arg.indexOf(chars.charAt(i)) > -1)
					return false;
			}
		}
		return true;
	}

    /**
     * sendRedirect가 내부적으로 사용하는 방법이며, pageContext 와 url을 넘기면
     * response.sendRedirect 와 같은 동작을 한다.
     *
     * @param pageContext response 객체를 가져오기위해 pageContext를 받는다.
     * @param url
     */
    public static void do_SendRedirect(PageContext pageContext, String url){
    	HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
    	response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    	response.setHeader("Location", url);
    }

    /**
     * Error log를 StackTrace로 처리
     * @param log
     * @param e
     */
    public static void do_PrintStackTrace(Logger log, Exception e){
    	log.error("trace : " + e);
        StackTraceElement[] lm_ste = e.getStackTrace();
        for(int i = 0 ; i < lm_ste.length; i++){
        	log.error("trace : " + lm_ste[i].toString());
        }
    }

    /**
     * 업로드된 문서를 ZIP 으로 압축하여, RQX와 공존 시킨다.
     * 파라미터를 나누어서 받는 이유는 업로드전에 해당 정보를 모두 가지고 있기 때문이다.
     * 파일압축에러시 리큐브 로그를 남기기 위해서 log 를 받는다.
     * @param sourceFullpath 압축할 문서 전체 경로 (ex. c:\test\test.rqx)
     * @param targetfolderpath zip 으로 압축할 폴더 경로 (ex. c:\test\ )
     * @param ZipedFilename  zip 으로 압축할 파일 이름 (ex. test.rqx)
     * @param level 압축율 기본은 6 이다.
     * @param log reqube log
     * @return
     */
    public static int requbeZip(String sourceFullpath, String targetfolderpath, String ZipedFilename, int level, Logger log){
    	int status = 1;

    	String targetFilename  = ZipedFilename.substring(0, ZipedFilename.lastIndexOf("."));
    	String targetFullpath =  targetfolderpath + targetFilename + ".zip";

		int size = 1024;
		byte[] buf = new byte[size];

		try {
			ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetFullpath)));

			FileInputStream fis = new FileInputStream(sourceFullpath);
			BufferedInputStream bis = new BufferedInputStream(fis, size);

			ZipEntry zentry = new ZipEntry(ZipedFilename);

			zout.putNextEntry(zentry);
			zout.setLevel(level); // 6--> default

			int len;
			while ((len = bis.read(buf)) > 0) {
				zout.write(buf, 0, len);
			}

			zout.closeEntry();
			fis.close();
			bis.close();

			zout.close();

		} catch (FileNotFoundException e) {
			do_PrintStackTrace(log, e);
			return -1;
		} catch (IOException e) {
			do_PrintStackTrace(log, e);
			return -1;
		}

    	return status;
    }

    /**
     * 기등록된 rqx는 사라지고 zip 저장된 압축파일을 rqx로 확장자를 바꾼다.
     * 즉 zip을 사용할경우 압축된 rqx만 존재하게 된다.(단 특정용량이상일경우)
     * 파일압축에러시 리큐브 로그를 남기기 위해서 log 를 받는다.
     * @param sourcepath
     * @param targetpath
     * @return
     */
    public static int replaceZiptoRQX(String sourcepath, String targetpath, Logger log){
    	int status = 1;
    	File sfile = new File(sourcepath);
    	File tfile = new File(targetpath);

    	sfile.delete();
    	tfile.renameTo(sfile);

    	return status;
    }

    //CDC DEV. code
    public void del(String filepath) {
		File f = new File(filepath);
		if (f.exists() && f.isDirectory()) {
			if (f.listFiles().length == 0) {
				f.delete();
			} else {
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						del(delFile[j].getAbsolutePath());
					}
					delFile[j].delete();
				}
			}
		}
	}

    //CDC DEV. code
	public boolean changeFileContent(String changefile, String begin,
			String end, String content) {
		try {
			BufferedReader bin = new BufferedReader(new InputStreamReader(
					new FileInputStream(changefile)));
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(changefile + "bak")));
			String onecontent = bin.readLine();
			System.out.println(onecontent);
			boolean haschange = false;
			while (onecontent != null) {
				int hasword = onecontent.indexOf(begin);
				System.out.println(hasword);
				if (hasword > 0) {
					int versionbegin = onecontent.indexOf(begin)
							+ begin.length();
					int versionend = onecontent.indexOf(end, versionbegin);
					String match = onecontent.substring(versionbegin,
							versionend);
					System.out.println(match);
					if ( !content.equals("") && !match.equals(content)) {
						onecontent = onecontent.replaceFirst(match, content);
						haschange = true;
						System.out.println(onecontent);
					}
				}
				bout.write(onecontent);
				bout.newLine();
				onecontent = bin.readLine();
			}
			bin.close();
			bout.close();
			File oldfile = new File(changefile);
			File newfile = new File(changefile + "bak");
			if (haschange == true) {
				oldfile.delete();
				newfile.renameTo(oldfile);
			} else {
				newfile.delete();
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}
	
	//CDC DEV. code
	public String readFileContentByLine(String searchfile, String searchbegin,
			String searchword) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(searchfile)));
			String temp = null;
			temp = in.readLine();
			boolean tempb = false;
			String returnword = "";
			while (temp != null) {
				if (temp.startsWith(searchbegin)) {
					tempb = true;
				}
				if (tempb == true && temp.startsWith(searchword)) {
					returnword = temp.substring(temp.indexOf("=") + 1);
					//System.out.println(returnword);
					in.close();
					return returnword;
				}
				temp = in.readLine();
			}
			in.close();
			return "";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	//CDC DEV. code
	public boolean deleteFile(String filename) {
		try {
			File file = new File(filename);
			file.delete();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}
	
	//CDC DEV. code
	public boolean mSExec(String rqcommand, String resultfile) {
		try {
			Process child = Runtime.getRuntime().exec(rqcommand);
			/*
			String line = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}*/
			// File oldfile = new File("C:/temp/rqviewer.inf");
			// oldfile.delete();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	public static byte[] merge(byte[] arg1, byte[] arg2){
		
		int len1 = (arg1 == null ? 0 : arg1.length); 
		int len2 = (arg2 == null ? 0 : arg2.length);
		
		byte [] tmp = new byte[len1 + len2]; 
		for(int i = 0 ; i < len1 ; i++){ 
			tmp[i] = arg1[i]; 
		} 
		for(int j = 0 ; j < len2 ; j++){ 
			tmp[len1 + j] = arg2[j]; 
		} 
		return tmp; 
	}
	
	public String strStampToDate(long p_time){
		String strRtnDate = "";
		Date lm_date = new Date(p_time);
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddhhmmssSSS");
		strRtnDate = formatter.format(lm_date);
		return strRtnDate;
	}
	
	/**
	 * 
	 * @param con
	 * @param p_method setAutoCommit, commit, rollback
	 * @param flag
	 */
	public static void setTransaction(Connection con, String p_method, boolean flag) throws SQLException{
	    Environment env  = Environment.getInstance();
		if(env.rqreport_transaction_use.equalsIgnoreCase("yes")){
			if(p_method.equals("setAutoCommit")){
				con.setAutoCommit(flag);
			}else if(p_method.equals("commit")){
				con.commit();
			}else if(p_method.equals("rollback")){
				con.rollback();
			}
		}
		
	}
	
	/**
	 * Query Comment 제거 
	 * @param p_str
	 * @return
	 */
	public String eliminateComment(String p_str){
		StringBuffer stbQry = new StringBuffer(p_str);
		// single comment eliminate 
		String startChar = "--";
		String endChar   = "\n";
		deleteComment(stbQry, startChar, endChar);
		
		// multi comment eliminate
        startChar = "/*";
		endChar   = "*/";
		deleteComment(stbQry, startChar, endChar);
		return stbQry.toString();
	}
	
	/**
	 * Eliminate Comment for Query (SQL)
	 * 
	 * @param stbQry	query
	 * @param startChar	start character
	 * @param endChar	end character
	 **/
	private void deleteComment(StringBuffer stbQry, String startChar, String endChar){
		int startIdx = stbQry.indexOf(startChar);  
		int endIdx   = stbQry.indexOf(endChar , startIdx);
		if(startIdx != -1){
			deleteCommentSelf(stbQry, startChar, endChar, startIdx, endIdx);
		}
	}
	
	
	/**
	 * recursive function for eliminate query (SQL)
	 * @param stbQry	query
	 * @param startChar	start Character
	 * @param endChar	end character
	 * @param startIdx	start index
	 * @param endIdx	end index
	 */
	private static void deleteCommentSelf(StringBuffer stbQry, String startChar, String endChar, int startIdx, int endIdx){
		if(endChar.equals("*/")) {
			// multi
			stbQry.delete(startIdx, endIdx + endChar.length());
		}else{
			// single
			stbQry.delete(startIdx, endIdx);
		}
		int rec_startIdx = stbQry.indexOf(startChar);
		if(rec_startIdx != -1){
			int rec_endIdx   = stbQry.indexOf(endChar , rec_startIdx);
			if(rec_endIdx != -1){
				deleteCommentSelf(stbQry, startChar, endChar, rec_startIdx, rec_endIdx);
			}else{
				deleteCommentSelf(stbQry, startChar, endChar, rec_startIdx, stbQry.length());
			}
		}
	}
}
