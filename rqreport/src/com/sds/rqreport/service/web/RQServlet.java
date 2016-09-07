package com.sds.rqreport.service.web;

import java.io.*;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import com.sds.rqreport.Environment;
import com.sds.rqreport.repository.*;
import com.sds.rqreport.util.Base64Decoder;
import com.sds.rqreport.util.RequbeUtil;

public class RQServlet extends HttpServlet {
	
	private Logger log = null;
	/**
	 * Constructor of the object.
	 */
	public RQServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// input code 
		HttpSession session = request.getSession(true);
		String cmd = request.getParameter("cmd");
		if(cmd != null){
			if(cmd.equals("svrver")){
				
				PrintWriter out = response.getWriter();
				// search for Viewer version in "/rqreport/setup/cab/RQViewer.inf"
				String contextPath = session.getServletContext().getRealPath("/setup/cab/");

				FileReader fr = new FileReader(contextPath + "/RQViewer.inf");
				BufferedReader br = new BufferedReader(fr);
				String line = "";
				String viewerVersion = "";
				String searchword = "RQViewer=";

				while((line = br.readLine()) != null){
					if(line.indexOf(searchword) != -1){
						int idx = line.indexOf(searchword);
						viewerVersion = line.substring(idx + searchword.length(), line.length());
						if(viewerVersion.length() > 0){
							viewerVersion = viewerVersion.trim();
							out.println(viewerVersion);
						}
					}
				}
				
			}else if(cmd.equals("getViewer")){
				String fileName = request.getParameter("filename"); 
				if(fileName != null && !fileName.equals("")){
					
					if(fileName.indexOf("\\..") != -1 || fileName.indexOf("\\.") != -1) return;
					
					response.setContentType("application/octet-stream");
					String contextPath = session.getServletContext().getRealPath("/setup/cab/");
					FileInputStream inFile = new FileInputStream(contextPath +"/"+ fileName); 
					ServletOutputStream resOut = response.getOutputStream(); 
					DataOutputStream output = new DataOutputStream(resOut); 
					byte [] buf = new byte[1024]; 
					while ( inFile.read( buf ) != -1 ) { 
						output.write( buf ); 
					} 
					inFile.close(); 
					output.close();
				}
			}
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//log.debug("## REQUEST ######################### " + cnt_1++);
		String cmd = request.getParameter("cmd");

		if(cmd != null) {
			if(cmd.equalsIgnoreCase("savepdf")){
				getXMLDocInRequst(request);
				
			}
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an erro
		r occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		log = Logger.getLogger("RQWEB");
	}
	
	public void savepdf(HttpServletRequest request, HttpServletResponse response)  
		throws ServletException, IOException {
		
	}
	
	public void getXMLDocInRequst(HttpServletRequest request){
		
		String doc  = request.getParameter("doc") == null ? "" : request.getParameter("doc");
		String id   = request.getParameter("id")  == null ? "" : request.getParameter("id");
		String pw   = request.getParameter("pw")  == null ? "" : request.getParameter("pw");
		String pid  = request.getParameter("pid") == null ? "" : request.getParameter("pid");
		String flag = request.getParameter("flag") == null ? "" : request.getParameter("flag");
		
		String uid = request.getRemoteAddr() + "_" + pid;
				
		RepositoryEnv env = RepositoryEnv.getInstance();
		//rqx 업로드시 request에서 문서정보를 따로 빼낸다.    ////////
		ByteArrayOutputStream baos = null;
		
		try {
			ServletInputStream in = request.getInputStream();
			
			Base64Decoder decoder = new Base64Decoder(in);
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];  // 4K buffer
			int readcount = 0;

			while((readcount = decoder.read(buffer)) != -1){
				baos.write(buffer, 0, readcount);
			}
			
			byte[] lm_decBytes = null;
			
			ifsentence :
			if(flag.equals("start")){
				
				lm_decBytes = baos.toByteArray();
				RQSavePdfparam pdfparam = new RQSavePdfparam();
				pdfparam.setDoc(doc);
				pdfparam.setId(id);
				pdfparam.setPw(pw);
				pdfparam.setUid(uid);
				pdfparam.setRawdata(null); // initialize 
				pdfparam.setRawdata(lm_decBytes);
				
				RQRawdataS instance = RQRawdataS.getInstance();
				instance.setDocIntoRawDList(uid, pdfparam);
				
			}else if(flag.equals("append")){
				
				lm_decBytes = baos.toByteArray();
				RQRawdataS instance = RQRawdataS.getInstance();
				RQSavePdfparam pdfparam = instance.getDocFromRawDList(uid);
				
				if(pdfparam != null){
					byte[] temp = RequbeUtil.merge(pdfparam.getRawdata(), lm_decBytes);
					pdfparam.setRawdata(temp);
				}else{
					break ifsentence;
				}
				
			} else if(flag.equals("end")) {
				
				RQRawdataS instance = RQRawdataS.getInstance();
				RQSavePdfparam pdfparam = instance.getDocFromRawDList(uid);
				
				ByteArrayInputStream bais = null;
				FileOutputStream     fos  = null;
				if(pdfparam != null){
					
					try{
						bais = new ByteArrayInputStream(pdfparam.getRawdata());
						String lm_path = env.repositoryRoot + doc; //(-) File.separator 
						
						int idx = lm_path.lastIndexOf("/");
						if(idx != -1){
							String lm_dir = lm_path.substring(0, idx);
							File f = new File(lm_dir);
							if(!f.exists())	f.mkdirs();
						}
						
						fos  = new FileOutputStream(lm_path);
						byte[] buf = new byte[1024];  // 4K buffer
						int bytesRead;
						while( (bytesRead = bais.read(buf)) != -1){
							fos.write(buf, 0, bytesRead);
						}
						pdfparam.setRawdata(null);
						HashMap hm = instance.getList();
						hm.put(uid, null);
					}finally{
						try{ if(bais != null) bais.close(); } catch (IOException e){}
						try{ if(fos != null) fos.close(); } catch (IOException e){}
					}
					
				}else{
					break ifsentence;
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {baos.close();} catch (IOException e) {e.printStackTrace();}
		}
	}
}
