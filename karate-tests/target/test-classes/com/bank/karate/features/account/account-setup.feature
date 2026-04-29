@ignore
Feature: Account Setup y utilidades comunes
Utilidades reutilizables para tests de API de cuentas.
Se marca con @ignore para no ejecutarse directamente.

Background:
# Acceder a datos globales de callSingle en karate-config.js
* def globalData = testData

# Headers comunes
* def jsonHeaders = globalData ? globalData.standardHeaders : { 'Content-Type': 'application/json', 'Accept': 'application/json' }

# Endpoints base
* def baseUrl = accountServiceUrl
* def accountsEndpoint = baseUrl + '/api/accounts'

# =====================================================================
# SCHEMAS CON WILDCARDS (validacion flexible)
# =====================================================================

# Schema base para respuestas de cuenta - usa wildcards para campos opcionales
* def accountSchema =
"""
{
    id: '#number',
    accountNumber: '#string',
    accountType: '#string',
    balance: '#number',
    active: '#boolean',
    clientId: '#number'
}
"""

# Schema para errores - valida estructura minima
* def errorSchema =
"""
{
    timestamp: '#? _.length > 0',
    status: '#number',
    error: '#string',
    message: '#string'
}
"""

# Schema para lista de cuentas - puede ser vacia o tener items
* def accountListSchema = "#[] accountSchema"

# Schema flexible - permite campos adicionales no definidos
* def flexibleAccountSchema =
"""
{
    id: '#number',
    accountNumber: '#string',
    accountType: '#string',
    active: '#boolean',
    "#ignore": "##"
}
"""

# =====================================================================
# FUNCIONES UTILITARIAS
# =====================================================================

# Generar cuenta con opcion de override (usa wildcard ## para merge)
* def generateAccount =
"""
function(overrides) {
    var base = {
        accountNumber: 'ACC' + new Date().getTime(),
        accountType: 'Ahorro',
        initialBalance: 1000.00,
        active: true,
        clientId: 1
    };

    if (overrides) {
        for (var key in overrides) {
            base[key] = overrides[key];
        }
    }
    return base;
}
"""

# Generador de IDs unicos para cuentas
* def generateAccountNumber =
"""
function(prefix) {
    return (prefix || 'ACC') + new Date().getTime() + Math.floor(Math.random() * 1000);
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

# Calcular saldo esperado despues de transaccion
* def calculateExpectedBalance =
"""
function(currentBalance, amount, isDeposit) {
    if (isDeposit) {
        return currentBalance + amount;
    } else {
        return currentBalance - amount;
    }
}
"""

# =====================================================================
# CALLONCE - Setup que se ejecuta UNA VEZ por Feature
# =====================================================================

# Crear cuenta base que se reutiliza en todos los escenarios de este feature
* def callonceSetup = callonce read('classpath:com/bank/karate/features/account/account-setup-once.feature')
* def sharedAccountNumber = callonceSetup.accountNumber
* def sharedAccountData = callonceSetup.accountData
* def sharedAccountId = callonceSetup.accountId

# =====================================================================
# FUNCIONES DE CLEANUP
# =====================================================================

# Cleanup inteligente que verifica antes de eliminar
* def safeDeleteAccount =
"""
function(accountNumber) {
    if (!accountNumber) return;

    // Primero obtener estado
    var result = karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });

    return result.success;
}
"""
