package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTotalSpentByCategoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GetTotalSpentByCategoryUseCase useCase;

    @Test
    @DisplayName("Deve calcular corretamente a soma dos gastos de uma categoria")
    void shouldCalculateTotalSpentByCategory() {
        var t1 = new Transaction("Supermercado", 15050L, Category.GROCERIES, LocalDate.now());
        var t2 = new Transaction("Feira", 4950L, Category.GROCERIES, LocalDate.now());

        when(transactionRepository.findAllByCategory(Category.GROCERIES))
                .thenReturn(List.of(t1, t2));

        var output = useCase.execute(Category.GROCERIES);

        assertThat(output.category()).isEqualTo("GROCERIES");
        assertThat(output.totalAmount()).isEqualTo(20000.00);
        assertThat(output.transactionCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver transações na categoria")
    void shouldReturnZeroWhenNoTransactionsInCategory() {
        when(transactionRepository.findAllByCategory(Category.PHARMA))
                .thenReturn(List.of());

        var output = useCase.execute(Category.PHARMA);

        assertThat(output.category()).isEqualTo("PHARMA");
        assertThat(output.totalAmount()).isEqualTo(0.00);
        assertThat(output.transactionCount()).isZero();
    }
}
