package com.sds.rqreport.scheduler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.apache.log4j.Logger;

import com.sds.rqreport.util.*;

public class ScheduleExecution extends TimerTask {
	private int sid;
	private static Logger L = Logger.getLogger("SCHEDULER");
	public ScheduleRunInfo sri = new ScheduleRunInfo();
	private Vector sids;
	private HashMap notScheduleddoc = new HashMap(); 
	
	SchedulerEnv env = null;
	
	public ScheduleExecution() {env = SchedulerEnv.getInstance();}
	
	ScheduleExecution(int sid) {
		this.sids = new Vector(1);
		this.sids.add(new Integer(sid));
		this.sid = sid;
		env = SchedulerEnv.getInstance();
		//generate ScheduleRunInfo!!
	}

	/**
	 *  Schedule Group을 위한 생성자
	 * @param sids
	 */
	ScheduleExecution(Vector sids)
	{
		this.sids = sids;
		env = SchedulerEnv.getInstance();
	}

	public void run() {
		//Get ScheduleRunInfo
		int runsize = sids.size();

		RQScheduleAPI ScheduleAPI = new RQScheduleAPI();
		int exesuccess = 0, mailsendsuccess = 0;
		int exetotal = 0, mailsendtotal = 0;
		String failedSchedule ="";
		String failedMailAddr = "";
		boolean failure = false, mailfailure = false;
		for (int n = 0; n < runsize; ++n) {
			int id = ((Integer) sids.get(n)).intValue();

			Vector ret = new Vector(3);

			ScheduleAPI.getScheduleRunInfos(id, ret);

			Vector runInfos = (Vector) ret.get(1);
			ret = null;
			int size = runInfos.size();
			MailInfo mi = null;
			// 메일보내기와 실행 성공 갯수를 카운트한다.
			exetotal += size;
			mailsendtotal += size;
			for (int i = 0; i < size; ++i) {
				ScheduleRunInfo sri = (ScheduleRunInfo) runInfos.get(i);
				String resultPath = "";
				try {
					resultPath = execute(sri);
					if(resultPath.length() > 0)
					{
						exesuccess++;

					}
					else
					{
						if(failure)
						{
							failedSchedule += ",";
						}
						failedSchedule += id;
						failure = true;
					}
				} catch (Exception e) {

					resultPath = "##Execution Failed!";
				}

				// 메일알림
				if (sri.notification == 1 || sri.notification == 3) {
					ret = new Vector(3);
					ScheduleAPI.getMailInfo(sri.runinfoid, ret);
					mi = (MailInfo) ret.get(1);
					if (sri.emailForm != null && sri.emailForm.length() > 1) {
						mi.makeMailForm(sri.emailForm);
					} else {
						mi.makeMailForm("mail.txt");
					}

					if (resultPath != null && resultPath.length() > 0) {
						// Set Result File to attach
						String getResult = getFile(resultPath);
						mi.setFile(resultPath);
					}
					if(sendMail(mi))
					{
						mailsendsuccess++;
					}
					else
					{
						if(mailfailure)
						{
							failedMailAddr += ",";
						}
						failedMailAddr += sri.mailingList;
						mailfailure = true;
					}
				} else if (sri.notification == 2) //금감원용 메일 파일로 저장.
					{
					ret = new Vector(3);
					ScheduleAPI.getMailInfo(sri.mailingList, ret);
					mi = (MailInfo) ret.get(1);
					if (resultPath != null && resultPath.length() > 0) {
						// Set Result File to attach
						// 해당 위치에 파일 복사
						//copyFile(resultPath, E);
						mi.setFile(resultPath);
					} else {
						mi.setFile("#No attach File");
					}

					writeIndex(sri.indexfile, mi);
					mailsendsuccess++;
				}

			}
			// Repository Manager에 스케줄 종료여부 등록
		}
		////////// CLient 실행시 client 로 내려가지 못한 문서들 재전송 ////////////////////
		int lm_hmsize = notScheduleddoc.size();
		if(lm_hmsize > 0){
			L.debug("Not Scheduled Doc size : " + lm_hmsize);
			Set keySet = notScheduleddoc.keySet();
			Iterator it = keySet.iterator();
			while(it.hasNext()){
				ScheduleInfoCL scl = (ScheduleInfoCL)it.next();
				sendInfoSocketServer(scl.getLocalip(), 
						             scl.getLocalipport(), 
						             scl.getResultFileName(), 
						             scl.getDoctype(), 
						             scl.getOScheduleRunInfo());
			}
		}
		////////////////////////////////////////////////////////////////////////
		L.info(exesuccess + " schedule(s) of " + exetotal + " have run successfully!");
		L.info(mailsendsuccess + " schedule(s) of " + mailsendtotal + " sent mail(s) successfully!");
		if( exesuccess != exetotal)
		{
			L.info("Schedule(s),  " + failedSchedule + " failed");
		}

		if( mailsendsuccess != mailsendtotal)
		{
			L.info("Mail address(es),  " + failedMailAddr + " failed");

		}


	}
	/**
	 *  파일로 저장 기능
	 * @param mi
	 * @return
	 */
	synchronized protected boolean writeIndex(String filepath, MailInfo mi) {
		try {

			FileWriter f = new FileWriter(filepath, true);
			f.write(mi.attachFiles[0]);
			for (int i = 0; i < mi.recipients.length; i++) {
				f.write("\t");
				f.write(mi.recipients[i]);

			}
			f.write("\n");
			f.close();
		} catch (Exception e) {
			L.error(e);
		}
		return true;
	}
	
