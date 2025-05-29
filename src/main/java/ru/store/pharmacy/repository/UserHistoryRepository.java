package ru.store.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.store.pharmacy.model.UserHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    @Query("SELECT h FROM UserHistory h WHERE h.changeTimestamp >= :cutoff ORDER BY h.changeTimestamp DESC")
    List<UserHistory> findLast24Hours(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT h FROM UserHistory h WHERE h.user.id = :userId")
    List<UserHistory> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT h FROM UserHistory h ORDER BY h.changeTimestamp ASC")
    List<UserHistory> findAllOrderedByTimestamp();

    List<UserHistory> findAllByOrderByChangeTimestampAsc();

    default List<UserHistory> findLast24Hours() {
        return findLast24Hours(LocalDateTime.now().minusHours(24));
    }
}
