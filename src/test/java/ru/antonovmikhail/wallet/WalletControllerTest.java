package ru.antonovmikhail.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import ru.antonovmikhail.wallet.controller.WalletController;
import ru.antonovmikhail.wallet.model.dto.NewTransaction;
import ru.antonovmikhail.wallet.model.dto.TransactionDto;
import ru.antonovmikhail.wallet.service.WalletService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.antonovmikhail.util.Constants.DATE_TIME_PATTERN;

@WebMvcTest(controllers = WalletController.class)
class WalletControllerTest {

    @MockBean
    private WalletService service;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final RateLimiter rateLimiter = RateLimiter.create(1000);

    private final String walletPath = "/api/v1";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private final UUID generatedId = UUID.randomUUID();
    private final NewTransaction dtoIn = NewTransaction.builder()
            .id(generatedId)
            .amount(BigDecimal.valueOf(100))
            .operation(NewTransaction.OperationType.WITHDRAW)
            .updateTime(LocalDateTime.of(2024, 4, 5, 17, 1, 1))
            .build();
    private final TransactionDto dtoOut = TransactionDto.builder()
            .id(generatedId)
            .amount(BigDecimal.valueOf(100))
            .operation(NewTransaction.OperationType.WITHDRAW)
            .updateTime(LocalDateTime.of(2024, 4, 5, 17, 1, 1))
            .build();

    @SneakyThrows
    @Test
    void getWallet_whenUUIDCorrect_returnDtoOutAndOk() {
        when(service.getWallet(generatedId))
                .thenReturn(dtoOut);
        mvc.perform(get(walletPath + "/wallets/{WALLET_UUID}", generatedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(dtoOut.getId().toString())))
                .andExpect(jsonPath("$.amount", is(dtoOut.getAmount().intValue())))
                .andExpect(jsonPath("$.updateTime", is(dtoOut.getUpdateTime().format(formatter))));
        verify(service, times(1))
                .getWallet(any(UUID.class));
    }

    @SneakyThrows
    @Test
    void get_whenUUIDNull_returnBadRequest() {
        when(service.getWallet(null))
                .thenReturn(dtoOut);
        mvc.perform(get(walletPath + "/wallets/{WALLET_UUID}", " "))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MissingPathVariableException));
        verify(service, never())
                .saveNewTransaction(any(NewTransaction.class));
    }

    @SneakyThrows
    @Test
    void post_whenDtoInCorrect_returnDtoOutAndCreate() {
        when(service.saveNewTransaction(dtoIn))
                .thenReturn(dtoOut);
        mvc.perform(post(walletPath + "/wallet")
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid", is(dtoOut.getId().toString())))
                .andExpect(jsonPath("$.amount", is(dtoOut.getAmount().intValue())))
                .andExpect(jsonPath("$.operation", is(dtoOut.getOperation().toString())))
                .andExpect(jsonPath("$.updateTime", is(dtoOut.getUpdateTime().format(formatter))));
        verify(service, times(1))
                .saveNewTransaction(any(NewTransaction.class));
    }

    @SneakyThrows
    @Test
    void post_whenUUIDNull_returnBadRequest() {
        dtoIn.setId(null);
        when(service.saveNewTransaction(dtoIn))
                .thenReturn(dtoOut);
        mvc.perform(post(walletPath + "/wallet")
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(service, never())
                .saveNewTransaction(any(NewTransaction.class));
    }

    @SneakyThrows
    @Test
    void post_whenAmountNull_returnBadRequest() {
        dtoIn.setAmount(null);
        when(service.saveNewTransaction(dtoIn))
                .thenReturn(dtoOut);
        mvc.perform(post(walletPath + "/wallet")
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(service, never())
                .saveNewTransaction(any(NewTransaction.class));
    }

    @SneakyThrows
    @Test
    void post_whenAmountNegative_returnBadRequest() {
        dtoIn.setAmount(BigDecimal.valueOf(-1));
        when(service.saveNewTransaction(dtoIn))
                .thenReturn(dtoOut);
        mvc.perform(post(walletPath + "/wallet")
                        .content(mapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));
        verify(service, never())
                .saveNewTransaction(any(NewTransaction.class));
    }
}