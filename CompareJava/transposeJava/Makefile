#
#
#

CPP = guiFunction.cpp car.cpp carDb.cpp inventory.cpp
CPPCOMP = icpc  # compilatore
CFLAGS= -std=c++11 #-DMKL_ILP64 -openmp -I$(MKLROOT)/include
#MKL_LIB=-L$(MKLROOT)/lib/intel64 -lmkl_intel_ilp64 -lmkl_core -lmkl_intel_thread -lpthread -lm

car.x: $(CPP)
	$(CPPCOMP) -O0 -g $(CFLAGS) $(MKL_LIB) $^ -o $@


.PHONY: clean xclean

clean:
	rm *.out* *.e* core*.*

xclean:
	rm  *.x
