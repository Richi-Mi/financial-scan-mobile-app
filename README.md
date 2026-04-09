# Financial Scan 🚀

**Financial Scan** es una aplicación Android moderna diseñada para ayudar a los usuarios a llevar un control inteligente de sus gastos personales. Utiliza Inteligencia Artificial (IA) y Reconocimiento Óptico de Caracteres (OCR) para automatizar el registro de tickets y ofrecer análisis financieros detallados.

## Link del repositorio de Backend.
[Repositorio de la API de Financial Scan](https://github.com/AlexisInsMu/financial_scan_backend)

## 🌟 Características Principales

*   **Escaneo de Tickets (OCR):** Utiliza Google ML Kit Document Scanner para digitalizar tickets físicos. El texto extraído es procesado por un backend con IA para identificar automáticamente el comercio, los productos, precios y categorías.
*   **Análisis de Gastos Hormiga:** Identificación inteligente de pequeños gastos recurrentes que afectan la salud financiera a largo plazo.
*   **Dashboard Visual:** Gráficos de distribución por categorías (comida, transporte, entretenimiento, etc.) y resumen de gastos mensuales.
*   **Recordatorios Inteligentes:** Sistema de notificaciones diarias programadas para incentivar el registro de gastos.
*   **Perfil Personalizado:** Pantalla de bienvenida para el registro inicial del usuario y seguimiento de su "Score Financiero".
*   **Arquitectura Robusta:** Desarrollada con **Jetpack Compose**, **Hilt (Inyección de dependencias)**, **Room (Base de datos local)** y **Retrofit (Consumo de API)**.

## 🛠️ Tecnologías Utilizadas

*   **Lenguaje:** Kotlin
*   **UI Framework:** Jetpack Compose con Material 3.
*   **Inyección de Dependencias:** Dagger Hilt.
*   **Persistencia de Datos:** Room Database para tickets y DataStore para preferencias de usuario.
*   **Networking:** Retrofit & OkHttp para la comunicación con el backend de IA.
*   **IA & ML:** 
    *   Google ML Kit para OCR y escaneo de documentos.
    *   Integración con modelos de lenguaje (LLM) para el análisis semántico de los gastos.
*   **Programación de Tareas:** AlarmManager para recordatorios diarios.
*   **Carga de Imágenes:** Coil.

## 📂 Estructura del Proyecto

*   `data/`: Contiene la lógica de acceso a datos, incluyendo la base de datos Room (`TicketDao`, `TicketEntity`), clientes de API (`FinancialScanApiService`) y repositorios.
*   `ui/`:
    *   `presentation/`: Organizado por pantallas (Home, OCR, Welcome, Profile, History). Cada una sigue el patrón MVVM.
    *   `components/`: Componentes de UI reutilizables como `TicketItem`.
    *   `navigation/`: Gestión de la navegación con Compose Navigation.
    *   `theme/`: Definición de la paleta de colores de Material 3, tipografía y formas.
*   `utils/`: Clases de apoyo para notificaciones (`NotificationHelper`, `NotificationScheduler`).
*   `helpers/`: Utilidades para OCR y reconocimiento de voz.

## 🚀 Cómo Empezar

1.  Clona el repositorio.
2.  Asegúrate de tener la última versión de Android Studio (Ladybug o superior).
3.  Sincroniza el proyecto con Gradle.
4.  Ejecuta la aplicación en un dispositivo físico o emulador con acceso a servicios de Google Play (necesario para el Scanner de ML Kit).

---
Desarrollado con ❤️ para mejorar tu salud financiera.
