package ru.store.pharmacy.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.store.pharmacy.model.Medicines;
import ru.store.pharmacy.service.MedicinesService;
import ru.store.pharmacy.dto.MedicineDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/medicines")
public class MedicineApiController {

    private final MedicinesService medicinesService;

    @Autowired
    public MedicineApiController(MedicinesService medicinesService) {
        this.medicinesService = medicinesService;
    }

    // Получение всех препаратов
    @GetMapping
    @Cacheable("medicines")
    public ResponseEntity<List<Medicines>> getAll() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(medicinesService.getAll());
    }

    // Получение одного препарата
    @GetMapping("/{id}")
    @Cacheable(value = "medicines", key = "#id")
    public ResponseEntity<Medicines> getById(@PathVariable Long id) {
        Medicines medicine = medicinesService.getMedicineById(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .eTag(createETag(medicine))
                .body(medicine);
    }

    // Метод HEAD
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<?> headById(@PathVariable Long id) {
        Medicines medicine = medicinesService.getMedicineById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .eTag(createETag(medicine))
                .build();
    }

    // Метод OPTIONS
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                        HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.HEAD,
                        HttpMethod.OPTIONS)
                .build();
    }

    // Создание препарата
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "medicines", allEntries = true)
    public ResponseEntity<Medicines> create(@Valid @RequestBody MedicineDto dto) {
        Medicines medicine = convertToEntity(dto);
        Medicines savedMedicine = medicinesService.addMedicine(medicine);
        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .body(savedMedicine);
    }

    // Полное обновление
    @PutMapping("/{id}")
    @CacheEvict(value = "medicines", key = "#id")
    public ResponseEntity<Medicines> update(
            @PathVariable Long id,
            @Valid @RequestBody MedicineDto dto) {
        Medicines medicine = convertToEntity(dto);
        medicine.setId(id);
        Medicines updated = medicinesService.updateMedicine(id, medicine);
        return ResponseEntity.ok()
                .eTag(createETag(updated))
                .body(updated);
    }

    // Частичное обновление
    @PatchMapping("/{id}")
    @CacheEvict(value = "medicines", key = "#id")
    public ResponseEntity<Medicines> patch(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Medicines existing = medicinesService.getMedicineById(id);
        applyUpdates(existing, updates);
        Medicines updated = medicinesService.updateMedicine(id, existing);
        return ResponseEntity.ok()
                .eTag(createETag(updated))
                .body(updated);
    }

    // Удаление
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "medicines", key = "#id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicinesService.deleteMedicine(id);
        return ResponseEntity.noContent()
                .cacheControl(CacheControl.noCache())
                .build();
    }

    // Генерация ETag
    private String createETag(Medicines medicine) {
        return "\"" + medicine.hashCode() + "\"";
    }

    // Конвертер DTO -> Entity
    private Medicines convertToEntity(MedicineDto dto) {
        Medicines medicine = new Medicines();
        medicine.setName(dto.getName());
        medicine.setDescription(dto.getDescription());
        medicine.setCategory(dto.getCategory());
        medicine.setForm(dto.getForm());
        medicine.setWeight(dto.getWeight());
        medicine.setPrice(dto.getPrice());
        return medicine;
    }

    // Обработка частичных обновлений
    private void applyUpdates(Medicines medicine, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    medicine.setName((String) value);
                    break;
                case "description":
                    medicine.setDescription((String) value);
                    break;
                case "category":
                    medicine.setCategory(Medicines.Category.valueOf((String) value));
                    break;
                case "form":
                    medicine.setForm(Medicines.Form.valueOf((String) value));
                    break;
                case "weight":
                    medicine.setWeight((Integer) value);
                    break;
                case "price":
                    medicine.setPrice((Integer) value);
                    break;
            }
        });
    }
}