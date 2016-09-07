package com.sds.rqreport.dataagent;

import com.sds.rqreport.common.AbstractRequester;
import com.sds.rqreport.common.RQServerManager;

public class DataAgentServer extends RQServerManager {

	public DataAgentServer() {
		super();
	}

	public AbstractRequester getRequester() {

		return new DARequester();
	}

	public static void main(String[] args) {
		int port = Integer.parseInt( args[0]);
		DataAgentServer daserver = new DataAgentServer();
		daserver.setPort(port);
		daserver.setActor(new DataAgent());
		daserver.run();

	}
}
