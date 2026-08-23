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
class GetTotalSpentByMonthUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private GetTotalSpentByMonthUseCase useCase;

    @Test
    @DisplayName("Deve calcular corretamente a soma dos gastos em um determinado mês e ano")
    void shouldCalculateTotalSpentByMonth() {
        var start = LocalDate.of(2026, 5, 1);
        var end = LocalDate.of(2026, 5, 31);

        var t1 = new Transaction("Gasolina", 10000L, Category.AUTO, LocalDate.of(2026, 5, 10));
        var t2 = new Transaction("Remédio", 5500L, Category.PHARMA, LocalDate.of(2026, 5, 20));

        when(transactionRepository.findAllByDateBetween(start, end))
                .thenReturn(List.of(t1, t2));

        var output = useCase.execute(5, 2026);

        assertThat(output.year()).isEqualTo(2026);
        assertThat(output.month()).isEqualTo(5);
        assertThat(output.totalAmount()).isEqualTo(15500.00);
        assertThat(output.transactionCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver transações no mês")
    void shouldReturnZeroWhenNoTransactionsInMonth() {
        var start = LocalDate.of(2026, 1, 1);
        var end = LocalDate.of(2026, 1, 31);

        when(transactionRepository.findAllByDateBetween(start, end))
                .thenReturn(List.of());

        var output = useCase.execute(1, 2026);

        assertThat(output.year()).isEqualTo(2026);
        assertThat(output.month()).isEqualTo(1);
        assertThat(output.totalAmount()).isEqualTo(0.00);
        assertThat(output.transactionCount()).isZero();
    }
}
