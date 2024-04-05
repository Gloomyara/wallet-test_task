package ru.antonovmikhail.wallet.service;

import ru.antonovmikhail.wallet.model.dto.NewTransaction;
import ru.antonovmikhail.wallet.model.dto.TransactionDto;

import java.util.UUID;

public interface WalletService {


    TransactionDto getWallet(UUID id);

    TransactionDto saveNewTransaction(NewTransaction dto);
}
