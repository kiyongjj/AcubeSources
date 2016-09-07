package com.sds.rqreport.service.queryexecute;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.sql.SQLException;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.RQCacheContent;
import com.sds.rqreport.model.querynode.*;
import com.sds.rqreport.service.cache.*;
import com.sds.rqreport.service.web.RQDocStatGetRC;

import javax.naming.*;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;

import com.sds.rqreport.service.*;
import com.sds.rqreport.service.Timer;
import com.sds.rqreport.util.RequbeUtil;

/**
 * DB로 부터 데이터를 받아와 fetch 작업을 하는 클래스
 * RQGetDataHierarchy.java
 *
 */
public class RQGetDataHierarchy extends RQGetDataIf{

    private String strJndiname = "";
    private static Hashtable dsMan = new Hashtable();
    Connection con = null;
    Hashtable connInfoRep;

    StringBufferEx strBf = null; //new StringBuffer();
    String result = "";

    int iSQLCnt;
    int iSQLmax;
    PreparedStatement[] ptmt;
    ResultSet[] rs;
    ResultSetMetaData[] rsMd;
    ArrayList connArr;
    private QueryFrame queryframe;
    private boolean bExecutor;

    private boolean flagIs;
    private String strDB;
    private String strXml;
    private String[] docDS = null;
    private Logger log = Logger.getLogger("RQQRYEXE");
    ///////////////// RQDoc Statistic //////////////////
    private int totalrowcnt = 0;
    ///////////////////////////////////////////////////
    public RQGetDataHierarchy(){}

    public RQGetDataHierarchy(RunInfo runinfo)
    {
      	connInfoRep = new Hashtable(20);
        this.strJndiname = runinfo.getJndiName();
        this.flagIs = runinfo.isBUseCache();
        this.strXml = runinfo.getStrXml();
        this.strDB = runinfo.getStrDBInfo();
        this.bExecutor = runinfo.isBExecutor();
        this.classType = XML_TYPE;
        this.docDS = runinfo.getDocDS();
        Environment env = Environment.getInstance();
        if(env.rqreport_dataset_buffer_use.equals("yes")){
        	strBf = new StringBufferEx(runinfo.getOut());
        }else{
        	strBf = new StringBufferEx();
        }
    }

    public RQGetDataHierarchy(String strJndiname, boolean flagIs, String strXml, String strDBInfo, boolean bExecutor){
    	connInfoRep = new Hashtable(20);
        this.strJndiname = strJndiname;
        this.flagIs = flagIs;
        this.strXml = strXml;
        this.strDB = strDBInfo;
        this.bExecutor = bExecutor;
        this.classType = XML_TYPE;
       	this.strBf = new StringBufferEx();

    }

    public RQGetDataHierarchy(String strJndiname, boolean flagIs, String strXml, String strDBInfo, boolean bExecutor, Writer out){
    	connInfoRep = new Hashtable(20);
        this.strJndiname = strJndiname;
        this.flagIs = flagIs;
        this.strXml = strXml;
        this.strDB = strDBInfo;
        this.bExecutor = bExecutor;
        this.classType = XML_TYPE;
        this.strBf = new StringBufferEx(out);

    }

    public void setConnInfoArr( ArrayList connArr){
    	this.connArr = connArr;
    }

    public void setQueryframe(QueryFrame queryframe){
        this.queryframe = queryframe;
    }

    public QueryFrame getQueryFrame(){
    	return this.queryframe;
    }

    public void setExecutor(boolean bExecutor){
    	this.bExecutor = bExecutor;
    }

