package br.com.til.todotasks.services;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.til.todotasks.adapters.TaskDTO;
import br.com.til.todotasks.entities.Tasks;
import br.com.til.todotasks.exceptions.ValidateException;
import br.com.til.todotasks.repositories.TasksRepository;
import br.com.til.todotasks.validations.TasksValidations;

@Service
public class TasksService {

	public static final String TASK_NOT_FOUND = "Task not found.";

	@Autowired
	private TasksRepository tasksRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	
	public TaskDTO saveTask(TaskDTO taskDTO) throws ValidateException {
		
		TasksValidations.validateTasks(taskDTO);
		
		Tasks tasks = mapper.map(taskDTO, Tasks.class);
		
		tasks = tasksRepository.save(tasks);
		
		return mapper.map(tasks, TaskDTO.class);
	}


	public Page<TaskDTO> findAll(Pageable pageable) {
		Page<Tasks> allTasks = this.tasksRepository.findAll(pageable);
		
		Page<TaskDTO> allTasksDTO = allTasks.map(task -> {
			TaskDTO dto = mapper.map(task, TaskDTO.class);
			return dto;
		});
		
		return allTasksDTO;
	}
	
	public void deleteTasksById(Long taskId) {
		
		Optional<Tasks> optionalTask = tasksRepository.findById(taskId);
		
		if (!optionalTask.isPresent()) {
			throw new ValidateException(TASK_NOT_FOUND);
		}
		
		this.tasksRepository.deleteById(taskId);
	}
	
	public TaskDTO findById(Long taskId) throws ValidateException {
		Optional<Tasks> optionalTask = tasksRepository.findById(taskId);
		
		if (!optionalTask.isPresent()) {
			throw new ValidateException(TASK_NOT_FOUND);
		}
		
		return mapper.map(optionalTask.get(), TaskDTO.class);
	}
}
