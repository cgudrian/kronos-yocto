SUMMARY = "Linux Kernel for the Korg Kronos"
LICENSE = "GPL"

SRC_URI = "\
    git:///home/christian/kronos/kronos/src/linux;protocol=file;nobranch=1 \
"
SRCREV = "f02ad93e6fa3a1987a8c1bf55615983d8df461b1"

inherit kernel

COMPATIBLE_MACHINE = "korg-kronos"
