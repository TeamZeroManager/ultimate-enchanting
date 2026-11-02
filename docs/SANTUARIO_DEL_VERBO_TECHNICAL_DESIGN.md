# Santuario del Verbo — Diseño técnico

Estado: diseño aprobado; implementación pendiente.
Versión objetivo: Minecraft Java 1.20.1 / Forge 47.3.0.
Rama de trabajo: feature/ultimate-enchanting-2.0.

## Objetivo

Añadir una estructura de mundo única por mundo, localizable mediante el comando vanilla
`/locate structure ultimate_enchanting:santuario_del_verbo` y mediante un mapa especial
vendido por aldeanos cartógrafos. El mapa debe localizar la misma estructura registrada por
el sistema de worldgen; nunca debe crear una segunda instancia.

## Reglas de generación

- Registro como estructura de mundo real en el registro de estructuras de Minecraft.
- Distribución aleatoria y única por mundo mediante un StructureSet con separación y
  espaciado configurados para que no aparezcan duplicados.
- Generación por piezas modulares tipo Stronghold, con seis niveles verticales descendentes.
- Los seis niveles son obligatorios y tienen una identidad fija:
  1. El Despertar — runa de La que Despertó.
  2. El Juramento — runa de La que Juró.
  3. La Caída — runa de La que Cayó.
  4. La Permanencia — runa de La que Permaneció.
  5. El Recuerdo — runa de La que Recordó.
  6. El Desconocido — sexta runa, símbolo del Verbo Primordial.
- Cada nivel tiene una ruta principal garantizada, habitaciones secundarias aleatorias y
  conexiones de regreso. Las rutas opcionales no pueden bloquear la progresión.
- El sexto nivel contiene una gran cámara central, cinco monumentos rúnicos y el altar.
- Los módulos deben evitar quedar enterrados de forma inaccesible o cortados por límites
  de chunk. La altura debe respetar los límites del mundo 1.20.1.

## Exploración y orientación

- Núcleo vertical reconocible y conexiones entre niveles.
- Cada nivel usa paleta, iluminación y motivos propios.
- Señales rúnicas en cruces, atajos desbloqueables y puntos de referencia.
- Habitaciones opcionales con trampas, spawners, cámaras de combate, tesoros y secretos.
- Las trampas deben ser evitables o desactivables; nunca deben sellar permanentemente
  el camino obligatorio.

## Cofres y documentos

Tablas de botín diferenciadas por nivel:
- Nivel I: hierro, carbón, comida, oro y recursos básicos.
- Nivel II: hierro, oro, lapislázuli y esmeraldas.
- Nivel III: diamantes, oro, esmeraldas y obsidiana.
- Nivel IV: diamantes, esmeraldas, materiales valiosos y equipo encantado ocasional.
- Nivel V: botín valioso, libros encantados y documentos históricos.
- Nivel VI: recompensas excepcionales y documentos únicos.

Los cofres se generan con loot tables vanilla/datapack y se rellenan una sola vez al abrirlos.
No debe existir una mecánica de regeneración de botín.

Los documentos tendrán títulos, autores y narración diegética. Colecciones previstas:
- Crónicas del Santuario.
- Las Cinco.
- Los Últimos Custodios.
- El Verbo.

Las cinco páginas necesarias para deducir el orden del ritual deben generarse siempre en
cofres accesibles de la estructura. Los documentos opcionales amplían el trasfondo, pero
no bloquean el ritual.

## Localización

- La estructura debe estar registrada en worldgen para que el comando vanilla
  `/locate structure ultimate_enchanting:santuario_del_verbo` funcione.
- El cartógrafo tendrá un intercambio avanzado que entregue el mapa del Santuario.
- El mapa consultará la ubicación de la estructura existente en el mundo. No debe buscar
  otra ubicación ni provocar generación duplicada.
- Si el mundo no tiene una ubicación válida todavía, el comercio no debe entregar un mapa
  engañoso; la búsqueda debe resolverse mediante el sistema de estructura del mundo.

## Ritual y persistencia

- Cinco ranuras, una por Matadioses, orden fijo:
  La que Despertó, La que Juró, La que Cayó, La que Permaneció, La que Recordó.
- Orden incorrecto: mensaje/feedback y ninguna espada se consume.
- Orden correcto: bloquear extracción durante la secuencia, ejecutar la convergencia y
  consumir las cinco espadas solo al confirmar el resultado.
- Estado del ritual persistente en BlockEntity y sincronizado con clientes.
- El Verbo Primordial solo se genera una vez por altar/mundo; evitar duplicados por
  desconexión, reinicio o interacción repetida.

## Compatibilidad y protección

- No modificar el comportamiento validado de la mesa, encantamientos, Vitalidad,
  Matadioses ni Lente del Verbo Antiguo salvo integración explícita.
- Evitar dependencias obligatorias de otros mods.
- Usar registros y datapack resources compatibles con Forge 47.3.0.
- No reutilizar ni sobrescribir la estructura vanilla Stronghold.

## Plan de implementación y verificación

1. Worldgen mínimo: estructura registrada, StructureSet, pools y piezas válidas.
2. Compilar con Java 17 / Forge 47.3.0 y verificar carga de datapacks.
3. Probar /locate, generación en mundo nuevo, altura, límites de chunk y persistencia.
4. Completar los seis niveles y validar rutas obligatorias/alternativas.
5. Añadir cofres, loot tables, documentos, spawners y trampas.
6. Añadir mapa del cartógrafo enlazado a la ubicación real.
7. Añadir altar y ritual persistente.
8. Pruebas en Minecraft: mundo nuevo, mundo existente, varios seeds, reinicio durante
   ritual, cofres accesibles, cinco páginas garantizadas, comercio y /locate.

No marcar ninguna fase como terminada solo por compilar. La generación y la experiencia
deben probarse en Minecraft.
