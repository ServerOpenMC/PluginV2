# Notre pipeline CI/CD

## Qu'est-ce qu'une "pipeline CI/CD"

Une pipeline CI/CD (Continuous Intergration/Continuous Deployment) est un système qui permet de
faciliter l'intégration des changements au repo (ici avec des build-test et unit-test) et le déploiment de la codebase.
Dans le cas de **OpenMC**, on créé une release avec un build du plugin.

## Comment elle fonctionne

La pipeline de **OpenMC** fonctionne autour du système de milestones de **Github**.
Lorsqu'une milestone est créé, une branch associée a celle-ci est créé.
Ainsi, lorsque des changements sont fait, il faut les faire sur la branch associée à la milestone de l'issue ou de la PR.
Une fois que la milestone est terminée, elle seras fermée par un admin du repo.
Ceci mergeras la branche milestone dans **master** et créera une release.

## Pour les contributeurs

Lorsque vous ferez vos PR, il vous faudra vérifier plusieurs choses (mais ne vous inquiétez pas, tout les dans le modêle de PR).
1. Attendez qu'un admin du repo associe votre PR a une milestone
2. Lorsque vous aurez une milestone, changez la branch cible de voutre PR vers la branche de la milestone associée a votre PR
3. Si tout a été fait correctement, les checks "**Milestone Checks**" seront validés

## Pour les administrateurs du repo

Ce système permet de relier la version du plugin à une milestone.
Il vous faudra donc nommer les milestones avec un nom de la forme "v2.1.3" (en semantic versining) afin de maintenir la cohérence du repo.
Ensuite, lorsque des PR sont créées, il faut impérativement associer une milestone a celle-ci, sinon les checks "**Milestone Checks**" ne reussiront pas (et le système de versionning aussi).
Il faut a tout prix **éviter de push directement dans `master`** et si sa vous arrive, il faudra synchroniser toute les branches milestone avec master.
Sinon, lors de la fermeture d'une milestone, il risque d'y avoir des conflits en mergeant avec master.

Enfin, il vous faudra à chaque fermeture de milestone, synchroniser toutes les branches milestone avec master (encore une fois pour éviter des conflits).
Cependant, je vais commencer a reflechir a un système qui ferait ça automatiquement.
