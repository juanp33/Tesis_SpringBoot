package org.example.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigo(String destinatario, String codigo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject("Código de verificación - Sistema Jurídico");
            mensaje.setText("Tu código de verificación es: " + codigo +
                    "\n\nEste código expira en 5 minutos.\n\nNo respondas a este correo.");

            mailSender.send(mensaje);
            System.out.println("✅ Correo enviado correctamente a " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
        }
    }
}