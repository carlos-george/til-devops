package br.com.til.todotasks.controllers.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.til.todotasks.controllers.exception.model.ErrorModelBuild;
import br.com.til.todotasks.exceptions.ValidateException;

@ControllerAdvice
public class RestControllerException extends ResponseEntityExceptionHandler{

	
	@ExceptionHandler(value = { ValidateException.class})
    protected ResponseEntity<Object> handleConflict(
      RuntimeException ex, WebRequest request) {
		ErrorModelBuild errorModelBuilder = 
        		new ErrorModelBuild();
		
		errorModelBuilder.reset();
		errorModelBuilder.withDateTime(LocalDateTime.now())
        		.withErrorMessage(ex.getMessage())
        		.withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        		.withError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        		.withStackTrace(ex);
        
        return handleExceptionInternal(ex, errorModelBuilder.getErroModel(), 
          new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
	
}
