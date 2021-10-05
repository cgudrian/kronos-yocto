SUMMARY = "Linux kernel for the Korg Kronos"
LICENSE = "GPLv2"

SRC_URI = "\
    git://github.com/cgudrian/linux-kronos.git;protocol=https;branch=v2.6.32.11-kronos \
    file://defconfig \
"
SRCREV = "e279ebf11edc750ca2252e2ff66da0e37d82ddb6"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"
PV = "2.6.32+git${SRCPV}"

KERNEL_CONFIG_COMMAND = "oe_runmake_call -C ${S} CC="${KERNEL_CC}" LD="${KERNEL_LD}" O=${B} defconfig || oe_runmake -C ${S} O=${B} CC="${KERNEL_CC}" LD="${KERNEL_LD}" noconfig"

#PREFERRED_VERSION_binutils-cross-${TARGET_ARCH} = "2.20.1"
#PREFERRED_VERSION_gcc-cross-${TARGET_ARCH} = "4.5.0"

inherit kernel
inherit kernel-yocto

COMPATIBLE_MACHINE = "korg-kronos"

KERNEL_VERSION_SANITY_SKIP = "1"

DEBUG_PREFIX_MAP = "-fdebug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
                    -fdebug-prefix-map=${STAGING_DIR_HOST}= \
                    -fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
"

KERNEL_CC = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_KERNEL_ARCH} ${DEBUG_PREFIX_MAP} -fdebug-prefix-map=${STAGING_KERNEL_DIR}=${KERNEL_SRC_PATH}"
KERNEL_LD = "${CCACHE}${HOST_PREFIX}ld ${HOST_LD_KERNEL_ARCH}"
