package ru.antonovmikhail.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.antonovmikhail.wallet.model.Wallet;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

}
