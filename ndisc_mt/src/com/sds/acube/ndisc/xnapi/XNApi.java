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
package com.sds.acube.ndisc.xnapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.xsocket.connection.BlockingConnection;
import org.xsocket.connection.BlockingConnectionPool;
import org.xsocket.connection.ConnectionUtils;
import org.xsocket.connection.IConnection.FlushMode;
import org.xsocket.connection.MaxConnectionsExceededException;
import org.xsocket.connection.NonBlockingConnection;
import org.xsocket.connection.NonBlockingConnectionPool;
//import org.xsocket.connection.multiplexed.MultiplexedConnection;

import com.sds.acube.cache.CacheUtil;
import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;

/**
 * XNApi 메인<br>
 * <br>
 * 환경 설정에 다음 옵션을 반드시 설정<br>
 * -- jstor_api_type : API 타입을 지정하는 옵션, XNDisc(XNApi)를 사용할 경우 반드시 xndisc 로 설정해야함.<br>
 * -- xnapi_ssl : SSL 을 사용하는 지 여부 설정, true일 경우 XNDisc Server 역시 SSL 설정이 되어 있어야함.<br>
 * -- xnapi_keyfile : SSL 인증용 키파일명, 전체 경로로 지정되거나, 클래스 패스상위 키파일명.<br>
 * -- xnapi_keypwd : SSL 인증용 키비밀번호, keytool(서버)에서 발급한 비밀번호와 일치해야함.(XNApiDesCipher로 인코딩해야함)<br>
 * ---- 환경 설정 지정시 예제)<br>
 * <code>
 * java -Djstor_api_type=xndisc -Dxnapi_ssl=true -Dxnapi_keyfile=xnapi-keyfile.jks -Dxnapi_keypwd=7eau4E0t1U0= -classpath ........
 * </code>
 * 
 * @author Takkies
 * 
 */
public class XNApi extends XNApiBase {

	/**
	 * 기본 생성자(환경설정의 서버 정보 사용, 캐시 사용않음)
	 */
	public XNApi() {
		this(false);
	}

	/**
	 * 생성자 (연결 서버 정보 제공, 캐시 사용않음)
	 * 
	 * @param HOST
	 *            HOST 정보
	 * @param PORT
	 *            PORT 정보
	 */
	public XNApi(String HOST, int PORT) {
		this(HOST, PORT, false);
	}

	/**
	 * 생성자(환경설정의 서버 정보 사용, 캐시 여부)
	 * 
	 * @param useCache
	 *            캐시 여부
	 */
	public XNApi(boolean useCache) {
		debug = logger.isDebugEnabled();
		this.HOST = XNApiConfig.getString(XNApiConfig.HOST, XNApiConfig.LOCAL_HOST);
		this.PORT = XNApiConfig.getInt(XNApiConfig.PORT, XNApiConfig.LOCAL_PORT);
		this.useCache = useCache;
		if (useCache) {
			initCache();
		}
		this.useSSL = Boolean.parseBoolean(System.getProperty(XNApiSSLUtils.XNAPI_USE_SSL_PROP, "false"));
		if (useSSL) {
			pool = new BlockingConnectionPool(XNApiSSLUtils.getSSLContext());
		} else {
			pool = new BlockingConnectionPool();
		}
		/* Multiplexing이 아닌 일반 Multi-Thread 방식일 경우 */
		pool.setMaxActive(10);
	}

	/**
	 * 생성자(연결 서버 정보 제공, 캐시 여부)
	 * 
	 * @param HOST
	 *            HOST 정보
	 * @param PORT
	 *            PORT 정보
	 * @param useCache
	 *            캐시 여부
	 */
	public XNApi(String HOST, int PORT, boolean useCache) {
		debug = logger.isDebugEnabled();
		this.HOST = HOST;
		this.PORT = PORT;
		this.useCache = useCache;
		if (useCache) {
			initCache();
		}
		this.useSSL = Boolean.parseBoolean(System.getProperty(XNApiSSLUtils.XNAPI_USE_SSL_PROP, "false"));
		if (useSSL) {
			pool = new BlockingConnectionPool(XNApiSSLUtils.getSSLContext());
		} else {
			pool = new BlockingConnectionPool();
		}
		/* Multiplexing이 아닌 일반 Multi-Thread 방식일 경우 */
		pool.setMaxActive(10);
	}

