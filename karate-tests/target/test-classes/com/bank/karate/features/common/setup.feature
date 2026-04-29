@ignore
Feature: Setup y utilidades comunes
  Utilidades reutilizables para tests de API.
  Se marca con @ignore para no ejecutarse directamente.

Background:
  # Acceder a datos globales de callSingle en karate-config.js
  * def globalData = testData
  
  # Headers comunes
  * def jsonHeaders = globalData ? globalData.standardHeaders : { 'Content-Type': 'application/json', 'Accept': 'application/json' }
  
  # Endpoints base
  * def baseUrl = customerServiceUrl
  * def clientsEndpoint = baseUrl + '/api/clients'

# =====================================================================
# SCHEMAS CON WILDCARDS (validacion flexible)
# =====================================================================

# Schema base para respuestas de cliente - usa wildcards para campos opcionales
* def clientSchema =
  """
  {
    id: '#number',
    name: '#string',
    gender: '##string',           
    age: '#number',
    identification: '#string',
    address: '##string',          
    phone: '##string',            
    active: '#boolean'
  }
  """

# Schema para errores - valida estructura minima
* def errorSchema =
  """
  {
    timestamp: '#? _.length > 0',     
    status: '#number',
    error: '#string',
    message: '#string',
    path: '#? _.length > 0'          
  }
  """

# Schema para lista de clientes - puede ser vacia o tener items
* def clientListSchema = "#[] clientSchema"

# Schema flexible - permite campos adicionales no definidos
* def flexibleClientSchema =
  """
  {
    id: '#number',
    name: '#string',
    identification: '#string',
    active: '#boolean',
    "#ignore": "##"
  }
  """

# =====================================================================
# FUNCIONES UTILITARIAS
# =====================================================================

# Generar cliente con opcion de override (usa wildcard ## para merge)
* def generateClient = globalData ? globalData.generateClient : 
  """
  function(overrides) {
    var base = {
      name: 'Test Client ' + new Date().getTime(),
      gender: 'Masculino',
      age: 30,
      identification: 'TEST' + new Date().getTime(),
      address: 'Test Address',
      phone: '09' + Math.floor(Math.random() * 89999999 + 10000000),
      password: 'testpass123',
      active: true
    };
    
    if (overrides) {
      for (var key in overrides) {
        base[key] = overrides[key];
      }
    }
    return base;
  }
  """

# Extraer ID de respuesta con manejo de null (usando wildcards)
* def extractIdFromResponse = 
  """
  function(response) {
    return response?.id || null;
  }
  """

# Verificar que respuesta contiene campos esperados (matching parcial con wildcards)
* def containsFields =
  """
  function(response, expectedFields) {
    for (var key in expectedFields) {
      if (response[key] != expectedFields[key]) {
        karate.log('Field mismatch - key:', key, 'expected:', expectedFields[key], 'actual:', response[key]);
        return false;
      }
    }
    return true;
  }
  """

# Verificar status code en rango
* def isSuccessStatus =
  """
  function(status) {
    return status >= 200 && status < 300;
  }
  """

# Generar ID unico
* def generateUniqueId = globalData ? globalData.uniqueId :
  """
  function(prefix) {
    return (prefix || 'ID') + new Date().getTime() + Math.floor(Math.random() * 1000);
  }
  """

# =====================================================================
# CALLONCE - Setup que se ejecuta UNA VEZ por Feature
# =====================================================================

# Crear cliente base que se reutiliza en todos los escenarios de este feature
* def callonceSetup = callonce read('classpath:com/bank/karate/features/common/setup-once.feature')
* def sharedClientId = callonceSetup.clientId
* def sharedClientData = callonceSetup.clientData
* def sharedIdentification = callonceSetup.identification

# =====================================================================
# FUNCIONES DE CLEANUP
# =====================================================================

# Cleanup inteligente que verifica antes de eliminar
* def safeDelete =
  """
  function(clientId) {
    if (!clientId) return;
    
    // Primero obtener estado
    var result = karate.call('classpath:com/bank/karate/features/common/cleanup.feature', 
      { clientId: clientId });
    
    return result.success;
  }
  """
