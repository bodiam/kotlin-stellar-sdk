mkdir -p build
cd build

git clone --depth 1 https://github.com/stellar/xdrgen.git
git clone --depth 1 https://github.com/stellar/stellar-core

(
cd xdrgen
rake install
)

./xdrgen/bin/xdrgen -o ../src/main/java/org/stellar/sdk/xdr -l java -n org.stellar.xdr stellar-core/src/xdr/*
