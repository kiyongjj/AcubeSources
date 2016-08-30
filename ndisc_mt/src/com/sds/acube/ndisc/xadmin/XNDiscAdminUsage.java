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

/**
 * XNDiscAdmin 사용법
 * 
 * @author Takkies
 *
 */
public enum XNDiscAdminUsage {

	MKVOL("vol mk {Volume Name} {Volume Access} [Description]"),
	LSVOL("vol ls [Volum Id]"),
	RMVOL("vol rm {Volum Id}"),
	CHVOL("vol ch {Volum Id} {Volume Name} {Volume Access} [Description]"),
	ACCESSTYPE("Access Type : R(Read), C(Create), U(Update), D(Delete)"),
	
	MKMEDIA("media mk [host] [port] {Media Name} {Media Type} {Media Path} [Description] {Max Size} {Volume Id}"),
	LSMEDIA("media ls [Media Id]"),
	RMMEDIA("media rm {Media Id}"),
	CHMEDIA("media ch {Media Id} {Media Name} {Media Type} {Media Path} [Description] {Max Size} {Volume Id}"),
	MEDIATYPE("Media Type : 1(HDD), 2(OD), 3(CD), 4(DVD)"),
	
	LSFILE("file ls [file Id]"),
	REGFILE("file reg [host] [port] {regist file path} {Volume Id}"),
	GETFILE("file get [host] [port] {file id} {destination file path}"),
	WHFILE("file wh {file id}"),
	RMFILE("file rm [host] [port] {file id}"),
	
	IDENC("id enc {id}"),
	IDDEC("id dec {id}");
	
	private String usage;
		
	XNDiscAdminUsage(String usage) {
		this.usage = usage;
	}
	
	public String getUsage() {
		return this.usage;
	}
}
