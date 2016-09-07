package com.sds.rqreport.service.queryexecute;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.querynode.*;
import com.sds.rqreport.util.*;

public class RQFetchExecute {

	protected static Logger log = Logger.getLogger("RQQRYEXE");
	public static Environment env = Environment.getInstance();
	
	public static String convertString(String str)
	{
		 int n = str.length();
	        StringBuffer sb = new StringBuffer(n + n / 10);
	        int idx = 0;
	        int from = 0, end = 0;
	        while(idx < n)
	        {

	        	char ch = str.charAt(idx);
	        	if(ch == '\\' || ch == '\n' || ch == '\t')
	        	{
	        		end = idx;
	        		sb.append(str.substring(from, end));
	        		from = end + 1;
	        		switch(ch)
	        		{
	        		case '\\':
	        			sb.append("\\\\");
	        			break;
	        		case '\n':
	        			sb.append("\\n");
	        			break;
	        		case '\t':
	        			sb.append("\\t");
	        			break;
	        		}
	        	}
	        	idx++;
	        }
	        sb.append(str.substring(from));
	        return sb.toString();
	}
	
	public static String Base64EnStrReplace(String p_baseEnString){
		String lm_rtnBaseEnString = "";
		lm_rtnBaseEnString = p_baseEnString.replaceAll("\n", "");
		return lm_rtnBaseEnString;
	}
	
