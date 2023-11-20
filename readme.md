# Binaries
If you want to install Elexis Base as precompile package you can get it from:
- [Windows](http://download.elexis.info/elexis/3.10/products/Elexis3-win32.win32.x86_64.zip)
- [Apple OS X](http://download.elexis.info/elexis/3.10/products/Elexis3-macosx.cocoa.x86_64.zip)
- [Linux](http://download.elexis.info/elexis/3.10/products/Elexis3-linux.gtk.x86_64.zip)

> [!NOTE]
>  As Elexis Base is part of the Elexis package you can not download it partially 

# Buildng from source
<a href="https://gitlab.medelexis.ch/elexis/elexis-3-base/commits/master"><img alt="build status" src="https://gitlab.medelexis.ch/elexis/elexis-3-base/badges/master/pipeline.svg" /></a>


@git clone https://github.com/elexis/elexis-3-base@
@cd elexis-3-base@
@mvn -V -Dtycho.localArtifacts=ignore -Dmaven.test.skip=true clean verify@

Remarks:
* -V: emits version of Java, Maven, GUI-Toolkit, Architecture. Handy when you ask a problem
* clean: Build everything from scratch
* verify: Compile, test and build a P2-site. But does NOT install maven artefacts
* -Dtycho.localArtifacts=ignore: Do not use any locally built maven artefacts
* -Dmaven.test.skip=true: Skip unit tests (Use it only if you want to speed up the build)
* Jenkins-CI job copIES the generated the *p2site\target\repository to a subdirectory reachable under  http://download.elexis.info/elexis/<branch-name>/p2/elexis-3-base
* Add `-Dmaven.test.skip=true` if you want to skip running the unit tests.
* -Drepo_variant=release as starting with branch 3.3 we begin to use the P2 named like http://download.elexis.info/elexis/<branch-name>/ and use branch names likes 3.3, 3.4

After the build use Help..Add New Software add a new 'local' p2 site pointing at the generated ch.elexis.base.p2site\target\repository directory and install the desired plugins

