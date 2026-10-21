# ShakeUp

**L’entraînement et la nutrition, enfin connectés à votre coach.**

ShakeUp est une application SaaS B2B2C conçue pour les coachs sportifs et leurs élèves. Elle permet de structurer les entraînements, d’ajuster les macros et de centraliser le suivi — de la salle à l’assiette.

Ce projet full-stack démontre une architecture en **quatre briques** (DB · API · vitrine/PWA élève · back-office coach), une API centralisée sécurisée, et la séparation claire des parcours utilisateurs (coach vs élève) dans un monorepo.

---

## Le concept & parcours utilisateurs

L’application est scindée pour deux contextes d’usage radicalement différents.

### Pour le coach — desktop first (`backoffice/`)

Un back-office pour piloter l’accompagnement : création des programmes, consignes nutrition, suivi des élèves et catalogues d’exercices / aliments. La feuille de route produit prévoit aussi les métriques avancées (feux tricolores, alertes de fatigue) et une inbox de contacts issus de la landing.

### Pour l’élève — mobile first / PWA (`frontend/`)

Une expérience pensée pour la salle : programmes assignés, journal nutritionnel au gramme près, retours coach. Vision produit : mode *Live Session* (minuteur dynamique, calculateur de charge, *ghost data* des perfs passées). Aujourd’hui : landing publique, glossaire produit (`/features`), auth élève et shell PWA.

### Vitrine publique

La landing s’adresse d’abord au pratiquant (acquisition + rassurance). Un glossaire multi-pages (lazy-loaded) explique musculation, nutrition et PWA. Le coach accède à son bureau sur un host séparé — le même découpage qu’en production type Railway (`shakeup.fr` / `app` / `pro` / `api`).

---

## Stack technique & architecture

| Brique | Techno | Rôle |
|--------|--------|------|
| **Base de données** | PostgreSQL (Docker Compose + pgAdmin) | Relations programmes, logs, nutrition, leads contact |
| **Back-end** | Java · Spring Boot 3 · JPA · Spring Security · JWT | API REST `/api/v1` — étanchéité `COACH` / `ELEVE` |
| **Front élève + landing** | Angular 19 · Tailwind · PWA | Vitrine, `/features`, connexion et shell élève |
| **Back-office** | Angular 19 · Tailwind | Login coach et bureau de suivi |

**Pourquoi ce découpage :** un recruteur voit immédiatement que landing ≠ app élève ≠ admin coach, avec une seule API et une seule base — déploiement local calqué sur une topologie cloud (Railway, etc.).

---

## Guide de démarrage (run locally)

### 1. Infrastructure (base de données)

```bash
docker compose up -d
```

| Service | Accès |
|---------|--------|
| PostgreSQL | `localhost:5432` — user / mdp / db : `shakeup` |
| pgAdmin | http://localhost:5050 — `admin@shakeup.local` / `admin` |

> Si le port 5432 est déjà pris (Homebrew) : `brew services stop postgresql@14`.

### 2. Back-end (API Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

- Health : http://localhost:8080/api/v1/health  
- Auth : `POST /api/v1/auth/login`

### 3. Front — vitrine & app élève

```bash
cd frontend
npm start
```

http://localhost:4200 — landing, **Découvrir l’application** (`/features`), connexion élève, shell PWA.

### 4. Front — back-office coach

```bash
cd backoffice
npm start
```

http://localhost:4300 — login coach et bureau de suivi.

---

## Tester l’application

Un **jeu de données de démonstration (seed)** est chargé au démarrage de l’API — pratique portfolio pour tester les rôles sans inscription.

| Rôle | Email | Mot de passe | Où se connecter |
|------|-------|--------------|-----------------|
| **Coach** | `coach@shakeup.local` | `coach123` | http://localhost:4300/login |
| **Élève** | `eleve@shakeup.local` | `eleve123` | http://localhost:4200/login |

Raccourci coach prérempli : http://localhost:4300/login?demo=1

Données seed liées à l’élève démo : programme **Séance A** (squat, développé, gainage) + consignes nutrition (2450 kcal).

Parcours suggéré pour un recruteur (≈ 3 minutes) :

1. Landing + glossaire `/features` (`:4200`)  
2. Back-office coach → voir l’élève **Camille Demo** (`:4300`)  
3. Déconnexion → login élève → programme + consignes sur le shell mobile (`:4200/login`)

---

## DevSecOps (socle & suite)

| En place | À venir (feuille de route) |
|----------|----------------------------|
| Postgres + pgAdmin conteneurisés (`docker compose`) | Pipeline GitHub Actions (build front + back) |
| Secrets hors repo, JWT, rôles séparés, CORS borné | SAST / SCA, scan image (Trivy), badges |
| Monorepo lisible, README + notes `.cursor/` | Compose « full stack », déploiement Railway |

L’objectif n’est pas seulement de livrer une feature : c’est de montrer qu’on sait **faire tourner**, **séparer les surfaces**, et **préparer** la chaîne CI/sécurité comme en entreprise.

---

## Structure du dépôt

```text
ShakeUp/
├── backend/          # API Spring Boot
├── frontend/         # Landing + PWA élève (:4200)
├── backoffice/       # Back-office coach (:4300)
├── docker-compose.yml
└── .cursor/          # Notes produit / design (local)
```
