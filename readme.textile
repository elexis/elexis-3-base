h1. elexis-3-base

<a href="https://gitlab.medelexis.ch/elexis/elexis-3-base/commits/master"><img alt="build status" src="https://gitlab.medelexis.ch/elexis/elexis-3-base/badges/master/pipeline.svg" /></a>

Elexis 3 Base Repository

h2. developer info

Created the pom.xml like this
@mvn org.eclipse.tycho:tycho-pomgenerator-plugin:generate-poms -DgroupId=ch.elexis.base -Dversion=3.0.0-SNAPSHOT@

Then I manually adjusted the pom.xml'x in the base folder and in the ch.elexis.base.ch.p2site

* There is handy tool to see InstallableUnit in P2-Repositories. See "p2-browser":https://github.com/ifedorenko/p2-browser.

It can be easily launched on the console using @javaws  http://ifedorenko.github.com/p2-browser/javaws/com.ifedorenko.p2browser.jnlp@

* The p2 site contains only the artifacts built inside this repository.

* No Eclipse RCP product is built, as we assume that the core product is distributed using the Elexis 3 core.

h2. Building

You need Java 1.8+. Maven >= 3.3. Then you should be able to generate a p2 update site using the following calls:

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

h2. TODO

* Decide how the release naming will work
@mvn org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=3.0.1-SNAPSHOT@ gives me a NPE.

Here some pointers.
* http://code.google.com/a/eclipselabs.org/p/spray/wiki/DevGuide_Release
* https://docs.sonatype.org/display/M2ECLIPSE/Staging+and+releasing+new+M2Eclipse+release
* https://openflow.stanford.edu/display/Beacon/Releasing
* http://maven.apache.org/maven-release/maven-release-plugin/examples/update-versions.html
* https://community.jboss.org/en/tools/blog/2011/09/17/coping-with-versions-in-large-multi-module-osgi-projects



h2. Javadoc & checkstyle

Use @mvn  --show-version clean -Dmaven.test.skip install -DaltDeploymentRepository=snapshot-repo::default::file:./snapshots -DforceContextQualifier=javadoc javadoc:javadoc checkstyle:checkstyle-aggregate@ to generate output:
* target/site/apidocs/index.html
* target/site/checkstyle-aggregate.html
