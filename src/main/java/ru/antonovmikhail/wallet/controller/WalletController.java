package ru.antonovmikhail.wallet.controller;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.antonovmikhail.wallet.model.dto.NewTransaction;
import ru.antonovmikhail.wallet.model.dto.TransactionDto;
import ru.antonovmikhail.wallet.service.WalletService;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Validated
@Controller
@RequestMapping("api/v1")
public class WalletController {
    private final WalletService walletService;
    private final RateLimiter rateLimiter;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
        this.rateLimiter = RateLimiter.create(1000);
    }

    @GetMapping("wallets/{WALLET_UUID}")
    public ResponseEntity<TransactionDto> getWallet(
            @PathVariable(value = "WALLET_UUID") UUID id) throws TimeoutException {
        checkRate();
        log.info("Received GET api/v1/wallets/{} request.", id);
        return ResponseEntity.ok(walletService.getWallet(id));
    }

    @PostMapping("wallet")
    public ResponseEntity<TransactionDto> createNewTransaction(
            @Valid @RequestBody NewTransaction dto) throws TimeoutException {
        log.info("Received POST api/v1/wallet request, dto: {}.", dto);
        checkRate();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(walletService.saveNewTransaction(dto));
    }

    private void checkRate() throws TimeoutException {
        rateLimiter.acquire();
        if (!rateLimiter.tryAcquire(1, 1, TimeUnit.MILLISECONDS))
            throw new TimeoutException("Timeout");
    }
}



