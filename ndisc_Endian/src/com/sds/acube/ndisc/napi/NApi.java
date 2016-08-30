package com.sds.acube.ndisc.napi;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.common.exception.NetworkException;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;

public class NApi extends NApiProcessor {

	static {
		String strVersion = "ACUBE DM NApi (2011-08-03)";

		System.out.println("\n");
		System.out.println("--------------------------------------------------");
		System.out.println("▶ Company :  SAMSUNG SDS");
		System.out.println("▶ Product Name : ACUBE DM API");
		System.out.println("▶ Version : " + strVersion);
		System.out.println("--------------------------------------------------");
		System.out.println("\n");

		System.setProperty("ndisc_napi_version", strVersion);
	}

	public NApi() {
		this.HOST = NDCommon.HOST;
		this.PORT = NDCommon.PORT;
		this.IsCacheUse = false;
		if (this.IsCacheUse) {
			initCache();
		}
	}

	public NApi(boolean IsCacheUse) {
		this.HOST = NDCommon.HOST;
		this.PORT = NDCommon.PORT;
		this.IsCacheUse = IsCacheUse;
		if (this.IsCacheUse) {
			initCache();
		}
	}

	public NApi(String HOST, int PORT) {
		this.HOST = HOST;
		this.PORT = PORT;
		this.IsCacheUse = false;
		if (this.IsCacheUse) {
			initCache();
		}
	}

	public NApi(String HOST, int PORT, boolean IsCacheUse) {
		this.HOST = HOST;
		this.PORT = PORT;
		this.IsCacheUse = IsCacheUse;
		if (this.IsCacheUse) {
			initCache();
		}
	}

	// /////////////////////////////////////////////////////////////
	// ndisc connect I
	// /////////////////////////////////////////////////////////////
	public int NDisc_Connect() throws NetworkException {
		return NDisc_Connect(this.HOST, this.PORT);
	}

	// /////////////////////////////////////////////////////////////
	// ndisc connect II
	// /////////////////////////////////////////////////////////////
	public int NDisc_Connect(String HOST, int PORT) throws NetworkException {
		int port = -1;
		String errmsg = null;

		if (NDCommon.CON_FAILOVER_STATE) {
			HOST = NDCommon.HOST_FO;
			PORT = NDCommon.PORT_FO;
		}
		
		port = Connect(HOST, PORT);
		if (port < 0) {
			if ("Y".equalsIgnoreCase(NDCommon.CON_FAILOVER_APPLY)) {
				System.out.println("[NDISC FAILOVER] NDISC Connection Fail - HOST : " + HOST + ", PORT : " + PORT);
				System.out.println("[NDISC FAILOVER] NDISC Connection FailOver Applied");
				String[] failoverTarget = NDCommon.CON_FAILOVER_TARGET.split(";");

				for (int i = 0; i < failoverTarget.length; i++) {
					System.out.println("[NDISC FAILOVER] NDISC Connection FailOver Try : " + failoverTarget[i]);
					String[] hosts = failoverTarget[i].split(":");
					
					String host_fo = hosts[0];
					int port_fo = Integer.parseInt(hosts[1]);
					if (host_fo.equalsIgnoreCase(HOST) && port_fo == PORT) {
						// 입력받은 호스트/포트와 동일할 경우 SKIP
						continue;
					}
					
					port = Connect(host_fo, port_fo);
					if (port > 0) {
						System.out.println("[NDISC FAILOVER] NDISC Connection FailOver Success : " + failoverTarget[i]);
						
						NDCommon.CON_FAILOVER_STATE = true;
						NDCommon.HOST_FO = host_fo;
						NDCommon.PORT_FO = port_fo;
						
						break;
					} else {
						System.out.println("[NDISC FAILOVER] NDISC Connection FailOver Fail : " + failoverTarget[i]);
					}
				}

				if (port < 0) {
					errmsg = "NDiscServer Connection Fail(FailOver Applied) - HOST : " + HOST + ", PORT : " + PORT;
					throw new NetworkException(errmsg);
				}
			} else {
				errmsg = "NDiscServer Connection Fail - HOST : " + HOST + ", PORT : " + PORT;
				throw new NetworkException(errmsg);
			}
		}

		return m_nLocalPort;
	}

	private int Connect(String HOST, int PORT) {
		try {
			sc = SocketChannel.open(new InetSocketAddress(HOST, PORT));

			// set blocking nio mode for napi
			// but, ndisc mts server uses non-blocking nio mode
			sc.configureBlocking(true);
			m_nLocalPort = sc.socket().getLocalPort();
		} catch (Exception e) {
			e.printStackTrace();
			m_nLocalPort = -1;
		}

		return m_nLocalPort;
	}

