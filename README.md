## diferencia entre CI y CD.
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
