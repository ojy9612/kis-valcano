package com.zeki.kisvolcano.domain._common.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.logging.MessageFormatter;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Setter
@Getter
public class GoogleChatAppender extends AppenderBase<ILoggingEvent> {
    private String webhookUri;
    private Layout<ILoggingEvent> layout = getDefaultLayout();
    private int timeout = 30000;

    private static Layout<ILoggingEvent> getDefaultLayout() {
        return new LayoutBase<>() {
            @Override
            public String doLayout(ILoggingEvent event) {
                return "-- [" + event.getLevel() + "]" +
                        event.getLoggerName() + " - " +
                        MessageFormatter.arrayFormat(event.getFormattedMessage(), event.getArgumentArray()).getMessage()
                                .replace("\n", "\n\t");
            }
        };
    }

    @Override
    protected void append(ILoggingEvent evt) {
        try {
            if (webhookUri != null && !webhookUri.isBlank()) {
                sendMessageWithWebhookUri(webhookUri, evt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            addError("Error sending message to GoogleChat: " + evt, e);
        }
    }

    private void sendMessageWithWebhookUri(String webhookUri, ILoggingEvent evt) throws IOException {
        String text = layout.doLayout(evt);

        GoogleChatWebhookDto webhookDto = new GoogleChatWebhookDto(text);
        byte[] bytes = new ObjectMapper().writeValueAsBytes(webhookDto);

        postMessage(webhookUri, "application/json", bytes);
    }

    private void postMessage(String uri, String contentType, byte[] bytes) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(uri).openConnection();
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestProperty("Content-Type", contentType);

        OutputStream os = conn.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }
}