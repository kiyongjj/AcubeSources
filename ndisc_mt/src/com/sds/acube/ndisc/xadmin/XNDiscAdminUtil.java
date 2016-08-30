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
package com.sds.acube.ndisc.xadmin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * XNDisc Admin 유틸리티 메인
 * 
 * @author Takkies
 *
 */
public class XNDiscAdminUtil {

	/* XNDisc Admin 배포 버전정보(version.txt) */
	private static String XNDiscAdmin_PublishingVersion;

	/* XNDisc Admin 배포 날짜(version.txt) */
	private static String XNDiscAdmin_PublishingDate;
	
	/* XNDisc Admin 유틸리티 버전 정보 */
	private static String XNDISC_ADMIN_UTIL_VERSION;
	
	/* Console 삭제하기 위한 표준 Command */
	private static final String CLEAR_TERMINAL_ANSI_CMD = new String(new byte[] { 27, 91, 50, 74, 27, 91, 72 });

	/* Console 에 출력할 기본 컬럼 사이즈 */
	private static final int PRINT_COLUMN_SIZE = 100;

	/* 로그 출력 옵션 */
	private static final boolean printlog = false;

	/* 로거 객체 */
	private static Logger logger = Logger.getLogger(XNDiscAdminUtil.class);

	/* out 객체 */
	private static PrintStream out = System.out;

	/* OS 별 라인 피드 */
	private static String LINE_SEPERATOR = System.getProperty("line.separator");

