package com.sds.rqreport.scheduler;

/**
 * 이 클래스는 RQExportMain.bat 파일에 의해 실행되는 클래스이며,
 * NT LocalScheduler와 소켓통신을 하는 메서드 (scheduleExe.sendInfoSocketServer)  호출한다.
 * @author jgrhee
 *
 */
public class RQExportMain extends Thread{
	
	public static void main(String[] args){
		
		if(args == null) return;
		String arg = args[0];
		
		// For Test 
		//String arg = "/RQReport1.rqx``pdf`RQReportpdf.pdf`yyyyMMdd";
		String[] values = arg.split("`",5);
		
		// total 5
		String doc = values[0];               // 실행할문서이름 (레파짓토리경로)
		String runvar = values[1];            // 실행변수
		String svrexport = values[2];         // 변환 형식
		String exdocname = values[3];         // 변환이름 
		String dformat = values[4];           // 포멧이름
		
		ScheduleExecution scheduleExe = new ScheduleExecution();
		ScheduleRunInfo sri = new ScheduleRunInfo();
		// export file name ex. /RQRerport1.pdf
		// if you want to change export file name ..
		// you just rename this ex_doc value 
		
		if(!exdocname.equals("")){
			sri.resultFileName = exdocname; // 변환이름을 따로 넘겨주면 그 이름을 사용한다.
		}else{
			String ex_doc = doc.substring( 0, doc.lastIndexOf(".") + 1 );
			sri.resultFileName = ex_doc + svrexport;	
		}
		
		sri.doc = doc;
		sri.runvar = runvar;
		sri.runinfoid = "0";
		// file format ex) yyyyMMdd, yyyyMM, MMdd, yyyyMMddhhmmss
		// if you set sri.dformat = "yyyyMMdd";
		// pdf export RQReport1_yyyyMMdd.pdf
		sri.dformat = dformat; 
		
		scheduleExe.sendInfoSocketServer("localhost", 59797, sri.resultFileName, svrexport, sri);	
	}
}
