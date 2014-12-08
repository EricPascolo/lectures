import java.io.*;
import java.util.Vector;

/*
 *
 * MatrixTranspose implements and profiling 3 algho of matrix trasposition.
 * the algorithm are:
 *    - basic transpose
 *    - fast transpose
 *    - parallel transpose
 * @author epascolo
 *
 */

public class MatrixTranspose
{

  public  double matrix[][];
  public  double matrixBasicTranspose[][];
  public  double matrixFastTranspose[][];
  public  double matrixParallelTranspose[][];
  public  int dimension;
  public  int blocksize;
  public  int numthreads;

  public MatrixTranspose(int dim,int blocksize,int numthreads)
  {
      this.matrix = new double[dim][dim];
      this.matrixBasicTranspose = new double[dim][dim];
      this.matrixFastTranspose = new double[dim][dim];
      this.matrixParallelTranspose = new double[dim][dim];
      this.dimension = dim;
      this.blocksize = blocksize;
      this.numthreads = numthreads;
  }

 /*
  *
  * Start benchmarck of matrix transpose.
  * In input take 3 parameter:
  *   - size of square matrix
  *   - size of block size (fast transpose)
  *   - number of threads (parallel transpose)
  *
  */
  public static void main(String args[])
  {
     long timer = 0;

     int inputDimension = Integer.valueOf(args[0]);
     int inputBlocksize = Integer.valueOf(args[1]);
     int inputNumThreads = Integer.valueOf(args[2]);
     System.out.println("!------------");
     System.out.println("Matrix Transpose JAVA");
     System.out.println("Matrix:"+inputDimension+"x"+inputDimension);
     System.out.println("Fast blocksize:"+inputBlocksize);
     System.out.println("Threads"+inputNumThreads);
     System.out.println("------");
     /* INIT MATRIX */
     MatrixTranspose transp = new MatrixTranspose(inputDimension,inputBlocksize,inputNumThreads);
     transp.initMatrix();

     /* BASIC TRANSPOSE */
     timer = System.nanoTime();
     transp.basicTranspose();
     timer = System.nanoTime() - timer;
     System.out.println("-- Basic Transpose time="+timer/1000);

     /* FAST TRANSPOSE */
     if(transp.blocksize != 0)
     {
       timer = System.nanoTime();
       transp.fastTranspose();
       timer = System.nanoTime() - timer;
       System.out.println("-- Fast Transpose time="+timer/1000);

       /* CHECK FAST TRANSPOSE */
       boolean ch1 = transp.checkMatrix(transp.matrixBasicTranspose,
                                    transp.matrixFastTranspose,transp.dimension);
       if(ch1)
         System.out.println("> Check fast matrix OK");
       else
         System.out.println("> Check fast matrix FAIL");
     }

     /* PARALLEL TRANSPOSE */
     if(transp.numthreads != 0)
     {
       timer = System.nanoTime();
       transp.parallelTranspose();
       timer = System.nanoTime() - timer;
       System.out.println("-- Parallel Transpose time="+timer/1000);

      /* CHECK PARALLEL TRANSPOSE */
      boolean ch2 = transp.checkMatrix(transp.matrixBasicTranspose,
                                transp.matrixParallelTranspose,transp.dimension);
      if(ch2)
        System.out.println("> Check // matrix OK");
      else
        System.out.println("> Check // matrix FAIL");
    }
    System.out.println("!------------");
  }

  /*
   * Init Matrix to be transpose
   *
   */
  private void initMatrix()
  {
    int count = 0;
    for(int i=0;i<dimension;i++)
      for(int j=0;j<dimension;j++)
      {
        this.matrix[i][j] = count;
        count++;
      }
  }

  /*
   * Basic transpose algorithm
   *
   */
  private void basicTranspose()
  {
    for(int i=0;i<dimension;i++)
      for(int j=0;j<dimension;j++)
      {
        this.matrixBasicTranspose[j][i] = this.matrix[i][j];
      }
  }

  /*
   * Fast transpose algorithm
   *
   */
  private void fastTranspose()
  {
    double buff = 0;

    for(int i=0;i<dimension;i=i+this.blocksize)
      for(int j=0;j<dimension;j=j+this.blocksize)
      {
        double block[][] = new double[this.blocksize][this.blocksize];

        for(int k=i;k<i+this.blocksize;k++)
          for(int l=j;l<j+this.blocksize;l++)
              block[k-i][l-j] = this.matrix[k][l];

        for(int m=0;m<this.blocksize;m++)
          for(int n=0;n<this.blocksize;n++)
          {
            buff = block[m][n];
            block[m][n] = block[n][m];
            block[n][m] = buff;
          }

        for(int k=i;k<i+this.blocksize;k++)
          for(int l=j;l<j+this.blocksize;l++)
            this.matrixFastTranspose[l][k] = block[k-i][l-j];

      }
  }

  /*
   * Parallel transpose algorithm
   *
   */
  private void parallelTranspose()
  {
    Vector<ParallelTranspose> commWorld = new Vector<ParallelTranspose>();

    int offset[] = this.loadBalancing();

    for(int i=0;i<this.numthreads;i++)
    {
      commWorld.add( new ParallelTranspose(this.matrix,this.matrixParallelTranspose,this.dimension,offset[i],offset[i+1]));
    }

    for(int i=0;i<this.numthreads;i++)
      commWorld.get(i).start();

    for(int i=0;i<this.numthreads;i++)
      commWorld.get(i).interrupt();

    for(int i=0;i<this.numthreads;i++)
    {
      try{  commWorld.get(i).join();  }
      catch(InterruptedException exception)
      {        }
    }

  }

  /*
   * Load balancing matrix row among threads
   *
   */
  private int[] loadBalancing()
  {
    int idx[] = new int[this.numthreads];

    for(int i=0;i<this.numthreads;i++)
    {
      if(i<this.dimension%this.numthreads)
        idx[i] = (this.dimension/this.numthreads)+1;
      else
        idx[i] = (this.dimension/this.numthreads);
    }

    int disp[] = new int[this.numthreads+1];
    disp[0] = 0;

    for(int i=1;i<this.numthreads;i++)
      disp[i] = disp[i-1]+ idx[i];

    disp[this.numthreads] = this.dimension;

    return disp;
  }

  /*
   * Compare two Matrix, return true if the matrix is equals
   *
   */
  private boolean checkMatrix(double matrix1[][],double matrix2[][],int dim)
  {
    boolean ch = true;
    check : for(int i=0;i<dim;i++)
      for(int j=0;j<dim;j++)
        if(matrix1[i][j] != matrix2[i][j])
        {
          ch = false;
          break check;
        }

    return ch;

  }

  /*
   * Return matrix on standard out
   *
   */
  private void printMatrix(double m[][],int size)
  {
    int count = 0;
    for(int i=0;i<size;i++)
    {
      for(int j=0;j<size;j++)
      {
        System.out.format(" %f ", m[i][j]);

      }
      System.out.println(" ");
    }
  }

}
