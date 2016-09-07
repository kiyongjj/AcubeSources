package com.sds.rqreport.service.queryexecute;

import java.util.*;
import org.apache.log4j.Logger;
import com.sds.rqreport.model.querynode.*;

public class RFCFuncHandler {
	private Logger L = Logger.getLogger("RQQRYEXE");
	
	public void querySplit(StringBuffer functionname, StringBuffer importvalue, StringBuffer tablevalue, QueryFrame queryframe , QueryHouse rootQueryHouse){
		RFCFuncHandler oRFuncHandler = new RFCFuncHandler();
		
		functionname = new StringBuffer();
		importvalue = new StringBuffer();
		tablevalue = new StringBuffer();
		
		HashMap ohM = queryframe.getSource();
		Collection oChM = ohM.values();
		Iterator itoChM = oChM.iterator();
		ArrayList bindSrc = null;
		
		// set functionname
		functionname.append( rootQueryHouse.getJCOFncNm() );
		bindSrc = rootQueryHouse.getOBindSrc();
		
		// for Cnt (I, T(IN_TABLE))
		Iterator itoChMforCnt = oChM.iterator();
		int iICnt = 0 ;
		int iTCnt = 0 ;
		while(itoChMforCnt.hasNext()){
			QueryHouse queryhouseNow = (QueryHouse) itoChMforCnt.next();
			if (  queryhouseNow.getJCOPrmClss().equals("I")  ){
				iICnt++;
			}
			if (  queryhouseNow.getJCOPrmClss().equals("I")  && queryhouseNow.getJCOPrmNm().equals("IN_TABLE") ){
				iTCnt++;
			}

		}// end of while
		
		int i=0; 	// for importvalue
		int j = 0;	// for tablevalue
		while(itoChM.hasNext()){
			QueryHouse queryhouseNow = (QueryHouse) itoChM.next();
			String JCOPrmClss = queryhouseNow.getJCOPrmClss();
			String JCOPrmNm = queryhouseNow.getJCOPrmNm();
			// set importvalue
			if (  queryhouseNow.getJCOPrmClss().equals("I")  ){
				importvalue.append(  JCOPrmNm + RQGetDataIf.ROW_SEP  ); // add JCOPrmNm
				//JCOPrmNme에 해당하는 값들을  을 bindSrc 에서 찾아서 importvalue에 붙인다.
				oRFuncHandler.addFVImportvalue(JCOPrmNm, bindSrc, importvalue );  
				if (i < iICnt-1){
					importvalue.append(RQGetDataIf.SSEP);
				}
				i++;
				
			}
			// set tablevalue

			if(JCOPrmClss.equals("T") && JCOPrmNm.equals("IN_TABLE")){
				JCOPrmNm = queryhouseNow.getJCOPrmNm();
				tablevalue.append(  JCOPrmNm + RQGetDataIf.ROW_SEP  ); // add JCOPrmNm
				oRFuncHandler.addFVImportvalue(JCOPrmNm, bindSrc, tablevalue );  
				if(j < iTCnt -1){
					tablevalue.append(RQGetDataIf.SSEP);
				}
				j++;

			}

		}// end of while
		L.debug("##functionname : " + functionname.toString());
		L.debug("##importvalue : " + importvalue.toString());
		L.debug("## tablevalue : " + tablevalue.toString());

	}
	
	private void addFVImportvalue(String searchWord, ArrayList bindSrc, StringBuffer retBuffer){
		
		StringBuffer strFieldNames = new StringBuffer();
		StringBuffer strValues = new StringBuffer();
		int iCnt = 0;
		
		// for Cnt (searchWord)
		Iterator itForCnt = (Iterator) bindSrc.iterator();
		while(itForCnt.hasNext()){
			ArrayList bindSrcEle = (ArrayList) itForCnt.next();
			String cpword = (String) bindSrcEle.get(0);   
			if(cpword.equals(searchWord)){
				iCnt++;
			}
		}// end of while
		
		Iterator itBindSrc = (Iterator) bindSrc.iterator();
		int i = 0;
		while(itBindSrc.hasNext()){
			ArrayList bindSrcEle = (ArrayList) itBindSrc.next();

			String cpword = (String) bindSrcEle.get(0);                      		
			
			if(cpword.equals(searchWord)){
				strFieldNames.append( (String) bindSrcEle.get(1) );				
				strValues.append( (String) bindSrcEle.get(2) );					
				if(i < iCnt - 1){
					strFieldNames.append(RQGetDataIf.COL_SEP) ; 	
					strValues.append(RQGetDataIf.COL_SEP ); 		
				}
				i++;
					
			}

		}// end of while

		retBuffer.append(strFieldNames).append(RQGetDataIf.ROW_SEP) .append(strValues) ;
	}
	
}
