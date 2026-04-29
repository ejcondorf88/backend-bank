@ignore
Feature: Account Setup Once - Crear cuenta base para reutilizar
Este feature se ejecuta UNA SOLA VEZ por suite de tests de accounts (callonce).
Crea una cuenta base que se reutiliza en todos los tests.

Background:
* url accountServiceUrl
* headers jsonHeaders

Scenario: Crear cuenta base para tests
# Generar datos de cuenta unicos
* def timestamp = new Date().getTime()
* def baseAccountData =
"""
{
    accountNumber: 'BASE' + timestamp,
    accountType: 'Ahorro',
    initialBalance: 5000.00,
    active: true,
    clientId: 1
}
"""

# Crear cuenta base
Given path '/api/accounts'
And request baseAccountData
When method post
Then status 201

# Guardar datos para reutilizar
* def accountId = response.id
* def accountNumber = response.accountNumber
* def accountData = baseAccountData
* def accountData.id = accountId

* print 'Cuenta base creada - ID:', accountId, 'Numero:', accountNumber

# Devolver datos para uso en otros features
* def callonceResult = { accountId: accountId, accountNumber: accountNumber, accountData: accountData }
