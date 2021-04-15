all: bulid run

bulid : bin/Logic/AgendaLogic.class bin/Shell/AgendaShell.class

bin/Shell/AgendaShell.class :
	cd src; javac -d ../bin Agenda/Shell/*.java -Xlint:unchecked

bin/Logic/AgendaLogic.class :
	cd src; javac -d ../bin Agenda/Logic/*.java -Xlint:unchecked

run :
	cd bin; java Agenda.Shell.AgendaShell