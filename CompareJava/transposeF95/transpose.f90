!
! Transpose Matrix Benchmark
!
! The program take in input 3 parameter:
!	- dimension square matrix
!	- dimension block for fast transpose
!	- number of core(optional if you compile without omp)
!
!

PROGRAM MatrixFastTranspose
 
#ifdef _OPENMP
    use omp_lib
#endif
 
 IMPLICIT NONE
 
 DOUBLE PRECISION,DIMENSION(:,:), ALLOCATABLE :: A,Atb,At,Block
 INTEGER :: i,j,ix,counter,core,num_args,matrixDim,blocksize
 CHARACTER (LEN=20) :: filename,str4
 CHARACTER(len=40), DIMENSION(:), ALLOCATABLE :: args(:)
 LOGICAL :: check=.true.
 DOUBLE PRECISION cclock
 DOUBLE PRECISION :: timer
 
     
 num_args = command_argument_count()
 ALLOCATE(args(num_args)) 

 DO ix = 1, num_args
    CALL get_command_argument(ix,args(ix))
 END DO
 
 read( args(1), '(i10)' ) matrixDim
 read( args(2), '(i10)' ) blocksize
 
#ifdef _OPENMP
 read( args(3), '(i10)' ) core
 CALL OMP_SET_NUM_THREADS(core)
 print *,"NUM THREADS = ",core
#endif

 IF(MOD(matrixDim,blocksize) .ne. 0) THEN
  STOP "MATRIX DIMENSION IS NO DIVISIBLE FOR BLOCKSIZE"
 ENDIF
 
 counter = 0
 ALLOCATE(A(matrixDim,matrixDim))
 ALLOCATE(At(matrixDim,matrixDim))
 ALLOCATE(Atb(matrixDim,matrixDim))
 ALLOCATE(Block(blocksize,blocksize))
 
 !FILL THE MATRIX
 DO j=1,matrixDim
  DO i=1,matrixDim
    A(i,j) = counter
    counter = counter+1
  END DO
 END DO
 
!BASIC TRANSPOSE
#ifdef CHECKCONTROL
 timer = cclock()
  
 DO j=1,matrixDim
  DO i=1,matrixDim
    Atb(i,j) =  A(j,i)
  END DO
 END DO
 timer = cclock() - timer
 print *, "TIME BASIC TRASPOSITION:",timer
#endif 

 !FAST TRANSPOSE 
 timer = cclock()
 
!$OMP parallel do private(Block) collapse(2)
 DO j=1,matrixDim,blocksize
  DO i=1,matrixDim,blocksize
    Block(:,:) = A(i:i+blocksize-1,j:j+blocksize-1)
    CALL fast_transpose(Block,blocksize)    
    At(j:j+blocksize-1, i:i+blocksize-1) = Block(:,:)
   
  END DO
 END DO
 
 timer = cclock() - timer
 print *, "TIME FAST TRASPOSITION:",timer

 
 !CHECK 
#ifdef CHECKCONTROL
 DO j=1,matrixDim
  DO i=1,matrixDim
    IF (At(i,j) .NE.  Atb(i,j)) THEN
      check = .false.
    ENDIF
  END DO
 END DO
 
 IF(check .eq. .true.) THEN
    print *, "MATRIX CHECK: OK"
 ELSE
    print *, "FAST TRANSPOSE ERROR"
 ENDIF
#endif
 
 DEALLOCATE(A)
 DEALLOCATE(At)
 DEALLOCATE(Atb)
 DEALLOCATE(Block)
 
END PROGRAM

!=----------------------------------------------------------------------=

SUBROUTINE fast_transpose(matrix,siz)
!
! This subroutine transpose the matrix that take in input
!
    IMPLICIT NONE
    INTEGER :: i, j
    INTEGER, INTENT(in) :: siz
    DOUBLE PRECISION :: buff
    CHARACTER (LEN=20) :: file
    DOUBLE PRECISION, DIMENSION(blocksize:blocksize),INTENT(inout) :: matrix(siz,siz)
    
    DO j=1,siz
      DO i=j,siz
	buff = matrix(i,j)
	matrix(i,j) = matrix(j,i)
	matrix(j,i) = buff
      END DO
    END DO
    

END SUBROUTINE fast_transpose

!=----------------------------------------------------------------------=

SUBROUTINE write_matrix(mtw,righe,colonne,percorso)
!
! This subroutine plot a double matrix that take in input
!
    IMPLICIT NONE
    INTEGER :: i, j, righe,colonne
    character(LEN=*), INTENT(in) :: percorso
    DOUBLE PRECISION, INTENT(in) :: mtw(righe,colonne)
    
    OPEN(unit=115, file=percorso, form='formatted')
    DO i=1,righe
       WRITE(115,55) mtw(i,1:colonne)
    END DO
    CLOSE(0)

    55 FORMAT(100F10.2)
    
END SUBROUTINE write_matrix

!=----------------------------------------------------------------------=


