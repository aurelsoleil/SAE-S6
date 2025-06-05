- Attention à bien build et lancer l'application en Java 1.8
- `docker compose up` permet de lancer une base de données

## Swagger
- http://localhost:8045/hospital/swagger-ui.html

## Feature [aurelsoleil/SAE-S6#26](https://github.com/aurelsoleil/SAE-S6/issues/26)

### Vérification de l'intégrité des factures

Le système de facturation utilise un mécanisme de chaînage et de hash pour garantir l'intégrité de toutes les factures. Chaque facture contient un hash calculé à partir de ses propres données et d'un sel secret (salt). 
Cela permet de détecter toute modification frauduleuse ou corruption.

Pour vérifier l'intégrité, utilisez l'endpoint `/billing/integrity-report` Le rapport indique pour chaque facture si l'intégrité est respectée (`integrityOk: true`) ou non.

#### Configuration du salt (BILL_HASH_SECRET)

Le sel utilisé pour le calcul du hash est défini par la variable d'environnement `BILL_HASH_SECRET`. Pour garantir la sécurité et la reproductibilité des vérifications d'intégrité :

- Définissez la variable d'environnement avant de lancer l'application :

```bash
export BILL_HASH_SECRET="votre-salt-secret"
```

- Si la variable n'est pas définie, une valeur par défaut peu sécurisée sera utilisée (`default-secret`).

Assurez-vous d'utiliser le même salt pour toutes les opérations de facturation et de vérification d'intégrité.

