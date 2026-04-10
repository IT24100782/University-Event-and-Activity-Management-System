package com.unievent.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.unievent.entity.Club;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    /**
     * Generate a beautifully designed PDF with all club details.
     */
    public byte[] generateClubApprovalPdf(Club club) {
        String approvalDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"));

        String frequency = club.getEventFrequency() != null ? club.getEventFrequency() : "N/A";

        // Build activity chips HTML
        StringBuilder activityChips = new StringBuilder();
        if (club.getPlannedActivities() != null && !club.getPlannedActivities().isEmpty()) {
            for (String activity : club.getPlannedActivities().split(",")) {
                activityChips.append("<span style=\"display:inline-block;background:#e8eaf6;color:#21417f;padding:4px 12px;border-radius:20px;font-size:11px;font-weight:600;margin:3px 4px 3px 0;\">")
                        .append(activity.trim())
                        .append("</span>");
            }
        } else {
            activityChips.append("<span style=\"color:#999;\">Not specified</span>");
        }

        // Build membership chips HTML
        StringBuilder membershipChips = new StringBuilder();
        if (club.getMembershipType() != null && !club.getMembershipType().isEmpty()) {
            for (String type : club.getMembershipType().split(",")) {
                membershipChips.append("<span style=\"display:inline-block;background:#e8f5e9;color:#006e25;padding:4px 12px;border-radius:20px;font-size:11px;font-weight:600;margin:3px 4px 3px 0;\">")
                        .append(type.trim())
                        .append("</span>");
            }
        } else {
            membershipChips.append("<span style=\"color:#999;\">Not specified</span>");
        }

        String html = "<!DOCTYPE html>"
                + "<html><head><style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; color: #191c1d; background: #ffffff; }"
                + ".container { max-width: 650px; margin: 0 auto; padding: 40px 48px; }"
                + ".header { background: linear-gradient(135deg, #21417f 0%, #3b5998 100%); padding: 48px 40px; text-align: center; border-radius: 0 0 24px 24px; }"
                + ".header h1 { color: #ffffff; font-size: 28px; font-weight: 800; letter-spacing: -0.5px; margin-bottom: 8px; }"
                + ".header p { color: rgba(255,255,255,0.85); font-size: 14px; }"
                + ".badge { display: inline-block; background: #006e25; color: #fff; padding: 6px 18px; border-radius: 20px; font-size: 12px; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; margin-top: 16px; }"
                + ".section { margin-top: 32px; }"
                + ".section-title { font-size: 13px; font-weight: 700; text-transform: uppercase; letter-spacing: 1.5px; color: #21417f; border-bottom: 2px solid #d9e2ff; padding-bottom: 8px; margin-bottom: 16px; }"
                + ".detail-row { display: flex; padding: 10px 0; border-bottom: 1px solid #f0f0f0; }"
                + ".detail-label { width: 200px; font-size: 12px; font-weight: 600; color: #747781; text-transform: uppercase; letter-spacing: 0.5px; }"
                + ".detail-value { flex: 1; font-size: 13px; font-weight: 500; color: #191c1d; }"
                + ".description-box { background: #f8f9fa; border-left: 4px solid #21417f; padding: 16px 20px; border-radius: 0 8px 8px 0; margin-top: 12px; font-size: 13px; line-height: 1.7; color: #444650; }"
                + ".certificate-box { background: linear-gradient(135deg, #f8f9ff 0%, #eef2ff 100%); border: 2px solid #d9e2ff; border-radius: 16px; padding: 32px; text-align: center; margin-top: 32px; }"
                + ".certificate-box h2 { font-size: 22px; color: #21417f; margin-bottom: 6px; }"
                + ".certificate-box .club-name { font-size: 26px; font-weight: 800; color: #21417f; margin: 16px 0 8px; }"
                + ".certificate-box .date { font-size: 12px; color: #747781; margin-top: 12px; }"
                + ".footer { text-align: center; margin-top: 40px; padding-top: 24px; border-top: 1px solid #e0e0e0; }"
                + ".footer p { font-size: 11px; color: #999; }"
                + ".footer .brand { font-weight: 700; color: #21417f; font-size: 13px; }"
                + "table { width: 100%; border-collapse: collapse; }"
                + "table td { padding: 10px 0; border-bottom: 1px solid #f0f0f0; vertical-align: top; }"
                + "table td:first-child { width: 200px; font-size: 12px; font-weight: 600; color: #747781; text-transform: uppercase; letter-spacing: 0.5px; }"
                + "table td:last-child { font-size: 13px; font-weight: 500; color: #191c1d; }"
                + "</style></head><body>"

                // Header
                + "<div class=\"header\">"
                + "<h1>🎉 Club Approved!</h1>"
                + "<p>Your organization has been officially registered on UniEvent</p>"
                + "<div class=\"badge\">✓ APPROVED</div>"
                + "</div>"

                + "<div class=\"container\">"

                // Certificate
                + "<div class=\"certificate-box\">"
                + "<h2>Certificate of Registration</h2>"
                + "<p style=\"color:#747781;font-size:13px;\">This certifies that the following organization has been</p>"
                + "<p style=\"color:#747781;font-size:13px;\">officially approved and registered on the UniEvent platform.</p>"
                + "<div class=\"club-name\">" + escapeHtml(club.getName()) + "</div>"
                + "<p style=\"color:#21417f;font-size:14px;font-weight:600;\">" + escapeHtml(club.getCategory() != null ? club.getCategory() : "General") + "</p>"
                + "<p class=\"date\">Approved on " + approvalDate + "</p>"
                + "</div>"

                // Club Details
                + "<div class=\"section\">"
                + "<div class=\"section-title\">Club Information</div>"
                + "<table>"
                + "<tr><td>Club Name</td><td><strong>" + escapeHtml(club.getName()) + "</strong></td></tr>"
                + "<tr><td>Category</td><td>" + escapeHtml(club.getCategory() != null ? club.getCategory() : "N/A") + "</td></tr>"
                + "<tr><td>Club Email</td><td>" + escapeHtml(club.getEmail() != null ? club.getEmail() : "N/A") + "</td></tr>"
                + "<tr><td>President Email</td><td>" + escapeHtml(club.getPresidentEmail() != null ? club.getPresidentEmail() : "N/A") + "</td></tr>"
                + "<tr><td>Event Frequency</td><td>" + escapeHtml(frequency) + "</td></tr>"
                + "</table>"
                + "</div>"

                // Description
                + "<div class=\"section\">"
                + "<div class=\"section-title\">Mission Statement</div>"
                + "<div class=\"description-box\">" + escapeHtml(club.getDescription() != null ? club.getDescription() : "No description provided.") + "</div>"
                + "</div>"

                // Planned Activities
                + "<div class=\"section\">"
                + "<div class=\"section-title\">Planned Activities</div>"
                + "<div style=\"padding:8px 0;\">" + activityChips.toString() + "</div>"
                + "</div>"

                // Membership Types
                + "<div class=\"section\">"
                + "<div class=\"section-title\">Membership Types</div>"
                + "<div style=\"padding:8px 0;\">" + membershipChips.toString() + "</div>"
                + "</div>"

                // Footer
                + "<div class=\"footer\">"
                + "<p class=\"brand\">UniEvent</p>"
                + "<p>University Event Management System</p>"
                + "<p style=\"margin-top:8px;\">This is an auto-generated document. Please keep it for your records.</p>"
                + "</div>"

                + "</div></body></html>";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(html, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate club approval PDF", e);
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
