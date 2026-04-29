@customer @crud
Feature: Customer Service - Client CRUD Operations
  Pruebas de API para operaciones CRUD del microservicio de clientes.
  Demuestra uso de callonce, callsingle y wildcards.

# =====================================================================
# BACKGROUND CON CALLONCE
# Se ejecuta UNA SOLA VEZ al inicio del feature, no antes de cada scenario
# =====================================================================

Background:
  # Cargar utilidades comunes (una sola vez)
  * callonce read('classpath:com/bank/karate/features/common/setup.feature')
  
  # Datos compartidos del callonce (disponibles en todos los scenarios)
  * def baseClient = callonceSetup.clientData
  * def sharedId = callonceSetup.clientId
  
  # Configuracion base
  * url customerServiceUrl
  * path '/api/clients'
  * headers jsonHeaders

# =====================================================================
# SCENARIOS DE CREACIÓN - USANDO WILDCARDS
# =====================================================================

@smoke @create @positive
Scenario: Crear un cliente exitosamente con validacion flexible
  # Usar wildcard ## para merge con overrides
  * def testClient = generateClient({ age: 25, active: true })
  
  Given request testClient
  When method post
  Then status 201
  
  # Validacion con wildcard - campos opcionales marcados con ##
  And match response == clientSchema
  
  # Validacion de campos especificos usando wildcards parciales
  And match response contains { name: '#(testClient.name)', active: true }
  And match response.identification == testClient.identification
  
  # Validacion flexible - solo campos requeridos
  And match response contains { id: '#number', name: '#string', active: '#boolean' }
  
  # Wildcard #ignore para ignorar campos adicionales
  And match response contains { name: '#string', "#ignore": "##" }
  
  # Guardar ID para cleanup
  * def createdId = response.id
  
  # Cleanup con callonce al final de TODO el feature
  * configure afterFeature = 
    """
    function() {
      karate.call('classpath:com/bank/karate/features/common/cleanup.feature', 
        { clientId: createdId });
    }
    """

@create @negative
Scenario: Intentar crear cliente con datos invalidos - wildcard en error
  * def invalidData = { name: '', identification: '', password: '123' }
  
  Given request invalidData
  When method post
  Then status 400
  
  # Validacion flexible de error con wildcards
  And match response contains { status: 400, error: '#string', message: '#? _.length > 0' }
  
  # Wildcard para campos que pueden variar
  And match response contains { timestamp: '#? _ != null', path: '#string' }

@create @negative
Scenario: Intentar crear cliente duplicado
  # Usar el cliente compartido del callonce (ya existe)
  Given request baseClient
  When method post
  Then status 409
  
  # Wildcard para mensaje que contiene cierto texto
  And match response.message contains "already exists"

# =====================================================================
# SCENARIOS DE CONSULTA - USANDO DATOS COMPARTIDOS
# =====================================================================

@smoke @read @positive
Scenario: Obtener cliente por ID usando datos compartidos
  # Usar el ID del callonce - no necesitamos crear uno nuevo
  Given url customerServiceUrl + '/api/clients/' + sharedId
  When method get
  Then status 200
  
  # Wildcard para validar estructura sin todos los campos
  And match response == clientSchema
  
  # Validacion parcial con contains
  And match response contains { id: sharedId, identification: baseClient.identification }

@read @positive
Scenario: Obtener cliente por identificacion
  Given url customerServiceUrl + '/api/clients/identification/' + sharedIdentification
  When method get
  Then status 200
  
  # Wildcard ## para campos opcionales
  And match response == { id: '#number', name: '#string', identification: sharedIdentification, gender: '##string', age: '#number', address: '##string', phone: '##string', active: '#boolean' }

@read @negative
Scenario: Intentar obtener cliente inexistente - wildcard en status
  Given url customerServiceUrl + '/api/clients/999999'
  When method get
  
  # Wildcard para rango de status codes
  Then status '#number? _ >= 400 && _ < 500'
  
  # Validacion con contains (no match exacto)
  And match response contains { status: '#number', message: '#? _.toLowerCase().contains("not found")' }

