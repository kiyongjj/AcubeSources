package com.sds.rqreport.service.web;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.sds.rqreport.Environment;
import com.sds.rqreport.model.RQDocStatCL;
import com.sds.rqreport.model.RQDocStatParam;
import com.sds.rqreport.model.RQDocStatSV;
import com.sds.rqreport.model.RQDocStatTB;
import com.sds.rqreport.repository.RepositoryEnv;

public class RQDocStatDAOImpl {
	
	Environment env = null;
	RepositoryEnv renv = null;
	
	public RQDocStatDAOImpl(){
		env = Environment.getInstance();
		renv = RepositoryEnv.getInstance();
	}
	
	public int insertDocStatInfoAbNormal(RQDocStatCL p_DocStatCL, RQDocStatSV p_DocStatSV){
		RQDocStatDAO jdbc = new RQDocStatDAO();
		PreparedStatement pstmt =  null;
		int applyRowflag = 0;
		try{
			jdbc.connect();
			String qry = "Insert into " +renv.doc_DocStatTableName+ " (ERROR_CNT) values (?)";
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.setInt(1, 1);
			applyRowflag = pstmt.executeUpdate();
			pstmt.close();
			jdbc.close();
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}finally{
			if (pstmt != null) try{pstmt.close();}catch(Exception e){}
			if (jdbc.conn != null ) jdbc.close();
		}
		return applyRowflag;
	}
	
	public int insertDocStatInfoNormal(RQDocStatCL p_DocStatCL, RQDocStatSV p_DocStatSV){
		RQDocStatDAO jdbc = new RQDocStatDAO();
		PreparedStatement pstmt =  null;
		int applyRowflag = 0;
		try{
			jdbc.connect();
			//renv
			String qry = "Insert into " +renv.doc_DocStatTableName+ " (" +
						 "  RUN_TIME, " +
						 "  FILE_NM, " +
						 "  RUNCNT, " +
						 "  SERVERTIME_ACCUMUL, " +
						 "  SERVERTIME_AVE, " +
						 "  TOTALTIME_ACCUMUL, " +
						 "  TOTALTIME_AVE, " +
						 "  MAXTIME, " +
						 "  MAXTIME_RUNVAR, " +
						 "  MINTIME, " +
						 "  MINTIME_RUNVAR, " +
						 "  ERROR_CNT " +
						 ") values ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )";
			
			pstmt = jdbc.conn.prepareStatement(qry);
			pstmt.setInt(1, Integer.parseInt(p_DocStatCL.getRUN_TIME().substring(0,8)));
			pstmt.setString(2, p_DocStatSV.getFILE_NM());
			pstmt.setInt(3, 1);
			pstmt.setInt(4, Integer.parseInt(p_DocStatSV.getSERVERTIME()));
			pstmt.setInt(5, Integer.parseInt(p_DocStatSV.getSERVERTIME()));
			pstmt.setInt(6, Integer.parseInt(p_DocStatCL.getTOTALTIME()));
			pstmt.setInt(7, Integer.parseInt(p_DocStatCL.getTOTALTIME()));
			
			pstmt.setInt(8, Integer.parseInt(p_DocStatCL.getTOTALTIME()));
			pstmt.setString(9, p_DocStatSV.getRUNVAR());
			pstmt.setInt(10, Integer.parseInt(p_DocStatCL.getTOTALTIME()));
			pstmt.setString(11, p_DocStatSV.getRUNVAR());
			pstmt.setInt(12, 0);
			
			applyRowflag = pstmt.executeUpdate();
			pstmt.close();
			jdbc.close();

		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}finally{
			if (pstmt != null) try{pstmt.close();}catch(Exception e){}
			if (jdbc.conn != null ) jdbc.close();
		}
		return applyRowflag;
	}
	
