package ru.antonovmikhail.wallet.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.antonovmikhail.wallet.model.dto.NewTransaction;
import ru.antonovmikhail.wallet.model.dto.TransactionDto;
import ru.antonovmikhail.wallet.model.Wallet;

@Mapper
public interface WalletMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TransactionDto toDtoOut(NewTransaction dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TransactionDto walletToDto(Wallet wallet);

}
