@ignore
Feature: Inicializacion de Datos Globales
  Este feature se ejecuta UNA SOLA VEZ por suite de tests (callSingle).
  Se usa para configurar datos base que se reutilizan en todos los tests.

Background:
  * def env = karate.get('env') || 'local'
  * def customerServiceUrl = karate.get('customerServiceUrl') || 'http://localhost:8081'

Scenario: Cargar datos de prueba globales
  # Datos base que se usan en multiples tests
  * def baseTestData =
    """
    {
      validClient: {
        name: 'Test Client Base',
        gender: 'Masculino',
        age: 30,
        identification: 'BASE' + new Date().getTime(),
        address: 'Base Address 123',
        phone: '0987654321',
        password: 'basepass123',
        active: true
      },
      
      validClientInactive: {
        name: 'Inactive Client',
        gender: 'Femenino',
        age: 25,
        identification: 'INACTIVE' + new Date().getTime(),
        address: 'Inactive Address',
        phone: '0987000000',
        password: 'inactpass123',
        active: false
      },
      
      invalidClient: {
        name: '',
        identification: '',
        password: '123'
      },
      
      partialUpdate: {
        name: 'Updated Via Global'
      },
      
      standardHeaders: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    }
    """
  
  # Generador de IDs unicos (como funcion para reusar)
  * def generateUniqueId = 
    """
    function(prefix) {
      return (prefix || 'ID') + new Date().getTime() + Math.floor(Math.random() * 1000);
    }
    """
  
  # Generador de clientes con override de campos
  * def generateClient =
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
      
      // Merge de overrides si existen
      if (overrides) {
        for (var key in overrides) {
          base[key] = overrides[key];
        }
      }
      return base;
    }
    """
  
  # Verificar conectividad (opcional, para fallar rapido si el servicio no esta)
  Given url customerServiceUrl
  And path '/actuator/health'
  When method get
  Then status 200
  * print 'Servicio health-check: OK'
  
  # Devolver datos para uso global
  * def testData = baseTestData
  * def testData.uniqueId = generateUniqueId
  * def testData.generateClient = generateClient
  
  * print 'Datos globales inicializados correctamente'
