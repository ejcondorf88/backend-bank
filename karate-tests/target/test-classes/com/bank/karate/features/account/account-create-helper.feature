@ignore
Feature: Account Create Helper - Helper para crear cuentas
Este feature es un helper reutilizable para crear cuentas en otros tests.
Recibe los datos de la cuenta como parametro.

Background:
* url accountServiceUrl
* headers jsonHeaders

Scenario: Crear cuenta con datos proporcionados
# Los datos de la cuenta se pasan como argumento al llamar

Given path '/api/accounts'
And request __arg
When method post
Then status 201

# Guardar datos de la cuenta creada
* def id = response.id
* def accountNumber = response.accountNumber
* def balance = response.balance
* def active = response.active
* def clientId = response.clientId

* print 'Cuenta creada - ID:', id, 'Numero:', accountNumber