	/**
	 * XNDisc Server에 접속
	 * 
	 * @return 접속 아이디
	 * @throws NetworkException
	 *             네트워크 에러
	 */
	public int XNDisc_Connect() throws NetworkException {
		return XNDisc_Connect(HOST, PORT);
	}

	/**
	 * XNDisc Server에 접속하기
	 * 
	 * @param HOST
	 *            HOST 정보
	 * @param PORT
	 *            PORT 정보
	 * @return 접속 아이디
	 * @throws NetworkException
	 *             네트워크 에러
	 */
	public int XNDisc_Connect(String HOST, int PORT) throws NetworkException {
		int connection_port = Connect(HOST, PORT);
		if (connection_port < 0) {
			boolean use_failover = XNApiConfig.getBoolean(XNApiConfig.NDISC_FAILOVER_APPLY);
			if (use_failover) {
				String failover_hosts[] = XNApiConfig.getArray(XNApiConfig.NDISC_FAILOVER_HOSTS, XNApiConfig.NDISC_FAILOVER_CATEGORY);
				if (failover_hosts != null && failover_hosts.length > 0) {
					for (String hosts : failover_hosts) {
						String fo_hostinfo[] = hosts.split(":");
						String fo_host = fo_hostinfo[0];
						int fo_port = Integer.parseInt(fo_hostinfo[1]);
						if (fo_host.equalsIgnoreCase(HOST) && fo_port == PORT) {
							if (debug) {
								logger.debug("XNDisc Server Connection already!!!(" + hosts + ")");
							}
							continue;
						}
						connection_port = Connect(fo_host, fo_port);
						if (connection_port > 0) { // fail over 정보 중 연결 가능한 곳에 연결되면 나머지는 무시함.
							if (debug) {
								logger.debug("XNDisc Server FailOver Connection success!!!( " + hosts + ")");
							}
							break;
						} else {
							logger.error("XNDisc Server FailOver Connection fail!!!(" + hosts + ")");
						}
					}
				}
				if (connection_port < 0) {
					String errmsg = "XNDisc Server FailOver Connection fail!!!(" + HOST + ":" + PORT + ")";
					logger.error(errmsg);
					throw new NetworkException(errmsg);
				}
			} else {
				String errmsg = "XNDisc Server Connection fail!!!(" + HOST + ":" + PORT + ")";
				logger.error(errmsg);
				throw new NetworkException(errmsg);
			}
		}
		return connection_port;
	}

