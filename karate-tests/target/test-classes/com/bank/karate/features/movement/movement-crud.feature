@movement @crud
Feature: Movement Service - Movement CRUD Operations
Pruebas de API para operaciones CRUD del microservicio de movimientos.
Implementa F2: Registro de movimientos con actualizacion de saldo.

# =====================================================================
# BACKGROUND CON CALLONCE
# Se ejecuta UNA SOLA VEZ al inicio del feature
# =====================================================================

Background:
# Cargar utilidades comunes para movements (una sola vez)
* callonce read('classpath:com/bank/karate/features/movement/movement-setup.feature')

# Datos compartidos del callonce
* def baseMovement = callonceSetup.movementData
* def sharedAccountNumber = callonceSetup.accountNumber

# Configuracion base
* url accountServiceUrl
* path '/api/movements'
* headers jsonHeaders

# =====================================================================
# SCENARIOS DE CREACION - F2: REGISTRO DE MOVIMIENTOS
# =====================================================================

@smoke @create @deposit @positive
Scenario: F2: Crear un deposito exitosamente
# Primero crear una cuenta para el deposito
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

# Realizar deposito
Given path '/api/movements/' + accountNumber + '/deposit'
And request 600.00
When method post
Then status 201

# Validar estructura del movimiento
And match response == movementSchema
And match response contains { accountNumber: accountNumber, type: 'Deposito', amount: 600.00 }
And match response.balance == '#? _ >= 600.00'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@smoke @create @withdrawal @positive
Scenario: F2: Crear un retiro exitosamente
# Primero crear cuenta con saldo
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

# Realizar deposito primero
Given path '/api/movements/' + accountNumber + '/deposit'
And request 1000.00
When method post

# Realizar retiro
Given path '/api/movements/' + accountNumber + '/withdraw'
And request 575.00
When method post
Then status 201

# Validar estructura
And match response == movementSchema
And match response contains { accountNumber: accountNumber, type: 'Retiro' }
And match response.amount == '#? _ < 0'
And match response.balance == '#? _ == 425.00'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@create @deposit @positive
Scenario: F2: Deposito actualiza saldo correctamente
# Crear cuenta con saldo inicial 100
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

# Realizar deposito de 600
Given path '/api/movements/' + accountNumber + '/deposit'
And request 600.00
When method post
Then status 201

