package com.bank.customer.domain.entity;

public class Client extends Person {

    private static final int PASSWORD_MIN_LENGTH = 4;
    private static final int PASSWORD_MAX_LENGTH = 50;

    private Long clientId;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public void changePassword(String newPassword) {
        this.password = validatePassword(newPassword);
    }

    public Long getClientId() {
        return clientId;
    }

    public String getPassword() {
        return password;
    }

    protected void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected void setActiveState(boolean active) {
        this.active = active;
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
               "clientId=" + clientId +
               ", name='" + getName() + '\'' +
               ", identification='" + getIdentification() + '\'' +
               ", active=" + active +
               '}';
    }
}