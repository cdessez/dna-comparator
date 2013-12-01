package edition_distance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import dna_file.DnaFileReader;

import static jcuda.driver.JCudaDriver.*;
import jcuda.*;
import jcuda.driver.*;

public class CudaAlgorithm {
	
	public static int cudaDistance(int[] u, int[] t) throws IOException
    {
        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Create the PTX file by calling the NVCC
        String ptxFileName = preparePtxFile("edition_distance/DnaDistanceKernel.cu");

        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Load the ptx file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, ptxFileName);

        // Obtain a function pointer to the "add" function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "distance");
        
        // ensures u.length >= t.length
        if (u.length < t.length){
        	int[] tmp = u;
        	u = t;
        	t = tmp;
        }
        
        int kmax = u.length + t.length ;
        int diagSize = t.length + 1;
        
        // prepare host diags to copy
        int[] hostDiag1 = {0};
        int[] hostDiag2 = {1, 1};
        
        // Allocate the device inputs and copy them to the device
        CUdeviceptr deviceInputU = new CUdeviceptr();
        cuMemAlloc(deviceInputU, u.length * Sizeof.INT);
        CUdeviceptr deviceInputT = new CUdeviceptr();
        cuMemAlloc(deviceInputT, t.length * Sizeof.INT);
        cuMemcpyHtoD(deviceInputU, Pointer.to(u), u.length * Sizeof.INT);
        cuMemcpyHtoD(deviceInputT, Pointer.to(t), t.length * Sizeof.INT);

        // Allocate the device diags
        CUdeviceptr deviceDiag0 = new CUdeviceptr();
        cuMemAlloc(deviceDiag0, diagSize * Sizeof.INT);
        CUdeviceptr deviceDiag1 = new CUdeviceptr();
        cuMemAlloc(deviceDiag1, diagSize * Sizeof.INT);
        CUdeviceptr deviceDiag2 = new CUdeviceptr();
        cuMemAlloc(deviceDiag2, diagSize * Sizeof.INT);
        cuMemcpyHtoD(deviceDiag1, Pointer.to(hostDiag1), Sizeof.INT);
        cuMemcpyHtoD(deviceDiag2, Pointer.to(hostDiag2), 2 * Sizeof.INT);

        // Set up the kernel parameters: A pointer to an array
        // of pointers which point to the actual values.
        Pointer kernelParameters = Pointer.to(
    		Pointer.to(new int[]{u.length}),
    		Pointer.to(new int[]{t.length}),
    		Pointer.to(new int[]{kmax}),
            Pointer.to(deviceInputU),
            Pointer.to(deviceInputT),
            Pointer.to(deviceDiag0),
            Pointer.to(deviceDiag1),
            Pointer.to(deviceDiag2)
        );

        // Call the kernel function.
        int blockSizeX = 672;//432;
        cuLaunchKernel(function,
            1,  1, 1,      // Grid dimension
            blockSizeX, 1, 1,      // Block dimension
            0, null,               // Shared memory size and stream
            kernelParameters, null // Kernel- and extra parameters
        );
        int finalDiag = (kmax+1) % 3;
        cuCtxSynchronize();

        // Allocate host output memory and copy the device diag2 to the host
        int[] res = new int[1];
        switch(finalDiag){
        case 0:
        	cuMemcpyDtoH(Pointer.to(res), deviceDiag0.withByteOffset(t.length * Sizeof.INT), Sizeof.INT);
        	break;
        case 1:
        	cuMemcpyDtoH(Pointer.to(res), deviceDiag1.withByteOffset(t.length * Sizeof.INT), Sizeof.INT);
        	break;
        case 2:
        	cuMemcpyDtoH(Pointer.to(res), deviceDiag2.withByteOffset(t.length * Sizeof.INT), Sizeof.INT);
        	break;
        }

        // Clean up.
        cuMemFree(deviceInputU);
        cuMemFree(deviceInputT);
        cuMemFree(deviceDiag0);
        cuMemFree(deviceDiag1);
        cuMemFree(deviceDiag2);
        
