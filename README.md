# Approche utilisée pour la réalisation de l'exercice :

A été choisi de partir de l'application de démonstration pour la faire évoluer par itérations successives vers la cible. Cette cible devant s'approcher d'une notion de MVP tout en gardant une compatibilité avec l'origine.

## Stratégie de test
Mise en place dès la première itération, d'une stratégie de test pour assurer une couverture de test à plus de 80% basée sur deux niveaux :
Cette stratégie est basée sur deux niveaux de tests.
- Les unitaires sur le code métier avec une approche DBB, pour valider les fonctionnalités et la non-régression.
- Les tests sur l'API REST, afin de vérifier en profondeur l'intégration.
  Un soin particulier a été pris pour rendre les tests le plus simples et lisible, le but avoué étant de faciliter la maintenance et leurs adoptions.
  Chaque cas d'erreur est testé sur les deux niveaux, ce qui implique une gestion d'erreurs commune entre les APIs.

###Test BDD du code métier :
Le code métier est testé en simulant la couche de persistance par des MOCKs
- les éléments du langage **Gherkins** (**GIVEN, WHEN, THEN**) ont été repris pour l'écriture des tests.
- les vérifications sont faites directement sur les objets métiers.

```shell
mvn test -DfailIfNoTests=false -Dtest=ShopCoreImplTest -pl shop-core -am
```

###Test d'intégration sur l'API
- Les tests d'intégration sont réalisés sur toutes la couches entre l'API REST et la base de données sans aucune simulation (sans MOCK).
- Ce test a été directement packagé dans la version de test du projet.
- Comme pour le test du code métier le langage Gherkins a été repris.
- La librairie HttpClient d'Apache a été utilisé pour tester la méthode **PATCH**
- Les méthodes **equals** ont été ajoutés sur les DTOs, pour permettre de réaliser les tests directement sur les DTOs.

```shell
mvn test -DfailIfNoTests=false -Dtest=ApiTest -pl controller -am
```


## Choix de la persistance
- La base de données choisie est HSQLDB en mode embarquée.
- Le modèle choisi est basé sur une approche additive, par ajout d'enregistrement et ajout de colonnes sans destruction d'information, ce qui permettra à terme de gérer ***l'historique des mouvements***, les ***retours de stock***.
- Le choix mode de persistance additive permet d'introduire la notion d'événement, ici inputDate et outputDate permet d'enregistrer les dates des événements d'entrée et de sortie du stock.
- Le binding bidirectionnel est utilisé pour éviter les problèmes de **SQL injection**.


## Approche utilisée pour le code métier
- Utiliser un découplage fort entre le code métier et la persistance (Packages et modules différents).
- Le parti pris a été fait d'orienter le code métier sur l'usage plutôt que sur le modèle de persistance.
- Dans ce sens le code métier est basé plus sur les DTO que sur la notion de table propre à la persistance.

# Première itération.
- Pour la première itération la modélisation de l'application a été réalisé sur une seule table SHOES_STOCK.
- Le catalogue est constitué par tous les modèles déjà enregistrés dans les stocks.
- **accept-single-value-as-array** a été utilisé pour implémenter la mise à jour du stock en simple ou multilignes.
- Dans le cas d'un update de Stock sur plusieurs lignes, les entrées sont traitées en priorité.

# Itérations suivantes :
- Séparation du modèle du catalogue de celui du stock (modèle sur deux tables), pour préparer le cas échéant un découpage en micron service. Ce qui motive ce découpage est le fait que les gouvernances du catalogue et du stock sont différentes dans un réseau de magasin, car le stock est géré par magasin, tandis que le catalogue est partagé dans le réseau.
- Séparation des deux API Stock et Catalogue, ce qui prépare le découpage en micro-service.
- Ajout des méthodes assurant la gestion du catalogue.
- Renforcement de la notion de ressource avec leur identifiant, ce qui renforce le futur découpage en micro-service.
- Documentation des APIs avec Swagger par annotation du code source.

## test

### Test du code métier
```shell
mvn test -DfailIfNoTests=false -Dtest=ShopCoreImplTest -pl shop-core -am
```

### Test de l'API
```shell
mvn test -DfailIfNoTests=false -Dtest=ApiTest -pl controller -am
```

### Manuel avec CURL


