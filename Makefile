#------------------------------
# Makefile de Camix_Java-v3.0
#
# Version : 3.0
# Auteur  : Matthias Brun
# 
#------------------------------



#---------------------------------
# Organisation générale du projet
#---------------------------------

# Organisation des sources de code, de test et de configuration.
SRC=src
TEST=test
CONFIG=config

# Organistion de la construction.
BUILD=build
DOC=doc



#--------------------------------------------------------
# Configuration des outils de compilation et d'exécution
#--------------------------------------------------------

# Encodage des fichiers sources.
ENCODING=utf-8

# Compilateur Java
JAVAC=javac

# JVM Java
JAVA=java



#-------------------------------------------
# Configuration des outils de documentation
#-------------------------------------------

# Génération de documentation
JAVADOC=javadoc



#----------------------------------------------
# Configuration des accès à la base de données
#----------------------------------------------


#----------------------------------
# Configuration des outils de test
#----------------------------------

# Organisation pour JMeter.
JMETER=jmeter
JMETERDIR=jmeter
JMETERPACKAGE=camix
JMETERSRC=$(JMETERDIR)/src
JMETERBUILD=$(JMETERDIR)/build
JMETERDOC=$(JMETERDIR)/doc
JMETERPLAN=$(JMETERDIR)/plan_test.jmx
JMETERLOG=$(JMETERDIR)/jmeter.log
JMETERPROPERTIES=$(JMETERDIR)/user.properties
JMETERRESULTATS=$(JMETERDIR)/resultats
JMETERJARS=/usr/share/jmeter/lib/ext/ApacheJMeter_tcp.jar

# Classpath intégrant JMeter.
JMETERCLASSPATH=$(JMETERBUILD):$(JMETERJARS)

# JMeter Serveur (controleur)
JMETERCONTROLEUR=$(JMETER)
JMETERCONTROLEURLOG=$(JMETERDIR)/jmeter_controller.log
JMETERCONTROLEURPROPERTIES=$(JMETERDIR)/controller.properties

# JMeter Serveur (injecteur)
JMETERSINJECTEURINTERFACE=wlan0
JMETERSINJECTEURHOSTIP=$(shell /sbin/ifconfig $(JMETERSINJECTEURINTERFACE) | grep "inet ad" | awk '{print $$2}' | awk -F ":" '{print $$2}')
JMETERSINJECTEUR=$(JMETER) -s
JMETERSINJECTEUROPTIONS=-Djava.rmi.server.hostname=$(JMETERSINJECTEURHOSTIP)
JMETERINJECTEURLOG=$(JMETERDIR)/jmeter_injector.log
JMETERINJECTEURPROPERTIES=$(JMETERDIR)/injector.properties


# Classpath intégrant JUnit et EasyMock.
JUNITPATH=/usr/share/java/junit4.jar
CGLIBPATH=/usr/share/java/cglib-nodep.jar
OBJENESISPATH=/usr/share/java/objenesis.jar
MOCKPATH=/usr/share/java/easymock.jar

# Décommenter pour utiliser l'interface texte de JUnit.
TESTRUNNER=junit.textui.TestRunner

# Décommenter pour utiliser l'interface swing de JUnit.
#TESTRUNNER=junit.swingui.TestRunner
#JUNITGRAPHICPATH=/usr/share/java/junit.jar

# Décommenter pour utiliser l'interface AWT de JUnit.
#TESTRUNNER=junit.awtui.TestRunner
#JUNITGRAPHICPATH=/usr/share/java/junit.jar

TESTPATH=$(JUNITGRAPHICPATH):$(JUNITPATH):$(CGLIBPATH):$(OBJENESISPATH):$(MOCKPATH)


#---------------------------------------------------
# Configuration des outils de vérification statique
#---------------------------------------------------

# Organisation de checkstyle
CHECKSTYLE=java -jar ~/Logiciels/Checkstyle/checkstyle-6.2/checkstyle-6.2-all.jar
CHECKSTYLEFILE=checkstyle.xml

# Organisation de findbugs
FINDBUGS=~/Logiciels/Findbugs/findbugs-2.0.2-rc1/bin/findbugs
FINDBUGSOPTIONS=-effort:max -maxRank 20 -low -textui -progress



#-------------------------
# Configuration du projet
#-------------------------

# Sourcepath et classpath du projet.
SOURCEPATH=$(SRC):$(TEST)
CLASSPATH=$(CONFIG):$(BUILD):$(TESTPATH)



#------------------------
# Organisation du projet
#------------------------

