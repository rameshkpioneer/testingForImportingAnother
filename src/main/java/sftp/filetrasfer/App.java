package sftp.filetrasfer;

import java.io.IOException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;



/**
 * Hello world!
 *
 */
public class App 
{
	
	static {
		try {
			PropertiesUtility propUtil = new PropertiesUtility(); 
			propUtil.loadPropertiesJSON("D:\\BSDF\\properties.json"); //Local
			//propUtil.loadPropertiesJSON("./properties.json"); //Server 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws JSchException {
		//MailSender.sendMail("SFTP File Download Status","SFTP File transfer has been started");
		SFTPFileDownload sftpd = new SFTPFileDownload();
		sftpd.sftpStartTransfer();
		try {
			sftpd.testValidateFiles();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//MailSender.sendMail("SFTP File Download Status","Successfully transferred all the files from SFTP Location to Server Path");
		
	}
}
