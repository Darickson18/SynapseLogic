# 🧠 SynapseLogic - Análisis de Conectividad y Transmisión Neuronal

**SynapseLogic** es una herramienta de simulación y análisis de redes neuronales que modela el cerebro humano como un grafo dirigido. Permite cargar redes sinápticas desde archivos CSV, gestionar un diccionario de neurotransmisores, detectar zonas aisladas, calcular rutas de mayor activación (Dijkstra) y simular el deterioro cognitivo por fatiga. El proyecto está implementado completamente en Java, cumpliendo con los requisitos técnicos del curso.

## 👥 Integrantes del Equipo

- **Darickson18** - *dleiva@correo.unimet.edu.ve*

## 📌 Funcionalidades Principales

- Cargar/guardar redes sinápticas desde archivos CSV.
- Cargar un diccionario de neurotransmisores (hasta 50 tipos) con propiedades como velocidad y efecto.
- Detectar zonas aisladas (componentes inalcanzables) usando BFS o DFS.
- Calcular la ruta de **mayor activación** (menor tiempo de transmisión) mediante el algoritmo de Dijkstra, donde el peso de cada arista se calcula como `distancia / (velocidad_neurotransmisor * k)`.
- Simular fatiga cognitiva multiplicando todos los `k` por 1.2 y haciendo inaccesibles aquellas sinapsis cuyo `k` supere el umbral de 1.8.
- Agregar o eliminar neuronas dinámicamente.
- Visualizar el grafo en una ventana interactiva (con librería GraphStream) resaltando zonas aisladas en color rojo.

## 🛠️ Tecnologías y Estructuras Implementadas

- **Lenguaje:** Java (JDK 11+)
- **IDE:** NetBeans
- **Interfaz Gráfica:** Swing (JFrame, JFileChooser, etc.)
- **Visualización de Grafos:** GraphStream (única librería externa permitida)
- **Estructuras de Datos (implementación manual sin librerías):**
  - `Map` (Tabla Hash con encadenamiento) para el diccionario de neurotransmisores y la lista de adyacencia del grafo.
  - `DirectedGraph` (Grafo dirigido con lista de adyacencia).
- **Algoritmos:** BFS, DFS, Dijkstra, Kosaraju (para conectividad fuerte).

## 📂 Estructura del Proyecto

SynapseLogic/

src/
src/modelo/
src/modelo/Map.java # Implementación manual de tabla hash
src/modelo/Neurotransmisor.java # Propiedades del neurotransmisor
src/modelo/Sinapsis.java # Conexión sináptica
src/modelo/DirectedGraph.java # Grafo dirigido
src/modelo/DijkstraResult.java # Resultado de Dijkstra

controlador/
controlador/FileManager.java # Carga/guardado de archivos

vista/
vista/MainGUI.java # Interfaz principal
vista/GraphVisualizer.java # Visualización con GraphStream

lib/ # Librería para visualización

nbproject/ # Archivos de proyecto NetBeans

build.xml

README.md

## 🚀 Instrucciones de Uso

1. **Abrir el proyecto** en NetBeans.
2. **Ejecutar** la clase `MainGUI` (clic derecho → Run File).
3. **Cargar el diccionario de neurotransmisores** (botón "Cargar Diccionario"). Usa el archivo de ejemplo proporcionado.
4. **Cargar la red sináptica** (botón "Cargar Red"). Usa el archivo CSV con el formato especificado.
5. **Explorar las funcionalidades:**
   - **Visualizar Grafo:** Muestra la red en una ventana interactiva.
   - **Detectar Zonas Aisladas:** Selecciona BFS o DFS e ingresa una neurona fuente.
   - **Calcular Ruta Óptima:** Ingresa origen y destino para ejecutar Dijkstra.
   - **Simular Fatiga:** Aplica el deterioro cognitivo y recalcula rutas/zonas aisladas.
   - **Agregar/Eliminar Neurona:** Modifica el grafo en tiempo real.
   - **Guardar Red:** Exporta el estado actual a un archivo CSV.

## 📋 Formato de Archivos

### Red sináptica (CSV)

origen, destino, distancia, ID_Neurotransmisor, coheficiente_eficiencia_sináptica
1, 2, 0.85, GLU, 1.0
1, 3, 0.42, DA, 0.8
...

### Diccionario de neurotransmisores (CSV)

id, nombre, efecto, velocidad, descripcion
GLU, Glutamato, Excitatorio, 2.5, "Principal mediador..."
GABA, Ácido Gamma-aminobutírico, Inhibitorio, 1.2, "Reduce la actividad..."
...

## 📝 Notas Finales

Este proyecto fue desarrollado como parte de un ejercicio académico para demostrar la implementación manual de estructuras de datos y algoritmos de grafos, aplicados al modelado de redes neuronales. Para cualquier consulta o sugerencia, no dudes en abrir un issue en el repositorio.
