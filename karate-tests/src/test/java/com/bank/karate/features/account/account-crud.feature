@account @crud
Feature: Account Service - Account CRUD Operations
Pruebas de API para operaciones CRUD del microservicio de cuentas bancarias.
Demuestra uso de callonce, callsingle y wildcards.

# =====================================================================
# BACKGROUND CON CALLONCE
# Se ejecuta UNA SOLA VEZ al inicio del feature
# =====================================================================

Background:
# Cargar utilidades comunes para accounts (una sola vez)
* callonce read('classpath:com/bank/karate/features/account/account-setup.feature')

# Datos compartidos del callonce
* def baseAccount = callonceSetup.accountData
* def sharedAccountNumber = callonceSetup.accountNumber

# Configuracion base
* url accountServiceUrl
* path '/api/accounts'
* headers jsonHeaders

# =====================================================================
# SCENARIOS DE CREACION - USANDO WILDCARDS
# =====================================================================

@smoke @create @positive
Scenario: Crear una cuenta exitosamente con validacion flexible
# Generar cuenta de prueba usando wildcard ## para merge con overrides
* def testAccount = generateAccount({ accountType: 'Ahorro', initialBalance: 1000.00 })

Given request testAccount
When method post
Then status 201

# Validacion con wildcard - campos opcionales marcados con ##
And match response == accountSchema

# Validacion de campos especificos usando wildcards parciales
And match response contains { accountNumber: '#(testAccount.accountNumber)', accountType: 'Ahorro', active: true }
And match response.balance == testAccount.initialBalance

# Validacion flexible - solo campos requeridos
And match response contains { id: '#number', accountNumber: '#string', accountType: '#string', balance: '#number', active: '#boolean' }

# Wildcard #ignore para ignorar campos adicionales
And match response contains { accountNumber: '#string', "#ignore": "##" }

# Guardar numero de cuenta para cleanup
* def createdAccountNumber = response.accountNumber

# Cleanup con callonce al final de TODO el feature
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: createdAccountNumber });
}
"""

@create @negative
Scenario: Intentar crear cuenta con datos invalidos - wildcard en error
* def invalidData = { accountNumber: '', accountType: 'Invalido', initialBalance: -100 }

Given request invalidData
When method post
Then status 400

# Validacion flexible de error con wildcards
And match response contains { status: 400, error: '#string', message: '#? _.length > 0' }

# Wildcard para campos que pueden variar
And match response contains { timestamp: '#? _ != null' }

@create @negative
Scenario: Intentar crear cuenta duplicada
# Usar la cuenta compartida del callonce (ya existe)
Given request baseAccount
When method post
Then status 409

# Wildcard para mensaje que contiene cierto texto
And match response.message contains "already exists"

@create @negative
Scenario: Intentar crear cuenta con tipo invalido
* def invalidTypeAccount = generateAccount({ accountType: 'InvalidType' })

Given request invalidTypeAccount
When method post
Then status 400

# Wildcard para validar mensaje de error
And match response.message contains '#string'

# =====================================================================
# SCENARIOS DE CONSULTA - USANDO DATOS COMPARTIDOS
# =====================================================================

@smoke @read @positive
Scenario: Obtener cuenta por numero usando datos compartidos
# Usar el numero de cuenta del callonce
Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber
When method get
Then status 200

# Wildcard para validar estructura sin todos los campos
And match response == accountSchema

# Validacion parcial con contains
And match response contains { accountNumber: sharedAccountNumber, accountType: baseAccount.accountType }

@read @positive
Scenario: Listar todas las cuentas
When method get
Then status 200

# Wildcard para array - puede ser vacio o tener items
And match response == '#[] accountSchema'

# Verificar que la cuenta compartida esta en la lista
And match response[*].accountNumber contains sharedAccountNumber

@read @positive
Scenario: Listar cuentas por cliente
* def testClientId = baseAccount.clientId

Given url accountServiceUrl + '/api/accounts/client/' + testClientId
When method get
Then status 200

# Wildcard: array de cuentas
And match response == '#[] accountSchema'

# Todas las cuentas deben pertenecer al cliente
And match each response[*].clientId == testClientId

@read @positive
Scenario: Listar cuentas activas
Given url accountServiceUrl + '/api/accounts/active'
When method get
Then status 200

# Wildcard: array de cuentas donde todas tienen active=true
And match response == '#[] accountSchema'
And match each response[*].active == true

@read @negative
Scenario: Intentar obtener cuenta inexistente - wildcard en status
Given url accountServiceUrl + '/api/accounts/999999999'
When method get

# Wildcard para rango de status codes
Then status '#number? _ >= 400 && _ < 500'

# Validacion con contains
And match response contains { status: '#number', message: '#? _.toLowerCase().contains("not found")' }

# =====================================================================
# SCENARIOS DE ACTUALIZACION - CALL Y WILDCARDS
# =====================================================================

@smoke @update @positive
Scenario: Actualizar cuenta con merge de campos (wildcard)
# Usar cuenta compartida
* def updateData = { accountType: 'Corriente', active: false }

Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber
And request updateData
When method put
Then status 200

# Wildcard: solo validamos los campos que cambiaron
And match response contains { accountType: 'Corriente', active: false }

# Los demas campos siguen igual
And match response.accountNumber == sharedAccountNumber

@update @positive
Scenario: Activar cuenta inactiva
# Primero desactivar la cuenta
Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber + '/deactivate'
When method patch
Then status 200
And match response.active == false

# Luego activar
Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber + '/activate'
When method patch
Then status 200
And match response.active == true

@update @negative
Scenario: Intentar activar cuenta ya activa - wildcard en status range
Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber + '/activate'
When method patch

# Status 400 con wildcard para cualquier 4xx
Then status '#number? _ == 400'

And match response.message contains 'already active'

@update @negative
Scenario: Intentar desactivar cuenta ya inactiva
# Primero asegurarnos que esta inactiva
Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber + '/deactivate'
When method patch

# Intentar desactivar de nuevo
Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber + '/deactivate'
When method patch
Then status 400

And match response.message contains 'already inactive'

# =====================================================================
# SCENARIOS DE TRANSACCIONES - DEPOSITOS Y RETIROS
# =====================================================================

@smoke @transaction @deposit @positive
Scenario: Realizar deposito exitoso
# Crear cuenta nueva para este test
* def testAccount = generateAccount({ initialBalance: 100.00 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def createdNumber = testAccount.accountNumber

# Realizar deposito
Given url accountServiceUrl + '/api/accounts/' + createdNumber + '/deposit'
And request { accountNumber: createdNumber, amount: 500.00 }
When method post
Then status 200

# Verificar saldo actualizado
And match response.balance == 600.00

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: createdNumber });
}
"""

