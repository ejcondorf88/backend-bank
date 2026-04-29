@ignore
Feature: Deactivate - Desactivar cliente
  Feature auxiliar para desactivar un cliente por ID.

Background:
  * url customerServiceUrl

Scenario: Desactivar cliente
  * def clientId = karate.get('clientId')
  * if (!clientId) karate.abort()
  
  Given path '/api/clients/' + clientId + '/deactivate'
  When method patch
  Then status 200
  
  * print 'Cliente', clientId, 'desactivado'
