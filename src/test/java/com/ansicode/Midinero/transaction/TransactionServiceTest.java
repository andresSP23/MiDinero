package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.category.Category;
import com.ansicode.Midinero.category.CategoryRepository;
import com.ansicode.Midinero.commom.PageResponse;
import com.ansicode.Midinero.enums.TransactionType;
import com.ansicode.Midinero.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private TransactionRequest request;
    private Category category;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@test.com").build();
        request = TransactionRequest.builder()
                .description("Test Transaction")
                .total(new BigDecimal("100.00"))
                .transactionType(TransactionType.EXPENSE)
                .categoryId(1L)
                .build();
        category = Category.builder().id(1L).name("Test Category").build();
        transaction = Transaction.builder().id(1L).description("Test Transaction").user(user).category(category)
                .build();
    }

    @Test
    void shouldCreateTransaction_WhenValidRequest() {
        // Given
        when(authentication.getPrincipal()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(eq(1L), eq(1L))).thenReturn(Optional.of(category));
        when(transactionMapper.toTransaction(any(), any(), any())).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toTransactionResponse(any(Transaction.class)))
                .thenReturn(TransactionResponse.builder().id(1L).build());

        // When
        TransactionResponse response = transactionService.createTransaction(request, authentication);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldFindAllTransactions_WhenTransactionsExist() {
        // Given
        when(authentication.getPrincipal()).thenReturn(user);
        Page<Transaction> page = new PageImpl<>(Collections.singletonList(transaction));
        when(transactionRepository.findAllByUserId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(transactionMapper.toTransactionResponse(any(Transaction.class)))
                .thenReturn(TransactionResponse.builder().id(1L).build());

        // When
        PageResponse<TransactionResponse> response = transactionService.findAllTransactionsByUser(0, 10, null,
                authentication);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(transactionRepository, times(1)).findAllByUserId(eq(1L), any(Pageable.class));
    }
}
