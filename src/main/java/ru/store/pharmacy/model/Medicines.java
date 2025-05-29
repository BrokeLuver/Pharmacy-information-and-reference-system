package ru.store.pharmacy.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicines")
public class Medicines {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "category")
    private Category category;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "form")
    private Form form;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int weight;
    //начало изменений
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.PERSIST)
    private List<MedicineHistory> history = new ArrayList<>();

    public Medicines() {
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    //конец изменений

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public enum Category {
        Антибиотик,
        Антисептик,
        Противовирусное,
        Обезболивающее,
        Слабительное,
        Снотворное,
        Антидепрессант,
        Транквилизатор,
        Инсулин,
        Контрацептив,
        Противоаллергическое,
        Витамины,
        БАД
    }

    public enum Form {
        Таблетки,
        Мазь,
        Сироп,
        Капли,
        Пластырь,
        Аэрозоль,
        Леденцы
    }
}