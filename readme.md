[![Build P2 Site](https://github.com/elexis/elexis-3-base/actions/workflows/build-p2-site.yaml/badge.svg)](https://github.com/elexis/elexis-3-base/actions/workflows/build-p2-site.yaml)

Elexis Base contains bundles that can be installed using the <a href="https://gitlab.medelexis.ch/elexis/elexis-3-core">Elexis</a> application.

# Elexis Base from source 
If you want to follow the ongoing commits:

## Build
To build Elexis Base on your own do the following steps:
```shell script
git clone https://github.com/elexis/elexis-3-base
cd elexis-3-base
mvn -V -Dtycho.localArtifacts=ignore -Dmaven.test.skip=true clean verify
```
> [!NOTE]
> * -V: emits version of Java, Maven, GUI-Toolkit, Architecture. Handy when you ask a problem
> * clean: Build everything from scratch
> * verify: Compile, test and build a P2-site. But does NOT install maven artefacts
> * -Dtycho.localArtifacts=ignore: Do not use any locally built maven artefacts
> * -Dmaven.test.skip=true: Skip unit tests (Use it only if you want to speed up the build)
> * Jenkins-CI job copIES the generated the *p2site\target\repository to a subdirectory reachable under `http://download.elexis.info/elexis/<branch-name>/p2/elexis-3-base`
> * Add `-Dmaven.test.skip=true` if you want to skip running the unit tests.
> * -Drepo_variant=release as starting with branch 3.3 we begin to use the P2 named like `http://download.elexis.info/elexis/<branch-name>/` and use branch names likes 3.3, 3.4

After the build use Help..Add New Software add a new 'local' p2 site pointing at the generated ch.elexis.base.p2site\target\repository directory and install the desired plugins