# Nom de base des packages.
PACKAGEBASE=camix

# Programme principale et programme de test associé.
MAINPROGRAM=$(PACKAGEBASE).Camix
TESTPROGRAM=$(PACKAGEBASE).CamixTestSuite


#-------------
# Compilation
#-------------

# Fichiers à compiler.
JAVAFILES= \
	$(SRC)/$(PACKAGEBASE)/*.java \
	$(TEST)/$(PACKAGEBASE)/*.java \

# Fichier à compiler pour JMeter.
JMETERJAVAFILES= \
	$(JMETERSRC)/$(JMETERPACKAGE)/*java

# Compilation des sources du projet.
compile :
	mkdir $(BUILD) && \
	$(JAVAC) -classpath $(CLASSPATH) -sourcepath $(SOURCEPATH) -d $(BUILD) -encoding $(ENCODING) $(JAVAFILES)

# Compilation des sources pour JMeter.
compile_jmeter :
	mkdir $(JMETERBUILD) && \
	$(JAVAC) -classpath $(JMETERCLASSPATH) -d $(JMETERBUILD) -encoding $(ENCODING) $(JMETERJAVAFILES)



#-----------
# Exécution 
#-----------

# Lancement du programme.
launch :
	$(JAVA) -classpath $(CLASSPATH) $(MAINPROGRAM)



#---------------
# Documentation
#---------------

# Génération de la documentation javadoc.
documentation :
	$(JAVADOC) -classpath $(CLASSPATH) -sourcepath $(SOURCEPATH) -d $(DOC) -encoding $(ENCODING) \
		-subpackages $(PACKAGEBASE)

# Génération de la documentation javadoc du code pour JMeter.
documentation_jmeter :
	$(JAVADOC) -classpath $(JMETERCLASSPATH) -sourcepath $(JMETERSRC) -d $(JMETERDOC) -encoding $(ENCODING) \
		-subpackages $(JMETERPACKAGE)



#-----------
# Nettoyage
#-----------

# Nettoyage du projet (suppression du répertoire build).
clean :
	rm -rf $(BUILD)

# Nettoyage du projet (suppression du répertoire build et de la documentation)
mrproper : clean
	rm -rf $(DOC)

# Nettoyer, compiler.
new : clean compile

# Nettoyage du projet (suppression du répertoire build) du code pour JMeter.
clean_jmeter :
	rm -rf $(JMETERBUILD)

# Nettoyage du projet (suppression du répertoire build et de la documentation) du code pour JMeter.
mrproper_jmeter : clean_jmeter
	rm -rf $(JMETERDOC)
	echo > $(JMETERLOG)
	echo > $(JMETERCONTROLEURLOG)
	echo > $(JMETERINJECTEURLOG)
	if [ -d $(JMETERRESULTATS) ]; then rm -f $(JMETERRESULTATS)/* ; fi

# Nettoyer, compiler le code pour JMeter.
new_jmeter : clean_jmeter compile_jmeter



#---------------
# Test logiciel
#---------------

# Tests du programme Camix.
test_camix :
	$(JAVA) -classpath $(CLASSPATH) $(TESTRUNNER) $(TESTPROGRAM)

# Tests avec JMeter
test_jmeter :
	$(JMETER) -q $(JMETERPROPERTIES) -j $(JMETERLOG) -t $(JMETERPLAN)

# Tests avec JMeter Serveur (controleur)
test_jmeter_controleur :
	$(JMETERCONTROLEUR) -q $(JMETERCONTROLEURPROPERTIES) -j $(JMETERCONTROLEURLOG) -t $(JMETERPLAN)

# Tests avec JMeter Serveur (injecteur)
test_jmeter_injecteur :
	$(JMETERSINJECTEUR) $(JMETERSINJECTEUROPTIONS) -q $(JMETERINJECTEURPROPERTIES) -j $(JMETERINJECTEURLOG)


#-----------------
# Qualité du code
#-----------------

# Vérifications statiques avec checkfile.
checkstyle_camix :
	$(CHECKSTYLE) -c $(CHECKSTYLEFILE) -r $(SRC) -r $(TEST)

# Vérifications statiques avec checkfile du code pour JMeter.
checkstyle_jmeter :
	$(CHECKSTYLE) -c $(CHECKSTYLEFILE) -r $(JMETERSRC)

# Vérification statique avec findbugs (les fichiers doivent être compilés !)
findbugs_camix : 
	$(FINDBUGS) $(FINDBUGSOPTIONS) -auxclasspath $(CLASSPATH):$(JMETERCLASSPATH) .

