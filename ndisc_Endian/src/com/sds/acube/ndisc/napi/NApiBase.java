package com.sds.acube.ndisc.napi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.sds.acube.cache.CacheConfig;
import com.sds.acube.cache.iface.ICache;
import com.sds.acube.ndisc.common.exception.FileException;
import com.sds.acube.ndisc.common.exception.NDiscException;
import com.sds.acube.ndisc.model.Media;
import com.sds.acube.ndisc.model.NFile;
import com.sds.acube.ndisc.model.Volume;
import com.sds.acube.ndisc.mts.common.NDCommon;
import com.sds.acube.ndisc.mts.common.NDConstant;

public class NApiBase {

	public NApiBase() {
		;
	}

	protected SocketChannel sc = null;

	protected String HOST = null;

	protected int PORT = -1;

	private ICache cacheService = null;

	protected boolean IsCacheUse = false;

	protected int m_nLocalPort = -1;

	protected NFile[] makeRegInfo(NFile[] nFile) throws Exception {

		try {
			for (int i = 0; i < nFile.length; i++) {
				File file = new File(nFile[i].getName());

				if (!file.exists()) {
					throw new FileException("file not found : " + nFile[i].getName());
				}

				nFile[i].setSize((int) file.length());

				if (null == nFile[i].getId() || "".equals(nFile[i].getId())) {
					nFile[i].setId(NDConstant.NDISC_NA_RESERV);
				}

				if (null == nFile[i].getCreatedDate() || "".equals(nFile[i].getCreatedDate())) {
					nFile[i].setCreatedDate(NDConstant.NDISC_NA_RESERV);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}

		return nFile;
	}

	protected NFile[] makeFetchInfo(NFile[] nFile) throws Exception {
		try {
			;
		} catch (Exception e) {
			throw e;
		} finally {

		}
		return nFile;
	}

	protected NFile[] makeRepInfo(NFile[] nFile) throws Exception {

		try {
			for (int i = 0; i < nFile.length; i++) {
				File file = new File(nFile[i].getName());

				if (!file.exists()) {
					throw new FileException("file not found : " + nFile[i].getName());
				}

				nFile[i].setSize((int) file.length());

				if (null == nFile[i].getModifiedDate() || "".equals(nFile[i].getModifiedDate())) {
					nFile[i].setModifiedDate(NDConstant.NDISC_NA_RESERV);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}

		return nFile;
	}

	protected NFile[] makeDelInfo(NFile[] nFile) throws Exception {
		try {
			;
		} catch (Exception e) {
			throw e;
		} finally {

		}
		return nFile;
	}

	protected NFile[] makeCpyInfo(NFile[] nFile) throws Exception {
		try {
			for (int i = 0; i < nFile.length; i++) {

				if (null == nFile[i].getCreatedDate() || "".equals(nFile[i].getCreatedDate())) {
					nFile[i].setCreatedDate(NDConstant.NDISC_NA_RESERV);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {

		}
		return nFile;
	}

	protected NFile[] makeMovInfo(NFile[] nFile) throws Exception {
		try {
			for (int i = 0; i < nFile.length; i++) {

				if (null == nFile[i].getCreatedDate() || "".equals(nFile[i].getCreatedDate())) {
					nFile[i].setCreatedDate(NDConstant.NDISC_NA_RESERV);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {

		}
		return nFile;
	}

	protected NFile[] makeQueryNFileInfo(NFile[] nFile) throws Exception {
		try {
			;
		} catch (Exception e) {
			throw e;
		} finally {

		}
		return nFile;
	}

	protected void sendQuitInfo() throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = 0;
			strBuf.append(NDConstant.SERVICE_STAT_QUIT);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			sendInfo(strBuf);

		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendRegInfo(NFile[] nFile) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = nFile.length;
			strBuf.append(NDConstant.SERVICE_STAT_FILEREG);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			// fle_name|fle_size|fle_volId|fle_statType|fle_id|fle_crtdt|
			for (int i = 0; i < nCount; i++) {
				// # 1
				strBuf.append(getNameFormatString(nFile[i].getName()));
				strBuf.append(NDConstant.DELIM_STR);
				// # 2
				strBuf.append(nFile[i].getSize());
				strBuf.append(NDConstant.DELIM_STR);
				// # 3
				strBuf.append(nFile[i].getVolumeId());
				strBuf.append(NDConstant.DELIM_STR);
				// # 4
				strBuf.append(nFile[i].getStatType());
				strBuf.append(NDConstant.DELIM_STR);
				// # 5
				strBuf.append(nFile[i].getId());
				strBuf.append(NDConstant.DELIM_STR);
				// # 6
				strBuf.append(nFile[i].getCreatedDate());
				strBuf.append(NDConstant.DELIM_STR);
			}

			sendInfo(strBuf);

		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendFetchInfo(NFile[] nFile) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = nFile.length;
			strBuf.append(NDConstant.SERVICE_STAT_FILEGET);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			// file_id|file_statType
			for (int i = 0; i < nCount; i++) {
				System.out.println("MRJH Debug nFile[i].getId() : " + nFile[i].getId());

				strBuf.append(getNameFormatString(nFile[i].getId()));
				strBuf.append(NDConstant.DELIM_STR);
				strBuf.append(nFile[i].getStatType());
				strBuf.append(NDConstant.DELIM_STR);
			}

			sendInfo(strBuf);

		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendRepInfo(NFile[] nFile) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = nFile.length;
			strBuf.append(NDConstant.SERVICE_STAT_FILEREP);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			// fle_name|fle_size|fle_statType|fle_id|fle_moddt|
			for (int i = 0; i < nCount; i++) {
				// # 1
				strBuf.append(getNameFormatString(nFile[i].getName()));
				strBuf.append(NDConstant.DELIM_STR);
				// # 2
				strBuf.append(nFile[i].getSize());
				strBuf.append(NDConstant.DELIM_STR);
				// # 3
				strBuf.append(nFile[i].getStatType());
				strBuf.append(NDConstant.DELIM_STR);
				// # 4
				strBuf.append(nFile[i].getId());
				strBuf.append(NDConstant.DELIM_STR);
				// # 5
				strBuf.append(nFile[i].getModifiedDate());
				strBuf.append(NDConstant.DELIM_STR);
			}

			sendInfo(strBuf);

		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendDelInfo(NFile[] nFile) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = nFile.length;
			strBuf.append(NDConstant.SERVICE_STAT_FILEDEL);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			// fle_id
			for (int i = 0; i < nCount; i++) {
				strBuf.append(getNameFormatString(nFile[i].getId()));
				strBuf.append(NDConstant.DELIM_STR);
			}

			sendInfo(strBuf);
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendCpyInfo(NFile[] nFile) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = nFile.length;
			strBuf.append(NDConstant.SERVICE_STAT_FILECPY);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			// fle_volId|fle_statType|fle_id|fle_crtdt|
			for (int i = 0; i < nCount; i++) {
				// # 1
				strBuf.append(nFile[i].getVolumeId());
				strBuf.append(NDConstant.DELIM_STR);
				// # 2
				strBuf.append(nFile[i].getStatType());
				strBuf.append(NDConstant.DELIM_STR);
				// # 3
				strBuf.append(nFile[i].getId());
				strBuf.append(NDConstant.DELIM_STR);
				// # 4
				strBuf.append(nFile[i].getCreatedDate());
				strBuf.append(NDConstant.DELIM_STR);
			}

			sendInfo(strBuf);

		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendMovInfo(NFile[] nFile) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = nFile.length;
			strBuf.append(NDConstant.SERVICE_STAT_FILEMOV);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			// fle_volId|fle_statType|fle_id|fle_crtdt|
			for (int i = 0; i < nCount; i++) {
				// # 1
				strBuf.append(nFile[i].getVolumeId());
				strBuf.append(NDConstant.DELIM_STR);
				// # 2
				strBuf.append(nFile[i].getStatType());
				strBuf.append(NDConstant.DELIM_STR);
				// # 3
				strBuf.append(nFile[i].getId());
				strBuf.append(NDConstant.DELIM_STR);
				// # 4
				strBuf.append(nFile[i].getCreatedDate());
				strBuf.append(NDConstant.DELIM_STR);
			}

			sendInfo(strBuf);

		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendQueryNFileInfo(NFile[] nFile) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = nFile.length;
			strBuf.append(NDConstant.SERVICE_STAT_FILEINFO);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			// fle_id
			for (int i = 0; i < nCount; i++) {
				strBuf.append(nFile[i].getId());
				strBuf.append(NDConstant.DELIM_STR);
			}

			sendInfo(strBuf);
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendVolumeInfo(Volume volume, String statusCode) throws Exception {
		int nCount = 1;
		StringBuffer strBuf = new StringBuffer();

		try {

			if (statusCode.equals(NDConstant.SERVICE_STAT_MKVOLUME)) { // Make
				// Volume
				strBuf.append(NDConstant.SERVICE_STAT_MKVOLUME);
			} else if (statusCode.equals(NDConstant.SERVICE_STAT_RMVOLUME)) { // Remove
				// Volume
				strBuf.append(NDConstant.SERVICE_STAT_RMVOLUME);
			}

			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(volume.getName());
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(volume.getAccessable());
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(volume.getDesc());
			strBuf.append(NDConstant.DELIM_STR);

			sendInfo(strBuf);

		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendMediaInfo(Media media) throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = 1;
			strBuf.append(NDConstant.SERVICE_STAT_MKMEDIA);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(media.getName());
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(media.getType());
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(media.getPath());
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(media.getDesc());
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(media.getMaxSize());
			strBuf.append(NDConstant.DELIM_STR);

			strBuf.append(media.getVolumeId());
			strBuf.append(NDConstant.DELIM_STR);

			sendInfo(strBuf);
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected void sendGetConfInfo() throws Exception {
		int nCount = -1;
		StringBuffer strBuf = new StringBuffer();

		try {
			nCount = 1;
			strBuf.append(NDConstant.SERVICE_STAT_GETCONF);
			strBuf.append(NDConstant.DELIM_STR);
			strBuf.append(nCount);
			strBuf.append(NDConstant.DELIM_STR);

			sendInfo(strBuf);
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	// no use currently
	protected void sendFile(NFile[] nFile) throws Exception {
		File file = null;
		ByteBuffer buffer = null;
		FileChannel inChannel = null;

		try {
			for (int i = 0; i < nFile.length; i++) {
				file = new File(nFile[i].getName());
				inChannel = new FileInputStream(file).getChannel();

				int nRemain = nFile[i].getSize();
				int nAmount = NDConstant.FILE_TRANS_BUFFER_SIZE;
				buffer = ByteBuffer.allocateDirect(nAmount);

				while (nRemain > 0) {
					if (nRemain < NDCommon.FILE_TRANS_BUFFER_SIZE) {
						nAmount = nRemain;
						buffer = ByteBuffer.allocateDirect(nAmount);
					}

					// completely read section
					while (buffer.position() < nAmount) {
						inChannel.read(buffer);
					}

					buffer.flip();

					// completely write section
					while (buffer.hasRemaining()) {
						sc.write(buffer);
					}

					nRemain -= nAmount;
					buffer.clear();
				}

				inChannel.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			deleteBuffer(buffer);
		}
	}

	// no use currently
	protected void sendFileEx(NFile[] nFile) throws Exception {
		File file = null;
		ByteBuffer buffer = null;
		FileChannel inChannel = null;

		try {
			for (int i = 0; i < nFile.length; i++) {
				file = new File(nFile[i].getName());
				inChannel = new FileInputStream(file).getChannel();

				int size = nFile[i].getSize();
				buffer = ByteBuffer.allocateDirect(size);

				// completely read section
				while (buffer.position() < size) {
					inChannel.read(buffer);
				}
				buffer.flip();

				// completely write section
				while (buffer.hasRemaining()) {
					sc.write(buffer);
				}

				buffer.clear();
				inChannel.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			deleteBuffer(buffer);
		}
	}

	// using nio transfer... function
	protected void sendFileExNIO(NFile[] nFile) throws Exception {
		FileChannel inChannel = null;

		try {
			for (int i = 0; i < nFile.length; i++) {
				inChannel = new FileInputStream(nFile[i].getName()).getChannel();

				int transfer = 0;
				while (transfer < inChannel.size()) {
					transfer += inChannel.transferTo(transfer, inChannel.size() - transfer, sc);
				}

				inChannel.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	// no use currently
	protected void receiveFile(NFile[] nFile) throws Exception {
		File file = null;
		ByteBuffer buffer = null;

		String strMsg = null;
		StringTokenizer sTK = null;

		FileChannel outChannel = null;

		try {
			// step 1 : receive file info
			strMsg = receiveReplyMsg();

			// step 2 : get file info
			sTK = new StringTokenizer(strMsg, NDConstant.DELIM_STR);
			for (int i = 0; i < nFile.length; i++) {
				nFile[i].setSize(Integer.parseInt(sTK.nextToken().trim()));
			}

			// step 3 : receive file
			for (int i = 0; i < nFile.length; i++) {
				file = new File(nFile[i].getName());
				outChannel = new FileOutputStream(file).getChannel();

				int nRemain = nFile[i].getSize();
				int nAmount = NDCommon.FILE_TRANS_BUFFER_SIZE;
				buffer = ByteBuffer.allocateDirect(nAmount);

				while (nRemain > 0) {
					if (nRemain < NDCommon.FILE_TRANS_BUFFER_SIZE) {
						nAmount = nRemain;
						buffer = ByteBuffer.allocateDirect(nAmount);
					}

					// completely read
					while (buffer.position() < nAmount) {
						sc.read(buffer);
					}

					buffer.flip();

					// completely write
					while (buffer.hasRemaining()) {
						outChannel.write(buffer);
					}

					buffer.clear();

					nRemain -= nAmount;
				}
				outChannel.close();
			}

		} catch (Exception e) {
			throw e;
		} finally {
			deleteBuffer(buffer);
		}
	}

	// no use currently
	protected void receiveFileEx(NFile[] nFile) throws Exception {
		File file = null;
		ByteBuffer buffer = null;

		String strMsg = null;
		StringTokenizer sTK = null;

		FileChannel outChannel = null;

		try {
			// step 1 : receive file info
			strMsg = receiveReplyMsg();

			// step 2 : get file info
			sTK = new StringTokenizer(strMsg, NDConstant.DELIM_STR);
			for (int i = 0; i < nFile.length; i++) {
				nFile[i].setSize(Integer.parseInt(sTK.nextToken().trim()));
			}

			// step 3 : receive file
			for (int i = 0; i < nFile.length; i++) {
				file = new File(nFile[i].getName());
				outChannel = new FileOutputStream(file).getChannel();

				int size = nFile[i].getSize();
				buffer = ByteBuffer.allocateDirect(size);

				// completely read
				while (buffer.position() < size) {
					sc.read(buffer);
				}

				buffer.flip();

				// completely write
				while (buffer.hasRemaining()) {
					outChannel.write(buffer);
				}

				buffer.clear();
				outChannel.close();
			}

		} catch (Exception e) {
			throw e;
		} finally {
			deleteBuffer(buffer);
		}
	}

	// using nio transfer... function
	protected void receiveFileExNIO(NFile[] nFile) throws Exception {
		String strMsg = null;
		StringTokenizer sTK = null;

		FileChannel outChannel = null;

		try {
			// step 1 : receive file info
			strMsg = receiveReplyMsg();

			// step 2 : get file info
			sTK = new StringTokenizer(strMsg, NDConstant.DELIM_STR);
			for (int i = 0; i < nFile.length; i++) {
				nFile[i].setSize(Integer.parseInt(sTK.nextToken().trim()));
			}

			// step 3 : receive file
			for (int i = 0; i < nFile.length; i++) {
				outChannel = new FileOutputStream(nFile[i].getName()).getChannel();

				int transfer = 0;
				while (transfer < nFile[i].getSize()) {
					transfer += outChannel.transferFrom(sc, transfer, nFile[i].getSize() - transfer);
				}

				outChannel.close();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			;
		}
	}

	protected String receiveReplyMsg() throws Exception {
		ByteBuffer buffer = null;
		String strMsg = null;

		try {
			buffer = ByteBuffer.allocateDirect(NDConstant.REPLY_BUFFER_SIZE);

			while (buffer.position() < NDConstant.REPLY_BUFFER_SIZE) {
				sc.read(buffer);
			}

			buffer.flip();
			strMsg = NDConstant.decoder.decode(buffer).toString();
			strMsg = strMsg.trim();
		} catch (Exception e) {
			System.out.println("*** [NDISC DEBUG] receiveReplyMsg Exception : Local Port No = "
					+ sc.socket().getLocalPort());
			// below will be deleted
			e.printStackTrace();
			throw e;
		} finally {
			deleteBuffer(buffer);
		}

		return strMsg;
	}

	protected HashMap receiveConfiguration() throws Exception {
		FileChannel outChannel = null;
		ObjectInputStream in = null;
		String file = "." + File.separator + "ndisc-server-config.tmp";

		String strMsg = null;
		StringTokenizer sTK = null;
		int size = -1;

		HashMap hash = null;

		try {
			strMsg = receiveReplyMsg();
			sTK = new StringTokenizer(strMsg, NDConstant.DELIM_STR);
			size = Integer.parseInt(sTK.nextToken().trim());

			outChannel = new FileOutputStream(file).getChannel();

			int transfer = 0;
			while (transfer < size) {
				transfer += outChannel.transferFrom(sc, transfer, size - transfer);
			}

			outChannel.close();

			in = new ObjectInputStream(new FileInputStream(file));

			hash = (HashMap) in.readObject();

		} catch (Exception e) {
			throw e;
		} finally {
			if (null != in) {
				in.close();
			}

			if (null != outChannel) {
				outChannel.close();
			}

			new File(file).delete();
		}

		return hash;
	}

	protected void sendStatReady() throws Exception {
		ByteBuffer buffer = null;

		try {
			buffer = ByteBuffer.allocateDirect(NDConstant.STAT_BUFFER_SIZE);
			buffer.put(NDConstant.SERVICE_STAT_READY.getBytes());
			buffer.flip();
			sc.write(buffer);
		} catch (Exception e) {
			throw e;
		} finally {
			deleteBuffer(buffer);
		}
	}

	protected void sendInfo(StringBuffer info) throws Exception {

		String strMsg = null;
		byte[] bytes = null;
		ByteBuffer buffer = null;

		try {
			/*
			 * strMsg = getFormatString(info.toString(),
			 * NDConstant.INIT_BUFFER_SIZE); bytes = strMsg.getBytes();
			 * //System.out.println("*** [NDISC DEBUG] sendInfo : strMsg = [" +
			 * strMsg + "]"); buffer =
			 * ByteBuffer.allocateDirect(NDConstant.INIT_BUFFER_SIZE);
			 * buffer.put(bytes,0,bytes.length); buffer.flip();
			 * //System.out.println("*** [NDISC DEBUG] sendInfo : buffer = [" +
			 * buffer.toString() + "]"); sc.write(buffer);//System.out.println(
			 * "*** [NDISC DEBUG] sendInfo : sc.write() success");
			 */

			// 수정 됨.. 2009.05.08
			strMsg = getFormatString(info.toString(), NDConstant.INIT_BUFFER_SIZE);
			bytes = strMsg.getBytes();
			buffer = ByteBuffer.allocateDirect(NDConstant.INIT_BUFFER_SIZE);

			int count = 0;
			int len = 0;
			while (count < bytes.length) {
				len = buffer.remaining();
				if (bytes.length - count < len) {
					len = bytes.length - count;
				}
				buffer.put(bytes, count, len);
				buffer.flip();
				sc.write(buffer);
				buffer.clear();
				count += len;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			deleteBuffer(buffer);
		}
	}

	protected void deleteBuffer(ByteBuffer buffer) throws Exception {
		if (buffer != null) {
			buffer.clear();
			buffer = null;
		}
	}

	protected String getFormatString(String data, int size) {
		String result = "";
		String tmp = data;
		int tmplen = tmp.length();

		for (int i = 0; i < (size - tmplen); i++) {
			result += "0";
		}

		result = tmp + result;

		return result;
	}

	protected String getNameFormatString(String strFilePath) {
		int nPos = strFilePath.lastIndexOf(File.separator);
		return strFilePath.substring(nPos + 1, strFilePath.length());
	}

	protected String[] getNDiscFileID(int count) throws Exception {
		String result = null;
		String[] fileID = null;
		StringTokenizer sTK = null;

		try {
			result = receiveReplyMsg();
			sTK = new StringTokenizer(result, NDConstant.DELIM_STR);
			if (NDConstant.ERROR.equals(sTK.nextToken())) {
				throw new NDiscException(sTK.nextToken().trim());
			} else {
				fileID = new String[count];
				for (int i = 0; i < count; i++) {
					fileID[i] = sTK.nextToken().trim();
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return fileID;
	}

	protected NFile[] getNDiscNFileInfo(int count) throws Exception {
		String result = null;
		NFile[] nFile = null;
		StringTokenizer sTK = null;

		try {
			result = receiveReplyMsg();

			sTK = new StringTokenizer(result, NDConstant.DELIM_STR);

			if (NDConstant.ERROR.equals(sTK.nextToken())) {
				throw new NDiscException(sTK.nextToken().trim());
			} else {
				nFile = new NFile[count];
				for (int i = 0; i < count; i++) {
					nFile[i] = new NFile();
					nFile[i].setId(sTK.nextToken().trim());
					nFile[i].setName(sTK.nextToken().trim());
					nFile[i].setSize(Integer.parseInt(sTK.nextToken().trim()));
					nFile[i].setCreatedDate(sTK.nextToken().trim());
					nFile[i].setModifiedDate(sTK.nextToken().trim());
					nFile[i].setMediaId(Integer.parseInt(sTK.nextToken().trim()));
					nFile[i].setStatType(sTK.nextToken().trim());
					nFile[i].setStoragePath(sTK.nextToken().trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return nFile;
	}

	protected String getNDiscReply() throws Exception {
		String result = null;
		StringTokenizer sTK = null;

		try {
			result = receiveReplyMsg();

			sTK = new StringTokenizer(result, NDConstant.DELIM_STR);
			result = sTK.nextToken().trim();
			if (NDConstant.ERROR.equals(result)) {
				throw new NDiscException(sTK.nextToken().trim());
			}
		} catch (Exception e) {
			throw e;
		}

		return result;
	}

	protected void initCache() {
		cacheService = CacheConfig.getService();
	}

	protected String getCache(String Id) {
		String FileCachePath = null;
		try {
			FileCachePath = cacheService.get(Id).toString();

			if (!(FileCachePath != null && new File(FileCachePath).canRead())) {
				FileCachePath = null;
			}
		} catch (Exception ex) {
			return null;
		}

		return FileCachePath;
	}

	protected void putCache(String Id, String FilePath) {
		try {
			cacheService.putInCache(Id, FilePath);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	protected void removeCache(String Id) {
		try {
			cacheService.removeFromCache(Id);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
