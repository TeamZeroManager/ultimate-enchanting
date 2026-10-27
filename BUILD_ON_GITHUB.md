# Compilar Ultimate Enchanting sin instalar Gradle en tu PC

Este proyecto incluye un flujo de GitHub Actions que intenta compilar el mod en un runner de GitHub con Java 17 y sube el JAR como artefacto.

## Pasos
1. Entra a https://github.com/ e inicia sesión o crea una cuenta.
2. Crea un repositorio nuevo. Puedes llamarlo `ultimate-enchanting`.
3. Sube el contenido de esta carpeta al repositorio (incluida la carpeta oculta `.github/workflows`). También puedes descomprimir el ZIP y usar la opción de subir archivos de GitHub.
4. Abre la pestaña **Actions** del repositorio.
5. Selecciona **Build Ultimate Enchanting** y pulsa **Run workflow**. Si el flujo no aparece, confirma que el archivo `.github/workflows/build.yml` quedó en la rama principal.
6. Espera a que termine. Si falla, abre la ejecución y copia aquí el primer error relevante del paso **Build mod** para corregir el proyecto.
7. Si termina correctamente, abre la ejecución, baja hasta **Artifacts** y descarga `ultimate-enchanting-jar`. Descomprime ese archivo y copia el JAR dentro de la carpeta `mods` de tu instalación de Minecraft Forge 1.20.1.

## Aviso
Este proceso es una vía para intentar compilarlo; no significa que el código ya haya compilado o que esté probado en el juego. Si la compilación falla, habrá que corregir los errores y volver a ejecutar el flujo. El JAR final debe probarse en una instancia de Forge 1.20.1 antes de considerarlo listo.
