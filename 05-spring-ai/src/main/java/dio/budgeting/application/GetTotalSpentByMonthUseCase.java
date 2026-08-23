package dio.budgeting.application;

import dio.budgeting.application.output.MonthlyTotalOutput;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class GetTotalSpentByMonthUseCase {
    private final TransactionRepository transactionRepository;

    public GetTotalSpentByMonthUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-total-spent-by-month", description = "Calcula a soma do valor total de gastos financeiros realizados em um determinado mês e ano")
    public MonthlyTotalOutput execute(
            @ToolParam(description = "Mês da consulta (número inteiro de 1 a 12)") int month,
            @ToolParam(description = "Ano da consulta (exemplo: 2024, 2025, 2026)") int year) {
        var startDate = LocalDate.of(year, month, 1);
        var endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        var transactions = transactionRepository.findAllByDateBetween(startDate, endDate);
        long totalCents = transactions.stream().mapToLong(Transaction::getAmount).sum();
        double total = BigDecimal.valueOf(totalCents).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return new MonthlyTotalOutput(year, month, total, transactions.size());
    }
}