	public int updateDocStatInfoNormal(RQDocStatCL p_DocStatCL, RQDocStatSV p_DocStatSV){
		RQDocStatDAO jdbc = new RQDocStatDAO();
		PreparedStatement pstmt =  null;
		int applyRowflag = 0;
		try{
			jdbc.connect();
			String run_time = p_DocStatCL.getRUN_TIME().substring(0,8);
			String wheresyntax = " where run_time=" +run_time+ " and file_nm='"+p_DocStatSV.getFILE_NM()+"'";
			String totRuncnt = "select runcnt + 1 from " +renv.doc_DocStatTableName+ " " + wheresyntax ;
			String qry = "update " +renv.doc_DocStatTableName+ " set " +
						 "run_time           = "+run_time+", " +
						 "file_nm            = '"+p_DocStatSV.getFILE_NM()+"', " +
						 "runcnt             = (" +totRuncnt+ "), " +
						 "servertime_accumul = (select servertime_accumul + " +p_DocStatSV.getSERVERTIME()+ " from " +renv.doc_DocStatTableName+ "" +wheresyntax+ "), " +
						 "servertime_ave     = (select ((select servertime_accumul + " +p_DocStatSV.getSERVERTIME()+ " from " +renv.doc_DocStatTableName+ " " +wheresyntax+ ")" +
						 "                     /("+totRuncnt+")) from dual), " +
						 "totaltime_accumul  = (select (select totaltime_accumul + " +p_DocStatCL.getTOTALTIME()+ " from " +renv.doc_DocStatTableName+ " "+wheresyntax+") from dual)," +
						 "totaltime_ave      = (select ((select totaltime_accumul + " +p_DocStatCL.getTOTALTIME()+ " from " +renv.doc_DocStatTableName+ " "+wheresyntax+")" +
						 "                     /("+totRuncnt+")) from dual), " +
						 "maxtime            = (select decode(sign(" +p_DocStatCL.getTOTALTIME()+ " - (select maxtime from " +renv.doc_DocStatTableName+ " "+wheresyntax+")), 1, " +p_DocStatCL.getTOTALTIME()+ ", " +
						 "                             -1, (select maxtime from " +renv.doc_DocStatTableName+ " "+wheresyntax+"), " +p_DocStatCL.getTOTALTIME()+ ") from dual), " +
						 "maxtime_runvar     = (select decode(sign("+p_DocStatCL.getTOTALTIME()+" - (select maxtime from " +renv.doc_DocStatTableName+ " "+wheresyntax+")), 1, '"+p_DocStatSV.getRUNVAR()+"', " +
						 "                             -1, (select maxtime_runvar from " +renv.doc_DocStatTableName+ " "+wheresyntax+"), '"+p_DocStatSV.getRUNVAR()+"') from dual), " +
						 "mintime            = (select decode(sign("+p_DocStatCL.getTOTALTIME()+" - (select mintime from " +renv.doc_DocStatTableName+ " "+wheresyntax+")), -1, "+p_DocStatCL.getTOTALTIME()+", " +
						 "                              1, (select mintime from " +renv.doc_DocStatTableName+ " "+wheresyntax+"), "+p_DocStatCL.getTOTALTIME()+") from dual ), " +
						 "mintime_runvar     = (select decode(sign("+p_DocStatCL.getTOTALTIME()+" - (select mintime from " +renv.doc_DocStatTableName+ " "+wheresyntax+")), -1, '"+p_DocStatSV.getRUNVAR()+"', " +
						 "                              1, (select mintime_runvar from " +renv.doc_DocStatTableName+ " "+wheresyntax+"), '"+p_DocStatSV.getRUNVAR()+"') from dual), " +
						 "error_cnt          = (select error_cnt + "+p_DocStatCL.getERROR()+"0 from " +renv.doc_DocStatTableName+ " "+wheresyntax+") " +
						 "where run_time = "+run_time+
						 "     	and file_nm = '"+p_DocStatSV.getFILE_NM()+"' ";
			
			pstmt = jdbc.conn.prepareStatement(qry);
			applyRowflag = pstmt.executeUpdate();
			pstmt.close();
			jdbc.close();
			
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}finally{
			if (pstmt != null) try{pstmt.close();}catch(Exception e){}
			if (jdbc.conn != null ) jdbc.close();
		}
		return applyRowflag;
	}
	
