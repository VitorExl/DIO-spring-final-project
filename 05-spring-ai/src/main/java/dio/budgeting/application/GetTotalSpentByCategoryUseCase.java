package dio.budgeting.application;

import dio.budgeting.application.output.CategoryTotalOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class GetTotalSpentByCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public GetTotalSpentByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-total-spent-by-category", description = "Calcula a soma do valor total gasto em uma determinada categoria financeira")
    public CategoryTotalOutput execute(@ToolParam(description = "Categoria de uma transação") Category category) {
        var transactions = transactionRepository.findAllByCategory(category);
        long totalCents = transactions.stream().mapToLong(Transaction::getAmount).sum();
        double total = BigDecimal.valueOf(totalCents).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return new CategoryTotalOutput(category.name(), total, transactions.size());
    }
}
