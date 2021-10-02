require recipes-devtools/gcc/gcc-${PV}.inc
require recipes-devtools/gcc/gcc-source.inc

EXCLUDE_FROM_WORLD = "1"

python do_preconfigure () {
    import subprocess
    cmd = d.expand('cd ${S} && PATH=${PATH} gnu-configize')
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
    # See 0044-gengtypes.patch, we need to regenerate this file
    bb.utils.remove(d.expand("${S}/gcc/gengtype-lex.c"))
    cmd = d.expand("sed -i 's/BUILD_INFO=info/BUILD_INFO=/' ${S}/gcc/configure")
    subprocess.check_output(cmd, stderr=subprocess.STDOUT, shell=True)
}
