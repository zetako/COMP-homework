src-self = src/parser/Calculator.java

.PHONY: build doc run test_simple test_standrad

build: bin/calculator.class

bin/calculator.class: $(src-self)
	cd src; javac -d ../bin -classpath ../bin parser/*.java

doc: $(src-self)
	javadoc -private -author -version -d ../doc -classpath ../bin parser/*.java

run: build
	cd bin; java ExprEval

test_simple: build
	cd bin; java test.ExprEvalTest ../testcases/simple.xml

test_standrad: build
	cd bin; java test.ExprEvalTest ../testcases/standard.xml  > ../testcases/report.txt
	cat testcases/report.txt
	rm testcases/report.txt