@smoke @transaction @withdraw @positive
Scenario: Realizar retiro exitoso
# Crear cuenta con saldo suficiente
* def testAccount = generateAccount({ initialBalance: 1000.00 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def createdNumber = testAccount.accountNumber

# Realizar retiro
Given url accountServiceUrl + '/api/accounts/' + createdNumber + '/withdraw'
And request { accountNumber: createdNumber, amount: 300.00 }
When method post
Then status 200

# Verificar saldo actualizado
And match response.balance == 700.00

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: createdNumber });
}
"""

@transaction @withdraw @negative
Scenario: Intentar retiro sin saldo suficiente
# Crear cuenta con saldo bajo
* def testAccount = generateAccount({ initialBalance: 100.00 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def createdNumber = testAccount.accountNumber

# Intentar retirar mas del saldo
Given url accountServiceUrl + '/api/accounts/' + createdNumber + '/withdraw'
And request { accountNumber: createdNumber, amount: 500.00 }
When method post
Then status 400

# Wildcard para mensaje de error
And match response.message contains '#string'

# Verificar que el saldo no cambio
Given url accountServiceUrl + '/api/accounts/' + createdNumber
When method get
Then status 200
And match response.balance == 100.00

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: createdNumber });
}
"""

@transaction @negative
Scenario: Intentar deposito en cuenta inactiva
# Crear cuenta inactiva
* def testAccount = generateAccount({ active: false })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def createdNumber = testAccount.accountNumber

# Desactivar la cuenta
Given url accountServiceUrl + '/api/accounts/' + createdNumber + '/deactivate'
When method patch

# Intentar depositar
Given url accountServiceUrl + '/api/accounts/' + createdNumber + '/deposit'
And request { accountNumber: createdNumber, amount: 100.00 }
When method post
Then status 400

And match response.message contains 'inactive'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: createdNumber });
}
"""

# =====================================================================
# SCENARIOS DE ELIMINACION
# =====================================================================

@smoke @delete @positive
Scenario: Eliminar cuenta inactiva
# Crear cuenta para eliminar
* def testAccount = generateAccount({ active: false })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def createdNumber = testAccount.accountNumber

# Eliminar cuenta
Given url accountServiceUrl + '/api/accounts/' + createdNumber
When method delete
Then status 204

# Verificar que ya no existe
Given url accountServiceUrl + '/api/accounts/' + createdNumber
When method get
Then status 404

@delete @negative
Scenario: Intentar eliminar cuenta activa
Given url accountServiceUrl + '/api/accounts/' + sharedAccountNumber
When method delete
Then status 400

# Wildcard para mensaje que contiene texto
And match response.message contains 'Cannot delete'

@delete @negative
Scenario: Intentar eliminar cuenta inexistente
Given url accountServiceUrl + '/api/accounts/999999999'
When method delete
Then status 404
