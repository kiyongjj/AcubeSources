package com.sds.rqreport.repository;
import java.io.*;
import sun.net.ftp.*;
import sun.net.*;

public class FTPSynchronizer implements RQSyncRepository {

	String[] ftpRoot = null;
	String[] serverList = null;
	int[] portList = null;
	String[] userlist = null;
	String[] passlist = null;
	
	public void setServerList(String[] serverlist) {
		serverList = serverlist;
	}

	public void setPortList(int[] portlist) {
		portList = portlist;
	}

	public void setConnectionInfo(String[] conn1, String[] conn2) {
		userlist = conn1;
		passlist = conn2;
	}

	public int sendDoc(File f, String fullName) {
		String server = ""; 
		int port = FtpClient.FTP_PORT;
		String user = "";
		String password = "";
		String path = "";
		String[] ret = new String[2];
		getDirFile(fullName,ret);
		String dirPath = ret[0];
		
		//String filename = f.getName();
		String filename = ret[1];
		if(serverList == null || serverList.length < 1)
		{
			return 0;
		}
		
		
		
		int size = serverList.length;
		
		for(int i = 0; i < size; ++i)
		{
			try {
			   server = serverList[i];
			   user = userlist[i];
			   password = passlist[i];
			   
			   
			   path = ftpRoot[i] + dirPath;
			   if(portList != null && portList.length == serverList.length)
			   {
				   port = portList[i];
			   }
			   
		       FtpClient ftpClient=new FtpClient();
		       ftpClient.openServer(server, port);
		       ftpClient.login(user, password);
		       if (path.length()!=0)
		       {
		    	   ftpClient.cd(path);
		       }
		       ftpClient.binary();
		       TelnetOutputStream os=ftpClient.put(filename);
		       File file_in = f;
		       FileInputStream is=new FileInputStream(file_in);
		       byte[] bytes=new byte[1024];
		       int c;
		       while ((c=is.read(bytes))!=-1){
		          os.write(bytes,0,c);
		       }
		       is.close();
		       os.close();
		       ftpClient.closeServer();
		    } 
			catch (IOException ex) {;}
		}
		return 0;
	}

	public int deleteDoc(String fullname) {
		String server = "";
		int port = FtpClient.FTP_PORT;
		String user = "";
		String password = "";
		String path = "";
		String filename = "";
		String[] ret = new String[2];
		
		getDirFile(fullname, ret);
		String dirpath = ret[0];
		filename = ret[1];
		if(serverList == null || serverList.length < 1)
		{
			return 0;
		}
		int size = serverList.length;
		for(int i = 0; i < size; ++i)
		{
			try {
				   server = serverList[i];
				   user = userlist[i];
				   password = passlist[i];
				   path = ftpRoot[i] + dirpath;
				   if(portList != null && portList.length == serverList.length)
				   {
					   port = portList[i];
				   }
			   
			   
		       FtpClient ftpClient=new FtpClient();
		       ftpClient.openServer(server);
		       ftpClient.login(user, password);
		       if (path.length()!=0) ftpClient.cd(path);
		       ftpClient.sendServer("DELE " + filename + "\r\n");
		       int lastReplyCode = ftpClient.readServerResponse();
		       System.out.println(ftpClient.getResponseString()); 
		       ftpClient.closeServer();
		    } 
			catch (IOException ex) {;}
		}
		return 0;
	}

	public int renameDoc(String srcpath, String destname) {
		String server = "";
		int port = FtpClient.FTP_PORT;
		String user = "";
		String password = "";
		String path = "";
		String filename = "";
		
		String[] ret = new String[2];
		getDirFile(srcpath, ret);
		String dirpath = ret[0];
		
		filename = ret[1];
		
		if(serverList == null || serverList.length < 1)
		{
			return 0;
		}
		int size = serverList.length;
		for(int i = 0; i < size; ++i)
		{
			try {
			   server = serverList[i];
			   user = userlist[i];
			   password = passlist[i];
			   if(portList != null && portList.length == serverList.length)
			   {
				   port = portList[i];
			   }		   
			   
		       FtpClient ftpClient=new FtpClient();
		       ftpClient.openServer(server, port);
		       ftpClient.login(user, password);
		       if("".equals(path))
				{
					path = "/";
				}
		       if (path.length()!=0) ftpClient.cd(path);
		       ftpClient.sendServer("REN \"" + filename + "\" \"" + destname + "\"");
		       int lastReplyCode = ftpClient.readServerResponse();
		       System.out.println(ftpClient.getResponseString()); 		       
		       ftpClient.closeServer();
		    } 
			catch (IOException ex) {;}
		}
		return 0;
	}