	public int updateDocStatInfoAbNormal(RQDocStatCL p_DocStatCL, RQDocStatSV p_DocStatSV){
		RQDocStatDAO jdbc = new RQDocStatDAO();
		PreparedStatement pstmt =  null;
		int applyRowflag = 0;
		try{
			jdbc.connect();
			String run_time = p_DocStatCL.getRUN_TIME().substring(0,8);
			String wheresyntax = " where run_time=" +run_time+ " and file_nm='"+p_DocStatSV.getFILE_NM()+"'";
			String qry = "update " +renv.doc_DocStatTableName+ " " +
					" set error_cnt = (select error_cnt + "+p_DocStatCL.getERROR()+" from " +renv.doc_DocStatTableName+ " "+wheresyntax+" ) " +
					" where run_time = " +run_time+ " and file_nm = '"+p_DocStatSV.getFILE_NM()+"' ";
			pstmt = jdbc.conn.prepareStatement(qry);
			applyRowflag = pstmt.executeUpdate();
			pstmt.close();
			jdbc.close();
			
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}finally{
			if (pstmt != null) try{pstmt.close();}catch(Exception e){}
			if (jdbc.conn != null ) jdbc.close();
		}
		return applyRowflag;
	}
	
	
	public ArrayList selectDocStatInfo(RQDocStatParam rqparam){
		RQDocStatDAO jdbc = new RQDocStatDAO();
		PreparedStatement pstmt =  null;
		ArrayList rtnset = null;
		ResultSet rs   = null;
		try{
			jdbc.connect();
			String qry_init = "select run_time, file_nm, runcnt, servertime_accumul, servertime_ave," +
					          "totaltime_accumul, totaltime_ave, maxtime, maxtime_runvar, mintime, mintime_runvar, error_cnt "+
			                  "from " +renv.doc_DocStatTableName+ " " +
			                  "where 1 = 1 ";
			String between  = "and run_time between ? and ? ";
			String fn_name  = "and file_nm like '%" + rqparam.getStrDocsearch() + "%'";
			String ob_time  = "order by run_time asc, file_nm asc";
			String ob_file  = "order by file_nm asc, run_time asc";
			
			String qry = qry_init;
			if(rqparam.getStrDuringstart() != null &&
			   !rqparam.getStrDuringstart().equals("") &&
			   rqparam.getStrDuringend() != null &&
			   !rqparam.getStrDuringend().equals("")){
				qry += between;
			}
			
			String strDocsearch = rqparam.getStrDocsearch();
			if(strDocsearch != null && !strDocsearch.equals("")){
				qry   += fn_name;
			}
			if(rqparam.getStrBasesearch().equals("baseday")){
				qry   += ob_time;
			}else{
				qry   += ob_file;
			}
			
			pstmt = jdbc.conn.prepareStatement(qry);
			if(rqparam.getStrDuringstart() != null &&
					   !rqparam.getStrDuringstart().equals("") &&
					   rqparam.getStrDuringend() != null &&
					   !rqparam.getStrDuringend().equals("")){
				pstmt.setInt(1, Integer.parseInt(rqparam.getStrDuringstart()));
				pstmt.setInt(2, Integer.parseInt(rqparam.getStrDuringend())  );
			}
			
			rs = pstmt.executeQuery();
			rtnset = new ArrayList();
			while(rs.next()){
				RQDocStatTB oRQDocStatTB = new RQDocStatTB();
				oRQDocStatTB.setRUN_TIME(rs.getInt(1));
				oRQDocStatTB.setFILE_NM(rs.getString(2));
				oRQDocStatTB.setRUNCNT(rs.getInt(3));
				oRQDocStatTB.setSERVERTIME_ACCUMUL(rs.getInt(4));
				oRQDocStatTB.setSERVERTIME_AVE(rs.getInt(5));
				oRQDocStatTB.setTOTALTIME_ACCUMUL(rs.getInt(6));
				oRQDocStatTB.setTOTALTIME_AVE(rs.getInt(7));
				oRQDocStatTB.setMAXTIME(rs.getInt(8));
				oRQDocStatTB.setMAXTIME_RUNVAR(rs.getString(9));
				oRQDocStatTB.setMINTIME(rs.getInt(10));
				oRQDocStatTB.setMINTIME_RUNVAR(rs.getString(11));
				oRQDocStatTB.setERROR_CNT(rs.getInt(12));
				rtnset.add(oRQDocStatTB);
			}
			rs.close();
			pstmt.close();
			jdbc.close();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if (rs != null) try{rs.close();}catch(Exception e){}
			if (pstmt != null) try{pstmt.close();}catch(Exception e){}
			if (jdbc.conn != null ) jdbc.close();
		}
		return rtnset;
	}
}
