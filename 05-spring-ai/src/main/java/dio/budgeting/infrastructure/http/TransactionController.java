package dio.budgeting.infrastructure.http;

import dio.budgeting.application.GetTotalSpentByCategoryUseCase;
import dio.budgeting.application.GetTotalSpentByMonthUseCase;
import dio.budgeting.application.ListTransactionsByCategoryUseCase;
import dio.budgeting.application.PersistTransactionUseCase;
import dio.budgeting.application.output.CategoryTotalOutput;
import dio.budgeting.application.output.MonthlyTotalOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.infrastructure.http.request.TransactionRequest;
import dio.budgeting.infrastructure.http.response.TransactionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;
    private final GetTotalSpentByCategoryUseCase getTotalSpentByCategoryUseCase;
    private final GetTotalSpentByMonthUseCase getTotalSpentByMonthUseCase;

    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 GetTotalSpentByCategoryUseCase getTotalSpentByCategoryUseCase,
                                 GetTotalSpentByMonthUseCase getTotalSpentByMonthUseCase,
                                 TranscriptionModel transcriptionModel,
                                 @Value("classpath:prompts/system-message.st") Resource systemPrompt,
                                 ChatClient.Builder chatClientBuilder,
                                 TextToSpeechModel textToSpeechModel) throws IOException {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.getTotalSpentByCategoryUseCase = getTotalSpentByCategoryUseCase;
        this.getTotalSpentByMonthUseCase = getTotalSpentByMonthUseCase;
        this.transcriptionModel = transcriptionModel;
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(
                        persistTransactionUseCase,
                        listTransactionsByCategoryUseCase,
                        getTotalSpentByCategoryUseCase,
                        getTotalSpentByMonthUseCase
                )
                .build();
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request) {
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    public List<TransactionResponse> readTransactions(@PathVariable Category category) {
        return listTransactionsByCategoryUseCase.execute(category).stream().map(TransactionResponse::from).toList();
    }

    @GetMapping("/total/category/{category}")
    public CategoryTotalOutput getTotalByCategory(@PathVariable Category category) {
        return getTotalSpentByCategoryUseCase.execute(category);
    }

    @GetMapping("/total/month")
    public MonthlyTotalOutput getTotalByMonth(@RequestParam int month, @RequestParam int year) {
        return getTotalSpentByMonthUseCase.execute(month, year);
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo de áudio enviado para transcrição não pode estar vazio.");
        }

        var userMessage = transcriptionModel.transcribe(file.getResource());
        var result = chatClient.prompt().user(userMessage).call().content();

        byte[] audio = textToSpeechModel.call(result);
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mp3"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("audio.mp3")
                                .build()
                                .toString())
                .body(resource);
    }
}