@read @positive
Scenario: Listar todos los clientes - wildcard en array
  When method get
  Then status 200
  
  # Wildcard para array - puede ser vacio o tener items
  And match response == '#[] clientSchema'
  
  # Verificar que el cliente compartido esta en la lista
  And match response[*].id contains sharedId

@read @positive  
Scenario: Listar clientes activos
  Given url customerServiceUrl + '/api/clients/active'
  When method get
  Then status 200
  
  # Wildcard: array de clientes donde todos tienen active=true
  And match response == '#[] clientSchema'
  And match each response[*].active == true
  
  # Verificar estructura minima con contains
  And match response[*] contains { id: '#number', active: true }

# =====================================================================
# SCENARIOS DE ACTUALIZACIÓN - CALL Y WILDCARDS
# =====================================================================

@smoke @update @positive
Scenario: Actualizar cliente con merge de campos (wildcard)
  # Usar cliente compartido
  * def updateData = { name: 'Updated Name', age: 35 }
  
  Given url customerServiceUrl + '/api/clients/' + sharedId
  And request updateData
  When method put
  Then status 200
  
  # Wildcard: solo validamos los campos que cambiaron
  And match response contains { name: 'Updated Name', age: 35 }
  
  # Los demas campos siguen igual (wildcard ## los ignora en match == )
  And match response.identification == baseClient.identification

@update @positive
Scenario: Actualizar con campos parciales
  # Solo actualizar el nombre
  Given url customerServiceUrl + '/api/clients/' + sharedId
  And request { name: 'Partial Update' }
  When method put
  Then status 200
  
  # Wildcard: validar solo el campo cambiado
  And match response.name == 'Partial Update'
  
  # Campos no cambiados siguen presentes
  And match response contains { id: sharedId, identification: '#string', active: '#boolean' }

# =====================================================================
# SCENARIOS DE ELIMINACIÓN
# =====================================================================

@smoke @delete @positive
Scenario: Eliminar cliente inactivo - usar wildcard para cleanup
  # Crear cliente inactivo (usando call para aislar)
  * def inactiveClient = call read('classpath:com/bank/karate/features/common/create-inactive-client.feature')
  
  Given url customerServiceUrl + '/api/clients/' + inactiveClient.id
  When method delete
  Then status 204
  
  # Verificar que ya no existe
  Given url customerServiceUrl + '/api/clients/' + inactiveClient.id
  When method get
  Then status 404

@delete @negative
Scenario: Intentar eliminar cliente activo
  Given url customerServiceUrl + '/api/clients/' + sharedId
  When method delete
  Then status 400
  
  # Wildcard para mensaje que contiene texto
  And match response.message contains 'Cannot delete active client'

# =====================================================================
# SCENARIOS DE ACTIVACIÓN/DESACTIVACIÓN
# =====================================================================

@smoke @deactivate @positive
Scenario: Desactivar cliente activo
  Given url customerServiceUrl + '/api/clients/' + sharedId + '/deactivate'
  When method patch
  Then status 200
  And match response.active == false
  
  # Cleanup al final: el shared client queda inactivo para poder eliminarlo
  * configure afterFeature = 
    """
    function() {
      karate.call('classpath:com/bank/karate/features/common/cleanup.feature', 
        { clientId: sharedId });
    }
    """

@smoke @activate @positive
Scenario: Activar cliente inactivo
  # Primero desactivar (si esta activo)
  Given url customerServiceUrl + '/api/clients/' + sharedId + '/deactivate'
  When method patch
  
  # Luego activar
  Given url customerServiceUrl + '/api/clients/' + sharedId + '/activate'
  When method patch
  Then status 200
  And match response.active == true

@activate @negative
Scenario: Intentar activar cliente ya activo - wildcard en status range
  # El shared client esta activo (creado asi)
  Given url customerServiceUrl + '/api/clients/' + sharedId + '/activate'
  When method patch
  
  # Status 400 con wildcard para cualquier 4xx
  Then status '#number? _ == 400'
  
  And match response.message contains 'already active'
