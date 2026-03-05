package br.com.finguard.infrastructure.web.controller;

import br.com.finguard.application.port.in.ProcessNotificationUseCase;
import br.com.finguard.domain.model.Transaction;
import br.com.finguard.infrastructure.web.dto.NotificationRequest;
import br.com.finguard.infrastructure.web.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final ProcessNotificationUseCase processNotificationUseCase;

    @PostMapping("/process")
    public ResponseEntity<TransactionResponse> process(
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "default-user") String userId,
            @Valid @RequestBody NotificationRequest request) {

        Transaction transaction = processNotificationUseCase.process(
                userId,
                request.getContent(),
                request.getPackageName()
        );

        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }
}
