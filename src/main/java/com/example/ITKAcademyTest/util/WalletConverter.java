package com.example.ITKAcademyTest.util;

import com.example.ITKAcademyTest.models.dto.WalletDto;
import com.example.ITKAcademyTest.models.entity.Wallet;
import org.springframework.stereotype.Controller;

@Controller
public class WalletConverter {

    public WalletDto convertToDto(Wallet wallet) {
        return WalletDto.builder()
                .uuid(wallet.getUuid())
                .balance(wallet.getBalance())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .version(wallet.getVersion())
                .build();
    }
}
