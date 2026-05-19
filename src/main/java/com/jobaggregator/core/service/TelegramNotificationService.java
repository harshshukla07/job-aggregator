package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.LeverJobDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class TelegramNotificationService {

    private final WebClient webClient;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    public TelegramNotificationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void sendJobAlert(LeverJobDto job, String companyName) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        String message = String.format(
                "🚀 <b>New SDE-1 Alert!</b>\n\n" +
                        "<b>Company:</b> %s\n" +
                        "<b>Role:</b> %s\n" +
                        "<b>Type:</b> %s\n\n" +
                        "🔗 <a href=\"%s\">Apply/Referral Link</a>",
                companyName,
                job.text(),
                (job.categories() != null && job.categories().commitment() != null) ? job.categories().commitment() : "Full Time",
                job.hostedUrl()
        );

        Map<String, String> body = Map.of(
                "chat_id", chatId,
                "text", message,
                "parse_mode", "HTML"
        );

        try {
            webClient.post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Alert sent to Telegram for: " + job.text());
        } catch (Exception e) {
            System.err.println("Failed to send Telegram alert: " + e.getMessage());
        }
    }
}