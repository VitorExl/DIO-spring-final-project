package dio.budgeting.infrastructure.http;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    @DisplayName("Deve tratar IllegalArgumentException retornando status 400")
    void shouldHandleIllegalArgumentException() {
        when(request.getRequestURI()).thenReturn("/transactions/ai");

        var ex = new IllegalArgumentException("O arquivo de áudio não pode estar vazio.");
        var response = exceptionHandler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("O arquivo de áudio não pode estar vazio.");
        assertThat(response.getBody().path()).isEqualTo("/transactions/ai");
    }

    @Test
    @DisplayName("Deve tratar MultipartException retornando status 400")
    void shouldHandleMultipartException() {
        when(request.getRequestURI()).thenReturn("/transactions/ai");

        var ex = new MultipartException("Current request is not a multipart request");
        var response = exceptionHandler.handleMultipartException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Falha ao processar o arquivo de áudio enviado.");
    }

    @Test
    @DisplayName("Deve tratar Exception genérica retornando status 500")
    void shouldHandleGeneralException() {
        when(request.getRequestURI()).thenReturn("/transactions");

        var ex = new RuntimeException("Erro inesperado");
        var response = exceptionHandler.handleGeneralException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
    }
}
