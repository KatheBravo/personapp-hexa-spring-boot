#!/usr/bin/env bash
# =====================================================================
# PersonApp - Prueba de integracion de la API REST
#
# Ejecuta el CRUD completo de persona, profesion, telefono y estudio
# contra MariaDB y luego contra MongoDB, con la API corriendo y SIN
# reiniciarla entre un motor y otro (cambio de base de datos en caliente).
#
# Requisitos: la API arriba, las bases cargadas con los scripts DML, curl y jq.
# Uso:   bash scripts/prueba-integracion.sh [URL_BASE]     (por defecto http://localhost:3000)
# Deja las bases como estaban: todo lo que crea lo borra al final.
# =====================================================================
set -u

BASE="${1:-http://localhost:3000}"
API="$BASE/api/v1"
TMP="$(mktemp)"
PASS=0
FAIL=0
N=0
ROWS=""

# check <motor> <descripcion> <metodo> <ruta o URL> <HTTP esperado> [cuerpo JSON] [filtro jq] [valor esperado]
check() {
  local motor="$1" desc="$2" method="$3" path="$4" expected="$5" body="${6:-}" filter="${7:-}" value="${8:-}"
  local url code got="" ok=1
  case "$path" in http*) url="$path" ;; *) url="$API$path" ;; esac
  if [ -n "$body" ]; then
    code=$(curl -s -L -o "$TMP" -w '%{http_code}' -X "$method" -H 'Content-Type: application/json' -d "$body" "$url")
  else
    code=$(curl -s -L -o "$TMP" -w '%{http_code}' -X "$method" "$url")
  fi
  [ "$code" = "$expected" ] || ok=0
  if [ -n "$filter" ]; then
    got=$(jq -r "$filter" "$TMP" 2>/dev/null | tr '\n' ' ' | sed 's/ $//')
    [ "$got" = "$value" ] || ok=0
  fi
  N=$((N + 1))
  local esperado="HTTP $expected" obtenido="HTTP $code"
  if [ -n "$filter" ]; then
    esperado="$esperado, \`$filter\` = $value"
    obtenido="$obtenido, $got"
  fi
  if [ $ok = 1 ]; then
    PASS=$((PASS + 1))
    printf 'OK    %3d [%s] %s %s -> %s\n' "$N" "$motor" "$method" "$path" "$obtenido"
    ROWS="$ROWS| $N | $motor | $desc | \`$method $path\` | $esperado | $obtenido | ✅ |"$'\n'
  else
    FAIL=$((FAIL + 1))
    printf 'FALLA %3d [%s] %s %s -> esperado: %s | obtenido: %s\n' "$N" "$motor" "$method" "$path" "$esperado" "$obtenido"
    printf '          respuesta: %s\n' "$(head -c 400 "$TMP")"
    ROWS="$ROWS| $N | $motor | $desc | \`$method $path\` | $esperado | $obtenido | ❌ |"$'\n'
  fi
}

persona() { printf '{"dni":"%s","firstName":"%s","lastName":"%s","age":"%s","sex":"%s","database":"%s"}' "$@"; }
profesion() { printf '{"id":"%s","name":"%s","description":"%s","database":"%s"}' "$@"; }
telefono() { printf '{"number":"%s","company":"%s","ownerId":"%s","database":"%s"}' "$@"; }
estudio() { printf '{"personId":"%s","professionId":"%s","graduationDate":"%s","universityName":"%s","database":"%s"}' "$@"; }

echo "== Swagger / OpenAPI =="
check "-" "Swagger UI disponible" GET "$BASE/swagger-ui.html" 200
check "-" "Especificacion OpenAPI generada" GET "$BASE/api-docs" 200 "" '.info.title' "PersonApp API"
check "-" "OpenAPI documenta los 23 recursos" GET "$BASE/api-docs" 200 "" '.paths | length' "23"
check "-" "Motor invalido" GET "/persona/POSTGRES" 400 "" '.status' "BAD_REQUEST"

