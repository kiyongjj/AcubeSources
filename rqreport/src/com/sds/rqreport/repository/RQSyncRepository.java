package com.sds.rqreport.repository;
import java.io.File;
 
public interface RQSyncRepository {

	public boolean setEnv(Object env);
	public void setServerList(String[] serverlist);
	public void setPortList(int[] portlist);
	public void setConnectionInfo(String[] conn1, String[] conn2);
	public int sendDoc(File f, String path);
	public int deleteDoc(String path);
	public int renameDoc(String srcpath, String destpath);
	public int deleteFolder(String path);
	public int makeFolder(String path);
	public int makeFolders(String path);

}