| Test            | Command  |
| ------------------ | --------------------------------------------------------- |
| Start backend | ```java -jar controller/target/controller-1.0.jar``` |
| Swagger Doc |  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| search V1 | ```curl -X GET "http://localhost:8080/shoes/search" -H "version: 1" ``` |
| search V2 | ```curl -X GET "http://localhost:8080/shoes/search" -H "version: 2" ``` |
| get catalog | ```curl -X GET "http://localhost:8080/shop/catalog" -H "version: 3" ``` |
| add model | ```curl -X PUT "http://localhost:8080/shop/catalog" -H "version: 3" -H "Content-Type: application/json" -d '{ "name": "Shop shoe", "size": 45, "color": "BLACK"}'``` |
| get model by ID | ```curl -X GET "http://localhost:8080/shop/catalog/0" -H "version: 3" ``` |
| get stock | ```curl -X GET "http://localhost:8080/shop/stock" -H "version: 3" ``` |
| update stock single line | ```curl -X PATCH "http://localhost:8080/shop/stock" -H "version: 3" -H "Content-Type: application/json" -d '{ "name":  "Shop shoe", "size": 40, "color": "BLACK", "quantity": -1 }'``` |
| update stock multi-lines | ```curl -X PATCH "http://localhost:8080/shop/stock" -H "version: 3" -H "Content-Type: application/json" -d '[{ "name": "Shop shoe", "size": 40, "color": "BLACK", "quantity": -1 },{ "name": "Shop shoe", "size": 39, "color": "BLUE", "quantity": -2 }]'``` |
| update stock single line en mode REST Full | ```curl -X PATCH "http://localhost:8080/shop/rest/stock" -H "version: 3" -H "Content-Type: application/json" -d '{ "model_id" :0, "quantity": 1 }'``` |
| update stock multi-lines en mode REST FULL| ```curl -X PATCH "http://localhost:8080/shop/rest/stock" -H "version: 3" -H "Content-Type: application/json" -d '[{ "model_id" : 0, "quantity": -1 },{ "model_id" : 1, "quantity": 2 }]'``` |


NB : Les cas d'erreurs et d'exception sont testés durant les tests unitaires et les tests d'intégration, pour cette raison, durant le build les stacks d'exception apparaissent.

# Version commitées :

| Version            | Command  |
| ------------------ | --------------------------------------------------------- |
| 0.1.3 | test unitaires, test d'API, Modèle mono table |
| 0.1.4 | Test unitaires, test d'API, Modèle sur deux tables, Doc D'API sur Swagger, Découplage avec la version DEMO: plus de contraintes sur les couleurs et le nom du modèle |
| 0.1.5.2 | Ajout de la notion de ressources REST. Mise à jour du catalogue et détail du catalogue, ajout des méthodes de gestion du catalogue   |

# Améliorations possibles
- Séparation plus propre entre les DTOs et les entités
- Déploiement de l'application en conteneur Docker en vue d'une intégration dans un pipeline CI/CD
- Utilisation d'un ORM comme Hibernate.
- Observabilité : Utiliser l'agent APM d'**ElasticsSearch** pour assurer la surveillance de la qualité de service avec une approche métier [APM Agent for JAVA](https://www.elastic.co/guide/en/apm/agent/java/current/index.html)]
- Découpage en micro-service, un micro-service pour la gestion des stocks et un autre pour la gestion des stocks, chacun ayant sa propre gouvernance de ses données, niveau magasin, niveau réseau. 

La gestion des stocks est un élément à haute valeur ajouté pour un magasin, c'est ce qui assure d'avoir les bons produits au bon moment tout en optimisant la capacité de stockage en flux tendu.
Par ailleurs beaucoup de services sont interconnectés avec le service de stock, ce qui en fait le noyau central de la gestion d'un magasin, avec des workflows complexes dont les événements arrivent de manière asynchrone les uns des autres (ventes, commandes, livraisons, etc..).
L'architecture la plus adaptée pour répondre à ses contraintes de découplage entre services et temporelles est l'architecture orientée événement.
Cette évolution peut être introduite à l'aide d'un bus asynchrone comme Kafka qui est réputé pour gérer des flux d'événements temps réel très importants.
Le découpage en domaine avec l'approche DDD, doit permettre de guider vers une architecture pérenne dans le temps.

