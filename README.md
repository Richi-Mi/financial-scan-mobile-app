# Finantial Scan 🧾📱

**Financial Scan** es una aplicación moderna de Android diseñada para ayudar a los usuarios a gestionar sus finanzas personales mediante el escaneo y la extracción de información de recibos y tickets.

## 🚀 Características

- **Escaneo Inteligente**: Captura de recibos usando CameraX y extracción de texto con Google ML Kit OCR.
- **Historial de Gastos**: Mantén un registro de todos tus documentos escaneados en una base de datos local.
- **Recordatorios en Segundo Plano**: Notificaciones automatizadas mediante WorkManager para recordar a los usuarios registrar sus gastos.
- **Interfaz Moderna**: Construida totalmente con Jetpack Compose siguiendo las guías de diseño de Material 3.

## 🛠 Stack Tecnológico

- **Lenguaje**: [Kotlin](https://kotlinlang.org/)
- **Framework de UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Inyección de Dependencias**: [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Base de Datos Local**: [Room](https://developer.android.com/training/data-storage/room)
- **Redes**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **IA/ML**: 
    - [Google ML Kit OCR](https://developers.google.com/ml-kit/vision/text-recognition)
- **Tareas en Segundo Plano**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Navegación**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Carga de Imágenes**: [Coil](https://coil-api.github.io/coil/)

## 📂 Estructura del Proyecto

El proyecto sigue los principios de Clean Architecture:

- `data/`: Implementación de repositorios, entidades de Room, DAOs y servicios de API con Retrofit.
- `domain/`: Lógica de negocio e interfaces de repositorios.
- `ui/`: Capa de interfaz de usuario que contiene:
    - `presentation/`: Funciones Composables específicas de pantalla y ViewModels (Inicio, Historial, Escáner, etc.).
    - `components/`: Elementos de UI reutilizables.
    - `navigation/`: Grafos y envoltorios de navegación.
    - `theme/`: Configuración del tema Material 3.
- `di/`: Módulos de inyección de dependencias.
- `worker/`: Workers en segundo plano para notificaciones y recordatorios.

## ⚙️ Configuración

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/financial-scan-mobile-app.git
   ```
2. Abre el proyecto en **Android Studio (Ladybug o superior)**.
3. Sincroniza Gradle y construye el proyecto.
4. Ejecuta la aplicación en un dispositivo físico o emulador (API 24+).

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - mira el archivo [LICENSE](LICENSE) para más detalles.
