package br.com.finguard.infrastructure.web.controller;

import br.com.finguard.application.port.in.ListTransactionsUseCase;
import br.com.finguard.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListTransactionsUseCase listTransactionsUseCase;

    @Test
    void shouldListTransactions() throws Exception {
        var transactionId = UUID.randomUUID();
        var transactions = List.of(
                new Transaction(
                        new TransactionId(transactionId),
                        "user-1",
                        new BigDecimal("150.00"),
                        TransactionType.PIX_IN,
                        "Maria Silva",
                        "Pix recebido de Maria",
                        Instant.now(),
                        "com.nubank"
                )
        );

        when(listTransactionsUseCase.listByUserId("user-1")).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions").header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].amount").value(150))
                .andExpect(jsonPath("$[0].type").value("PIX_IN"))
                .andExpect(jsonPath("$[0].origin").value("Maria Silva"));

        verify(listTransactionsUseCase).listByUserId("user-1");
    }

    @Test
    void shouldUseDefaultUserWhenHeaderOmitted() throws Exception {
        when(listTransactionsUseCase.listByUserId("default-user")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(listTransactionsUseCase).listByUserId("default-user");
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactions() throws Exception {
        when(listTransactionsUseCase.listByUserId("empty-user")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/transactions").header("X-User-Id", "empty-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(listTransactionsUseCase).listByUserId("empty-user");
    }
}
