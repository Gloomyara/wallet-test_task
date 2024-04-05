package ru.antonovmikhail.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.antonovmikhail.util.InsufficientAmountException;
import ru.antonovmikhail.wallet.mapper.WalletMapper;
import ru.antonovmikhail.wallet.model.Wallet;
import ru.antonovmikhail.wallet.model.dto.NewTransaction;
import ru.antonovmikhail.wallet.model.dto.TransactionDto;
import ru.antonovmikhail.wallet.repository.WalletRepository;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;
    //private final OperationsRepository history; для сохранения истории и нормальных DtoOut репозиторий или доп сервис
    private final WalletMapper mapper = Mappers.getMapper(WalletMapper.class);

    @Transactional(readOnly = true)
    @Override
    public TransactionDto getWallet(UUID id) throws EntityNotFoundException {
        return mapper.walletToDto(repository.findById(id).orElseThrow(() -> new EntityNotFoundException()));
    }

    @Override
    public TransactionDto saveNewTransaction(NewTransaction dto) throws EntityNotFoundException, InsufficientAmountException {
        Wallet wallet = repository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException());
        switch (dto.getOperation()) {
            case WITHDRAW:
                if (wallet.getAmount().compareTo(dto.getAmount()) >= 0) {
                    wallet.getAmount().subtract(dto.getAmount());
                    // dto.setAmount(wallet.getAmount());  в зависимости какой ответ нужен; возвращаем баланс кошелька?
                    repository.save(wallet); // после сохранить успешную операцию и вернуть дто операции
                } else {
                    throw new InsufficientAmountException();
                }
                break;
            case DEPOSIT:
                wallet.getAmount().add(dto.getAmount());
                // dto.setAmount(wallet.getAmount());  в зависимости какой ответ нужен; возвращаем баланс кошелька?
                repository.save(wallet); // ^^ тоже тут
                break;
        }
        return mapper.toDtoOut(dto);
    }
}
