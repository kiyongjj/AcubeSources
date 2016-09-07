package com.sds.rqreport.model;

import java.io.Serializable;
import java.util.*;

public class UserModel implements Serializable{

	private static final long serialVersionUID = 1L;

	public UserModel(){}
    
    private String strUserid = "";
    private ArrayList arrPrivilegePage = new ArrayList();
    private String strAuth = "";
    
    /*
     * setter method to set Parameters.
     */
    public void setUserid(String p_Userid){
        this.strUserid =   p_Userid;   
    }
    public void setPrivilegePage(String p_PrivilegePage){
        this.arrPrivilegePage.add(p_PrivilegePage);   
    }
    public void setAuth(String p_Auth){
    	this.strAuth = p_Auth;
    }
    
    /*
     * getter method to get Parameters.
     */
    public String getUserid(){
        return strUserid; 
    }
    public ArrayList getPrivilegePage(){
        return arrPrivilegePage; 
    }
    public String getAuth(){
    	return strAuth;
    }

}