	/**
	 * XNDisc Server 접속 끊기
	 * 
	 * @throws NetworkException
	 *             네트워크 에러
	 * @throws IOException
	 *             파일 에러
	 */
	public void XNDisc_Disconnect() throws NetworkException, IOException {
		if (connection.isOpen()) {
			// 실제 Quit Info를 서버에 전송하는 부분은 의미없음
			// if (debug) {
			// logger.debug(connection.getLocalPort() + " sendQuitInfo start");
			// }
			// StringBuffer strBuf = new StringBuffer();
			// strBuf.append(XNApiUtils.getStatus(XNApiStatus.QUIT.getStatus()));
			// strBuf.append(XNApiConfig.DELIM_STR);
			// strBuf.append("0");
			// strBuf.append(XNApiConfig.DELIM_STR);
			// String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			// try {
			// connection.write(sendmsg);
			// connection.flush();
			// } catch (BufferOverflowException e) {
			// logger.error(XNApiUtils.printStackTrace(e));
			// } catch (IOException e) {
			// logger.error(XNApiUtils.printStackTrace(e));
			// }
			// if (debug) {
			// logger.debug(connection.getLocalPort() + " sendQuitInfo end");
			// }

			try {
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] Client Disconnect");
				}
				connection.close();
			} catch (Exception e) {
				destroyConnectionPool();
				throw new NetworkException(XNApiUtils.printStackTrace(e));
			}
			/* Multiplexing이 아닌 일반 Multi-Thread 방식일 경우 */
			destroyConnectionPool();
		}
	}

	/**
	 * 파일 등록하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 등록된 파일 아이디 배열
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public String[] XNDISC_FileReg(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String fileids[] = null;
		connection.setAutoflush(false);
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeRegInfo start");
			}
			nFile = XNApiUtils.makeRegInfo(nFile);
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeRegInfo end");
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendRegInfo start");
			}

			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.REG.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(nFile.length);
			strBuf.append(XNApiConfig.DELIM_STR);
			for (NFile file : nFile) {
				strBuf.append(XNApiUtils.getNameFormatString(file.getName()));
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getSize());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getVolumeId());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getStatType());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getId());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getCreatedDate());
				strBuf.append(XNApiConfig.DELIM_STR);
			}

			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendRegInfo end");
			}

			System.out.println("Kiyong TEST XNapi.java XNDISC_FileReg 1 XNApiConfig.REPLY_BUFFER_SIZE ::: " + XNApiConfig.REPLY_BUFFER_SIZE);
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			System.out.println("Kiyong TEST XNapi.java XNDISC_FileReg 2 rcvmsg ::: " + rcvmsg);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendFileExNIO start");
				}
				FileChannel fc = null;
				FileInputStream fis = null;
				for (NFile file : nFile) {
					fis = new FileInputStream(file.getName());
					fc = fis.getChannel();
					connection.transferFrom(fc, file.getSize());
					fc.close();
					fis.close();
				}
				connection.flush();
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendFileExNIO end");
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] getNDiscFileID start");
				}
				rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
				fileids = XNApiUtils.getFileIds(rcvmsg, nFile.length);
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] getNDiscFileID end");
				}
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return fileids;
	}

	/**
	 * 파일 등록하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 등록된 파일 아이디 배열
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public String[] XNDISC_FileRegEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String regfiles[] = null;
		try {
			XNDisc_Connect();
			regfiles = XNDISC_FileReg(nFile);
			XNDisc_Disconnect();
		} catch (IOException e) {
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return regfiles;
	}

	/**
	 * 파일 다운로드 하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileGet(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			if (useCache) {
				Vector<NFile> vec = new Vector<NFile>();
				for (int i = 0; i < nFile.length; i++) {
					String cachePath = getCache(nFile[i].getId());
					if (null == cachePath) {
						vec.add(nFile[i]);
					} else {
						CacheUtil.copyFile(cachePath, nFile[i].getName());
					}
				}
				nFile = new NFile[vec.size()];
				Enumeration<NFile> enu = vec.elements();
				for (int i = 0; enu.hasMoreElements();) {
					nFile[i] = (NFile) enu.nextElement();
				}
			}

			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendFetchInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.GET.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(nFile.length);
			strBuf.append(XNApiConfig.DELIM_STR);
			for (NFile file : nFile) {
				strBuf.append(XNApiUtils.getNameFormatString(file.getId()));
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getStatType());
				strBuf.append(XNApiConfig.DELIM_STR);
			}
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendFetchInfo end");
			}

			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] receiveFileExNIO start");
				}
				rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
				StringTokenizer st = new StringTokenizer(rcvmsg, XNApiConfig.DELIM_STR);
				for (NFile file : nFile) {
					file.setSize(Integer.parseInt(st.nextToken().trim()));
				}
				FileChannel fc = null;
				FileOutputStream fos = null;
				for (NFile file : nFile) {
					fos = new FileOutputStream(file.getName());
					fc = fos.getChannel();
					connection.transferTo(fc, file.getSize());
					fc.close();
					fos.close();
				}
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] receiveFileExNIO end");
				}
				rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
				rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
				if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
					rtn = true;
				}
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return rtn;
	}

	/**
	 * 파일 다운로드 하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileGetEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			XNDisc_Connect();
			rtn = XNDISC_FileGet(nFile);
			XNDisc_Disconnect();
		} catch (IOException e) {
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return rtn;
	}

	/**
	 * 파일 교체 하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileRep(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeRepInfo start");
			}
			for (NFile nfile : nFile) {
				File file = new File(nfile.getName());
				if (!file.exists()) {
					throw new FileException("*** File not found : " + nfile.getName());
				}
				nfile.setSize((int) file.length());
				if (null == nfile.getModifiedDate() || "".equals(nfile.getModifiedDate())) {
					nfile.setModifiedDate(XNApiConfig.NDISC_NA_RESERV);
				}
			}
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeRepInfo end");
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendRepInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.REP.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(nFile.length);
			strBuf.append(XNApiConfig.DELIM_STR);
			for (NFile file : nFile) {
				strBuf.append(XNApiUtils.getNameFormatString(file.getName()));
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getSize());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getStatType());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getId());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getModifiedDate());
				strBuf.append(XNApiConfig.DELIM_STR);
			}
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendRepInfo end");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendFileExNIO start");
				}
				FileChannel fc = null;
				FileInputStream fis = null;
				for (NFile file : nFile) {
					fis = new FileInputStream(file.getName());
					fc = fis.getChannel();
					connection.transferFrom(fc);
					fc.close();
					fis.close();
				}
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendFileExNIO end");
				}
				rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
				rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
				if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
					rtn = true;
				}
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return rtn;
	}

	/**
	 * 파일 교체 하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileRepEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			XNDisc_Connect();
			rtn = XNDISC_FileRep(nFile);
			XNDisc_Disconnect();
		} catch (IOException e) {
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return rtn;
	}

	/**
	 * 파일 삭제하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileDel(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendDelInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.DEL.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(nFile.length);
			strBuf.append(XNApiConfig.DELIM_STR);
			for (int i = 0; i < nFile.length; i++) {
				strBuf.append(XNApiUtils.getNameFormatString(nFile[i].getId()));
				strBuf.append(XNApiConfig.DELIM_STR);
			}
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				rtn = true;
			}
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendDelInfo end");
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return rtn;
	}

	/**
	 * 파일 삭제하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileDelEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			XNDisc_Connect();
			rtn = XNDISC_FileDel(nFile);
			XNDisc_Disconnect();
		} catch (IOException e) {
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return rtn;
	}

	/**
	 * 파일 복사하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 복사된 파일 아이디 배열
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public String[] XNDISC_FileCpy(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String[] fileIds = null;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeCpyInfo start");
			}
			for (NFile file : nFile) {
				if (null == file.getCreatedDate() || "".equals(file.getCreatedDate())) {
					file.setCreatedDate(XNApiConfig.NDISC_NA_RESERV);
				}
			}
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeCpyInfo end");
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendCpyInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.CPY.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(nFile.length);
			strBuf.append(XNApiConfig.DELIM_STR);
			for (NFile file : nFile) {
				strBuf.append(file.getVolumeId());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getStatType());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getId());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getCreatedDate());
				strBuf.append(XNApiConfig.DELIM_STR);
			}
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendCpyInfo end");
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] getNDiscFileID start");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			StringTokenizer st = new StringTokenizer(rcvmsg, XNApiConfig.DELIM_STR);
			if (XNApiConfig.ERROR.equals(st.nextToken())) {
				throw new NDiscException(st.nextToken().trim());
			}
			fileIds = new String[nFile.length];
			for (int i = 0; i < nFile.length; i++) {
				fileIds[i] = st.nextToken().trim();
			}
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] getNDiscFileID end");
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return fileIds;
	}

	/**
	 * 파일 복사하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 복사된 파일 아이디 배열
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public String[] XNDISC_FileCpyEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String cpyfiles[] = null;
		try {
			XNDisc_Connect();
			cpyfiles = XNDISC_FileCpy(nFile);
			XNDisc_Disconnect();
		} catch (IOException e) {
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return cpyfiles;
	}

	/**
	 * 파일 이동하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileMov(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeMovInfo start");
			}
			for (NFile file : nFile) {
				if (null == file.getCreatedDate() || "".equals(file.getCreatedDate())) {
					file.setCreatedDate(XNApiConfig.NDISC_NA_RESERV);
				}
			}
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] makeMovInfo end");
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendMovInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.MOV.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(nFile.length);
			strBuf.append(XNApiConfig.DELIM_STR);
			for (NFile file : nFile) {
				strBuf.append(file.getVolumeId());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getStatType());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getId());
				strBuf.append(XNApiConfig.DELIM_STR);
				strBuf.append(file.getCreatedDate());
				strBuf.append(XNApiConfig.DELIM_STR);
			}
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendMovInfo end");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				rtn = true;
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return rtn;
	}

	/**
	 * 파일 이동하기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_FileMovEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			XNDisc_Connect();
			rtn = XNDISC_FileMov(nFile);
			XNDisc_Disconnect();
		} catch (IOException e) {
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return rtn;
	}

	/**
	 * 파일 정보 가져오기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return NFile 정보 배열
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public NFile[] XNDISC_FileInfo(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		NFile nfiles[] = null;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendQueryNFileInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.FILEINFO.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(nFile.length);
			strBuf.append(XNApiConfig.DELIM_STR);
			for (NFile file : nFile) {
				strBuf.append(file.getId());
				strBuf.append(XNApiConfig.DELIM_STR);
			}
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendQueryNFileInfo end");
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] getNDiscNFileInfo start");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			StringTokenizer st = new StringTokenizer(rcvmsg, XNApiConfig.DELIM_STR);
			if (XNApiConfig.ERROR.equals(st.nextToken())) {
				throw new NDiscException(st.nextToken().trim());
			}
			nfiles = new NFile[nFile.length];
			for (int i = 0; i < nFile.length; i++) {
				nfiles[i] = new NFile();
				nfiles[i].setId(st.nextToken().trim());
				nfiles[i].setName(st.nextToken().trim());
				nfiles[i].setSize(Integer.parseInt(st.nextToken().trim()));
				nfiles[i].setCreatedDate(st.nextToken().trim());
				nfiles[i].setModifiedDate(st.nextToken().trim());
				nfiles[i].setMediaId(Integer.parseInt(st.nextToken().trim()));
				nfiles[i].setStatType(st.nextToken().trim());
				nfiles[i].setStoragePath(st.nextToken().trim());
			}
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] getNDiscNFileInfo end");
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return nfiles;
	}

	/**
	 * 파일 정보 가져오기
	 * 
	 * @param nFile
	 *            NFile 정보 배열
	 * @return NFile 정보 배열
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public NFile[] XNDISC_FileInfoEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		NFile nfiles[] = null;
		try {
			XNDisc_Connect();
			nfiles = XNDISC_FileInfo(nFile);
			XNDisc_Disconnect();
		} catch (IOException e) {
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return nfiles;
	}

	/**
	 * 볼륨 정보 만들기
	 * 
	 * @param volume
	 *            볼륨 정보
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_MakeVolume(Volume volume) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendVolumeInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.MKVOLUME.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append("1");
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(volume.getName());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(volume.getAccessable());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(volume.getDesc());
			strBuf.append(XNApiConfig.DELIM_STR);
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendVolumeInfo end");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				rtn = true;
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return rtn;
	}

	/**
	 * 볼륨 삭제하기
	 * 
	 * @param volume
	 *            볼륨 정보
	 * @return 삭제 결과(성공이면 0)
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public int XNDISC_DeleteVolume(Volume volume) throws FileException, NetworkException, NDiscException {
		int rtn = Integer.parseInt(XNApiConfig.ERROR);
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendVolumeInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.RMVOLUME.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append("1");
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(volume.getName());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(volume.getAccessable());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(volume.getDesc());
			strBuf.append(XNApiConfig.DELIM_STR);
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendVolumeInfo end");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				rtn = Integer.parseInt(XNApiConfig.NO_ERROR);
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return rtn;
	}

	/**
	 * 미디어 만들기
	 * 
	 * @param media
	 *            미디어 정보
	 * @return 성공이면 true, 실패이면 false
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	public boolean XNDISC_MakeMedia(Media media) throws FileException, NetworkException, NDiscException {
		boolean rtn = false;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendMediaInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.MKMEDIA.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append("1");
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(media.getName());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(media.getType());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(media.getPath());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(media.getDesc());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(media.getMaxSize());
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append(media.getVolumeId());
			strBuf.append(XNApiConfig.DELIM_STR);
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendMediaInfo end");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				rtn = true;
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return rtn;
	}

	/**
	 * 서버환경정보 얻어오기
	 * 
	 * @return 환경정보
	 * @throws FileException
	 *             파일 처리 에러
	 * @throws NetworkException
	 *             네트워크 통신 에러
	 * @throws NDiscException
	 *             NDisc 에러
	 */
	@SuppressWarnings("rawtypes")
	public HashMap XNDISC_GetServerConfigure() throws FileException, NetworkException, NDiscException {
		HashMap hash = null;
		try {
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendGetConfInfo start");
			}
			StringBuffer strBuf = new StringBuffer();
			strBuf.append(XNApiUtils.getStatus(XNApiStatus.GETCONF.getStatus()));
			strBuf.append(XNApiConfig.DELIM_STR);
			strBuf.append("1");
			strBuf.append(XNApiConfig.DELIM_STR);
			String sendmsg = XNApiUtils.getFormatString(strBuf.toString(), XNApiConfig.INIT_BUFFER_SIZE);
			connection.write(sendmsg);
			connection.flush();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] sendGetConfInfo end");
			}
			String rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
			rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
			if (XNApiConfig.NO_ERROR.equals(rcvmsg)) {
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] receiveConfiguration start");
				}
				FileChannel fc = null;
				FileOutputStream fos = null;
				FileInputStream fis = null;
				ObjectInputStream in = null;
				String file = ".".concat(File.separator).concat("ndisc-server-config.tmp");
				int size = -1;
				rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
				StringTokenizer st = new StringTokenizer(rcvmsg, XNApiConfig.DELIM_STR);
				size = Integer.parseInt(st.nextToken().trim());
				fos = new FileOutputStream(file);
				fc = fos.getChannel();

				connection.transferTo(fc, size);

				fc.close();
				fos.close();

				fis = new FileInputStream(file);
				in = new ObjectInputStream(fis);
				hash = (HashMap) in.readObject();

				in.close();
				fis.close();
				if (debug) {
					logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] receiveConfiguration end");
				}
				rcvmsg = connection.readStringByLength(XNApiConfig.REPLY_BUFFER_SIZE);
				rcvmsg = XNApiUtils.getReplyMessage(rcvmsg);
				if (XNApiConfig.ERROR.equals(rcvmsg)) {
					hash = null;
				}
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
		return hash;
	}

	/**
	 * XNDisc Server에 접속<br>
	 * Multiplexing을 사용하지 않을 경우 {@link MultiplexedConnection} 대신 <br>
	 * {@link NonBlockingConnection} 또는 {@link BlockingConnection} 을 사용 해야함.<br>
	 * 이 경우 Pool을 사용할 수 있음.<br>
	 * {@link NonBlockingConnectionPool} 또는 {@link BlockingConnectionPool} 참고<br>
	 * 
	 * @param HOST
	 *            HOST 정보
	 * @param PORT
	 *            PORT 정보
	 * @return 접속 아이디 정보
	 */
	private int Connect(String HOST, int PORT) {
		int connection_port = -1;
		try {

			InetAddress ia = InetAddress.getByName(HOST);
			if (ia == null) {
				throw new NDiscException("*** XNDisc Server HOST Information is invalid or null !!!");
			}

			if (useSSL) {
				// mxconnection = new MultiplexedConnection(new NonBlockingConnection(ia, PORT, XNApiSSLUtils.getSSLContext(), true));
				connection = pool.getBlockingConnection(ia, PORT, true);
			} else {
				// mxconnection = new MultiplexedConnection(new NonBlockingConnection(ia, PORT));
				connection = pool.getBlockingConnection(ia, PORT);
			}

			/* Multiplexing이 아닌 일반 Multi-Thread 방식일 경우 */

			connection = ConnectionUtils.synchronizedConnection(connection);

			// String pipelineId = mxconnection.createPipeline();
			// connection = mxconnection.getBlockingPipeline(pipelineId);
			connection.setFlushmode(FlushMode.ASYNC);
			connection_port = connection.getLocalPort();
			if (debug) {
				logger.debug("*** [" + connection.getId() + "] [" + connection.getLocalAddress().getHostAddress() + ":" + connection.getLocalPort() + "] Connected Client");
			}
		} catch (MaxConnectionsExceededException e) {
			destroyConnectionPool();
			logger.error(XNApiUtils.printStackTrace(e));
		} catch (SocketTimeoutException e) {
			destroyConnectionPool();
			logger.error(XNApiUtils.printStackTrace(e));
		} catch (IOException e) {
			destroyConnectionPool();
			logger.error(XNApiUtils.printStackTrace(e));
		} catch (NDiscException e) {
			destroyConnectionPool();
			logger.error(XNApiUtils.printStackTrace(e));
		}
		return connection_port;
	}
}
