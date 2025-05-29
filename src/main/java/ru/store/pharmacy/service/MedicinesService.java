package ru.store.pharmacy.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.store.pharmacy.model.MedicineHistory;
import ru.store.pharmacy.model.Medicines;
import ru.store.pharmacy.repository.MedicineHistoryRepository;
import ru.store.pharmacy.repository.MedicinesRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

@Service
public class MedicinesService {
    private final MedicinesRepository medicinesRepository;
    private final MedicineHistoryRepository medicineHistoryRepository;

    @Autowired
    public MedicinesService(MedicinesRepository medicinesRepository,
                            MedicineHistoryRepository medicineHistoryRepository) {
        this.medicinesRepository = medicinesRepository;
        this.medicineHistoryRepository = medicineHistoryRepository;
    }

    // Основные методы работы с препаратами
    public List<Medicines> searchAndSort(String searchQuery, String sortBy, String direction) {
        return medicinesRepository.findAll(
                buildSearchSpecification(searchQuery),
                buildSort(sortBy, direction)
        );
    }

    public List<Medicines> getAllSorted(String sortBy, String direction) {
        return medicinesRepository.findAll(buildSort(sortBy, direction));
    }

    public List<Medicines> getAll() {
        return medicinesRepository.findAll();
    }

    public Medicines getMedicineById(Long id) {
        return medicinesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Препарат не найден"));
    }

    @Transactional
    public Medicines addMedicine(Medicines medicine) {
        validateMedicine(medicine);
        Medicines savedMedicine = medicinesRepository.save(medicine);
        createHistoryEntry(savedMedicine, MedicineHistory.ChangeType.ADDED);
        return savedMedicine;
    }

    @Transactional
    public Medicines updateMedicine(Long id, Medicines medicine) {
        Medicines existing = getMedicineById(id);
        existing.setName(medicine.getName());
        existing.setDescription(medicine.getDescription());
        existing.setCategory(medicine.getCategory());
        existing.setForm(medicine.getForm());
        existing.setPrice(medicine.getPrice());
        existing.setWeight(medicine.getWeight());
        validateMedicine(existing);
        createHistoryEntry(existing, MedicineHistory.ChangeType.UPDATED);
        return medicinesRepository.save(existing);
    }

    @Transactional
    public void deleteMedicine(Long id) {
        Medicines medicine = getMedicineById(id);

        // 1. Находим все записи истории для этого препарата
        List<MedicineHistory> medicineHistory = medicineHistoryRepository
                .findAllByMedicineId(medicine.getId());

        // 2. Отвязываем записи истории от препарата
        medicineHistory.forEach(entry -> entry.setMedicine(null));
        medicineHistoryRepository.saveAll(medicineHistory);

        // 3. Создаем запись об удалении
        MedicineHistory deletionRecord = new MedicineHistory();
        deletionRecord.setChangeType(MedicineHistory.ChangeType.DELETED);
        deletionRecord.setChangeTimestamp(LocalDateTime.now());
        deletionRecord.setMedicine(null);
        medicineHistoryRepository.save(deletionRecord);

        // 4. Удаляем сам препарат
        medicinesRepository.delete(medicine);
    }

    // Вспомогательные методы

    private Sort buildSort(String sortBy, String direction) {
        List<String> allowedFields = List.of("id", "name", "category", "form", "weight", "price");
        String validSortBy = allowedFields.contains(sortBy) ? sortBy : "id";

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(sortDirection, validSortBy);
    }

    private Specification<Medicines> buildSearchSpecification(String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return Specification.where(null);
        }

        String searchTerm = "%" + searchQuery.toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), searchTerm),
                cb.like(cb.lower(root.get("category")), searchTerm),
                cb.like(cb.lower(root.get("description")), searchTerm)
        );
    }

    private void createHistoryEntry(Medicines medicine, MedicineHistory.ChangeType type) {
        MedicineHistory history = new MedicineHistory();
        history.setMedicine(medicine);
        history.setChangeType(type);
        history.setChangeTimestamp(LocalDateTime.now());
        medicineHistoryRepository.save(history);
    }

    private void validateMedicine(Medicines medicine) {
        if (medicine.getPrice() <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
        if (medicine.getWeight() <= 0) {
            throw new IllegalArgumentException("Вес должен быть положительным");
        }
    }

    // Методы для статистики

    public NavigableMap<LocalDateTime, Long> getMedicineStatistics() {
        List<MedicineHistory> history = medicineHistoryRepository.findFullHistory();
        return processHistory(history);
    }

    private NavigableMap<LocalDateTime, Long> processHistory(List<MedicineHistory> history) {
        NavigableMap<LocalDateTime, Long> stats = new TreeMap<>();
        long currentCount = 0;

        for (MedicineHistory entry : history) {
            switch (entry.getChangeType()) {
                case ADDED -> currentCount++;
                case UPDATED -> {
                } // Не влияет на количество
                case DELETED -> currentCount--;
            }
            stats.put(entry.getChangeTimestamp(), currentCount);
        }

        return stats;
    }
}