    /**
     * Method : objectToString
     * @param rsMd
     * @param rs
     * @param iColumnCount
     * @return
     */
	public static String objectToString(ResultSetMetaData rsMd, 
										ResultSet rs, 
										int iColumnCount, 
										String[] bindDataArr,
										int nQueryType, // for procedure 
										CallableStatement cStmt, 
										int nParamInOut, 
										int nParamIdx, 
										RQHierarchyBindingSource oRQHBindingSource, 
										QueryHouse QueryHouseNow, 
										QueryHouse pQueryHouse,
										boolean bExecutor) throws SQLException {
		
        if ( nQueryType != QueryHouse.RQ_QUERY_PROCPARAM &&
        	 (rsMd == null || rs == null) )
        	return "";
        StringBuffer sbfRow = new StringBuffer();
        String tempStr = "";
        int nColType;
        byte[] bbuf = null;
        Blob lm_blob = null;
        String lm_EnString = "";
        InputStream  bais = null;
        ByteArrayOutputStream baos = null;
        
        for(int i=1; i<= iColumnCount; i++){
            if ( nQueryType == QueryHouse.RQ_QUERY_PROCPARAM )
            	nColType = Types.VARCHAR;
            else
            	nColType = rsMd.getColumnType(i);

            switch(nColType){

            case Types.BLOB :

                lm_blob = rs.getBlob(i);
                if (rs.getBlob(i) != null) {
                	
                	int lm_iPos = (int)lm_blob.length();
					bbuf = lm_blob.getBytes(1,lm_iPos);
                	
					if(bExecutor){
                		/////////////// byte array String //////////////////////////////////////
                		//lm_EnString = new String(bbuf);
    					try{
    						lm_EnString = new String(bbuf,"8859_1");
    					}catch(UnsupportedEncodingException e){}
   					
                	}else{
	                	////////////// Base64 Encoding /////////////////////////////////////////
						lm_EnString = Base64Encoder.encode(bbuf);
						lm_EnString = Base64EnStrReplace(lm_EnString);
                	}
                	
					int length = lm_EnString.length(); 
					sbfRow.append( oRQHBindingSource.getIOffset() );
					int iOffset = oRQHBindingSource.getIOffset() + length; 
					oRQHBindingSource.setIOffset(iOffset);
					sbfRow.append(",");
					sbfRow.append(length);
					oRQHBindingSource.getStfBlob().append(lm_EnString);
					
                	//////////////// byte array //////////////////////////////////////////
					/*
					int lm_iPos = (int)lm_blob.length();
					bbuf = lm_blob.getBytes(1,lm_iPos);
					//lm_EnString = new String(bbuf);
					try{
						lm_EnString = new String(bbuf,"8859_1");
					}catch(UnsupportedEncodingException e){}
	
					int length = lm_EnString.length();

					RQBindingBytesSource obindingBytesSource = RQBindingBytesSource.getInstance();
					sbfRow.append(obindingBytesSource.getIOffset());
					int iOffset = obindingBytesSource.getIOffset() + length;
					obindingBytesSource.setIOffset(iOffset);
					sbfRow.append(",");
					sbfRow.append(length);
					obindingBytesSource.addBytesSource(bbuf);
					*/
                	//////////////////////////////////////////////////////////////////////

                	//////////////// Base64 to file code (for test) //////////////////////////////
                	/*
					try{
						FileWriter fw = new FileWriter("d:/tmp/tmp.txt");
						BufferedWriter bw = new BufferedWriter(fw);
						String enStr = (Base64Encoder.encode(bbuf)).toString();
						bw.write(enStr);
						bw.close();

						//byte[] deStrbyte = Base64Decoder.decodeToBytes(enStr);

						InputStream binstr = lm_blob.getBinaryStream();
						FileOutputStream fos = new FileOutputStream("d:/tmp/tmp.jpg");

						byte abyte0[] = new byte[4096];

						int j;
						while((j = binstr.read(abyte0)) != -1){
							fos.write(abyte0, 0, i);
						}
						binstr.close();

						fos.write(bbuf);
						fos.close();

					}catch(IOException e){
						e.printStackTrace();
					}
					*/
                	////////////////////////////////////////////////////////////////////////////

                }else{
                	oRQHBindingSource.getStfBlob().append("");
                }
                if (bindDataArr != null)
                	bindDataArr[i-1] = "";
            break;

            case Types.CLOB :
                Clob lm_clob = rs.getClob(i);
                if (rs.getClob(i) != null) {
                    String lm_strReturn = getClobToString(lm_clob);
                    //sbfRow.append(lm_strReturn);
                    bbuf = lm_strReturn.getBytes();
                    
                    if(bExecutor){
                    	try{
    						lm_EnString = new String(bbuf,"8859_1");
    					}catch(UnsupportedEncodingException e){}
                    }else{
                    	lm_EnString = Base64Encoder.encode(bbuf);
                        lm_EnString = Base64EnStrReplace(lm_EnString);	
                    }
                    
                    int length = lm_EnString.toString().length();
                    sbfRow.append( oRQHBindingSource.getIOffset() );
                    int iOffset = oRQHBindingSource.getIOffset() + length;
                    oRQHBindingSource.setIOffset(iOffset);
                    sbfRow.append(",");
                    sbfRow.append(length);

                    oRQHBindingSource.getStfBlob().append(lm_EnString);

                }else{
                    //sbfRow.append(lm_clob);
                	oRQHBindingSource.getStfBlob().append("");
                }
                if (bindDataArr != null)
                	bindDataArr[i-1] = "";
            break;
            
            /*
			case Types.LONGVARCHAR :
				Reader lm_oStringReader = rs.getCharacterStream(i);
				sbfRow.append(getLongToString(lm_oStringReader));
				//Object lm_oObject = rs.getString(i);
				//strBf.append(lm_oObject);
				if (bindDataArr != null)
					bindDataArr[i-1] = "";
			break;
			*/
            /*
			case Types.NUMERIC :
				BigDecimal lm_bd = new BigDecimal( "" + rs.getBigDecimal(i) );
				if( lm_bd.scale() != 0 ){
					tempStr = "" + lm_bd.setScale(10, 4); // ROUND_HALF_UP
				}else{
					tempStr = "" + lm_bd.intValue();
				}
				sbfRow.append(tempStr);
			break;
			*/
            
            case Types.LONGVARCHAR :
            case Types.LONGVARBINARY : // MDB
            	if(!env.rqreport_rdbms_name.equalsIgnoreCase("informix")){
            		setBase64StringLV(rs, i, sbfRow, oRQHBindingSource, bExecutor);
                    if (bindDataArr != null)
                    	bindDataArr[i-1] = "";
                    break;
            	}
            
            default:
                //    throw new IllegalArgumentException(
                //           "Invalid SQL Type:"+ rsMd.getColumnTypeName(i));
                //sbfRow.append(rs.getString(i) == null || rs.getString(i).equals("") ? "null" : rs.getString(i));
            	ArrayList arr = null;
            	if ( nQueryType == QueryHouse.RQ_QUERY_PROCPARAM )
            	{
            		if ( nParamInOut == DatabaseMetaData.procedureColumnInOut ||
            			 nParamInOut == DatabaseMetaData.procedureColumnOut   ||
						 nParamInOut == DatabaseMetaData.procedureColumnReturn )
            			tempStr = cStmt.getString(nParamIdx);
            		else
            		{
            			arr = (ArrayList)pQueryHouse.getOBindSrc();
		        		Iterator it_arr = arr.iterator();
		        		ArrayList lma = null;
		        		String SPName = "";
		        		while( it_arr.hasNext() ){
		        			lma = (ArrayList) it_arr.next();
		        			SPName = QueryHouseNow.getSPName();
		        			if(SPName.equals(""+lma.get(6))){
		        				tempStr = (String)lma.get(3);
		        				break;
		        			}
		        		}
            			//tempStr = "";
            		}
            	}
            	else
            	{
            		tempStr = rs.getString(i);
            		// eleminate nul value ///////////////////////////////////////////////////
            		if(bExecutor){
            			if(tempStr != null){
		            		if(tempStr.indexOf("\0") != 0)
		            			tempStr = tempStr.replaceAll("\0", "");
            			}
            		}else{
            			if(env.rqreport_dataset_null_check.equalsIgnoreCase("yes")){
                			if(tempStr != null){
    		            		if(tempStr.indexOf("\0") != 0)
    		            			tempStr = tempStr.replaceAll("\0", "");
                			}
                		}
            		}
            		//////////////////////////////////////////////////////////////////////////
            	}
            	String lm_str_char = "";
            	if( tempStr == null ){
            		lm_str_char = "null";
            	}else{

            		try{
                		lm_str_char = tempStr;
            		}catch(Exception e){
            			RequbeUtil.do_PrintStackTrace(log, e);
            		}

            	}
            	sbfRow.append(convertString(lm_str_char));
            	if (bindDataArr != null)
            		bindDataArr[i-1] = tempStr;
            }
            if(i != iColumnCount) sbfRow.append(RQGetDataIf.COL_SEP);
        }
        return sbfRow.toString();

    }