    public void connect() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException{
    	
    	Environment tenv  = Environment.getInstance();
    	
    	if (bExecutor){

    		int nDBIdx;
    		String strDriver, strConnStr, strID, strPW;
    		ConnectionInfo connInfo;
    		for ( int i=0; i<connArr.size(); i++ )
    		{

    			connInfo = (ConnectionInfo)connArr.get(i);
    			nDBIdx 		= connInfo.connidx;
    			strDriver 	= connInfo.driver;
    			strConnStr 	= connInfo.connection;
    			strID 		= connInfo.id;
    			strPW 		= connInfo.pw;

    			//System.out.println("strDriver-" +strDriver+"-");
    			//System.out.println("strConnStr-" +strConnStr+"-");
    			//System.out.println("strConnStr.trim-" +strConnStr.trim()+"-");
    			//System.out.println("strID-"+strID+"-");
    			//System.out.println("strPW-" +strPW+"-");

    			Class.forName(strDriver).newInstance();
    			Connection c = DriverManager.getConnection(strConnStr, strID, strPW);
    			log.info("Connect [START], " + c);
    			connInfoRep.put("" + nDBIdx, c);

    		}

    	}else{

        	int nDBIdx;
    		String strDriver, strConnStr, strID, strPW;
    		ConnectionInfo connInfo;

    		if(docDS != null && docDS.length > 0){

    			for(int i = 0; i < docDS.length; ++i) {

    				if(docDS[i].equalsIgnoreCase("XML") || docDS[i].equalsIgnoreCase("TEXT")) continue;
	               	DataSource ds = (DataSource)dsMan.get(docDS[i]);
	               	if(ds == null)
	               	{
	               		Context initContext = new InitialContext();
	               		String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
	               		NamingEnumeration oNenum = null;
	               		try{
	               			oNenum = initContext.listBindings(bind_str);
	               			if(!oNenum.hasMore()){
	               				bind_str = "java:comp/env";
	               			}
	               			Context envContext  = (Context)initContext.lookup(bind_str);
	               			ds = (DataSource)envContext.lookup(docDS[i]);
	               		}catch(Exception e){
	               			ds = (DataSource)initContext.lookup(docDS[i]);
	               		}
	               		if(ds != null)
	               			dsMan.put(docDS[i], ds);
	               	}

    				Connection c = ds.getConnection();
    				log.info("Connect [START], " + c);
    				connInfoRep.put("" + i, c);
    			}

			} else {

    			if(connArr == null){

               		Context initContext = new InitialContext();
               		String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
               		NamingEnumeration oNenum = null;
               		DataSource ds = null;
               		try{
               			oNenum = initContext.listBindings(bind_str);
               			if(!oNenum.hasMore()){
               				bind_str = "java:comp/env";
               			}
               			Context envContext  = (Context)initContext.lookup(bind_str);
               			ds = (DataSource)envContext.lookup(strJndiname);
               		}catch(Exception e){
               			ds = (DataSource)initContext.lookup(strJndiname);
               		}
    				Connection c = ds.getConnection();
    				log.info("Connect [START], " + c);
    				connInfoRep.put("" + 0, c);

    			} else {

    				for (int i=0; i<connArr.size(); i++) {
    	    			connInfo = (ConnectionInfo)connArr.get(i);
    	    			nDBIdx 		= connInfo.connidx;
    	    			strDriver 	= connInfo.driver;
    	    			strConnStr 	= connInfo.connection;
    	    			strID 		= connInfo.id;
    	    			strPW 		= connInfo.pw;

    	    			//DBUtil oDBUtil = new DBUtil();
    					//oDBUtil.setJndiMap(oDBUtil.getBindingObj());
    					//String strJndiname = (String) oDBUtil.jndiMap.get(strConnStr.trim());
    					String strJndiname = "";

    		            if(strJndiname != null && !strJndiname.equals("")){

    		            	Context initContext = new InitialContext();
    		            	String bind_str = tenv.serverType == 5 ? "java:comp/env" : "";
    	               		NamingEnumeration oNenum = null;
    	               		DataSource ds = null;
    	               		try{
    	               			oNenum = initContext.listBindings(bind_str);
    	               			if(!oNenum.hasMore()){
    	               				bind_str = "java:comp/env";
    	               			}
    	               			Context envContext  = (Context)initContext.lookup(bind_str);
    	               			ds = (DataSource)envContext.lookup(strJndiname);
    	               		}catch(Exception e){
    	               			ds = (DataSource)initContext.lookup(strJndiname);
    	               		}

    			            Connection c = ds.getConnection();
    			            log.info("Connect [START], " + c);
    			            connInfoRep.put("" + nDBIdx, c);
    		            }else{
    		    			Class.forName(strDriver).newInstance();
    		    			Connection c = DriverManager.getConnection(strConnStr, strID, strPW);
    		    			log.info("Connect [START], " + c);
    		    			connInfoRep.put("" + nDBIdx, c);
    		            }

    	    		}
    			}
	    	}
    	}
    }

    public void fetch(){}

