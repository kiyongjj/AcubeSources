package com.sds.rqreport.service;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.math.*;

//JNDI
import javax.naming.*;
import javax.sql.DataSource;

import oracle.jdbc.driver.OracleConnection;

import com.sds.rqreport.common.StringComp;
import com.sds.rqreport.model.*;
import com.sds.rqreport.service.cache.*;
import com.sds.rqreport.service.queryexecute.*;
import com.sds.rqreport.util.*;

/**
 * RQcommController
 * @version 1.0
 */

public class RQcommController {
    
	int idx = 1;
	int stmtIdx = 1;
//	int decodeIdx = 1;
	Hashtable connInfoRep;
	Hashtable stmtInfoRep;
//	Hashtable decodeStmtRep;
    
    Connection con = null;
    Statement  stmt = null;
    PreparedStatement  ptmt = null;
    ResultSetMetaData rsMd = null;
    StringBuffer strBf = new StringBuffer();
    StringBuffer strQuery = new StringBuffer();
    StringBuffer strBfcol = new StringBuffer();
    String result = "";
    //separate 
//    private static char SSEP;
//    private static String RSEP = null;
//    private static String CSEP = null;
    static char SSEP = 5;	// Data Set 제일 처음
    static char COL_SEP = 9;	// 
    static char ROW_SEP = 10;
    //properties info 
    private Iterator iterator;
        
    static {
        HashMap hmaps = new HashMap();
        try {
//            ResourceBundle rb = ResourceBundle.getBundle("RQ");
//            Enumeration lm_enum = rb.getKeys();
//            char SSEP = (char)Integer.parseInt(rb.getString("SSEP"));
//            while(lm_enum.hasMoreElements()) {
//                String bkey = (String)lm_enum.nextElement();
//                hmaps.put(bkey, rb.getString(bkey));
//            }
//            SSEP = (char)Integer.parseInt((String)hmaps.get("SSEP"));
//            COL_SEP = (String)hmaps.get("COL_SEP");
//            ROW_SEP = (String)hmaps.get("ROW_SEP");
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public RQcommController(){
    	connInfoRep = new Hashtable(100);
    	stmtInfoRep = new Hashtable(100);
//    	decodeStmtRep = new Hashtable(100);
    }
    
    /**
     * Method : connect : DB Connect / JNDI setting
     * @param 
     * @return void
     */
    public void connect(){

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context)initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)envContext.lookup("jdbc/Reqube");
            con = ds.getConnection();
      
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
    public void connect(String p_strJndiname){
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context)initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)envContext.lookup(p_strJndiname);
            con = ds.getConnection();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method : makeConnection 
     * @param 
     * @return int
     */  
	public synchronized int makeConnection(String driver, String connstr, String id, String pw, boolean[] schema) throws Exception
	{
		Connection c = connect(driver, connstr, id, pw);
		System.out.println(idx + " connected");
		int idnum = idx;
		connInfoRep.put("" + idx++,c);
		DatabaseMetaData 	dbmetadata = c.getMetaData();
		
		if( (dbmetadata.getDriverName()).indexOf("Oracle") != -1 ){
			((OracleConnection)c ).setIncludeSynonyms(true);
		}
		
		boolean				bSchema = dbmetadata.supportsSchemasInDataManipulation();
		schema[0] = bSchema;
		return idnum;
	}
	
	public synchronized int makeConnection(String driver, String connstr, String id, String pw) throws Exception
	{
		Connection c = connect(driver, connstr, id, pw);
		System.out.println(idx + " connected");
		int idnum = idx;
		connInfoRep.put("" + idx++,c);
		return idnum;
	}
	
	public static Connection connect(String driver, String connstr, String id, String pw) throws Exception
	{
		Class.forName(driver).newInstance();
		Connection c = DriverManager.getConnection(connstr, id, pw);
		return c;
	}
    
	public synchronized int queryPrepare(int connid, String strQry) throws SQLException
	{
		Connection c = (Connection)connInfoRep.get(""+connid);
		PreparedStatement stmt = queryPrepare(c, strQry);
		int idnum = stmtIdx;
		stmtInfoRep.put("" + stmtIdx++, stmt);
		return idnum;
	}
    
	public static PreparedStatement queryPrepare(Connection conn, String strQry) throws SQLException
	{
		PreparedStatement stmt = conn.prepareStatement(strQry);
		return stmt;
		
	}
	
	public boolean queryExecute(int stmtid) throws SQLException
	{
		PreparedStatement stmt = (PreparedStatement)stmtInfoRep.get(""+stmtid);
		return stmt.execute();
		
		
	}
	
	public String getFetch(int stmtidx) throws SQLException
	{
		Statement stmt = (Statement)stmtInfoRep.get(""+stmtidx);
		strBf.delete(0,strBf.length());
		return getFetch(stmt,strBf);
	}

