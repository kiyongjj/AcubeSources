/*
 * <pre>
 * Copyright (c) 2014 Samsung SDS.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of Samsung
 * SDS. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Samsung SDS.
 *
 * Author	          : Takkies
 * Date   	          : 2014. 04. 01.
 * Description 	  : 
 * </pre>
 */
package com.sds.acube.ndisc.xmigration;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.acube.jstor.JSTORApi;
import com.sds.acube.jstor.JSTORApiFactory;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.xmigration.db.XNDiscMigConnDB;
import com.sds.acube.ndisc.xmigration.db.XNDiscMigDBUtil;
import com.sds.acube.ndisc.xmigration.db.XNDiscMigData;
import com.sds.acube.ndisc.xmigration.db.XNDiscMigItem;
import com.sds.acube.ndisc.xmigration.util.XNDiscMigConfig;
import com.sds.acube.ndisc.xmigration.util.XNDiscMigUtil;
import com.sds.acube.ndisc.xnapi.XNApi;

/**
 * 
 * @author Takkies
 *
 */
public class XNDiscMigration {

	/* XMigration 버전정보 */
	private static String XMIGRATION_VERSION;
	
	/* logger 객체 */
	private static Logger logger = Logger.getLogger(XNDiscMigration.class);

	/* Migration 할 대상 JSTOR Api 객체 */
	private JSTORApi jSTOR = null;

	/* Migration 할 타겟 api 객체 */
	private XNApi xNApi = null;

	/* Migration DB 객체 */
	private XNDiscMigConnDB migdb = null;

	/* Migration 할 대상 JSTOR 연결 아이디 */
	private int jstorConnID = -1;

	/* Migration 할 타겟 연결 아이디 */
	private int ndiscConnID = -1;

	/* Migration 에 사용할 테이블 명 */
	private String MIG_TABLE = XNDiscMigConfig.getString("mig-table", "S2N_MIG");

	/* Migration 결과 메시지 */
	private StringBuilder msg = new StringBuilder();

	/* OS 별 라인 피드 */
	protected static String LINE_SEPERATOR = System.getProperty("line.separator");
	
