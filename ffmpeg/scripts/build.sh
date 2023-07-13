#!/bin/bash

#最低支持的Android版本 (miniSdk)
androidApi=26
#mac：darwin，linux：linux，windows：windows
osType=darwin
ndkVersion=22.1.7171670
# 自己本机 NDK 所在目录
ndkRoot=${ANDROID_HOME}/ndk/${ndkVersion}
# 交叉编译工具链所在目录
toolchainPath=${ndkRoot}/toolchains/llvm/prebuilt/${osType}-x86_64
# 编译输出的目录
buildDir=$(pwd)

: <<EOF
  all: all platform
  arm64：arm64-v8a
  arm：armeabi-v7a
  x86_64：x86_64
  x86：x86
  clean: ${buildDir}
EOF

cd code

init_arm64() {
  echo "build platform for：arm64-v8a"
  ABI=arm64-v8a
  ARCH1=aarch64
  ARCH2=aarch64
  ANDROID=android
  CPU=armv8-a
  PRE_CFLAGS="-march=${CPU} -marm"
  GCC_L=${ndkRoot}/toolchains/${ARCH1}-linux-${ANDROID}-4.9/prebuilt/${osType}-x86_64/lib/gcc/${ARCH1}-linux-${ANDROID}/4.9.x
  ASM_SWITCH='--enable-asm'
}

init_arm32() {
  echo "build platform for：armeabi-v7a"
  ABI=armeabi-v7a
  ARCH1=arm
  ARCH2=armv7a
  ANDROID=androideabi
  CPU=armv7-a
  PRE_CFLAGS="-mfloat-abi=softfp -march=${CPU} -marm -mfpu=neon"
  GCC_L=${ndkRoot}/toolchains/${ARCH1}-linux-${ANDROID}-4.9/prebuilt/${osType}-x86_64/lib/gcc/${ARCH1}-linux-${ANDROID}/4.9.x
  ASM_SWITCH='--enable-asm'
}

init_x86_64() {
  echo "build platform for：x86_64"
  ABI=x86_64
  ARCH1=x86_64
  ARCH2=x86_64
  ANDROID=android
  CPU=x86-64
  PRE_CFLAGS="-march=${CPU} -msse4.2 -mpopcnt -m64 -mtune=intel"
  GCC_L=${ndkRoot}/toolchains/${ABI}-4.9/prebuilt/${osType}-x86_64/lib/gcc/${ARCH1}-linux-${ANDROID}/4.9.x
  ASM_SWITCH='--enable-asm'
}

init_x86() {
  echo "build platform for：x86"
  ABI=x86
  ARCH1=i686
  ARCH2=i686
  ANDROID=android
  CPU=i686
  PRE_CFLAGS="-march=${CPU} -mtune=intel -mssse3 -mfpmath=sse -m32"
  GCC_L=${ndkRoot}/toolchains/${ABI}-4.9/prebuilt/${osType}-x86_64/lib/gcc/${ARCH1}-linux-${ANDROID}/4.9.x
  # x86 架构必须设置这样，因为移除了寄存器，enable 会导致编译不通过
  ASM_SWITCH='--disable-asm'
}

