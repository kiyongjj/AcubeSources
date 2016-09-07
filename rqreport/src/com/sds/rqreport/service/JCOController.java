package com.sds.rqreport.service;

import org.apache.log4j.Logger;

//import java.sql.Connection;
import java.io.IOException;
import java.util.*;

import org.jdom.*;

import sun.util.logging.resources.logging;
//import java.io.IOException;

import com.sap.mw.jco.*;
import com.sds.rqreport.model.querynode.*;
import com.sds.rqreport.service.cache.RunInfo;
import com.sds.rqreport.service.queryexecute.*;
import com.sds.rqreport.util.*;

//import com.sap.mw.jco.JCO.Repository;

public class JCOController
{
	/**
	 * Logger for this class
	 */
	private final Logger log = Logger.getLogger("RQJCO");
	int mKey = 0;
	Hashtable mConnInfo;
	
	static char SSEP = 5;	// output : Data Set 제일 처음, input : param separator
	//static char PARAM_SEP = 5;	
    static char COL_SEP = 9;	// 
    static char ROW_SEP = 10;
	
    ConnectionInfo connInfo = null;							// Connection 정보를 memeber var. 로 선언
    QueryFrame queryframe = new QueryFrame(); 	            // queryframe 을 member var. 로 선언 
    String rootJCOFncNm = ""; 								// root SQLStmt Function name (JCOFncNm) 
    ArrayList bindSrc = new ArrayList();   					// root SQLStmt bindSrc (ArrayList)
    HashMap charvalue = null;								// input type 이 char 
    HashMap structurevalue = null; 							// input type 이 structure
    HashMap tablevalue = null;								// input type 이 tablevalue
    HashMap runvarState = null;
	StringBuffer resultset = null;
	int iRootRow = 1;
	RQHierarchyBindingSource oRQHBindingSource = null; //Binary Obejct 
	
    public JCOController()
    {
    	mConnInfo = new Hashtable(100);
    }
    
	public synchronized int connect(String clientNumber, String user, String password, String language, String hostname, String systemnumber) 
						throws JCO.Exception, Exception
	{
 		int jcoConnKey = -1;
		JCO.Pool pool = null;
		
		JCO.Client client = JCO.createClient( clientNumber,       // SAP client
				user,   // userid
				password,     // password
				language,        // language
				hostname, // host name
				systemnumber );      // system number
		
		// exception 발생할 경우 메모리 해제를 할 수 없기때문에 메모리 생성 이전에 미리 체크함.
		// Open the connection
		client.connect();
		
		client.disconnect();

	
		
		try 
		{
	        // Add a connection pool to the specified system
	        //    The pool will be saved in the pool list to be used
	        //    from other threads by JCO.getClient(SID).
	        //    The pool must be explicitely removed by JCO.removeClientPool(SID)
	    	String poolname = hostname + clientNumber;
	    	pool = JCO.getClientPoolManager().getPool(poolname);
	    	if (pool == null)
	    	{
	    		JCO.addClientPool( poolname,         // Alias for this pool
		                           2,          		// Max. number of connections
		                           clientNumber,       // SAP client
		                           user,   // userid
		                           password,     // password
		                           language,        // language
		                           hostname, // host name
		                           systemnumber );
	        
	        

	    		JCO.Repository repository = getRepository(poolname, hostname);
	    		pool = JCO.getClientPoolManager().getPool(poolname);
	    		
	    		//IFunctionTemplate ftemplate = repository.getFunctionTemplate("RFC_SYSTEM_INFO");
	    	      // if the function definition was found in backend system
	    		//if(ftemplate != null)
	    		//{
	  	    		JCOConnection jcoConn = new JCOConnection();
		    		jcoConn.mRepository = repository;
		    		jcoConn.mPool = pool;
		    		jcoConn.mKey = mKey;
		    		jcoConnKey = mKey;
		    		mConnInfo.put("" + mKey++, jcoConn);
		    	//}
	    		
	    	}
	    	else
	    	{
	    		int size = mConnInfo.size();
	    		for(int i=0;i<size;i++)
	    		{
	    			JCOConnection jcoConn = (JCOConnection)mConnInfo.get("" + i);
	    			if( jcoConn.getPool().getName().equals(pool.getName()) == true )
	    			{
	    				jcoConnKey = jcoConn.getKey();
	    				break;
	    			}
	    		}
	    	}
	    	
	    	return jcoConnKey;
		}
		/*
		catch (JCO.AbapException ex)
		{
			if (ex.getKey().equalsIgnoreCase("NOT_FOUND")) {
			System.out.println("Dictionary structure/table not found.");
			return -1;
			}
			else {
			System.out.println(ex.getMessage());
			return -1;
			}
		}
		*/		
		catch (JCO.Exception ex)
		{
			if( pool != null)
				JCO.removeClientPool(pool.getName());
			//ex.printStackTrace();
			throw ex;
			//return -1;
		}
		
		catch (Exception ex)
		{
			if( pool != null)
				JCO.removeClientPool(pool.getName());
			//ex.printStackTrace();
			throw ex;
		
		}	
		
		/*			
		catch (JCO.Exception ex)
		{
			System.out.println("Caught an exception: \n" + ex);
			return -1;
		}	
		*/			
	}
	
