# Approche utilisé pour la réalisation de l'exercice:

A été choisi de partir de l'application de démonstration pour la faire évoluer par itérations successives vers la cible. Cette cible devant s'approcher d'une notion de MVP tout en gardant une compatibilité avec l'origine.

## Stratégie de test
Mise en place dès la première itération, d'une stratégie de test pour assurer une couverture de test à plus de 80% basée sur deux niveaux :
Cette stratégie est basé sur deux niveaux de tests.
- Les unitaires sur le code métier avec une approche DBB, pour valider les fonctionnalités et la non-régression.
- Les tests sur l'API REST, affin de vérifier en profondeur l'intégration.


###Test BDD du code métier:
Le code métier est testé en simulant la couche de persistence par des MOCKs
- les éléments du langage Gherkins (**GIVEN, WHEN, THEN**) ont été repris pour l'écriture des tests.
- Un soin particulier a été pris pour rendre le code du test le plus simple et le plus lisible.

```shell
mvn test -DfailIfNoTests=false -Dtest=ShopCoreImplTest -pl shop-core -am
```

###Test d'intégration sur l'API
- Les tests d'intégration sont réalisés sur toutes la couches entre l'API REST et la base de données sans aucune simulation (MOCK).
- Ce test a été directement packagé dans la version de test du projet.
- Comme pour le test du code métier le langage gherkins a été repris.
- La librairie HttpClient d'Apache a été utilisé pour tester la méthode **PATCH**

```shell
mvn test -DfailIfNoTests=false -Dtest=ApiTest -pl controller -am
```


## Choix de la persistance
- La base de données choisie est HSQlDB en mode embarquée.
- Le modèle choisi est basé sur une approche additive, par ajout d'enregistrement et ajout de colonnes sans destruction d'enregistrement, ceci pour préserver un historique des données, et ainsi assurer la traçabilité.
- Le binding bidirectionnel est utilisé pour éviter les problèmes de **SQL injection**.

# Première itération.
- Pour la première itération la modélisation de l'application a été réalisé sur une seule table SHOES_STOCK.
- Le catalogue est constitué par tous les modèles déjà enregistrés dans les stocks.
- **accept-single-value-as-array** a été utilisé pour implémenter la mise à jour du stock en simple ou multi-lignes.
- Dans le cas d'un update de Stock sur plusieurs lignes, les entrées sont traitées en priorité 

## test

### Test du code metier
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
| Swagger Doc |  [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/) |
| search V1 | ```curl -X GET "http://localhost:8080/shoes/search" -H "version: 1" ``` |
| search V2 | ```curl -X GET "http://localhost:8080/shoes/search" -H "version: 2" ``` |
| search V3 | ```curl -X GET "http://localhost:8080/shoes/search" -H "version: 3" ``` |
| get stock | ```curl -X GET "http://localhost:8080/shop/stock" -H "version: 3" ``` |
| update stock single line | ```curl -X PATCH "http://localhost:8080/shop/stock" -H "version: 3" -H "Content-Type: application/json" -d '{ "size": 40, "color": "BLACK", "quantity": -1 }'``` |
| update stock multi-lines | ```curl -X PATCH "http://localhost:8080/shop/stock" -H "version: 3" -H "Content-Type: application/json" -d '[{ "size": 40, "color": "BLACK", "quantity": -1 },{ "size": 39, "color": "BLUE", "quantity": -2 }]'``` |
| get stock  | ```curl -X GET "http://localhost:8080/shop/stock" -H "version: 3"``` |


