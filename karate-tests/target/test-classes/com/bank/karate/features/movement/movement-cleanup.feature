@ignore
Feature: Movement Cleanup - Eliminar movimiento de prueba
Este feature limpia los movimientos creados durante los tests.

Background:
* url accountServiceUrl
* headers jsonHeaders

Scenario: Eliminar movimiento por ID
# Parametro: movementId

# Eliminar movimiento
Given path '/api/movements/' + movementId
When method delete

# Verificar eliminacion
Given path '/api/movements/' + movementId
When method get
Then status 404

* print 'Movimiento eliminado:', movementId
* def success = true
