package br.com.finguard.infrastructure.web.controller;

import br.com.finguard.application.port.in.GetCashFlowUseCase;
import br.com.finguard.domain.model.CashFlow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CashFlowController.class)
class CashFlowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetCashFlowUseCase getCashFlowUseCase;

    @Test
    void shouldGetCashFlow() throws Exception {
        var cashFlow = new CashFlow(
                "user-1",
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("500.00")
        );

        when(getCashFlowUseCase.getByUserId("user-1")).thenReturn(cashFlow);

        mockMvc.perform(get("/api/v1/cash-flow").header("X-User-Id", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.balance").value(500))
                .andExpect(jsonPath("$.totalCredits").value(1000))
                .andExpect(jsonPath("$.totalDebits").value(500));

        verify(getCashFlowUseCase).getByUserId("user-1");
    }

    @Test
    void shouldUseDefaultUserWhenHeaderOmitted() throws Exception {
        var cashFlow = CashFlow.empty("default-user");

        when(getCashFlowUseCase.getByUserId("default-user")).thenReturn(cashFlow);

        mockMvc.perform(get("/api/v1/cash-flow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("default-user"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.totalCredits").value(0))
                .andExpect(jsonPath("$.totalDebits").value(0));

        verify(getCashFlowUseCase).getByUserId("default-user");
    }

    @Test
    void shouldReturnEmptyCashFlowForNewUser() throws Exception {
        when(getCashFlowUseCase.getByUserId("new-user")).thenReturn(CashFlow.empty("new-user"));

        mockMvc.perform(get("/api/v1/cash-flow").header("X-User-Id", "new-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.totalCredits").value(0))
                .andExpect(jsonPath("$.totalDebits").value(0));
    }
}
