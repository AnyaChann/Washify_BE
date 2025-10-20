package com.washify.apis.service;

import com.washify.apis.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service xác thực email có thực sự tồn tại hay không
 * 
 * Gồm 3 levels:
 * 1. Format validation - Regex check
 * 2. Domain validation - DNS MX record check
 * 3. Mailbox validation - SMTP check (optional, có thể chậm)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    // RFC 5322 compliant email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Common disposable email domains to block
    private static final List<String> DISPOSABLE_DOMAINS = List.of(
        "tempmail.com", "guerrillamail.com", "10minutemail.com", 
        "mailinator.com", "throwaway.email", "fakeinbox.com",
        "yopmail.com", "maildrop.cc", "temp-mail.org"
    );

    /**
     * Validate email format using regex
     */
    public boolean isValidFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim().toLowerCase()).matches();
    }

    /**
     * Check if email domain is disposable/temporary
     */
    public boolean isDisposableEmail(String email) {
        if (email == null) {
            return false;
        }
        
        String domain = extractDomain(email);
        return DISPOSABLE_DOMAINS.stream()
            .anyMatch(disposable -> domain.equalsIgnoreCase(disposable));
    }

    /**
     * Extract domain from email
     */
    private String extractDomain(String email) {
        if (email == null || !email.contains("@")) {
            return "";
        }
        return email.substring(email.indexOf("@") + 1).toLowerCase();
    }

    /**
     * Level 2: Check if domain has valid MX records (DNS check)
     * This verifies domain can receive emails
     */
    public boolean hasMXRecord(String email) {
        String domain = extractDomain(email);
        if (domain.isEmpty()) {
            return false;
        }

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            
            InitialDirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");
            
            if (attr == null || attr.size() == 0) {
                log.warn("Domain {} has no MX records", domain);
                return false;
            }
            
            log.info("Domain {} has {} MX record(s)", domain, attr.size());
            return true;
            
        } catch (NamingException e) {
            log.error("Failed to lookup MX records for domain {}: {}", domain, e.getMessage());
            return false;
        }
    }

    /**
     * Get MX records for a domain
     */
    public List<String> getMXRecords(String email) {
        List<String> mxRecords = new ArrayList<>();
        String domain = extractDomain(email);
        
        if (domain.isEmpty()) {
            return mxRecords;
        }

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            
            InitialDirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");
            
            if (attr != null) {
                for (int i = 0; i < attr.size(); i++) {
                    String mxRecord = (String) attr.get(i);
                    // Format: "priority mailserver" e.g., "10 gmail-smtp-in.l.google.com."
                    String[] parts = mxRecord.split(" ");
                    if (parts.length >= 2) {
                        mxRecords.add(parts[1].replaceAll("\\.$", "")); // Remove trailing dot
                    }
                }
            }
        } catch (NamingException e) {
            log.error("Failed to get MX records for domain {}: {}", domain, e.getMessage());
        }
        
        return mxRecords;
    }

    /**
     * Level 3: SMTP verification - Check if mailbox actually exists
     * WARNING: This may be slow and can be blocked by some mail servers
     * Use with caution in production
     */
    public boolean verifyMailboxViaSMTP(String email) {
        List<String> mxRecords = getMXRecords(email);
        
        if (mxRecords.isEmpty()) {
            log.warn("No MX records found for email: {}", email);
            return false;
        }

        // Try first MX server
        String mxHost = mxRecords.get(0);
        
        try (Socket socket = new Socket(mxHost, 25);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            
            // Read server greeting
            String response = reader.readLine();
            log.debug("SMTP Response: {}", response);
            
            // HELO command
            sendCommand(writer, "HELO washify.com");
            response = reader.readLine();
            log.debug("HELO Response: {}", response);
            
            // MAIL FROM
            sendCommand(writer, "MAIL FROM:<verify@washify.com>");
            response = reader.readLine();
            log.debug("MAIL FROM Response: {}", response);
            
            // RCPT TO (check if recipient exists)
            sendCommand(writer, "RCPT TO:<" + email + ">");
            response = reader.readLine();
            log.debug("RCPT TO Response: {}", response);
            
            // QUIT
            sendCommand(writer, "QUIT");
            
            // Check if response code is 250 (success)
            return response != null && response.startsWith("250");
            
        } catch (Exception e) {
            log.error("SMTP verification failed for {}: {}", email, e.getMessage());
            return false;
        }
    }

    private void sendCommand(BufferedWriter writer, String command) throws Exception {
        writer.write(command + "\r\n");
        writer.flush();
    }

    /**
     * Comprehensive email validation
     * Level 1: Format check
     * Level 2: Domain MX check
     * Level 3: Disposable email check
     * 
     * @param email Email to validate
     * @param throwException If true, throw exception on invalid email
     * @return true if email is valid
     */
    public boolean validateEmail(String email, boolean throwException) {
        // Level 1: Format validation
        if (!isValidFormat(email)) {
            if (throwException) {
                throw new BadRequestException("Email không hợp lệ: Format sai");
            }
            return false;
        }

        // Level 2: Disposable email check
        if (isDisposableEmail(email)) {
            if (throwException) {
                throw new BadRequestException("Email không hợp lệ: Không chấp nhận email tạm thời");
            }
            return false;
        }

        // Level 3: MX record check
        if (!hasMXRecord(email)) {
            if (throwException) {
                throw new BadRequestException("Email không hợp lệ: Domain không thể nhận email");
            }
            return false;
        }

        log.info("Email {} passed all validation checks", email);
        return true;
    }

    /**
     * Quick validation without throwing exception
     */
    public boolean isValidEmail(String email) {
        return validateEmail(email, false);
    }

    /**
     * Strict validation with exception
     */
    public void validateEmailStrict(String email) {
        validateEmail(email, true);
    }

    /**
     * Deep validation including SMTP check (use sparingly)
     */
    public boolean validateEmailDeep(String email, boolean throwException) {
        // First run basic validation
        if (!validateEmail(email, throwException)) {
            return false;
        }

        // Then run SMTP check
        if (!verifyMailboxViaSMTP(email)) {
            if (throwException) {
                throw new BadRequestException("Email không tồn tại: Mailbox không hợp lệ");
            }
            return false;
        }

        log.info("Email {} passed deep validation including SMTP check", email);
        return true;
    }
}
