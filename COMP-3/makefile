all: build

testcases = testcase-001 testcase-002 testcase-003 testcase-004

.PHONY : build doc run testcase $(testcases)

build: bin/Postfix.class

bin/Postfix.class: src/Postfix.java
	javac -d bin src/Postfix.java

doc: src/Postfix.java
	javadoc -d doc -private src/Postfix.java

run: build
	cd bin; java Postfix loop

testcase: $(testcases) build

testcase-001: testcases/tc-001.infix testcases/tc-001.postfix build
	$(call case, 001)

testcase-002: testcases/tc-002.infix testcases/tc-002.postfix build
	$(call case, 002)

testcase-003: testcases/tc-003.infix testcases/tc-003.postfix build
	$(call case, 003)

testcase-004: testcases/tc-004.infix testcases/tc-004.postfix build
	$(call case, 004)

define case
	@echo "Running Testcase$(1): a correct input from DBv2."
	@echo "=============================================="
	@echo "The input is:"
	@cat testcases/tc-$(strip $(1)).infix
	@echo "----------------------------------------------"
	
	@# Run the testcase with input direction
	@cd bin; java Postfix < ../testcases/tc-$(strip $(1)).infix

	@# Compare the expected output
	@echo "----------------------------------------------"
	@echo "The output should be:" 
	@cat testcases/tc-$(strip $(1)).postfix
	@echo "=============================================="
endef

timetest: build
	@python src/gen.py 100000 > testcases/tmpcase.infix
	@cd bin; java -Xss32M Postfix time loop < ../testcases/tmpcase.infix
	@cd bin; java -Xss32M Postfix time recursion < ../testcases/tmpcase.infix
	@cat bin/result.log
	@-rm testcases/tmpcase.infix bin/result.log

repeattest: build
	@for index in {1..100}; do \
		./src/gen.c 500000 > testcases/tmpcase.infix; \
		cd bin; \
		java -Xss32M Postfix record loop < ../testcases/tmpcase.infix > /dev/null; \
		java -Xss32M Postfix record recursion < ../testcases/tmpcase.infix > /dev/null; \
		cd ..; \
		rm testcases/tmpcase.infix; \
		echo $$index; \
	done
	@cd src; python avg.py
	@-rm bin/recursion-record.log bin/loop-record.log
