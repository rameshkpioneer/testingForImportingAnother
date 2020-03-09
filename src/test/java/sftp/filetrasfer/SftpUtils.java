package sftp.filetrasfer;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * helper class which uses the jsch library
 * 
 * - open a sftp connection - upload multiple files from a local source to a
 * remote destination - download multiple files from a remote source to a local
 * destination
 *
 */
public class SftpUtils {
	// private static Logger logger = Logger.getLogger(BsdfTrasfromToRsJson.class);
	 private static Logger logger = Logger.getLogger(SftpUtils.class);

	private static final int PORT = 22;

	private final String user;
	private final String host;
	private final String privateKey;
	private final byte[] keyPassword;

	String SFTPLocation = "62.168.216.33";
	String userName = "PIM_Riversand";
	String password = "Zm*2_a2*z#m/8*H#";
	String port = "22";
	String src = "/home/PIM_Riversand/SAPImport/int";

	private JSch jsch;
	private Session session;
	private ChannelSftp sftp;

	private String[] ignoreFiles;

	/**
	 * opens a sftp connection with the given privatekey
	 * 
	 * @param user
	 * @param host
	 * @param privateKey
	 */
	public SftpUtils(String user, String host, String privateKey) {
		this(user, host, privateKey, null);
	}

	/**
	 * opens a sftp connection with the given private key and the key password
	 * 
	 * @param user
	 * @param host
	 * @param privateKey
	 * @param keyPassword
	 */
	public SftpUtils(String user, String host, String privateKey, byte[] keyPassword) {
		this.user = user;
		this.host = host;
		this.privateKey = privateKey;
		this.keyPassword = keyPassword;

		/*
		 * try { init(); } catch (JSchException e) { throw new
		 * RuntimeException("Could not connect to host [" + host + "] using KeyFile [" +
		 * privateKey + "] for User [" + user + "]", e); }
		 */
	}

	public String[] getIgnoreFiles() {
		return ignoreFiles;
	}

	/**
	 * ignore the files which starts with the given strings
	 *
	 * @param ignoreFiles
	 */
	public void setIgnoreFiles(String[] ignoreFiles) {
		this.ignoreFiles = ignoreFiles;
	}

	public ChannelSftp getSftp() {
		return sftp;
	}

	private void init() throws JSchException {
		jsch = new JSch();
		session = jsch.getSession(userName, SFTPLocation, 22);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();
		session.isConnected();
		System.out.println(" session  is connected  " + session.isConnected());
		Channel channel = session.openChannel("sftp");
		channel.connect();
		System.out.println(" Channel is   connected  " + channel.isConnected());
		sftp = (ChannelSftp) channel;
	}

	/**
	 * downloads the files from the remotefolder to the local folder
	 * 
	 * @param remoteFolder
	 * @param localFolder
	 * @throws SftpException
	 */
	@SuppressWarnings("unchecked")
	public void downloadFiles(String remoteFolder, String localFolder) {
		System.out.println(" Test " + " Remote Folder " + remoteFolder);
		System.out.println(" Startime of file download  " +System.nanoTime() );
		logger.error(" Startime of file download  " +System.nanoTime()  );
		long trsanferStartTime = System.nanoTime() ;
		File localFile = new File(localFolder);
		try {

			if (localFile.exists()) {

				System.out.println("Going to Local Folder ...");
				Vector<ChannelSftp.LsEntry> fileList;

				fileList = sftp.ls(remoteFolder);

				System.out.println(" File list size " + fileList.size());
				File destFile;
				for (ChannelSftp.LsEntry file : fileList) {

					if (isRealFile(file.getFilename()) && !ignoreFile(file.getFilename())) {

						if (file.getAttrs().isDir()) {
							String localPath = localFolder + "\\" + file.getFilename();
							if(! new File(localPath).exists()) {
								new File(localPath).mkdir(); //Creating new files.
							}
							System.out.println(" local path " + localPath);
							downloadFiles(remoteFolder + "/" + file.getFilename(), localPath);
						} else {
							File f = new File(file.getFilename());
							destFile = new File(localFolder, file.getFilename());
							System.out.println(" File name " + file.getFilename());
							if (destFile.exists()) {
								// logger.info("file already exist " + destFile.getAbsolutePath());
								System.out.println(" file already exist \" + destFile.getAbsolutePath() ");
							}
							String completFile = remoteFolder + "/" + file.getFilename();
							System.out.println(" Remote Folder compplete path .. " + completFile);
							sftp.get(remoteFolder + "/" + file.getFilename(), localFolder);
							// logger.debug("download " + file.getFilename());
							System.out.println("download " + file.getFilename());
						}
					}
				}
			} else {
				// logger.error("local folder \"" + localFile.getAbsolutePath() + "\" does not
				// exist");
				System.out.println("local folder \"" + localFile.getAbsolutePath() + "\" does not exist");
			}
		} catch (SftpException e) {
			System.out.println(" Exception in File downloading  " + e.getMessage());
		}
		System.out.println(" End time of File downloading " + System.nanoTime());
		logger.info("End time of File downloading "+ System.nanoTime() );
		long trsanferEndTime = System.nanoTime() ;
		long trasferTimeInNano = (trsanferEndTime - trsanferStartTime);
		long trasferTimeInInSec = TimeUnit.NANOSECONDS.toSeconds(trasferTimeInNano);
		logger.info(" trasferTimeInNano " + trasferTimeInInSec);
		System.out.println(" End time of File downloading " + trasferTimeInInSec);
	}

	private boolean ignoreFile(String fileName) {
		if (ignoreFiles != null) {
			for (String ignore : ignoreFiles) {
				if (fileName.startsWith(ignore)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * close and destroy the sftp connection
	 */
	public void destroy() {
		sftp.exit();
		sftp.disconnect();
		session.disconnect();
	}

	/**
	 * check if the filename is not ".." and "." (for unix)
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isRealFile(String filename) {
		return (!filename.equals("..") && !filename.equals("."));
	}

	public static void main(String args[]) {

		String SFTPLocation = "62.168.216.33";
		String userName = "PIM_Riversand";
		String password = "Zm*2_a2*z#m/8*H#";
		String port = "22";
		String src = "/home/PIM_Riversand/SAPImport/int";

		SftpUtils sftp = new SftpUtils(userName, SFTPLocation, password);
		try {
			sftp.init();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			System.out.println(" Exception in main method " + e.getMessage());
		}
	 
		sftp.destroy();
	}
}