	public ArrayList getQueryExecuteDirect(int connid, String sql, boolean getrst, int[] colsize) throws SQLException
	{
		Connection c = (Connection)connInfoRep.get(""+connid);
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		String field, typeName;
		ArrayList arr = new ArrayList(100);

		int lm_columnCount = 0;
		lm_columnCount = rs.getMetaData().getColumnCount();
		colsize[0] = lm_columnCount;
		for(int i=1; i<=lm_columnCount; i++){
			arr.add(new StringComp(rs.getMetaData().getColumnName(i)));
		}
		int nRow = 0;
		
		if (getrst)
		{
	        while( rs.next() && nRow < 100 ){ // 결과 100개로 제한 
	            for(int i=1; i<=lm_columnCount; i++){
	                //column type 이 BLOB 일경우 처리
	            	typeName = rs.getMetaData().getColumnTypeName(i);
	                if(typeName!= null && typeName.equalsIgnoreCase("blob")) {
	                    //Blob로 받아와 byte 배열로 만들고 배열을 스트링으로 만들어 스트링에 넣는다.
	                	field = "BLOB";
	                }
	                else if(typeName != null && typeName.equalsIgnoreCase("clob")) {
	                    //Clob일경우 처리 
	                	field = "CLOB";
	
	                }
	                /**
	                 * ColType 이 Number 일경우 BigDecimal을 써야 정확한 표현이 가능하나
	                 * Low Level Lib. 가 아니기 때문에 호환성 문제가 있을수 있다. 
	                 *
	                 * */
	                /*
	                else if(typeName != null && typeName.equalsIgnoreCase("NUMBER")){
	                	BigDecimal lm_bd = new BigDecimal( "" + rs.getBigDecimal(i) );
	                	if( lm_bd.scale() != 0 ){
	                		field = "" + lm_bd.setScale(10, 4); // ROUND_HALF_UP
	                	}else{
	                		field = "" + lm_bd.intValue();
	                	}
	                }*/
	                else
	                {	
	                	//System.out.println("++++++++++++else typeName+++++++++" +typeName);
	                	field = rs.getString(i);
	                	if(field != null){
		                	if(field.indexOf("\0") != 0)
		                		field = field.replaceAll("\0", "");
	                	}
	                }
	                
	                arr.add(new StringComp(field));
	                
	            }
	            nRow++;
	        }  		
		}
		return arr;
	}
	
	public int endFetch(int stmtidx) throws SQLException
	{
		Statement stmt = (Statement)stmtInfoRep.get(""+stmtidx);
		if ( stmt != null )
		{
			stmt.close();
			stmtInfoRep.remove( ""+stmtidx );
		}
		return 0;
	}
    
    /**
     * Method : query 로 데이터셋 을 가져와 스트링으로 Fetch작업을 한다.  
     * @param 
     * @return String
     */  
	public String getFetch(Statement stmt, StringBuffer strBf) throws SQLException
	{
	    ResultSet rs =  stmt.getResultSet();
		ResultSetMetaData rsMd = rs.getMetaData();
        //ResultSetMetaData 로 컬럼의 갯수를 가져온다.
        int lm_columnCount = rsMd.getColumnCount();

        strBf.append(SSEP);
        while(rs.next()){
           strBf.append(objectToString(rsMd, rs, lm_columnCount));
           strBf.append(ROW_SEP);
        }    

        return strBf.toString();		
	}
	
	public static int endFetch(Statement stmt) throws SQLException
	{
		stmt.close();
		return 0;
	}
	
    /**
     * Method : Connection Close
     * @param 
     * @return void
     */
    public void close(){
        try{
            con.close();    
            //System.out.println("dataBase disconnected.");
        }catch(SQLException e){
            //System.out.println("Connection fail.");
            e.printStackTrace();
        }finally{
            try{
                if(con != null) con.close();
            }catch(SQLException e){}
        }
    }

    /**
     * Method : getRqResultSet 
     * @param 
     * @return String
     */
    public String getRQResultSet(String p_strSql, String p_strIp, String p_strDocName, String p_strRunvar){
     
        if(con == null) connect();      
        String lm_result = getResultset(p_strSql);
        close();
        return lm_result;
    }


