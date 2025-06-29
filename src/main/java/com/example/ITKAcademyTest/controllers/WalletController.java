package com.example.ITKAcademyTest.controllers;

import com.example.ITKAcademyTest.models.dto.WalletDto;
import com.example.ITKAcademyTest.models.dto.WalletOperationRequest;
import com.example.ITKAcademyTest.models.entity.Wallet;
import com.example.ITKAcademyTest.services.WalletService;
import com.example.ITKAcademyTest.util.WalletConverter;
import com.example.ITKAcademyTest.validation.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("v1/wallet")
public class WalletController {
    private final WalletService service;
    private final WalletConverter converter;


    @Autowired
    public WalletController(WalletService service, WalletConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @PostMapping()
    public WalletDto balanceOperation(@Valid @RequestBody WalletOperationRequest request) throws Exception {
        Wallet wallet = service.balanceOperationWithHybridLock(request.getWalletId(), request.getOperationType(), request.getAmount());
        return converter.convertToDto(wallet);
    }

    @GetMapping("/{uuid}")
    public BigDecimal getBalance(@PathVariable UUID uuid) throws NotFoundException {
        return service.getBalance(uuid);
    }

    @PostMapping("/create")
    public WalletDto createWallet(){
        Wallet wallet = service.createWallet();
        return converter.convertToDto(wallet);
    }
}