	// /////////////////////////////////////////////////////////////
	// ndisc disconnect
	// /////////////////////////////////////////////////////////////
	public void NDisc_Disconnect() throws NetworkException {

		try {
			if (sc.isConnected()) {
				sendQuitInfo();
				if (sc.isConnected()) {
					sc.close();
					System.out.println("*** [NDISC DEBUG] NDisc_Disconnect : Local Port No = "
							+ sc.socket().getLocalPort());
				}
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			throw new NetworkException(msg);
		}
	}

	// /////////////////////////////////////////////////////////////
	// file regist
	// /////////////////////////////////////////////////////////////
	public String[] NDISC_FileReg(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String[] fileID = null;

		try {
			fileID = registNFile(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return fileID;
	}

	// /////////////////////////////////////////////////////////////
	// file get(fetch)
	// /////////////////////////////////////////////////////////////
	public boolean NDISC_FileGet(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			bRet = fetchNFile(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return bRet;
	}

	// /////////////////////////////////////////////////////////////
	// file replace
	// /////////////////////////////////////////////////////////////
	public boolean NDISC_FileRep(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			bRet = replaceNFile(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return bRet;
	}

	// /////////////////////////////////////////////////////////////
	// file del
	// /////////////////////////////////////////////////////////////
	public boolean NDISC_FileDel(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			bRet = deleteNFile(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return bRet;
	}

	// /////////////////////////////////////////////////////////////
	// file cpy
	// /////////////////////////////////////////////////////////////
	public String[] NDISC_FileCpy(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String[] fileID = null;

		try {
			fileID = copyNFile(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return fileID;
	}

	// /////////////////////////////////////////////////////////////
	// file mov
	// /////////////////////////////////////////////////////////////
	public boolean NDISC_FileMov(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			bRet = moveNFile(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return bRet;
	}

	// /////////////////////////////////////////////////////////////
	// file info
	// /////////////////////////////////////////////////////////////
	public NFile[] NDISC_FileInfo(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		NFile[] retNFile = null;

		try {
			retNFile = queryNFileInfo(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return retNFile;
	}

	// /////////////////////////////////////////////////////////////
	// make volume
	// /////////////////////////////////////////////////////////////
	public boolean NDISC_MakeVolume(Volume volume) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			bRet = makeVolume(volume);

		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return bRet;
	}

	// /////////////////////////////////////////////////////////////
	// make media
	// /////////////////////////////////////////////////////////////
	public boolean NDISC_MakeMedia(Media media) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			bRet = makeMedia(media);

		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return bRet;
	}

	// /////////////////////////////////////////////////////////////
	// get ndisc server configuration volume
	// /////////////////////////////////////////////////////////////
	public HashMap NDISC_GetServerConfigure() throws FileException, NetworkException, NDiscException {
		HashMap hash = null;

		try {
			hash = getConfiguration();
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return hash;
	}

	// /////////////////////////////////////////////////////////////
	// below : EX Function List
	// /////////////////////////////////////////////////////////////
	public String[] NDISC_FileRegEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String[] fileID = null;

		try {
			NDisc_Connect();
			fileID = NDISC_FileReg(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} finally {
			NDisc_Disconnect();
		}

		return fileID;
	}

	public boolean NDISC_FileGetEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			NDisc_Connect();
			bRet = NDISC_FileGet(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			NDisc_Disconnect();
		}

		return bRet;
	}

	public boolean NDISC_FileRepEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			NDisc_Connect();
			bRet = NDISC_FileRep(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			NDisc_Disconnect();
		}

		return bRet;
	}

	public boolean NDISC_FileDelEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			NDisc_Connect();
			bRet = NDISC_FileDel(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			NDisc_Disconnect();
		}

		return bRet;
	}

	public String[] NDISC_FileCpyEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		String[] fileID = null;

		try {
			NDisc_Connect();
			fileID = NDISC_FileCpy(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			NDisc_Disconnect();
		}

		return fileID;
	}

	public boolean NDISC_FileMovEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		boolean bRet = false;

		try {
			NDisc_Connect();
			bRet = moveNFile(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			NDisc_Disconnect();
		}

		return bRet;
	}

	public NFile[] NDISC_FileInfoEx(NFile[] nFile) throws FileException, NetworkException, NDiscException {
		NFile[] retNFile = null;

		try {
			NDisc_Connect();
			retNFile = queryNFileInfo(nFile);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			NDisc_Disconnect();
		}

		return retNFile;
	}

	// /////////////////////////////////////////////////////////////
	// volume delete
	// /////////////////////////////////////////////////////////////
	public int NDISC_VolumeDel(Volume volume) throws FileException, NetworkException, NDiscException {
		int ret = 0;

		try {
			ret = removeVolume(volume);
		} catch (FileException e) {
			throw e;
		} catch (NetworkException e) {
			throw e;
		} catch (NDiscException e) {
			throw e;
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			;
		}

		return ret;
	}

	public void NDISC_ResetConFailOver() {
		NDCommon.CON_FAILOVER_STATE = false;
	}
}
