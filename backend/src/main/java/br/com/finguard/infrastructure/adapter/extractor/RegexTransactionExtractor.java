package br.com.finguard.infrastructure.adapter.extractor;

import br.com.finguard.application.port.out.TransactionExtractionPort;
import br.com.finguard.domain.model.ExtractedTransactionData;
import br.com.finguard.domain.model.NotificationPayload;
import br.com.finguard.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter - Extrai transação usando Regex Pattern Matching.
 * Padrões comuns de notificações bancárias brasileiras.
 */
@Component
class RegexTransactionExtractor implements TransactionExtractionPort {

    private static final Pattern[] AMOUNT_PATTERNS = {
            Pattern.compile("R\\$?\\s*([\\d.,]+)"),
            Pattern.compile("([\\d.,]+)\\s*reais"),
            Pattern.compile("valor[:\s]+R\\$?\\s*([\\d.,]+)", Pattern.CASE_INSENSITIVE),
    };

    private static final Map.Entry<Pattern, TransactionType>[] TYPE_PATTERNS = new Map.Entry[]{
            Map.entry(Pattern.compile("pix\\s*recebido|entrada\\s*pix|pix\\s*in", Pattern.CASE_INSENSITIVE), TransactionType.PIX_IN),
            Map.entry(Pattern.compile("pix\\s*enviado|saída\\s*pix|pix\\s*out", Pattern.CASE_INSENSITIVE), TransactionType.PIX_OUT),
            Map.entry(Pattern.compile("transferência\\s*recebida|transferência\\s*entrada", Pattern.CASE_INSENSITIVE), TransactionType.TRANSFER_IN),
            Map.entry(Pattern.compile("transferência\\s*enviada|transferência\\s*saída", Pattern.CASE_INSENSITIVE), TransactionType.TRANSFER_OUT),
            Map.entry(Pattern.compile("pagamento|débito|cartão|compra", Pattern.CASE_INSENSITIVE), TransactionType.PAYMENT),
            Map.entry(Pattern.compile("crédito|depósito|recebido", Pattern.CASE_INSENSITIVE), TransactionType.CREDIT),
            Map.entry(Pattern.compile("débito|saque|retirada", Pattern.CASE_INSENSITIVE), TransactionType.DEBIT),
    };

    @Override
    public Optional<ExtractedTransactionData> extract(NotificationPayload payload) {
        String content = payload.content();
        if (content == null || content.isBlank()) {
            return Optional.empty();
        }

        var amount = extractAmount(content);
        if (amount.isEmpty()) {
            return Optional.empty();
        }

        var type = extractType(content);
        var origin = extractOrigin(content);

        var data = new ExtractedTransactionData(
                amount.get(),
                type,
                origin.orElse("Desconhecido"),
                content,
                payload.packageName()
        );

        return Optional.of(data);
    }

    private Optional<BigDecimal> extractAmount(String content) {
        for (var pattern : AMOUNT_PATTERNS) {
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String amountStr = matcher.group(1).replace(".", "").replace(",", ".");
                try {
                    return Optional.of(new BigDecimal(amountStr));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return Optional.empty();
    }

    private TransactionType extractType(String content) {
        for (var entry : TYPE_PATTERNS) {
            if (entry.getKey().matcher(content).find()) {
                return entry.getValue();
            }
        }
        return TransactionType.UNKNOWN;
    }

    private Optional<String> extractOrigin(String content) {
        var patterns = new Pattern[]{
                Pattern.compile("de\\s+([^.]+?)(?:\\.|\\s+R\\$|$)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("para\\s+([^.]+?)(?:\\.|\\s+R\\$|$)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("origem[:\s]+([^.]+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("de\\s+(\\w+\\s+\\w+)", Pattern.CASE_INSENSITIVE),
        };

        for (var pattern : patterns) {
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return Optional.of(matcher.group(1).trim());
            }
        }
        return Optional.empty();
    }
}
