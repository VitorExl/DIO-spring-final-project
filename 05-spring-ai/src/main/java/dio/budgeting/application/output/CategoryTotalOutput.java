package dio.budgeting.application.output;

public record CategoryTotalOutput(String category, double totalAmount, int transactionCount) {
}