ffmpeg_configure() {
  FF_CONFIG=""
  FF_CONFIG="${FF_CONFIG} --enable-gpl"
  FF_CONFIG="${FF_CONFIG} --enable-shared"
  FF_CONFIG="${FF_CONFIG} --disable-static"
  FF_CONFIG="${FF_CONFIG} --disable-stripping"
  FF_CONFIG="${FF_CONFIG} --disable-ffmpeg"
  FF_CONFIG="${FF_CONFIG} --disable-ffplay"
  FF_CONFIG="${FF_CONFIG} --disable-ffprobe"
  FF_CONFIG="${FF_CONFIG} --disable-avdevice"
  FF_CONFIG="${FF_CONFIG} --disable-indevs"
  FF_CONFIG="${FF_CONFIG} --disable-outdevs"
  FF_CONFIG="${FF_CONFIG} --disable-devices"
  FF_CONFIG="${FF_CONFIG} --disable-debug"
  FF_CONFIG="${FF_CONFIG} --disable-doc"
  FF_CONFIG="${FF_CONFIG} --enable-cross-compile"
  FF_CONFIG="${FF_CONFIG} --disable-bsfs"
  FF_CONFIG="${FF_CONFIG} --enable-bsf=aac_adtstoasc"
  FF_CONFIG="${FF_CONFIG} --enable-bsf=h264_mp4toannexb"
  FF_CONFIG="${FF_CONFIG} --enable-small"
  FF_CONFIG="${FF_CONFIG} --enable-dct"
  FF_CONFIG="${FF_CONFIG} --enable-dwt"
  FF_CONFIG="${FF_CONFIG} --enable-lsp"
  FF_CONFIG="${FF_CONFIG} --enable-mdct"
  FF_CONFIG="${FF_CONFIG} --enable-rdft"
  FF_CONFIG="${FF_CONFIG} --enable-fft"
  FF_CONFIG="${FF_CONFIG} --enable-version3"
  FF_CONFIG="${FF_CONFIG} --enable-nonfree"
  FF_CONFIG="${FF_CONFIG} --disable-filters"
  FF_CONFIG="${FF_CONFIG} --disable-encoders"
  FF_CONFIG="${FF_CONFIG} --disable-decoders"
  FF_CONFIG="${FF_CONFIG} --disable-parsers"
  FF_CONFIG="${FF_CONFIG} --disable-muxers"
  FF_CONFIG="${FF_CONFIG} --disable-demuxers"
  FF_CONFIG="${FF_CONFIG} --disable-protocols"
  FF_CONFIG="${FF_CONFIG} --disable-avfilter"
  FF_CONFIG="${FF_CONFIG} --disable-postproc"

  # 协议
  FF_CONFIG="${FF_CONFIG} --enable-protocol=file"

  # 增加解封装
  FF_CONFIG="${FF_CONFIG} --enable-demuxer=hls"
  FF_CONFIG="${FF_CONFIG} --enable-demuxer=mpegts"
  FF_CONFIG="${FF_CONFIG} --enable-demuxer=mpegtsraw"
  FF_CONFIG="${FF_CONFIG} --enable-demuxer=mpegps"
  FF_CONFIG="${FF_CONFIG} --enable-demuxer=mpegvideo"

  # 增加封装
  FF_CONFIG="${FF_CONFIG} --enable-muxer=mp4"
  FF_CONFIG="${FF_CONFIG} --enable-muxer=mov"
  FF_CONFIG="${FF_CONFIG} --enable-muxer=m4v"

  # 增加parser
  FF_CONFIG="${FF_CONFIG} --enable-parser=h264"
  FF_CONFIG="${FF_CONFIG} --enable-parser=hevc"

  # 增加decoder
  FF_CONFIG="${FF_CONFIG} --enable-decoder=h264"
  FF_CONFIG="${FF_CONFIG} --enable-decoder=aac"
  FF_CONFIG="${FF_CONFIG} --enable-decoder=hevc"
}

build() {
  # 目标文件输出目录
  OUTPUT_DIR="${buildDir}/libs/${ABI}"
  # 系统库文件所在目录
  SYSROOT_L=${toolchainPath}/sysroot/usr/lib/${ARCH1}-linux-${ANDROID}
  # 传递给编译器的标志
  EXTRA_CFLAGS=""
  EXTRA_CFLAGS="-O3 -fpic ${PRE_CFLAGS} -I${OUTPUT_DIR}/include"
  # 传递给链接器的标志
  EXTRA_LDFLAGS=""
  EXTRA_LDFLAGS="-O3 -lc -ldl -lm -lz -llog -lgcc -L${OUTPUT_DIR}/lib"

  EXTERNAL_LINK_LIBRARY=""
  EXTERNAL_LINK_TAG=""

  rm -rf ${OUTPUT_DIR}

  ./configure \
    --target-os=android \
    --prefix=${OUTPUT_DIR} \
    --arch=${ARCH1} \
    --cpu=${CPU} \
    --sysroot=${toolchainPath}/sysroot \
    ${ASM_SWITCH} \
    ${FF_CONFIG} \
    --cc=${toolchainPath}/bin/${ARCH2}-linux-${ANDROID}${androidApi}-clang \
    --cxx=${toolchainPath}/bin/${ARCH2}-linux-${ANDROID}${androidApi}-clang++ \
    --cross-prefix=${toolchainPath}/bin/${ARCH1}-linux-${ANDROID}-

  # 设置编译用的核心数
  make clean
  make -j4
  make install
  echo "build output = ${OUTPUT_DIR}"
  mv ${OUTPUT_DIR}/lib/* ${OUTPUT_DIR}
  rm -rf ${OUTPUT_DIR}/lib
}

# ffmpeg基本参数
ffmpeg_configure

case "$1" in
all)
  init_arm32
  build

  init_arm64
  build

  init_x86
  build

  init_x86_64
  build
  ;;
arm)
  init_arm32
  build
  ;;
arm64)
  init_arm64
  build
  ;;
x86)
  init_x86
  build
  ;;
x86_64)
  init_x86_64
  build
  ;;
clean)
  rm -rf ${buildDir}
  ;;
esac