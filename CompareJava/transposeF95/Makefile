#
# Makefile of Matrix Multiplication benchmark
#
# This make take in input parameter OMP = true/false for enable
# openmp and the parameter CHECK = true/false for enable Matrix
# transpose check control.
#
#

F = transpose.f90       # file da compilare
C = cptimer.c
FCOMP = ifort     # compiler
CCOMP = icc
FFLAGS= -O3  -inline -fpp -free -opt-report4
CFLAGS= -O3  -std=c99
OBJ = cptimer.o transpose.o

ifeq ($(OMP),true)
        FFLAGS+= -openmp
	CFLAGS+= -openmp
	NAME = transposeOMP.x
else
	NAME = transpose.x
endif

ifeq ($(CHECK),true)
	FFLAGS+= -DCHECKCONTROL
endif

$(NAME): $(OBJ)
	$(FCOMP) $(FFLAGS) $^ -o $@
transpose.o : $(F)
	$(FCOMP) $(FFLAGS) $^ -c
cptimer.o : $(C)
	$(CCOMP) $(CFLAGS) $^ -c

.PHONY: clean xclean

clean:
	rm *.o *.out* *.e* core*.*
xclean:
	rm *.x
