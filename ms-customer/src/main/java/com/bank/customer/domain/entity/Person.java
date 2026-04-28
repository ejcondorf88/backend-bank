package com.bank.customer.domain.entity;

import java.util.regex.Pattern;

public class Person {

    private static final int MINIMUM_AGE = 18;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$");

    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;

    protected Person() {
    }

    public Person(String name, String gender, Integer age,
                  String identification, String address, String phone) {
        this.name = validateName(name);
        this.gender = gender;
        this.age = validateAge(age);
        this.identification = identification;
        this.address = address;
        this.phone = phone;
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        String normalized = name.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
        if (!NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Name can only contain letters");
        }
        return normalized;
    }

    private Integer validateAge(Integer age) {
        if (age == null) {
            throw new IllegalArgumentException("Age is required");
        }
        if (age < MINIMUM_AGE) {
            throw new IllegalArgumentException("Must be at least " + MINIMUM_AGE + " years old");
        }
        return age;
    }

    public boolean isAdult() {
        return this.age >= MINIMUM_AGE;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getIdentification() { return identification; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    public void setId(Long id) { this.id = id; }
    protected void setName(String name) { this.name = name; }
    protected void setGender(String gender) { this.gender = gender; }
    protected void setAge(Integer age) { this.age = age; }
    protected void setIdentification(String identification) { this.identification = identification; }
    protected void setAddress(String address) { this.address = address; }
    protected void setPhone(String phone) { this.phone = phone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return identification != null && identification.equals(person.identification);
    }

    @Override
    public int hashCode() {
        return identification != null ? identification.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", identification='" + identification + '\'' +
                '}';
    }

}
