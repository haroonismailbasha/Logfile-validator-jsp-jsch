package com.connector.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import com.connector.impl.UnixConnectorFuncs;
import com.connector.interfaces.UnixConnectorIntrfc;
import com.connector.vo.ConnectionStatus;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class UnixConnectorWeb {

	
	public static Session Sessionmain;
	public static boolean connectionFlag=Boolean.valueOf(false);
	public static Properties logFileProp=new Properties();
	public static Session getConnectorForJsp() throws JSchException {
		UnixConnectorIntrfc unixConnectorIntrfc=new UnixConnectorFuncs();
		System.out.println(System.getProperty("user.dir"));
		String homedir=System.getProperty("user.home");
		File file=new File(System.getProperty("user.home"));
		
		Sessionmain=unixConnectorIntrfc.getSession("serverIp", "username", 22);
		System.out.println("Connection is "+Sessionmain.isConnected());
		System.getProperty("user.dir");
		connectionFlag=Boolean.valueOf(Sessionmain.isConnected());
		System.out.println("Connection is "+Sessionmain.isConnected());
		return Sessionmain;
	}
	public static void keepActiveConnectionForJsp(ArrayList<ConnectionStatus> connectionPool) {
		UnixConnectorIntrfc unixConnectorIntrfc=new UnixConnectorFuncs();
		unixConnectorIntrfc.keepSessionActive(connectionPool);
	}
	
	public static String executeCommandGetFile(Session session,String scriptToExecute,String fileName) throws InterruptedException, IOException {
		String textInFile="";
		UnixConnectorIntrfc unixConnectorIntrfc=new UnixConnectorFuncs();
		System.out.println(System.getProperty("user.dir"));
		Vector vector=new Vector();
		String homedir=System.getProperty("user.home");
		File file=new File(System.getProperty("user.home"));
		System.getProperty("user.dir",file.getAbsolutePath());
		
		unixConnectorIntrfc.executeScript(session, scriptToExecute);
		Thread.sleep(7000);
		vector.add("get");
		vector.add("t.txt");
		unixConnectorIntrfc.getFile(session, vector);
		BufferedReader br=new BufferedReader(new FileReader(new File(System.getProperty("user.dir")+"\\"+"t.txt")));
		String line=null;
		while((line=br.readLine())!=null) {
			textInFile=textInFile+"\n"+line;
		}
		br.close();
		return textInFile;
	}
	
	public static boolean isValidSearchData(String requestId) throws FileNotFoundException, IOException {
		boolean isValidScenario=false;
		if(Pattern.compile("requestId").matcher("").matches()) {
			isValidScenario=true;
		}
		ClassLoader classLoader=new UnixConnectorWeb().getClass().getClassLoader();
		logFileProp.load(new FileReader(new File(classLoader.getResource("LogfileUtil.properties").getPath())));
		System.out.println("Test key is"+logFileProp.getProperty("Testkey"));
		return isValidScenario;
	}
}
