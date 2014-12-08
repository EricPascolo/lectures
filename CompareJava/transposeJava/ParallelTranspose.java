
/**
 *
 * ParallelTranspose is a class that perform a parallel matrix tranpose
 *
 * @author epascolo
 *
 */

public class ParallelTranspose extends Thread {

    public  double matrixParallelTranspose[][];
    public  double matrix[][];
    public  int dimension;
    private int rowBegin;
    private int rowEnd;

    /*
     * Init threads with parameter
     *
     * @param matrix : matrix that to be transpose
     * @param parmatrix : result matrix
     * @param dimension : square matrix size
     * @param rowB : threads begin row
     * @param rowE : threads end row
     *
     */
     
    public ParallelTranspose(double matrix[][],double parmatrix[][], int dimension,int rowB,int rowE)
    {

      this.dimension = dimension;
      this.rowBegin = rowB;
      this.rowEnd = rowE;
      this.matrix = matrix;
      this.matrixParallelTranspose = parmatrix;

    }

    public void run()
    {

      for(int i=rowBegin;i<rowEnd;i++)
        for(int j=0;j<dimension;j++)
        {
          this.matrixParallelTranspose[j][i] = this.matrix[i][j];
        }

    }
}
