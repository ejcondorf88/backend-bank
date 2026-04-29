# GitHub Actions Workflows

Este directorio contiene los workflows de GitHub Actions para el proyecto backend-bank.

## Workflows Disponibles

### 1. PR Unit Tests (`pr-unit-tests.yml`)

**Trigger:** Se ejecuta automáticamente cuando se crea un Pull Request desde una rama `feature/**` hacia `develop`.

**Qué hace:**
1. Detecta qué microservicios han sido modificados
2. Ejecuta los tests unitarios solo de los microservicios modificados:
   - `ms-customer`
   - `ms-account`
3. Si algún test falla, bloquea el PR (no permite merge)
4. Si todos los tests pasan, el PR puede ser mergeado

**Flujo:**
```
PR creado (feature/** → develop)
    ↓
Detectar cambios en microservicios
    ↓
Ejecutar tests unitarios (solo servicios modificados)
    ↓
✅ Todos pasan → PR puede mergearse
❌ Alguno falla → PR bloqueado
```

## Configuración Requerida

### 1. Configurar Branch Protection en GitHub

Para que el workflow funcione correctamente, debes configurar la protección de la rama `develop`:

1. Ve a tu repositorio en GitHub
2. Click en **Settings** → **Branches**
3. Click en **Add rule**
4. En **Branch name pattern**, escribe: `develop`
5. Configura las siguientes opciones:

#### ✅ Requerir Pull Request antes de mergear
- **Require approvals**: 1 (o más)
- **Dismiss stale PR approvals**: ✅

#### ✅ Requerir status checks antes de mergear
- **Require branches to be up to date**: ✅
- **Status checks que son requeridos**:
  - `test-summary`

#### ✅ (Opcional) Requerir resolución de conversaciones

### 2. Verificar que los tests existen

Asegúrate de que cada microservicio tenga tests unitarios:

```bash
# Para ms-customer
cd ms-customer
mvn test

# Para ms-account
cd ms-account
mvn test
```

### 3. Convención de nombres de ramas

Para que el workflow se active correctamente, usa la convención:

```
feature/nombre-de-la-feature
```

Ejemplos válidos:
- `feature/add-new-endpoint`
- `feature/fix-balance-calculation`
- `feature/HU-123-improve-performance`

## Estructura del Workflow

```yaml
Jobs:
├── detect-changes       # Detecta qué microservicios cambiaron
├── test-ms-customer     # Ejecuta tests de ms-customer (si cambió)
├── test-ms-account      # Ejecuta tests de ms-account (si cambió)
└── test-summary         # Resume resultados y valida éxito
```

## Optimización

El workflow está optimizado para:
- **Ejecutar solo tests de servicios modificados** (no todos)
- **Usar cache de Maven** para acelerar builds
- **Subir artefactos** de resultados de tests para debugging
- **Parallelización** de jobs cuando múltiples servicios cambian

## Troubleshooting

### Los tests no se ejecutan

Verifica que:
1. La rama sigue el patrón `feature/**`
2. El PR apunta a `develop`
3. Hay cambios en `ms-customer/` o `ms-account/`

### Quiero forzar la ejecución de todos los tests

Modifica el workflow temporalmente para ejecutar todos los jobs sin condición `if`.

### Necesito agregar más microservicios

1. Agrega el servicio en `detect-changes.outputs`
2. Crea un nuevo job similar a `test-ms-customer`
3. Agrega el job a las dependencias de `test-summary`

## Comandos Útiles

```bash
# Ejecutar tests localmente antes de hacer PR
cd ms-customer && mvn test
cd ms-account && mvn test

# Ver logs del workflow
# Ir a: GitHub → Actions → Seleccionar workflow → Ver logs

# Forzar re-ejecución del workflow
# Ir al PR → Checks tab → Re-run jobs
```
