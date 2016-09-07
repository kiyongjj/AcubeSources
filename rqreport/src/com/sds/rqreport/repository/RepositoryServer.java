package com.sds.rqreport.repository;

import com.sds.rqreport.common.AbstractRequester;
import com.sds.rqreport.common.RQServerManager;
import com.sds.rqreport.dataagent.DataAgent;
import com.sds.rqreport.dataagent.DataAgentServer;

public class RepositoryServer extends RQServerManager {

	public RepositoryServer() {
		super();
	}

	public AbstractRequester getRequester() {

		return new RepositoryRequester();
	}

	public static void main(String[] args) {
		int port = Integer.parseInt( args[0]);
		RepositoryServer repserver = new RepositoryServer();
		repserver.setPort(port);
		repserver.setActor(new RepositoryAgent());
		repserver.run();

	}
}
