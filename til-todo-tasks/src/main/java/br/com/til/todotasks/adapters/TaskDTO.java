package br.com.til.todotasks.adapters;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class TaskDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String description;
	
	private LocalDate dueDate;

	public TaskDTO() {
		super();
	}

	public TaskDTO(String description, LocalDate dueDate) {
		super();
		this.description = description;
		this.dueDate = dueDate;
	}
	
	public TaskDTO(Long id, String description, LocalDate dueDate) {
		super();
		this.id = id;
		this.description = description;
		this.dueDate = dueDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskDTO other = (TaskDTO) obj;
		return Objects.equals(id, other.id);
	}
	
}
