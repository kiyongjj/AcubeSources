package com.sds.rqreport.common;

import java.util.*;

public class RepositoryClient {

	public RepositoryClient() {
		super();
	}

	public static void main(String[] args) throws Exception {
		SocketConnector sc = new SocketConnector("127.0.0.1",8708);
		RepositoryClientRequester requester = new RepositoryClientRequester(sc);
		ArrayList arglist = new ArrayList();
		ArrayList ret = new ArrayList();
		arglist.add("/");
		requester.callFunction(105,arglist,ret);


	}

}
