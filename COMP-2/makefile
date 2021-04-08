all: bulid run

bulid : bin/Agenda/Logic/AgendaLogic.class bin/Agenda/Shell/AgendaShell.class bin/AgendaService.class

bin/AgendaService.class :
	cd src; javac -d ../bin AgendaService.java -Xlint:unchecked
bin/Agenda/Shell/AgendaShell.class :
	cd src; javac -d ../bin Agenda/Shell/*.java -Xlint:unchecked

bin/Agenda/Logic/AgendaLogic.class :
	cd src; javac -d ../bin Agenda/Logic/*.java -Xlint:unchecked

run :
	cd bin; java AgendaService