	public synchronized void disconnect(int nKey) throws JCO.Exception
	{
		JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+nKey);
		if( jcoConn != null)
		{
			JCO.Pool pool = jcoConn.getPool();
		
			if( pool != null )
				JCO.removeClientPool(pool.getName());
		}
	}
	
	public synchronized void disconnectall() throws JCO.Exception
	{
		int size = mConnInfo.size();
		for(int i=0;i<size;i++)
		{
			JCOConnection jcoConn = (JCOConnection)mConnInfo.get("" + i);
			if( jcoConn != null)
			{
				JCO.Pool pool = jcoConn.getPool();
			
				if( pool != null )
					JCO.removeClientPool(pool.getName());
			}
		}
	}
	
	public JCO.Repository getRepository(String poolname, String hostname) throws JCO.Exception
	{
		JCO.Repository rtnRepository = null;
		JCO.Repository repository = null;
		
		int size = mConnInfo.size();
		for(int i=0; i<size; i++)
		{
			JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+i);
			repository = jcoConn.getRepository();
			if( repository.getName().equals(hostname) == true)
			{
				rtnRepository = repository;
				break;
			}
		}
		
		if( rtnRepository == null)
		{
			rtnRepository = new JCO.Repository(hostname, poolname);
		}
		
		return rtnRepository;
	}
	
	public synchronized ArrayList rfcFunctionSearch(int nKey, String functionname, String groupname, String language) throws JCO.Exception, Exception
	{
		ArrayList functionArray = new ArrayList(100);
		JCO.Client client = null;
	    try
		{
			JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+nKey);
	    	JCO.Repository repository = jcoConn.getRepository();
	    	IFunctionTemplate ftemplate = null;
	    	
	        // Get a function template from the repository
	        ftemplate = repository.getFunctionTemplate("RFC_FUNCTION_SEARCH");
	
	        // if the function definition was found in backend system
	        if(ftemplate != null)
	        {
	
	        	// Create a function from the template
	        	JCO.Function function = ftemplate.getFunction();
		          
	        	JCO.ParameterList input = function.getImportParameterList();
		          
		        if( functionname.equals("") == true)
		        	return null;
	
	 	        input.setValue(         functionname.toUpperCase(), "FUNCNAME" );
		          
		        if( groupname.equals("") != true )
		        	input.setValue(         groupname.toUpperCase(), "GROUPNAME" );
		        if( language.equals("") != true )
		          	input.setValue(         language.toUpperCase(), "LANGUAGE" );
	
		          // Get a client from the pool
		        client = JCO.getClient(jcoConn.getPool().getName());
	
		        client.execute(function);
	
		          // The export parameter 'RFCSI_EXPORT' contains a structure of type 'RFCSI'
		        	//boolean isStructure = function.getTableParameterList().isStructure("FUNCTIONS");
		        JCO.Table s = function.getTableParameterList().getTable("FUNCTIONS");
	
		          // Use enumeration to loop over all fields of the structure
		        System.out.println("System info for " + jcoConn.getPool().getName() + ":\n" +
		          				   "--------------------");
	
		  		if (s.getNumRows() > 0)
		  		{
	
		  			// Loop over all rows
		  			do
		  			{
		  				JCOFunctionInfo functionInfo = new JCOFunctionInfo();
		  				//for (int i = 0; i < s.getFieldCount(); i++)
		  				//{
		  					functionInfo.name = s.getString("FUNCNAME");
		  					functionInfo.groupname = s.getString("GROUPNAME");
		  					functionInfo.appl = s.getString("APPL");
		  					functionInfo.host = s.getString("HOST");
		  					functionInfo.stext = s.getString("STEXT");
		  				//}//for
			  	    
		  				functionArray.add(functionInfo);
	  					/*
						System.out.println("-----------------------------------------");

						// Loop over all columns in the current row
						for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); )
						{
							JCO.Field field = e.nextField();
							System.out.println(field.getName() + ":\t" + field.getString());
						}//for*/			  		    
		  			} while(s.nextRow());
	
		  		}
		  		else
		  		{
		  			System.out.println("No results found");
		  		}//if
	
		          // Release the client into the pool
		  		JCO.releaseClient(client);
	        }
	        else
	        {
	        	System.out.println("Function RFC_FUNCTION_SEARCH not found in backend system.");
	  	  	}
		}
		/*
			catch (JCO.AbapException ex)
			{
				System.out.println(ex.getMessage());
			}
		*/	    
		catch (JCO.Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			//ex.printStackTrace();
			throw ex;
		}
	
		catch (Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);			
			//ex.printStackTrace();
			throw ex;
		}
		
	  return functionArray;
	}
		
	public synchronized ArrayList	rfcGetFunctionInterface(int nKey, String functionname, String language) throws JCO.Exception, Exception
	{
		ArrayList paramArray = new ArrayList(100);
		JCO.Client client = null;
        String strCheck;
		try
		{
	    	
			JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+nKey);
	    	JCO.Repository repository = jcoConn.getRepository();
	    	
	        // Get a function template from the repository
	        IFunctionTemplate ftemplate = repository.getFunctionTemplate("RFC_GET_FUNCTION_INTERFACE");

	        // if the function definition was found in backend system
	        if(ftemplate != null)
	        {

	        	// Create a function from the template
	        	JCO.Function function = ftemplate.getFunction();
		          
	        	JCO.ParameterList input = function.getImportParameterList();
		          
		        if( functionname.equals("") == true)
		        	return null;

     	        input.setValue(         functionname.toUpperCase(), "FUNCNAME" );
		          
		        if( language.equals("") != true )
		          	input.setValue(         language.toUpperCase(), "LANGUAGE" );
	
		        // Get a client from the pool
		        client = JCO.getClient(jcoConn.getPool().getName());
	
		        // We can call 'RFC_SYSTEM_INFO' directly since it does not need any input parameters
		        client.execute(function);
	
		        // The export parameter 'RFCSI_EXPORT' contains a structure of type 'RFCSI'
		        //boolean isStructure = function.getTableParameterList().isStructure("FUNCTIONS");
		        JCO.Table s = function.getTableParameterList().getTable("PARAMS");
	
		        // Use enumeration to loop over all fields of the structure
		        System.out.println("System info for " + jcoConn.getPool().getName() + ":\n" +
		          				   "--------------------");
		  		if (s.getNumRows() > 0)
		  		{
		  			// Loop over all rows
		  			do
		  			{
		  				strCheck = s.getString("PARAMCLASS");
		  				if( s.getString("PARAMCLASS").compareToIgnoreCase("I") == 0 || s.getString("PARAMCLASS").compareToIgnoreCase("E") == 0 || 
		  						s.getString("PARAMCLASS").compareToIgnoreCase("T")==0 )
		  				{	
		  					JCOParamInfo functionStructure = new JCOParamInfo();

		  					functionStructure.paramclass = s.getString("PARAMCLASS");
		  					functionStructure.parameter = s.getString("PARAMETER");
		  					functionStructure.tabname = s.getString("TABNAME");
		  					functionStructure.fieldname = s.getString("FIELDNAME");
		  					functionStructure.exid = s.getString("EXID");
		  					functionStructure.defaultvalue = s.getString("DEFAULT");
		  					functionStructure.paramtext = s.getString("PARAMTEXT");
		  					functionStructure.optional = s.getString("OPTIONAL");
			  	    
		  					paramArray.add(functionStructure);
		  				}
		  				/*
						System.out.println("-----------------------------------------");

						// Loop over all columns in the current row
						for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); )
						{
							JCO.Field field = e.nextField();
							System.out.println(field.getName() + ":\t" + field.getString());
						}//for
						*/			  		    
		  			} while(s.nextRow());
		  			
		  			//	Release the client into the pool
		  			JCO.releaseClient(client);
		  			
		  			IFunctionTemplate template = repository.getFunctionTemplate(functionname);
		  			if(template != null)
		  			{
			        	// Create a function from the template
			        	JCO.Function func = template.getFunction();
			  			JCOParamInfo ParamInfo = null;
			  			JCO.ParameterList list = null;
			  			String strtype;
			  			String parametername;
			  			int size = paramArray.size();
			  			for(int i=0; i<size; i++)
			  			{
			  				ParamInfo = (JCOParamInfo)paramArray.get(i);
			  				parametername = ParamInfo.parameter;
			  				if(ParamInfo.paramclass.compareToIgnoreCase("I") == 0)
			  				{
			  					list = func.getImportParameterList();
			  					strtype = list.getTypeAsString(ParamInfo.parameter);
			  					ParamInfo.paramtype = strtype;
			  					System.out.println(strtype);
			  				}
			  				else if(ParamInfo.paramclass.compareToIgnoreCase("E") == 0)
			  				{
			  					list = func.getExportParameterList();
			  					strtype = list.getTypeAsString(ParamInfo.parameter);
			  					ParamInfo.paramtype = strtype;
			  					System.out.println(strtype);
			  				}
			  				else if(ParamInfo.paramclass.compareToIgnoreCase("T") == 0)
			  				{
			  					list = func.getTableParameterList();
			  					strtype = list.getTypeAsString(ParamInfo.parameter);
			  					ParamInfo.paramtype = strtype;
			  					System.out.println(strtype);
			  				}
			  			}
		  			}
		  			

		  		}
		  		else
		  		{
		  			
		  			System.out.println("No results found");
		  			//	Release the client into the pool
		  			JCO.releaseClient(client);
		  		}//if
	        }
	        else
	        {
	        	System.out.println("Function RFC_GET_FUNCTION_INTERFACE not found in backend system.");
	  	  	}
	    }
		/*
			catch (JCO.AbapException ex)
			{
				System.out.println(ex.getKey());
				System.out.println(ex.getMessage());
		
			}
		*/	    
		catch (JCO.Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			throw ex;
			//ex.printStackTrace();
			//System.out.println(ex.getMessage());
		}
		
		catch (Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			throw ex;
			//ex.printStackTrace();
			//System.out.println(ex.getMessage());
		}		
		
		return paramArray;		
	}
	
	public synchronized ArrayList rfcGetFieldInfo(int nKey, String tabname, String fieldname, String uclen, String language, String alltype) throws JCO.Exception, Exception
	{
		ArrayList functionArray = new ArrayList(100);
		JCO.Client client = null;
	    try
		{
			JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+nKey);
	    	JCO.Repository repository = jcoConn.getRepository();
	    	IFunctionTemplate ftemplate = null;
	    	
	        // Get a function template from the repository
	        ftemplate = repository.getFunctionTemplate("DDIF_FIELDINFO_GET");
	
	        // if the function definition was found in backend system
	        if(ftemplate != null)
	        {
	
	        	// Create a function from the template
	        	JCO.Function function = ftemplate.getFunction();
		          
	        	JCO.ParameterList input = function.getImportParameterList();
		          
		        if( tabname.equals("") == true)
		        	return null;
	
	 	        input.setValue(         tabname.toUpperCase(), "TABNAME" );
	 	        
		        if( fieldname.equals("") != true )
		        	input.setValue(         fieldname.toUpperCase(), "FIELDNAME" );
		        if( uclen.equals("") != true )
		        	input.setValue(         uclen.toUpperCase(), "UCLEN" );
		        if( language.equals("") != true )
		          	input.setValue(         language.toUpperCase(), "LANGU" );
		        if( alltype.equals("") != true )
		          	input.setValue(         language.toUpperCase(), "ALL_TYPES" );
	
		        // Get a client from the pool
		        client = JCO.getClient(jcoConn.getPool().getName());
	
		        client.execute(function);
	
		        //The export parameter 'RFCSI_EXPORT' contains a structure of type 'RFCSI'
		        //boolean isStructure = function.getTableParameterList().isStructure("FUNCTIONS");
		        JCO.Table s = function.getTableParameterList().getTable("DFIES_TAB");
	
		          // Use enumeration to loop over all fields of the structure
		        System.out.println("System info for " + jcoConn.getPool().getName() + ":\n" +
		          				   "--------------------");
	
		  		if (s.getNumRows() > 0)
		  		{
	
		  			// Loop over all rows
		  			do
		  			{
		  				JCOFieldStructure fieldInfo = new JCOFieldStructure();
		  				//for (int i = 0; i < s.getFieldCount(); i++)
		  				//{
		  				fieldInfo.fieldname = s.getString("FIELDNAME");
		  				//fieldInfo.position = s.getString("POSITION");
		  				fieldInfo.length = s.getInt("OUTPUTLEN");
	  					fieldInfo.decimals = s.getInt("DECIMALS");
	  					fieldInfo.type = s.getInt("INTTYPE");
	  					fieldInfo.fieldtext = s.getString("FIELDTEXT");
	  					//}//for
			  	    
		  				functionArray.add(fieldInfo);
			  		  	
			  				System.out.println("-----------------------------------------");
	
			  				// Loop over all columns in the current row
				  		    for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); )
				  		    {
				  		    	JCO.Field fieldS = e.nextField();
				  		    	System.out.println(fieldS.getName() + ":\t" + fieldS.getString());
				  		    }//for
				  		    
		  			} while(s.nextRow());
		  		}
		  		else
		  		{
		  			System.out.println("No results found");
		  		}//if
	
		          // Release the client into the pool
		  		JCO.releaseClient(client);
	        }
	        else
	        {
	        	System.out.println("Function DDIF_FIELDINFO_GET not found in backend system.");
	  	  	}
		}
		/*
			catch (JCO.AbapException ex)
			{
					System.out.println(ex.getMessage());
			}
		*/	    
		catch (JCO.Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			throw ex;
		}

		catch (Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			throw ex;
		}
		
		return functionArray;
	}

	public synchronized ArrayList rfcGetFunctionFieldInfo(int nKey, String functionname) throws JCO.Exception, Exception
	{
		ArrayList fieldArray = new ArrayList(100);
		
		//try
		//{
			JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+nKey);
	    	JCO.Repository repository = jcoConn.getRepository();
	    	IFunctionTemplate ftemplate = null;
	    	
	        // Get a function template from the repository
	        ftemplate = repository.getFunctionTemplate(functionname);
	
	        // if the function definition was found in backend system
	        if(ftemplate != null)
	        {
	        	//int i;
	        	JCO.FieldIterator e, et;
 	        	JCO.Field field = null;
 	        	String typename;
 	        	//String name;
	        	boolean bs;
	        	boolean bt;
	        	//String strde;
	        	//int len;
	        	//int type;
	        	//boolean isOpt;
	        	//int index = 0;
	        	JCO.Field F = null;
	        	JCO.ParameterList paramlist = null;

	        	// Create a function from the template
	        	JCO.Function function = ftemplate.getFunction();
	        	
	        	paramlist = function.getImportParameterList();
	        	if( paramlist != null )
	        	{
	   	            for (e = paramlist.fields(); e.hasMoreElements(); )
		            {
		                field = e.nextField();
		                
		                typename = field.getTypeAsString();
		                
			        	bs = field.isStructure();
			        	bt = field.isTable();
			        	
			        	if( bs == true )
			        	{
			        		JCO.Structure S = null;
			        		S = field.getStructure();
				  		    for (et = S.fields(); et.hasMoreElements();)
				  		    {
				  		    	F = et.nextField();
				  			  
				  		    	JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
								fieldInfo.paramname = field.getName(); // parameter name
								fieldInfo.fieldname = F.getName();			// field name
								fieldInfo.type = F.getType();			// field type
								fieldInfo.typename = F.getTypeAsString();
								//fieldInfo.type = F.getType();				// field type
								fieldInfo.length = F.getLength();				// field length
								fieldInfo.decimals = F.getDecimals();			// field decimals	
								fieldInfo.fieldtext = F.getDescription();		// field description
											  			  
								fieldArray.add(fieldInfo);
				  		    }//for
	 		        	}
			        	else if( bt == true )
			        	{
				        	JCO.Table T = null;
			        		T = field.getTable();
				  		    for (et = T.fields(); et.hasMoreElements();)
				  		    {
				  		    	F = et.nextField();
				  			  
				  		    	JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  		    	fieldInfo.paramname = field.getName(); // parameter name
				  		    	fieldInfo.fieldname = F.getName();			// field name
				  		    	fieldInfo.type = F.getType();			// field type
				  		    	fieldInfo.typename = F.getTypeAsString();
				  		    	//fieldInfo.type = F.getType();				// field type
				  		    	fieldInfo.length = F.getLength();				// field length
				  		    	fieldInfo.decimals = F.getDecimals();			// field decimals	
				  		    	fieldInfo.fieldtext = F.getDescription();		// field description
				  			  
				  		    	fieldArray.add(fieldInfo);
				  		    }//for	
			        	}
			        	else
			        	{
				  			  JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  			  fieldInfo.paramname = field.getName(); // parameter name
				  			  fieldInfo.fieldname = field.getName();			// field name
				  			  fieldInfo.type = field.getType();			// field type
				  			  fieldInfo.typename = field.getTypeAsString();
				  			  //fieldInfo.type = field.getType();				// field type
				  			  fieldInfo.length = field.getLength();				// field length
				  			  fieldInfo.decimals = field.getDecimals();			// field decimals	
				  			  fieldInfo.fieldtext = field.getDescription();		// field description
				  			  
				  			  fieldArray.add(fieldInfo);
			        	}
		            }
	        	}
	            
	        	paramlist = function.getExportParameterList();
	        	if( paramlist != null )
	        	{
		            for (e = paramlist.fields(); e.hasMoreElements(); )
		            {
	 	                field = e.nextField();
	
			        	bs = field.isStructure();
			        	bt = field.isTable();
			        	
			        	if( bs == true )
			        	{
			        		JCO.Structure S = null;
			        		S = field.getStructure();
				  		    for (et = S.fields(); et.hasMoreElements();)
				  		    {
				  			  F = et.nextField();
	
				  			  JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  			  fieldInfo.paramname = field.getName(); // parameter name
				  			  fieldInfo.fieldname = F.getName();			// field name
				  			  fieldInfo.type = F.getType();			// field type
				  			  fieldInfo.typename = F.getTypeAsString();
				  			  //fieldInfo.type = F.getType();				// field type
				  			  fieldInfo.length = F.getLength();				// field length
				  			  fieldInfo.decimals = F.getDecimals();			// field decimals	
				  			  fieldInfo.fieldtext = F.getDescription();		// field description
				  			  
				  			  fieldArray.add(fieldInfo);
				  			  
				  		    }//for		        		
	 		        	}
			        	else if( bt == true )
			        	{
				        	JCO.Table T = null;
			        		T = field.getTable();
				  		    for (et = T.fields(); et.hasMoreElements();)
				  		    {
				  			  F = et.nextField();
		
				  			  JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  			  fieldInfo.paramname = field.getName(); // parameter name
				  			  fieldInfo.fieldname = F.getName();			// field name
				  			  fieldInfo.type = F.getType();			// field type
				  			  fieldInfo.typename = F.getTypeAsString();
				  			  //fieldInfo.type = F.getType();				// field type
				  			  fieldInfo.length = F.getLength();				// field length
				  			  fieldInfo.decimals = F.getDecimals();			// field decimals	
				  			  fieldInfo.fieldtext = F.getDescription();		// field description
				  			  
				  			  fieldArray.add(fieldInfo);
				  			  
				  		    }//for	
			        	}
			        	else
			        	{
				  			  JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  			  fieldInfo.paramname = field.getName(); // parameter name		        		
				  			  fieldInfo.fieldname = field.getName();			// field name
				  			  fieldInfo.type = field.getType();			// field type
				  			  fieldInfo.typename = field.getTypeAsString();
				  			  //fieldInfo.type = field.getType();				// field type
				  			  fieldInfo.length = field.getLength();				// field length
				  			  fieldInfo.decimals = field.getDecimals();			// field decimals	
				  			  fieldInfo.fieldtext = field.getDescription();		// field description
				  			  
				  			  fieldArray.add(fieldInfo);
			        	}
		            }
	        	}
	        	
	        	paramlist = function.getTableParameterList();
	        	if( paramlist != null )
	        	{
		            for (e = paramlist.fields(); e.hasMoreElements(); )
		            {
	 	                field = e.nextField();
	
			        	bs = field.isStructure();
			        	bt = field.isTable();
			        	
			        	if( bs == true )
			        	{
			        		JCO.Structure S = null;
			        		S = field.getStructure();
				  		    for (et = S.fields(); et.hasMoreElements();)
				  		    {
				  		    	F = et.nextField();
	
				  		    	JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  		    	fieldInfo.paramname = field.getName(); // parameter name
				  		    	fieldInfo.fieldname = F.getName();			// field name
				  		    	fieldInfo.type = F.getType();			// field type
				  		    	fieldInfo.typename = F.getTypeAsString();
				  		    	//fieldInfo.type = F.getType();				// field type
				  		    	fieldInfo.length = F.getLength();				// field length
				  		    	fieldInfo.decimals = F.getDecimals();			// field decimals	
				  		    	fieldInfo.fieldtext = F.getDescription();		// field description
				  			  
				  		    	fieldArray.add(fieldInfo);
				  			  
				  		    }//for		        		
	 		        	}
			        	else if( bt == true )
			        	{
				        	JCO.Table T = null;
			        		T = field.getTable();
				  		    for (et = T.fields(); et.hasMoreElements();)
				  		    {
				  		    	F = et.nextField();
	
				  		    	JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  		    	fieldInfo.paramname = field.getName(); // parameter name
				  		    	fieldInfo.fieldname = F.getName();			// field name
				  		    	fieldInfo.type = F.getType();			// field type
				  		    	fieldInfo.typename = F.getTypeAsString();
				  		    	//fieldInfo.type = F.getType();				// field type
				  		    	fieldInfo.length = F.getLength();				// field length
				  		    	fieldInfo.decimals = F.getDecimals();			// field decimals	
				  		    	fieldInfo.fieldtext = F.getDescription();		// field description
				  			  
				  		    	fieldArray.add(fieldInfo);
				  			  
				  		    }//for	
			        	}
			        	else
			        	{
				  			  JCOFieldStructure fieldInfo = new JCOFieldStructure();
				  			  
				  			  fieldInfo.paramname = field.getName(); // parameter name		        		
				  			  fieldInfo.fieldname = field.getName();			// field name
				  			  fieldInfo.type = field.getType();			// field type
				  			  fieldInfo.typename = field.getTypeAsString();
				  			  //fieldInfo.type = field.getType();				// field type
				  			  fieldInfo.length = field.getLength();				// field length
				  			  fieldInfo.decimals = field.getDecimals();			// field decimals	
				  			  fieldInfo.fieldtext = field.getDescription();		// field description
				  			  
				  			  fieldArray.add(fieldInfo);
			        	}
		            }
	        	}
	        }
	        else
	        {
	        	System.out.println("Function not found in backend system.");
	  	  	}
	    //}
	    /*
		catch (JCO.AbapException ex)
		{
			System.out.println(ex.getMessage());
		}
		catch (JCO.Exception ex)
		{
			ex.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		*/		
		return fieldArray;
	}	
	
	public synchronized ArrayList rfcExecuteFunction(int nKey, String functionname, String importvalue, String tablevalue) throws JCO.Exception, Exception 
	{
		int i, j, k;
		String result = null;
		ArrayList arr = new ArrayList();
		JCO.Client client = null;
		boolean isStructure = false;
		JCO.Structure structure = null;
		JCO.Field param = null;
		int fieldsize;
		JCO.Table table = null;

		try
		{
	  		JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+nKey);
	    	JCO.Repository repository = jcoConn.getRepository();
	    	
	    	functionname.toUpperCase();
	    	IFunctionTemplate ftemplate = repository.getFunctionTemplate(functionname);
	    	
	    	String[] arrayvalues;	// input 값을  param separator로 분리한 array
	    	String[] paramvalue;	// 
    		String field;			// field1|field2|field3|....
    		String[] fieldname;		// field를 COL_SEP로 split
	    	int size;
	    	String tmp;
	    	String paramname;
	    	String value;
	    	String[] fieldvalue;	
	    	
	    	if(ftemplate != null)
	    	{
	    		JCO.Function function = ftemplate.getFunction();
	    		
	    		arrayvalues = importvalue.split(""+SSEP);
	    		size = arrayvalues.length;
	    		for(i=0; i<size; i++)
	    		{
	    			if(arrayvalues[i] != null && !arrayvalues[i].equals("")) 
	    			{
		    			paramvalue = arrayvalues[i].split(""+ROW_SEP,3);
		    			int chcek = paramvalue.length;
		    			paramname = paramvalue[0];
		    			//value = paramvalue[2];
		    			
			    		param = getImportParam(function, paramname);
			    		if( param != null )
			    		{
			    			isStructure = param.isStructure();
			    			if(isStructure == false)
			    			{
			    				value = paramvalue[2];
			    				param.setValue(value.toUpperCase());
			    			}
			    			else
			    			{
			    				structure = param.getStructure();
				    			field = paramvalue[1];
				    			fieldname = field.split(""+COL_SEP);
			    				fieldsize = fieldname.length;
			    				fieldvalue = paramvalue[2].split(""+COL_SEP, fieldsize);
			    				
			    				for(j=0;j<fieldsize;j++)
			    				{
			    					structure.setValue(fieldvalue[j].toUpperCase(),fieldname[j].toUpperCase());
			    				}
			    				
			    			}
			    		}
	    			}
	    		}
	    		
	    		arrayvalues = tablevalue.split(""+SSEP);
	    		size = arrayvalues.length;
	    		
	    		for(i=0 ; i < size; i++) // table param count 
	    		{
	    			tmp = arrayvalues[i];
	    			if( tmp != null && !tmp.equals("") )
	    			{
		    			paramvalue = tmp.split(""+ROW_SEP);
		    			paramname = paramvalue[0];
		    			param = getTableParam(function, paramname);
		    			
		    			if( param != null)
		    			{
		    				table = param.getTable();
		    				if(table != null)
		    				{
				    			field = paramvalue[1];
				    			fieldname = field.split(""+COL_SEP);
				    			fieldsize = fieldname.length;
				    			
				    			int valuesize; //table value size
				    			valuesize = paramvalue.length; //0: talbe parameter name 1: field name
				    			table.appendRows(valuesize-2);
				    			table.firstRow();
				    			for(j=2 ; j<valuesize; j++ )
				    			{
				    				fieldvalue = paramvalue[j].split(""+COL_SEP, fieldsize);
				    				
				    				for(k=0; k<fieldsize ; k++)
				    				{
				    					table.setValue(fieldvalue[k].toUpperCase(),fieldname[k].toUpperCase());
				    				}
				    				
				    				table.nextRow();
				    			}
		    				}
		    			}
	    			}
	    		}
	    		
		        client = JCO.getClient(jcoConn.getPool().getName());
		    	client.execute(function);	
		    	
		    	arr  = makeResultSet(function);
		    	/*
				JCO.Table s = function.getTableParameterList().getTable("FIELDS");

				s.writeHTML("c:\\FIELDS_LIST.html");
				s.writeXML("c:\\FIELDS_LIST.xml");
			
				s = function.getTableParameterList().getTable("OPTIONS");
				s.writeHTML("c:\\OPTIONS.html");
				s.writeXML("c:\\OPTIONS.xml");
				s = function.getTableParameterList().getTable("DATA");
				s.writeHTML("c:\\DATA.html");
				s.writeXML("c:\\DATA.xml");
				*/		    		
		    	/*
				s.writeHTML("c:\\FIELDS_LIST.html");
				s.writeXML("c:\\FIELDS_LIST.xml");
				// Use enumeration to loop over all fields of the structure
				System.out.println("System info for " + jcoConn.getPool().getName() + ":\n" +
								"--------------------");
				if (s.getNumRows() > 0)
				{
					// Loop over all rows
					do
					{
						System.out.println("-----------------------------------------");
	
						// Loop over all columns in the current row
						for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); )
						{
							JCO.Field field = e.nextField();
							System.out.println(field.getName() + ":\t" + field.getString());
						}//for

					} while(s.nextRow());
				}

				System.out.println("\n\n");

				s = function.getTableParameterList().getTable("DATA");
	
				s.writeHTML("c:\\DATA_LIST.html");
				s.writeXML("c:\\DATA_LIST.xml");


				if (s.getNumRows() > 0)
				{
	
					// Loop over all rows
					do
					{
						System.out.println("-----------------------------------------");

						// Loop over all columns in the current row
						for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); )
						{
							JCO.Field field = e.nextField();
							System.out.println(field.getName() + ":\t" + field.getString());
						}//for

					} while(s.nextRow());
	
				}

				System.out.println("\n\n");
				*/	    		
	    	}
	    	else
	    	{
	    		
	    	}
	    	
		}
		/*		
		catch(IOException e)
		{
			e.printStackTrace();
		}
		*/
	    	
		catch (JCO.Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			//ex.printStackTrace();
			throw ex;
		}
		catch (Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			//ex.printStackTrace();
			throw ex;
		}	
		if( client != null )
			JCO.releaseClient(client);
		
		return arr;
	}
	
	public ArrayList makeResultSet(JCO.Function function) throws JCO.Exception, Exception
	{
		ArrayList ResultArray = new ArrayList(100);
		int i, j, size, nCount, nLength;
		String name, value;
		JCO.Field field = null;
		JCO.Structure structure = null;
		JCO.Table table = null;
		String result;
		//StringBuffer 		lm_strBf = new StringBuffer();
		JCOParamResult paramset = null;

		//ArrayList array = new ArrayList(100);
		JCO.ParameterList list = function.getExportParameterList();
		if(list == null)
			size = 0;
		else
			size = list.getNumFields();
		
		boolean isStructure = false;
		
		for(i=0; i<size; i++) // export parameter가 존재하면....
		{
			//lm_strBf.setLength(0);

			//lm_strBf.append(SSEP);
			field = list.getField(i);
			//lm_strBf.append(COL_SEP);
			//lm_strBf.append(""+i);
			//lm_strBf.append(ROW_SEP);
			
			isStructure = field.isStructure();
			if( isStructure )
			{
				paramset = new JCOParamResult();
				paramset.paramname = "";
				paramset.fieldname = "";
				paramset.result = "";
				paramset.paramclass = "";
				
				paramset.paramname = field.getName();
				paramset.paramclass = "E";
				//lm_strBf.append(field.getName()); //param name
				//result += field.getName();
				
				//lm_strBf.append(ROW_SEP);
				//result += ROW_SEP;
				
				structure = field.getStructure();
				int length = structure.getFieldCount();
				for(j=0; j<length; j++)
				{
					paramset.fieldname += structure.getField(j).getName();
					//lm_strBf.append(structure.getField(j).getName());
					//result += structure.getField(j).getName();
					
					if(j != length-1) 
					{
						//lm_strBf.append(COL_SEP);
						paramset.fieldname += COL_SEP;
						//result += COL_SEP;
					}
					else
					{
						//lm_strBf.append(ROW_SEP);
						//paramset.fieldname += ROW_SEP;
						//result += ROW_SEP;
					}
					
				}

				for(j=0; j<length; j++)
				{
					//value = structure.getString(j);
					paramset.result += structure.getString(j);
					//lm_strBf.append(value);
					//result += value;
					
					if(j != length-1) 
					{
						paramset.result += COL_SEP;
						//lm_strBf.append(COL_SEP);
						//result += COL_SEP;
					}
					else
					{
						//lm_strBf.append(ROW_SEP);
						//result += ROW_SEP;
					}
				}
				
				//nCount = lm_strBf.length();
				//nLength = result.length();
				
				//lm_strBf.deleteCharAt(nCount-1);
				
				//nCount = lm_strBf.length();
				
				
				//result = lm_strBf.toString();
				
				//result = result.substring(0, result.length()-1 );
				
				ResultArray.add(paramset);
				//lm_strBf.append(SSEP);
			}
			else
			{
				paramset = new JCOParamResult();
				paramset.paramname = "";
				paramset.fieldname = "";
				paramset.result = "";
				paramset.paramclass = "";
				
				paramset.paramname = field.getName();
				paramset.paramclass = "E";
				
				if(field.getValue() != null)
					paramset.result = field.getValue().toString();
				
				ResultArray.add(paramset);
			}
		}
		
		list = function.getTableParameterList();
		if(list == null)
			size = 0;
		else
			size = list.getNumFields();
		for(i=0; i<size; i++)
		{
			//lm_strBf.setLength(0);
			//lm_strBf.append(SSEP);
			field = list.getField(i);
			//lm_strBf.append(COL_SEP);
			//lm_strBf.append(""+i);
			//lm_strBf.append(ROW_SEP);
			//lm_strBf.append(field.getName()); //param name
			//lm_strBf.append(ROW_SEP);
			
			boolean isTable = field.isTable();
			if( isTable )
			{
				paramset = new JCOParamResult();
				paramset.paramname = "";
				paramset.fieldname = "";
				paramset.result = "";
				paramset.paramclass = "";
				//lm_strBf.append(field.getName()); //param name
				paramset.paramname = field.getName();
				paramset.paramclass = "T";
				//lm_strBf.append(ROW_SEP);
				
				table = field.getTable();
				int length = table.getFieldCount();
				for(j=0; j<length; j++)
				{
					paramset.fieldname += table.getField(j).getName();
					//lm_strBf.append(table.getField(j).getName());
					
					if(j != length-1) 
					{
						paramset.fieldname += COL_SEP;
						//lm_strBf.append(COL_SEP);
					}
					//else
						//lm_strBf.append(ROW_SEP);
					
				}
				
				int nsize = table.getNumRows();
				int loop=0;
				if( nsize > 0 )
				{
		  			do
		  			{
						for(j=0; j<length; j++)
						{
							value = table.getString(j);
							System.out.println(value);
							//lm_strBf.append(value);
							paramset.result += value;
							
							if(j != length-1) 
							{
								paramset.result += COL_SEP;
								//lm_strBf.append(COL_SEP);
							}
							//else
								//lm_strBf.append(ROW_SEP);
						}
						
						loop++;
						if( loop < nsize )
						{
							//lm_strBf.append(ROW_SEP);
							paramset.result += ROW_SEP;
						}
						
		  			}
		  			while( table.nextRow());
		  			
		  			result = paramset.result.replace('\0', '#');
		  			paramset.result = result;
		  			//System.out.println(result);
		  			//nCount = lm_strBf.length();
					
		  			//lm_strBf.deleteCharAt(nCount-1);
					
					//nCount = lm_strBf.length();

					//lm_strBf.setCharAt(nCount-1, (char)Integer.parseInt(""));
					
		  			//result = lm_strBf.toString();
					
		  			//result = result.substring(0, result.length()-1 );
					
					ResultArray.add(paramset);
					//lm_strBf.append(SSEP);
		  			
				}
				else
				{
					//nCount = lm_strBf.length();
					
					//lm_strBf.deleteCharAt(nCount-1);
					
					//nCount = lm_strBf.length();
					
					//result = lm_strBf.toString();
					//result = result.substring(0, result.length()-1 );
					ResultArray.add(paramset);
					//lm_strBf.append(SSEP);
				}
					
					
			}
		}
		//nCount = lm_strBf.length();
		//lm_strBf.deleteCharAt(nCount-1);
		
		//System.out.println(lm_strBf);
		//array.add(lm_strBf);
		
		return ResultArray;
	}
	
	public JCO.Field getImportParam(JCO.Function function, String paramname) throws JCO.Exception
	{
		JCO.Field rtnfield = null;
		int i;
		int sizeofinput;

		JCO.ParameterList param = null;
		JCO.FieldIterator e;
		
		param = function.getImportParameterList();
		
        for (e = param.fields(); e.hasMoreElements(); )
        {
            JCO.Field field = e.nextField();
            if( paramname.equals(field.getName()) == true)
            {
            	rtnfield = field;
            	break;
            }
        }

		return rtnfield;
	}
	
	public JCO.Field getTableParam(JCO.Function function, String paramname) throws JCO.Exception
	{
		JCO.Field rtnfield = null;
		int i;
		int sizeofinput;

		JCO.ParameterList param = null;
		JCO.FieldIterator e;
		
		param = function.getTableParameterList();
		
        for (e = param.fields(); e.hasMoreElements(); )
        {
            JCO.Field field = e.nextField();
            if( paramname.equals(field.getName()) == true)
            {
            	rtnfield = field;
            	break;
            }
        }

		return rtnfield;
	}
	// Retrieves and prints information about the remote system
	public synchronized void rfcGetSystemInfo(int nKey) throws JCO.Exception, Exception
	{
		JCO.Client client = null;
	  	try
		{
	  		JCOConnection jcoConn = (JCOConnection)mConnInfo.get(""+nKey);
	    	JCO.Repository repository = jcoConn.getRepository();
	    	// Get a function template from the repository
	    	IFunctionTemplate ftemplate = repository.getFunctionTemplate("RFC_SYSTEM_INFO");

	    	// if the function definition was found in backend system
	    	if(ftemplate != null)
	    	{
	    		// Create a function from the template
	    		JCO.Function function = ftemplate.getFunction();

	    		// Get a client from the pool
	    		client = JCO.getClient(jcoConn.getPool().getName());

	    		// We can call 'RFC_SYSTEM_INFO' directly since it does not need any input parameters
	    		client.execute(function);

	    		// The export parameter 'RFCSI_EXPORT' contains a structure of type 'RFCSI'
	    		JCO.Structure s = function.getExportParameterList().getStructure("RFCSI_EXPORT");

	    		// Use enumeration to loop over all fields of the structure
	    		System.out.println("System info for " + jcoConn.getPool().getName() + ":\n" +
	        				   "--------------------");

	    		for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); )
	    		{
	    			JCO.Field field = e.nextField();
	    			System.out.println(field.getName() + ":\t" + field.getString());
	    		}//for

	    		System.out.println("\n\n");

	    		// Release the client into the pool
	    		JCO.releaseClient(client);
	    	}
		    else
		    {
		    	System.out.println("Function RFC_SYSTEM_INFO not found in backend system.");
			}
	    }
		/*
			catch (JCO.AbapException ex)
			{
				System.out.println(ex.getMessage());
			}
		*/	    
	    
		catch (JCO.Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			//ex.printStackTrace();
			throw ex;
		}
		
		catch (Exception ex)
		{
			if( client != null )
				JCO.releaseClient(client);
			//ex.printStackTrace();
			throw ex;
		}
		
	}
	
	public String getRFCInfo( String strQry, String strDBInfo ) throws JCO.Exception
	{
		
		String strResultSet = "";
		/*		
		String strKey = "123456";
		if(strKey == null || strKey.length() < 2)
			strKey = "123456";

		String jndiname = null;
		boolean flag = false;

		RQCacheImpl cache = new RQCacheUseImpl();
		RunInfo runInfo = new RunInfo(strKey, jndiname, flag, null, strQry, true,
									null, null, null, null, 0);
		runInfo.setStrDBInfo(strDBInfo);
		strResultSet = cache.setCMger(flag).getResultSet(runInfo);

		runInfo = null;
		*/        
		/////////////////////////
        int i,j;
        JCO.Client mConnection = null;
        JCO.Repository mRepository = null;
        JCO.Function function = null;
      	JCO.Table table = null;
      	JCO.Field param = null;
      	JCO.FieldIterator e = null;
      	IFunctionTemplate ftemplate = null;
      	JCO.ParameterList paramlist = null;
      	boolean isStructure = false;
      	JCO.Structure structure = null;
      	String paramName;
      	String JCOPrmNm = "";
      	String JCOPrmTbl = "";
      	String tableName = "";// 확인용...
      	String strFieldRow; // 확인용...

        //String strClient = "800";
        //String strAccount = "easybase";
        //String strPassword = "reqube";
        //String strLanguage = "EN";
        //String strHost = "70.2.216.41";
        //String strSysNumber = "05";
        
        String strClient = connInfo.Client;
        String strAccount = connInfo.id;
        String strPassword = connInfo.pw;
        String strLanguage = connInfo.Language;
        String strHost = connInfo.host;
        String strSysNumber = connInfo.SysNumber;
        // for load balancing
        String strJcoConnType = connInfo.jcoConnType;
        String strR3name      = connInfo.r3name;
        String strGroup       = connInfo.group;

        String SID            = connInfo.key;
        //int iPoolsize         = Integer.parseInt(connInfo.poolsize);
        
        try{
        	if(strJcoConnType == null){
        		mConnection = JCO.createClient(strClient, 	// SAP client
    					strAccount,           		// userid
    					strPassword,                // password
    					strLanguage,                // language
    					strHost,                    // application server host name
    					strSysNumber);              // system number
        	}else{
        		if(strJcoConnType.equals("LB")){
            		mConnection = JCO.createClient(strClient, 
            						strAccount, 
            						strPassword, 
            						strLanguage, 
            						strHost, 
            						strR3name, 
            						strGroup);
            	}else{
            		mConnection = JCO.createClient(strClient, 	// SAP client
        					strAccount,           		// userid
        					strPassword,                // password
        					strLanguage,                // language
        					strHost,                    // application server host name
        					strSysNumber);              // system number
            	}
        	}
        	
        }catch(JCO.Exception ex){
        	ex.printStackTrace();
        }catch(Exception ex2){
        	ex2.printStackTrace();
        }
        mConnection.connect();
        String name = strHost + strSysNumber;
        mRepository = new JCO.Repository(name, mConnection);

      	//String strFunctionNmae = "ZYSH_0001";
      	String strFunctionNmae = rootJCOFncNm;									// root SQLStmt의 Function name (JCOFucNm)
      	
        ftemplate = mRepository.getFunctionTemplate(strFunctionNmae);
        
        function = ftemplate.getFunction();
        
        if (function == null)
        {
          System.exit(1);
        }
        // Input value(Import Parameter) Setting !!!!!!!!!        
        paramlist = function.getImportParameterList();
        if(paramlist != null)
        {
        for (e = paramlist.fields(); e.hasMoreElements(); )
        {
            param = e.nextField();
            paramName = param.getName();
            if( param != null )
    		{
    			isStructure = param.isStructure();
    			if(isStructure == false)
    			{
    				//if(paramName.compareToIgnoreCase("INPUT_LAISO") == 0 )
    				//	param.setValue("EN");
    				
    				// charvalue
    				if(paramName != null && !paramName.equals("")){
    					ArrayList lma = (ArrayList) charvalue.get( (String) paramName );  	// lma : 		[[JCOPrmTbl, JCOPrmNm, field1, value1]]
    					if(lma != null && lma.size() != 0 ) {
        					ArrayList lm_lma = (ArrayList) lma.get(0);									// lm_lma : 	[JCOPrmTbl, JCOPrmNm, field1, value1]
        					
        					JCOPrmNm = (String) lm_lma.get(1);											// JCOPrmNm
        					String field = (String) lm_lma.get(2); 												// filed1
        					String fvalue = (String)lm_lma.get(3); 										// value1 

        					//param set
        					if( JCOPrmNm.equalsIgnoreCase(paramName) ) {
        						param.setValue(fvalue);
        					}
    					}
    				}
    				
    			}
    			else
    			{
    				structure = param.getStructure();
    				paramName = param.getName();
    				
    				// if(paramName.compareToIgnoreCase("INPUT_STRUCTURE") == 0)
    				// {
    				//  for(i=0;i<structure.getNumFields();i++)
    				//	{
	    			//	structure.setValue("재규아저씨~~~!!", "FIELDNAME");
	    			//	structure.setValue("123456", "OFFSET");
	    			//	structure.setValue("555555", "LENGTH");
	    			//	structure.setValue("그만자요~!!!", "FIELDTEXT");
    				// }
    				//}
    				
    				// structurevalue 
    				if(paramName != null && !paramName.equals("") ){
    					ArrayList lma = (ArrayList) structurevalue.get( (String) paramName ); 		// lma :  [ [JCOPrmTbl, JCOPrmNm, field1, value1] , [JCOPrmTbl, JCOPrmNm, field2, value2] ... ]
    					if(lma != null){
        					Iterator it = lma.iterator();
        					while(it.hasNext()){
        						ArrayList lm_lma = (ArrayList) it.next(); 											// lm_lma : [JCOPrmTbl, JCOPrmNm, field1, value1]
        						JCOPrmTbl = (String) lm_lma.get(0);
        						JCOPrmNm = (String) lm_lma.get(1);
        						
        						tableName = param.getStructure().getName();										// ????? JCOPrmTbl 이 이게 맞는지 모르겠음..
        						
        						if( JCOPrmNm.equalsIgnoreCase(paramName) && JCOPrmTbl.equalsIgnoreCase(tableName) ) {
            						//param set
            						structure.setValue( (String) lm_lma.get(3), (String) lm_lma.get(2) );	// structure.setValue(value, field)
        						}
        						
        						
        					}
    					}
    					
    				}// end of if
     			}
    		}            
        }
        }
        // Input value(Table Parameter) Setting !!!!!!!!!        
        paramlist = function.getTableParameterList();
        if(paramlist != null)
        {
        for (e = paramlist.fields(); e.hasMoreElements(); )
        {
            param = e.nextField();
            paramName = param.getName();
            table = param.getTable();
            tableName = table.getName();
			if(table != null)
			{
				//if(paramName.compareToIgnoreCase("IN_TABLE") == 0)
				//{
					//table.setRow(2);
					//table.deleteRow();
					//table.deleteRow(5);
					//table.appendRow();
					//table.setValue("RFC_GET%", "FUNCNAME");
					//table.setValue("Does not exist", "COMP_NAME");
					//table.appendRows(2);
					//table.setValue("YYYY", "COMP_CODE");
					//table.setValue("Does not exist either", "COMP_NAME");
					//table.nextRow();
					//table.setValue("ZZZZ", "COMP_CODE");
					//table.setValue("Nor does this", "COMP_NAME");
					/*					
					table.appendRows(valuesize-2);
					table.firstRow();
					for(j=2 ; j<valuesize; j++ )
					{
						fieldvalue = paramvalue[j].split(""+COL_SEP);
	
						for(k=0; k<fieldsize ; k++)
						{
							table.setValue(fieldvalue[k].toUpperCase(),fieldname[k].toUpperCase());
						}

						table.nextRow();
					}*/	    			
				///}
				
				/*
					{
						{JCOPrmTbl, JCOPrmNm, FUNCNAME	,RFC_GET% , , },
						{JCOPrmTbl, JCOPrmNm, R3STATE		, 				  , , },
						{JCOPrmTbl, JCOPrmNm, PARAMETER , 				  , , }
					}
				 */
				// tablevalue
				if(paramName != null && !paramName.equals("")){
					String[][] lmsa = (String[][]) tablevalue.get( (String) paramName ); 
					if(lmsa != null){
						
						JCOPrmTbl = lmsa[0][0];
						JCOPrmNm = lmsa[0][1];
						int tableFieldCnt = 0 ;
						
						if( JCOPrmNm.equalsIgnoreCase(paramName) && JCOPrmTbl.equalsIgnoreCase(tableName) ) {
							
							int iRunvarType = Integer.parseInt((String)runvarState.get(paramName));
							if(iRunvarType == 1){
								// RunvarType 이 1 일 경우 (사용)
								// runvar를 따로 받아(parameter) "/" 로 split한 갯수만큼 row를 추가 시킨다.
								String sep = "/";
								String runvar = lmsa[0][3];
								
								String[] arrRunvar = runvar.split(sep);
								for(int ar = 0 ; ar < arrRunvar.length ; ar ++){
									table.appendRow();
									String lm_run = arrRunvar[ar];
									
									tableFieldCnt = table.getFieldCount();
									int startpos = 0;
									int endpos = 0;
									for(int fc = 0 ; fc < tableFieldCnt ; fc ++){
										endpos += table.getLength(fc);
										
										if(startpos >= lm_run.length()) break;
										if(endpos >= lm_run.length()) endpos = lm_run.length();
										
										// "/" 로 split한 각 배열의 값은 또다시 필드의 length 만큼 쪼갠다.
										String inputValue = lm_run.substring(startpos, endpos);
										String fname = table.getField(fc).getName();
										table.setValue(inputValue, fname);
										
										startpos = endpos;
									}
								}
								
							}else{
								// RunvarType 이 1이 아닐경우 (미사용, 일반적인 경우)
								for(int x = 3; x < lmsa[0].length ; x++){		
									table.appendRow();
									for(int y = 0; y< lmsa.length; y++){		 
										tableFieldCnt = table.getFieldCount();
										for( int fcnt = 0 ; fcnt < tableFieldCnt ; fcnt++ ){
											String fname = table.getField(fcnt).getName();
											if(  lmsa[y][2].equalsIgnoreCase(fname) ){
												table.setValue( lmsa[y][x],  lmsa[y][2]); 	// table.setValue("RFC_GET%", "FUNCNAME") ..... table.setValue("", "R3STATE");
												break;
											}
										} // end of for loop fcnt
									} // end of for loop y
								} // end of for loop x
								
							}
						}
					}
				}// end of if
			}
        }
        }
        mConnection.execute(function);
        
        //makeResultSet(function, resultset);
        // Output Value(Export Parameter) Getting !!!!!!! 
        
        paramlist = function.getExportParameterList();
        if( paramlist != null)
        {
        for (e = paramlist.fields(); e.hasMoreElements(); )
        {
            param = e.nextField();
            paramName = param.getName();
            int iSQLIdxNow = getSQLIdxwithParamName(paramName);
            if( param != null )
    		{
    			isStructure = param.isStructure();
    			if(isStructure == false)
    			{
    				strFieldRow = "";
    				strFieldRow = param.getName();
    				System.out.println(strFieldRow);
   				
    				strFieldRow = "";
    				if(param.getValue() != null){
    					strFieldRow = param.getValue().toString();
    				}
    				System.out.println(strFieldRow);
    				
    				String charvalue = "";
    				if(param.getValue() != null){
    					charvalue = param.getValue().toString(); 
    				}
    				resultset.append(RQGetDataIf.SSEP);
    				resultset.append(iSQLIdxNow);
    				resultset.append(RQGetDataIf.ROW_SEP);
    				
    			    // set headerInfo
    			    setHeaderInfo(param);   				
    				
    				resultset.append(iRootRow);										
    				resultset.append(RQGetDataIf.COL_SEP);
    				resultset.append("1");							// char 는 row가 1
    				resultset.append(RQGetDataIf.COL_SEP);
    				
    				//resultset.append(charvalue);
    				String lms = "";
    				if(param != null){
    					lms = objectToString(param, 0);
    				}
					resultset.append(lms);
					
    				resultset.append(RQGetDataIf.ROW_SEP);
    				
    			}
    			else
    			{
    				structure = param.getStructure();
    				paramName = structure.getName();
    				
    				/////////////////////check ////////////////////////////////////////////
    				strFieldRow = "";
    				for(i=0;i<structure.getNumFields(); i++)
    				{
    					strFieldRow += structure.getField(i).getName();
    					strFieldRow += '\t';
    				}
					System.out.println(strFieldRow );
					
					strFieldRow = "";
					
					String typename;
					String value;
    			    for ( i = 0; i < structure.getNumFields(); i++)
    			    {
    			    	int type = structure.getField(i).getType();
    			    	typename = structure.getField(i).getTypeAsString();
    			    	int length;
    			    	int decimals;
    			    	float floatval;
    			    	int intval;
    			    	long longval;
    			    	byte[] bytearray;
    			    	char[] chararray;
    			    	java.util.Date date;
    			    	switch(type)
    			    	{
    			    	case JCO.TYPE_CHAR:
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		chararray = structure.getCharArray(i);
    			    		break;
    			    	case JCO.TYPE_DATE:
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		date = structure.getDate(i);
    			    		break;
    			    	case JCO.TYPE_BCD://1~16byte floating
    			    		decimals = structure.getDecimals(i);
    			    		length = structure.getLength(i);
    			    		value = structure.getString(i);
    			    		
    			    		break;
    			    	case JCO.TYPE_TIME:
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		date = structure.getTime(i);
    			    		
    			    		break;
    			    	case JCO.TYPE_BYTE:
    			    		//byteval = structure.getByte(i);
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		bytearray = structure.getByteArray(i);
    			    		break;
    			    	case JCO.TYPE_NUM:
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		
    			    		break;
    			    	case JCO.TYPE_FLOAT:
    			    		decimals = structure.getDecimals(i);
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		floatval = structure.getFloat(i);
    			    		break;
    			    	case JCO.TYPE_INT: // 4byte inteager
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		longval = structure.getLong(i);
    			    		break;
    			    	case JCO.TYPE_INT1: // 1byte inteager
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		longval = structure.getLong(i);
    			    		break;
    			    	case JCO.TYPE_INT2: // 2byte inteager
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		longval = structure.getLong(i);
    			    		break;
    			    	case JCO.TYPE_STRING:
    			    		value = structure.getString(i);
    			    		length = structure.getLength(i);
    			    		break;
    			    		
    			    		
    			    		
     			    	}
    			    	
    			    	strFieldRow += structure.getString(i);
    			    	//strFieldRow += structure.getField(i).getValue().toString();
			          strFieldRow += '\t';
    			    }
    			    System.out.println(strFieldRow );
    			    /////////////////////check end /////////////////////////////////////////////////////////
    			    
    			    resultset.append(RQGetDataIf.SSEP);
    			    resultset.append(iSQLIdxNow);
    			    resultset.append(RQGetDataIf.ROW_SEP);
    			    
    			    // set headerInfo
    			    setHeaderInfo(structure);
    			    
    			    resultset.append(iRootRow);
    			    resultset.append(RQGetDataIf.COL_SEP);
    			    resultset.append("1");										// structure 는 row가 1
    			    resultset.append(RQGetDataIf.COL_SEP);
    			    for ( i = 0; i < structure.getNumFields(); i++){
    			    	
    			    	//resultset.append(   structure.getString(i)  );
						String lms = objectToString(structure, i);
						resultset.append(lms);
						
    			    	if ( i != structure.getNumFields()-1 ) resultset.append(RQGetDataIf.COL_SEP);
    			    }
    			    resultset.append(RQGetDataIf.ROW_SEP);
     			}
    		}            
        }        
        }
        paramlist = function.getTableParameterList();
        if(paramlist != null)
        {
        for (e = paramlist.fields(); e.hasMoreElements(); )
        {
            param = e.nextField();
            paramName = param.getName();
            int iSQLIdxNow = getSQLIdxwithParamName(paramName);
            if( param != null )
    		{

				table = param.getTable();
				paramName = table.getName();
				
				/////////////////////check //////////////////////////////////////////////////
				strFieldRow = "";
				for(i=0;i<table.getNumFields(); i++)
				{
					strFieldRow += table.getField(i).getName();
					strFieldRow += '\t';
				}
				System.out.println(strFieldRow );
				
				for(i=0; i<table.getNumRows(); i++)
				{
					table.setRow(i);
					strFieldRow = "";
					String typename;
					String value;
				    for ( j = 0; j < table.getNumFields(); j++)
				    {
			          //strFieldRow += table.getField(j).getValue().toString();
	   			    	int type = table.getField(j).getType();
    			    	typename = table.getField(j).getTypeAsString();
    			    	int length;
    			    	int decimals;
    			    	float floatval;
    			    	int intval;
    			    	long longval;
    			    	byte[] bytearray;
    			    	char[] chararray;
    			    	java.util.Date date;
    			    	switch(type)
    			    	{
    			    	case JCO.TYPE_CHAR:
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		chararray = table.getCharArray(j);
    			    		break;
    			    	case JCO.TYPE_DATE:
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		date = table.getDate(j);
    			    		break;
    			    	case JCO.TYPE_BCD://1~16byte floating
    			    		decimals = table.getDecimals(j);
    			    		length = table.getLength(j);
    			    		value = table.getString(j);
    			    		
    			    		break;
    			    	case JCO.TYPE_TIME:
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		date = table.getTime(j);
    			    		
    			    		break;
    			    	case JCO.TYPE_BYTE:
    			    		//byteval = structure.getByte(i);
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		bytearray = table.getByteArray(j);
    			    		break;
    			    	case JCO.TYPE_NUM:
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		
    			    		break;
    			    	case JCO.TYPE_FLOAT:
    			    		decimals = table.getDecimals(j);
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		floatval = table.getFloat(j);
    			    		break;
    			    	case JCO.TYPE_INT: // 4byte inteager
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		longval = table.getLong(j);
    			    		break;
    			    	case JCO.TYPE_INT1: // 1byte inteager
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		longval = table.getLong(j);
    			    		break;
    			    	case JCO.TYPE_INT2: // 2byte inteager
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		longval = table.getLong(j);
    			    		break;
    			    	case JCO.TYPE_STRING:
    			    		value = table.getString(j);
    			    		length = table.getLength(j);
    			    		break;
     			    	}
				    	strFieldRow += table.getString(j);
			          strFieldRow += '\t';
				    }
				    System.out.println(strFieldRow );
				    
				}
				/////////////////////check end //////////////////////////////////////////////////			
			    resultset.append(RQGetDataIf.SSEP);
			    resultset.append(iSQLIdxNow);
			    resultset.append(RQGetDataIf.ROW_SEP);
				
			    //set headerInfo
			    setHeaderInfo(table);
			    
				for(i=0; i<table.getNumRows(); i++){
    			    resultset.append(iRootRow);
    			    resultset.append(RQGetDataIf.COL_SEP);
    			    resultset.append(i+1);										
					resultset.append(RQGetDataIf.COL_SEP);
					table.setRow(i);
					for ( j = 0; j < table.getNumFields(); j++){
						
						//resultset.append( table.getString(j) );
						String lms = objectToString(table, j);
						resultset.append(lms);
						
						if ( j != table.getNumFields()-1 ) resultset.append(RQGetDataIf.COL_SEP);
					}
					resultset.append(RQGetDataIf.ROW_SEP);
				}
				

    		}            
        }
        }
        mConnection.disconnect();
		//return strResultSet;
        return resultset.toString();
	}

    public static void main(String[] argv)
	{
    	try
    	{
		JCOController conn = new JCOController();
		/*
		int nKey = conn.connect("800", "easybase", "reqube", "EN", "70.2.216.41", "05");
		

		//conn.rfcGetSystemInfo(nKey);
		//conn.rfcFunctionSearch(nKey,"RFC_FUNCTION_SEARCH","","");
		//conn.rfcGetFunctionInterface(nKey, "DDIF_FIELDINFO_GET","EN");
		//conn.rfcGetFieldInfo(nKey,"BSID","DMBTR","","EN","X");
		
		//conn.rfcGetFunctionFieldInfo(nKey,"DDIF_FIELDINFO_GET");
		
		String importS;
		String tableS;
		
		importS = "LAISO";
		importS += ROW_SEP;
		importS += "LAISO";
		importS += ROW_SEP;
		importS += "EN";
		importS += SSEP;
		
		importS += "OPERATOR";
		importS += ROW_SEP;
		importS += "OPERATOR";
		importS += ROW_SEP;
		importS += "LIKE";
		importS += SSEP;
		
		importS += "EB_FUNC_NAME";
		importS += ROW_SEP;
		importS += "EB_FUNC_NAME";
		importS += ROW_SEP;
		importS += "RFC_GET_F%";
		
		conn.rfcExecuteFunction(nKey,"Z_EB_GETFUNCINFO", importS, tableS="");
		*/		
		//String strXml = "<SQL><SQLStmt DBIdx='0' SQLIdx='0' QryType='2' UIType='3' JCOFncNm='ZYSH_0001' JCOFncGrp='ZEB2' JCOFuncDscpt='test'><SQLData/><ConditionInfo/><HavingInfo/><SQLStmt DBIdx='0' SQLIdx='1' QryType='102' UIType='3' JCOPrmClss='E' JCOPrmNm='EXPORT_STRUCTURE' JCOPrmTbl='RFC_DB_FLD' JCOPrmDscpt='RFC Table Read: Description of Fields to Retrieve' JCOPrmTp='STRUCTURE' JCOPrmValueSize='0'><SQLData/></SQLStmt><SQLStmt DBIdx='0' SQLIdx='2' QryType='102' UIType='3' JCOPrmClss='E' JCOPrmNm='RFCSI_EXPORT' JCOPrmTbl='RFCSI' JCOPrmDscpt='RFC system info (see RFC_SYSTEM_INFO_functionmodule)' JCOPrmTp='STRUCTURE' JCOPrmValueSize='0'><SQLData/></SQLStmt><SQLStmt DBIdx='0' SQLIdx='3' QryType='102' UIType='3' JCOPrmClss='E' JCOPrmNm='RFC_LOGIN_COMPLETE' JCOPrmTbl='SYST' JCOPrmFld='DEBUG' JCOPrmABAPTp='C' JCOPrmDscpt='Internal' JCOPrmTp='CHAR' JCOPrmValueSize='0'><SQLData/></SQLStmt><SQLStmt DBIdx='0' SQLIdx='4' QryType='102' UIType='3' JCOPrmClss='I' JCOPrmNm='INPUT_LAISO' JCOPrmTbl='T002' JCOPrmFld='LAISO' JCOPrmABAPTp='C' JCOPrmDscpt='Client' JCOPrmTp='CHAR' JCOPrmValueSize='1'><SQLData/></SQLStmt><BindSrc ParamName='INPUT_LAISO' FieldName='INPUT_LAISO' Value='en'><![CDATA[en]]></BindSrc><SQLStmt DBIdx='0' SQLIdx='5' QryType='102' UIType='3' JCOPrmClss='I' JCOPrmNm='INPUT_STRUCTURE' JCOPrmTbl='RFC_DB_FLD' JCOPrmDscpt='Accounting: Secondary Index for Vendors' JCOPrmOpt='X' JCOPrmTp='STRUCTURE' JCOPrmValueSize='1'><SQLData/></SQLStmt><BindSrc ParamName='INPUT_STRUCTURE' FieldName='FIELDNAME' Value='재규아저씨~~~!!'><![CDATA[재규아저씨~~~!!]]></BindSrc><BindSrc ParamName='INPUT_STRUCTURE' FieldName='OFFSET' Value='123456'><![CDATA[123456]]></BindSrc><BindSrc ParamName='INPUT_STRUCTURE' FieldName='LENGTH' Value='555555'><![CDATA[555555]]></BindSrc><BindSrc ParamName='INPUT_STRUCTURE' FieldName='TYPE'/><BindSrc ParamName='INPUT_STRUCTURE' FieldName='FIELDTEXT' Value='그만자요~!!!'><![CDATA[그만자요~!!!]]></BindSrc><SQLStmt DBIdx='0' SQLIdx='6' QryType='102' UIType='3' JCOPrmClss='T' JCOPrmNm='IN_TABLE' JCOPrmTbl='FUPARAREF' JCOPrmDscpt='Parameters of function modules' JCOPrmTp='TABLE' JCOPrmValueSize='1'><SQLData/></SQLStmt><BindSrc ParamName='IN_TABLE' FieldName='FUNCNAME' Value='RFC_GET%'><![CDATA[RFC_GET%]]></BindSrc><BindSrc ParamName='IN_TABLE' FieldName='R3STATE'/><BindSrc ParamName='IN_TABLE' FieldName='PARAMETER'/><BindSrc ParamName='IN_TABLE' FieldName='PARAMTYPE'/><BindSrc ParamName='IN_TABLE' FieldName='STRUCTURE'/><BindSrc ParamName='IN_TABLE' FieldName='DEFAULTVAL'/><BindSrc ParamName='IN_TABLE' FieldName='REFERENCE'/><BindSrc ParamName='IN_TABLE' FieldName='PPOSITION'/><BindSrc ParamName='IN_TABLE' FieldName='OPTIONAL'/><BindSrc ParamName='IN_TABLE' FieldName='TYPE'/><BindSrc ParamName='IN_TABLE' FieldName='CLASS'/><BindSrc ParamName='IN_TABLE' FieldName='REF_CLASS'/><BindSrc ParamName='IN_TABLE' FieldName='LINE_OF'/><BindSrc ParamName='IN_TABLE' FieldName='TABLE_OF'/><BindSrc ParamName='IN_TABLE' FieldName='RESFLAG1'/><BindSrc ParamName='IN_TABLE' FieldName='RESFLAG2'/><BindSrc ParamName='IN_TABLE' FieldName='RESFLAG3'/><BindSrc ParamName='IN_TABLE' FieldName='RESFLAG4'/><BindSrc ParamName='IN_TABLE' FieldName='RESFLAG5'/><SQLStmt DBIdx='0' SQLIdx='7' QryType='102' UIType='3' JCOPrmClss='T' JCOPrmNm='OUT_TABLE' JCOPrmTbl='T002' JCOPrmDscpt='Accounting: Secondary Index for Vendors' JCOPrmTp='TABLE' JCOPrmValueSize='0'><SQLData/></SQLStmt><SQLStmt DBIdx='0' SQLIdx='8' QryType='102' UIType='3' JCOPrmClss='T' JCOPrmNm='TABLE_STRUCTURE' JCOPrmTbl='DNTAB' JCOPrmDscpt='DD interface: nametab definition for GET_NAMETAB' JCOPrmTp='TABLE' JCOPrmValueSize='0'><SQLData/></SQLStmt></SQLStmt></SQL>";
		//String strDBInfo = "<Database><DBInfo DBIdx='0' IFType='3' DBID='70.2.216.41 - SAP1' UserID='EASYBASE' Password='reqube' Host='70.2.216.41' Language='EN' Client='800' SysNumber='05'/></Database>";
		
		String strDBInfo = "<Database><DBInfo DBIdx=\"0\" IFType=\"3\" OwnTable=\"0\" DBID=\"*??*FgBbFbHbFeRdBhXdYbOdWdTgIiIhWeBbXdLcJiZczYfZhGhGdVhNjGhfAhvPfUcYfNiOjKgAiVjMb\" UserID=\"*??*hPhRiPiZhJgNiFbKfIgSjUhXfKfQiMg\" Password=\"*??*AeOjHfLjSbDcPfKbYgUcQcThirUfCj\" Host=\"70.7.105.41\" Language=\"EN\" Client=\"800\" SysNumber=\"05\"><![CDATA[*??*FgBbFbHbFeRdBhXdYbOdWdTgIiIhWeBbXdLcJiZczYfZhGhGdVhNjGhfAhvPfUcYfNiOjKgAiVjMb]]><![CDATA[*??*hPhRiPiZhJgNiFbKfIgSjUhXfKfQiMg]]><![CDATA[*??*AeOjHfLjSbDcPfKbYgUcQcThirUfCj]]></DBInfo></Database>";
		String strXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SQL><SQLStmt DBIdx=\"0\" SQLIdx=\"0\" NameIdx=\"1\" QryType=\"2\" UIType=\"3\" JCOFncNm=\"RFC_GET_FUNCTION_INTERFACE\" JCOFncGrp=\"RFC1\"><SQLData/><ConditionInfo/><HavingInfo/><SQLStmt DBIdx=\"0\" SQLIdx=\"1\" NameIdx=\"-1\" QryType=\"102\" UIType=\"3\" JCOPrmClss=\"I\" JCOPrmNm=\"FUNCNAME\" JCOPrmTbl=\"TFDIR\" JCOPrmFld=\"FUNCNAME\" JCOPrmABAPTp=\"C\" JCOPrmDscpt=\"Name of function module\" JCOPrmTp=\"CHAR\" JCOPrmValueSize=\"1\"><SQLData/></SQLStmt><BindSrc ParamName=\"FUNCNAME\" FieldName=\"FUNCNAME\"><![CDATA[RFC_READ_TABLE]]></BindSrc><SQLStmt DBIdx=\"0\" SQLIdx=\"2\" NameIdx=\"-1\" QryType=\"102\" UIType=\"3\" JCOPrmClss=\"I\" JCOPrmNm=\"LANGUAGE\" JCOPrmTbl=\"SYST\" JCOPrmFld=\"LANGU\" JCOPrmABAPTp=\"C\" JCOPrmDflt=\"SY-LANGU\" JCOPrmDscpt=\"Language for parameter texts\" JCOPrmOpt=\"X\" JCOPrmTp=\"CHAR\" JCOPrmValueSize=\"1\"><SQLData/></SQLStmt><BindSrc ParamName=\"LANGUAGE\" FieldName=\"LANGUAGE\"><![CDATA[E]]></BindSrc><SQLStmt DBIdx=\"0\" SQLIdx=\"3\" NameIdx=\"-1\" QryType=\"102\" UIType=\"3\" JCOPrmClss=\"T\" JCOPrmNm=\"PARAMS\" JCOPrmTbl=\"RFC_FUNINT\" JCOPrmDscpt=\"Parameter of function module\" JCOPrmTp=\"TABLE\" JCOPrmValueSize=\"0\"><SQLData/></SQLStmt></SQLStmt></SQL>";
		
		String lm_strRs = conn.rfcExecuteFunction2(strXml,strDBInfo);
		System.out.println(lm_strRs);
		
		//conn.disconnectall();
		
    	}catch(JCO.Exception ex){
    	}catch(Exception e){
		}
	}
	
    /**
     * XML로 받은 DB 정보를 connInfo 로 리턴 하며 파싱된 DB 정보는 
     * JCOController member var. 로 저장
     * @param strDBInfo 
     * @return connInfo
     */
    protected ConnectionInfo getRFCCon(String strDBInfo){
		RQQueryParse oQueryParse = new RQQueryParse();
		try {
			Document doc = oQueryParse.parseXML(strDBInfo);
			List elements = doc.getRootElement().getChildren();
			Iterator it = elements.iterator();
			if(  it.hasNext()  ){
				Element element = (Element) it.next();
				if(element.getName().equals("DBInfo")){
					//String strDB = element.get
					String DBIdx = element.getAttributeValue("DBIdx"); // "0"
					//String IFType = element.getAttributeValue("IFType"); // "3"
					String DBID = decryptEncValue( element.getAttributeValue("DBID") ); // 70.2.216.41 - SAP1
					String UserID = decryptEncValue( element.getAttributeValue("UserID") ); //
					String Password = decryptEncValue( element.getAttributeValue("Password") ); //
					String Host = decryptEncValue( element.getAttributeValue("Host") );
					String Language = decryptEncValue( element.getAttributeValue("Language") );
					String Client = decryptEncValue( element.getAttributeValue("Client") );
					String SysNumber = decryptEncValue( element.getAttributeValue("SysNumber") );
					
					connInfo = new ConnectionInfo();
					connInfo.connidx = Integer.parseInt(DBIdx);
					connInfo.connid = DBID;
					connInfo.id = UserID == null ? "" : UserID.trim();
					connInfo.pw = Password == null ? "" : Password.trim();
					connInfo.host = Host;
					connInfo.Language = Language;
					connInfo.Client = Client;
					connInfo.SysNumber = SysNumber;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connInfo;
	}
	
    /**
     * Decoding
     * @param str
     * @return
     */
    protected String decryptEncValue(String str)
    {
    	
    	if(str != null && str.startsWith("*??*"))
    	{
    		Decrypter dec = new Decrypter("RQREPORT6**??");
    		return dec.decrypt(str.substring(4));
    		
    	}
    	else
    		return str;
    }
    
	/**
	 * XML로 받은 strQry 와 strDBInfo 정보를 파싱한뒤 Root SQLStmt 갯수 만큼 
	 * rfcExecuteFunction2 를 실행하여 결과물을 받아온다.
	 * @param strQry 		문서내 <SQL></SQL> Element - 쿼리정보가 담겨 있다.  
	 * @param strDBInfo		문서내 <Database></Database> Element - DB정보가 담겨 있다.
	 */
    public String rfcExecuteFunction2(String strQry , String strDBInfo){
    	
    	resultset = new StringBuffer(); // resultset : 최종 결과물 (여기서 초기화) 
    	if(strDBInfo != null && !strDBInfo.equals("")){
    		connInfo = getRFCCon(strDBInfo);
    	}
		RQMakeSQLHierarchy oRQMakeSQLHierarchy = new RQMakeSQLHierarchy(strQry);
		oRQMakeSQLHierarchy.makeSQLHierarchy();

		queryframe = oRQMakeSQLHierarchy.getQueryframe();
		
        String[] rootSQLidxs = queryframe.getRootSQLIdx();
        HashMap hM = queryframe.getSource();
        
        ////////////////////////////////////////////////////////
        System.out.println("DataSetFetch start");
		////////////////////////////////////////////////////////
		
        for(int i = 0 ; i < rootSQLidxs.length ; i++){
        	String rootSQLidx = (String)rootSQLidxs[i];
        	QueryHouse rootQueryHouse = (QueryHouse) hM.get( rootSQLidx );
    		 
    		bindSrc = rootQueryHouse.getOBindSrc();
    		rootJCOFncNm = rootQueryHouse.getJCOFncNm(); // root element Function Name
    		
    		// HashMap charvalue 와 structurevalue, tablevalue는 member field로 선언
    		charvalue = new HashMap();
    		structurevalue = new HashMap();
    		tablevalue = new HashMap();
    		
    		getBindSrcEachCondition(charvalue, structurevalue, tablevalue);
    		// fetch start
    		resultset.append(RQGetDataIf.SSEP);
    		resultset.append( rootSQLidx );
    		resultset.append(RQGetDataIf.ROW_SEP);
    		
    		// rootFetch
    		resultset.append( rootSQLidx );
    		resultset.append(RQGetDataIf.COL_SEP);
    		resultset.append(iRootRow); // rootSQL 은 결과물이 없음. Function 그자체 
    		resultset.append(RQGetDataIf.ROW_SEP);
    		 // header info ?
    		
    		// Binary Object 초기화 
    		oRQHBindingSource = new RQHierarchyBindingSource();
    		
    		getRFCInfo(strQry, strDBInfo); 			// rootSQLidxs.length 만큼 loop 를 돈다. 
    		// fetch end.
    		
	        if(oRQHBindingSource.getStfResult() != null ){
				if(!oRQHBindingSource.getStfResult().toString().equals("")){
					resultset.append(oRQHBindingSource.getStfBlob().toString());
				}
	        }
	        
        }
        ///////////////////////////////////////////////////
        System.out.println("DataSetFetch end");
        ////////////////////////////////////////////////
		
        return resultset.toString();
	}
    
    public String getResultString(String p_resultset){
    	String lm_str = p_resultset;
    	lm_str = lm_str.replaceAll("\n", "\\\\n");
    	lm_str = lm_str.replaceAll("\r", "\\\\r");
    	lm_str = lm_str.replaceAll("\t", "\\\\t");
    	return lm_str;
    }
    
	/**
	 * 	Input 조건에 따라 pair 로 이루어진 ArrayList를 만들어
	 * sample) ArrayList arr1 = [  [field1, value1] , [field2, value2], ...  ] , ...
	 * 각각 만들어진 ArrayList 는 HashMap 에 담는다. < key1, arr1>,<key2, arr2> ...
	 * 이 Method를 사용하려면 queryframe이 반드시 있어야 하기 때문에 상속을 통하여 사용하도록 한다.
	 * @param charvalue 		input type 이 char 인 것만 모아둔 map
	 * @param structurevalue 	input type 이 structure 인 것만 모아둔 map
	 * @param tablevalue		input type 이 table 인 것만 모아둔 map
	 */
	protected void getBindSrcEachCondition(HashMap charvalue, HashMap structurevalue, HashMap tablevalue ){
		
		HashMap ohM = queryframe.getSource();
		Collection oChM = ohM.values();
		Iterator itoChM = oChM.iterator();
		runvarState = new HashMap();
		
		while(itoChM.hasNext()){
			QueryHouse queryhouseNow = (QueryHouse) itoChM.next();
			
			// JCOPrmClss 가 "I" 이고 getJCOPrmTp 가 "STRUCTURE" 일경우 
			if (  queryhouseNow.getJCOPrmClss().equals("I")){
				if(queryhouseNow.getJCOPrmTp().equals("STRUCTURE")){
					String JCOPrmNm = queryhouseNow.getJCOPrmNm();
					structurevalue.put( JCOPrmNm , getBindvalueArr( JCOPrmNm  ) ); // hashmap setting (structurevalue)
					
				// JCOPrmClss 가 "I" 이면 모두 
				}else{
					String JCOPrmNm = queryhouseNow.getJCOPrmNm();
					charvalue.put( JCOPrmNm , getBindvalueArr( JCOPrmNm  ) ); // hashmap setting (charvalue)	
				}
			}
			
			// SQLStmt의 JCOPrmClss가 "T" 이고(and)  JCOPrmTp가 "TABLE" 이고 getJCOPrmValueSize > 0 인 SQLStmt의 bindSrc 의 value
			if (  	queryhouseNow.getJCOPrmClss().equals("T")  && 
					queryhouseNow.getJCOPrmTp().equals("TABLE") && 
					Integer.parseInt( queryhouseNow.getJCOPrmValueSize()) > 0) {
				
				String JCOPrmNm = queryhouseNow.getJCOPrmNm();
				tablevalue.put( JCOPrmNm , getInTable( JCOPrmNm , queryhouseNow ) ); // hashmap setting (tablevalue)
				runvarState.put(JCOPrmNm, queryhouseNow.getRunVarType());
				
			}
			
		} // end of while

	}
	
	/**
	 * bindSrc에서 JCOPrmNm 에 맞는 fieldname 과 value 값을 ArrayList 로 반환
	 * @param JCOPrmNm	JCOPrmNm에 해당하는 fieldname과 value를 찾는다.
	 * @return ret  ex)  [ [JCOPrmTbl, JCOPrmNm, field1, value1] , [JCOPrmTbl, JCOPrmNm, field2, value2] ... ]
	 */
	private ArrayList getBindvalueArr(String JCOPrmNm){
		ArrayList ret = new ArrayList();
		Iterator itBindSrc = (Iterator) bindSrc.iterator();
		String JCOPrmTbl = getJCOPrmTblwithParamName(JCOPrmNm);
		
		while(itBindSrc.hasNext()){
			ArrayList bindSrcEle = (ArrayList) itBindSrc.next();
			String cpword = (String) bindSrcEle.get(0); 					// cpword : compare word
			ArrayList lm = new ArrayList();
			if(cpword.equals(JCOPrmNm)){
				lm.add( JCOPrmTbl  ); 										// JCOPrmTbl
				lm.add( cpword ); 											// JCOPrmNm
				lm.add( (String) bindSrcEle.get(1)  ); 						// field name  
				lm.add( ((String) bindSrcEle.get(2)).toUpperCase()  ); 		// field value
				ret.add(lm);
			}
			
		}
		return ret;
	}
	
	/**
	 * IN_TABLE 에 해당하는 값을 2D 배열로 가져옴 
	 * @param JCOPrmNm
	 * @return ex) {
	 * 							{JCOPrmTbl, JCOPrmNm, FUNCNAME	,RFC_GET% , , },
	 * 							{JCOPrmTbl, JCOPrmNm, R3STATE		, 				  , , },
	 * 							{JCOPrmTbl, JCOPrmNm, PARAMETER , 				  , , }
	 * 						}
	 */
	private String[][] getInTable(String JCOPrmNm , QueryHouse queryhouseNow){
		
		String JCOPrmTbl = getJCOPrmTblwithParamName(JCOPrmNm);
		int fieldsize = 0; 
		int valuesize = Integer.parseInt( queryhouseNow.getJCOPrmValueSize() );
		
		// for count
		Iterator it = (Iterator) bindSrc.iterator();
		while(it.hasNext()){
			ArrayList bindSrcEle = (ArrayList) it.next();
			String cpword = (String) bindSrcEle.get(0); 				         // cpword : compare word
			if(cpword.equals(JCOPrmNm)){
				fieldsize++;
			}
		}
		
		String[][] sa = new String[fieldsize][valuesize + 3];  	                 // 3 --> JCOPrmTbl, JCOPrmNm, FieldName
		
		// for fetch
		Iterator itBindSrc = (Iterator) bindSrc.iterator();
		int y = 0;
		while(itBindSrc.hasNext()){
			ArrayList bindSrcEle = (ArrayList) itBindSrc.next();
			String cpword = (String) bindSrcEle.get(0); 				         // cpword : compare word			
			if(cpword.equals(JCOPrmNm)){
				
				String fieldname = (String)bindSrcEle.get(1);		             // field name
				String fieldvalues = ((String)bindSrcEle.get(2)).toUpperCase();	 // field value (   value1(sep)value2(sep)value3....   )
				String[] valueArr = fieldvalues.split(""+RQGetDataIf.SSEP);	 	 // separator  
				
				//sa[y][0] = fieldname;
				sa[y][0] = JCOPrmTbl;
				sa[y][1] = cpword;
				sa[y][2] = fieldname;
				
				for(int x = 0 ; x < valueArr.length ; x ++){			         // value
					sa[y][x+3] =  valueArr[x];
				}
				y++;
				if (y == fieldsize) break ;
			}
		}
		return sa;
	}
	
	/**
	 * paramName 으로 SQLIdx 를 가져온다.
	 * @param paramName
	 * @return
	 */
	private int getSQLIdxwithParamName(String paramName){
		
		int iSQLIdx = -1;
		
		HashMap hm = (HashMap) queryframe.getSource();
		Collection c = hm.values();
		Iterator it = c.iterator();
		for(int i = 0 ; i < c.size(); i++){
			QueryHouse qhnow = (QueryHouse) it.next();
			String JCOPrmNm = qhnow.getJCOPrmNm();
			if ( JCOPrmNm.equals(paramName) ){
				iSQLIdx = qhnow.getSQLIdx();
			}
		}
		
		return iSQLIdx;
	}
	
	/**
	 * paramName 으로 JCOPrmTbl 를 가져온다.
	 * @param paramName
	 * @return
	 */
	private String getJCOPrmTblwithParamName(String paramName){
		
		String JCOPrmTbl = "";
		
		HashMap hm = (HashMap) queryframe.getSource();
		Collection c = hm.values();
		Iterator it = c.iterator();
		for(int i = 0 ; i < c.size(); i++){
			QueryHouse qhnow = (QueryHouse) it.next();
			String JCOPrmNm = qhnow.getJCOPrmNm();
			if ( JCOPrmNm.equals(paramName) ){
				JCOPrmTbl = qhnow.getJCOPrmTbl();
			}
		}
		
		return JCOPrmTbl;
	}
	
	/**
	 * DataSet Header 정보를 표시한다. 
	 * @param Object table , structure , char 를 받는다.
	 */
	private void setHeaderInfo(Object object){
				    
		if(object instanceof JCO.Field){
			
			JCO.Field field = (JCO.Field) object;
			int iFType = 0x6000;
			int fsize = 0;
	    	iFType = field.getType();
	    	iFType = RequbeUtil.getDsTypeJCO(iFType);
			fsize = ( field.getLength());
			resultset.append(iFType+":"+fsize);
			resultset.append(RQGetDataIf.ROW_SEP);
			
		}else if (object instanceof JCO.Structure){
			
			JCO.Structure structure = (JCO.Structure) object;
			int structurefieldcnt = structure.getFieldCount();
			int iFType = 0x6000;
			int fsize = 0;
			for(int sf =0 ; sf < structurefieldcnt ; sf++){
		    	iFType = structure.getField(sf).getType();
		    	iFType = RequbeUtil.getDsTypeJCO(iFType);
		    	fsize = ( structure.getField(sf) ).getLength();
		    	resultset.append(iFType+":"+fsize);
		    	if( sf != structurefieldcnt-1 ) resultset.append(RQGetDataIf.COL_SEP);
		    }
		    resultset.append(RQGetDataIf.ROW_SEP);
		    
		}else if (object instanceof JCO.Table) {

			JCO.Table table = (JCO.Table) object;
		    int tablefieldcnt = table.getFieldCount();
		    int iFType = 0x6000;
		    int fsize = 0;
		    for(int tf =0 ; tf < tablefieldcnt ; tf++){
		    	iFType = table.getField(tf).getType();
		    	iFType = RequbeUtil.getDsTypeJCO(iFType);
		    	fsize = ( table.getField(tf) ).getLength();
		    	resultset.append(iFType+":"+fsize);
		    	if( tf != tablefieldcnt-1 ) resultset.append(RQGetDataIf.COL_SEP);
		    }
		    resultset.append(RQGetDataIf.ROW_SEP);
		}
	}
	
	/**
	 * 
	 * @param object table , structure , char 를 받는다.
	 * @param icolumn 
	 * @return
	 */
	private String objectToString(Object object, int icolumn){
		StringBuffer lmsbf = new StringBuffer();
		
		if(object instanceof JCO.Table){
			
			JCO.Table table = (JCO.Table) object;
			
			int type = table.getField(icolumn).getType();
			
			switch(type){
			
			//case JCO.TYPE_BYTE:
			case JCO.TYPE_STRING:
				
				byte[] bbuf = table.getByteArray(icolumn);
				int length = (Base64Encoder.encode(bbuf)).toString().length();
				
				lmsbf.append( oRQHBindingSource.getIOffset() );
				int iOffset = oRQHBindingSource.getIOffset() + length;
				oRQHBindingSource.setIOffset(iOffset);
				
				lmsbf.append(",");
				lmsbf.append(length);
				
				oRQHBindingSource.getStfBlob().append(Base64Encoder.encode(bbuf));
				
			break;
			default:
				//lmsbf.append( table.getString(icolumn) );
				lmsbf.append( getResultString( table.getString(icolumn)) );
				//////////////////////////debug info /////////////////////////////////////////
				//log.debug("table value : " + getResultString( table.getString(icolumn)) );
				///////////////////////////////////////////////////////////
			}
			
		}else if(object instanceof JCO.Structure){
			
			JCO.Structure structure = (JCO.Structure) object;
			
			int type = structure.getField(icolumn).getType();
			
			switch(type){
			
			//case JCO.TYPE_BYTE:
			case JCO.TYPE_STRING:
				
				byte[] bbuf = structure.getByteArray(icolumn);
				int length = (Base64Encoder.encode(bbuf)).toString().length();
				
				lmsbf.append( oRQHBindingSource.getIOffset() );
				int iOffset = oRQHBindingSource.getIOffset() + length;
				oRQHBindingSource.setIOffset(iOffset);
				
				lmsbf.append(",");
				lmsbf.append(length);
				
				oRQHBindingSource.getStfBlob().append(Base64Encoder.encode(bbuf));
				
			break;
			default:
				lmsbf.append( getResultString( structure.getString(icolumn) ));
				//////////////////////////debug info /////////////////////////////////////////
				//log.debug("structure value : " + getResultString( structure.getString(icolumn)) );
				///////////////////////////////////////////////////////////
			}
			
		}else if(object instanceof JCO.Field){
			
			JCO.Field field = (JCO.Field) object;
			int type = field.getType();
			
			switch(type){
			
			//case JCO.TYPE_BYTE:
			case JCO.TYPE_STRING:
				
				byte[] bbuf = field.getByteArray();
				int length = (Base64Encoder.encode(bbuf)).toString().length();
				
				lmsbf.append( oRQHBindingSource.getIOffset() );
				int iOffset = oRQHBindingSource.getIOffset() + length;
				oRQHBindingSource.setIOffset(iOffset);
				
				lmsbf.append(",");
				lmsbf.append(length);
				
				oRQHBindingSource.getStfBlob().append(Base64Encoder.encode(bbuf));
				
			break;
			default:
				lmsbf.append( getResultString( field.getString()) );
				//////////////////////////debug info /////////////////////////////////////////
				//log.debug("Field Value : " + getResultString( field.getString()) );
				///////////////////////////////////////////////////////////
			}
		}
		
		return lmsbf.toString();
	}
	
}
