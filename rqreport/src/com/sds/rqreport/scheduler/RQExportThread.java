package com.sds.rqreport.scheduler;

import java.io.*;

public class RQExportThread extends Thread {
	
	private static final String delimiter =  "`";
	private String doc = "";
	private String runvar = "";
	private String svrexport = "";
	private String exdocname = "";
	private String dformat = "";
	private String bat_path = "";
	
	public RQExportThread(String doc, String runvar, String svrexport, String exdocname, String dformat, String bat_path) {
		this.doc = doc;
		this.runvar = runvar;
		this.svrexport = svrexport;
		this.exdocname = exdocname;
		this.dformat = dformat;
		this.bat_path = bat_path;
	}
	
	public void run(){
		//String arg = "/RQReport1.rqx``pdf`RQReportpdf.pdf`yyyyMMdd";
		String runvalue = doc       + RQExportThread.delimiter +
						  runvar    + RQExportThread.delimiter +
						  svrexport + RQExportThread.delimiter +
						  exdocname + RQExportThread.delimiter +
						  dformat;
		
		try {
			Process process = null;
			//String currentDir = System.getProperty("user.dir");
			//Process process = Runtime.getRuntime().exec("D:\\App\\voc\\WEB\\webapps\\WEB-INF\\bin\\RQExportCmd.bat " + "\"" + runvalue + "\""); // 시스템환경변수에 경로가 있어야한다.
			if(!bat_path.equals("")) {
				process = Runtime.getRuntime().exec("\"" + bat_path + "RQExportCmd.bat\" " + "\"" + runvalue + "\"");
			}else{
				process = Runtime.getRuntime().exec("RQExportCmd.bat " + "\"" + runvalue + "\"");
			}
			 
			/*
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String tmp = "";
			while( (tmp = br.readLine()) != null){
				System.out.println(tmp);
			}*/
			//process.waitFor();

//		} catch (InterruptedException ie){
//			ie.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
