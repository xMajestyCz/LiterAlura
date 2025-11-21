## Explicación de la ejecución del pipeline
Este repositorio utiliza un pipeline de Integración Continua (CI) configurado con GitHub Actions para automatizar la validación del código, la ejecución de pruebas y la generación del artefacto final de la aplicación. El pipeline se ejecuta automáticamente cada vez que se realiza un push o una pull request hacia la rama main.

El flujo está compuesto por tres jobs que se ejecutan en orden secuencial. El primer job realiza la validación del estilo mediante Checkstyle. En esta etapa se descarga el repositorio, se configura la versión de Java requerida, se instala Maven y se ejecuta el comando que verifica las reglas de estilo del proyecto. Si esta validación falla, el pipeline se detiene y no continúa con los siguientes pasos.

Si el análisis de Checkstyle finaliza correctamente, se ejecuta el segundo job, encargado de realizar las pruebas unitarias y generar el reporte de cobertura utilizando JaCoCo. En este job se ejecuta el proceso de verificación de Maven, se genera el reporte de cobertura y finalmente se publica dicho reporte como artefacto descargable en GitHub Actions.

Una vez superadas las pruebas y generada la cobertura, se ejecuta el tercer job, encargado de compilar y empaquetar la aplicación. Aquí se ejecuta el comando de construcción de Maven omitiendo las pruebas, y el archivo JAR resultante se publica como artefacto para su descarga.

## Logs de un run exitoso y uno fallido
<img width="1091" height="489" alt="image" src="https://github.com/user-attachments/assets/9a627bfe-e789-405d-8b53-2ad4fca0260d" />
<img width="1089" height="337" alt="image" src="https://github.com/user-attachments/assets/ab4820eb-dabd-417f-9ffb-9ce5fb3032ee" />

## Diferencia entre CI y CD.
La Integración Continua (CI) es el proceso mediante el cual cada cambio realizado en el código se integra y valida automáticamente para asegurar que el proyecto permanezca en un estado funcional, mientras que la Entrega/Despliegue Continuo (CD) se encarga de automatizar los pasos posteriores, preparando o ejecutando el despliegue del software hacia entornos como pruebas o producción. En resumen, CI garantiza que el código nuevo no rompa nada y CD garantiza que ese código validado llegue rápida y continuamente a los usuarios.

## Lenguaje: Java 21
Justificación:
- Compatibilidad: Spring Boot 3.4.1 requiere Java 17+, Java 21 es la versión LTS más reciente
- Rendimiento: Mejoras significativas en performance y gestión de memoria
- Ecosistema: Amplia adopción en la industria para aplicaciones empresariales
- Características modernas: Records, pattern matching, mejoras en concurrencia

## Linter: Checkstyle
Justificación:
- Integración nativa: Soporte oficial con Maven a través de maven-checkstyle-plugin
- Estándares reconocidos: Configuración con google_checks.xml siguiendo convenciones ampliamente adoptadas
- Feedback inmediato: Detección temprana de problemas de estilo y posibles bugs
- Consistencia: Mantiene un código base uniforme entre todos los desarrolladores
- Configurabilidad: Reglas personalizables según las necesidades del proyecto

## Herramienta de Cobertura: JaCoCo
Justificación:
- Estandar en el ecosistema: Integración nativa con Maven y Spring Boot
- Reportes detallados: Genera reportes HTML interactivos y datos en múltiples formatos (XML, CSV)
- Umbrales configurables: Permite definir mínimos de cobertura por instrucción, línea, rama, etc.
- Integración CI: Fácil configuración para fallar el build si no se alcanzan los umbrales
- Compatibilidad: Funciona perfectamente con herramientas modernas como GitHub Actions

## Act
Act es una herramienta de línea de comandos de código abierto que permite ejecutar GitHub Actions workflows localmente. Simula el entorno de GitHub Actions en tu máquina local usando contenedores Docker, permitiendo probar y depurar workflows CI/CD sin necesidad de hacer push al repositorio.

## Comando para ejecutar el workflow localmente.
act -P ubuntu-latest=catthehacker/ubuntu:runner-latest

## Cómo identificar fallos de linter, pruebas y cobertura en logs
Se pueden identificar fallos en el pipeline revisando los logs de cada job: los errores de linter (Checkstyle) aparecen como mensajes [ERROR] indicando archivo, línea y regla incumplida; los fallos de pruebas unitarias (JUnit) muestran detalles como AssertionError, diferencias entre valores esperados y obtenidos, y resúmenes del tipo Tests run / Failures; y los problemas de cobertura (JaCoCo) se reconocen por mensajes que indican que no se alcanzó el porcentaje mínimo configurado o errores como Rule Violation o Coverage checks have not been met. Cada fallo detiene el pipeline en ese paso y GitHub Actions marca en rojo el punto exacto donde ocurrió.

## Generar un run fallido y uno exitoso y explicar la diferencia
Un run exitoso muestra todos los pasos del pipeline en color verde, indicando que el código pasó las validaciones de estilo, las pruebas unitarias y las reglas de cobertura, permitiendo finalmente generar el artefacto .jar; en cambio, un run fallido detiene la ejecución en el paso donde ocurrió el error y muestra un ícono rojo, generalmente acompañado de mensajes como [ERROR] en el linter, pruebas fallidas con AssertionError o incumplimiento de cobertura con Rule Violation. La diferencia principal es que en un run exitoso todos los jobs se completan y generan artefactos, mientras que en un run fallido el pipeline no continúa y no produce resultados finales.

## dos métodos para detectar código generado por IA.
El análisis de patrones estilísticos evalúa la consistencia en la indentación, el uso repetitivo de estructuras, comentarios genéricos y la ausencia de errores típicos humanos, características comunes en código producido por modelos de lenguaje. Otro método es el uso de detectores automáticos basados en modelos de clasificación, como herramientas entrenadas para distinguir texto humano del generado por IA mediante métricas de probabilidad, análisis de entropía y modelos supervisados que comparan el estilo del código con patrones conocidos de generación automática.

## ¿por qué no es posible asegurar al 100% la autoría?
No es posible asegurar al 100% la autoría de un código porque tanto los humanos como las IA pueden producir estilos similares, modificar su forma de escribir y adaptarse a patrones distintos; además, el código puede ser editado, mezclado, refactorizado o reutilizado, lo que difumina cualquier rastro original. Los detectores tampoco son infalibles: se basan en probabilidades, no en certezas, y pueden generar falsos positivos o negativos debido a la variabilidad del lenguaje, la diversidad de estilos de programación y la capacidad de los modelos de IA para imitar código humano.

## Políticas razonables de uso de IA en educación y calidad.
Una política razonable de uso de IA en educación y calidad debería fomentar el aprendizaje auténtico sin prohibir herramientas útiles. Se puede permitir el uso de IA como apoyo para explicaciones, retroalimentación, ejemplos, tutoría y revisión de estilo, siempre que el estudiante declare explícitamente cuándo la ha utilizado y cuál fue su aporte personal en la solución. La IA no debe emplearse para realizar trabajos completos, resolver evaluaciones, generar código sin comprensión o sustituir el razonamiento propio. Las instituciones deberían promover la transparencia, capacitar a los estudiantes en un uso ético, y complementar las actividades con evaluaciones donde se evidencie la comprensión individual (defensas orales, ejercicios en clase, entregas parciales). Además, se recomienda incluir mecanismos de revisión y acompañamiento para garantizar la calidad, enfocándose en la interpretación, el análisis crítico y la capacidad del estudiante para explicar lo que entrega, en lugar de depender únicamente de detectores automáticos de IA, que no son completamente fiables.
