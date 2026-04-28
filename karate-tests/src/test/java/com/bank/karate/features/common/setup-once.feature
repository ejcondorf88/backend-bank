@ignore
Feature: Setup Once - Se ejecuta UNA VEZ por Feature File
  Este feature se llama con callonce para crear datos compartidos
  entre todos los scenarios del mismo feature file.

Background:
  * url customerServiceUrl
  * headers { 'Content-Type': 'application/json', 'Accept': 'application/json' }

Scenario: Crear cliente compartido para el feature
  # Generar cliente base
  * def timestamp = new Date().getTime()
  * def identification = 'SHARED' + timestamp
  
  * def requestBody =
    """
    {
      name: 'Shared Feature Client',
      gender: 'Masculino',
      age: 30,
      identification: '#(identification)',
      address: 'Shared Address',
      phone: '0999000000',
      password: 'sharedpass123',
      active: true
    }
    """
  
  Given path '/api/clients'
  And request requestBody
  When method post
  Then status 201
  
  * def clientId = response.id
  * def clientData = response
  * def sharedIdentification = response.identification
  
  * print 'Cliente compartido creado - ID:', clientId

# El feature devuelve automaticamente las variables definidas
