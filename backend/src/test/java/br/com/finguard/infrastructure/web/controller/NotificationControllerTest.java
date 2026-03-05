package br.com.finguard.infrastructure.web.controller;

import br.com.finguard.application.port.in.ProcessNotificationUseCase;
import br.com.finguard.domain.model.Transaction;
import br.com.finguard.domain.model.TransactionId;
import br.com.finguard.domain.model.TransactionType;
import br.com.finguard.infrastructure.web.dto.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessNotificationUseCase processNotificationUseCase;

    @Test
    void shouldProcessNotification() throws Exception {
        var request = NotificationRequest.builder()
                .content("Pix recebido de João. Valor R$ 100,00")
                .packageName("com.nubank")
                .build();

        var transaction = new Transaction(
                new TransactionId(java.util.UUID.randomUUID()),
                "user-1",
                new BigDecimal("100.00"),
                TransactionType.PIX_IN,
                "João",
                request.getContent(),
                Instant.now(),
                "com.nubank"
        );

        when(processNotificationUseCase.process(anyString(), anyString(), anyString()))
                .thenReturn(transaction);

        mockMvc.perform(post("/api/v1/notifications/process")
                        .header("X-User-Id", "user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.type").value("PIX_IN"))
                .andExpect(jsonPath("$.origin").value("João"));
    }
}
