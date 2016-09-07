package com.sds.rqreport.service.queryexecute;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.util.*;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.querynode.*;
import com.sds.rqreport.service.*;
import com.sds.rqreport.util.*;


public class RQMakeSQLHierarchy {

    private String strJndiname;
    private boolean flagIs;
    private String strDB;
    private String strXml;
    private boolean bExecutor; // Executor 에서 직접 실행여부
    private QueryFrame queryframe = new QueryFrame();
    Environment g_env = Environment.getInstance();
    
    String  strResultSet = "";
    int iSQLCnt = 0;
    int iSQLOrder = 0;
    
    private Logger log = Logger.getLogger("RQQRYEXE");
    
    public RQMakeSQLHierarchy(){} //direct connect 일경우 생성자 함수 추후에 만들어야 함
    
    public RQMakeSQLHierarchy(String strXml){
    	this.strXml = strXml;
    }
	/**
	 * Method : 생성자 
	 * @param strXml	: (IN) Query 정보 XML
	 * @param strDBInfo	: (IN) DBInfo XML
	 * @param bExecutor	: (IN) Executor에서 직접 실행이면 true, 서버에서 실행이면 false
	 */
    public RQMakeSQLHierarchy(String strJndiname, boolean flagIs, String strXml, String strDBInfo, boolean bExecutor){
        this.strJndiname = strJndiname;
        this.flagIs = flagIs;
        this.strDB  = strDBInfo;
        this.strXml = strXml;
        this.bExecutor = bExecutor;
    }
    
    public QueryFrame getQueryframe(){
    	return queryframe;
    }
    
    protected String decryptEncValue(String str)
    {
    	
    	if(str != null && str.startsWith("*??*"))
    	{
    		Decrypter dec = new Decrypter("RQREPORT6**??");
    		return dec.decrypt(str.substring(4));
    		
    	}
    	else
    		return str;
    }
    
