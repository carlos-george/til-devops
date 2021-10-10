package br.com.til.todotasks.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import br.com.til.todotasks.adapters.TaskDTO;
import br.com.til.todotasks.controllers.exception.model.ErroModel;
import br.com.til.todotasks.entities.Tasks;
import br.com.til.todotasks.repositories.TasksRepository;
import br.com.til.todotasks.services.TasksService;
import br.com.til.todotasks.validations.TasksValidations;
import br.com.til.todotasks.wrapper.PageableResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TasksControllerIT {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private TasksRepository tasksRepository;
	
	@Test
	@DisplayName("List returns list of tasks inside page object when successful.")
	public void returnsListOfTsksInsidePageObjectWhenSuccess() {
		
		Tasks savedTask = tasksRepository.save(new Tasks("Tasks - Integrate Test", LocalDate.now().plusMonths(1)));
		
		String expectedTaskDescription = savedTask.getDescription();
		
		PageableResponse<Tasks> tasksPage = testRestTemplate.exchange("/tasks", HttpMethod.GET, null, 
				new ParameterizedTypeReference<PageableResponse<Tasks>>() {
		}).getBody();
		
		assertThat(tasksPage).isNotNull();
		
		assertThat(tasksPage.toList())
			.isNotNull()
			.hasSize(1);
		
		assertThat(tasksPage.getTotalElements()).isGreaterThan(0);
		
		assertThat(tasksPage.toList().get(0).getDescription()).isEqualTo(expectedTaskDescription);
	}
	
	@Test
	@DisplayName("Save task when successful.")
	public void savedTaskWithSuccess() {
		
		TaskDTO taskDTO = new TaskDTO("Tasks - Integrate Test", LocalDate.now().plusMonths(1));
		
		String expectedTaskDescription = taskDTO.getDescription();
		
		ResponseEntity<TaskDTO> responseEntity = testRestTemplate.postForEntity("/tasks", taskDTO, TaskDTO.class);
		
		assertThat(responseEntity).isNotNull();
		
		assertThat(responseEntity.getBody()).isNotNull();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		assertThat(responseEntity.getBody().getDescription()).isEqualTo(expectedTaskDescription);
		
		assertThat(responseEntity.getBody().getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Save task with dua date in the past when unsuccessful.")
	public void saveTaskWithDueDateInPastUnsuccess() {
		
		TaskDTO taskDTO = new TaskDTO("Tasks - Integrate Test", LocalDate.now().minusMonths(1));
		
		ResponseEntity<ErroModel> responseEntity = testRestTemplate.postForEntity("/tasks", taskDTO, ErroModel.class);
		
		assertThat(responseEntity).isNotNull();
		
		assertThat(responseEntity.getBody()).isNotNull();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		
		assertThat(responseEntity.getBody().getErrorMessage()).isNotNull();
		
		assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo(TasksValidations.TASK_DUE_DATE_MUST_BE_IN_THE_FUTURE);
	}
	
	@Test
	@DisplayName("Save task with empty description when unsuccessful.")
	public void saveTaskWithEmptyDescriptionUnsuccess() {
		
		TaskDTO taskDTO = new TaskDTO("", LocalDate.now().plusMonths(1));
		
		ResponseEntity<ErroModel> responseEntity = testRestTemplate.postForEntity("/tasks", taskDTO, ErroModel.class);
		
		assertThat(responseEntity).isNotNull();
		
		assertThat(responseEntity.getBody()).isNotNull();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		
		assertThat(responseEntity.getBody().getErrorMessage()).isNotNull();
		
		assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo(TasksValidations.TASK_DESCRIPTION_MUST_BE_FILL);
	}
	
	@Test
	@DisplayName("Remove task when successful.")
	public void removeTaskWithSuccess() {
		
		Tasks savedTask = tasksRepository.save(new Tasks("Tasks - Integrate Test", LocalDate.now().plusMonths(1)));
		
		Optional<Tasks> taskFound = tasksRepository.findById(savedTask.getId());
		
		assertTrue(taskFound.isPresent());
		
		testRestTemplate.delete("/tasks/"+savedTask.getId());
		
		Optional<Tasks> taskNotFound = tasksRepository.findById(savedTask.getId());
		
		assertFalse(taskNotFound.isPresent());
	}
	
	@Test
	@DisplayName("Remove task when unsuccessful.")
	public void removeTaskWithUnuccess() {
		
		int idRamdom = (int) Math.random() * 2;
		
		ResponseEntity<ErroModel> responseEntity = testRestTemplate.exchange("/tasks/"+idRamdom, 
				HttpMethod.DELETE, 
				null,
				ErroModel.class);
		
		assertThat(responseEntity).isNotNull();
		
		assertThat(responseEntity.getBody()).isNotNull();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		
		assertThat(responseEntity.getBody().getErrorMessage()).isNotNull();
		
		assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo(TasksService.TASK_NOT_FOUND);
	}
	
	@Test
	@DisplayName("Find task by id when successful.")
	public void findTaskByIdWithSuccess() {
		
		Tasks savedTask = tasksRepository.save(new Tasks("Tasks - Integrate Test", LocalDate.now().plusMonths(1)));
		
		ResponseEntity<TaskDTO> responseEntity = testRestTemplate.getForEntity("/tasks/"+savedTask.getId(), TaskDTO.class);
		
		assertThat(responseEntity).isNotNull();
		
		assertThat(responseEntity.getBody()).isNotNull();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertThat(responseEntity.getBody().getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Find task by id when unsuccessful.")
	public void findTaskByIdWithUnsuccess() {
		
		int idRamdom = (int) Math.random() * 2;
		
		ResponseEntity<ErroModel> responseEntity = testRestTemplate.getForEntity("/tasks/"+idRamdom, ErroModel.class);
		
		assertThat(responseEntity).isNotNull();
		
		assertThat(responseEntity.getBody()).isNotNull();
		
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		
		assertThat(responseEntity.getBody().getErrorMessage()).isNotNull();
		
		assertThat(responseEntity.getBody().getErrorMessage()).isEqualTo(TasksService.TASK_NOT_FOUND);
	}
	
}
