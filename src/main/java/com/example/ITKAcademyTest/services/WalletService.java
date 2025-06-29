package com.example.ITKAcademyTest.services;

import com.example.ITKAcademyTest.models.entity.Wallet;
import com.example.ITKAcademyTest.models.enums.OperationType;
import com.example.ITKAcademyTest.repositories.WalletRepository;
import com.example.ITKAcademyTest.validation.exceptions.InsufficientFundsException;
import com.example.ITKAcademyTest.validation.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WalletService {
    private final WalletRepository repository;

    @Autowired
    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Wallet balanceOperationWithHybridLock(UUID walletId, OperationType operationType, BigDecimal amount) throws Exception {
        Wallet wallet = repository
                .findById(walletId)
                .orElseThrow(()->new NotFoundException(String.format("Wallet with uuid \"%s\" not found.",walletId)));

        BigDecimal newBalance = calculateNewBalance(wallet.getBalance(), operationType, amount);
        wallet.setBalance(newBalance);

        try {
            //Use optimistic lock
            return repository.save(wallet);
        } catch (ObjectOptimisticLockingFailureException ex) {
            //Use pessimistic lock
            Wallet lokedWallet = repository
                    .findByIdForUpdate(walletId)
                    .orElseThrow(()->new NotFoundException(String.format("Wallet with uuid \"%s\" not found.",walletId)));

            BigDecimal newBalance2 = calculateNewBalance(wallet.getBalance(), operationType, amount);
            wallet.setBalance(newBalance);
            return repository.save(wallet);
        }
    }

    private BigDecimal calculateNewBalance(BigDecimal balance,OperationType operationType,BigDecimal amount) throws InsufficientFundsException {
        return switch (operationType) {
            case DEPOSIT -> deposit(balance, amount);
            case WITHDRAW -> withdraw(balance, amount);
        };
    }

    private BigDecimal withdraw(BigDecimal balance, BigDecimal amount) throws InsufficientFundsException {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds.");
        }
        return balance.subtract(amount);
    }

    private BigDecimal deposit(BigDecimal balance, BigDecimal amount) {
        return balance.add(amount);
    }

    public BigDecimal getBalance(UUID uuid) throws NotFoundException {
        return repository
                .findById(uuid)
                .orElseThrow(()->new NotFoundException(String.format("Wallet with uuid \"%s\" not found.",uuid)))
                .getBalance();
    }

    @Transactional
    public Wallet createWallet() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        return repository.save(wallet);
    }
}
