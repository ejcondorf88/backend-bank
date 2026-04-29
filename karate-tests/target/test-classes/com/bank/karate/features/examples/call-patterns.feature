@examples
Feature: Ejemplos de Call, CallOnce y CallSingle
  Demuestra patrones de reutilizacion de codigo en Karate.

Background:
  # CallOnce: Se ejecuta UNA VEZ por feature (cacheado para todos los scenarios)
  * def shared = callonce read('classpath:com/bank/karate/features/common/setup-once.feature')
  * print 'Shared ID from callonce:', shared.clientId

# =====================================================================
# CALL - Se ejecuta en CADA scenario (no cacheado)
# =====================================================================

Scenario: Usar call para crear datos temporales
  # Call: Se ejecuta cada vez que se invoca
  * def tempClient = call read('classpath:com/bank/karate/features/common/create-inactive-client.feature')
  
  * print 'Cliente temporal - ID:', tempClient.id
  
  # Usar el cliente creado
  Given url customerServiceUrl + '/api/clients/' + tempClient.id
  When method get
  Then status 200
  And match response.id == tempClient.id

Scenario: Call con parametros
  # Llamar con parametros personalizados
  * def customClient = 
    """
    {
      name: 'Custom Client',
      gender: 'Femenino',
      age: 25,
      identification: 'CUSTOM' + new Date().getTime(),
      address: 'Custom Address',
      phone: '0999888777',
      password: 'custom123',
      active: true
    }
    """
  
  # Crear usando el endpoint directamente
  Given url customerServiceUrl + '/api/clients'
  And request customClient
  When method post
  Then status 201
  
  * def customId = response.id
  * print 'Cliente custom creado - ID:', customId

# =====================================================================
# CALLONCE - Se ejecuta UNA VEZ y se reutiliza
# =====================================================================

Scenario: Reusar datos del callonce
  # Los datos del callonce en el Background estan disponibles aqui
  * print 'Reusando shared ID:', shared.clientId
  
  Given url customerServiceUrl + '/api/clients/' + shared.clientId
  When method get
  Then status 200
  
  # Wildcard para validar estructura
  And match response == { id: '#number', name: '#string', identification: '#string', "#ignore": "##" }

Scenario: Otro scenario reusa el mismo callonce
  # Mismo shared.clientId que el scenario anterior
  * print 'Shared ID sigue siendo:', shared.clientId
  
  Given url customerServiceUrl + '/api/clients/' + shared.clientId
  When method get
  Then status 200

# =====================================================================
# CALLSINGLE - Se ejecuta UNA VEZ por toda la suite (en karate-config.js)
# =====================================================================

Scenario: Acceder a datos globales de callsingle
  # Los datos de callsingle estan en la variable 'testData'
  * print 'Datos globales desde callsingle:', testData
  
  # Usar el generador de clientes global
  * def newClient = testData.generateClient({ name: 'From Global' })
  * print 'Cliente generado globalmente:', newClient
  
  Given url customerServiceUrl + '/api/clients'
  And request newClient
  When method post
  Then status 201
  And match response.name == 'From Global'

# =====================================================================
# WILDCARDS - Validacion flexible
# =====================================================================

Scenario: Wildcards en schemas
  Given url customerServiceUrl + '/api/clients/' + shared.clientId
  When method get
  Then status 200
  
  # ## - Campo opcional (puede ser null o no existir)
  And match response == { id: '#number', name: '#string', gender: '##string', age: '#number', "#ignore": "##" }
  
  # #? _ - Validacion con expresion (el valor debe cumplir la condicion)
  And match response.name == '#? _.length > 0'
  And match response.age == '#? _ >= 18'
  
  # #[] - Array que puede ser vacio
  And match [] == '#[]'
  And match response == '#? _ != null'

Scenario: Wildcards parciales con contains
  Given url customerServiceUrl + '/api/clients/' + shared.clientId
  When method get
  Then status 200
  
  # Contains: solo valida que tenga estos campos (puede tener mas)
  And match response contains { id: shared.clientId, name: '#string', active: true }
  
  # Contains deep: para objetos anidados
  And match response contains { name: '#string', identification: '#string' }

Scenario: Wildcards en arrays
  Given url customerServiceUrl + '/api/clients'
  When method get
  Then status 200
  
  # Array de cualquier tamaño con schema
  And match response == '#[] { id: "#number", name: "#string" }'
  
  # Validar que al menos tiene un item
  * if (response.length == 0) karate.fail('Lista vacia')
  
  # Each: cada item cumple el criterio
  And match each response contains { id: '#number', name: '#string' }
