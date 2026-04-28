package com.bank.customer.domain.entity;

import com.bank.customer.domain.exception.InvalidClientStateException;

public class Client extends Person {

    private static final int PASSWORD_MIN_LENGTH = 4;
    private static final int PASSWORD_MAX_LENGTH = 50;

    private String password;
    private boolean active;

    protected Client() {
        super();
    }

    public Client(String name, String gender, Integer age,
                  String identification, String address, String phone,
                  String password, boolean active) {
        super(name, gender, age, identification, address, phone);
        this.password = validatePassword(password);
        this.active = active;
    }

    private String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        String trimmedPassword = password.trim();

        if (trimmedPassword.length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalArgumentException(
                "Password must be at least " + PASSWORD_MIN_LENGTH + " characters long");
        }

        if (trimmedPassword.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Password cannot exceed " + PASSWORD_MAX_LENGTH + " characters");
        }

        return trimmedPassword;
    }

    public boolean isActive() {
        return this.active;
    }

    public void activate() {
        if (this.active) {
            throw new InvalidClientStateException("Client is already active");
        }
        this.active = true;
    }

    public void deactivate() {
        if (!this.active) {
            throw new InvalidClientStateException("Client is already inactive");
        }
        this.active = false;
    }

    public void setPassword(String password) {
        this.password = validatePassword(password);
    }

    public String getPassword() {
        return password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return getIdentification() != null &&
               getIdentification().equals(client.getIdentification());
    }

    @Override
    public int hashCode() {
        return getIdentification() != null ? getIdentification().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", identification='" + getIdentification() + '\'' +
                ", active=" + active +
                '}';
    }
}