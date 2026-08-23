package dio.budgeting.application.output;

public record MonthlyTotalOutput(int year, int month, double totalAmount, int transactionCount) {
}
