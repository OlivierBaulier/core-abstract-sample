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