# El saldo debe ser 700 (100 inicial + 600)
And match response.balance == 700.00

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@create @withdrawal @positive
Scenario: F2: Retiro actualiza saldo correctamente
# Crear cuenta con saldo 2000
* def testAccount = generateAccount({ initialBalance: 2000.00 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def accountNumber = testAccount.accountNumber

# Realizar retiro de 575
Given path '/api/movements/' + accountNumber + '/withdraw'
And request 575.00
When method post
Then status 201

# El saldo debe ser 1425 (2000 - 575)
And match response.balance == 1425.00

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@create @withdrawal @positive
Scenario: F2: Retiro que deja saldo en cero
# Crear cuenta con saldo 540
* def testAccount = generateAccount({ initialBalance: 540.00 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def accountNumber = testAccount.accountNumber

# Retirar todo
Given path '/api/movements/' + accountNumber + '/withdraw'
And request 540.00
When method post
Then status 201

# Saldo debe ser 0
And match response.balance == 0.00

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@create @negative
Scenario: F2: Crear movimiento en cuenta inexistente
Given path '/api/movements/999999/deposit'
And request 100.00
When method post
Then status 404
And match response contains { status: 404, message: '#string' }

@create @deposit @negative
Scenario: F2: Crear deposito en cuenta inactiva
# Crear cuenta inactiva
* def inactiveAccount = generateAccount({ active: false })
* def result = call read('classpath:com/bank/karate/features/account/account-create-helper.feature') inactiveAccount
* def accountNumber = result.accountNumber

# Intentar depositar
Given path '/api/movements/' + accountNumber + '/deposit'
And request 100.00
When method post
Then status 400
And match response.message contains 'inactive'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

# =====================================================================
# SCENARIOS F3: SALDO NO DISPONIBLE
# =====================================================================

@f3 @withdrawal @negative
Scenario: F3: Retiro rechazado por saldo insuficiente
# Crear cuenta con saldo 0
* def testAccount = generateAccount({ initialBalance: 0.00 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def accountNumber = testAccount.accountNumber

# Intentar retirar 100
Given path '/api/movements/' + accountNumber + '/withdraw'
And request 100.00
When method post
Then status 400
And match response.message == 'Saldo no disponible'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@f3 @withdrawal @negative
Scenario: F3: Retiro rechazado cuando monto excede saldo
# Crear cuenta con saldo 2000
* def testAccount = generateAccount({ initialBalance: 2000.00 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def accountNumber = testAccount.accountNumber

# Intentar retirar 2500
Given path '/api/movements/' + accountNumber + '/withdraw'
And request 2500.00
When method post
Then status 400
And match response.message == 'Saldo no disponible'

# Verificar que el saldo no cambio
Given url accountServiceUrl + '/api/accounts/' + accountNumber
When method get
Then status 200
And match response.balance == 2000.00

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@f3 @withdrawal @negative
Scenario: F3: Retiro rechazado en cuenta inactiva
# Crear cuenta inactiva
* def inactiveAccount = generateAccount({ active: false })
* def result = call read('classpath:com/bank/karate/features/account/account-create-helper.feature') inactiveAccount
* def accountNumber = result.accountNumber

# Intentar retirar
Given path '/api/movements/' + accountNumber + '/withdraw'
And request 100.00
When method post
Then status 400
And match response.message contains 'inactive'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

# =====================================================================
# SCENARIOS DE CONSULTA - F2/F4
# =====================================================================

@smoke @read @positive
Scenario: F2: Obtener movimiento por ID
# Crear movimiento
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request 600.00
When method post
Then status 201
* def movementId = response.id

# Obtener por ID
Given url accountServiceUrl + '/api/movements/' + movementId
When method get
Then status 200
And match response == movementSchema
And match response.id == movementId

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@read @negative
Scenario: F2: Intentar obtener movimiento inexistente
Given url accountServiceUrl + '/api/movements/999999'
When method get
Then status 404
And match response contains { status: 404, message: '#string' }

@smoke @read @positive
Scenario: F2: Listar todos los movimientos
When method get
Then status 200
And match response == '#[] movementSchema'

@smoke @read @positive
Scenario: F2: Listar movimientos de una cuenta
# Crear movimientos
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request 100.00
When method post

Given path '/api/movements/' + accountNumber + '/deposit'
And request 200.00
When method post

# Listar movimientos de la cuenta
Given url accountServiceUrl + '/api/movements/account/' + accountNumber
When method get
Then status 200
And match response == '#[] movementSchema'
And match response[*].accountNumber contains accountNumber

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@read @positive
Scenario: F2: Cuenta sin movimientos retorna lista vacia
Given url accountServiceUrl + '/api/movements/account/999999999'
When method get
Then status 200
And match response == '#[]'

@read @positive
Scenario: F2: Contar movimientos de cuenta
# Crear movimientos
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request 100.00
When method post

# Contar
Given url accountServiceUrl + '/api/movements/count/' + accountNumber
When method get
Then status 200
And match response == '#number'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

# =====================================================================
# SCENARIOS F4: REPORTES POR RANGO DE FECHAS
# =====================================================================

@smoke @f4 @read @positive
Scenario: F4: Reporte de movimientos por rango de fechas
# Crear movimiento
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request 500.00
When method post

# Consultar por rango de fechas (hoy)
* def today = new Date().toISOString().split('T')[0]
Given url accountServiceUrl + '/api/movements/report'
And param fechaInicio = today
And param fechaFin = today
When method get
Then status 200
And match response == '#[] movementSchema'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@smoke @f4 @read @positive
Scenario: F4: Reporte de movimientos por cuenta y rango de fechas
# Crear movimiento
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request 500.00
When method post

# Consultar reporte
* def today = new Date().toISOString().split('T')[0]
Given url accountServiceUrl + '/api/movements/report/' + accountNumber
And param fechaInicio = today
And param fechaFin = today
When method get
Then status 200
And match response == '#[] movementSchema'
And match response[*].accountNumber contains accountNumber

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@f4 @read @positive
Scenario: F4: Reporte por cliente
# Crear cuenta y movimiento para cliente 1
* def testAccount = generateAccount({ clientId: 1 })
* call read('classpath:com/bank/karate/features/account/account-create-helper.feature') testAccount
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request 300.00
When method post

# Consultar movimientos del cliente
Given url accountServiceUrl + '/api/movements/client/1'
When method get
Then status 200
And match response == '#[] movementSchema'

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

# =====================================================================
# SCENARIOS DE ELIMINACION
# =====================================================================

@smoke @delete @positive
Scenario: Eliminar movimiento
# Crear movimiento
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request 100.00
When method post
Then status 201
* def movementId = response.id

# Eliminar
Given url accountServiceUrl + '/api/movements/' + movementId
When method delete
Then status 204

# Verificar que ya no existe
Given url accountServiceUrl + '/api/movements/' + movementId
When method get
Then status 404

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@delete @negative
Scenario: Eliminar movimiento inexistente
Given url accountServiceUrl + '/api/movements/999999'
When method delete
Then status 404

# =====================================================================
# SCENARIOS DE VALIDACION
# =====================================================================

@validation @negative
Scenario: Validar monto negativo en deposito
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/deposit'
And request -100.00
When method post
Then status 400

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""

@validation @negative
Scenario: Validar monto cero en retiro
* def testAccount = call read('classpath:com/bank/karate/features/account/account-create-helper.feature')
* def accountNumber = testAccount.accountNumber

Given path '/api/movements/' + accountNumber + '/withdraw'
And request 0.00
When method post
Then status 400

# Cleanup
* configure afterFeature =
"""
function() {
    karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });
}
"""
