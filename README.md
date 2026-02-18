# Flahasmart

Application de gestion de stock agricole développée avec JavaFX.

## 📋 Description
Cette application permet de gérer les stocks agricoles avec une interface utilisateur moderne et intuitive.

## ✨ Fonctionnalités
- Gestion des stocks agricoles
- Interface utilisateur avec JavaFX et FXML
- Design responsive avec CSS
- Architecture MVC

## 🛠️ Technologies utilisées
- **Java 17** - Langage de programmation
- **JavaFX** - Framework d'interface utilisateur
- **Maven** - Gestionnaire de dépendances
- **CSS** - Style et design
- **Git** - Versionnement

## 📁 Structure du projet
flahasmart/
├── .mvn/ # Configuration Maven wrapper
├── src/
│ ├── main/
│ │ ├── java/ # Code source Java
│ │ │ └── com/example/flahasmart/
│ │ │ ├── controllers/ # Contrôleurs JavaFX
│ │ │ ├── entities/ # Entités métier
│ │ │ ├── services/ # Services
│ │ │ └── utils/ # Utilitaires
│ │ └── resources/ # Ressources (FXML, CSS)
│ │ └── com/example/flahasmart/
│ │ ├── css/ # Feuilles de style
│ │ ├── AgrichStock.fxml # Interface agricole
│ │ └── BackStock.fxml # Interface back-office
├── .gitignore # Fichiers ignorés par Git
├── mvnw # Maven wrapper (Linux/Mac)
├── mvnw.cmd # Maven wrapper (Windows)
└── pom.xml # Configuration Maven
## 🚀 Installation et exécution

### Prérequis
- Java 17 ou supérieur
- Maven (ou utilisez le wrapper inclus)

### Étapes d'installation

1. **Cloner le dépôt**
```bash
git clone https://github.com/boshbosh2025/flahasmart.git
cd flahasmart