	static {
		XNDISC_ADMIN_UTIL_VERSION = "XNDisc Admin Utility " + getXNDiscAdminVersion() + "(" + getXNDiscAdminPublshingDate() + ")";
	}
	/**
	 * XNDisc Admin 사용법 출력
	 * 
	 * @param mainop 메인 operation
	 * @param processop 프로세스 operation
	 */
	private static void printAdminUsage(String mainop, String processop) {

		clearConsoleOutput();

		List<String> ops = new ArrayList<String>();
		if (StringUtils.isEmpty(mainop)) {
			ops.add("file");
			ops.add("media");
			ops.add("vol");
			ops.add("id");
		} else {
			ops.add(mainop);
		}
		if (StringUtils.isEmpty(processop)) {
			ops.add("ls");
			ops.add("mk");
			ops.add("rm");
			ops.add("wh");
			ops.add("ch");
			ops.add("reg");
			ops.add("get");
			ops.add("enc");
			ops.add("dec");
		} else {
			ops.add(processop);
		}

		StringBuilder usage = new StringBuilder(LINE_SEPERATOR);
		usage.append("┌").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┐").append(LINE_SEPERATOR);
		usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
		usage.append("│").append(StringUtils.center(XNDISC_ADMIN_UTIL_VERSION, PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
		usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
		usage.append("├").append(StringUtils.center("", PRINT_COLUMN_SIZE, "-")).append("┤").append(LINE_SEPERATOR);
		usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);

		if (ops.contains("vol")) {
			usage.append("│").append(StringUtils.rightPad("      VOLUME Usage", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			if (ops.contains("mk")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.MKVOL.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
				usage.append("│").append(StringUtils.rightPad("  - " + XNDiscAdminUsage.ACCESSTYPE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("ls")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.LSVOL.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("rm")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.RMVOL.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("ch")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.CHVOL.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
				usage.append("│").append(StringUtils.rightPad("  - " + XNDiscAdminUsage.ACCESSTYPE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("├").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┤").append(LINE_SEPERATOR);
		}
		if (ops.contains("media")) {
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("│").append(StringUtils.rightPad("      MEDIA Usage", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			if (ops.contains("mk")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.MKMEDIA.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
				usage.append("│").append(StringUtils.rightPad("  - " + XNDiscAdminUsage.MEDIATYPE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("ls")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.LSMEDIA.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("rm")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.RMMEDIA.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("ch")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.CHMEDIA.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
				usage.append("│").append(StringUtils.rightPad("  - " + XNDiscAdminUsage.MEDIATYPE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("├").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┤").append(LINE_SEPERATOR);
		}
		if (ops.contains("file")) {
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("│").append(StringUtils.rightPad("      FILE Usage", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			if (ops.contains("ls")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.LSFILE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("reg")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.REGFILE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("get")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.GETFILE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("wh")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.WHFILE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("rm")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.RMFILE.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("├").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┤").append(LINE_SEPERATOR);
		}
		if (ops.contains("id")) {
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("│").append(StringUtils.rightPad("      ID Usage", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			if (ops.contains("enc")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.IDENC.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			if (ops.contains("dec")) {
				usage.append("│").append(StringUtils.rightPad(XNDiscAdminUsage.IDDEC.getUsage(), PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
			}
			usage.append("│").append(StringUtils.center("", PRINT_COLUMN_SIZE, " ")).append("│").append(LINE_SEPERATOR);
		}
		usage.append("└").append(StringUtils.rightPad("", PRINT_COLUMN_SIZE, "-")).append("┘").append(LINE_SEPERATOR);
		usage.append(LINE_SEPERATOR);
		if (printlog) {
			logger.info(usage.toString());
		} else {
			out.print(usage.toString());
		}
	}

	/**
	 * 숫자형인지 체크
	 * 
	 * @param obj Object 객체
	 * @return 숫자형이면 true, 아니면 false
	 */
	private static boolean isInteger(Object obj) {
		try {
			Integer val = -1;
			if (obj == null) {
				return false;
			} else {
				val = Integer.valueOf(obj.toString());
			}
			if (val instanceof Integer) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Console clear 하기
	 */
	private static void clearConsoleOutput() {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String ostype = (os.contains("windows")) ? "W" : "U";
			if (ostype.equals("W")) {
				CommandLine cmdLine = CommandLine.parse("cls");
				DefaultExecutor executor = new DefaultExecutor();
				executor.execute(cmdLine);
				System.out.printf("%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n", new Object[0]);
			} else {
				CommandLine cmdLine = CommandLine.parse("clear");
				DefaultExecutor executor = new DefaultExecutor();
				executor.execute(cmdLine);
				System.out.print(CLEAR_TERMINAL_ANSI_CMD);
				System.out.flush();
			}
		} catch (IOException e) {
		}
	}
	
	public static void main(String args[]) {

		if (args.length == 0) {
			XNDiscAdminUtil.printAdminUsage(null, null);
			// System.exit(0);
		}

		XNDiscAdminFile file = new XNDiscAdminFile(printlog, out, logger);
		XNDiscAdminMedia media = new XNDiscAdminMedia(printlog, out, logger);
		XNDiscAdminVolume volume = new XNDiscAdminVolume(printlog, out, logger);
		XNDiscAdminEnDecrypt endecrypt = new XNDiscAdminEnDecrypt(printlog, out, logger);

		Scanner in = new Scanner(System.in);
		String input = "";
		List<String> options = null;
		Scanner part = null;
		do {
			input = in.nextLine();
			part = new Scanner(input).useDelimiter(" ");
			options = new ArrayList<String>();
			while (part.hasNext()) {
				String val = part.next().trim();
				if (StringUtils.isEmpty(val)) {
					continue;
				}
				options.add(val);
			}
			if (options.size() < 2 || input.equalsIgnoreCase("q")) {
				if (input != null && input.trim().equalsIgnoreCase("q")) {
					System.out.println(LINE_SEPERATOR + "XNDiscAdminUtil quit!!!" + LINE_SEPERATOR);
				} else {
					System.out.println(LINE_SEPERATOR + "XNDiscAdminUtil are invalid parameters!!!" + LINE_SEPERATOR);
				}
				System.exit(0);
			}

			String main_op = (StringUtils.isEmpty(options.get(0))) ? "" : options.get(0);
			String process_op = (StringUtils.isEmpty(options.get(1))) ? "" : options.get(1);
			if (main_op.equals("clear")) {
				if (process_op.equals("screen")) {
					clearConsoleOutput();
				}
			} else if (main_op.equals("file")) {
				if (process_op.equals("ls")) {
					String fileid = "";
					if (options.size() > 2) {
						fileid = options.get(2);
					}
					if (StringUtils.isEmpty(fileid)) {
						file.selectFileList();
					} else {
						file.selectFileById(fileid);
					}
				} else if (process_op.equals("reg")) {
					String host = "";
					int port = -1;
					String filepath = "";
					int vol = -1;
					if (options.size() == 4) {
						host = XNDiscAdminConfig.getString(XNDiscAdminConfig.HOST);
						port = XNDiscAdminConfig.getInt(XNDiscAdminConfig.PORT);
						filepath = options.get(2);
						if (isInteger(options.get(3))) {
							vol = Integer.parseInt(options.get(3));
						}
					} else if (options.size() > 5) {
						host = options.get(2);
						port = Integer.parseInt(options.get(3));
						filepath = options.get(4);
						if (isInteger(options.get(5))) {
							vol = Integer.parseInt(options.get(5));
						}
					}
					if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(filepath) && port > 0 && vol > 0) {
						file.regFile(host, port, filepath, vol, "0");
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				} else if (process_op.equals("get")) {
					String host = "";
					int port = -1;
					String fileid = "";
					String filepath = "";
					if (options.size() == 4) {
						host = XNDiscAdminConfig.getString(XNDiscAdminConfig.HOST);
						port = XNDiscAdminConfig.getInt(XNDiscAdminConfig.PORT);
						fileid = options.get(2);
						filepath = options.get(3);
					} else if (options.size() > 5) {
						host = options.get(2);
						if (isInteger(options.get(3))) {
							port = Integer.parseInt(options.get(3));
						}
						fileid = options.get(4);
						filepath = options.get(5);
					}
					if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(fileid) && !StringUtils.isEmpty(filepath) && port > 0) {
						file.getFile(host, port, fileid, filepath);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				} else if (process_op.equals("wh")) {
					String fileid = "";
					if (options.size() > 2) {
						fileid = options.get(2);
					}
					if (!StringUtils.isEmpty(fileid)) {
						file.getFilePathByFileId(fileid);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				} else if (process_op.equals("rm")) {
					String host = "";
					int port = -1;
					String fileid = "";
					if (options.size() == 3) {
						host = XNDiscAdminConfig.getString(XNDiscAdminConfig.HOST);
						port = XNDiscAdminConfig.getInt(XNDiscAdminConfig.PORT);
						fileid = options.get(2);
					} else if (options.size() > 4) {
						host = options.get(2);
						if (isInteger(options.get(3))) {
							port = Integer.parseInt(options.get(3));
						}
						fileid = options.get(4);
					}
					if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(fileid) && port > 0) {
						file.removeFile(host, port, fileid);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				}
			} else if (main_op.equals("media")) {
				if (process_op.equals("mk")) {
					String host = "";
					int port = -1;
					String name = "";
					int type = 0;
					String path = "";
					String desc = " ";
					int maxsize = -1;
					int vol = -1;
					if (options.size() == 7) {
						host = XNDiscAdminConfig.getString(XNDiscAdminConfig.HOST);
						port = XNDiscAdminConfig.getInt(XNDiscAdminConfig.PORT);
						name = options.get(2);
						if (isInteger(options.get(3))) {
							type = Integer.parseInt(options.get(3));
						}
						path = options.get(4);
						if (isInteger(options.get(5))) {
							maxsize = Integer.parseInt(options.get(5));
						}
						if (isInteger(options.get(6))) {
							vol = Integer.parseInt(options.get(6));
						}
					} else if (options.size() == 8) {
						host = XNDiscAdminConfig.getString(XNDiscAdminConfig.HOST);
						port = XNDiscAdminConfig.getInt(XNDiscAdminConfig.PORT);
						name = options.get(2);
						if (isInteger(options.get(3))) {
							type = Integer.parseInt(options.get(3));
						}
						path = options.get(4);
						desc = options.get(5) + " ";
						if (isInteger(options.get(6))) {
							maxsize = Integer.parseInt(options.get(6));
						}
						if (isInteger(options.get(7))) {
							vol = Integer.parseInt(options.get(7));
						}
					} else if (options.size() == 9) {
						host = options.get(2);
						if (isInteger(options.get(3))) {
							port = Integer.parseInt(options.get(3));
						}
						name = options.get(4);
						if (isInteger(options.get(5))) {
							type = Integer.parseInt(options.get(5));
						}
						path = options.get(6);
						if (isInteger(options.get(7))) {
							maxsize = Integer.parseInt(options.get(7));
						}
						if (isInteger(options.get(8))) {
							vol = Integer.parseInt(options.get(8));
						}
					} else if (options.size() > 9) {
						host = options.get(2);
						if (isInteger(options.get(3))) {
							port = Integer.parseInt(options.get(3));
						}
						name = options.get(4);
						if (isInteger(options.get(5))) {
							type = Integer.parseInt(options.get(5));
						}
						path = options.get(6);
						desc = options.get(7) + " ";
						if (isInteger(options.get(8))) {
							maxsize = Integer.parseInt(options.get(8));
						}
						if (isInteger(options.get(9))) {
							vol = Integer.parseInt(options.get(9));
						}
					}
					if (!StringUtils.isEmpty(host) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(path) && port > 0 && vol > 0 && maxsize > 0) {
						media.makeMedia(host, port, name, type, path, desc, maxsize, vol);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				} else if (process_op.equals("ls")) {
					String mediaid = "-1";
					if (options.size() > 2) {
						mediaid = options.get(2);
					}
					if (StringUtils.isEmpty(mediaid) || mediaid.equals("-1")) {
						media.selectMediaList();
					} else {
						int id = -1;
						if (isInteger(mediaid)) {
							id = Integer.parseInt(mediaid);
						}
						if (id > 0) {
							media.selectMediaById(id);
						} else {
							XNDiscAdminUtil.printAdminUsage(main_op, process_op);
						}
					}
				} else if (process_op.equals("rm")) {
					String mediaid = "-1";
					if (options.size() > 2) {
						mediaid = options.get(2);
					}
					if (!StringUtils.isEmpty(mediaid)) {
						int id = -1;
						if (isInteger(mediaid)) {
							id = Integer.parseInt(options.get(2));
						}
						if (id > 0) {
							media.removeMedia(id);
						} else {
							XNDiscAdminUtil.printAdminUsage(main_op, process_op);
						}
					}
				} else if (process_op.equals("ch")) {
					int mediaid = -1;
					String name = "";
					int type = 0;
					String path = "";
					String desc = "";
					int maxsize = -1;
					int vol = -1;
					if (options.size() == 8) {
						if (isInteger(options.get(2))) {
							mediaid = Integer.parseInt(options.get(2));
						}
						name = options.get(3);
						if (isInteger(options.get(4))) {
							type = Integer.parseInt(options.get(4));
						}
						path = options.get(5);
						if (isInteger(options.get(6))) {
							maxsize = Integer.parseInt(options.get(6));
						}
						if (isInteger(options.get(7))) {
							vol = Integer.parseInt(options.get(7));
						}
					} else if (options.size() > 8) {
						if (isInteger(options.get(2))) {
							mediaid = Integer.parseInt(options.get(2));
						}
						name = options.get(3);
						if (isInteger(options.get(4))) {
							type = Integer.parseInt(options.get(4));
						}
						path = options.get(5);
						desc = options.get(6);
						if (isInteger(options.get(7))) {
							maxsize = Integer.parseInt(options.get(7));
						}
						if (isInteger(options.get(8))) {
							vol = Integer.parseInt(options.get(8));
						}
					}
					if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(path) && mediaid > 0 && maxsize > 0 && vol > 0) {
						media.changeMedia(mediaid, name, type, path, desc, maxsize, vol);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				}
			} else if (main_op.equals("vol")) {
				if (process_op.equals("mk")) {
					String name = "";
					String access = "";
					String desc = " ";
					if (options.size() == 4) {
						name = options.get(2);
						access = options.get(3);
					} else if (options.size() > 4) {
						name = options.get(2);
						access = options.get(3);
						desc = options.get(4) + " ";
					}
					if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(access)) {
						volume.makeVolume(name, access, desc);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				} else if (process_op.equals("ls")) {
					String volid = "-1";
					if (options.size() > 2) {
						volid = options.get(2);
					}
					if (StringUtils.isEmpty(volid) || volid.equals("-1")) {
						volume.selectVolumeList();
					} else {
						int volumeid = -1;
						if (isInteger(volid)) {
							volumeid = Integer.parseInt(volid);
						}
						if (volumeid > 0) {
							volume.selectVolumeById(volumeid);
						} else {
							XNDiscAdminUtil.printAdminUsage(main_op, process_op);
						}
					}
				} else if (process_op.equals("rm")) {
					String volid = "-1";
					if (options.size() > 2) {
						volid = options.get(2);
					}
					if (!StringUtils.isEmpty(volid)) {
						int volumeid = -1;
						if (isInteger(volid)) {
							volumeid = Integer.parseInt(volid);
						}
						if (volumeid > 0) {
							volume.removeVolume(volumeid);
						}
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				} else if (process_op.equals("ch")) {
					int volumeid = -1;
					String name = "";
					String access = "";
					String desc = "";
					if (options.size() == 5) {
						if (isInteger(options.get(2))) {
							volumeid = Integer.parseInt(options.get(2));
						}
						name = options.get(3);
						access = options.get(4);
					} else if (options.size() > 5) {
						if (isInteger(options.get(2))) {
							volumeid = Integer.parseInt(options.get(2));
						}
						name = options.get(3);
						access = options.get(4);
						desc = options.get(5) + " ";
					}
					if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(access) && volumeid > 0) {
						volume.changeVolume(volumeid, name, access, desc);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				}
			} else if (main_op.equals("id")) {
				String id = "";
				if (options.size() > 2) {
					id = options.get(2);
				}
				if (process_op.equals("enc")) {
					if (!StringUtils.isEmpty(id)) {
						endecrypt.encrypt(id);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				} else if (process_op.equals("dec")) {
					if (!StringUtils.isEmpty(id)) {
						endecrypt.decrypt(id);
					} else {
						XNDiscAdminUtil.printAdminUsage(main_op, process_op);
					}
				}
			}
			part.close();
		} while (!input.equalsIgnoreCase("q"));
		in.close();
	}

	/**
	 * XNDisc Admin 배포 버전정보 얻어오기
	 * 
	 * @return XNDisc Admin 버전정보
	 */
	public static String getXNDiscAdminVersion() {
		if (XNDiscAdmin_PublishingVersion == null) {
			readVersionFromFile();
		}
		return XNDiscAdmin_PublishingVersion;
	}

	/**
	 * XNDisc Admin 배포일 얻어오기
	 * 
	 * @return XNDisc Admin 배포일
	 */
	public static String getXNDiscAdminPublshingDate() {
		if (XNDiscAdmin_PublishingDate == null) {
			readVersionFromFile();
		}
		return XNDiscAdmin_PublishingDate;
	}
	
	/**
	 * XNDisc Admin 배포정보 읽어오기
	 */
	private static void readVersionFromFile() {
		XNDiscAdmin_PublishingVersion = "<unknown>";
		XNDiscAdmin_PublishingDate = "<unknown>";
		InputStreamReader isr = null;
		LineNumberReader lnr = null;
		try {
			isr = new InputStreamReader(XNDiscAdminUtil.class.getResourceAsStream("/com/sds/acube/ndisc/xadmin/version.txt"));
			if (isr != null) {
				lnr = new LineNumberReader(isr);
				String line = null;
				do {
					line = lnr.readLine();
					if (line != null) {
						if (line.startsWith("Publishing-Version=")) {
							XNDiscAdmin_PublishingVersion = line.substring("Publishing-Version=".length(), line.length()).trim();
						} else if (line.startsWith("Publishing-Date=")) {
							XNDiscAdmin_PublishingDate = line.substring("Publishing-Date=".length(), line.length()).trim();
						}
					}
				} while (line != null);
				lnr.close();
			}
		} catch (IOException ioe) {
			XNDiscAdmin_PublishingVersion = "<unknown>";
			XNDiscAdmin_PublishingDate = "<unknown>";
		} finally {
			try {
				if (lnr != null) {
					lnr.close();
				}
				if (isr != null) {
					isr.close();
				}
			} catch (IOException ioe) {
			}
		}
	}
}
