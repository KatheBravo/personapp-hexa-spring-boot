#!/usr/bin/env bash
# =====================================================================
# PersonApp - Prueba del adaptador de consola (CLI)
#
# Alimenta el menu con una secuencia de opciones (como si una persona las
# escribiera) y verifica la salida: listar en MariaDB y MongoDB, crear,
# buscar y eliminar una persona, ver telefonos, buscar un estudio y contar.
#
# Requisitos: las bases cargadas con los scripts DML y el JAR compilado.
# Uso:   bash scripts/prueba-cli.sh [ruta del JAR]
# =====================================================================
set -u

JAR="${1:-cli-input-adapter/target/cli-input-adapter-1.0.0.jar}"
SALIDA="${SALIDA_CLI:-salida-cli.txt}"

# Cada linea es lo que se escribe en el teclado
ENTRADA=$(cat <<'EOF'
1
1
1
3
2020202020
Ana
Lopez
F

2
2020202020
5
2020202020
0
2
1
7
123456789
0
0
2
2
6
0
0
3
1
1
0
0
4
2
2
123456789
1
0
0
0
EOF
)

printf '%s\n' "$ENTRADA" | timeout 180 java -Dstdout.encoding=UTF-8 -jar "$JAR" > "$SALIDA" 2>&1
CODIGO=$?

PASS=0
FAIL=0
ROWS=""
esperar() { # descripcion, texto que debe aparecer en la salida
  if grep -qF -- "$2" "$SALIDA"; then
    PASS=$((PASS + 1)); echo "OK    $1"
    ROWS="$ROWS| $1 | \`$2\` | ✅ |"$'\n'
  else
    FAIL=$((FAIL + 1)); echo "FALLA $1 (no aparece: $2)"
    ROWS="$ROWS| $1 | \`$2\` | ❌ |"$'\n'
  fi
}

esperar "Listar personas en MariaDB" "Persona[cc=123456789, nombre=Pepe Perez, genero=M, edad=30]"
esperar "Crear persona en MariaDB" "Creada: Persona[cc=2020202020, nombre=Ana Lopez, genero=F, edad=-]"
esperar "Buscar persona creada" "Persona[cc=2020202020, nombre=Ana Lopez, genero=F, edad=-]"
esperar "Eliminar persona" "Persona 2020202020 eliminada"
esperar "Cambiar a MongoDB sin reiniciar" "Personas [MongoDB]"
esperar "Telefonos de una persona en MongoDB" "Telefono[numero=3001234567, operador=Claro, duenio=123456789 (Pepe Perez)]"
esperar "Contar profesiones en MongoDB" "Total de profesiones en MongoDB: 5"
esperar "Listar telefonos en MariaDB" "Telefono[numero=6012345678, operador=ETB, duenio=987654321 (Pepito Perez)]"
esperar "Buscar estudio en MongoDB" "Estudio[persona=123456789 (Pepe Perez), profesion=1 (Ingeniería de Sistemas), fecha=2015-12-01, universidad=Pontificia Universidad Javeriana]"
esperar "Salir del menu" "Hasta luego."

if [ "$CODIGO" -eq 0 ]; then
  PASS=$((PASS + 1)); echo "OK    La aplicacion termina sola (codigo 0)"
  ROWS="$ROWS| La aplicacion termina sola | codigo de salida 0 | ✅ |"$'\n'
else
  FAIL=$((FAIL + 1)); echo "FALLA La aplicacion termino con codigo $CODIGO"
  ROWS="$ROWS| La aplicacion termina sola | codigo de salida $CODIGO | ❌ |"$'\n'
fi

echo
echo "Resultado CLI: $PASS correctas, $FAIL fallidas (salida completa en $SALIDA)"

if [ -n "${GITHUB_STEP_SUMMARY:-}" ]; then
  {
    echo "## Prueba del adaptador de consola (CLI)"
    echo
    echo "**$PASS de $((PASS + FAIL)) verificaciones correctas.**"
    echo
    echo "| Caso | Texto esperado en la consola | |"
    echo "|---|---|---|"
    printf '%s' "$ROWS"
  } >> "$GITHUB_STEP_SUMMARY"
fi

[ "$FAIL" -eq 0 ]
