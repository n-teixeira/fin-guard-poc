package br.com.finguard.infrastructure.adapter.extractor;

import br.com.finguard.domain.model.ExtractedTransactionData;
import br.com.finguard.domain.model.NotificationPayload;
import br.com.finguard.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RegexTransactionExtractorTest {

    private RegexTransactionExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new RegexTransactionExtractor();
    }

    @Test
    void shouldExtractPixReceived() {
        var payload = new NotificationPayload(
                "Pix recebido de João Silva. Valor R$ 150,00",
                "com.nubank"
        );

        var result = extractor.extract(payload);

        assertTrue(result.isPresent());
        ExtractedTransactionData data = result.get();
        assertEquals(new BigDecimal("150.00"), data.amount());
        assertEquals(TransactionType.PIX_IN, data.type());
        assertEquals("com.nubank", data.bankIdentifier());
    }

    @Test
    void shouldExtractPayment() {
        var payload = new NotificationPayload(
                "Pagamento aprovado no valor de R$ 89,90. Supermercado XYZ",
                "com.inter"
        );

        var result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("89.90"), result.get().amount());
        assertEquals(TransactionType.PAYMENT, result.get().type());
    }

    @Test
    void shouldReturnEmptyForBlankContent() {
        var payload = new NotificationPayload("", "com.banco");

        var result = extractor.extract(payload);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoAmountFound() {
        var payload = new NotificationPayload(
                "Sua fatura está disponível para consulta",
                "com.banco"
        );

        var result = extractor.extract(payload);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldExtractPixSent() {
        var payload = new NotificationPayload(
                "Pix enviado para Pedro Costa. Valor R$ 200,00",
                "com.itau"
        );

        var result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("200.00"), result.get().amount());
        assertEquals(TransactionType.PIX_OUT, result.get().type());
    }

    @Test
    void shouldExtractTransferReceived() {
        var payload = new NotificationPayload(
                "Transferência recebida de Banco XYZ. R$ 500,00",
                "com.bradesco"
        );

        var result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("500.00"), result.get().amount());
        assertEquals(TransactionType.TRANSFER_IN, result.get().type());
    }

    @Test
    void shouldExtractTransferSent() {
        var payload = new NotificationPayload(
                "Transferência enviada para conta poupança. Valor: R$ 1.000,00",
                "com.bb"
        );

        var result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("1000.00"), result.get().amount());
        assertEquals(TransactionType.TRANSFER_OUT, result.get().type());
    }

    @Test
    void shouldExtractAmountWithValorPrefix() {
        var payload = new NotificationPayload(
                "Compra aprovada. valor: R$ 45,50 no estabelecimento Loja XYZ",
                "com.santander"
        );

        var result = extractor.extract(payload);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("45.50"), result.get().amount());
        assertEquals(TransactionType.PAYMENT, result.get().type());
    }
}