	public static void setBase64StringLV(	ResultSet rs,
											int i,
											StringBuffer sbfRow,
											RQHierarchyBindingSource oRQHBindingSource,
											boolean bExecutor){

		try {
			byte[] bbuf = rs.getBytes(i);
			String lm_EnString = "";
			if(bbuf != null){
				
				if(bExecutor){
					try{
						lm_EnString = new String(bbuf,"8859_1");
					}catch(UnsupportedEncodingException e){}
				}else{
					lm_EnString = Base64Encoder.encode(bbuf);
					lm_EnString = Base64EnStrReplace(lm_EnString);
				}
				
				int length = lm_EnString.toString().length();
				sbfRow.append( oRQHBindingSource.getIOffset() );
				int iOffset = oRQHBindingSource.getIOffset() + length;
				oRQHBindingSource.setIOffset(iOffset);
				sbfRow.append(",");
				sbfRow.append(length);
				oRQHBindingSource.getStfBlob().append(lm_EnString);
			}else{
				int iOffset = oRQHBindingSource.getIOffset();
				sbfRow.append(iOffset);
				oRQHBindingSource.setIOffset(iOffset);
				sbfRow.append(",");
				sbfRow.append(0);
			}
		}catch(SQLException se){
			RequbeUtil.do_PrintStackTrace(log, se);
		}

	}

