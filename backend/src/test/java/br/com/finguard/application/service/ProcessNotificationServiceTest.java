package br.com.finguard.application.service;

import br.com.finguard.application.port.out.CashFlowPersistencePort;
import br.com.finguard.application.port.out.TransactionExtractionPort;
import br.com.finguard.application.port.out.TransactionPersistencePort;
import br.com.finguard.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessNotificationServiceTest {

    @Mock
    private TransactionExtractionPort extractionPort;

    @Mock
    private TransactionPersistencePort transactionPort;

    @Mock
    private CashFlowPersistencePort cashFlowPort;

    @InjectMocks
    private ProcessNotificationService service;

    @Test
    void shouldProcessNotificationAndUpdateCashFlow() {
        var extracted = new ExtractedTransactionData(
                new BigDecimal("100.00"),
                TransactionType.PIX_IN,
                "João",
                "Pix recebido R$ 100,00",
                "com.nubank"
        );
        var savedTransaction = new Transaction(
                TransactionId.generate(),
                "user-1",
                new BigDecimal("100.00"),
                TransactionType.PIX_IN,
                "João",
                "raw",
                java.time.Instant.now(),
                "com.nubank"
        );

        when(extractionPort.extract(any())).thenReturn(Optional.of(extracted));
        when(transactionPort.save(any())).thenReturn(savedTransaction);
        when(cashFlowPort.findByUserId("user-1")).thenReturn(Optional.empty());

        var result = service.process("user-1", "Pix recebido R$ 100,00", "com.nubank");

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.amount());
        assertEquals(TransactionType.PIX_IN, result.type());
        verify(cashFlowPort).save(any(CashFlow.class));
    }

    @Test
    void shouldThrowWhenExtractionFails() {
        when(extractionPort.extract(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.process("user-1", "Invalid content", "com.banco"));
    }
}
