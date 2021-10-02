LANGUAGES = "c"

require recipes-devtools/gcc/gcc-${PV}.inc
require recipes-devtools/gcc/gcc-cross.inc

do_compile () {
	export CC="${BUILD_CC}"
	export AR_FOR_TARGET="${TARGET_SYS}-ar"
	export RANLIB_FOR_TARGET="${TARGET_SYS}-ranlib"
	export LD_FOR_TARGET="${TARGET_SYS}-ld"
	export NM_FOR_TARGET="${TARGET_SYS}-nm"
	export CC_FOR_TARGET="${CCACHE} ${TARGET_SYS}-gcc"
	export CFLAGS_FOR_TARGET="${TARGET_CFLAGS}"
	export CPPFLAGS_FOR_TARGET="${TARGET_CPPFLAGS}"
	export CXXFLAGS_FOR_TARGET="${TARGET_CXXFLAGS}"
	export LDFLAGS_FOR_TARGET="${TARGET_LDFLAGS}"

	# Prevent native/host sysroot path from being used in configargs.h header,
	# as it will be rewritten when used by other sysroots preventing support
	# for gcc plugins
	oe_runmake configure-gcc
	sed -i 's@${STAGING_DIR_TARGET}@/host@g' ${B}/gcc/configargs.h
	sed -i 's@${STAGING_DIR_HOST}@/host@g' ${B}/gcc/configargs.h

	oe_runmake all-host configure-target-libgcc
}

do_install () {
	oe_runmake 'DESTDIR=${D}' install-host

	install -d ${D}${target_base_libdir}
	install -d ${D}${target_libdir}

	# Link gfortran to g77 to satisfy not-so-smart configure or hard coded g77
	# gfortran is fully backwards compatible. This is a safe and practical solution. 
	if [ -n "${@d.getVar('FORTRAN')}" ]; then
		ln -sf ${STAGING_DIR_NATIVE}${prefix_native}/bin/${TARGET_PREFIX}gfortran ${STAGING_DIR_NATIVE}${prefix_native}/bin/${TARGET_PREFIX}g77 || true
		fortsymlinks="g77 gfortran"
	fi

	# Insert symlinks into libexec so when tools without a prefix are searched for, the correct ones are
	# found. These need to be relative paths so they work in different locations.
	dest=${D}${libexecdir}/gcc/${TARGET_SYS}/${BINV}/
	install -d $dest
	for t in ar as ld ld.bfd ld.gold nm objcopy objdump ranlib strip gcc cpp $fortsymlinks; do
		ln -sf ${BINRELPATH}/${TARGET_PREFIX}$t $dest$t
		ln -sf ${BINRELPATH}/${TARGET_PREFIX}$t ${dest}${TARGET_PREFIX}$t
	done

	# Remove things we don't need but keep share/java
	for d in info man share/doc share/locale share/man share/info; do
		rm -rf ${D}${STAGING_DIR_NATIVE}${prefix_native}/$d
	done

	find ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include-fixed -type f -not -name "README" -not -name limits.h -not -name syslimits.h | xargs rm -f
}
