@ignore
Feature: Create Inactive Client - Helper para crear cliente inactivo
  Crea un cliente inactivo y devuelve su ID.

Background:
  * url customerServiceUrl
  * headers { 'Content-Type': 'application/json', 'Accept': 'application/json' }

Scenario: Crear cliente inactivo
  * def timestamp = new Date().getTime()
  * def identification = 'INACTIVE' + timestamp
  
  * def requestBody =
    """
    {
      name: 'Inactive Test Client',
      gender: 'Masculino',
      age: 30,
      identification: '#(identification)',
      address: 'Test Address',
      phone: '0999000000',
      password: 'testpass123',
      active: false
    }
    """
  
  Given path '/api/clients'
  And request requestBody
  When method post
  Then status 201
  
  * def id = response.id
  * def identification = response.identification
