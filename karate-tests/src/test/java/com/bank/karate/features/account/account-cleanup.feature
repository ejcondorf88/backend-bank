@ignore
Feature: Account Cleanup - Eliminar cuenta de prueba
Este feature limpia las cuentas creadas durante los tests.

Background:
* url accountServiceUrl
* headers jsonHeaders

Scenario: Eliminar cuenta por numero de cuenta
# Parametro: accountNumber

# Primero intentar desactivar la cuenta (si esta activa)
Given path '/api/accounts/' + accountNumber + '/deactivate'
When method patch

# Luego eliminar la cuenta
Given path '/api/accounts/' + accountNumber
When method delete

# Verificar eliminacion
Given path '/api/accounts/' + accountNumber
When method get
Then status 404

* print 'Cuenta eliminada:', accountNumber
* def success = true
