package ecommerce.system.api.tools;

import ecommerce.system.api.models.SimpleMailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Component
public class PasswordRecoverHandler {

    @Value("${password.recover.token.expiration.hours}")
    private int expirationHours;

    @Value("${application.front.customer.url}")
    private String baseUrl;

    @Value("${password.recover.token.key}")
    private String tokenKey;

    private final AESCodec aesCodec;
    private final EmailSender emailSender;

    @Autowired
    public PasswordRecoverHandler(AESCodec aesCodec, EmailSender emailSender) {
        this.aesCodec = aesCodec;
        this.emailSender = emailSender;
    }

    private String createBase(int userId) {

        return LocalDateTime.now().plusHours(expirationHours).toString() + "|" + userId;
    }

    public String generateLink(int userId) throws Exception {

        String base = this.createBase(userId);
        String encryptedParameter = this.aesCodec.encryptText(base, this.tokenKey);

        return baseUrl + "/password-recover/" + encryptedParameter;
    }

    public int extractId(String token) throws Exception {

        String decryptedToken = this.aesCodec.decryptText(token, this.tokenKey);

        String[] splitedToken = decryptedToken.split(Pattern.quote("|"), 2);

        int id = Integer.parseInt(splitedToken[1]);

        return id;
    }

    public LocalDateTime extractExpirationDate(String token) throws Exception {

        String decryptedToken = this.aesCodec.decryptText(token, this.tokenKey);

        String[] splitedToken = decryptedToken.split(Pattern.quote("|"), 0);

        LocalDateTime expirationDate = LocalDateTime.parse(splitedToken[0]);

        return expirationDate;
    }

    public boolean validateToken(String token) throws Exception {

        LocalDateTime expirationDate = this.extractExpirationDate(token);

        return LocalDateTime.now().isBefore(expirationDate);
    }

    public void sendEmail(int userId, String userEmail) throws Exception {

        String link = this.generateLink(userId);

        String emailTemplate = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"https://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html xmlns=\"https://www.w3.org/1999/xhtml\"><head><title>Recuperação de senha</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><meta name=\"viewport\" content=\"width=device-width\" /><style type=\"text/css\">@media screen{@font-face{font-family:'Source Sans Pro';font-style:normal;font-weight:400;src:local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff')}@font-face{font-family:'Source Sans Pro';font-style:normal;font-weight:700;src:local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff')}}body,table,td,a{-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}table,td{mso-table-rspace:0pt;mso-table-lspace:0pt}img{-ms-interpolation-mode:bicubic}a[x-apple-data-detectors]{font-family:inherit !important;font-size:inherit !important;font-weight:inherit !important;line-height:inherit !important;color:inherit !important;text-decoration:none !important}div[style*=\"margin: 16px 0;\"]{margin:0 !important}body{width:100% !important;height:100% !important;padding:0 !important;margin:0 !important}table{border-collapse:collapse !important}a{color:#1a82e2}img{height:auto;line-height:100%;text-decoration:none;border:0;outline:none}.preheader{display:none;max-width:0;max-height:0;overflow:hidden;font-size:1px;line-height:1px;color:#fff;opacity:0}.header-logo{display:block;width:48px;max-width:48px;min-width:48px;border:0;width:48}.title-container{padding:36px 24px 0;font-family:'Source Sans Pro',Helvetica,Arial,sans-serif;border-top:3px solid #d4dadf;background-color:#fff}.title{margin:0;font-size:32px;font-weight:700;letter-spacing:-1px;line-height:48px}.body-text{padding:24px;font-family:'Source Sans Pro',Helvetica,Arial,sans-serif;font-size:16px;line-height:24px}p{margin:0}.button{display:inline-block;padding:16px 36px;font-family:'Source Sans Pro',Helvetica,Arial,sans-serif;font-size:16px;color:#fff;text-decoration:none;border-radius:6px}.goodbye{padding:24px;font-family:'Source Sans Pro',Helvetica,Arial,sans-serif;font-size:16px;line-height:24px;border-bottom:3px solid #d4dadf}.footer-text{padding:12px 24px;font-family:'Source Sans Pro',Helvetica,Arial,sans-serif;font-size:14px;line-height:20px;color:#666}.footer-contact{padding:12px 24px;font-family:'Source Sans Pro',Helvetica,Arial,sans-serif;font-size:14px;line-height:20px;color:#666}</style></head><body style=\"background-color: #e9ecef;\"><div class=\"preheader\"> Esqueceu sua senha? Não se preocupe, te ajudamos com isso!</div><table class=\"body\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" data-made-with-foundation><tbody><tr><td class=\"float-center\" align=\"center\" valign=\"top\"><center><table class=\"container\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tbody><tr align=\"center\" bgcolor=\"#e9ecef\"><td><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\"> <img src=\"data:image/png;base64, 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\" alt=\"header-logo\" class=\"header-logo\"></td></tr></table></td></tr><tr><td align=\"center\" bgcolor=\"#e9ecef\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td class=\"title-container\"><h1> Recuperação de senha</h1></td></tr></table></td></tr><tr><td align=\"center\" bgcolor=\"#e9ecef\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td align=\"left\" bgcolor=\"#ffffff\" class=\"body-text\"><p> Pressione o botão abaixo para cadastrar uma nova senha.</p></td></tr><tr><td align=\"left\" bgcolor=\"#ffffff\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\"> <a href=\"[[link]]\" target=\"_blank\" class=\"button\"> Cadastrar nova senha </a></td></tr></table></td></tr></table></td></tr><tr><td align=\"left\" bgcolor=\"#ffffff\" class=\"goodbye\"><p> Conte conosco, <br> Seus amigos do E-commerce System</p></td></tr></table></td></tr><tr><td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\"><tr><td align=\"center\" bgcolor=\"#e9ecef\" class=\"footer-text\"><p> Você recebeu esse e-mail porque recebemos uma solitação de recuperaão de senha para a sua conta. <br> Se não foi você, por favor, ignore este e-mail ou entre em contato conosco para mais detalhes.</p></td></tr><tr><td align=\"center\" bgcolor=\"#e9ecef\" class=\"footer-contact\"><p> Avenida Paulista, 123, São Paulo - SP <br> contato@ecommercesystem.com</p></td></tr></table></td></tr></tbody></table></center></td></tr></tbody></table></body></html>";

        emailTemplate.replace("[[link]]", link);

        SimpleMailModel mail = new SimpleMailModel(userEmail, "Recuperação de senha", emailTemplate);

        this.emailSender.sendMimeEmail(mail);
    }
}
