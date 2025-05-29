package ru.store.pharmacy.dto;


import ru.store.pharmacy.model.Medicines;

public class MedicineDto {
    private String name;
    private String description;
    private Medicines.Category category;
    private Medicines.Form form;
    private int weight;
    private int price;

    // Конструктор по умолчанию
    public MedicineDto() {
    }

    // Геттеры и сеттеры
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

    public Medicines.Category getCategory() {
        return category;
    }

    public void setCategory(Medicines.Category category) {
        this.category = category;
    }

    public Medicines.Form getForm() {
        return form;
    }

    public void setForm(Medicines.Form form) {
        this.form = form;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}