	public void fetch(int p_iSQLIdx, RQHierarchyBindingSource oRQHBindingSource) throws SQLException{

        QueryHouse oQueryHouseNow = (QueryHouse) (queryframe.getSource()).get(Integer.toString(p_iSQLIdx));
        QueryHouse oQueryHouseParent = null;

        int iSQLIdx = oQueryHouseNow.getSQLIdx();               //queryhouse 의 SQLIdx
        int iSQLOrder = oQueryHouseNow.getSQLOrder();
        String SQLStmt = oQueryHouseNow.getSQLStmt();           //queryhouse 의 SQLStmt
        log.debug("SQLStmt : " +SQLStmt);
  //      System.out.println("SQLStmt : " + SQLStmt);

        String strLastNodeIs = oQueryHouseNow.getLastNodeIs();  //queryhouse 의 strLastNodeIs
        int nDBIdx = oQueryHouseNow.getDBIdx();					//queryhouse 의 DB Index
        int nQueryType = oQueryHouseNow.getQueryType();			//queryhouse 의 SQL Type
        int nParentQueryType = QueryHouse.RQ_QUERY_UNKNOWN;
        int nParamIndex = oQueryHouseNow.getParamIndex();		//queryhouse 의 Parameter Index
        int nParentSQLIdx = oQueryHouseNow.getIParentSQLIdx();	//queryhouse 의 부모 쿼리 index
        int nParamInOut = oQueryHouseNow.getInOutType();
        int nParamCount = 0;
        ArrayList oBindSrc = null;

        oBindSrc = (ArrayList) ((QueryHouse) (queryframe.getSource()).get(Integer.toString(iSQLIdx))).getOBindSrc();
        if (oQueryHouseNow.isFirstNode() == false){
        	oQueryHouseParent = (QueryHouse) (queryframe.getSource()).get(Integer.toString(nParentSQLIdx));
        	nParentQueryType = oQueryHouseParent.getQueryType();
        }

    	prepareQuery( nDBIdx, nQueryType, SQLStmt, ptmt, iSQLIdx);

    	if(isStopStatus())
		{
			log.debug("RQGetDataHierarchy fetch stop 1");
			return ;
		}

        if(oBindSrc != null ){
        	bindColumn(oBindSrc, nQueryType, oQueryHouseNow, ptmt, iSQLIdx);
        }

        int nStmtSQLIdx = 0;
        if ( nQueryType == QueryHouse.RQ_QUERY_PROCCURSOR ) // 커서의 경우 부모 Stetement(프로시저) 에서 resultset을 생성
        	nStmtSQLIdx = nParentSQLIdx;
        else
        	nStmtSQLIdx = iSQLIdx;
        executeQuery( nQueryType, ptmt, rs, rsMd, iSQLIdx, nStmtSQLIdx, nParamIndex );

        boolean flagFirstIs = true;
        CallableStatement cStmt = null;

        if ( nQueryType == QueryHouse.RQ_QUERY_PROCPARAM ) // 파라미터의 경우 부모 Statement(프로시저)에서 데이터 가져온다
        {
        	cStmt = (CallableStatement)ptmt[nParentSQLIdx];
        	nParamCount = oQueryHouseNow.getParamCount();
        }

        boolean bHaveChild = false;
        boolean bFirstLoop = true;
        bHaveChild = ( oQueryHouseNow.getIChildNodeCnt() > 0 ) ? true : false; // 주 쿼리 결과가 없어도 종 쿼리가 돌기 위하여 추가
        Timer t2 = new Timer();
        log.info("SQLIdx " + oQueryHouseNow.getSQLIdx() + ", [APPEND START]");
        do{
        	if ( nQueryType != QueryHouse.RQ_QUERY_PROCPARAM && bFirstLoop )
        	{
            	if ( rs[iSQLIdx] == null )
            	{
            		if ( bHaveChild == false )
            			break;
            	}
            	else
            	{
            		if ( rs[iSQLIdx].next() == false){ //rs[iSQLIdx].next() == false && bHaveChild == false 을 수정
                		strBf.append(SSEP);
           				//strBf.append(iSQLIdx);  //기존 SQLIdx 를 SQLStmt 순서로 대체
           				strBf.append(iSQLOrder);
           				strBf.append(ROW_SEP);

           				if( oQueryHouseNow.getIRowCntNow() == 0 && oQueryHouseNow.getHIchkflag() == false ){
                            strBf.append(RQFetchExecute.shHeadInfo(con, rsMd[iSQLIdx], nQueryType));  //add 2007.02.22 show Header info
                            strBf.append(ROW_SEP);
                            // head info 는 각 쿼리에서 한번만 찍기 위해 check flag를 queryhouse 안에 선언.
                            oQueryHouseNow.setHIchkflag(true); // hichkflag (header info check flag)
                        }
           				break;
            		}
            	}
        	}
            //if(rs[iSQLIdx].isFirst() || iSQLIdx+1 <  iSQLCnt){
            //if(rs[iSQLIdx].isFirst() || strLastNodeIs.equals("false")){
        	if(flagFirstIs == true){ //modified by ljg 2007.01.25

        		strBf.append(SSEP);
   				//strBf.append(iSQLIdx);  //기존 SQLIdx 를 SQLStmt 순서로 대체
   				strBf.append(iSQLOrder);
                strBf.append(ROW_SEP);

                if( oQueryHouseNow.getIRowCntNow() == 0 && oQueryHouseNow.getHIchkflag() == false){
                    strBf.append(RQFetchExecute.shHeadInfo(con, rsMd[iSQLIdx], nQueryType));  //add 2007.02.22 show Header info
                    strBf.append(ROW_SEP);
                    oQueryHouseNow.setHIchkflag(true); // hichkflag (header info check flag)
                }

            }else if(strLastNodeIs.equals("false")){
                strBf.append(SSEP);
                //strBf.append(iSQLIdx);  //기존 SQLIdx 를 SQLStmt 순서로 대체
                strBf.append(iSQLOrder);
                strBf.append(ROW_SEP);
            }

        	flagFirstIs = false;

            //if(iSQLIdx !=0 ){
            if(!oQueryHouseNow.getFirstNodeIs().equals("true")){
                //strBf.append(rs[iSQLIdx-1].getRow());
                int iparentSQLIdx = oQueryHouseNow.getIParentSQLIdx();
                strBf.append(
                        //((QueryHouse) queryframe.getSource().get(Integer.toString(iSQLIdx-1))). getIRowCntNow()
                        ((QueryHouse) queryframe.getSource().get( Integer.toString(iparentSQLIdx))  ). getIRowCntNow()
                );

            }else{
            	//주쿼리일경우 iSQLIdx의 값과 상관없이 상속키 "0" 으로 셋팅 --> Spec. 상 정의됨
            	//자기쿼리를 넣을경우 밑의 주석으로 전환
       			strBf.append("0");
       			//strBf.append(iSQLIdx);
            }

            strBf.append(COL_SEP);

            strBf.append(oQueryHouseNow.getIRowCntNow() + 1);
                oQueryHouseNow.setIRowCntNow(
                        oQueryHouseNow.getIRowCntNow() + 1
            );

            int nCol = 0;
            if ( nQueryType == QueryHouse.RQ_QUERY_PROCPARAM )
            	nCol = nParamCount;
            else
            	nCol = rsMd[iSQLIdx] != null ? rsMd[iSQLIdx].getColumnCount() : 0;
            if ( bFirstLoop && oQueryHouseNow.getBindDataArr() == null )
            {
            	oQueryHouseNow.allocBindData(nCol);
            }
            String[] strBindData = oQueryHouseNow.getBindDataArr();
            strBf.append(COL_SEP);
            strBf.append(RQFetchExecute.objectToString(rsMd[iSQLIdx],   
				            						   rs[iSQLIdx], 
				            						   nCol, 
				            						   strBindData,
				            						   nQueryType, 
				            						   cStmt, 
				            						   nParamInOut, 
				            						   nParamIndex, 
				            						   oRQHBindingSource, 
				            						   oQueryHouseNow, 
				            						   (QueryHouse)queryframe.getSource().get(""+nParentSQLIdx), 
				            						   bExecutor));
            ///////////////// RQDoc Statistic //////////////////
            totalrowcnt++;
            ///////////////////////////////////////////////////
            strBf.append(ROW_SEP);

            //if(iSQLIdx+1 <  iSQLCnt){
            if(strLastNodeIs.equals("false")){

                for(int i=0;i<oQueryHouseNow.getIChildNodeCnt();i++){

                    fetch(Integer.parseInt(oQueryHouseNow.getOChildSQLIdxArr().get(i).toString()), oRQHBindingSource);
                    bHaveChild = false;

                }

            }

            if(isStopStatus())
    		{
    			log.debug("RQGetDataHierarchy fetch stop 33333");
    			return ;
    		}
            bFirstLoop = false;

            if ( rs[iSQLIdx] == null )
            	break;

        }  while(rs[iSQLIdx].next());
        log.info("SQLIdx " + oQueryHouseNow.getSQLIdx() + ", [APPEND END]");
        // finally ?
//            if(!rs[iSQLIdx].next()){
            //System.out.println("iSQLIdx : " + iSQLIdx + " fetch end !!!");
    	try {
			result = strBf.toString();
            if ( rs[iSQLIdx]!= null )
                rs[iSQLIdx].close();
            if ( ptmt[iSQLIdx] != null )
                ptmt[iSQLIdx].close();
//	            }
		} catch (SQLException e) {
			RequbeUtil.do_PrintStackTrace(log, e);
			this.exception_status = -1;
		} finally {
			///////////////// RQDoc Statistic //////////////////
			RQDocStatGetRC lm_DocStatGetRC = RQDocStatGetRC.getInstance();
			lm_DocStatGetRC.setRCinHM(Thread.currentThread().getName(), ""+totalrowcnt);
		}
    }

