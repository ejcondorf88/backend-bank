@ignore
Feature: Cleanup - Eliminacion segura de clientes
  Feature para limpiar datos de prueba de forma segura.

Background:
  * url customerServiceUrl

Scenario: Eliminar cliente si existe
  * def clientId = karate.get('clientId')
  * def success = false
  
  # Si no hay ID, nada que hacer
  * if (!clientId) karate.abort()
  
  # Primero verificar si existe
  Given path '/api/clients/' + clientId
  When method get
  
  # Si no existe, ya esta limpio
  * if (responseStatus == 404) success = true
  * if (responseStatus == 404) karate.abort()
  
  # Si existe pero esta activo, desactivar primero
  * if (response.active) karate.call('classpath:com/bank/karate/features/common/deactivate.feature', { clientId: clientId })
  
  # Eliminar
  Given path '/api/clients/' + clientId
  When method delete
  
  * if (responseStatus == 204) success = true
  
  * print 'Cliente', clientId, 'eliminado:', success
