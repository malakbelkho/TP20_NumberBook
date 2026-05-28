# ContactAura – TP 20

Application Android permettant de lire les contacts enregistrés dans un smartphone, de les afficher dans une interface moderne, puis de les synchroniser vers une base de données distante à travers une API PHP consommée avec Retrofit.

## Objectif:

Le but de ce laboratoire est de :

- Comprendre comment Android accède aux contacts du téléphone avec `ContentResolver`
- Demander et gérer dynamiquement la permission `READ_CONTACTS`
- Récupérer les noms et numéros enregistrés dans le smartphone
- Afficher les contacts dans une liste moderne avec `RecyclerView`
- Communiquer avec une API distante en utilisant Retrofit
- Convertir automatiquement les objets Java en JSON avec Gson
- Stocker les contacts dans une base MySQL distante
- Rechercher des contacts par nom ou par numéro depuis l’application
- Comprendre l’architecture complète mobile + backend + base de données

## Description de l’application:

L’application **ContactAura** est une version personnalisée du concept **Number Book**.

Elle contient :

- Une interface Android moderne et colorée
- Un bouton pour charger les contacts du téléphone
- Un bouton pour synchroniser les contacts vers le serveur
- Un champ de recherche par nom ou numéro
- Un bouton de recherche distante
- Une liste dynamique des contacts avec `RecyclerView`
- Une API PHP connectée à une base MySQL
- Une base de données `numberbook` contenant les contacts synchronisés

L’application fonctionne selon ce scénario :

1. L’utilisateur ouvre l’application
2. L’application demande la permission d’accès aux contacts
3. Les contacts du téléphone sont lus avec `ContentResolver`
4. Les contacts sont affichés dans une liste moderne
5. L’utilisateur lance la synchronisation
6. Les contacts sont envoyés vers l’API distante avec Retrofit
7. Le serveur PHP insère les données dans MySQL
8. L’utilisateur peut rechercher un contact distant par nom ou numéro
9. Les résultats retournés par le serveur sont affichés dans l’application

## Fonctionnalités:

- Lecture des contacts locaux du téléphone
- Demande de permission `READ_CONTACTS`
- Affichage dynamique avec `RecyclerView`
- Synchronisation des contacts vers un backend distant
- Communication HTTP avec Retrofit
- Conversion JSON avec Gson
- Insertion des contacts dans MySQL
- Recherche distante par nom ou numéro
- Nettoyage simple des numéros de téléphone
- Prévention des doublons grâce à une contrainte unique sur le numéro
- Interface visuelle personnalisée avec :
  - Dégradé de fond
  - Cartes arrondies
  - Boutons colorés
  - Header moderne
  - Badge de statut
  - ProgressBar de chargement

## Technologies utilisées:

- Android Studio
- Java
- XML
- Retrofit
- Gson Converter
- RecyclerView
- PHP
- PDO
- MySQL
- phpMyAdmin
- XAMPP
- API REST
- JSON

## Aperçu de l’application:

▶️ Une démonstration vidéo complète est disponible dans le dossier **Demo** du repository.

⚠️ En cas de problème de lecture :

