package com.example.service_statistics.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.service_statistics.exception.DeleteTransactionException;
import com.example.service_statistics.exception.RecordNotFoundException;
import com.example.service_statistics.model.EmployeeEntity;
import com.example.service_statistics.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository repository;

    public List<EmployeeEntity> getAllEmployees() {
        List<EmployeeEntity> result = (List<EmployeeEntity>) repository.findAll();

        if (result.size() > 0) {
            return result;
        } else {
            return new ArrayList<>();
        }

    }

    public EmployeeEntity getEmployeeById(Long id) throws RecordNotFoundException {
        Optional<EmployeeEntity> employee = repository.findById(id);

        if (employee.isPresent()) {
            return employee.get();
        } else {
            throw new RecordNotFoundException("No employee record exist for given id");
        }

    }

    @Transactional
    public EmployeeEntity createOrUpdateEmployee(EmployeeEntity entity) {

        if (entity.getId()  == null) {
            entity = repository.save(entity);

            return entity;
        } else {

            Optional<EmployeeEntity> employee = repository.findById(entity.getId());

            if (employee.isPresent()) {

                EmployeeEntity newEntity = employee.get();
                newEntity.setAge(entity.getAge());
                newEntity.setName(entity.getName());
                newEntity.setLastName(entity.getLastName());

                newEntity = repository.save(newEntity);

                return newEntity;
            } else {
                entity = repository.save(entity);

                return entity;
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            rollbackFor = DeleteTransactionException.class)
    public void deleteEmployeeById(Long id) throws RecordNotFoundException {
        Optional<EmployeeEntity> employee = repository.findById(id);

        if (employee.isPresent()) {
            repository.deleteById(id);
        } else {
            throw new RecordNotFoundException("No employee record exist for given id");
        }
    }

}
