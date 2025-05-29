package ru.store.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.store.pharmacy.model.MedicineHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicineHistoryRepository extends JpaRepository<MedicineHistory, Long> {

    // Находим записи без привязки к препарату (удаленные)
    @Query("SELECT h FROM MedicineHistory h WHERE h.medicine IS NULL")
    List<MedicineHistory> findOrphanedRecords();

    // Найти все записи по ID препарата
    @Query("SELECT h FROM MedicineHistory h WHERE h.medicine.id = :medicineId")
    List<MedicineHistory> findAllByMedicineId(@Param("medicineId") Long medicineId);

    // Полная история с сортировкой
    @Query("SELECT h FROM MedicineHistory h ORDER BY h.changeTimestamp ASC")
    List<MedicineHistory> findFullHistory();

    // История за период
    @Query("SELECT h FROM MedicineHistory h WHERE h.changeTimestamp BETWEEN :start AND :end")
    List<MedicineHistory> findHistoryByPeriod(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);
}
