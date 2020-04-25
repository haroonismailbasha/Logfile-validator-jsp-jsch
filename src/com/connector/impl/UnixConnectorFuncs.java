package com.connector.impl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;

import com.connector.interfaces.UnixConnectorIntrfc;
import com.connector.vo.ConnectionStatus;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import com.sun.xml.internal.ws.api.server.Container;

public class UnixConnectorFuncs implements UnixConnectorIntrfc {
	public JSch jsch = new JSch();
	static String passwd;

	public Session getSession(String hostname, String user, int port) throws JSchException {
		Session session = this.jsch.getSession(user, hostname, port);
		UserInfo ui = new MyUserInfo();
		session.setUserInfo(ui);
		session.connect();
		return session;
	}

	public void keepSessionActive(ArrayList<ConnectionStatus> connectionPool)
		{
			ScheduledExecutorService scheduler=Executors.newScheduledThreadPool(1);
			Runnable beeper=new UnixConnectorFuncs().1(this,connectionPool);
			
			ScheduledFuture<?> beeperHandle=scheduler.scheduleAtFixedRate(beeper, 2L, 240L, TimeUnit.SECONDS);
			scheduler.schedule(new UnixConnectorFuncs().2(this,  beeperHandle, 36000000L,TimeUnit.MINUTES);
		}

	public void executeScript(Session session, String scriptText) {
		try {

			String command = scriptText;
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			channel.setInputStream(null);

			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();

			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning", JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			return foo == 0;
		}

		JTextField passwordField = (JTextField) new JPasswordField(20);

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			Object[] ob = { passwordField };
			int result = JOptionPane.showConfirmDialog(null, ob, message, JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				passwd = passwordField.getText();
				return true;
			} else {
				return false;
			}
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
		private JPanel panel;

		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
				boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": " + name, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}

	}

	public void getFile(Session session, Vector cmds) {
		try {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp c = (ChannelSftp) channel;

			java.io.InputStream in = System.in;
			java.io.PrintStream out = System.out;
			String cmd = (String) cmds.elementAt(0);
			if (cmd.equals("get") || cmd.equals("get-resume") || cmd.equals("get-append") || cmd.equals("put")
					|| cmd.equals("put-resume") || cmd.equals("put-append")) {
				if (cmds.size() != 2 && cmds.size() != 3) {
				}
				;
				String p1 = (String) cmds.elementAt(1);
//			    		  String p2=p1;
				String p2 = ".";
				if (cmds.size() == 3)
					p2 = (String) cmds.elementAt(2);
				try {
					SftpProgressMonitor monitor = new MyProgressMonitor();
					if (cmd.startsWith("get")) {
						int mode = ChannelSftp.OVERWRITE;
						if (cmd.equals("get-resume")) {
							mode = ChannelSftp.RESUME;
						} else if (cmd.equals("get-append")) {
							mode = ChannelSftp.APPEND;
						}
						c.get(p1, p2, monitor, mode);
					} else {
						int mode = ChannelSftp.OVERWRITE;
						if (cmd.equals("put-resume")) {
							mode = ChannelSftp.RESUME;
						} else if (cmd.equals("put-append")) {
							mode = ChannelSftp.APPEND;
						}
						c.put(p1, p2, monitor, mode);
					}
				} catch (SftpException e) {
					System.out.println(e.toString());
				}
				return;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static class MyProgressMonitor implements SftpProgressMonitor {
		ProgressMonitor monitor;
		long count = 0;
		long max = 0;

		public void init(int op, String src, String dest, long max) {
			this.max = max;
			monitor = new ProgressMonitor(null, ((op == SftpProgressMonitor.PUT) ? "put" : "get") + ": " + src, "", 0,
					(int) max);
			count = 0;
			percent = -1;
			monitor.setProgress((int) this.count);
			monitor.setMillisToDecideToPopup(1000);
		}

		private long percent = -1;

		public boolean count(long count) {
			this.count += count;

			if (percent >= this.count * 100 / max) {
				return true;
			}
			percent = this.count * 100 / max;

			monitor.setNote("Completed " + this.count + "(" + percent + "%) out of " + max + ".");
			monitor.setProgress((int) this.count);

			return !(monitor.isCanceled());
		}

		public void end() {
			monitor.close();
		}
	}
}
