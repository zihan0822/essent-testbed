# Fetch riscv-fesvr
git clone https://github.com/riscv/riscv-fesvr.git

# Make result directories
mkdir -p riscv-fesvr/build
RISCV_DEST=`pwd`/riscv
RISCV_HEAD_DEST=`pwd`/riscv-head
mkdir -p $RISCV_DEST
mkdir -p $RISCV_HEAD_DEST

# Old-riscv-fesvr
cd riscv-fesvr/build
git checkout 18e712bc9931c49c101d27219f196f499b64c95e
../configure --prefix=$RISCV_DEST --target=riscv64-unknown-elf
make install

# More recent riscv-fesvr
rm -rf ./*
git checkout 68c12d06ebbdfe20856b886570822fe66804fd26
../configure --prefix=$RISCV_HEAD_DEST --target=riscv64-unknown-elf
make install
