@ignore
Feature: Movement Setup y utilidades comunes
Utilidades reutilizables para tests de API de movimientos.
Se marca con @ignore para no ejecutarse directamente.

Background:
# Acceder a datos globales de callSingle en karate-config.js
* def globalData = testData

# Headers comunes
* def jsonHeaders = globalData ? globalData.standardHeaders : { 'Content-Type': 'application/json', 'Accept': 'application/json' }

# Endpoints base
* def baseUrl = accountServiceUrl
* def movementsEndpoint = baseUrl + '/api/movements'

# =====================================================================
# SCHEMAS CON WILDCARDS (validacion flexible)
# =====================================================================

# Schema base para respuestas de movimiento - usa wildcards para campos opcionales
* def movementSchema =
"""
{
    id: '#number',
    accountNumber: '#string',
    date: '#? _.length > 0',
    type: '#string',
    amount: '#number',
    balance: '#number'
}
"""

# Schema para movimientos de deposito
* def depositSchema =
"""
{
    id: '#number',
    accountNumber: '#string',
    date: '#? _.length > 0',
    type: 'Deposito',
    amount: '#number? _ > 0',
    balance: '#number'
}
"""

# Schema para movimientos de retiro
* def withdrawalSchema =
"""
{
    id: '#number',
    accountNumber: '#string',
    date: '#? _.length > 0',
    type: 'Retiro',
    amount: '#number? _ < 0',
    balance: '#number'
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

# Schema para lista de movimientos - puede ser vacia o tener items
* def movementListSchema = "#[] movementSchema"

# Schema flexible - permite campos adicionales no definidos
* def flexibleMovementSchema =
"""
{
    id: '#number',
    accountNumber: '#string',
    type: '#string',
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

# Generar movimiento de prueba
* def generateMovement =
"""
function(overrides) {
    var timestamp = new Date().getTime();
    var base = {
        accountNumber: 'ACC' + timestamp,
        type: 'Deposito',
        amount: 600.00
    };

    if (overrides) {
        for (var key in overrides) {
            base[key] = overrides[key];
        }
    }
    return base;
}
"""

# Calcular fecha de hoy en formato yyyy-MM-dd
* def getToday =
"""
function() {
    var date = new Date();
    var year = date.getFullYear();
    var month = String(date.getMonth() + 1).padStart(2, '0');
    var day = String(date.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
}
"""

# Calcular fecha de ayer en formato yyyy-MM-dd
* def getYesterday =
"""
function() {
    var date = new Date();
    date.setDate(date.getDate() - 1);
    var year = date.getFullYear();
    var month = String(date.getMonth() + 1).padStart(2, '0');
    var day = String(date.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
}
"""

# Calcular fecha de manana en formato yyyy-MM-dd
* def getTomorrow =
"""
function() {
    var date = new Date();
    date.setDate(date.getDate() + 1);
    var year = date.getFullYear();
    var month = String(date.getMonth() + 1).padStart(2, '0');
    var day = String(date.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
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

# Extraer ID de movimiento de respuesta
* def extractMovementId =
"""
function(response) {
    return response?.id || null;
}
"""

# =====================================================================
# CALLONCE - Setup que se ejecuta UNA VEZ por Feature
# =====================================================================

# Crear cuenta y movimiento base que se reutilizan
* def callonceSetup = callonce read('classpath:com/bank/karate/features/movement/movement-setup-once.feature')
* def sharedMovementId = callonceSetup.movementId
* def sharedAccountNumber = callonceSetup.accountNumber
* def sharedMovementData = callonceSetup.movementData

# =====================================================================
# FUNCIONES DE CLEANUP
# =====================================================================

# Cleanup inteligente que verifica antes de eliminar
* def safeDeleteMovement =
"""
function(movementId) {
    if (!movementId) return;

    var result = karate.call('classpath:com/bank/karate/features/movement/movement-cleanup.feature',
    { movementId: movementId });

    return result.success;
}
"""

* def safeDeleteAccount =
"""
function(accountNumber) {
    if (!accountNumber) return;

    var result = karate.call('classpath:com/bank/karate/features/account/account-cleanup.feature',
    { accountNumber: accountNumber });

    return result.success;
}
"""
