package ru.store.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.store.pharmacy.model.Medicines;

import java.util.List;

public interface MedicinesRepository extends JpaRepository<Medicines, Long>, JpaSpecificationExecutor<Medicines> {

    List<Medicines> findByName(String medicineName);

    List<Medicines> findByCategory(Medicines.Category category);
}
