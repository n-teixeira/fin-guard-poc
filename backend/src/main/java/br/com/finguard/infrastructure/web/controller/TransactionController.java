package br.com.finguard.infrastructure.web.controller;

import br.com.finguard.application.port.in.ListTransactionsUseCase;
import br.com.finguard.infrastructure.web.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ListTransactionsUseCase listTransactionsUseCase;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "default-user") String userId) {

        var transactions = listTransactionsUseCase.listByUserId(userId);
        var response = transactions.stream()
                .map(TransactionResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}
