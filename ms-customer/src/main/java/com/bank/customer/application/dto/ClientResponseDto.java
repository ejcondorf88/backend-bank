package com.bank.customer.application.dto;

public class ClientResponseDto {

    private Long id;
    private Long clientId;
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
    private Boolean active;

    public ClientResponseDto() {
    }

    public ClientResponseDto(Long id, Long clientId, String name, String gender,
                             Integer age, String identification, String address,
                             String phone, Boolean active) {
        this.id = id;
        this.clientId = clientId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.identification = identification;
        this.address = address;
        this.phone = phone;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getIdentification() { return identification; }
    public void setIdentification(String identification) { this.identification = identification; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "ClientResponseDto{" +
               "id=" + id +
               ", clientId=" + clientId +
               ", name='" + name + '\'' +
               ", identification='" + identification + '\'' +
               ", active=" + active +
               '}';
    }
}