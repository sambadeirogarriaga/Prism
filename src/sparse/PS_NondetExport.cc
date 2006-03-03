//==============================================================================
//	
//	File:		PS_NondetExport.cc
//	Date:		17/12/03
//	Author:		Dave Parker
//	Desc:		Export nondeterministic model (MDP) to a file
//	
//------------------------------------------------------------------------------
//	
//	Copyright (c) 2002-2004, Dave Parker
//	
//	This file is part of PRISM.
//	
//	PRISM is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//	
//	PRISM is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with PRISM; if not, write to the Free Software Foundation,
//	Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//	
//==============================================================================

// includes
#include "PrismSparse.h"
#include <util.h>
#include <cudd.h>
#include <dd.h>
#include <odd.h>
#include "sparse.h"
#include "PrismSparseGlob.h"

//------------------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_sparse_PrismSparse_PS_1NondetExport
(
JNIEnv *env,
jclass cls,
jint t,			// trans matrix
jint rv,		// row vars
jint num_rvars,
jint cv,		// col vars
jint num_cvars,
jint ndv,		// nondet vars
jint num_ndvars,
jint od,		// odd
jint et,		// export type
jstring fn		// filename
)
{
	DdNode *trans = (DdNode *)t;	// trans matrix
	DdNode **rvars = (DdNode **)rv; // row vars
	DdNode **cvars = (DdNode **)cv; // col vars
	DdNode **ndvars = (DdNode **)ndv;	// nondet vars
	ODDNode *odd = (ODDNode *)od;
	// sparse matrix
	NDSparseMatrix *ndsm;
	// model stats
	int i, j, k, n, nc, l1, h1, l2, h2;
	long nnz;
	
	const char *filename = env->GetStringUTFChars(fn, 0);
	FILE *file = fopen(filename, "w");
	if (!file) {
		env->ReleaseStringUTFChars(fn, filename);
		return -1;
	}
	
	// build sparse matrix
	ndsm = build_nd_sparse_matrix(ddman, trans, rvars, cvars, num_rvars, ndvars, num_ndvars, odd);
	n = ndsm->n;
	nnz = ndsm->nnz;
	nc = ndsm->nc;
	
	// print file header
	switch (et) {
	case EXPORT_PLAIN: fprintf(file, "%d %d %d\n", n, nc, nnz); break;
	case EXPORT_MATLAB: for (i = 0; i < ndsm->k; i++) fprintf(file, "S%d = sparse(%d,%d);\n", i+1, n, n); break;
	}
	
	// print main part of file
	// first get data structure info
	double *non_zeros = ndsm->non_zeros;
	unsigned char *row_counts = ndsm->row_counts;
	int *row_starts = (int *)ndsm->row_counts;
	unsigned char *choice_counts = ndsm->choice_counts;
	int *choice_starts = (int *)ndsm->choice_counts;
	bool use_counts = ndsm->use_counts;
	unsigned int *cols = ndsm->cols;
	// then traverse data structure
	h1 = h2 = 0;
	for (i = 0; i < n; i++) {
		if (!use_counts) { l1 = row_starts[i]; h1 = row_starts[i+1]; }
		else { l1 = h1; h1 += row_counts[i]; }
		for (j = l1; j < h1; j++) {
			if (!use_counts) { l2 = choice_starts[j]; h2 = choice_starts[j+1]; }
			else { l2 = h2; h2 += choice_counts[j]; }
			for (k = l2; k < h2; k++) {
				switch (et) {
				case EXPORT_PLAIN: fprintf(file, "%d %d %d %.12f\n", i, j-l1, cols[k], non_zeros[k]); break;
				case EXPORT_MATLAB: fprintf(file, "S%d(%d,%d)=%.12f;\n", j-l1+1, i+1, cols[k]+1, non_zeros[k]); break;
				}
			}
		}
	}
	
	fclose(file);
	env->ReleaseStringUTFChars(fn, filename);
	
	return 0;
}

//------------------------------------------------------------------------------