    public void prepareQuery(int nDBIdx, int nQueryType, String strSQL, PreparedStatement[] pStmt, int iSQLIdx)throws SQLException
	{
		con = (Connection)connInfoRep.get(""+nDBIdx);
		if ( nQueryType == QueryHouse.RQ_QUERY_PROC )
			pStmt[iSQLIdx] = con.prepareCall(strSQL);
		else if ( nQueryType == QueryHouse.RQ_QUERY_SQL )
			pStmt[iSQLIdx] = con.prepareStatement(strSQL);
	}

    /**
     * 쿼리를 실행한다. 실행시 에러가 발생하면 해당 쿼리 내용과 에러 STACKTRACE를 로그로 남긴다.
     * @param nQueryType
     * @param pStmt
     * @param rsSet
     * @param rsSetMD
     * @param iSQLIdx
     * @param nStmtIdx
     * @param nParamIndex
     */
    public void executeQuery(int nQueryType, PreparedStatement[] pStmt, ResultSet[] rsSet, ResultSetMetaData[] rsSetMD, int iSQLIdx, int nStmtIdx, int nParamIndex) throws SQLException
	{
    	if ( nQueryType == QueryHouse.RQ_QUERY_SQL )
    	{
            //Environment env = Environment.getInstance();
            //Environment.load();
    		//int fetchsize = 0;
    		//if(env.RS_fetchSize != null){
    		//	int fetchsize = env.RS_fetchSize;
    		//}

			//Timer t1 = new Timer();
			//System.out.println("+++++++++++++++++++ pStmt.execute+++++++++++++++ : " + t1.start("t1") );
			//pStmt[iSQLIdx].setFetchSize(fetchsize);
			pStmt[iSQLIdx].execute();

	    	rsSet[iSQLIdx] = pStmt[iSQLIdx].getResultSet();
	    	//System.out.println("+++++++++++++++++++after Get Rs ++++++++++ : " + " From pStmt to Get Rs " + t1.end("t1"));
	    	rsSetMD[iSQLIdx] = rsSet[iSQLIdx].getMetaData();
    	}
    	else if ( nQueryType == QueryHouse.RQ_QUERY_PROC )
    	{
    		pStmt[iSQLIdx].execute();
    	}
    	else if ( nQueryType == QueryHouse.RQ_QUERY_PROCCURSOR )
    	{
    		rsSet[iSQLIdx] = (ResultSet)((CallableStatement)pStmt[nStmtIdx]).getObject(nParamIndex);
    		rsSetMD[iSQLIdx] = rsSet[iSQLIdx].getMetaData();
    	}
	}

