package com.unievent.service;

import com.unievent.entity.Club;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PdfService pdfService;

    /**
     * Send a beautifully designed club approval email with PDF attachment.
     * Runs asynchronously so it doesn't block the approval API response.
     */
    @Async
    public void sendClubApprovalEmail(Club club) {
        try {
            byte[] pdfBytes = pdfService.generateClubApprovalPdf(club);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(club.getPresidentEmail());
            helper.setSubject("🎉 Congratulations! Your Club \"" + club.getName() + "\" Has Been Approved!");
            helper.setText(buildApprovalEmailHtml(club), true); // true = isHtml

            // Attach PDF
            helper.addAttachment(
                    club.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_Approval.pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf"
            );

            mailSender.send(message);
            System.out.println("✅ Club approval email sent to: " + club.getPresidentEmail());

        } catch (Exception e) {
            System.err.println("❌ Failed to send club approval email to " + club.getPresidentEmail() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build a premium, modern HTML email template for club approval.
     */
    private String buildApprovalEmailHtml(Club club) {
        String frequency = club.getEventFrequency() != null ? club.getEventFrequency() : "N/A";

        // Build activity chips
        StringBuilder activityHtml = new StringBuilder();
        if (club.getPlannedActivities() != null && !club.getPlannedActivities().isEmpty()) {
            for (String act : club.getPlannedActivities().split(",")) {
                activityHtml.append("<span style=\"display:inline-block;background:#e8eaf6;color:#21417f;padding:5px 14px;border-radius:20px;font-size:12px;font-weight:600;margin:3px 4px;\">")
                        .append(act.trim()).append("</span>");
            }
        } else {
            activityHtml.append("<span style=\"color:#999;\">Not specified</span>");
        }

        // Build membership chips
        StringBuilder membershipHtml = new StringBuilder();
        if (club.getMembershipType() != null && !club.getMembershipType().isEmpty()) {
            for (String type : club.getMembershipType().split(",")) {
                membershipHtml.append("<span style=\"display:inline-block;background:#e8f5e9;color:#006e25;padding:5px 14px;border-radius:20px;font-size:12px;font-weight:600;margin:3px 4px;\">")
                        .append(type.trim()).append("</span>");
            }
        } else {
            membershipHtml.append("<span style=\"color:#999;\">Not specified</span>");
        }

        return "<!DOCTYPE html>"
                + "<html><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"></head>"
                + "<body style=\"margin:0;padding:0;background:#f0f2f5;font-family:'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;\">"

                // Wrapper
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f0f2f5;\">"
                + "<tr><td align=\"center\" style=\"padding:40px 20px;\">"
                + "<table role=\"presentation\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);\">"

                // Header
                + "<tr><td style=\"background:linear-gradient(135deg,#21417f 0%,#3b5998 50%,#5474b8 100%);padding:48px 40px;text-align:center;\">"
                + "<div style=\"width:72px;height:72px;background:rgba(255,255,255,0.2);border-radius:50%;margin:0 auto 20px;line-height:72px;font-size:36px;\">🎉</div>"
                + "<h1 style=\"color:#ffffff;font-size:26px;font-weight:800;margin:0 0 8px;letter-spacing:-0.5px;\">Your Club Has Been Approved!</h1>"
                + "<p style=\"color:rgba(255,255,255,0.85);font-size:15px;margin:0;\">Welcome to the UniEvent community</p>"
                + "<div style=\"display:inline-block;background:#006e25;color:#fff;padding:8px 24px;border-radius:24px;font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:1.5px;margin-top:20px;\">✓ OFFICIALLY APPROVED</div>"
                + "</td></tr>"

                // Body
                + "<tr><td style=\"padding:40px;\">"

                // Greeting
                + "<p style=\"font-size:16px;color:#191c1d;line-height:1.6;margin:0 0 24px;\">Dear Club Administrator,</p>"
                + "<p style=\"font-size:15px;color:#444650;line-height:1.7;margin:0 0 32px;\">We're thrilled to inform you that your club <strong style=\"color:#21417f;\">"
                + escapeHtml(club.getName())
                + "</strong> has been officially approved and registered on the UniEvent platform! You can now start managing your club, organizing events, and building your community.</p>"

                // Club Card
                + "<div style=\"background:linear-gradient(135deg,#f8f9ff 0%,#eef2ff 100%);border:2px solid #d9e2ff;border-radius:16px;padding:28px;margin:0 0 32px;\">"
                + "<h2 style=\"font-size:20px;color:#21417f;margin:0 0 20px;text-align:center;font-weight:800;\">" + escapeHtml(club.getName()) + "</h2>"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
                + buildDetailRow("Category", escapeHtml(club.getCategory() != null ? club.getCategory() : "N/A"))
                + buildDetailRow("Club Email", escapeHtml(club.getEmail() != null ? club.getEmail() : "N/A"))
                + buildDetailRow("President Email", escapeHtml(club.getPresidentEmail() != null ? club.getPresidentEmail() : "N/A"))
                + buildDetailRow("Event Frequency", escapeHtml(frequency))
                + "</table>"
                + "</div>"

                // Planned Activities
                + "<div style=\"margin:0 0 24px;\">"
                + "<p style=\"font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:1.5px;color:#21417f;margin:0 0 12px;\">Planned Activities</p>"
                + "<div>" + activityHtml.toString() + "</div>"
                + "</div>"

                // Membership Types
                + "<div style=\"margin:0 0 32px;\">"
                + "<p style=\"font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:1.5px;color:#006e25;margin:0 0 12px;\">Membership Types</p>"
                + "<div>" + membershipHtml.toString() + "</div>"
                + "</div>"

                // PDF Note
                + "<div style=\"background:#fffbeb;border-left:4px solid #f59e0b;padding:16px 20px;border-radius:0 8px 8px 0;margin:0 0 32px;\">"
                + "<p style=\"font-size:13px;color:#92400e;margin:0;\"><strong>📎 PDF Attached:</strong> A detailed Club Registration Certificate is attached to this email. Please save it for your records.</p>"
                + "</div>"

                // CTA Button
                + "<div style=\"text-align:center;margin:32px 0;\">"
                + "<a href=\"http://localhost:8080/clubadmin-myclub.html\" style=\"display:inline-block;background:linear-gradient(135deg,#21417f,#3b5998);color:#ffffff;text-decoration:none;padding:14px 40px;border-radius:12px;font-size:15px;font-weight:700;letter-spacing:0.5px;box-shadow:0 4px 16px rgba(33,65,127,0.3);\">Go to Your Club Dashboard →</a>"
                + "</div>"

                + "</td></tr>"

                // Footer
                + "<tr><td style=\"background:#f8f9fa;padding:32px 40px;text-align:center;border-top:1px solid #e8e8e8;\">"
                + "<p style=\"font-size:15px;font-weight:800;color:#21417f;margin:0 0 4px;letter-spacing:-0.5px;\">UniEvent</p>"
                + "<p style=\"font-size:12px;color:#999;margin:0 0 16px;\">University Event Management System</p>"
                + "<p style=\"font-size:11px;color:#bbb;margin:0;\">This is an automated message. Please do not reply to this email.</p>"
                + "</td></tr>"

                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }

    private String buildDetailRow(String label, String value) {
        return "<tr>"
                + "<td style=\"padding:8px 0;border-bottom:1px solid rgba(0,0,0,0.05);width:160px;font-size:12px;font-weight:600;color:#747781;text-transform:uppercase;letter-spacing:0.5px;vertical-align:top;\">" + label + "</td>"
                + "<td style=\"padding:8px 0 8px 12px;border-bottom:1px solid rgba(0,0,0,0.05);font-size:14px;font-weight:500;color:#191c1d;vertical-align:top;\">" + value + "</td>"
                + "</tr>";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
