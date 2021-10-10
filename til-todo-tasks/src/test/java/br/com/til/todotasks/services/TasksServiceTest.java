package br.com.til.todotasks.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.til.todotasks.adapters.TaskDTO;
import br.com.til.todotasks.entities.Tasks;
import br.com.til.todotasks.exceptions.ValidateException;
import br.com.til.todotasks.repositories.TasksRepository;
import br.com.til.todotasks.validations.TasksValidations;

@ExtendWith(SpringExtension.class)
public class TasksServiceTest {

	@InjectMocks
	private TasksService tasksService;
	
	@Mock
	private TasksRepository tasksRepositoryMock;
	
	@Mock
	private ModelMapper modelMapperMock;
	
	@Test
	public void savedTaskWithSuccess() throws ValidateException {
		TaskDTO dto = new TaskDTO("Test new Task", LocalDate.now().plusMonths(1));
		
		Tasks taskMock = createTasksMock(dto);
		
		BDDMockito.when(modelMapperMock.map(dto,Tasks.class))
			.thenReturn(taskMock);
		
		BDDMockito.when(tasksRepositoryMock.save(ArgumentMatchers.any()))
			.thenReturn(taskMock);
		
		dto.setId(taskMock.getId());
		
		BDDMockito.when(modelMapperMock.map(taskMock, TaskDTO.class))
		.thenReturn(dto);
		
		TaskDTO savedTask = tasksService.saveTask(dto);
		
		assertThat(savedTask.getId()).isNotNull();
		Mockito.verify(modelMapperMock, Mockito.times(2)).map(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verify(tasksRepositoryMock).save(Mockito.any(Tasks.class));
	}

	@Test
	public void savedTaskWithDescriptionBlankWillNotBeSaved() {
		
		try {
			TaskDTO dto = new TaskDTO("", LocalDate.now().plusMonths(1));
			
			tasksService.saveTask(dto);
			fail("Must fali");
			
		} catch (ValidateException e) {
			String message = e.getMessage();
			assertThat(message).isEqualTo(TasksValidations.TASK_DESCRIPTION_MUST_BE_FILL);
		}
		
	}
	
	@Test
	public void savedTaskWithDescriptionNullWillNotBeSaved() {
		
		try {
			TaskDTO dto = new TaskDTO(null, LocalDate.now().plusMonths(1));
			
			tasksService.saveTask(dto);
			fail("Must fali");
			
		} catch (ValidateException e) {
			String message = e.getMessage();
			assertThat(message).isEqualTo(TasksValidations.TASK_DESCRIPTION_MUST_BE_FILL);
		}
		
	}
	
	@Test
	public void savedTaskNullWillNotBeSaved() {
		
		try {
			
			tasksService.saveTask(null);
			fail("Must fali");
			
		} catch (ValidateException e) {
			String message = e.getMessage();
			assertThat(message).isEqualTo(TasksValidations.TASK_DETAILS_MUST_BE_FILL);
		}
		
	}
	
	@Test
	public void savedTaskWithPastDueDateWillNotBeSaved() {
		
		try {
			
			TaskDTO dto = new TaskDTO("Taks qwith past due date", LocalDate.now().minusMonths(1));
			
			tasksService.saveTask(dto);
			fail("Must fali");
			
		} catch (ValidateException e) {
			String message = e.getMessage();
			assertThat(message).isEqualTo(TasksValidations.TASK_DUE_DATE_MUST_BE_IN_THE_FUTURE);
		}
		
	}
	
	@Test
	public void findAllTaks() throws ValidateException {
		TaskDTO dto = new TaskDTO("Test new Task", LocalDate.now().plusMonths(1));
		
		Tasks taskMock = createTasksMock(dto);
		
		PageImpl<Tasks> listOfTasks = new PageImpl<>(List.of(taskMock));
		
		BDDMockito.when(tasksRepositoryMock.findAll(Pageable.ofSize(10)))
		.thenReturn(listOfTasks);
		
		BDDMockito.when(modelMapperMock.map(taskMock, TaskDTO.class))
		.thenReturn(dto);
		
		Page<TaskDTO> allTasks = tasksService.findAll(Pageable.ofSize(10));
		
		assertThat(allTasks.getTotalElements()).isGreaterThan(0);
		Mockito.verify(modelMapperMock, Mockito.times(1)).map(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.verify(tasksRepositoryMock).findAll(Pageable.ofSize(10));
		
	}
	
	@Test
	public void removeTaksWithSuccess() {
		
		TaskDTO dto = new TaskDTO("Test new Task", LocalDate.now().plusMonths(1));
		
		Tasks taskMock =createTasksMock(dto);
		
		BDDMockito.when(tasksRepositoryMock.findById(ArgumentMatchers.any()))
		.thenReturn(Optional.<Tasks>of(taskMock));
		
		tasksService.deleteTasksById(taskMock.getId());
		
		Mockito.verify(tasksRepositoryMock).deleteById(taskMock.getId());
			
	}
	
	@Test
	public void removeTaksWithTaksNotFoundError() {
		
		try {
			
			BDDMockito.when(tasksRepositoryMock.findById(ArgumentMatchers.any()))
				.thenReturn(Optional.<Tasks>empty());

			tasksService.deleteTasksById(1L);
			
			fail("Must fali");
			
		} catch (Exception e) {
			String message = e.getMessage();
			
			assertThat(message).isEqualTo(TasksService.TASK_NOT_FOUND);
		}
		
	}
	
	@Test
	public void findTaskByIdNotFoundError() {
		
		try {
			
			BDDMockito.when(tasksRepositoryMock.findById(ArgumentMatchers.any()))
			.thenReturn(Optional.<Tasks>empty());
			
			tasksService.findById(1L);
			
			fail("Must fali");
			
		} catch (Exception e) {
			String message = e.getMessage();
			
			assertThat(message).isEqualTo(TasksService.TASK_NOT_FOUND);
		}
		
	}
	
	@Test
	public void findTaksById() throws ValidateException {
			
		TaskDTO dto = new TaskDTO("Test new Task", LocalDate.now().plusMonths(1));
		
		Tasks taskMock =createTasksMock(dto);
		
		dto.setId(taskMock.getId());
		
		BDDMockito.when(tasksRepositoryMock.findById(ArgumentMatchers.any()))
		.thenReturn(Optional.<Tasks>of(taskMock));
		
		BDDMockito.when(modelMapperMock.map(taskMock, TaskDTO.class))
		.thenReturn(dto);
			
		TaskDTO taskFound = tasksService.findById(taskMock.getId());
		
		Mockito.verify(tasksRepositoryMock).findById(taskMock.getId());
		Mockito.verify(modelMapperMock, Mockito.times(1)).map(taskMock, TaskDTO.class);
		
		assertThat(taskFound.getId()).isEqualTo(taskMock.getId());
		
	}
	
	private Tasks createTasksMock(TaskDTO dto) {
		return  new Tasks(1L, dto.getDescription(), dto.getDueDate());
	}	
}
