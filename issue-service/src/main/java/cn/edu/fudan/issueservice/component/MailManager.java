package cn.edu.fudan.issueservice.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

/**
 * @author WZY
 * @version 1.0
 **/
@Component
public class MailManager {

    @Value("${spring.mail.username}")
    private String mailFrom;

    private JavaMailSender mailSender;

    public MailManager(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /*
     * 发送简单文本型邮件
     */
    public void sendTextMail(String ...mailTo){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(mailTo);
        simpleMailMessage.setFrom(mailFrom);
        simpleMailMessage.setSubject("邮件的主题");
        simpleMailMessage.setText("文件的内容");
        mailSender.send(simpleMailMessage);
    }

    public void sendHtmlMail(String ...mailTo){
        try{
            MimeMessage mimeMessage=mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setFrom(mailFrom);
            mimeMessageHelper.setSubject("邮件的主题");

            StringBuilder sb=new StringBuilder();
            sb.append("<html>");
            sb.append("<head>");
            sb.append("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\" type=\"text/css\">");
            sb.append("</head>");
            sb.append("<body><h1>spring 邮件测试</h1><p>hello!this is spring mail test。</p>");
            sb.append("<a class=\"btn btn-outline-primary m-2\" href=\"#\">Primary</a>");
            sb.append("<script src=\"http://code.jquery.com/jquery-3.2.1.js\" integrity=\"sha256-DZAnKJ/6XZ9si04Hgrsxu/8s717jcIzLy3oi35EouyE=\" crossorigin=\"anonymous\"></script>");
            sb.append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\"></script>");
            sb.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\"></script>");
            sb.append("</body>");
            sb.append("</html>");

            mimeMessageHelper.setText(sb.toString(),true);
            mailSender.send(mimeMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
