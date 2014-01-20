include $(top_srcdir)/config/utility.am
include $(top_srcdir)/config/java.am
include $(top_srcdir)/common.am

EXTRA_DIST = \
	$(cfg_SRC) \
	$(cfg_RESOURCES) \
	$(test_SRC) \
	$(PUB_PACKAGES) \
	web

PUB_PACKAGES = \
	packages/browser \
	packages/analyzer \
	packages/args \
	packages/intl \
	packages/logging \
	packages/path \
	packages/shadow_dom \
	packages/unmodifiable_collection

SRC_PUB_PACKAGES = $(addprefix $(srcdir)/,$(PUB_PACKAGES))

# tests do not compile
noinst_DATA = \
	javac-cfg \
	SRC_PUB_PACKAGES
# javac-test   tests do not compile	

# Notice, this does not detect if newer versions of dependencies are available for download
# If however the file pubspec.yaml is updated to require new versions of deps then dart's 
# package manager should be re-executed.
pub-get $(SRC_PUB_PACKAGES) : pubspec.yaml
	cd $(srcdir); $(DART_HOME)/bin/pub get
	touch $(SRC_PUB_PACKAGES)
	$(MAKE) remove-extraneous-packages-symlinks

# Dart's package manager has an annoying feature that generates packages symlinks in
# web directory.  This removes all the copies that are not needed by how sipXconfig has integrated
# dart.  See following URL for background on links:
#  http://stackoverflow.com/questions/15888308/is-there-a-way-to-disable-all-those-symlinks-to-the-packages-directory
remove-extraneous-packages-symlinks :
	find $(srcdir)/web \
	  -name packages -type l \
	  -not -path '$(srcdir)/web/context/WEB-INF/*' \
	  -exec rm {} \;

web_JAVAROOT=.

DART_SRC = $(shell cd $(srcdir); find . -maxdepth 2 -name '*.dart' -not -name sipxconfig.dart)
# DART_PKGS = $(shell cd $(top_srcdir); find . -name '*.js')
DART_PKGS = $(shell cd $(srcdir); find packages/*/ -name '*.js')

java-dart : $(addprefix $(web_JAVAROOT)/,$(DART_SRC:.dart=.js) $(DART_PKGS))

$(addprefix $(web_JAVAROOT)/,$(DART_PKGS)) : $(DART_PKGS)
	test -d $(dir $@) || mkdir -p $(dir $@)
	cp $< $@

$(addprefix $(web_JAVAROOT)/,$(DART_SRC:.dart=.js)) : $(web_JAVAROOT)/%.js : %.dart
	test -d $(dir $@) || mkdir -p $(dir $@)
	$(DART_HOME)/bin/dart2js --minify -o$@ $<

jardir = @SIPX_JAVADIR@/sipXconfig/plugins
jar_DATA = $(cfg_JAR)
cfg_JAR = openfire-cfg-service.jar
cfg_RESOURCES = \
	$(shell cd $(srcdir); find plugin -type f) \
	sipxplugin.beans.xml

web_RESOURCES = $(shell cd $(srcdir); find . -type f  \( \
	  -name '*.css' \
	  -o -name '*.html' \
	  -o -name '*.page' \
	  -o -name '*.js' \
	\))

$(cfg_JAR) : java-dart javac-cfg $(cfg_RESOURCES)
	jar -cf $@ \
		$(call JarInclude,$(JAVAROOT),.) \
		$(call JarInclude,$(web_JAVAROOT),.) \
		$(call JarInclude,$(srcdir),$(cfg_RESOURCES),$(web_RESOURCES))

cfg_DEPS = \
	$(call JavaDep,@SIPX_JAVADIR@/sipXcommons @SIPX_JAVADIR@/sipXconfig,$(cfg_PKGS))

cfg_SRC = \
	$(shell cd $(srcdir); find src -name '*.java')

noinst_DATA = 
test_JAVAROOT = classes.test
test_PKGS = \
	$(openfire_PKGS) \
	junit \
	easymock \
	commons-io \
	sipxcommons

test_DEPS = \
	$(call JavaDep,@SIPX_JAVADIR@/sipXcommons @SIPX_JAVADIR@/sipXconfig,$(test_PKGS))

test_SRC = $(shell cd $(srcdir); find test -name '*.java')

style_SRC = $(cfg_SRC)

precommit : style
