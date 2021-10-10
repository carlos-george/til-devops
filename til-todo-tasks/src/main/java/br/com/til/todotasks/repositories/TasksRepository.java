package br.com.til.todotasks.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.com.til.todotasks.entities.Tasks;

public interface TasksRepository extends PagingAndSortingRepository<Tasks, Long> {

}