	public String sendInfoSocketServer(String localip, int localipport, String resultFileName, String doctype, ScheduleRunInfo sri){
		RQScheduleAPI ScheduleAPI = new RQScheduleAPI();
		BufferedWriter bw = null;
		BufferedReader br = null;
		Socket socket     = null;
		String statussignal = "";
		env = SchedulerEnv.getInstance();
		try{
			L.debug("sri.resultFileName : " + sri.resultFileName);
			Encrypter enc = new Encrypter("RQREPORT6**??");
			String enc_ftpinfo = enc.encrypt(env.ftpinfo);
			String scheduleinfo = sri.doc       + env.rqsep + 
								  sri.runvar    + env.rqsep + 
								  doctype       + env.rqsep + 
								  sri.resultFileName + env.rqsep + 
								  ""            + env.rqsep + //http://70.7.31.125:8080/rqreport/
								  sri.runinfoid + env.rqsep + 
								  enc_ftpinfo   + env.rqsep +
								  sri.dformat   + env.rqsep;
			socket = new Socket(localip, localipport);
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			/////// data check /////////////////////////////////////////////////////////
			ScheduleInfoCL scl = new ScheduleInfoCL();
			scl.setLocalip(localip);
			scl.setLocalipport(localipport);
			scl.setResultFileName(resultFileName);
			scl.setDoctype(doctype);
			scl.setOScheduleRunInfo(sri);
			notScheduleddoc.put(sri.runinfoid,scl);
			///////////////////////////////////////////////////////////////////////////
			
			bw.write(scheduleinfo);
			bw.flush();
			L.debug("Schedule info. : " + scheduleinfo);
			
			statussignal = br.readLine();
			
			//////////////// client에서 status signal 을 받아 보낸것과 체크를 한다. ////////
			L.debug("status signal  : " + statussignal);
			if(statussignal.equalsIgnoreCase("ok")){
				notScheduleddoc.remove(sri.runinfoid);
			}
			////////////////////////////////////////////////////////////////////////////

			return resultFileName;
			
		}catch(IOException e){
			ScheduleAPI.addRunInfoStatus(sri.runinfoid, "ServerError");
			e.printStackTrace();
			RequbeUtil.do_PrintStackTrace(L, e);
		}finally{
			try {
				br.close();
				bw.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultFileName;
	}
	
	protected String execute(ScheduleRunInfo sri) {
		// Schedule Result
		String localip =  env.pcexelocalip;
		int localipport = 59797;
		//System.out.println("To Do: Scheduler execute ");
		RQScheduleAPI ScheduleAPI = new RQScheduleAPI();
		ScheduleAPI.addRunInfoStatus(sri.runinfoid, "Running");
		L.debug("doc: " + sri.doc);
		L.debug("runvar: " + sri.runvar);

		String resultFileName = sri.resultFileName; 
		String doctype = resultFileName.substring(resultFileName.lastIndexOf(".") + 1, resultFileName.length());
		
		////// rqv 가 아닐경우만 (pdf, hwp, xls, gul, rtf) ////////////
		// rqv가 아닐경우 PC(NT) 실행을 위해 59797 포트를 통해 클라이언트로 해당하는 정보를 넘겨준다.
		if(!doctype.equalsIgnoreCase("rqv")){
			try{Thread.sleep(10);}catch(InterruptedException e){e.printStackTrace();}
			/////// socket test ////////////////////////////////////////////////////////
			// sendInfoSocketServer("70.7.31.125", 60000, resultFileName, doctype, sri);
			////////////////////////////////////////////////////////////////////////////
			return sendInfoSocketServer(localip, localipport, resultFileName, doctype, sri);
		}
		return getResultFile(ScheduleAPI, sri);
	}

	public String getResultFile(RQScheduleAPI ScheduleAPI, ScheduleRunInfo sri){
		String args = "action=getResult&doc=";
		args += sri.doc;
		args += "&runvar=";
		args += sri.runvar;
		//env.server;

		//URL url = new URL();
		try {
			URL url = new URL(env.server);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//OutputStream out = conn.getOutputStream();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			send(conn, args);
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				String result = read(conn);
				if (sri.resultFileName != null && sri.resultFileName.length() > 0) {
					addDoc(sri,result);
					return sri.resultFileName;
				}

				return result;


			}
			else
			{
				return "ER";
			}
			//OutputStream outStream = conn.getOutputStream();
		//	outStream.write(args.getBytes("UTF-8"));
		//	DataInputStream inStream = new DataInputStream(conn.getInputStream());
		//	outStream.close();

		} catch (MalformedURLException e) {
			if(sri.callAPI == false) ScheduleAPI.addRunInfoStatus(sri.runinfoid, "Server Error");
			e.printStackTrace();
		} catch (IOException e) {
			if(sri.callAPI == false) ScheduleAPI.addRunInfoStatus(sri.runinfoid, "Server Error");
			e.printStackTrace();
		}
		return "";
	}
	
	protected short addDoc(ScheduleRunInfo sri, String result) {
		// To Reg
		//System.out.println("To Do: Scheduler execute ");
		L.debug("doc: " + sri.doc);
		L.debug("runvar: " + sri.runvar);

		String args = "func=adddoc&args=";
		args += Encrypter.encrypt26(result) + ",";
		args += Encrypter.encrypt26(sri.resultFileName)+ ",";
		args += Encrypter.encrypt26(sri.user)+ ",";
		if(sri.callAPI == false){
			args += Encrypter.encrypt26("Scheduled Result");	
		}else{
			args += Encrypter.encrypt26("api result");
		}
		
	//	args += sri.runvar;
		//env.server;

		//URL url = new URL();
		try {
			URL url = new URL(env.serverRepository);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//OutputStream out = conn.getOutputStream();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			send(conn, args);
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				read(conn);
				return 0;
			}
			else
			{
				return -2;
			}

			//OutputStream outStream = conn.getOutputStream();
		//	outStream.write(args.getBytes("UTF-8"));
		//	DataInputStream inStream = new DataInputStream(conn.getInputStream());
		//	outStream.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	public String getFile(String result) {

		String args = "func=download2&args=";
		args += Encrypter.encrypt26(result);
//		args += Encrypter.encrypt26(sri.resultFileName)+ ",";
//		args += Encrypter.encrypt26(sri.user)+ ",";
//		args += Encrypter.encrypt26("Scheduled Result");

		try {
			URL url = new URL(env.serverRepository);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			//OutputStream out = conn.getOutputStream();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			send(conn, args);
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				String datastr =  read(conn);
				if(datastr != null && datastr.length() > 20)
				{
					int pos = datastr.indexOf("<RESULTDATA>");
					int pos2 = datastr.indexOf("</RESULTDATA>");
					if(pos < 0 || pos2 < 0)
					{
						return "ERR";
					}
					byte[] data = Base64Decoder.decodeToBytes(datastr.substring(pos + 12, pos2));
					if(data.length > 0)
					{
						String writeFilePath;
						if(!result.startsWith("/"))
						{
							writeFilePath = env.schedulerDocDir + "/" + result;
						}
						else
						{
							writeFilePath = env.schedulerDocDir + result;
						}



						File writeFile = new File(writeFilePath);
						if(!writeFile.getParentFile().exists())
						{
							writeFile.getParentFile().mkdirs();
						}

						FileOutputStream fo = new FileOutputStream(writeFile);
						fo.write(data);
						fo.close();
						return writeFile.getPath();
					}

				}
			}
			else
			{
				return "ERR";
			}

			//OutputStream outStream = conn.getOutputStream();
		//	outStream.write(args.getBytes("UTF-8"));
		//	DataInputStream inStream = new DataInputStream(conn.getInputStream());
		//	outStream.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}


	public String read(HttpURLConnection p_con) throws IOException {
        DataInputStream dis = new DataInputStream(p_con.getInputStream());
        int c;
        StringBuffer buf = new StringBuffer();
        while ((c = dis.read()) != -1) {
                buf.append((char)c);
        }
        return buf.toString().trim();
    }

	public  void send(HttpURLConnection p_con, String p_writeMsg) throws IOException {
			OutputStream out = p_con.getOutputStream();
			out.write(p_writeMsg.getBytes("UTF-8"));
	        //PrintWriter pout = new PrintWriter(p_con.getOutputStream());
	        //pout.print(p_writeMsg);
	        //dos.flush();
	}

	protected final int FAIL_TO_RUN = 1; // 문서실행에 실패했을경우.
	protected final int SUCCESS_TO_RUN = 2; // 실행 성공시.
	protected final int USER_DEFINED_SUCCESS = 3; // 사용자 정의 성공.

	protected void makeMessage(int type, String doc) {
		String subject;
		String message;
		String attachFile;
		boolean bFindFile = false;
		if (type == USER_DEFINED_SUCCESS) {
			bFindFile = true;
		}

		StringBuffer sbuf = new StringBuffer(1024);
		if (type == FAIL_TO_RUN) {
			//2-2. 제목
			subject = ""; //EasyBase 문서 '%1'가 실행 실패하였습니다.

			//2-3. 본문
			sbuf.append(""); //예약하신 다음 EasyBase 문서가 실행에 실패하였습니다.

			sbuf.append(""); //\t스케줄 번호: %1\n
			sbuf.append(""); //\t문서명: %1\n
			sbuf.append(""); //\t실행변수: %1\n
			sbuf.append(""); //\t예약 사용자명: %1

			sbuf.append(""); //실행 시작 시간: %1\n
			sbuf.append(""); //실행 종료 시간: %1\n

			sbuf.append(""); //실행시 에러 메시지는 다음과 같습니다.
			sbuf.append(""); // strFailMessage;

			message = sbuf.toString();
		} else if (
			type == SUCCESS_TO_RUN
				|| (type == USER_DEFINED_SUCCESS
					&& !bFindFile)) { // 사용자 정의 메일양식을 사용하지 않는 경우
			//2-2. 제목
			subject = ""; //EasyBase 문서 '%1'가 성공적으로 실행되었습니다.

			//2-3. 본문
			sbuf.append(""); //예약하신 다음 EasyBase 문서가 성공적으로 실행되었음을 알려드립니다.

			sbuf.append(""); //\t스케줄 번호: %1\n
			sbuf.append(""); //\t문서명: %1\n
			sbuf.append(""); //\t실행변수: %1\n
			sbuf.append(""); //\t예약 사용자명: %1

			sbuf.append(""); //실행 시작 시간: %1\n
			sbuf.append(""); //실행 종료 시간: %1\n

			//2-4. 결과 첨부
			int nNotification = 3;
			boolean bAttach = ((nNotification == 3) ? true : false);
			//첨부 여부 - RUser에 SNotification==3 일때

			if (bAttach) {
				boolean bFilePath = true;
				if (bFilePath) {
					sbuf.append("");
					//결과 문서를 첨부합니다. 결과 문서를 보려면 EasyBase Viewer가 설치되어 있어야 합니다.
				} else {
					sbuf.append(""); // strFilePathError;
					sbuf.append(""); //그리하여 결과 첨부를 하지 못하였습니다.
				}
			}

			message = sbuf.toString();
		} else if (type == USER_DEFINED_SUCCESS) {
			InputStream in = null;
			File f;
			byte[] data = null;
			int length;
			try {
				f =
					new File(
						"c:\temp"
							+ System.getProperty("file.seperator")
							+ "mail.txt");
				length = (int) f.length();
				in = new BufferedInputStream(new FileInputStream(f));
				data = new byte[length];
				int len = 0;
				len = in.read(data, 0, length);
				if (len < length) {
					L.warn("Result length less then file size.");
				}
				in.close();
				in = null;
				String mail = new String(data, env.envMime);
				/*
				 * 제목부 입니다.[첨부파일명 입니다. 확장자는 붙이지 않습니다.]
				 * plain:
				 * 여기서부터는 본문입니다.
				 */

				// 제목부에 대치할 사용자지정 첨부파일명을 넣어준다. [첨부파일명]
				int pos1 = mail.indexOf('[', 2);
				int pos2 = mail.indexOf(']', pos1);
				subject = mail.substring(0, pos1);
				attachFile = mail.substring(pos1 + 1, pos2) + ".rqr";
				StringBuffer ssbuf = new StringBuffer(mail);
				ssbuf.delete(0, ssbuf.toString().indexOf("\n") + 1);
				boolean bHtml;
				if (ssbuf.toString().indexOf("html") == 0)
					bHtml = true;
				else
					bHtml = false;
				ssbuf.delete(0, ssbuf.toString().indexOf("\n") + 1);
			} catch (Exception ex1) {
			}
		}
	}

	protected boolean sendMail(MailInfo mi) {
		if (mi == null)
			return false;

		try {
			postMail(mi);
			return true;
		} catch (MessagingException mex) {
			mex.printStackTrace();
			System.out.println();
			Exception ex = mex;
			do {
				if (ex instanceof SendFailedException) {
					SendFailedException sfex = (SendFailedException) ex;
					Address[] invalid = sfex.getInvalidAddresses();
					if (invalid != null) {
						System.out.println("    ** Invalid Addresses");
						if (invalid != null) {
							for (int i = 0; i < invalid.length; i++)
								System.out.println("         " + invalid[i]);
						}
					}
					Address[] validUnsent = sfex.getValidUnsentAddresses();
					if (validUnsent != null) {
						System.out.println("    ** ValidUnsent Addresses");
						if (validUnsent != null) {
							for (int i = 0; i < validUnsent.length; i++)
								System.out.println(
									"         " + validUnsent[i]);
						}
					}
					Address[] validSent = sfex.getValidSentAddresses();
					if (validSent != null) {
						System.out.println("    ** ValidSent Addresses");
						if (validSent != null) {
							for (int i = 0; i < validSent.length; i++)
								System.out.println("         " + validSent[i]);
						}
					}
				}
				System.out.println();
				if (ex instanceof MessagingException)
					ex = ((MessagingException) ex).getNextException();
				else
					ex = null;
			} while (ex != null);
		}
		return false;
	}

	protected void postMail(MailInfo mi) throws MessagingException {
		System.out.println("Called postMail");
		String id = "";
		String password = "";
		boolean debug = false;

		//Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", env.smtpServer);
		L.debug("Set the host smtp address");

		// create some properties and get the default Session
		UserAuthentication user =
			new UserAuthentication(
				env.mailAuthID,
				env.mailAuthPw);
		props.put("mail.smtp.auth", env.mailAuthentification);
		Session session = Session.getDefaultInstance(props, user);
		session.setDebug(debug);
		L.debug("create some properties and get the default Session");

		// create a message
		Message msg = new MimeMessage(session);
		L.debug("create a message");

		// set the from address
		InternetAddress addressFrom =
			new InternetAddress(env.schedulerMailSender);
		msg.setFrom(addressFrom);
		L.debug("set the from and to address");

		// get e-mail list
		// set the to address
		InternetAddress[] addressTo = new InternetAddress[mi.recipients.length];
		for (int i = 0; i < mi.recipients.length; i++) {
			addressTo[i] = new InternetAddress(mi.recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);
		L.debug("set the Recipients and to address");

		// Setting the Subject and Content Type
		System.out.println("Subject:" + mi.subject);

		try {
			msg.setSubject(
				MimeUtility.encodeText(mi.subject, env.envMime, null));
		} catch (UnsupportedEncodingException e) {
			msg.setSubject(mi.subject);
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
			return;
		}
		msg.setSentDate(new Date());

		if (mi.attachFiles != null && mi.attachFiles.length > 0) {
			// create and fill the first message part
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setContent(
				mi.message,
				mi.mineType + "; charset=" + env.envMime);
			// create the Multipart and its parts to it
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp);
			mbp = null;

			// create and fill the second, third,... message part
			FileDataSource fds;
			for (int i = 0; i < mi.attachFiles.length; i++) {
				mbp = new MimeBodyPart();
				// attach the file to the message
				L.debug(mi.attachFiles[i]);
				fds = new FileDataSource(mi.attachFiles[i]);
				mbp.setDataHandler(new DataHandler(fds));
				if (i == 0
					&& mi.attachFileName != null
					&& mi.attachFileName.length() > 0) // 결과 첨부인 경우.
					try {
						L.debug(mi.attachFileName);
						//mbp.setFileName(mi.attachFileName);
						mbp.setFileName(
							MimeUtility.encodeText(
								mi.attachFileName,
								env.envMime,
								null));
					} catch (Exception e1) {
						mbp.setFileName(mi.attachFileName);

					} else {
					String name = fds.getName();
					mbp.setFileName(name);
				}
				mp.addBodyPart(mbp);
				fds = null;
				mbp = null;
			}

			// add the Multipart to the message
			msg.setContent(mp);
			mp = null;
		} else {
			msg.setContent(
				mi.message,
				mi.mineType + "; charset=" + env.envMime);
		}

		// Optional : You can also set your custom headers in the Email if you Want
		//msg.addHeader("MyHeaderName", "myHeaderValue");
		//System.out.println("Optional : You can also set your custom headers in the Email if you Want");
		Transport.send(msg);
		L.debug("send e-mail");

	}

	private String getResultPath(byte[] resultData) {
//		OutputStream out = null;
//		File fd, ff;
//		try {
//			// RepositoryManager와 EngineManager가 동일한 서버에 설치된 경우에 한함.
//			// 다른 서버에 설치된다면 환경정보 추가 필요!!
//			// ToDoList : Scheduler
//			fd = new File(env.scheduler_doc_path);
//
//			ff = File.createTempFile("RQS", ".rqr", fd);
//			L.debug("Write Scheduler FilePath: " + ff.getPath());
//			out = new BufferedOutputStream(new FileOutputStream(ff));
//			out.write(resultData);
//			return ff.getPath();
//		} catch (Exception e) {
//			L.error("File Read", e);
//			return null;
//		} finally {
//			try {
//				out.close();
//			} catch (IOException e) {
//			}
//			fd = null;
//			ff = null;
//			out = null;
//		}

		return null;
	}

	private byte[] getResultData(String resultPath) {
		int length;
		InputStream in = null;
		File f;
		try {
			f = new File(resultPath);
			length = (int) f.length();
			in = new BufferedInputStream(new FileInputStream(f));
		} catch (Exception e) {
			L.error("File Access", e);
			f = null;
			in = null;
			return null;
		}

		byte[] data = new byte[length];
		try {
			int len = in.read(data, 0, length);
			if (len < length) {
				L.warn("Result length less then file size.");
			}
		} catch (IOException e) {
			data = null;
			L.error("File Read", e);
		} finally {
			if (!f.delete()) {
				L.warn("cannot delete the result file.");
			}
			try {
				in.close();
			} catch (IOException e) {
			}
			f = null;
			in = null;
		}
		return data;
	}
}

class UserAuthentication extends Authenticator {
	PasswordAuthentication pa;

	public UserAuthentication(String id, String password) {
		pa = new PasswordAuthentication(id, password);
	}
	public PasswordAuthentication getPasswordAuthentication() {
		return pa;
	}
}
