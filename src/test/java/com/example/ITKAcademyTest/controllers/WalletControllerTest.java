package com.example.ITKAcademyTest.controllers;

import com.example.ITKAcademyTest.models.dto.WalletOperationRequest;
import com.example.ITKAcademyTest.models.entity.Wallet;
import com.example.ITKAcademyTest.models.enums.OperationType;
import com.example.ITKAcademyTest.services.WalletService;
import com.example.ITKAcademyTest.util.WalletConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;
    @Spy
    private WalletConverter walletConverter = new WalletConverter();

    @InjectMocks
    private WalletController walletController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }


    @Test
    void balanceOperation() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest(
                UUID.randomUUID(),
                OperationType.DEPOSIT,
                BigDecimal.valueOf(1000)
        );
        String reqJson = objectMapper.writeValueAsString(request);

        Wallet returnWallet = new Wallet(request.getWalletId(),BigDecimal.valueOf(1000),LocalDateTime.now(),LocalDateTime.now(),1L);

        when(walletService.balanceOperationWithHybridLock(request.getWalletId(),request.getOperationType(),request.getAmount()))
                .thenReturn(returnWallet);

        mockMvc.perform(post("/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqJson))
                .andExpect(status().isOk());

        verify(walletService,times(1))
                .balanceOperationWithHybridLock(request.getWalletId(),request.getOperationType(),request.getAmount());
        verify(walletConverter,times(1))
                .convertToDto(returnWallet);
    }

    @Test
    void getBalance() throws Exception {
        UUID testUuid = UUID.randomUUID();

        when(walletService.getBalance(testUuid)).thenReturn(BigDecimal.TEN);

        mockMvc.perform(get("/v1/wallet/{uuid}", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10));
        verify(walletService,times(1)).getBalance(testUuid);
    }

    @Test
    void createWallet() throws Exception {
        UUID expectedUuid = UUID.randomUUID();
        Wallet testWallet = new Wallet(
                expectedUuid,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0L
        );

        when(walletService.createWallet()).thenReturn(testWallet);

        mockMvc.perform(post("/v1/wallet/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(expectedUuid.toString()))
                .andExpect(jsonPath("$.balance").value(0));
        verify(walletService,times(1)).createWallet();
        verify(walletConverter,times(1)).convertToDto(testWallet);
    }
}