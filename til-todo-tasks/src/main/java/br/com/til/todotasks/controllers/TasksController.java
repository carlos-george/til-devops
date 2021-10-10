package br.com.til.todotasks.controllers;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.til.todotasks.adapters.TaskDTO;
import br.com.til.todotasks.exceptions.ValidateException;
import br.com.til.todotasks.services.TasksService;

@RestController
@RequestMapping("/tasks")
public class TasksController {

	@Autowired
	private TasksService tasksService;
	
	@GetMapping
	public ResponseEntity<Page<TaskDTO>> findAll(Pageable pageable) {
		return ResponseEntity.ok(tasksService.findAll(pageable));
	}
	
	@PostMapping
	public ResponseEntity<TaskDTO> saveTask(@RequestBody TaskDTO taskDTO) throws URISyntaxException, ValidateException {
		
		TaskDTO savedTask = tasksService.saveTask(taskDTO);
		
		return ResponseEntity.created(new URI("/tasks/" + savedTask.getId())).body(savedTask);
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTask(@PathVariable Long id) throws ValidateException  {
		
		tasksService.deleteTasksById(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TaskDTO> findTaskById(@PathVariable Long id) throws ValidateException {
		
		return ResponseEntity.ok(tasksService.findById(id));
	}
}
