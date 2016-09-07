package com.sds.rqreport.service.web;

import java.text.SimpleDateFormat;
import java.util.*;

public class RQFileList {
	
	private int maxNum = 0;
	private HashMap oFileMap = new HashMap();
	TreeMap treeMap = null;
	ArrayList arrayList = null;
	/**
	 * 한번의 루프로 필요한정보를 담기위해 만든 클래스 
	 * 파일 싱크를 위해 필요한 maxCnt 와 파일이름 목록을 가지게 된다.
	 * 
	 */
	public RQFileList(){}
	
	public void setFileList(String order, String filename, long lastModified){
		ArrayList lm_arr = new ArrayList();
		lm_arr.add(filename);
		
		Date lm_date = new Date(lastModified);
		//SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd G 'at' hh:mm:ss a zzz");
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
		String dateString = formatter.format(lm_date);
		
		lm_arr.add(""+dateString);
		
		oFileMap.put(order, lm_arr);
	}
	
	public void setMaxNum(int p_maxNum){
		this.maxNum = p_maxNum;
	}
	
	public HashMap getFileList(){
		return oFileMap;
	}
	
	//
	public TreeMap getTreeMap(){
		return treeMap;
	}
	
	public int getMaxNum(){
		return maxNum;
	}
	
	public void setSorting(){
		//treeMap 을 통한 Sorting
		treeMap = new TreeMap(oFileMap); //JDK 1.4 에선 Key 값으로 없으므로 int 소팅이 안된다.
		
		//keyset 을 구해 Sorting
		/*
		Set keyset = oFileMap.keySet();
		TreeSet ts = new TreeSet(keyset);
		Iterator it = ts.iterator();

		String key = "";
		
		while(it.hasNext()){
		    key = (String)it.next();
		    System.out.println("key = "+key);
		    System.out.println(((ArrayList)treeMap.get(key)).toString());
		}
		*/
	}
	
	public void setSortingInt(){
		//int Sorting
		Set lm_keyset = oFileMap.keySet();
		Iterator it = lm_keyset.iterator();
		int key = 0;
		int[] keys = new int[oFileMap.size()];
		for(int i = 0 ; i < oFileMap.size() ; i++){
			key = Integer.parseInt( (String)it.next() );
			keys[i] = key;
		}
		
		int tmp = 0;
		for(int j = 0 ; j < keys.length; j++){
			for(int k = j ; k < keys.length ; k++){
				if(keys[j] > keys[k]){
					tmp = keys[j];
					keys[j] = keys[k];
					keys[k] = tmp;
				}
			}
		}
		ArrayList lm_arr = null;
		ArrayList lm_tmp = null;
		arrayList = new ArrayList();
		for(int l = 0 ; l < keys.length ; l++){
			lm_arr = new ArrayList();
			lm_tmp = new ArrayList();
			lm_arr.add(""+keys[l]);
			lm_tmp = (ArrayList)oFileMap.get(""+keys[l]);
			lm_arr.add("" + (String)lm_tmp.get(1));
			arrayList.add(lm_arr);
		}
		
		//System.out.println(arrayList.toString());
	}
	
	public ArrayList getArray(){
		return arrayList;
	}
}
