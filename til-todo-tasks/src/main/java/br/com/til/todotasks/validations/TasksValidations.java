package br.com.til.todotasks.validations;

import java.time.LocalDate;

import br.com.til.todotasks.adapters.TaskDTO;
import br.com.til.todotasks.exceptions.ValidateException;

public class TasksValidations {


	public static final String TASK_DESCRIPTION_MUST_BE_FILL = "Task description must be fill.";

	public static final String TASK_DETAILS_MUST_BE_FILL = "Task details must be fill.";

	public static final String TASK_DUE_DATE_MUST_BE_IN_THE_FUTURE = "Task due date must be in the future.";

	public static void validateTasks(TaskDTO taskDTO) throws ValidateException {
		
		if(taskDTO == null) {
			
			throw new ValidateException(TASK_DETAILS_MUST_BE_FILL);
		}
		
		if(taskDTO.getDescription() == null || taskDTO.getDescription().isBlank()) {
			throw new ValidateException(TASK_DESCRIPTION_MUST_BE_FILL);
		}
		
		if(LocalDate.now().isAfter(taskDTO.getDueDate())) {
			throw new ValidateException(TASK_DUE_DATE_MUST_BE_IN_THE_FUTURE);
		}
	}
}
