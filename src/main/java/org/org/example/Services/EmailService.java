package org.example.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarTokenVerificacion(String destinatario, String token) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Código de verificación - Abogado Inteligente");
        mensaje.setText("Tu código de verificación es: " + token + "\n\nEste código expira en 5 minutos.");
        mensaje.setFrom("abogadointeligenteia@gmail.com");
        mailSender.send(mensaje);
    }
}