👉 [▶️ Voir la démo sur Google Drive](https://)

## Captures du backend et de la base de données:

<p align="center">
  <img width="48%" alt="XAMPP Apache MySQL running" src="https://github.com/user-attachments/assets/6c87819e-ca97-42a7-9e21-9520a805ea2d" />
<img width="48%" alt="Création de la base numberbook" src="https://github.com/user-attachments/assets/cba360ce-e50a-4ce6-8790-e7386ccbe626" />
</p>


<p align="center">
  <img width="48%" alt="Table contact vide" src="https://github.com/user-attachments/assets/bf56d5df-56a6-4727-a611-a4d33ef95e6d" />
  <img width="48%" alt="Dossier numberbook-api" src="https://github.com/user-attachments/assets/35635d39-fc5c-40e5-8ae1-665db7a200af" />
</p>

<p align="center">
  <img width="48%" alt="Dossier api" src="https://github.com/user-attachments/assets/aac5c5dc-cc69-4579-9895-50d85d64c4f0" />
  <img width="48%" alt="Dossier config" src="https://github.com/user-attachments/assets/d2ba8c11-b4fe-431c-b81e-ab2b23f92d22" />
</p>

<p align="center">
  <img width="48%" alt="Dossier repository" src="https://github.com/user-attachments/assets/b51db1ef-d5ee-48b2-8b97-da5aedb1047f" />
</p>



## Structure du projet Android:

### `MainActivity.java`

Cette classe représente l’écran principal de l’application.

Elle permet de :

- Initialiser les composants graphiques
- Gérer les boutons de l’interface
- Demander la permission `READ_CONTACTS`
- Lire les contacts du téléphone
- Afficher les contacts dans le `RecyclerView`
- Envoyer les contacts au serveur
- Lancer une recherche distante
- Afficher l’état de chargement et les messages utilisateur

Les principales méthodes sont :

- `verifyPermissionThenLoad()` : vérifie si la permission contacts est accordée
- `loadPhoneContacts()` : charge les contacts du téléphone
- `syncVisibleContacts()` : synchronise les contacts vers le serveur
- `searchFromServer()` : recherche les contacts dans la base distante
- `normalizePhone()` : nettoie le numéro de téléphone
- `setLoading()` : affiche ou masque la ProgressBar

### `PhonePerson.java`

Cette classe représente un contact côté Android.

Elle contient les informations suivantes :

- `remoteId`
- `displayName`
- `phoneNumber`
- `origin`
- `createdAt`

Elle utilise `@SerializedName` pour faire correspondre les noms Java avec les champs JSON renvoyés par le serveur.

Exemple :

```java
@SerializedName("name")
private String displayName;
```
Cela permet de garder des noms de variables personnalisés côté Android tout en restant compatible avec l’API PHP.

### `ServerReply.java`

Cette classe représente la réponse envoyée par le serveur après une opération d’insertion.

Elle contient :

- `success`
- `message`

Exemple de réponse JSON :

```json
{
  "success": true,
  "message": "Contact enregistré"
}
```

### `DirectoryApi.java`

Cette interface définit les routes de l’API utilisées par Retrofit.

Elle contient :

- `uploadPerson()` : envoie un contact vers le serveur
- `loadRemoteDirectory()` : récupère tous les contacts distants
- `searchRemotePeople()` : recherche un contact distant par mot-clé

Les annotations utilisées sont :

- `@POST`
- `@GET`
- `@Body`
- `@Query`

### `ApiHub.java`

Cette classe configure Retrofit.

Elle contient l’URL de base de l’API :

```java
private static final String BASE_URL = "http://10.0.2.2/numberbook-api/api/";
```

Pour l’émulateur Android, `10.0.2.2` permet d’accéder au serveur local du PC.

Pour un téléphone physique, il faut remplacer cette adresse par l’adresse IP locale du PC, par exemple :

```java
private static final String BASE_URL = "http://192.168.1.10/numberbook-api/api/";
```

### `PeopleAdapter.java`

Cette classe relie les données des contacts à l’interface graphique.

Elle permet de :

- Créer les lignes de la liste
- Remplir chaque carte avec le nom et le numéro
- Afficher une bulle avec la première lettre du contact
- Mettre à jour les données affichées avec `refreshWith()`

## Structure des layouts Android:

### `activity_main.xml`

Ce fichier contient l’écran principal de l’application.

Il inclut :

- Un header avec le nom de l’application
- Un badge de statut
- Un bouton pour charger les contacts
- Un bouton pour synchroniser les contacts
- Un champ de recherche
- Un bouton pour chercher dans la base distante
- Une ProgressBar
- Un RecyclerView

### `item_aura_contact.xml`

Ce fichier représente une ligne de contact dans le `RecyclerView`.

Il contient :

- Une bulle avatar
- Le nom du contact
- Le numéro du contact
- Une carte arrondie avec un design moderne

## Design de l’application:

Les fichiers dans `res/drawable` permettent de personnaliser l’interface.

### `bg_aura_screen.xml`

Fond principal de l’application avec un dégradé coloré.

### `bg_header_aura.xml`

Carte supérieure contenant le titre et la description de l’application.

### `bg_button_primary.xml`

Bouton principal avec un dégradé violet et rose.

### `bg_button_secondary.xml`

Bouton secondaire avec un dégradé cyan et vert.

### `bg_search_box.xml`

Fond personnalisé pour le champ de recherche.

### `bg_contact_card.xml`

Carte utilisée pour afficher chaque contact dans la liste.

### `bg_status_chip.xml`

Badge indiquant l’état actuel de l’application.

## Structure du backend PHP:

Le backend est placé dans le dossier :

```txt
C:\xampp\htdocs\numberbook-api
```

Structure utilisée :

```txt
numberbook-api/
│
├── config/
│   └── Database.php
│
├── repository/
│   └── PhoneDirectoryRepository.php
│
└── api/
    ├── insertContact.php
    ├── getAllContacts.php
    └── searchContact.php
```

### `config/Database.php`

Ce fichier gère la connexion à la base de données MySQL avec PDO.

Il définit :

- Le serveur MySQL
- Le nom de la base
- L’utilisateur
- Le mot de passe
- Les options PDO
- La gestion des erreurs de connexion

### `repository/PhoneDirectoryRepository.php`

Ce fichier contient la logique d’accès aux données.

Il permet de :

- Insérer un contact
- Récupérer tous les contacts
- Rechercher un contact par nom ou numéro

La méthode d’insertion utilise :

```sql
ON DUPLICATE KEY UPDATE
```

Cela permet d’éviter les doublons lorsque le même numéro est synchronisé plusieurs fois.

### `api/insertContact.php`

Ce endpoint reçoit un contact envoyé par Android en JSON.

Il vérifie :

- Que la méthode HTTP est bien `POST`
- Que les champs `name` et `phone` existent
- Que les données peuvent être insérées dans MySQL

Il retourne une réponse JSON indiquant si l’opération a réussi.

### `api/getAllContacts.php`

Ce endpoint retourne tous les contacts enregistrés dans la base.

Exemple d’URL :

```txt
http://localhost/numberbook-api/api/getAllContacts.php
```

### `api/searchContact.php`

Ce endpoint permet de rechercher un contact par nom ou numéro.

Exemple d’URL :

```txt
http://localhost/numberbook-api/api/searchContact.php?keyword=malak
```

## Base de données:

La base utilisée s’appelle :

```sql
numberbook
```

La table principale s’appelle :

```sql
contact
```

Structure de la table :

```sql
CREATE TABLE IF NOT EXISTS contact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    source VARCHAR(50) DEFAULT 'mobile',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_phone (phone)
);
```

### Rôle des colonnes:

- `id` : identifiant unique du contact
- `name` : nom du contact
- `phone` : numéro de téléphone
- `source` : origine du contact
- `created_at` : date d’insertion
- `updated_at` : date de dernière modification

Le numéro est stocké sous forme de chaîne de caractères car il peut contenir :

- un indicatif international
- des espaces
- des tirets
- le symbole `+`

## Tests réalisés:

### Test 1 — Vérification de XAMPP

Apache et MySQL doivent être démarrés dans XAMPP.

Résultat attendu :

```txt
Apache : Running
MySQL : Running
```

### Test 2 — Vérification de la base de données

Dans phpMyAdmin :

```sql
SELECT * FROM contact;
```

Résultat attendu au début :

```txt
MySQL a retourné un résultat vide
```

Cela signifie que la table existe mais qu’aucun contact n’a encore été inséré.

### Test 3 — Vérification du dossier backend

Dans le navigateur :

```txt
http://localhost/numberbook-api/
```

Résultat attendu :

```txt
Index of /numberbook-api
```

Le navigateur doit afficher les dossiers :

```txt
api/
config/
repository/
```

### Test 4 — Vérification des endpoints API

Dans le navigateur :

```txt
http://localhost/numberbook-api/api/
```

Résultat attendu :

```txt
getAllContacts.php
insertContact.php
searchContact.php
```

### Test 5 — Lecture des contacts depuis l’API

URL :

```txt
http://localhost/numberbook-api/api/getAllContacts.php
```

Résultat attendu si la base est vide :

```json
[]
```

## Test 6 — Insertion avec REST Client

Pour tester l’insertion d’un contact dans la base de données, l’extension Chrome **REST Client** a été utilisée.

Méthode utilisée :

```txt
POST
```

URL utilisée :

```txt
http://localhost/numberbook-api/api/insertContact.php
```

Corps de la requête au format JSON :

```json
{
  "name": "TEEEESTTT",
  "phone": "+212600000000"
}
```

Résultat obtenu :

```json
{
  "success": true,
  "message": "Contact enregistré"
}
```

Ce résultat confirme que l’API `insertContact.php` reçoit correctement les données JSON envoyées par le client REST, puis les insère dans la table `contact` de la base MySQL.

<p align="center">
  <img width="90%" alt="Test insertion contact avec REST Client" src="https://github.com/user-attachments/assets/c78519e4-4767-4f3e-b9e2-81e152c33fec" />
</p>

### Test 7 — Recherche distante

URL :

```txt
http://localhost/numberbook-api/api/searchContact.php?keyword=Malak
```

Résultat attendu :

```json
[
  {
    "id": 1,
    "name": "Malak Test",
    "phone": "+212600000000",
    "source": "mobile",
    "created_at": "...",
    "updated_at": "..."
  }
]
```

### Test 8 — Chargement des contacts Android

Dans l’application :

```txt
Charger les contacts du téléphone
```

Résultat attendu :

- Une demande de permission apparaît
- Après acceptation, les contacts du téléphone sont affichés
- Le badge de statut indique le nombre de contacts chargés

### Test 9 — Synchronisation vers le serveur

Dans l’application :

```txt
Synchroniser vers le serveur
```

Résultat attendu :

- Les contacts sont envoyés vers l’API
- Les données apparaissent dans phpMyAdmin
- Le badge de statut affiche le nombre de contacts synchronisés

### Test 10 — Recherche depuis Android

Dans le champ de recherche :

```txt
Malak
```

ou :

```txt
600
```

Résultat attendu :

- L’application envoie une requête GET vers le serveur
- Les résultats retournés par MySQL sont affichés dans le RecyclerView

## Remarque importante:

Un contact ajouté directement dans phpMyAdmin est ajouté uniquement dans la base MySQL.

Il ne sera pas ajouté automatiquement dans l’application Contacts de l’émulateur Android.

Il existe donc deux espaces différents :

```txt
Contacts de l’émulateur Android
→ lus par ContentResolver

Base MySQL phpMyAdmin
→ utilisée pour la recherche distante
```

Pour tester le chargement local, il faut ajouter un contact dans l’application Contacts de l’émulateur.

Pour tester la recherche distante, on peut insérer un contact dans phpMyAdmin ou avec REST Client.

## Problèmes rencontrés:

### Problème 1 — Table vide dans phpMyAdmin

Message affiché :

```txt
MySQL a retourné un résultat vide
```

Ce message n’est pas une erreur.

Il signifie simplement que la table `contact` existe, mais qu’elle ne contient pas encore de données.

### Problème 2 — Dossier API introuvable

Le dossier `numberbook-api` doit être créé manuellement dans :

```txt
C:\xampp\htdocs
```

Chemin final attendu :

```txt
C:\xampp\htdocs\numberbook-api
```

### Problème 3 — Mauvaise URL dans Android

Dans l’émulateur, il ne faut pas utiliser :

```txt
http://localhost/...
```

Il faut utiliser :

```txt
http://10.0.2.2/...
```

Car `10.0.2.2` représente l’ordinateur hôte depuis l’émulateur Android.

### Problème 4 — Contact ajouté dans phpMyAdmin non visible dans l’émulateur

Un contact inséré dans phpMyAdmin est stocké dans MySQL.

Il ne devient pas automatiquement un contact local Android.

Pour qu’il soit chargé avec `ContentResolver`, il doit exister dans l’application Contacts de l’émulateur.

## Sécurité et bonnes pratiques:

Ce TP applique plusieurs bonnes pratiques :

- Utilisation de requêtes préparées avec PDO
- Communication entre Android et le serveur en JSON
- Vérification des champs avant insertion
- Gestion des permissions Android
- Appels réseau asynchrones avec Retrofit
- Séparation des responsabilités côté backend
- Utilisation d’une contrainte unique sur le numéro pour éviter les doublons
- Nettoyage des numéros avant l’affichage et l’envoi

## Architecture globale:

```txt
Application Android
        |
        | Retrofit
        v
API PHP dans XAMPP
        |
        | PDO
        v
Base MySQL numberbook
        |
        v
Table contact
```

## Flux de fonctionnement:

```txt
Utilisateur
   |
   v
Ouvre ContactAura
   |
   v
Accepte la permission READ_CONTACTS
   |
   v
Android lit les contacts avec ContentResolver
   |
   v
RecyclerView affiche les contacts
   |
   v
Utilisateur clique sur Synchroniser
   |
   v
Retrofit envoie les contacts en JSON
   |
   v
PHP reçoit les données
   |
   v
PDO insère les contacts dans MySQL
   |
   v
Utilisateur recherche un contact
   |
   v
PHP retourne les résultats JSON
   |
   v
Android affiche les résultats
```

## Différence entre contacts locaux et contacts distants:

| Élément | Emplacement | Utilisé par |
|---|---|---|
| Contacts locaux | Émulateur ou téléphone Android | Bouton Charger les contacts |
| Contacts distants | Base MySQL dans phpMyAdmin | Recherche distante |
| API PHP | Dossier `htdocs/numberbook-api` | Communication Android ↔ serveur |

## Conclusion:

Ce laboratoire permet de développer une application Android complète connectée à un backend distant.

Il combine plusieurs notions importantes du développement mobile moderne :

- Accès aux données système Android
- Gestion des permissions
- Affichage dynamique avec RecyclerView
- Communication HTTP avec Retrofit
- Sérialisation et désérialisation JSON
- Développement d’une API PHP
- Connexion à une base MySQL
- Recherche distante
- Organisation d’un mini projet client-serveur

Le projet **ContactAura** constitue une base solide pour comprendre comment une application mobile peut communiquer avec un serveur distant afin de synchroniser et consulter des données en temps réel.
