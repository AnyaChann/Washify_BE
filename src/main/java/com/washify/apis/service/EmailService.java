package com.washify.apis.service;

import com.washify.apis.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.name}")
    private String appName;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Gửi email reset password
     * @param toEmail Email người nhận
     * @param resetToken Token để reset password
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            String subject = "Đặt lại mật khẩu - " + appName;
            String content = buildPasswordResetEmailContent(resetLink);

            sendHtmlEmail(toEmail, subject, content);
            
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new EmailSendException("Không thể gửi email. Vui lòng thử lại sau.", e);
        }
    }
    
    /**
     * Gửi email xác nhận đổi password
     * @param toEmail Email người nhận
     * @param changeToken Token để confirm change password
     */
    public void sendPasswordChangeConfirmationEmail(String toEmail, String changeToken) {
        try {
            String confirmLink = frontendUrl + "/confirm-password-change?token=" + changeToken;
            String subject = "Xác nhận đổi mật khẩu - " + appName;
            String content = buildPasswordChangeConfirmationEmailContent(confirmLink);

            sendHtmlEmail(toEmail, subject, content);
            
            log.info("Password change confirmation email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password change confirmation email to: {}", toEmail, e);
            throw new EmailSendException("Không thể gửi email. Vui lòng thử lại sau.", e);
        }
    }
    
    /**
     * Gửi email xác nhận bật/tắt 2FA
     * @param toEmail Email người nhận
     * @param token Token để confirm toggle 2FA
     * @param enable true = bật, false = tắt
     */
    public void send2FAToggleConfirmationEmail(String toEmail, String token, boolean enable) {
        try {
            String confirmLink = frontendUrl + "/confirm-2fa-toggle?token=" + token;
            String subject = (enable ? "Xác nhận BẬT" : "Xác nhận TẮT") + " bảo mật 2 lớp - " + appName;
            String content = build2FAToggleConfirmationEmailContent(confirmLink, enable);

            sendHtmlEmail(toEmail, subject, content);
            
            log.info("2FA toggle ({}) confirmation email sent successfully to: {}", 
                enable ? "enable" : "disable", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send 2FA toggle confirmation email to: {}", toEmail, e);
            throw new EmailSendException("Không thể gửi email. Vui lòng thử lại sau.", e);
        }
    }


    /**
     * Gửi email HTML
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Template HTML cho email reset password
     */
    private String buildPasswordResetEmailContent(String resetLink) {
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Đặt lại mật khẩu</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .container {
                        background-color: #f9f9f9;
                        border-radius: 10px;
                        padding: 30px;
                        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        color: #4CAF50;
                        margin-bottom: 30px;
                    }
                    .content {
                        background-color: white;
                        padding: 25px;
                        border-radius: 8px;
                        margin-bottom: 20px;
                    }
                    .button {
                        display: inline-block;
                        background-color: #4CAF50;
                        color: white !important;
                        padding: 14px 28px;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                        margin: 20px 0;
                        text-align: center;
                    }
                    .button:hover {
                        background-color: #45a049;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 12px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .footer {
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #ddd;
                    }
                    .link-text {
                        color: #666;
                        font-size: 12px;
                        word-break: break-all;
                        background-color: #f5f5f5;
                        padding: 10px;
                        border-radius: 4px;
                        margin-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🧺 %s</h1>
                    </div>
                    
                    <div class="content">
                        <h2>Yêu cầu đặt lại mật khẩu</h2>
                        
                        <p>Xin chào,</p>
                        
                        <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn tại <strong>%s</strong>.</p>
                        
                        <p>Để đặt lại mật khẩu, vui lòng click vào nút bên dưới:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">
                                🔐 Đặt lại mật khẩu
                            </a>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý quan trọng:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Link này sẽ <strong>hết hạn sau 30 phút</strong></li>
                                <li>Link chỉ có thể sử dụng <strong>một lần duy nhất</strong></li>
                                <li>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này</li>
                            </ul>
                        </div>
                        
                        <p>Nếu nút phía trên không hoạt động, bạn có thể copy và dán link sau vào trình duyệt:</p>
                        
                        <div class="link-text">
                            %s
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Email này được gửi tự động từ hệ thống <strong>%s</strong></p>
                        <p>Vui lòng không trả lời email này.</p>
                        <p style="margin-top: 15px;">
                            © 2024 %s. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, appName, resetLink, resetLink, appName, appName);
    }
    
    /**
     * Template HTML cho email xác nhận đổi password
     */
    private String buildPasswordChangeConfirmationEmailContent(String confirmLink) {
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Xác nhận đổi mật khẩu</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .container {
                        background-color: #f9f9f9;
                        border-radius: 10px;
                        padding: 30px;
                        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        color: #FF9800;
                        margin-bottom: 30px;
                    }
                    .content {
                        background-color: white;
                        padding: 25px;
                        border-radius: 8px;
                        margin-bottom: 20px;
                    }
                    .button {
                        display: inline-block;
                        background-color: #FF9800;
                        color: white !important;
                        padding: 14px 28px;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                        margin: 20px 0;
                        text-align: center;
                    }
                    .button:hover {
                        background-color: #F57C00;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 12px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .footer {
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #ddd;
                    }
                    .link-text {
                        color: #666;
                        font-size: 12px;
                        word-break: break-all;
                        background-color: #f5f5f5;
                        padding: 10px;
                        border-radius: 4px;
                        margin-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🧺 %s</h1>
                    </div>
                    
                    <div class="content">
                        <h2>Xác nhận đổi mật khẩu</h2>
                        
                        <p>Xin chào,</p>
                        
                        <p>Bạn vừa yêu cầu <strong>đổi mật khẩu</strong> cho tài khoản của mình tại <strong>%s</strong>.</p>
                        
                        <p>Để hoàn tất việc đổi mật khẩu, vui lòng click vào nút bên dưới:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">
                                ✅ Xác nhận đổi mật khẩu
                            </a>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý quan trọng:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Link này sẽ <strong>hết hạn sau 30 phút</strong></li>
                                <li>Link chỉ có thể sử dụng <strong>một lần duy nhất</strong></li>
                                <li>Sau khi xác nhận, mật khẩu mới sẽ được áp dụng ngay lập tức</li>
                                <li><strong>Nếu bạn không yêu cầu đổi mật khẩu, vui lòng BỎ QUA email này và đổi mật khẩu ngay để bảo mật tài khoản!</strong></li>
                            </ul>
                        </div>
                        
                        <p>Nếu nút phía trên không hoạt động, bạn có thể copy và dán link sau vào trình duyệt:</p>
                        
                        <div class="link-text">
                            %s
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Email này được gửi tự động từ hệ thống <strong>%s</strong></p>
                        <p>Vui lòng không trả lời email này.</p>
                        <p style="margin-top: 15px;">
                            © 2025 %s. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(appName, appName, confirmLink, confirmLink, appName, appName);
    }
    
    /**
     * Template HTML cho email xác nhận bật/tắt 2FA
     */
    private String build2FAToggleConfirmationEmailContent(String confirmLink, boolean enable) {
        String action = enable ? "BẬT" : "TẮT";
        String actionLower = enable ? "bật" : "tắt";
        String emoji = enable ? "🔒" : "⚡";
        String color = enable ? "#4CAF50" : "#FF9800";
        
        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Xác nhận %s bảo mật 2 lớp</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .container {
                        background-color: #f9f9f9;
                        border-radius: 10px;
                        padding: 30px;
                        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        color: %s;
                        margin-bottom: 30px;
                    }
                    .content {
                        background-color: white;
                        padding: 25px;
                        border-radius: 8px;
                        margin-bottom: 20px;
                    }
                    .button {
                        display: inline-block;
                        background-color: %s;
                        color: white !important;
                        padding: 14px 28px;
                        text-decoration: none;
                        border-radius: 5px;
                        font-weight: bold;
                        margin: 20px 0;
                        text-align: center;
                    }
                    .button:hover {
                        opacity: 0.9;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 12px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .info {
                        background-color: #e3f2fd;
                        border-left: 4px solid #2196F3;
                        padding: 12px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .footer {
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #ddd;
                    }
                    .link-text {
                        color: #666;
                        font-size: 12px;
                        word-break: break-all;
                        background-color: #f5f5f5;
                        padding: 10px;
                        border-radius: 4px;
                        margin-top: 10px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🧺 %s</h1>
                    </div>
                    
                    <div class="content">
                        <h2>%s Xác nhận %s bảo mật 2 lớp</h2>
                        
                        <p>Xin chào,</p>
                        
                        <p>Bạn vừa yêu cầu <strong>%s bảo mật 2 lớp</strong> cho tính năng đổi mật khẩu tại <strong>%s</strong>.</p>
                        
                        %s
                        
                        <p>Để hoàn tất việc %s, vui lòng click vào nút bên dưới:</p>
                        
                        <div style="text-align: center;">
                            <a href="%s" class="button">
                                ✅ Xác nhận %s
                            </a>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý quan trọng:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Link này sẽ <strong>hết hạn sau 30 phút</strong></li>
                                <li>Link chỉ có thể sử dụng <strong>một lần duy nhất</strong></li>
                                <li><strong>Nếu bạn không yêu cầu thay đổi này, vui lòng BỎ QUA email này!</strong></li>
                            </ul>
                        </div>
                        
                        <p>Nếu nút phía trên không hoạt động, bạn có thể copy và dán link sau vào trình duyệt:</p>
                        
                        <div class="link-text">
                            %s
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Email này được gửi tự động từ hệ thống <strong>%s</strong></p>
                        <p>Vui lòng không trả lời email này.</p>
                        <p style="margin-top: 15px;">
                            © 2025 %s. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                action,  // title
                color, color,  // header color, button color
                appName,  // app name in header
                emoji, action,  // emoji + action in title
                actionLower, appName,  // action text + app name
                enable  // info box
                    ? """
                        <div class="info">
                            <strong>📋 Bảo mật 2 lớp là gì?</strong>
                            <p style="margin: 10px 0;">
                                Khi bật bảo mật 2 lớp, mỗi lần bạn đổi mật khẩu sẽ cần xác nhận qua email. 
                                Điều này giúp bảo vệ tài khoản của bạn tốt hơn, ngay cả khi mật khẩu hiện tại bị lộ.
                            </p>
                        </div>
                        """
                    : """
                        <div class="warning" style="background-color: #fff3cd;">
                            <strong>⚠️ Lưu ý:</strong>
                            <p style="margin: 10px 0;">
                                Sau khi tắt bảo mật 2 lớp, bạn có thể đổi mật khẩu ngay lập tức mà không cần xác nhận email. 
                                Tính năng này tiện lợi nhưng kém bảo mật hơn.
                            </p>
                        </div>
                        """,
                actionLower,  // action text
                confirmLink,  // confirm link
                action,  // button text
                confirmLink,  // link text
                appName, appName  // footer
            );
    }

    /**
     * Gửi email thông báo đơn giản
     * @param toEmail Email người nhận
     * @param subject Tiêu đề
     * @param content Nội dung
     */
    public void sendSimpleEmail(String toEmail, String subject, String content) {
        try {
            sendHtmlEmail(toEmail, subject, content);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", toEmail, e);
            throw new EmailSendException("Không thể gửi email. Vui lòng thử lại sau.", e);
        }
    }
}
