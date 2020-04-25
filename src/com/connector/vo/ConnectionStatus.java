package com.connector.vo;

import com.jcraft.jsch.Session;

public class ConnectionStatus {
	public Session session;
	public int Connection;
	public boolean connectionState;
	public boolean connectionBusy;
	public String connectionName;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public int getConnection() {
		return Connection;
	}

	public void setConnection(int connection) {
		Connection = connection;
	}

	public boolean isConnectionState() {
		return connectionState;
	}

	public void setConnectionState(boolean connectionState) {
		this.connectionState = connectionState;
	}

	public boolean isConnectionBusy() {
		return connectionBusy;
	}

	public void setConnectionBusy(boolean connectionBusy) {
		this.connectionBusy = connectionBusy;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
}
