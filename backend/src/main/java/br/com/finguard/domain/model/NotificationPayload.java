package br.com.finguard.domain.model;

/**
 * Value Object - Payload bruto da notificação interceptada.
 */
public record NotificationPayload(String content, String packageName) {
}