    public void bindColumn(ArrayList oBindSrc, int nQueryType, QueryHouse oQueryHouseNow, PreparedStatement[] pStmt, int iSQLIdx) throws SQLException
    {
        Iterator itOBindSrc = oBindSrc.iterator();
        ArrayList bindsrcArr;
        int bindQryIdx = 0;
        int bindColIdx = 0;
    	QueryHouse oQueryHouseParent = null;
    	int nParentQueryType = QueryHouse.RQ_QUERY_UNKNOWN;
    	int nParentSQLIdx = oQueryHouseNow.getIParentSQLIdx();	//queryhouse 의 부모 쿼리 index
    	String strBindData;

        if (oQueryHouseNow.isFirstNode() == false){
        	oQueryHouseParent = (QueryHouse) (queryframe.getSource()).get(Integer.toString(nParentSQLIdx)); // 바인딩할 부모 쿼리
        	nParentQueryType = oQueryHouseParent.getQueryType();
        }

        for(int i = 1; i <= oBindSrc.size(); i++){
            bindsrcArr =  (ArrayList)itOBindSrc.next();
            if ( nQueryType == QueryHouse.RQ_QUERY_PROC )
            {
            	int nParamIndex = Integer.parseInt( (bindsrcArr.get(0)).toString() );
            	int nInOut		= Integer.parseInt( (bindsrcArr.get(1)).toString() );
            	int bIsCursor	= Integer.parseInt( (bindsrcArr.get(2)).toString() );
            	String strParam	= (bindsrcArr.get(3)) == null ? "" : (bindsrcArr.get(3)).toString();
            	bindQryIdx = (bindsrcArr.get(4)) == null ? 0 : Integer.parseInt( (bindsrcArr.get(4)).toString() );
            	bindColIdx = (bindsrcArr.get(5)) == null ? 0 : Integer.parseInt( (bindsrcArr.get(5)).toString() );
    			//paramInfo = (ColumnInfo)paramList.get(i);
    			if ( bIsCursor == 1 ) // cursor Type
    			{
    				((CallableStatement)pStmt[iSQLIdx]).registerOutParameter(i, -10 /*OracleTypes.CURSOR*/);
    			}
    			else
    			{
    				switch ( nInOut )
    				{
    					case DatabaseMetaData.procedureColumnIn :
    	    				if ( nParentQueryType == QueryHouse.RQ_QUERY_SQL ) // 주 쿼리가 SQL일 경우
    	    				{
    	    					oQueryHouseParent = (QueryHouse) (queryframe.getSource()).get(Integer.toString(bindQryIdx)); // 바인딩할 부모 쿼리
    	    			        if (oQueryHouseParent !=null)
    	    			        	strBindData = oQueryHouseParent.getBindData(bindColIdx);
    	    			        else
    	    			        	strBindData = "";
    	    			        ((CallableStatement)pStmt[iSQLIdx]).setString( i, strBindData );
    	    					//((CallableStatement)pStmt[iSQLIdx]).setString( i, rs[bindQryIdx].getString(bindColIdx+1) );
    	    				}
    	    				else
    	    					((CallableStatement)pStmt[iSQLIdx]).setString( i, strParam );
    						break;
    					case DatabaseMetaData.procedureColumnOut :
    					case DatabaseMetaData.procedureColumnReturn :
    						((CallableStatement)pStmt[iSQLIdx]).registerOutParameter(i, Types.VARCHAR);
    						break;
    					case DatabaseMetaData.procedureColumnInOut :
    						if ( nParentQueryType == QueryHouse.RQ_QUERY_SQL ) // 주 쿼리가 SQL일 경우
    						{
    	    					oQueryHouseParent = (QueryHouse) (queryframe.getSource()).get(Integer.toString(bindQryIdx)); // 바인딩할 부모 쿼리
    	    			        if (oQueryHouseParent !=null)
    	    			        	strBindData = oQueryHouseParent.getBindData(bindColIdx);
    	    			        else
    	    			        	strBindData = "";
    	    			        ((CallableStatement)pStmt[iSQLIdx]).setString( i, strBindData );
    							//((CallableStatement)pStmt[iSQLIdx]).setString( i, rs[bindQryIdx].getString(bindColIdx+1) );
    						}
    						else
    							((CallableStatement)pStmt[iSQLIdx]).setString( i, strParam );
    						((CallableStatement)pStmt[iSQLIdx]).registerOutParameter(i, Types.VARCHAR);
    						break;
    				}
    			}
            }
            else
            {
				bindQryIdx = Integer.parseInt((bindsrcArr.get(0)).toString()); //BindQryIdx@BindSrc(0)
				bindColIdx = Integer.parseInt((bindsrcArr.get(1)).toString()); //bindColIdx@BindSrc(0)
				//ptmt[iSQLIdx].setString(i, rs[bindQryIdx].getString(bindColIdx));
		        if (oQueryHouseNow.isFirstNode() == false){
		        	oQueryHouseParent = (QueryHouse) (queryframe.getSource()).get(Integer.toString(bindQryIdx)); // 바인딩할 부모 쿼리
		        	nParentQueryType = oQueryHouseParent.getQueryType();
		        }
		        if (oQueryHouseParent !=null)
		        	strBindData = oQueryHouseParent.getBindData(bindColIdx);
		        else
		        	strBindData = "";
		        	pStmt[iSQLIdx].setString(i, strBindData);
		        	//pStmt[iSQLIdx].setString(i, rs[bindQryIdx].getString(bindColIdx+1)); //modified by ljg 2007.01.29
            }

            if(isStopStatus())
    		{
    			log.debug("RQGetDataHierarchy fetch stop 22222");
    			return ;
    		}
        }
    }


