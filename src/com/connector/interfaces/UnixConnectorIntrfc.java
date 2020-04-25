package com.connector.interfaces;

import java.util.ArrayList;
import java.util.Vector;

import com.connector.vo.ConnectionStatus;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract interface UnixConnectorIntrfc {

	public abstract Session getSession(String paramString1, String paramString2, int paramInt) throws JSchException;

	public abstract void executeScript(Session paramSession, String paramString);

	public abstract void getFile(Session paramSession, Vector paramVector);

	public void keepSessionActive(ArrayList<ConnectionStatus> connectionPool);
}
