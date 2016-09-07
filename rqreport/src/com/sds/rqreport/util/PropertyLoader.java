package com.sds.rqreport.util;

import java.util.*;
import java.io.*;
import org.apache.log4j.*;

public class PropertyLoader {

//	String language = "KO", charset;
	Locale currentLocale;
//	ResourceBundle prop;
	Properties prop = null;
	String configfile = null;
	private static Logger L= Logger.getLogger("PROPERTY");

//	public PropertyLoader(String propertyFile, String lang) throws MissingResourceException {
//		load(propertyFile, lang);
//	}

	public PropertyLoader(String propertyFile){
		prop = new Properties();
		
		load(propertyFile);
	}

	public boolean load(String propertyFile){
		if (prop == null)
			return false;
		configfile = propertyFile;
		InputStream in = null; 
		try {
			
			if(propertyFile != null && propertyFile.length() > 0)
			{
				in = new FileInputStream(propertyFile);
			}
			
			if(in != null)
				prop.load(in);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
		   try {
			    if (in != null) {
			     in.close();
			    }
		   } catch (IOException e) {;}
		 }
		return true;
	}

	public boolean isKey(String key) {
		String value = prop.getProperty(key);
		if (value == null)
			return false;
		return true;
	}

	public String getString(String key) {
//	 This method returns null if the property is not found.
		String value;
		try {
			value = prop.getProperty(key);
			if( value == null)
			{
				return "";
			}
			if (value.startsWith("**"))
			{
///				value = Decrypter.decrypt26(value.substring(2).trim());
			}      
		}
		catch (Exception ex) {
			L.warn(ex.toString());
			value = "";
		}
		L.debug(key + "=" + value);
		return value;
	}

	public String getString(String key, String defaultValue) {
//	 This method returns the default value argument if the property is not found.
		String value = getString(key);
		if (value.startsWith("**"))
		{
///			value = Decrypter.decrypt26(value.substring(2));
		}
		if (value.equals(""))
		{
			prop.put(key, defaultValue);
			return defaultValue;
		}
		return value;
	}

	public int getInt(String key) {
//	 This method returns null if the property is not found.
		try {
			String value = getString(key);
			if(value != null && value.length() > 0)
			{
				return Integer.parseInt(this.getString(key));
			}
			else
				return 0;
		}
		catch ( NumberFormatException ex) {
			//L.warn(ex.toString());
			return 0;
		}
	}

	public int getInt(String key, int defaultValue) {
//	 This method returns the default value argument if the property is not found.
		try {
			return Integer.parseInt(this.getString(key));
		}
		catch (NumberFormatException e) {
			//L.warn(e.toString());
			prop.put(key, "" + defaultValue);
			return defaultValue;
		}
		catch (Exception e) {
			//L.warn(e.toString());
			prop.put(key, "" + defaultValue);
			return defaultValue;
		}
	}

	public short getShort(String key) {
//	 This method returns 0 if the property is not found.
		try {
			return Short.parseShort(this.getString(key));
		}
		catch (NumberFormatException ex) {
			L.warn(ex.toString());
			return (short) 0;
		}
	}

	public short getShort(String key, short defaultValue) {
//	 This method returns the default value argument if the property is not found.
		try {
			return Short.parseShort(this.getString(key));
		}
		catch (NumberFormatException e) {
			//L.warn(e.toString());
			prop.put(key, "" + defaultValue);
			return defaultValue;
		}
		catch (Exception e) {
			//L.warn(e.toString())
			prop.put(key, "" + defaultValue);;
			return defaultValue;
		}
	}

	public String chCharset(String str, String charset) {
		String temp = null;
		try {
			if (str == null) {
				return "";
			}
			else {
				temp = new String(str.getBytes("8859_1"), charset);
			}
		}
		catch (Exception e) {
			L.warn(e.toString());
		}
		return temp;
	}
	
	public Properties getProperties()
	{
		return prop;
	}
	
	public int setProperty(Map paramMap) {
	    FileReader fr = null;
	    FileReader fr2 = null;
	    Vector requbeProp;
	    Vector logProp;

	    FileOutputStream fo = null;
	    try {
	      String path = configfile;
	      String path2="";// = ClassLoader.getSystemResource("log4j.properties").getPath();

	      fr = new FileReader(path);
	    //  fr2 = new FileReader(path2);

	      BufferedReader br = new BufferedReader(fr);
	    //  BufferedReader br2 = new BufferedReader(fr2);
	      String s;
	      requbeProp = new Vector();
	      logProp = new Vector();
	      while ((s = br.readLine()) != null)
	      {
	        requbeProp.add(s);
	      }
//	      while((s = br2.readLine()) != null)
	//      {
//	        logProp.add(s);
//	      }

	      int size = requbeProp.size();
	      boolean isUpdated = false;
	      Iterator it = paramMap.keySet().iterator();
	      String key = null;
	      String[] value = null;
	      while(it.hasNext())
	      {
	          key = (String)it.next();
	          value = (String[])paramMap.get(key);
//	          for(int i=0; i< value.length; i++)
//	          {
//	              System.out.println(key + " : " + value[i]);
//	          }
	          String val = value[0];
		      for(int i = 0; i < size; ++i)
		      {
		        String line = (String)requbeProp.get(i);
		        if(!line.startsWith("#"))
		        {
		          if(line.startsWith(key))
		            {
		              String rep = key + "=" + val;
		              requbeProp.setElementAt(rep,i);
		              isUpdated = true;
		              break;
		            }
		        }
		      }
	      }
	      if(isUpdated)
	      {
	        FileWriter fw = new FileWriter(path);
	        BufferedWriter bw = new BufferedWriter(fw);
	        for(int i = 0; i < size; ++i)
	        {
	          String line = (String)requbeProp.get(i);
	          System.out.println(line);
	          bw.write(line);
	          bw.newLine();
	        }
	        bw.close();
	        bw = null;
	        fw = null;
	      }
	      else
	      {
	        size = logProp.size();
	        for (int i = 0; i < size; ++i) {
	          String line = (String) logProp.get(i);
	          if (!line.startsWith("#")) {
	            if (line.startsWith(key)) {
//	              String rep = key + "=" + val;
//	              logProp.setElementAt(rep, i);
	              isUpdated = true;
	              break;
	            }
	          }
	        }
	        if(isUpdated)
	        {
	          FileWriter fw = new FileWriter(path2);
	          BufferedWriter bw = new BufferedWriter(fw);
	          for (int i = 0; i < size; ++i) {
	            String line = (String) logProp.get(i);
	            bw.write(line);
	            bw.newLine();
	          }
	          bw.close();
	          bw = null;
	          fw = null;
	        }
	      }
		  prop.clear();
		  load(configfile);
	      return 0;
	    }
	    catch (Exception ex) {
	        L.error("SetProperty Error!", ex);
	    }
	    finally {
	      try {
	        if (fr != null) {
	          fr.close();
	          fr = null;
	        }
	        if (fr2 != null) {
	          fr2.close();
	          fr2 = null;
	        }
	        if (fo != null) {
	          fo.close();
	          fo = null;
	        }
	      }
	      catch (IOException ex1) {
	      }
	    }
	    prop.clear();
	    load(configfile);
	    return 0;
	 }
}
