@ignore
Feature: Movement Setup Once - Crear movimiento base para reutilizar
Este feature se ejecuta UNA SOLA VEZ por suite de tests de movements (callonce).
Crea una cuenta y un movimiento base que se reutilizan en todos los tests.

Background:
* url accountServiceUrl
* headers jsonHeaders

Scenario: Crear cuenta y movimiento base para tests
# Primero crear cuenta base
* def timestamp = new Date().getTime()
* def baseAccountData =
"""
{
    accountNumber: 'MOVBASE' + timestamp,
    accountType: 'Ahorro',
    initialBalance: 5000.00,
    active: true,
    clientId: 1
}
"""

# Crear cuenta
Given path '/api/accounts'
And request baseAccountData
When method post
Then status 201

# Guardar datos de la cuenta
* def accountId = response.id
* def accountNumber = response.accountNumber

# Crear movimiento de deposito
Given path '/api/movements/' + accountNumber + '/deposit'
And request 1000.00
When method post
Then status 201

# Guardar datos del movimiento
* def movementId = response.id
* def movementData =
"""
{
    id: movementId,
    accountNumber: accountNumber,
    type: 'Deposito',
    amount: 1000.00
}
"""

* print 'Movimiento base creado - ID:', movementId, 'Cuenta:', accountNumber

# Devolver datos para uso en otros features
* def callonceResult = { movementId: movementId, accountNumber: accountNumber, movementData: movementData }