    public void close() throws SQLException{
    	String lm_driverName = "";
    	if(con != null && !con.isClosed()){
    		DatabaseMetaData  dbMeta = con.getMetaData();
    		lm_driverName = dbMeta.getDriverName();
    	}

    	if (bExecutor)
    	{
    		ConnectionInfo connInfo;
    		Connection c;
    		int nDBIdx;
    		for ( int i=0; i<connArr.size(); i++ )
    		{
    			connInfo = (ConnectionInfo)connArr.get(i);
    			nDBIdx = connInfo.connidx;
    			c = (Connection)connInfoRep.get(""+nDBIdx);
        		if (c == null)
        			return;
        		c.close();
        		log.info("Connect [END], " + c + ", (from Hashtable-Executor)");
        		connInfoRep.remove(""+nDBIdx);
    		}
    	}
    	else
    	{
    		if (con == null){
	    		int nDBIdx;
	    		Enumeration keys = connInfoRep.keys();

	    		while ( keys.hasMoreElements() )
	    		{
	    			Connection c = (Connection)connInfoRep.get(keys.nextElement());

	        		if (c != null && !c.isClosed())
	        		{
	        			c.close();
	        			log.info("Connect [END], " + c + ", (from Hashtable-Web case1)");
	        		}
	        	}
	    		connInfoRep.clear();
	    		return;
    		}else{

    			con.close();
    			log.info("Connect [END], " + con + ", (made by prepareQuery)");

	    		Enumeration keys = connInfoRep.keys();
	    		while ( keys.hasMoreElements() )
	    		{
	    			Connection c = (Connection)connInfoRep.get(keys.nextElement());

	        		if (c != null && !c.isClosed())
	        		{
	        			log.info("Connect [END], " + c + ", (from Hashtable-Web case2)");
	        			c.close();
	        		}
	        	}
	    		connInfoRep.clear();
    		}
    	}
    	for(int i = 0 ; i < iSQLmax; i++){
    		if (rs[i]!= null && lm_driverName.indexOf("JDBC-ODBC") == -1){
    			rs[i].close();
    		}
            if (ptmt[i] != null){
            	ptmt[i].close();
            }

    	}

    }