	static {
		XMIGRATION_VERSION = "XMigration " + XNDiscMigUtil.getXMigrationVersion() + "(" + XNDiscMigUtil.getXMigrationPublshingDate() + ")";
		StringBuilder smsg = new StringBuilder(LINE_SEPERATOR);
		smsg.append("┌").append(StringUtils.rightPad("", 60, "-")).append("┐").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Company        : SAMSUNG SDS", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Product Name   : ACUBE XMIGRATION", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.rightPad("   Version        : " + XMIGRATION_VERSION, 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("│").append(StringUtils.center("", 60, " ")).append("│").append(LINE_SEPERATOR);
		smsg.append("└").append(StringUtils.rightPad("", 60, "-")).append("┘").append(LINE_SEPERATOR);
		System.out.println(smsg.toString());
	}
	
	public static void main(String args[]) {
		XNDiscMigConnDB db = XNDiscMigDBUtil.create(true);
		try {
			XNDiscMigration mig = new XNDiscMigration(db);
			if (mig.connect()) {
				logger.debug("*** jstor, ndisc connection successfully!!!");
				if (mig.execute()) {
					logger.debug("*** migration successfully!!!");
					mig.disconnect();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				db.commit();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 생성자
	 * 
	 * @param migdb Migration 처리 DB 객체
	 */
	public XNDiscMigration(XNDiscMigConnDB migdb) {
		this.migdb = migdb;
	}

	/**
	 * Migration 대상, 타겟에 연결하기
	 * 
	 * @return 연결 성공하면 true, 실패하면 false
	 */
	public boolean connect() {
		boolean rtn = false;
		rtn = getSTORConnection();
		if (rtn) {
			rtn = getXNApiConnection();
		}
		return rtn;
	}

	/**
	 * Migration 실행
	 * 
	 * @return Migration 성공이면 true, 실패이면 false
	 */
	public boolean execute() {
		boolean rtn = false;
		try {
			if (migdb == null) {
				throw new Exception("Connection is null!!!");
			}
			if (!migdb.hasTable(MIG_TABLE)) {
				logger.debug("*** create migration table[" + MIG_TABLE + "]");
				migdb.appendSQL(XNDiscMigConnDB.getMigrationTable());
				rtn = migdb.queryUpdate() >= 0;
				if (rtn) {
					migdb.commit();
				}
			}
			if (rtn) {
				migdb.init();
				migdb.appendSQL(XNDiscMigConnDB.getJSTORListCount());
				int total_data_count = migdb.queryInt(0); // 전체 수
				int process_once_count = XNDiscMigConfig.getInt("stor-get-count", 100);
				logger.debug("*** total migration count : " + total_data_count);
				logger.debug("*** process once count : " + process_once_count);
				if (total_data_count > 0) {
					int do_count = (int) Math.ceil((float) total_data_count / (float) process_once_count);
					do_count = (do_count == 0) ? 1 : do_count; // 처리할 개수는 항상 1이상임.
					logger.debug("*** do count : " + do_count);
					if (do_count > 0) {
						int start = 0;
						int end = 0;
						for (int i = 0; i < do_count; i++) {
							XNDiscMigUtil.clearConsoleOutput();
							msg.setLength(0);
							start = i * process_once_count; // 페이징 시 시작 구간
							end = (i + 1) * process_once_count; // 페이징 시 끝 구간
							logger.debug("*** migration start : " + start + ", end : " + end);
							msg.append("*** jstor to xndisc migration paging list from " + start + " to " + end + XNDiscMigUtil.LINE_SEPERATOR);
							rtn = migration(start, end);
							if (rtn) {
								System.out.println(msg.toString());
							} else {
								System.out.println("*** Fail to migration jstor to xndisc!!!");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * Migration 실제 처리부<br>
	 * Migration 은 쿼리 페이징을 이용하여 처리함.<br>
	 * 
	 * @param start Migration 대상 데이터 시작번호
	 * @param end Migration 대상 데이터 끝번호
	 * @return 처리 성공이면 true, 실패이면 false
	 */
	private boolean migration(int start, int end) {
		boolean rtn = false;
		try {
			ArrayList<XNDiscMigItem> list = null;
			migdb.init();
			migdb.appendSQL(XNDiscMigConnDB.getJSTORList(start, end));
			ArrayList<XNDiscMigData> jstorlist = migdb.queryList();
			if (jstorlist != null && jstorlist.size() > 0) {
				list = new ArrayList<XNDiscMigItem>();
				XNDiscMigData data = null;
				XNDiscMigItem item = null;
				int mgrs = (jstorlist == null) ? 0 : jstorlist.size();
				for (int i = 0; i < mgrs; i++) {
					item = new XNDiscMigItem();
					data = (XNDiscMigData) jstorlist.get(i);
					item.setFileId(XNDiscMigUtil.getString(data.getString("FLE_ID")));
					item.setFileMediaId(data.getInt("FLE_MDID"));
					item.setFileName(XNDiscMigUtil.getString(data.getString("FLE_NAME")));
					item.setFileRegDate(XNDiscMigUtil.getString(data.getString("FLE_CRTDT")));
					item.setFileSize(data.getInt("FLE_SIZE"));
					item.setFileStatus(XNDiscMigUtil.getString(data.getString("FLE_STATUS")));
					list.add(item);
				}
				if (list != null && list.size() > 0) {
					rtn = downloadJSTOR(list);
					if (rtn) {
						logger.debug("*** jstor down load : " + rtn);
						rtn = uploadNDISC(list);
						logger.debug("*** ndisc upload load : " + rtn);
						if (rtn) {
							rtn = removeTempFiles(list);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * Migration 대상, 타겟 연결 끊기
	 */
	public void disconnect() {
		try {
			jSTOR.JSTOR_Disconnect(getJstorConnID());
			xNApi.XNDisc_Disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Migration 대상 JSTOR 연결하기
	 * 
	 * @return 연결되면 true, 연결 실패하면 false
	 */
	private boolean getSTORConnection() {
		boolean rtn = true;
		try {
			System.setProperty("jstor_api_type", XNDiscMigConfig.getString("stor-api-type"));
			System.setProperty("jstor_svr_type", XNDiscMigConfig.getString("stor-svr-type"));

			String stor_host = XNDiscMigConfig.getString("stor-host");
			int stor_port = XNDiscMigConfig.getInt("stor-port");

			logger.debug("*** jstor ip : " + stor_host + ", port : " + stor_port);

			JSTORApiFactory jsFactory = new JSTORApiFactory();
			jSTOR = jsFactory.getInstance();

			setJstorConnID(jSTOR.JSTOR_Connect(stor_host, stor_port));
			if (getJstorConnID() < 0) {
				rtn = false;
				throw new Exception("stor connection fail - " + jSTOR.JSTOR_getErrCode() + ", " + jSTOR.JSTOR_getErrMsg());
			}
		} catch (Exception e) {
			rtn = false;
			logger.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * Migration 타겟에 연결하기
	 * 
	 * @return 연결 성공하면 true, 실패하면 false
	 */
	private boolean getXNApiConnection() {
		boolean rtn = true;
		try {
			System.setProperty("xnapi_ssl", XNDiscMigConfig.getString("xnapi-ssl"));
			System.setProperty("xnapi_keyfile", XNDiscMigConfig.getString("xnapi-keyfile"));
			System.setProperty("xnapi_keypwd", XNDiscMigConfig.getString("xnapi-keypwd"));
			String ndisc_host = XNDiscMigConfig.getString("ndisc-host");
			int ndisc_port = XNDiscMigConfig.getInt("ndisc-port");

			logger.debug("*** ndisc ip : " + ndisc_host + ", port : " + ndisc_port);

			xNApi = new XNApi(false);
			setNdiscConnID(xNApi.XNDisc_Connect(ndisc_host, ndisc_port));
			if (getNdiscConnID() < 0) {
				rtn = false;
				throw new Exception("can not connect to ndisc server");
			}
		} catch (Exception e) {
			rtn = false;
			logger.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * Migration 대상 파일 다운로드 하기
	 * 
	 * @param list Migration 대상 정보
	 * @return 다운로드 성공이면 true, 실패이면 false
	 */
	private boolean downloadJSTOR(ArrayList<XNDiscMigItem> list) {
		boolean rtn = false;
		try {
			String[][] fileGetInfo = new String[list.size()][3];
			XNDiscMigItem item = null;
			for (int i = 0; i < list.size(); i++) {
				item = (XNDiscMigItem) list.get(i);
				fileGetInfo[i][0] = item.getFileId();
				fileGetInfo[i][1] = XNDiscMigUtil.getFilePath(item.getFileId(), XNDiscMigUtil.getRealFileName(item.getFileName()));
				fileGetInfo[i][2] = "-1"; // auto reverse filter id
			}
			rtn = jSTOR.JSTOR_FileGet(getJstorConnID(), list.size(), fileGetInfo, 0) >= 0;
			if (rtn) {
				jSTOR.JSTOR_Commit(getJstorConnID());
			}
			logger.debug("*** jstor download : " + rtn);
			msg.append("*** jstor file download result is " + rtn + XNDiscMigUtil.LINE_SEPERATOR);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * Migration 타겟에 파일 업로드하기
	 * 
	 * @param list Migration 대상 정보
	 * @return 업로드 성공이면 true, 실패이면 false
	 */
	private boolean uploadNDISC(ArrayList<XNDiscMigItem> list) {
		boolean rtn = false;
		try {
			String ndiscStatus = XNDiscMigConfig.getString("ndisc-status", "0");
			NFile[] nFile = new NFile[list.size()];
			XNDiscMigItem item = null;
			for (int i = 0; i < list.size(); i++) {
				item = (XNDiscMigItem) list.get(i);
				nFile[i] = new NFile();
				nFile[i].setId(item.getFileId());
				nFile[i].setName(XNDiscMigUtil.getFilePath(item.getFileId(), XNDiscMigUtil.getRealFileName(item.getFileName())));
				nFile[i].setCreatedDate(item.getFileRegDate());
				if ("R".equalsIgnoreCase(ndiscStatus)) {
					nFile[i].setStatType(item.getFileStatus());
				} else {
					nFile[i].setStatType(ndiscStatus);
				}
			}
			String files[] = xNApi.XNDISC_FileReg(nFile);

			if (files != null && files.length > 0) {
				logger.debug("*** ndisc upload count : " + files.length);
				msg.append("*** xndisc file upload count (" + files.length + ")" + XNDiscMigUtil.LINE_SEPERATOR);
				msg.append("*** xndisc file ids " + Arrays.toString(files) + XNDiscMigUtil.LINE_SEPERATOR);
				migdb.init();
				migdb.appendSQL(XNDiscMigConnDB.getMigrationHistory());
				for (int i = 0; i < files.length; i++) {
					migdb.setParam(1, files[i]);
					migdb.queryUpdate();
				}
				migdb.commit();
			}
			rtn = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * Migration 시 다운로드한 파일 및 디렉토리 삭제하기
	 * 
	 * @param list Migration 대상 정보
	 * @return 올바르게 삭제되면 true, 실패하면 false
	 */
	private boolean removeTempFiles(ArrayList<XNDiscMigItem> list) {
		boolean rtn = false;
		try {
			XNDiscMigItem item = null;
			for (int i = 0; i < list.size(); i++) {
				item = (XNDiscMigItem) list.get(i);
				XNDiscMigUtil.deleteTmpFiles(item.getFileId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return rtn;
	}

	/**
	 * JSTOR 연결 아이디 얻어오기
	 * 
	 * @return JSTOR 연결 아이디
	 */
	int getJstorConnID() {
		return jstorConnID;
	}

	/**
	 * JSTOR 연결 아이디 설정하기
	 * 
	 * @param jstorConnID JSTOR 연결 아이디
	 */
	void setJstorConnID(int jstorConnID) {
		this.jstorConnID = jstorConnID;
	}

	/**
	 * XNAPI 연결 아이디 얻어오기
	 * 
	 * @return XNAPI 연결 아이디
	 */
	int getNdiscConnID() {
		return ndiscConnID;
	}

	/**
	 * XNAPI 연결 아이디 설정하기
	 * 
	 * @param ndiscConnID XNAPI 연결 아이디
	 */
	void setNdiscConnID(int ndiscConnID) {
		this.ndiscConnID = ndiscConnID;
	}
}