		return res[0];
	}
	
	public static SeqDist getClosestInFile(int[] u, File file) throws IOException {
		/*
		 *  ///!!!\\\ all the sequences in the file must have the same size
		 */
		
		DnaFileReader reader = new DnaFileReader(file);
		int nSeq = reader.totalNumberofSeq();
		int[] bestSeq = null, curSeq = null;
		int bestDist = Integer.MAX_VALUE;
		int[] t = reader.readSequence();
		
		// Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Create the PTX file by calling the NVCC
        String ptxFileName = preparePtxFile("edition_distance/DnaDistanceKernel.cu");

        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Load the ptx file.
        CUmodule module = new CUmodule();
        cuModuleLoad(module, ptxFileName);

        // Obtain a function pointer to the "add" function.
        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "distance");
        
		// ensures u.length >= t.length
        if (u.length < t.length){
        	int[] tmp = u;
        	u = t;
        	t = tmp;
        }
        
        int kmax = u.length + t.length ;
        int diagSize = t.length + 1;
        
        // prepare host diags to copy
        int[] hostDiag1 = {0};
        int[] hostDiag2 = {1, 1};
        
        // Allocate the device inputs
        CUdeviceptr deviceInputU = new CUdeviceptr();
        cuMemAlloc(deviceInputU, u.length * Sizeof.INT);
        CUdeviceptr deviceInputT = new CUdeviceptr();
        cuMemAlloc(deviceInputT, t.length * Sizeof.INT);
        
        // Allocate the device diags
        CUdeviceptr deviceDiag0 = new CUdeviceptr();
        cuMemAlloc(deviceDiag0, diagSize * Sizeof.INT);
        CUdeviceptr deviceDiag1 = new CUdeviceptr();
        cuMemAlloc(deviceDiag1, diagSize * Sizeof.INT);
        CUdeviceptr deviceDiag2 = new CUdeviceptr();
        cuMemAlloc(deviceDiag2, diagSize * Sizeof.INT);
        
        int blockSizeX = 672;
        int[] res = new int[1];
        int finalDiag = (kmax+1) % 3;
		
		for(int i = 0 ; i < nSeq ; i++){
			if (i != 0){
				t = reader.readSequence();
				// ensures u.length >= t.length
		        if (u.length < t.length){
		        	int[] tmp = u;
		        	u = t;
		        	t = tmp;
		        }
			}

	        cuMemcpyHtoD(deviceInputU, Pointer.to(u), u.length * Sizeof.INT);
	        cuMemcpyHtoD(deviceInputT, Pointer.to(t), t.length * Sizeof.INT);
	        
	        cuMemcpyHtoD(deviceDiag1, Pointer.to(hostDiag1), Sizeof.INT);
	        cuMemcpyHtoD(deviceDiag2, Pointer.to(hostDiag2), 2 * Sizeof.INT);
	        
	        // Set up the kernel parameters: A pointer to an array
	        // of pointers which point to the actual values.
	        Pointer kernelParameters = Pointer.to(
	    		Pointer.to(new int[]{u.length}),
	    		Pointer.to(new int[]{t.length}),
	    		Pointer.to(new int[]{kmax}),
	            Pointer.to(deviceInputU),
	            Pointer.to(deviceInputT),
	            Pointer.to(deviceDiag0),
	            Pointer.to(deviceDiag1),
	            Pointer.to(deviceDiag2)
	        );

	        // Call the kernel function.
	        cuLaunchKernel(function,
	            1,  1, 1,      // Grid dimension
	            blockSizeX, 1, 1,      // Block dimension
	            0, null,               // Shared memory size and stream
	            kernelParameters, null // Kernel- and extra parameters
	        );
	        cuCtxSynchronize();

	        // Allocate host output memory and copy the device diag2 to the host
	        switch(finalDiag){
	        case 0:
	        	cuMemcpyDtoH(Pointer.to(res), deviceDiag0.withByteOffset(t.length * Sizeof.INT), Sizeof.INT);
	        	break;
	        case 1:
	        	cuMemcpyDtoH(Pointer.to(res), deviceDiag1.withByteOffset(t.length * Sizeof.INT), Sizeof.INT);
	        	break;
	        case 2:
	        	cuMemcpyDtoH(Pointer.to(res), deviceDiag2.withByteOffset(t.length * Sizeof.INT), Sizeof.INT);
	        	break;
	        }
	        
	        System.err.println("Processed distance #" + i +": "+res[0]);
	        
			if (res[0] < bestDist){
				bestSeq = curSeq;
				bestDist = res[0];
			}
		}
		
		// Clean up.
        cuMemFree(deviceInputU);
        cuMemFree(deviceInputT);
        cuMemFree(deviceDiag0);
        cuMemFree(deviceDiag1);
        cuMemFree(deviceDiag2);
		
		return new SeqDist(bestSeq, bestDist);
	}
	
    /**
     * The extension of the given file name is replaced with "ptx".
     * If the file with the resulting name does not exist, it is
     * compiled from the given file using NVCC. The name of the
     * PTX file is returned.
     *
     * @param cuFileName The name of the .CU file
     * @return The name of the PTX file
     * @throws IOException If an I/O error occurs
     */
    private static String preparePtxFile(String cuFileName) throws IOException
    {
        int endIndex = cuFileName.lastIndexOf('.');
        if (endIndex == -1)
        {
            endIndex = cuFileName.length()-1;
        }
        String ptxFileName = cuFileName.substring(0, endIndex+1)+"ptx";
        File ptxFile = new File(ptxFileName);
        if (ptxFile.exists())
        {
            return ptxFileName;
        }

        File cuFile = new File(cuFileName);
        if (!cuFile.exists())
        {
            throw new IOException("Input file not found: "+cuFileName);
        }
        String modelString = "-m"+System.getProperty("sun.arch.data.model");
        String command =
            "nvcc " + modelString + " -ptx "+
            cuFile.getPath()+" -o "+ptxFileName;

        System.out.println("Executing\n"+command);
        Process process = Runtime.getRuntime().exec(command);

        String errorMessage =
            new String(toByteArray(process.getErrorStream()));
        String outputMessage =
            new String(toByteArray(process.getInputStream()));
        int exitValue = 0;
        try
        {
            exitValue = process.waitFor();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IOException(
                "Interrupted while waiting for nvcc output", e);
        }

        if (exitValue != 0)
        {
            System.out.println("nvcc process exitValue "+exitValue);
            System.out.println("errorMessage:\n"+errorMessage);
            System.out.println("outputMessage:\n"+outputMessage);
            throw new IOException(
                "Could not create .ptx file: "+errorMessage);
        }

        System.out.println("Finished creating PTX file");
        return ptxFileName;
    }

    /**
     * Fully reads the given InputStream and returns it as a byte array
     *
     * @param inputStream The input stream to read
     * @return The byte array containing the data from the input stream
     * @throws IOException If an I/O error occurs
     */
    private static byte[] toByteArray(InputStream inputStream)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }
}