	public int deleteFolder(String filepath) {
		String server = ""; 
		int port = FtpClient.FTP_PORT;
		String user = "";
		String password = "";
		String path = "";
		String filename = "";
		String[] ret = new String[2];
		if(filepath.endsWith("/"))
			filepath = filepath.substring(0,filepath.length() - 1);
		getDirFile(filepath, ret);
		path = ret[0];
		filename = ret[1];		
		if(serverList == null || serverList.length < 1)
		{
			return 0;
		}
		int size = serverList.length;
		for(int i = 0; i < size; ++i)
		{
			try {
			   server = serverList[i];
			   user = userlist[i];
			   password = passlist[i];
			   path = ftpRoot[i] + path;
		       FtpClient ftpClient=new FtpClient();
			   if(portList != null && portList.length == serverList.length)
			   {
				   port = portList[i];
			   }	
		       ftpClient.openServer(server, port);
		       ftpClient.login(user, password);
		       if (path.length()!=0) ftpClient.cd(path);
		       ftpClient.sendServer("RMD " + filename + "\r\n");
		       int lastReplyCode = ftpClient.readServerResponse();
		       System.out.println(ftpClient.getResponseString()); 		       
		       ftpClient.closeServer();
		    } 
			catch (IOException ex) {;}
		}
		return 0;

	}

	public int makeFolder(String dirpath) {
		String server = ""; 
		int port = FtpClient.FTP_PORT;
		String user = "";
		String password = "";
		String path = "";
		String filename = "";
		String[] ret = new String[2];
		getDirFile(dirpath, ret);
		path = ret[0];
		filename = ret[1];			
		if(serverList == null || serverList.length < 1)
		{
			return 0;
		}
		int size = serverList.length;
		for(int i = 0; i < size; ++i)
		{
			try {
			   server = serverList[i];
			   user = userlist[i];
			   password = passlist[i];
			   if(ftpRoot[i].endsWith("/"))
			   {
				   // null 값 체크
				   String lm_p = ""; 
				   if(path.length() > 0 ) lm_p = path.substring(1);
				   path = ftpRoot[i] + lm_p; 
			   }
			   else
			   {
				   path = ftpRoot[i] + path;
			   }
			   
		       FtpClient ftpClient=new FtpClient();
			   if(portList != null && portList.length == serverList.length)
			   {
				   port = portList[i];
			   }
		       ftpClient.openServer(server, port);
		       ftpClient.login(user, password);
		       if (path.length()!=0)
		       {
		    	   ftpClient.cd(path);
		       }
		       
		       ftpClient.sendServer("MKD " + filename + "\r\n");
		       int lastReplyCode = ftpClient.readServerResponse();
		       System.out.println(ftpClient.getResponseString()); 
		       ftpClient.closeServer();
		    } 
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}
	
	public int makeFolders(String path)
	{
		String[] ret = new String[2];
		getDirFile(path, ret);
		if(ret[0].equals("/"))
		{
			return makeFolder(path);
		}
		else
		{
			makeFolders(ret[0]);
			return makeFolder(path);
		}
		//return 0;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FTPSynchronizer sync = new FTPSynchronizer();
		String[] ftpRoot = {"/"};
		String[] serverlist = {"127.0.0.1"};
		int[] portlist = {FtpClient.FTP_PORT};
		String[] user = {"bean057"};
		String[] pw = {"tkftk76"};
		sync.ftpRoot = ftpRoot;//new String[1];
		sync.serverList = serverlist;
		sync.portList = portlist;
		sync.userlist = user;
		sync.passlist = pw;
		
		File f = new File("C:\\Samples\\test\\각부문TOP5.rqd");
		//sync.sendDoc(f, "/TEST");
	//	sync.makeFolder("/hello hi");
		sync.deleteFolder("/hello");

	}
	
	private void getDirFile(String fullname, String[] ret)
	{
		int pos = fullname.lastIndexOf("/");
		if(ret != null && ret.length > 1)
		{
			ret[0] = fullname.substring(0,pos);
			ret[1] = fullname.substring(pos + 1);
		}
	}

	public boolean setEnv(Object env) {
		
		
		if(env instanceof RepositoryEnv)
		{
			RepositoryEnv repenv = (RepositoryEnv)env;
			this.ftpRoot = repenv.ftpRoot;
			this.serverList = repenv.ftpServerList;
			this.portList = repenv.portList;
			this.userlist = repenv.userlist;
			this.passlist = repenv.passlist;
			return true;
		}
		else if(env instanceof FtpSyncEnv)
		{
			FtpSyncEnv ftpenv = (FtpSyncEnv)env;
			this.ftpRoot = ftpenv.ftpRoot;
			this.serverList = ftpenv.serverList;
			this.portList = ftpenv.portList;
			this.userlist = ftpenv.userlist;
			this.passlist = ftpenv.passlist;
			return true;
		}
		return false;
	}
}