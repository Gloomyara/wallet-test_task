package ru.antonovmikhail.wallet.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import ru.antonovmikhail.wallet.model.Wallet;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    Wallet save(Wallet wallet);
}