    protected void getElementsRef(List elements){
        Iterator it = elements.iterator();
        while(it.hasNext()){

            Element element = (Element) it.next();
            // "SQLStmt" element
            if(element.getName().equals("SQLStmt")){
            	
            	String strUIType = element.getAttributeValue("UIType") != null ? element.getAttributeValue("UIType").trim():"";
            	if(strUIType.equals("5") || strUIType.equals("6")) continue;
                //String strSQLStmt = element.getTextTrim();
                String strDBIdx = element.getAttributeValue("DBIdx").trim();
                String strSQLIdx = element.getAttributeValue("SQLIdx").trim();
                String strQueryType = element.getAttributeValue("QryType") != null ? element.getAttributeValue("QryType").trim():"";
                int	   nQueryType = Integer.parseInt(strQueryType);
                
                //iSQLCnt = Integer.parseInt(strSQLIdx)+1; //iSQLCnt 는 RQGetDataHierarchy 에서 ptmt, rs를 갯수만큼 만들때 쓰인다.
                ++iSQLCnt; //strSQLIdx 가 1부터 시작하는것이 있으므로 SQLStmt 갯수로 Count  
                 
                Element sqldata = element.getChild("SQLData");
                String strSQLStmt = decryptEncValue(sqldata.getText());
                
                // Stored Procedure
                String strSPName = "";
                int nSPType = DatabaseMetaData.procedureResultUnknown;
                int nParamCount = 0;
                // SP Parameter
                int nParamIndex = 0;
                int nInOutType = DatabaseMetaData.procedureColumnUnknown;
                if ( nQueryType == QueryHouse.RQ_QUERY_PROC 		||
                	 nQueryType == QueryHouse.RQ_QUERY_PROCPARAM 	||
					 nQueryType == QueryHouse.RQ_QUERY_PROCCURSOR )
                {
                	strSPName = element.getAttributeValue("SPName");
                	String strSPType = element.getAttributeValue("SPType").trim();
                	String strParamCnt = element.getAttributeValue("ParamCnt").trim();
                	nSPType = Integer.parseInt(strSPType);
                	nParamCount = Integer.parseInt(strParamCnt);
                	String strParamIdx = element.getAttributeValue("ParamIndex");
                	String strInOut = element.getAttributeValue("InOutType");
                	if ( strSPName != null )
                		strSPName = strSPName.trim();
                	if ( strParamIdx != null )
                		nParamIndex = Integer.parseInt(strParamIdx.trim());
                	if ( strInOut != null )
                		nInOutType = Integer.parseInt(strInOut.trim());
                }
                
                List bindelements = element.getChildren("BindSrc");
                ArrayList oBindSrcArr;
                if ( nQueryType == QueryHouse.RQ_QUERY_PROC )
                	oBindSrcArr = new ArrayList(nParamCount);
                else
                	oBindSrcArr = new ArrayList();
                int index = 0;
                for(Iterator bit = bindelements.iterator(); bit.hasNext();){
                    
                    ArrayList oBindSrcArrElement = new ArrayList();
                    
                    // "BindSrc" element
                    Element bindelement = (Element) bit.next();
                    if ( nQueryType == QueryHouse.RQ_QUERY_PROC ) {
	                    String strParamIdx = bindelement.getAttributeValue("ParamIndex");
	                    String strInOut = bindelement.getAttributeValue("InOutType");
	                    String strIsCursor = bindelement.getAttributeValue("IsCursor");
	                    String strDataSrc = bindelement.getAttributeValue("DataSrc");
	                    String strBindQryIdx = bindelement.getAttributeValue("BindQryIdx");
	                    String strBindColIdx = bindelement.getAttributeValue("BindColIdx");
	                    String strParamName = bindelement.getAttributeValue("ParamName");
	                    oBindSrcArrElement.add(strParamIdx);
	                    oBindSrcArrElement.add(strInOut);
	                    oBindSrcArrElement.add(strIsCursor);                   	
	                    oBindSrcArrElement.add(strDataSrc);
	                    oBindSrcArrElement.add(strBindQryIdx);
	                    oBindSrcArrElement.add(strBindColIdx);
	                    oBindSrcArrElement.add(strParamName);
	                    
	                // RFC FUNC.
                    }else if ( nQueryType == QueryHouse.RQ_QUERY_RFCFUNC || nQueryType == QueryHouse.RQ_QUERY_JCOPARAM ){
                    	String bindJCOPrmNm = bindelement.getAttributeValue("ParamName") != null ? bindelement.getAttributeValue("ParamName") : "";
                    	String FieldName = bindelement.getAttributeValue("FieldName") != null ? bindelement.getAttributeValue("FieldName") : "";
                    	String Value = bindelement.getText(); // CDATA 는 값이 없을경우 "" 반환
                    	oBindSrcArrElement.add(bindJCOPrmNm);
                    	oBindSrcArrElement.add(FieldName);
                    	oBindSrcArrElement.add(Value);
                    	
                    }else{
	                    String strBindQryIdx = bindelement.getAttributeValue("BindQryIdx");
	                    String strBindColIdx = bindelement.getAttributeValue("BindColIdx");
	                    String BindColName = bindelement.getAttributeValue("BindColName");
	                    oBindSrcArrElement.add(strBindQryIdx);
	                    oBindSrcArrElement.add(strBindColIdx);
	                    oBindSrcArrElement.add(BindColName);
                    }
                    
                    oBindSrcArr.add(oBindSrcArrElement);
                    index++;
                }
                QueryHouse queryhouse = new QueryHouse();
                if(g_env.rqreport_query_comment_delete.equalsIgnoreCase("yes")){
                	// 쿼리주석 제거 (쿼리에 주석이 있을경우 JDBC 드라이버 종류에 따라 실행이 안되는경우가 있음)
                	strSQLStmt = new RequbeUtil().eliminateComment(strSQLStmt);	
                }
                queryhouse.setSQLStmt(strSQLStmt);
                queryhouse.setDBIdx(Integer.parseInt(strDBIdx));
                queryhouse.setSQLIdx(Integer.parseInt(strSQLIdx));
                queryhouse.setSQLOrder(iSQLOrder);
                iSQLOrder++;
                queryhouse.setQueryType(nQueryType);
                queryhouse.setSPName(strSPName);
                queryhouse.setSPType(nSPType);
                queryhouse.setParamCount(nParamCount);
                queryhouse.setParamIndex(nParamIndex);
                queryhouse.setInOutType(nInOutType);
                
                queryhouse.setOBindSrc(oBindSrcArr);
                
                // RFC FUNC.
                if ( nQueryType == QueryHouse.RQ_QUERY_RFCFUNC 
                		|| nQueryType == QueryHouse.RQ_QUERY_JCOPARAM ){
                	// RQ_QUERY_RFCFUNC
                	String JCOFncNm = element.getAttributeValue("JCOFncNm");	
                	String JCOFncGrp = element.getAttributeValue("JCOFncGrp");
                	String JCOFuncDscpt = element.getAttributeValue("JCOFuncDscpt");
                	// RQ_QUERY_JCOPARAM
                	String JCOPrmClss = element.getAttributeValue("JCOPrmClss");			// E, I, T 
                	String JCOPrmNm = element.getAttributeValue("JCOPrmNm"); 
                	String JCOPrmTbl = element.getAttributeValue("JCOPrmTbl");
                	String JCOPrmFld = element.getAttributeValue("JCOPrmFld");
                	String JCOPrmABAPTp = element.getAttributeValue("JCOPrmABAPTp");	// C
                	String JCOPrmOpt = element.getAttributeValue("JCOPrmOpt");				// X
                	String JCOPrmDscpt = element.getAttributeValue("JCOPrmDscpt");
                	String JCOPrmTp = element.getAttributeValue("JCOPrmTp");					// CHAR, STRUCTURE, TABLE
                	String RunVarType = element.getAttributeValue("RunVarType");				// TABLE 시 Ruvar사용 여부 0 이면 없음 1이면 사용 
                	String JCOPrmValueSize = element.getAttributeValue("JCOPrmValueSize");
                	queryhouse.setJCOFncNm(JCOFncNm);
                    queryhouse.setJCOFncGrp(JCOFncGrp);
                    queryhouse.setJCOFuncDscpt(JCOFuncDscpt);
                    queryhouse.setJCOPrmClss(JCOPrmClss);
                    queryhouse.setJCOPrmNm(JCOPrmNm);
                    queryhouse.setJCOPrmTbl(JCOPrmTbl);
                    queryhouse.setJCOPrmFld(JCOPrmFld);
                    queryhouse.setJCOPrmABAPTp(JCOPrmABAPTp);
                    queryhouse.setJCOPrmOpt(JCOPrmOpt);
                    queryhouse.setJCOPrmDscpt(JCOPrmDscpt);
                    queryhouse.setJCOPrmTp(JCOPrmTp);
                    queryhouse.setRunVarType(RunVarType);
                    queryhouse.setJCOPrmValueSize(JCOPrmValueSize);
                }
                // RFC FUNC. end 

                //if(!strSQLIdx.equals("0")){
                //if(!strSQLIdx.equals("0") && !element.getParentElement().getName().equals("SQL")){ //부모쿼리의 SQLIdx를 찾아서 셋팅한다.
                if(!element.getParentElement().getName().equals("SQL")){ //부모쿼리의 SQLIdx를 찾아서 셋팅한다. //자식쿼리 SQLIdx 가 0 일수도 있다.
                    queryhouse.setIParentSQLIdx(Integer.parseInt(element.getParentElement().getAttributeValue("SQLIdx")) ); 
                }else{
                    //queryhouse.setIParentSQLIdx(0);
                	queryhouse.setIParentSQLIdx(Integer.parseInt(strSQLIdx)); //주쿼리 일경우 자기자신의 SQLIdx를 셋팅 0일경우 0을 1일경우 1을 
                }
                
                if(element.getParentElement().getName().equals("SQL")){ //check FirstSQL(rooSQL) node 
                	queryhouse.setFirstNodeIs("true");
                	queryframe.setRootSQLIdx(strSQLIdx);   //queryframe 에 rootSQL 의 idx를 배열로 가지고 있도록 한다.                 
                }else{
                	queryhouse.setFirstNodeIs("false");
                }
                
                if(element.getChild("SQLStmt") != null){ //check child SQLStmt node 
                    queryhouse.setLastNodeIs("false");
                    queryhouse.setIChildNodeCnt(element.getChildren("SQLStmt").size());
                    
                    ArrayList lm_arr = new ArrayList();
                    List lm_elements = element.getChildren("SQLStmt");
                    Iterator lm_it = lm_elements.iterator();
                    
                    while(lm_it.hasNext()){
                        Element lm_element = (Element) lm_it.next();
                        String lm_strSQLIdx = lm_element.getAttributeValue("SQLIdx").trim();
                        lm_arr.add(lm_strSQLIdx);
                    }
                    queryhouse.setOChildSQLIdxArr(lm_arr);
                    
                    
                }else{
                    queryhouse.setLastNodeIs("true");
                }
                
                //RQGetDataImpl oRQGetDataImpl = new RQGetDataHierarchy(strJndiname, strSQLStmt);
                //strResultSet = oRQGetDataImpl.getRQResult();
                queryframe.setISQLCnt(iSQLCnt);
                queryframe.addToframe(strSQLIdx, queryhouse);
            }//end of if(element.getName().equals("SQLStmt"))

            if(element.getChildren("SQLStmt") != null){
                getElementsRef(element.getChildren("SQLStmt"));
            }
            
        }//end of while
    
    }//end of getElementsRef
    
