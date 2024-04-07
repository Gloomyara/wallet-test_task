package ru.antonovmikhail.wallet;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.antonovmikhail.util.InsufficientAmountException;
import ru.antonovmikhail.wallet.model.Wallet;
import ru.antonovmikhail.wallet.model.dto.NewTransaction;
import ru.antonovmikhail.wallet.model.dto.TransactionDto;
import ru.antonovmikhail.wallet.repository.WalletRepository;
import ru.antonovmikhail.wallet.service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class WalletServiceTest {


    @Autowired
    WalletRepository repository;

    @Autowired
    WalletService walletService;

    @Test
    void getWallet() {
        Wallet wallet = repository.save(new Wallet(null, BigDecimal.valueOf(1000)));
        TransactionDto dtoOut = walletService.getWallet(wallet.getId());
        assertEquals(wallet.getId(), dtoOut.getId());
        assertEquals(wallet.getAmount(), dtoOut.getAmount());
    }

    @Test
    void getWallet_shouldThrowEntityNotFound() {
        repository.save(new Wallet(null, BigDecimal.valueOf(1000)));
        assertThrows(EntityNotFoundException.class,
                () -> walletService.getWallet(UUID.randomUUID()));
    }

    @Test
    void saveNewTransaction() {
        Wallet wallet = repository.save(new Wallet(null, BigDecimal.valueOf(1000)));
        NewTransaction dtoIn = new NewTransaction(wallet.getId(), NewTransaction.OperationType.WITHDRAW,
                wallet.getAmount(), LocalDateTime.now());
        TransactionDto dtoOut = walletService.saveNewTransaction(dtoIn);
        assertEquals(dtoIn.getId(), dtoOut.getId());
        assertEquals(dtoIn.getAmount(), dtoOut.getAmount());
        assertEquals(dtoIn.getOperation(), dtoOut.getOperation());
        assertEquals(dtoIn.getUpdateTime(), dtoOut.getUpdateTime());
    }

    @Test
    void saveNewTransaction_shouldThrowEntityNotFound() {
        Wallet wallet = repository.save(new Wallet(null, BigDecimal.valueOf(1000)));
        NewTransaction dtoIn = new NewTransaction(UUID.randomUUID(), NewTransaction.OperationType.WITHDRAW,
                wallet.getAmount(), LocalDateTime.now());
        assertThrows(EntityNotFoundException.class,
                () -> walletService.saveNewTransaction(dtoIn));
    }

    @Test
    void saveNewTransactionWithNotEnoughAmount_shouldThrowInsufficientAmountException() {
        Wallet wallet = repository.save(new Wallet(null, BigDecimal.valueOf(1000)));
        NewTransaction dtoIn = new NewTransaction(wallet.getId(), NewTransaction.OperationType.WITHDRAW,
                BigDecimal.valueOf(1001), LocalDateTime.now());
        assertThrows(InsufficientAmountException.class,
                () -> walletService.saveNewTransaction(dtoIn));
    }
}