    /**
     * getClobToString
     * @param
     * @return String
     */
    public static String getClobToString(Clob oClob){

        try{
            Reader oStringReader =   oClob.getCharacterStream();
            StringBuffer sbfWd   =   new StringBuffer();
            char[] buffer = new char[1024];
            //int lm_iPosition = 0;
            int len =   -1;

            while(true){
                len = oStringReader.read(buffer,0,buffer.length);
                sbfWd.append(buffer);
                if(len < 1024){break; }
                buffer = new char[1024];
            }

            if(oStringReader != null) try{ oStringReader.close(); } catch(IOException e) {}
            return (sbfWd.toString()).trim();

        }catch(Exception e){
        	RequbeUtil.do_PrintStackTrace(log, e);
            return null;
        }
    }

    /**
     * getLongToString
     * @param Reader p_oStringReader
     * @return String
     */
    public static String getLongToString(Reader oStringReader) throws SQLException {

        StringBuffer sbfWd = new StringBuffer();
        char[] buffer = new char[10];
        int lm_ilen = -1;
        try {
            if (oStringReader != null) {
                //while( (p_oStringReader.read(buffer)) != -1) {
                //sbfWd.append(buffer);
                while( (lm_ilen = oStringReader.read(buffer,0,buffer.length)) != -1) {
                    sbfWd.append(buffer, 0, lm_ilen);
                    if(lm_ilen < 10){break; }
                    buffer = new char[10];
                }
            }else if(oStringReader == null) {
                sbfWd.append("null");
            }

        } catch(IOException ex) {
        } finally {
            if (oStringReader != null) try { oStringReader.close(); } catch(IOException ex) {}
        }
        return sbfWd.toString().trim();
    }

    /**
     * 해더 정보를 표시한다.
     *
     * @param con 커넥션
     * @param rsMd
     * @return
     */
    public static String shHeadInfo(Connection con, ResultSetMetaData rsMd, int nQueryType){

    	String lm_str = "";
    	try {
    		if ( nQueryType == QueryHouse.RQ_QUERY_PROCPARAM ||
    			 nQueryType == QueryHouse.RQ_QUERY_PROC )
    			lm_str = 0 + ":" + 0;
    		else
    		{
        		if ( rsMd == null )
        			return lm_str;
    			for(int i = 1 ; i <= rsMd.getColumnCount() ; i++){
    				//int cS = rsMd.getColumnDisplaySize(i);
    				if(!env.rqreport_rdbms_name.equalsIgnoreCase("informix")){
	    				int cT = RequbeUtil.getDsType(rsMd.getColumnType(i)); // default 8192
	    				int cS = 0; //default 0
	    				//getTableName, column size 를 받아오는 API 가 드라이버 특성을 탐, 방법이 생길때 까지 주석 처리
	    				/*
						if(  !(rsMd.getTableName(i)).equals("")  ){  // getTableName의 값이 없을경우 아래 검색을 타지 않도록한다.
							ArrayList cSArr = RQcommController.getColumns(con, "", rsMd.getTableName(i));
							if(cSArr.size() != 0){
								String cN = rsMd.getColumnName(i); //search word
								Iterator it = cSArr.iterator();
								while(it.hasNext()){
									ColumnInfo ci = (ColumnInfo) it.next();
									if(cN.equals(ci.name)){
										cS = ci.col_size;
									}
								}
							}else{
								cS = 0;
							}
						}*/
	    				lm_str +=  cT +":"+ cS;
    				}else{
    					lm_str +=  "8192:0";
    				}

    				if (i != rsMd.getColumnCount()) lm_str += RQGetDataIf.COL_SEP;
    			}
    		}
		} catch (SQLException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
		}
    	return lm_str;
    }
}
