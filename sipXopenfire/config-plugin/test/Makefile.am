include $(top_srcdir)/config/utility.am
include $(top_srcdir)/config/java.am
include $(top_srcdir)/common.am
include $(top_srcdir)/config/sipxconfig-integration-testing.am

EXTRA_DIST = \
	$(test_SRC)

noinst_DATA = javac-test
test_PKGS = \
	$(openfire_PKGS) \
	junit \
	easymock \
	commons-io \
	sipxcommons

test_DEPS = \
	$(call JavaDep,@SIPX_JAVADIR@/sipXcommons @SIPX_JAVADIR@/sipXconfig,$(test_PKGS))

test_SRC = $(shell cd $(srcdir); find org -name '*.java')

precommit : check
	$(MAKE) sipxconfig-reset-integration-test plugin-reset-integration-test check-integration
