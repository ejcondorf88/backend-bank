function fn() {
  // Obtener el entorno de ejecucion
  var env = karate.env || 'local';
  karate.log('Karate environment:', env);

  // Configuracion base
  var config = {
    env: env,
    timeout: 5000,
    retry: { count: 3, interval: 1000 }
  };

  // Configuracion por entorno
  if (env === 'local') {
    config.baseUrl = 'http://localhost:8080';
    config.customerServiceUrl = 'http://localhost:8081';
    config.accountServiceUrl = 'http://localhost:8082';
    config.eurekaUrl = 'http://localhost:8761';
  } else if (env === 'dev') {
    config.baseUrl = 'http://dev-gateway.bank.com';
    config.customerServiceUrl = 'http://dev-customer.bank.com';
    config.accountServiceUrl = 'http://dev-account.bank.com';
    config.eurekaUrl = 'http://dev-eureka.bank.com:8761';
  } else if (env === 'test') {
    config.baseUrl = 'http://test-gateway.bank.com';
    config.customerServiceUrl = 'http://test-customer.bank.com';
    config.accountServiceUrl = 'http://test-account.bank.com';
    config.eurekaUrl = 'http://test-eureka.bank.com:8761';
  }

  // Headers comunes
  config.defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };

  // Configuracion de timeouts
  karate.configure('connectTimeout', config.timeout);
  karate.configure('readTimeout', config.timeout);

  // SSL para local
  if (env === 'local') {
    karate.configure('ssl', true);
  }

  // callSingle: Cargar datos globales UNA SOLA VEZ por ejecucion
  // Util para tokens, configuraciones, datos base que no cambian
  var result = karate.callSingle('classpath:com/bank/karate/features/common/init-data.feature', config);
  config.testData = result;

  // Log de configuracion
  karate.log('Base URL:', config.baseUrl);
  karate.log('Customer Service URL:', config.customerServiceUrl);

  return config;
}