    /**
     * Method : getResultset 
     * @param 
     * @return String
     */
    public String getResultset(String p_strSql) {
        
        try{
            String query = p_strSql;   
            ptmt = con.prepareStatement(query);
            result = getFetch(ptmt, strBf);
            ptmt.close();
            
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if(ptmt != null) ptmt.close();
            }catch(SQLException e){}
        }
        return result;
    }
    
    /**
     * Method : Obejct Owner, Object name, Object type 을 arraylist로 가져와 하나의 스트링에 담는다.  
     * @param 
     * @return String
     */
    
	public ArrayList getTables(int idx, String userPattern, String namePattern) throws SQLException
	{
		Connection c = (Connection)connInfoRep.get(""+idx);
		return getTables(c, userPattern,  namePattern );
	}
	
	public ArrayList getColumns(int idx, String schema, String table) throws SQLException
	{
		Connection c = (Connection)connInfoRep.get(""+idx);
		
		return getColumns(c, schema, table);
	}
	
	public ArrayList getSQLColumns(int idx, String sql) throws SQLException
	{
		Connection c = (Connection)connInfoRep.get(""+idx);
		
		return getSQLColumns(c, sql);
	}	
	public ArrayList getSQLColumns(Connection conn, String sql)throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rssql = stmt.executeQuery(sql);
		ResultSetMetaData rm = rssql.getMetaData();
	
		ArrayList arr = getMDColumnInfo( rm );
		stmt.close();
		return arr;
	}
	
	public ArrayList getMDColumnInfo( ResultSetMetaData rm )throws SQLException
	{
		ArrayList arr = new ArrayList(100);
		int size = rm.getColumnCount();
        
		for (int i =1 ; i <= size; ++i)
		{
			ColumnInfo ci = new ColumnInfo();
			
			//String[] ti = new String[4];
			ci.schema = rm.getSchemaName(i);
			ci.table = rm.getTableName(i);
			ci.name = rm.getColumnName(i);   // column name
			ci.data_type = rm.getColumnType(i); // data type
			ci.col_size = rm.getColumnDisplaySize(i); // column size
			ci.dec_digits = rm.getScale(i);
			ci.nullable = rm.isNullable(i); // nullable
			try {
				ci.precision = rm.getPrecision(i); // precision
			} catch (NumberFormatException e) {
				ci.precision = 0;
				e.printStackTrace();
			}
			ci.type_name = rm.getColumnTypeName(i);
	   	 	arr.add(ci);
		}		
		return arr;
	}
 
    /**
     * Method : 소유자별로 Object name과 Object type 을 ArrayList 로 리턴한다.
     * @param Connection c, String userPattern, String[] namePattern
     * @return ArrayList
     */
    public ArrayList getTables(Connection c, String userPattern, String[] namePattern)throws SQLException {
        ArrayList arrTable = new ArrayList(100);

        try {
            DatabaseMetaData dbmetadata = c.getMetaData();
            
            //Owner 가 있을경우 
            if(!userPattern.equals("") && userPattern != null) {                
                userPattern = userPattern.toUpperCase();                
            }else{
                userPattern = null;
            }
            //Object Type 을 parameter 로 받았을경우 
            if(namePattern != null){

                   for (int i = 0 ; i < namePattern.length; i++){
                        namePattern[i] = namePattern[i].toUpperCase();
                        ResultSet rs = dbmetadata.getTables(null, userPattern, null, new String[] {namePattern[i]}); //new String[] {"TABLE"};
           
                        while (rs.next()) {
                            ArrayList arrRow = new ArrayList();
                            //1: catalog(null), 2:table_shema(Owner), 3:table name, 4:table type , 5: remarks
                            for (int j = 2 ; j <= 4 ; j++){  
                                arrRow.add(rs.getString(j));   
                            }
                            arrTable.add(arrRow);
                            /*
                            TableInfo tableinfo = new TableInfo();
                            
                            tableinfo.table_schema = rs.getString(2);
                            tableinfo.table_name = rs.getString(3);
                            tableinfo.table_type = rs.getString(4);
                            tableinfo.table_remark = rs.getString(5);
                            
                            arrTable.add(tableinfo);
                            */
                        }
                        
                    }//end of  for j

            }else if (namePattern == null){
                
                ResultSet rs = dbmetadata.getTables(null, userPattern, null, null); 
                
                while (rs.next()) {
                    ArrayList arrRow = new ArrayList();
                    for (int j = 2 ; j <= 4 ; j++){  //1: catalog(null), 2:table_shema(Owner), 3:table name, 4:table type , 5: remarks
                        arrRow.add(rs.getString(j));   
                    }
                    arrTable.add(arrRow);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return arrTable;
    }   
       
	public static ArrayList getColumns(Connection c, String schema, String table) throws SQLException
	{
        ArrayList arr = new ArrayList(100);
        DatabaseMetaData dbmetadata = c.getMetaData();

        if ( schema != null && schema.length()== 0 )
        	schema = null;
        
		ResultSet rs = dbmetadata.getColumns(null, schema , table , null);
        
        while (rs.next())
		{
			ColumnInfo ci = new ColumnInfo();
			
			try{
				ci.name = rs.getString(4);   	// column name
				ci.data_type = rs.getInt(5); 	// data type
				ci.type_name = rs.getString(6);	// type name
				ci.col_size = rs.getInt(7); 	// column size
				ci.precision = ci.col_size;	// precision for Number
				ci.dec_digits = rs.getInt(9); 	// column scale
				ci.nullable = rs.getInt(11); 	// nullable
			} catch( NumberFormatException e ){
				ci.precision = 0;
				e.printStackTrace();
			} catch( SQLException e ) {
				ci.precision = 0;
				e.printStackTrace();
			}
	   	 	arr.add(ci);
		}
				
		return arr;
        
//        return arrTable;
	}
	
	public static ArrayList getTables(Connection c, String userPattern, String namePattern)throws SQLException
	{
		String[] 			strTableType = {"TABLE","VIEW","ALIAS","SYNONYM"};
		int					nSchmaCnt = 0;
		TableInfo 			tableinfo;
		ResultSet			rs = null;
		DatabaseMetaData 	dbmetadata = c.getMetaData();
		ArrayList 			arr = new ArrayList(100);
		boolean				bSchema = dbmetadata.supportsSchemasInDataManipulation();
		if ( bSchema == false )
			userPattern = null;
		// 소유자 정보가 넘어오지 않으면 일단 소유자(스키마)리스트를 리턴한다.
		if ( bSchema && userPattern.length() == 0 )
		{
			rs = dbmetadata.getSchemas();
			while (rs.next())
			{
				tableinfo = new TableInfo();
				tableinfo.table_schema = rs.getString(1);
				arr.add(tableinfo);
				nSchmaCnt++;
			}
		}
		// 스키마(소유자)가 넘어오면 그 스키마에 해당하는 테이블 리스트를 리턴한다.
		else
		{
			if ( namePattern.length()== 0 )
				namePattern = null;
			rs = dbmetadata.getTables(null, userPattern, namePattern, strTableType);
			while (rs.next())
			{
				tableinfo = new TableInfo();
				
				tableinfo.table_schema = rs.getString(2);
				tableinfo.table_name = rs.getString(3);
				tableinfo.table_type = rs.getString(4);
				tableinfo.table_remark = rs.getString(5);
				
				arr.add(tableinfo);
			}
		}
		
		return arr;
	}	
	
	/**
	 * Method : DMBS에서 제공하는 함수 리스트를 리턴 ({fn 함수명} 형식으로 사용하게 됨) 
	 * @param connIndex	: (IN) Connection Handle Index
	 * @param funcType	: (IN) SYSTEM, STRING, NUMBER, DATE
	 * @return 함수명 리스트
	 */
	public ArrayList getFuncList(int connIndex, int funcType)throws SQLException
	{
		Connection c = (Connection)connInfoRep.get(""+connIndex);
		DatabaseMetaData meta = c.getMetaData();
		ArrayList arr = new ArrayList(100);
		String strFunclist = "";
		String strFunc = "";
		int nFind, nSt, nEnd;
		
		switch(funcType)
		{
		case 0:
			strFunclist = meta.getSystemFunctions();
			break;
		case 1:
			strFunclist = meta.getStringFunctions();
			break;
		case 2:
			strFunclist = meta.getNumericFunctions();
			break;
		case 3:
			strFunclist = meta.getTimeDateFunctions();
			break;
		}
		
		nFind = 0;
		nSt = 0;
		if ( strFunclist.length()!= 0 )
		{
			while( (nFind = strFunclist.indexOf(',', nFind)) >= 0 )
			{
				nEnd = nFind;
				strFunc = strFunclist.substring(nSt, nEnd);
				arr.add( new StringComp(strFunc) );
				nSt = ++nFind;
			}
			strFunc = strFunclist.substring(nSt);
			arr.add( new StringComp(strFunc) );
		}
		return arr;
	}
	
	
	/**
	 * Method : Stored Procedure 정보 리스트를 리턴  
	 * @param connIndex	: (IN) Connection Handle Index
	 * @param schema	: (IN) Schema(소유자)
	 * @return SP 정보(이름, return Type) 리스트
	 */
	public ArrayList getProcedureList(int connIndex, String schema)throws SQLException
	{
		int					procType;
		ResultSet 			rs = null;
		ProcedureInfo 		procedures = null;
		Connection 			c = (Connection)connInfoRep.get(""+connIndex);
		DatabaseMetaData 	meta = c.getMetaData();
		ArrayList			arr = new ArrayList(100);
		
		if ( schema.length()==0 )
			schema = null;
		rs = meta.getProcedures(null, schema, null);
			
		String catalog, procname;
		while(rs.next())
		{
			procedures = new ProcedureInfo();
			procedures.catalog = rs.getString(1);	// package name
			procedures.name = rs.getString(3);		// procedure name
			procedures.returnType = rs.getShort(8);	// return type (procedureResultUnknown, procedureNoResult, procedureReturnsResult)
			arr.add(procedures);
		}
		return arr;
	}
	
	/**
	 * Method : Stored Procedure 파라미터 정보 리스트를 리턴  
	 * @param connIndex	: (IN) Connection Handle Index
	 * @param schema	: (IN) Schema(소유자)
	 * @param pkgname	: (IN) 패키지 이름
	 * @param spname	: (IN) 프로시저 이름
	 * @return SP 파라미터 정보(이름, IN/OUT, 데이터 타입) 리스트
	 */
	public ArrayList getProcedureParams(int connIndex, String schema, String pkgname, String spname) throws SQLException
	{
		ResultSet 			rs = null;
		ArrayList			arr = new ArrayList(100);
		ColumnInfo			paraminfo = null;
		Connection			c = (Connection)connInfoRep.get(""+connIndex);
		DatabaseMetaData	meta = c.getMetaData();
		
		if ( schema.length()==0 )
			schema = null;
		if ( pkgname.length()==0 )
			pkgname = null;
		if ( spname.length()==0 )
			spname = null;
		
		rs = meta.getProcedureColumns(pkgname, schema, spname, null);
		String s = null;
		int nType = 0;
		while(rs.next())
		{
			paraminfo = new ColumnInfo();
			switch(rs.getShort(5)) // COLUMN IN OUT Type
			{
			case DatabaseMetaData.procedureColumnUnknown :
				s = "Unknown";
				break;
			case DatabaseMetaData.procedureColumnIn :
				s = "IN";
				break;
			case DatabaseMetaData.procedureColumnInOut :
				s = "INOUT";
				break;
			case DatabaseMetaData.procedureColumnOut :
				s = "OUT";
				break;
			case DatabaseMetaData.procedureColumnReturn :
				s = "Return Value";
				break;
			case DatabaseMetaData.procedureColumnResult :
				s = "Result Set";
				break;
			}
			paraminfo.name		= rs.getString(4); 	// COLUMN_NAME
			paraminfo.col_type	= rs.getShort(5);	// IN OUT
			paraminfo.col_typename 	= s;
			paraminfo.data_type = rs.getInt(6);		// DATA_TYPE
			paraminfo.type_name = rs.getString(7); 	// TYPE_NAME
			paraminfo.precision = rs.getInt(8);		// PRECISION
			paraminfo.col_size	= rs.getInt(9);		// LENGTH
			paraminfo.dec_digits = rs.getShort(10);	// SCALE
			arr.add(paraminfo);
		}
		return arr;
	}
	
	/**
	 * Method : 프로시저 커서 컬럼정보  
	 * @param connIndex	: (IN) Connection Handle Index
	 * @param schema	: (IN) Schema(소유자)
	 * @param pkgname	: (IN) 패키지 이름
	 * @param spname	: (IN) 프로시저 이름
	 * @param nRtnType	: (IN) procedureResultUnknown, procedureNoResult, procedureReturnsResult
	 * @param inParams	: (IN) 프로시저 Input 파라미터
	 * @param nCurIndex	: (IN) 컬럼정보를 가져올 커서 Index(1base)
	 * @return 컬럼 정보(이름, 데이터 타입, 사이즈등) 리스트
	 */
	public ArrayList getCursorParams(int connIndex, String schema, String pkgname, String spname, int nRtnType, String inParams, int nCurIndex) throws SQLException
	{
		int					nParamCol = 0;
		ArrayList			arrParams = new ArrayList(100);
		ColumnInfo			paramInfo = null;
		ResultSet			rs = null;
		ResultSetMetaData 	rm = null;
		
		ArrayList			arr = executeProc( connIndex, null, schema, pkgname, spname, nRtnType, inParams ); 
		CallableStatement 	cStmt 	  = (CallableStatement)arr.get(0); 		// Callable Stmt for execute Procedure
		int					nParamCnt = ((Integer)(arr.get(1))).intValue(); // Procedure Parameter Count
		ArrayList			paramList = (ArrayList)arr.get(2);				// Procedure Parameter List	
			
		for ( int i=0; i<nParamCnt; i++ )
		{
			paramInfo = (ColumnInfo)paramList.get(i);
			if ( (nCurIndex == i+1) && ((paramInfo.type_name).indexOf("CURSOR") > 0) ) // cursor Type
			{
				rs = (ResultSet)cStmt.getObject(i+1);
				rm = rs.getMetaData();
			}
		}
		
		if ( rm != null )
			arrParams = getMDColumnInfo(rm);
		
		cStmt.close();
		return arrParams;
	}
	
	/**
	 * Method : 프로시저 실행. 프로시저를 실행하여 해당 Statement 핸들index 와 결과를 리턴한다.
	 * @param connIndex	: (IN) Connection Handle Index
	 * @param cstmtIdx	: (OUT) Statement Handle Index
	 * @param schema	: (IN) Schema(소유자)
	 * @param pkgname	: (IN) 패키지 이름
	 * @param spname	: (IN) 프로시저 이름
	 * @param nRtnType	: (IN) procedureResultUnknown, procedureNoResult, procedureReturnsResult
	 * @param inParams	: (IN) 프로시저 Input 파라미터
	 * @param nCurIndex	: (IN) 컬럼정보를 가져올 커서 Index(1base)
	 * @return 프로시저 실행결과 데이터
	 */
	public synchronized ArrayList executeProcedure( int connIndex, Integer[] cstmtIdx, String schema, String pkgname, String spname, 
													int nRtnType, String inParams, String callStmt, int[] nParam ) throws SQLException
	{
		ColumnInfo			paramInfo = null;
		StringBuffer 		lm_strBf = new StringBuffer();
		ArrayList arr = new ArrayList(100);
		ArrayList			arrProc = executeProc( connIndex, callStmt, schema, pkgname, spname, nRtnType, inParams ); 
		int					nParamCnt = ((Integer)(arrProc.get(1))).intValue(); // Procedure Parameter Count
		ArrayList			paramList = (ArrayList)arrProc.get(2);				// Procedure Parameter List	
		CallableStatement	cStmt 	  = (CallableStatement)arrProc.get(0); 		// Callable Stmt for execute Procedure
		cstmtIdx[0] = new Integer(stmtIdx);
		stmtInfoRep.put(""+stmtIdx++, cStmt);
		String 				field;
		String[]			inParam = inParams.split(""+SSEP);
			
		nParam[0] = nParamCnt;
		int i;
		for ( i=0; i<nParamCnt; i++ )
		{
			paramInfo = (ColumnInfo)paramList.get(i);
			arr.add(new StringComp(paramInfo.name));
		}
		for ( i=0; i<nParamCnt; i++ )
		{
			paramInfo = (ColumnInfo)paramList.get(i);
			if ( (paramInfo.type_name).indexOf("CURSOR") > 0 ) // cursor Type
			{
				arr.add(new StringComp(""));
			}
			else if ( paramInfo.col_type == DatabaseMetaData.procedureColumnOut    ||
					  paramInfo.col_type == DatabaseMetaData.procedureColumnInOut  ||
					  paramInfo.col_type == DatabaseMetaData.procedureColumnReturn    )
			{
				field = cStmt.getString(i+1) == null || cStmt.getString(i+1).equals("") ? "" : cStmt.getString(i+1); 
				arr.add(new StringComp(field));
			}
			else
			{
				arr.add(new StringComp(inParam[i]));
			}
		}
		
		return arr;
	}
	
 	public ArrayList executeProc(int connIndex, String callStmt, String schema, String pkgname, String spname, int nRtnType, String inParams) throws SQLException
	{
		Connection 			c = (Connection)connInfoRep.get(""+connIndex);
		int					i;
		int 				nParamCnt = 0;
		String				execProc = null;
		ArrayList			arr = new ArrayList(10);
		ColumnInfo			paramInfo = null;
 		CallableStatement  	stmt = null;
 		ResultSet 			rs = null;
 		String[]			inParam = inParams.split(""+SSEP);
 		
 		ArrayList			paramList = getProcedureParams( connIndex, schema, pkgname, spname );
 		nParamCnt = paramList.size();
 
 		execProc = makeCallStmt( pkgname, spname, nRtnType, nParamCnt );
 		if ( callStmt == null )
 			stmt = c.prepareCall( execProc );
 		else
 			stmt = c.prepareCall( callStmt );
		
//		register the type of the out param - an Oracle specific type
		for ( i=0; i<nParamCnt; i++ )
		{
			paramInfo = (ColumnInfo)paramList.get(i);
			if ( (paramInfo.type_name).indexOf("CURSOR") > 0 ) // cursor Type
			{
				stmt.registerOutParameter(i+1, -10 /*OracleTypes.CURSOR*/);
			}
			else
			{
				switch ( paramInfo.col_type )
				{
					case DatabaseMetaData.procedureColumnIn :
						stmt.setString( i+1, inParam[i] );
						break;
					case DatabaseMetaData.procedureColumnOut :
					case DatabaseMetaData.procedureColumnReturn :
						stmt.registerOutParameter(i+1, Types.VARCHAR);
						break;
					case DatabaseMetaData.procedureColumnInOut :
						stmt.setString( i+1, inParam[i] );
						stmt.registerOutParameter(i+1, Types.VARCHAR);
						break;
				}
			}
		}
		
//		execute and retrieve the result set
		stmt.execute();
		
		arr.add(stmt); 						// Callable Statement
		arr.add(new Integer(nParamCnt)); 	// Parameter Count
		arr.add(paramList); 				// Parameter Information
		return arr;
	}
	
	public String makeCallStmt( String pkgname, String spname, int nRtnType, int nParamCnt )
	{
		int	nParam = 0;
		int nLen = 0;
		String execProc = null;
		String strParam = null;
		StringBuffer strParamBf = new StringBuffer(); 
		
 		try {
			if ( nRtnType == DatabaseMetaData.procedureReturnsResult ) // function (return value)
				nParam = nParamCnt-1;
			else
				nParam = nParamCnt;

			String	strTmp = "?,";
			for ( int i=0; i<nParam; i++ )
			{
				strParamBf.append( strTmp );
			}
			nLen = strParamBf.length();
			strParam = strParamBf.substring(0, nLen - 1);
			
			if ( pkgname.length() != 0 )
				spname = pkgname + '.' + spname;
			if ( nRtnType == DatabaseMetaData.procedureReturnsResult ) // function (return value)
				execProc = "{? = call " + spname + "(" + strParam + ")}";
			else
				execProc = "{call " + spname + "(" + strParam + ")}";
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		return execProc;
	}
	
	/**
	 * Method : 커서 결과 Fetch. 커서 Index에 해당하는 컬럼의 결과를 Fetch 한다. 
	 * @param stmtid	: (IN) Statement Handle Index
	 * @param curIndex	: (IN) 컬럼정보를 가져올 커서 Index(1base)
	 * @return 프로시저 실행 Fetch결과 데이터
	 */
	public ArrayList fetchCursorProc( int stmtid, int curIndex, int[] paramSize )throws SQLException
	{
		ArrayList arr = new ArrayList(100);
		CallableStatement cStmt = (CallableStatement)stmtInfoRep.get(""+stmtid);
		ResultSet		rs = (ResultSet)cStmt.getObject(curIndex);
		ResultSetMetaData rsMd = rs.getMetaData();
        int lm_columnCount = rsMd.getColumnCount();
        paramSize[0] = lm_columnCount;
        String field;
        int nRow = 0;
        
		for(int i=1; i<=lm_columnCount; i++){
			arr.add(new StringComp(rsMd.getColumnName(i)));
		}
/*		
        while(rs.next() && nRow < 100){ // 결과 100개로 제한
        	field = (objectToString(rsMd, rs, lm_columnCount));
        	arr.add(new StringComp(field));
        	nRow++;
        }    
*/		
        while( rs.next() && nRow < 100 ){ // 결과 100개로 제한 
            for(int i=1; i<=lm_columnCount; i++){
                //column type 이 BLOB 일경우 처리
                if(rsMd.getColumnTypeName(i).equalsIgnoreCase("blob")) {
                    //Blob로 받아와 byte 배열로 만들고 배열을 스트링으로 만들어 스트링에 넣는다.
                	field = "BLOB";
                }
                else if(rsMd.getColumnTypeName(i).equalsIgnoreCase("clob")) {
                    //Clob일경우 처리 
                	field = "CLOB";
                }
                else
                {
                	field = rs.getString(i);
                }
                arr.add(new StringComp(field));
            }
            nRow++;
        } 
		return arr;
	}
	
	/**
	 * Method : script Decode 실행, table.column 에서 binddata를 조건으로 결과를 리턴한다.
	 * @param connid	: (IN) Connection Handle Index  
	 * @param stmtid	: (OUT) Statement Handle Index
	 * @param table		: (IN) Decode 할 테이블명
	 * @param column	: (IN) Decode 할 컬럼명
	 * @param where		: (IN) Decode 할 조건 컬럼명
	 * @param binddata	: (IN) Decode 할 조건 데이터
	 * @return Decode 결과 데이터
	 */
	public String decodeExecute(int connid, Integer[] pstmtIdx, String table, String column, String where, String binddata) throws SQLException
	{
		int i;
		Connection c = (Connection)connInfoRep.get(""+connid);
		String strDecode = null;
		PreparedStatement pstmt = null;
		int stmtKey = 0;
		strDecode = "select " + column + " from " + table + " where " + where + " = ?";
		
		if ( pstmtIdx[0].intValue() == 0 ) // decode 처음 시작
		{
			stmtKey = queryPrepare(connid, strDecode);
			pstmtIdx[0] = new Integer(stmtKey);
		}
		else
		{
			stmtKey = pstmtIdx[0].intValue();
		}
		pstmt = (PreparedStatement)stmtInfoRep.get(""+stmtKey);

		if ( pstmt == null )
			return null;
		
		bindParameter( stmtKey, 1, binddata );
		pstmt.execute();
		ResultSet rs =  pstmt.getResultSet();
		
		String field = "";
		int lm_columnCount = 0;
		lm_columnCount = rs.getMetaData().getColumnCount();
		if ( rs.next() )
		{
            for( i=1; i<=lm_columnCount; i++ ){
                //column type 이 BLOB 일경우 처리
                if(rs.getMetaData().getColumnTypeName(i).equalsIgnoreCase("blob")) {
                	field = "BLOB"; 
                }
                else if(rs.getMetaData().getColumnTypeName(i).equalsIgnoreCase("clob")) {
                	field = "CLOB";
                }
                else {
                	field = rs.getString(i);
                }
            }
		}
		return field;
	}	
/*
	public int endDecode(int idx) throws SQLException
	{
		PreparedStatement decodeStmt = (PreparedStatement)decodeStmtRep.get(""+idx);
		if ( decodeStmt != null )
		{
			decodeStmt.close();
			decodeStmtRep.remove( ""+idx );
		}
		return 0;
	}
*/	
	public int bindParameter( int stmtKey, int colIdx, String binddata ) throws SQLException
	{
		PreparedStatement pstmt = null;
		pstmt = (PreparedStatement)stmtInfoRep.get(""+stmtKey);		
		if ( pstmt == null || colIdx < 0 )
			return -100;
		pstmt.setString(colIdx, binddata);
		return 0;
	}
	
	public String executeQueryString( String strQry, String strDBInfo ) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException
	{
		String strResultSet = "";
		String strKey = "123456";
		if(strKey == null || strKey.length() < 2)
    		strKey = "123456";
    	
        String jndiname = null;
        boolean flag = false;
        
        RQCacheImpl cache = new RQCacheUseImpl();
        RunInfo runInfo = new RunInfo(strKey, jndiname, flag, null, strQry, true,
        								null, null, null, null, 0);
        runInfo.setStrDBInfo(strDBInfo);
        strResultSet = cache.setCMger(flag).getResultSet(runInfo);

        runInfo = null;

		return strResultSet;
	}
	
	public int close(int idx) throws SQLException
	{
		Connection c = (Connection)connInfoRep.get(""+idx);		
		if (c == null)
			return -100;
		c.close();
		connInfoRep.remove(""+idx);
		return 0;
	}

    /**
     * Method : Blob 형을 String으로 리턴한다.
     * @param p_oBlob
     * @return
     */
    public static String getBlobToString(Blob p_oBlob){
        
        try {
          
            BufferedInputStream in = new BufferedInputStream(p_oBlob.getBinaryStream());
            StringBuffer lm_strBf = new StringBuffer();
            //InputStream lm_oBufInStream = lm_blob.getBinaryStream();
            byte[] buf = new byte[1024];
            int len = -1;
          
//            while((len = in.read(buf,0,buf.length)) != -1) {
//                lm_strBf.append(buf);
//             
//                //strBf.append(Base64Encoder.encode(buf));
//            }
          
            while(true){
                len = in.read(buf,0,buf.length);
                lm_strBf.append(buf);
                //lm_strBf.append(Base64Encoder.encode(buf));
                if(len < 1024) {break;}
                buf = new byte[1024];
            }
          
            return(lm_strBf.toString()).trim();
            //strBf.append(Base64Encoder.encode(lm_oBufInStream.toString()));
          
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
       
    /**
     * Method : Clob를 String으로 리턴한다. 
     * @param java.sql.Clob p_oClob
     * @return String
     */
    public static String getClobToString(java.sql.Clob p_oClob){

        try{
            Reader lm_oStringReader =   p_oClob.getCharacterStream();
            StringBuffer lm_sStringBuffer   =   new StringBuffer();
            char[] buffer = new char[1024];
            //int lm_iPosition = 0;
            int len =   -1;

            while(true){
                len = lm_oStringReader.read(buffer,0,buffer.length);
                lm_sStringBuffer.append(buffer);
                if(len < 1024){break; }
                buffer = new char[1024];
            }
            
            if(lm_oStringReader != null) try{ lm_oStringReader.close(); } catch(IOException e) {}
            return (lm_sStringBuffer.toString()).trim();

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
        /**
     * Method : getLongToString 
     * @param Reader p_oStringReader
     * @return String
     */
    public static String getLongToString(Reader p_oStringReader) throws SQLException {

        StringBuffer lm_sStringBuffer = new StringBuffer();
        char[] buffer = new char[10];
        int lm_ilen = -1;
        try {
            if (p_oStringReader != null) {
//                while( (p_oStringReader.read(buffer)) != -1) {
//                    lm_sStringBuffer.append(buffer);
                while( (lm_ilen = p_oStringReader.read(buffer,0,buffer.length)) != -1) {
                    lm_sStringBuffer.append(buffer, 0, lm_ilen);
                    if(lm_ilen < 10){break; }
                    buffer = new char[10];
                }
            }else if(p_oStringReader == null) {
                lm_sStringBuffer.append("null");                
            }

        } catch(IOException ex) {
        } finally {
            if (p_oStringReader != null) try { p_oStringReader.close(); } catch(IOException ex) {}
        }
        return lm_sStringBuffer.toString().trim();
    }
    
    
    /**
     * Method : objectToString 
     * @param rsMd
     * @param rs
     * @param lm_columnCount
     * @return
     */
    public static String objectToString(ResultSetMetaData rsMd, ResultSet rs, int lm_columnCount) throws SQLException{
        
        StringBuffer lm_strBf = new StringBuffer();
        for(int i=1; i<=lm_columnCount; i++){

            switch(rsMd.getColumnType(i)){
            case Types.BLOB :
               
                Blob lm_blob = rs.getBlob(i);
                if (rs.getBlob(i) != null) {
                    int lm_iPos = (int)lm_blob.length();
                    byte[] bbuf = lm_blob.getBytes(1,lm_iPos);
                    lm_strBf.append(Base64Encoder.encode(bbuf));
                }else{
                    lm_strBf.append(lm_blob);
                }
            break;
            
            case Types.CHAR :
                lm_strBf.append(rs.getString(i) == null || rs.getString(i).equals("") ? "null" : rs.getString(i));
            break;
            
            case Types.DATE :
                lm_strBf.append(rs.getString(i) == null || rs.getString(i).equals("") ? "null" : rs.getString(i));
            break;
            
            case Types.CLOB :
                Clob lm_clob = rs.getClob(i);
                if (rs.getClob(i) != null) {
                    String lm_strReturn = getClobToString(lm_clob);
                    lm_strBf.append(lm_strReturn);
                }else{
                    lm_strBf.append(lm_clob);
                }
            break;

            case Types.LONGVARCHAR :
                Reader lm_oStringReader = rs.getCharacterStream(i);
                lm_strBf.append(getLongToString(lm_oStringReader));
                //Object lm_oObject = rs.getString(i);
                //strBf.append(lm_oObject);
                
            break;
            
            case Types.NUMERIC :
                lm_strBf.append(rs.getString(i) == null || rs.getString(i).equals("") ? "null" : rs.getString(i));
            break;
            
            case Types.VARCHAR :
                lm_strBf.append(rs.getString(i) == null || rs.getString(i).equals("") ? "null" : rs.getString(i));
            break;
                
            default:
                throw new IllegalArgumentException(
                        "Invalid SQL Type:"+ rsMd.getColumnTypeName(i));
            }
            if(i != lm_columnCount) lm_strBf.append(COL_SEP);
        }
        return lm_strBf.toString();

    }
}
