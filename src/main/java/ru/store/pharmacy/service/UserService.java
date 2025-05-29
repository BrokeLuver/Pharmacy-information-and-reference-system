package ru.store.pharmacy.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.store.pharmacy.model.UserHistory;
import ru.store.pharmacy.model.User;
import ru.store.pharmacy.repository.UserHistoryRepository;
import ru.store.pharmacy.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserHistoryRepository userHistoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new IllegalStateException("Невозможно удалить текущий аккаунт");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        // 1. Отвязываем историю
        List<UserHistory> userHistory = userHistoryRepository.findAllByUserId(user.getId());
        userHistory.forEach(entry -> entry.setUser(null));
        userHistoryRepository.saveAll(userHistory);

        // 2. Создаем запись об удалении
        UserHistory deletionRecord = new UserHistory();
        deletionRecord.setChangeType(UserHistory.ChangeType.DELETED);
        deletionRecord.setChangeTimestamp(LocalDateTime.now());
        deletionRecord.setUser(null);
        userHistoryRepository.save(deletionRecord);

        // 3. Удаляем пользователя
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"));
    }

    public void toggleAdminRole(Long userId, Long currentUserId) {
        if (userId.equals(currentUserId)) {
            throw new IllegalStateException("Нельзя изменить собственную роль администратора");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        String newRole = user.getRole().equals("ROLE_ADMIN") ? "ROLE_USER" : "ROLE_ADMIN";
        user.setRole(newRole);
        createHistoryEntry(user, UserHistory.ChangeType.UPDATED);
        userRepository.save(user);
    }

    // статистические методы

    public NavigableMap<LocalDateTime, Long> getUserStatistics() {
        List<UserHistory> history = userHistoryRepository.findAllOrderedByTimestamp();
        return processUserHistory(history);
    }

    private NavigableMap<LocalDateTime, Long> processUserHistory(List<UserHistory> history) {
        NavigableMap<LocalDateTime, Long> stats = new TreeMap<>();
        long currentCount = 0;

        for (UserHistory entry : history) {
            switch (entry.getChangeType()) {
                case ADDED -> currentCount++;
                case UPDATED -> {} // Не влияет на количество
                case DELETED -> currentCount--;
            }
            stats.put(entry.getChangeTimestamp(), currentCount);
        }

        return stats;
    }

    private void createHistoryEntry(User user, UserHistory.ChangeType type) {
        UserHistory history = new UserHistory();
        history.setUser(user);
        history.setChangeType(type);
        history.setChangeTimestamp(LocalDateTime.now());
        userHistoryRepository.save(history);
    }

    // Метод для создания записи при добавлении пользователя
    @Transactional
    public void createUserWithHistory(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        createHistoryEntry(savedUser, UserHistory.ChangeType.ADDED);
    }
}
