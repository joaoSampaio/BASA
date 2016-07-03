package pt.ulisboa.tecnico.mybasaclient.rest.mail;

/**
 * Created by Sampaio on 08/06/2016.
 */
public class WelcomeTemplate {
    public static String getTemplate(){

        String text = "<html>\n" +
                "<head>    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Simples-Minimalistic Responsive Template</title><style type=\"text/css\">\n" +
                "         /* Client-specific Styles */\n" +
                "         #outlook a {padding:0;} /* Force Outlook to provide a \"view in browser\" menu link. */\n" +
                "         body{width:100% !important; -webkit-text-size-adjust:100%; -ms-text-size-adjust:100%; margin:0; padding:0;}\n" +
                "         /* Prevent Webkit and Windows Mobile platforms from changing default font sizes, while not breaking desktop design. */\n" +
                "         .ExternalClass {width:100%;} /* Force Hotmail to display emails at full width */\n" +
                "         .ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div {line-height: 100%;} /* Force Hotmail to display normal line spacing.*/\n" +
                "         #backgroundTable {margin:0; padding:0; width:100% !important; line-height: 100% !important;}\n" +
                "         img {outline:none; text-decoration:none;border:none; -ms-interpolation-mode: bicubic;}\n" +
                "         a img {border:none;}\n" +
                "         .image_fix {display:block;}\n" +
                "         p {margin: 0px 0px !important;}\n" +
                "         table td {border-collapse: collapse;}\n" +
                "         table { border-collapse:collapse; mso-table-lspace:0pt; mso-table-rspace:0pt; }\n" +
                "         a {color: #0a8cce;text-decoration: none;text-decoration:none!important;}\n" +
                "         /*STYLES*/\n" +
                "         table[class=full] { width: 100%; clear: both; }\n" +
                "         /*IPAD STYLES*/\n" +
                "         @media only screen and (max-width: 640px) {\n" +
                "         a[href^=\"tel\"], a[href^=\"sms\"] {\n" +
                "         text-decoration: none;\n" +
                "         color: #0a8cce; /* or whatever your want */\n" +
                "         pointer-events: none;\n" +
                "         cursor: default;\n" +
                "         }\n" +
                "         .mobile_link a[href^=\"tel\"], .mobile_link a[href^=\"sms\"] {\n" +
                "         text-decoration: default;\n" +
                "         color: #0a8cce !important;\n" +
                "         pointer-events: auto;\n" +
                "         cursor: default;\n" +
                "         }\n" +
                "         table[class=devicewidth] {width: 440px!important;text-align:center!important;}\n" +
                "         table[class=devicewidthinner] {width: 420px!important;text-align:center!important;}\n" +
                "         img[class=banner] {width: 440px!important;height:220px!important;}\n" +
                "         img[class=colimg2] {width: 440px!important;height:220px!important;}\n" +
                "         \n" +
                "         \n" +
                "         }\n" +
                "         /*IPHONE STYLES*/\n" +
                "         @media only screen and (max-width: 480px) {\n" +
                "         a[href^=\"tel\"], a[href^=\"sms\"] {\n" +
                "         text-decoration: none;\n" +
                "         color: #0a8cce; /* or whatever your want */\n" +
                "         pointer-events: none;\n" +
                "         cursor: default;\n" +
                "         }\n" +
                "         .mobile_link a[href^=\"tel\"], .mobile_link a[href^=\"sms\"] {\n" +
                "         text-decoration: default;\n" +
                "         color: #0a8cce !important; \n" +
                "         pointer-events: auto;\n" +
                "         cursor: default;\n" +
                "         }\n" +
                "         table[class=devicewidth] {width: 280px!important;text-align:center!important;}\n" +
                "         table[class=devicewidthinner] {width: 260px!important;text-align:center!important;}\n" +
                "         img[class=banner] {width: 280px!important;height:140px!important;}\n" +
                "         img[class=colimg2] {width: 280px!important;height:140px!important;}\n" +
                "         \n" +
                "        \n" +
                "         }\n" +
                "      </style>\n" +
                "</head><body>\n" +
                "\n" +
                "<table width=\"100%\" bgcolor=\"#ffffff\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t<div class=\"innerbg\">\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td width=\"100%\">\n" +
                "\t\t\t\t\t\t\t\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td height=\"20\" style=\"font-size:1px; line-height:1px; mso-line-height-rule: exactly;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t<table width=\"560\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"devicewidthinner\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- Title -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td style=\"font-family: Helvetica, arial, sans-serif; font-size: 30px; color: #333333; text-align:center; line-height: 30px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWelcome to Basa\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- End of Title -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td width=\"100%\" height=\"20\" style=\"font-size:1px; line-height:1px; mso-line-height-rule: exactly;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- End of spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- content -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td style=\"font-family: Helvetica, arial, sans-serif; font-size: 16px; color: #666666; text-align:center; line-height: 30px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<strong><span style=\"font-size: 14pt;\"><em>your personal office assistent</em></span></strong>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- End of content -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td height=\"20\" style=\"font-size:1px; line-height:1px; mso-line-height-rule: exactly;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t</table>\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "<table width=\"100%\" bgcolor=\"#ffffff\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t<div class=\"innerbg\">\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td width=\"100%\">\n" +
                "\t\t\t\t\t\t\t\t<table width=\"600\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<!-- start of image -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td align=\"center\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"imgpop\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"uploader_wrap\" style=\"width: 600px; margin-top: 130px; opacity: 0;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"upload_buttons\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"img_link\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"img_upload\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div class=\"img_edit\" style=\"visibility: visible;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</div> <a href=\"#\"><img width=\"600\" border=\"0\" height=\"600\" alt=\"\" style=\"display:block; border:none; outline:none; text-decoration:none;\" src=\"cid:qrcode.jpg\" class=\"banner\" /></a>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t<!-- end of image -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t</table>\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "<table width=\"100%\" bgcolor=\"#ffffff\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t<div class=\"innerbg\">\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<table width=\"600\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td align=\"center\" height=\"30\" style=\"font-size:1px; line-height:1px;\">\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td width=\"550\" align=\"center\" height=\"1\" bgcolor=\"#d1d1d1\" style=\"font-size:1px; line-height:1px;\">\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td align=\"center\" height=\"30\" style=\"font-size:1px; line-height:1px;\">\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t</table>\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "<table width=\"100%\" bgcolor=\"#ffffff\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t<div class=\"innerbg\">\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td width=\"100%\">\n" +
                "\t\t\t\t\t\t\t\t<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td height=\"20\" style=\"font-size:1px; line-height:1px; mso-line-height-rule: exactly;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t<table width=\"560\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"devicewidthinner\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- Title -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td style=\"font-family: Helvetica, arial, sans-serif; font-size: 30px; color: #333333; text-align:center; line-height: 30px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tInstructions\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- End of Title -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td width=\"100%\" height=\"20\" style=\"font-size:1px; line-height:1px; mso-line-height-rule: exactly;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- End of spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- content -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td style=\"font-family: Helvetica, arial, sans-serif; font-size: 16px; color: #666666; text-align:center; line-height: 30px;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWith the BASA mobile app you are automaticly recognized by your office.\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tThe office enviroment changes to fit it's occupants needs and desires.\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tIf you don't have an Android phone with the app, you can show the QR code to the BASA Hub in the office and you will be logged in.\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t\t<!-- End of content -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t\t<td height=\"20\" style=\"font-size:1px; line-height:1px; mso-line-height-rule: exactly;\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t\t\t\t<!-- Spacing -->\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t\t\t\t\t</table>\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t</table>\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "<table width=\"100%\" bgcolor=\"#ffffff\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "\t<tbody>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t<div class=\"innerbg\">\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<table width=\"600\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"devicewidth\">\n" +
                "\t\t\t\t\t<tbody>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td align=\"center\" height=\"30\" style=\"font-size:1px; line-height:1px;\">\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td width=\"550\" align=\"center\" height=\"1\" bgcolor=\"#d1d1d1\" style=\"font-size:1px; line-height:1px;\">\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<td align=\"center\" height=\"30\" style=\"font-size:1px; line-height:1px;\">\n" +
                "\t\t\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t</tbody>\n" +
                "\t\t\t\t</table>\n" +
                "\t\t\t</td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody>\n" +
                "</table></body>\n" +
                "</html>";


        return text;
    }
}
