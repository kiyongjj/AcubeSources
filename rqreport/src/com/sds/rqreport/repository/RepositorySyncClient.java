package com.sds.rqreport.repository;

import com.sds.rqreport.util.*;
import java.io.*;
import java.util.*;

public class RepositorySyncClient {
	//String server = "127.0.0.1";
	//int port = 12255;
	RSClientRequester[] rsReqs = null; //new RSClientRequester();
	String[] servers = null;
	int[]	ports = null;
	public RepositorySyncClient(String server, int port)
	{
		servers = new String[1];
		ports = new int[1];
		rsReqs = new RSClientRequester[1];
		servers[0] = server;
		ports[0] = port;
		rsReqs[0] = new RSClientRequester(server, port);
	}

	public RepositorySyncClient(String[] servers, int[] ports)
	{

		this.servers = servers;
		this.ports = ports;
		this.rsReqs = new RSClientRequester[servers.length];
	}

	public int writeFile(String path, File f)
	{
		if(servers == null ||  servers.length < 1)
			return -100;
		//FunctionID : 1
		long size = f.length();
		byte[] data = new byte[(int)size];
		String encodedData;
		try {
			FileInputStream fi = new FileInputStream(f);
			fi.read(data);
			encodedData = Base64Encoder.encode(data);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList args = new ArrayList(2);
		ArrayList rets = new ArrayList(1);
		args.add(path);
		args.add(data);
		try {
			for(int i=0; i < rsReqs.length; ++i)
			{
				rsReqs[i].callFunction(1, args, rets);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public int deleteFile(String path)
	{
		if(servers == null ||  servers.length < 1)
			return -100;
		ArrayList args = new ArrayList(2);
		ArrayList rets = new ArrayList(1);
		args.add(path);
		try {
			for(int i=0; i < rsReqs.length; ++i)
			{
				rsReqs[i].callFunction(2, args, rets);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int terminateServer()
	{
		ArrayList args = new ArrayList(1);
		ArrayList rets = new ArrayList(1);
		try {
			for(int i=0; i < rsReqs.length; ++i)
			{
				rsReqs[i].callFunction(99, args, rets);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public int writeAll(String rootPath)
	{
		if(servers == null ||  servers.length < 1)
			return -100;
		String encodedData = null;
		try {
			DocRepository docRep = new DocRepository();
			File f = File.createTempFile("RQT","REP");
			docRep.makeRepositoryToZip(f.getPath(), docRep.env.repositoryRoot);
			int len = (int)f.length();
			byte[] fdata = new byte[len];

			FileInputStream fi = new FileInputStream(f);
			fi.read(fdata);
			encodedData = Base64Encoder.encode(fdata);
			fi.close();
			f.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList args = new ArrayList(2);
		ArrayList rets = new ArrayList(1);
		args.add(encodedData);
		try {
			for(int i=0; i < rsReqs.length; ++i)
			{
				rsReqs[i].callFunction(3, args, rets);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
