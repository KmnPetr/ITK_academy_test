package com.example.ITKAcademyTest.models.dto;

import com.example.ITKAcademyTest.models.enums.OperationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WalletOperationRequest {
    @NotNull
    private UUID walletId;

    @NotNull
    private OperationType operationType;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}