	/**
	 * Method : DB 정보 XML Parsing 
	 */    
    protected ArrayList getElementsDBInfo(List elements){
        Iterator it = elements.iterator();
        ArrayList connArr = new ArrayList(20);
        while(it.hasNext())
        {
            Element element = (Element) it.next();
            if(element.getName().equals("DBInfo"))
            {
                String strDBIdx = element.getAttributeValue("DBIdx").trim(); //"0";
                String strDBID = decryptEncValue(element.getAttributeValue("DBID")).trim(); //"jdbc:oracle:thin:@dbserver2:1521:WorldAV";
                String strDriver = decryptEncValue(element.getAttributeValue("Driver")).trim(); //"oracle.jdbc.driver.OracleDriver";
                String strConnStr = decryptEncValue(element.getAttributeValue("ConnectionStr")).trim(); //"jdbc:oracle:thin:@dbserver2:1521:WorldAV";
                String strUserID = decryptEncValue(element.getAttributeValue("UserID")); //"";
                String strPassword = decryptEncValue(element.getAttributeValue("Password")); //"";
                
                //System.out.println("strDBIdx-"+strDBIdx+"-");
                //System.out.println("strDBID-"+strDBID+"-");
                //System.out.println("strDriver-"+strDriver+"-");
                //System.out.println("strConnStr-"+strConnStr+"-");
                //System.out.println("strUserID-"+strUserID+"-");
                //System.out.println("strPassword-"+strPassword+"-");
                
                ConnectionInfo connInfo = new ConnectionInfo();
                connInfo.connidx = Integer.parseInt(strDBIdx);
                connInfo.connid = strDBID;
                connInfo.driver = strDriver;
                connInfo.connection = strConnStr;
                connInfo.id = strUserID == null? "" : strUserID.trim();
                connInfo.pw = strPassword == null? "" : strPassword.trim();
                connArr.add(connInfo);
            }
        }
        return connArr;
    }
    
    public String makeSQLHierarchy(){
        
        try {
            RQQueryParse oRQQueryParse = new RQQueryParse();
            Document doc = oRQQueryParse.parseXML(strXml);
            List elements = doc.getRootElement().getChildren();
            getElementsRef(elements);
//            getElementsDBInfo(null);
            
        } catch (JDOMException e) {
        	RequbeUtil.do_PrintStackTrace(log, e);
        } catch (IOException e) {
        	RequbeUtil.do_PrintStackTrace(log, e);
        }

        return strResultSet;
        
    }
    
}
