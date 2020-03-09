package sftp.filetrasfer;

 

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.sun.mail.util.MailConnectException;

import sftp.filetrasfer.PropertiesUtility;

 


public class MailSender {
    private static Logger logger = Logger.getLogger(MailSender.class);

	public static void sendMail(String subject, String messageBody) {

		Properties properties = new Properties();
		final String myAccountEmail;
		final String password;
		
		String host = PropertiesUtility.getMailProperties().get("smtpHost").getAsString() == null ? null
				: PropertiesUtility.getMailProperties().get("smtpHost").getAsString();
		String port = PropertiesUtility.getMailProperties().get("smtpPort").getAsString() == null ? null
				: PropertiesUtility.getMailProperties().get("smtpPort").getAsString();
		String sendToString = PropertiesUtility.getMailProperties().get("sendTo").getAsString() == null ? null
				: PropertiesUtility.getMailProperties().get("sendTo").getAsString();
		String ccToString = PropertiesUtility.getMailProperties().get("ccTo").getAsString() == null ? null
				: PropertiesUtility.getMailProperties().get("ccTo").getAsString();
		String bccToString = PropertiesUtility.getMailProperties().get("bccTo").getAsString() == null ? null
				: PropertiesUtility.getMailProperties().get("bccTo").getAsString();
		
		if (sendToString == null || sendToString.equals("")) {
			sendToString = "";
			logger.error("Recipient details should not be Empty");
			return;
		}
		if (ccToString == null) {
			ccToString = "";
		}
		if (bccToString == null) {
			bccToString = "";
		}

		myAccountEmail = PropertiesUtility.getMailProperties().get("smtpAcount").getAsString() == null ? null
				: PropertiesUtility.getMailProperties().get("smtpAcount").getAsString();
		if (myAccountEmail.equals(null) || myAccountEmail.equals("")) {
			logger.error("Sender Id  should not be Empty");
			return;
		}
		password = PropertiesUtility.getMailProperties().get("smtpPassword").getAsString() == null ? null
				: PropertiesUtility.getMailProperties().get("smtpPassword").getAsString();
		if (password.equals(null) || password.equals("")) {
			logger.error("Password should not be Empty");
			return;
		}

		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(myAccountEmail, password);

			}
		});

		Message message = prepareMessage(session, myAccountEmail, sendToString, ccToString, bccToString, subject,
				messageBody);
		 try {
				Transport.send(message);
				logger.info("Message sent successfully to " +sendToString+" " +ccToString + " "+bccToString);
			} catch (MailConnectException me) {
				// TODO Auto-generated catch block
				  logger.error("Mail Connection  Exception  --  Mail sending Failed "+me.getMessage());
			
			}catch(MessagingException em) {
				  logger.error("Messaging Exception Mail sending Failed "+em.getMessage());
			}
	  		 
	       

	}

 

private static Message prepareMessage(Session session,String myAccountEmail,String recepientIds,String ccToIds,String bccIds, String Subject,String messageBody) {
    try {
    	
        Message message=new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccountEmail));
        message.setSubject(Subject);
        message.setText(messageBody);
        if(!recepientIds.equals(null)&&!recepientIds.equals(""))
        {
            String[] recepient=recepientIds.split(",");
            InternetAddress[] toAddress = new InternetAddress[recepient.length];
             
             // To get the array of toaddresses
             for( int i = 0; i < recepient.length; i++ ) {
                 toAddress[i] = new InternetAddress(recepient[i]);
             }
            
             for( int i = 0; i < toAddress.length; i++) {
                 message.addRecipient(Message.RecipientType.TO, toAddress[i]);
             }
        }
        
        if(!ccToIds.equals(null)&&!ccToIds.equals(""))
        {
         String[] ccTo=ccToIds.split(",");
         InternetAddress[] ccAddress = new InternetAddress[ccTo.length];
         
         // To get the array of ccaddresses
         for( int i = 0; i < ccTo.length; i++ ) {
             ccAddress[i] = new InternetAddress(ccTo[i]);
         }
         
         // Set cc: header field of the header.
         for( int i = 0; i < ccAddress.length; i++) {
             message.addRecipient(Message.RecipientType.CC, ccAddress[i]);
         }
        }
        if(!bccIds.equals(null)&&!bccIds.equals(""))
        {
         String[] bcc=bccIds.split(",");
         InternetAddress[] bccAddress = new InternetAddress[bcc.length];
         
         // To get the array of bccaddresses
         for( int i = 0; i < bcc.length; i++ ) {
             bccAddress[i] = new InternetAddress(bcc[i]);
         }
         
         // Set bcc: header field of the header.
         for( int i = 0; i < bccAddress.length; i++) {
             message.addRecipient(Message.RecipientType.BCC, bccAddress[i]);
         }
        }
         
        return message;
                
    }
    catch (Exception e) {
        // TODO: handle exception
        System.out.print(e.getMessage());
        logger.error(e.getMessage());
    }
    return null;
}
                                    

 

}