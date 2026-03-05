package br.com.finguard.application.service;

import br.com.finguard.application.port.out.TransactionPersistencePort;
import br.com.finguard.domain.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTransactionsServiceTest {

    @Mock
    private TransactionPersistencePort transactionPort;

    @InjectMocks
    private ListTransactionsService service;

    @Test
    void shouldReturnTransactionsByUserId() {
        var userId = "user-1";
        var transactions = List.of(
                new Transaction(
                        TransactionId.generate(),
                        userId,
                        new BigDecimal("100.00"),
                        TransactionType.PIX_IN,
                        "João",
                        "Pix recebido",
                        Instant.now(),
                        "com.nubank"
                ),
                new Transaction(
                        TransactionId.generate(),
                        userId,
                        new BigDecimal("50.00"),
                        TransactionType.PAYMENT,
                        "Supermercado",
                        "Pagamento aprovado",
                        Instant.now(),
                        "com.nubank"
                )
        );

        when(transactionPort.findAllByUserId(userId)).thenReturn(transactions);

        var result = service.listByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("100.00"), result.get(0).amount());
        assertEquals(TransactionType.PIX_IN, result.get(0).type());
        assertEquals(new BigDecimal("50.00"), result.get(1).amount());
        assertEquals(TransactionType.PAYMENT, result.get(1).type());
        verify(transactionPort).findAllByUserId(userId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoTransactions() {
        var userId = "user-without-transactions";

        when(transactionPort.findAllByUserId(userId)).thenReturn(List.of());

        var result = service.listByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionPort).findAllByUserId(userId);
    }
}
