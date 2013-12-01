extern "C"
__global__ void distance(int nu_g, int nt_g, int nk_g, int *u, int *t, int *d0, int *d1, int *d2)
{
	// allocate local copies of this variable for a faster access
	int nu, nt, nd, nk;
	nu = nu_g; nt = nt_g; nk = nk_g;
	
	// local indexes and temp variables
    int k, l, max_l, tmp_min;
    int *tmp_d;
    
    for(k = 2 ; k <= nk ; k++){
    	tmp_d = d0;
    	d0 = d1;
    	d1 = d2;
    	d2 = tmp_d;
    	l = (k > nu ? (k - nu) : 0) + threadIdx.x;
    	max_l = k > nt ? nt : k;
    	
    	for(; l <= max_l ; l += blockDim.x){
    		if (l == 0){
    			d2[0] = d1[0] + 1;
    		} else if (l == k){
    			d2[l] = d1[l-1] + 1;
    		} else {
    			tmp_min = d1[l-1] > d1[l] ? (d1[l] + 1) : (d1[l-1] + 1);
    			if (u[k-l-1] != t[l-1])
    				d2[l] = tmp_min < (d0[l-1] + 1) ? tmp_min : (d0[l-1] + 1);
    			else
    				d2[l] = tmp_min < d0[l-1] ? tmp_min : d0[l-1];
    		}
    	}
    	
    	__syncthreads();
    }
}