    /**
     * Method : Connection connect
     * @param
     * @return void
     */
    public void connect(String p_strJndiname){

    }

    /**
     * Method : getRQResult
     * @param
     * @return String
     */
    public String getRQResult() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NamingException{
    	log.info("Get ResultSet, [START]");
    	
    	RQMakeSQLHierarchy oRQMakeSQLHierarchy = new RQMakeSQLHierarchy(strJndiname, flagIs, strXml, strDB, bExecutor);
    	oRQMakeSQLHierarchy.makeSQLHierarchy();
    	//this.connArr = oRQMakeSQLHierarchy.getElementsDBInfo(null);
    	if(bExecutor)
    	{
    		System.out.println("strDB : " + strDB);
    		System.out.println("strXml : " + strXml);
    	}

    	if(strDB != null && !strDB.equals("")){

            RQQueryParse oRQQueryParse = new RQQueryParse();
            Document doc;
    		try {
    			doc = oRQQueryParse.parseXML(strDB);
    	        List elements = doc.getRootElement().getChildren();
    	        this.connArr = oRQMakeSQLHierarchy.getElementsDBInfo(elements);
    	    	//this.connArr = oRQMakeSQLHierarchy.getElementsDBInfo(strDB);
    		} catch (JDOMException e1) {
    			RequbeUtil.do_PrintStackTrace(log, e1);
    		} catch (IOException e1) {
    			RequbeUtil.do_PrintStackTrace(log, e1);
    		}
    	}

    	setQueryframe(oRQMakeSQLHierarchy.getQueryframe());

    	//sqlstmt 의 갯수만큼 각 객체를 만든다.(0,1,2...와같은 순차적일경우)
    	//iSQLCnt = queryframe.getISQLCnt();
    	//ptmt = new PreparedStatement[iSQLCnt];
    	//rs = new ResultSet[iSQLCnt];
    	//rsMd = new ResultSetMetaData[iSQLCnt];

    	//sqlstmt id 의 가장 높은 숫자로 각 객체를 만든다.(1,3,5 와 같이 비순차적일경우)
    	//close() 실행시 만들어진 모든 객체는 null 시킨다.
    	iSQLmax = getMaxiSQLidx();
    	ptmt = new PreparedStatement[iSQLmax];
    	rs = new ResultSet[iSQLmax];
    	rsMd = new ResultSetMetaData[iSQLmax];

		RQCacheContent rqContent = null;
		
		//BLOB, CLOB 만 담아서 결과셋 맨뒤에 붙이기 위한 객체
		RQHierarchyBindingSource oRQHBindingSource = new RQHierarchyBindingSource();
		//RQBindingBytesSource obindingBytesSource = RQBindingBytesSource.getInstance();

		rqContent = new RQCacheContent();
		try{
			if(con == null) connect();

			if(isStopStatus())
			{
				log.debug("RQGetDataHierarchy getRQResult stop 1");
				rqContent.setStopStatus();
				rqContent.setReturnStrObject(result);
				setRQCacheContent(rqContent);
				return result;
			}

	        String[] rootSQLidxs = queryframe.getRootSQLIdx();
	        HashMap hM = queryframe.getSource();

	        ///////////////////////////////////////// fetch start ///////////////////////////////////////////////////
			log.info("Fetch [START]");
	        for(int i = 0 ; i < rootSQLidxs.length ; i++){
	        	QueryHouse lm_QueryHouse = (QueryHouse) hM.get( (String)rootSQLidxs[i] );
	        	// QueryHouse.RQ_QUERY_RFCFUNC
	        	if(lm_QueryHouse.getQueryType() == QueryHouse.RQ_QUERY_RFCFUNC){

	        		log.debug("RFCFUNC.");
	        		StringBuffer functionname = null, importvalue = null, tablevalue = null;
	        		RFCFuncHandler oRFCFuncHandler = new RFCFuncHandler();
	        		oRFCFuncHandler.querySplit(functionname, importvalue, tablevalue, queryframe, lm_QueryHouse);

	        		log.debug("RFCFUNC. end");

	        	}else{
	        		// QueryHouse.RQ_QUERY_SQL and QueryHouse.RQ_QUERY_PROC
	        		fetch(lm_QueryHouse.getSQLIdx(), oRQHBindingSource);
	        	//	result = "OK";
	        	}
	        }
	        log.info("Fetch [END]");
	        ////////////////////////////////////////////  fetch end ///////////////////////////////////////////////////
	        
	        if(oRQHBindingSource.getStfResult() != null ){
				if(!oRQHBindingSource.getStfResult().toString().equals("")){
				    result += oRQHBindingSource.getStfBlob().toString();
				}
	        }
	        //L.debug("BindingSource : " + oRQHBindingSource.getStfResult());

	        if(isStopStatus())
			{
				log.debug("RQGetDataHierarchy getRQResult stop 2");
				rqContent.setStopStatus();
				rqContent.setReturnStrObject(result);
				setRQCacheContent(rqContent);
				return result;
			}

	        rqContent.setReturnStrObject(result);
			setRQCacheContent(rqContent);

		}finally{
			try {
				close();
				oRQHBindingSource.setStfBlobEmpty();	
			} catch (SQLException e) {
				RequbeUtil.do_PrintStackTrace(log, e);
			}
		}
		/////////////// dataset file write for test ///////////////////
		/*
		FileWriter fw = null;
		try{
			fw = new FileWriter("C:/ds2.txt");
			fw.write(result);
			fw.close();
		}catch(IOException e){}
		*/
		//////////////////////////////////////////////////////////////
		log.info("Get ResultSet, [END]");
        return result;
    }

    protected void finalize() throws Throwable, IOException {
    	close();
    }

    private int getMaxiSQLidx(){
    	int max = 0;
    	HashMap lm_map = queryframe.getSource();
    	Set keyset = lm_map.keySet();
    	Iterator it_key = keyset.iterator();
    	QueryHouse lm_qh = null;
    	while(it_key.hasNext()){
    		lm_qh = (QueryHouse) lm_map.get((String)it_key.next());
    		if(max < lm_qh.getSQLIdx()){
    			max = lm_qh.getSQLIdx();
    		}

    	}
    	return max + 1; // 0 부터 시작하므로 갯수는 +1 해준다.
    }
}
