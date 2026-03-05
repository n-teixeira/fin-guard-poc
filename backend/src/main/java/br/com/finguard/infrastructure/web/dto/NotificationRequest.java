package br.com.finguard.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "Conteúdo da notificação é obrigatório")
    private String content;

    @NotBlank(message = "Package name do app é obrigatório")
    private String packageName;
}
