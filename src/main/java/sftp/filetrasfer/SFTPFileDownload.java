package sftp.filetrasfer;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SFTPFileDownload {
	
	 private static Logger logger = Logger.getLogger(SFTPFileDownload.class);

		private static final int PORT = 22;


		private final	String SFTPLocation ; //="62.168.216.33";
		private final	String userName ;//= "PIM_Riversand";
		private final	String password;// = "Zm*2_a2*z#m/8*H#";
		private final	String port ;//= "22";
		private final	String src ;//= "/home/PIM_Riversand/SAPImport/int";
		private final   String localDirectoryPath ; //

		private JSch jsch;
		private Session session;
		private Channel channel;
		private ChannelSftp sftp;
		private String[] ignoreFiles;
		
	public SFTPFileDownload() {
		JsonObject SFTPJsonObject = PropertiesUtility.getSFTPConfigJsonObject().getAsJsonObject();
		SFTPLocation = SFTPJsonObject.get("SFTPLocation").getAsString();// "62.168.216.33";
		userName = SFTPJsonObject.get("userName").getAsString();// "PIM_Riversand";
		password = SFTPJsonObject.get("password").getAsString();// "Zm*2_a2*z#m/8*H#";
		port = SFTPJsonObject.get("port").getAsString();// "22";
		//src = "/home/PIM_Riversand/SAPImport/TestWeekendData" ;//SFTPJsonObject.get("src").getAsString();// "/home/PIM_Riversand/SAPImport/int";
		//localDirectoryPath =   "D:\\BSDF\\SAPweekendData";//SFTPJsonObject.get("localDirectoryPath").getAsString();
		src = SFTPJsonObject.get("src").getAsString();
		localDirectoryPath =  SFTPJsonObject.get("localDirectoryPath").getAsString();
		
	}
	
		
		/**
		 * Open SFTP connection
		 * @throws JSchException
		 */
		private   void openConnection() throws JSchException  {
			
			jsch = new JSch();
			try {
				session = jsch.getSession(userName, SFTPLocation, 22);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(password);
				session.connect();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
			  logger.info("Exception in Opening connection  with SFTP");
			  throw new JSchException("Exception in Establsih SFTP connection");
			}
			
			
			if( session.isConnected()) {
				logger.info(" SFTP  Connection sucess fully estatblished");
			}
			
		}
		
		/**
		 * Open SFTP Channel 
		 * @throws JSchException
		 */
		private  void openSFTPChannel() throws JSchException {
		
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;	
		   if(channel.isConnected()) {
			   logger.info("SFTP Channel is successfully Opened");
		   }
		   
		}
		
		/**
		 * downloads the files from the remotefolder to the local folder
		 * 
		 * @param remoteFolder
		 * @param localFolder
		 * @throws SftpException
		 */
		@SuppressWarnings("unchecked")
		private void downloadFiles(String remoteFolder, String localFolder) {
				if( !session.isConnected()) {
					try {
						openConnection();
					} catch (JSchException e) {
						// TODO Auto-generated catch block
						System.out.println("Exception in Opening Connection " + e.getMessage());
						e.printStackTrace();
					}
				}
			
				if(  !channel.isConnected() ) {
					try {
						openSFTPChannel();
					} catch (JSchException e) {
						// TODO Auto-generated catch block
						System.out.println(" Exception in Opening SFTP Channel" + e.getMessage());
						e.printStackTrace();
					}
				}
					
			System.out.println(" Test " + " Remote Folder " + remoteFolder);
			logger.info(" Remote Folder " +remoteFolder );
			System.out.println(" Startime of file download  " +System.nanoTime() );
			logger.error(" Startime of file download  " +System.nanoTime()  );
			long trsanferStartTime = System.nanoTime() ;
			File localFile = new File(localFolder);
			
			try {

				if (localFile.exists()) {
					
					Vector<ChannelSftp.LsEntry> fileList;
					fileList = sftp.ls(remoteFolder);
					validaRequireFolders(fileList);
					File destFile;
				for (ChannelSftp.LsEntry file : fileList) {

					if (isRealFile(file.getFilename()) && !ignoreFile(file.getFilename())) {
						if (file.getAttrs().isDir()) {
							String localPath = localFolder + "\\" + file.getFilename();
							if (!new File(localPath).exists()) {
								new File(localPath).mkdir(); // Creating new files.
							}
							System.out.println("Downloading to " + localPath);
							logger.info("Downloading to " + localPath);
							downloadFiles(remoteFolder + "/" + file.getFilename(), localPath);
						} else {
							File f = new File(file.getFilename());
							destFile = new File(localFolder, file.getFilename());
							System.out.println(" File name " + file.getFilename());
							if (destFile.exists()) {
								logger.info("file already exist " + destFile.getAbsolutePath());
								System.out.println(" file already exist \" + destFile.getAbsolutePath() ");
							}
							String completFile = remoteFolder + "/" + file.getFilename();
							System.out.println(" Remote Folder compplete path .. " + completFile);
							sftp.get(remoteFolder + "/" + file.getFilename(), localFolder);
							logger.info("-- Completed the downloading file --  " + file.getFilename());
							System.out.println("Completed the downloading file  " + file.getFilename());
						}
					}
					
				}
				} else {
					 logger.error("local folder " + localFile.getAbsolutePath() + " does not exist");
					System.out.println("local folder" + localFile.getAbsolutePath() + " does not exist");
				}
			} catch (SftpException e) {
				System.out.println(" Exception in File downloading  " + e.getMessage());
			}finally {
				
				destroy();
			}
			System.out.println(" End time of File downloading " + System.nanoTime());
			logger.info("End time of File downloading "+ System.nanoTime() );
			long trsanferEndTime = System.nanoTime() ;
			long trasferTimeInNano = (trsanferEndTime - trsanferStartTime);
			long trasferTimeInInSec = TimeUnit.NANOSECONDS.toSeconds(trasferTimeInNano);
			logger.info("Time taken to complete Downloadin files from SFTP : trasferTimeInInSec " + trasferTimeInInSec);
			System.out.println(" Time taken to complete Downloadin files from SFTP : trasferTimeInInSec  " + trasferTimeInInSec);
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
		private  void destroy() {
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
		
		public void sftpStartTransfer() throws JSchException {
			logger.info("Applciation  Startime of file Trasfer Operatios  " +System.nanoTime()  );
			System.out.println("Applciation  Startime of file Trasfer Operatios  " +System.nanoTime()  );
			long trsanferStartTime = System.nanoTime() ;
			openConnection();
			openSFTPChannel();
			downloadFiles(src,localDirectoryPath);
			destroy();
			logger.info("Application End time  of file Trasfer Operatios  " +System.nanoTime()  );
			System.out.println("Application End time  of file Trasfer Operatios  " +System.nanoTime() );
			long trsanferEndTime = System.nanoTime() ;
			long trasferTimeInNano = (trsanferEndTime - trsanferStartTime);
			long trasferTimeInInSec = TimeUnit.NANOSECONDS.toSeconds(trasferTimeInNano);
			logger.info("Final Time taken to complete Downloadin files from the SFTP : trasferTimeInInSec" + trasferTimeInInSec);
			System.out.println("Final Time taken to complete Downloadin files the  SFTP : trasferTimeInInSec  " + trasferTimeInInSec);
		}
			
		/**
		 *  Validating all the required folder are availabe or not 
		 *  SAP-APO
		 *  SAP-PLM
		 *  SAP-ERP
		 *  Checks the above folder are thre or not.
		 * @param fileList
		 */

	private void validaRequireFolders(Vector<LsEntry> fileList) {
		// TODO Auto-generated method stub
		

		for (ChannelSftp.LsEntry file : fileList) {

			if (isRealFile(file.getFilename()) && !ignoreFile(file.getFilename())) {
				System.out.println(" File Name " + file.getFilename());
			}
		}

	}
	public void testValidateFiles() throws SftpException {
		Vector<ChannelSftp.LsEntry> fileList = sftp.ls(src);
		validaRequireFolders(fileList);
	}
	public static void main(String[] args) throws SftpException {
		// TODO Auto-generated method stub
		//SFTPFileDownload sftpD = new SFTPFileDownload();
		//sftpD.testValidateFiles();
	}

}