for DB in MARIA MONGO; do
  if [ "$DB" = "MARIA" ]; then M="MariaDB"; OTRO="MONGO"; else M="MongoDB"; OTRO="MARIA"; fi
  echo "== $M =="

  # --- Lectura de los datos de prueba (scripts DML) ---
  check "$M" "Listar personas" GET "/persona/$DB" 200 "" 'length' "5"
  check "$M" "Contar personas" GET "/persona/$DB/count" 200 "" '.' "5"
  check "$M" "Buscar persona" GET "/persona/$DB/123456789" 200 "" '.firstName + " " + .lastName' "Pepe Perez"
  check "$M" "Telefonos de una persona" GET "/persona/$DB/123456789/telefonos" 200 "" '[.[].number] | sort | join(",")' "3001234567,3109876543"
  check "$M" "Estudios de una persona" GET "/persona/$DB/123456789/estudios" 200 "" '[.[].professionName] | sort | join(",")' "Arquitectura,Ingeniería de Sistemas"
  check "$M" "Listar profesiones" GET "/profesion/$DB" 200 "" 'length' "5"
  check "$M" "Estudios de una profesion" GET "/profesion/$DB/2/estudios" 200 "" '.[0].personName' "Pepa Juarez"
  check "$M" "Listar telefonos" GET "/telefono/$DB" 200 "" 'length' "5"
  check "$M" "Buscar telefono (con duenio)" GET "/telefono/$DB/3001234567" 200 "" '.ownerName' "Pepe Perez"
  check "$M" "Listar estudios" GET "/estudio/$DB" 200 "" 'length' "5"
  check "$M" "Fecha de grado sin desfase" GET "/estudio/$DB/123456789/1" 200 "" '.graduationDate' "2015-12-01"
  check "$M" "Estudio sin fecha de grado" GET "/estudio/$DB/963852741/3" 200 "" '.graduationDate' "null"

  # --- Crear ---
  check "$M" "Crear persona" POST "/persona" 201 "$(persona 1010101010 Laura Gomez 25 F $DB)" '.database' "$M"
  check "$M" "Crear persona repetida" POST "/persona" 409 "$(persona 1010101010 Laura Gomez 25 F $DB)" '.status' "CONFLICT"
  check "$M" "Rechazar edad negativa" POST "/persona" 400 "$(persona 1212121212 Ana Ruiz -3 F $DB)"
  check "$M" "Rechazar genero invalido" POST "/persona" 400 "$(persona 1212121212 Ana Ruiz 20 X $DB)"
  check "$M" "Rechazar cedula no numerica" POST "/persona" 400 "$(persona abc Ana Ruiz 20 F $DB)"
  if [ "$DB" = "MARIA" ]; then
    check "$M" "Motores independientes" GET "/persona/$OTRO/1010101010" 404
  fi
  check "$M" "Crear profesion" POST "/profesion" 201 "$(profesion 10 Psicologia 'Estudia la conducta humana' $DB)" '.name' "Psicologia"
  check "$M" "Crear telefono" POST "/telefono" 201 "$(telefono 3001112233 Claro 1010101010 $DB)" '.ownerName' "Laura Gomez"
  check "$M" "Telefono con duenio inexistente" POST "/telefono" 404 "$(telefono 3009998877 Claro 999 $DB)"
  check "$M" "Telefono con letras" POST "/telefono" 400 "$(telefono 300-ABC Claro 1010101010 $DB)"
  check "$M" "Crear estudio" POST "/estudio" 201 "$(estudio 1010101010 10 2024-06-15 'Pontificia Universidad Javeriana' $DB)" '.professionName' "Psicologia"
  check "$M" "Estudio con fecha futura" POST "/estudio" 400 "$(estudio 1010101010 1 2999-01-01 Javeriana $DB)"
  check "$M" "Estudio con profesion inexistente" POST "/estudio" 404 "$(estudio 1010101010 99 2020-01-01 Javeriana $DB)"
  check "$M" "Telefonos de la persona creada" GET "/persona/$DB/1010101010/telefonos" 200 "" 'length' "1"
  check "$M" "Estudios de la persona creada" GET "/persona/$DB/1010101010/estudios" 200 "" '.[0].universityName' "Pontificia Universidad Javeriana"

  # --- Editar ---
  check "$M" "Editar persona" PUT "/persona/1010101010" 200 "$(persona 1010101010 Laura Gomez 26 F $DB)" '.age' "26"
  check "$M" "Editar persona inexistente" PUT "/persona/5555" 404 "$(persona 5555 X Y 1 F $DB)"
  check "$M" "Editar profesion" PUT "/profesion/10" 200 "$(profesion 10 Psicologia 'Ciencia del comportamiento' $DB)" '.description' "Ciencia del comportamiento"
  check "$M" "Editar telefono" PUT "/telefono/3001112233" 200 "$(telefono 3001112233 Movistar 1010101010 $DB)" '.company' "Movistar"
  check "$M" "Editar estudio" PUT "/estudio/1010101010/10" 200 "$(estudio 1010101010 10 2024-12-01 'Universidad Nacional de Colombia' $DB)" '.universityName + " " + .graduationDate' "Universidad Nacional de Colombia 2024-12-01"
  check "$M" "Persona editada conserva relaciones" GET "/persona/$DB/1010101010/telefonos" 200 "" 'length' "1"

  # --- Eliminar ---
  check "$M" "Crear estudio para eliminar" POST "/estudio" 201 "$(estudio 147258369 1 2020-01-10 Javeriana $DB)"
  check "$M" "Eliminar estudio" DELETE "/estudio/$DB/147258369/1" 200 "" '.status' "OK"
  check "$M" "Estudio eliminado" GET "/estudio/$DB/147258369/1" 404
  check "$M" "Eliminar profesion (con estudios)" DELETE "/profesion/$DB/10" 200 "" '.status' "OK"
  check "$M" "Cascada profesion -> estudios" GET "/estudio/$DB/1010101010/10" 404
  check "$M" "Eliminar persona (con telefonos)" DELETE "/persona/$DB/1010101010" 200 "" '.status' "OK"
  check "$M" "Cascada persona -> telefonos" GET "/telefono/$DB/3001112233" 404
  check "$M" "Eliminar persona inexistente" DELETE "/persona/$DB/1010101010" 404
  check "$M" "Datos de prueba intactos" GET "/estudio/$DB/count" 200 "" '.' "5"
done

rm -f "$TMP"
echo
echo "Resultado: $PASS de $N pruebas correctas, $FAIL fallidas"

if [ -n "${GITHUB_STEP_SUMMARY:-}" ]; then
  {
    echo "## Prueba de integracion de la API REST"
    echo
    echo "**$PASS de $N pruebas correctas** (MariaDB y MongoDB, cambio de motor sin reiniciar la API)."
    echo
    echo "| # | Motor | Caso | Peticion | Esperado | Obtenido | |"
    echo "|---|---|---|---|---|---|---|"
    printf '%s' "$ROWS"
  } >> "$GITHUB_STEP_SUMMARY"
fi

[ "$FAIL" -eq 0 ]
