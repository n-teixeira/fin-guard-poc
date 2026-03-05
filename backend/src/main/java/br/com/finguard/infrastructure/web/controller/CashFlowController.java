package br.com.finguard.infrastructure.web.controller;

import br.com.finguard.application.port.in.GetCashFlowUseCase;
import br.com.finguard.infrastructure.web.dto.CashFlowResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cash-flow")
@RequiredArgsConstructor
public class CashFlowController {

    private final GetCashFlowUseCase getCashFlowUseCase;

    @GetMapping
    public ResponseEntity<CashFlowResponse> get(
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "default-user") String userId) {

        var cashFlow = getCashFlowUseCase.getByUserId(userId);
        return ResponseEntity.ok(CashFlowResponse.from(cashFlow));
    }
}
