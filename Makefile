PHONIES += check download-and-verify help unpack unpack-clean
.PHONY: $(PHONIES)

GPG2      = gpg2
WGET      = wget

REPO      = measurement-kit/measurement-kit
BASEURL   = https://github.com/$(REPO)/releases/download
PACKAGE   = org/openobservatory/measurement_kit
SRCDIR    = app/src/main
TAG       = -2-dev
VERSION   = v0.4.0-alpha.2

INPUT     = measurement_kit_android-$(VERSION)$(TAG).tar.bz2

help:
	@printf "Targets:\n"
	@for TARGET in `grep ^PHONIES Makefile|sed 's/^PHONIES += //'`; do     \
	  if echo $$TARGET|grep -qv ^_; then                                   \
	    printf "  - $$TARGET\n";                                           \
	  fi;                                                                  \
	done

unpack: unpack-clean download-and-verify
	@echo "Unpack $(INPUT) inside $(SRCDIR)"
	@tar -C $(SRCDIR) -xf $(INPUT)

unpack-clean:
	@echo "Clean inside $(SRCDIR)"
	@rm -rf $(SRCDIR)/jniLibs/*
	@rm -rf $(SRCDIR)/java/$(PACKAGE)/jni/

download-and-verify: check $(INPUT) $(INPUT).asc
	$(GPG2) --verify $(INPUT).asc

$(INPUT):
	$(WGET) $(BASEURL)/$(VERSION)/$(INPUT)

$(INPUT).asc:
	$(WGET) $(BASEURL)/$(VERSION)/$(INPUT).asc

check:
	@if [ -z "$$(which $(GPG2))" ]; then                                   \
	  echo "FATAL: install $(GPG2) or make sure it's in PATH" 1>&2;        \
	  exit 1;                                                              \
	fi
	@echo "Using $(GPG2): $$(which $(GPG2))"
	@if [ -z "$$(which $(WGET))" ]; then                                   \
	  echo "FATAL: install $(WGET) or make sure it's in PATH" 1>&2;        \
	  exit 1;                                                              \
	fi
	@echo "Using $(WGET): $$(which $(WGET))"
