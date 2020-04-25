package com.connector.main;

import java.io.File;

import com.connector.impl.UnixConnectorFuncs;
import com.connector.interfaces.UnixConnectorIntrfc;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class UnixConnectorMain {
	public static Session Sessionmain;
	public static boolean connectionFlag=Boolean.valueOf(false);
	public static void main(String[] args) throws JSchException {
		UnixConnectorIntrfc unixConnectorIntrfc=new UnixConnectorFuncs();
		System.out.println(System.getProperty("user.dir"));
		String homedir=System.getProperty("user.home");
		File file=new File(System.getProperty("user.home"));
		
		Sessionmain=unixConnectorIntrfc.getSession("209.182.219.185", "root", 22);
		System.out.println("Connection is "+Sessionmain.isConnected());
		System.getProperty("user.dir");
		connectionFlag=Boolean.valueOf(Sessionmain.isConnected());
		System.out.println("Connection is "+Sessionmain.isConnected());
	}

}
