//==============================================================================
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

package prism;

import jdd.JDDNode;

// interface for state probability vector classes

public interface StateProbs
{
	void roundOff(int places);
	void subtractFromOne();
	void add(StateProbs sp);
	void timesConstant(double d);
	void filter(JDDNode filter);
	void clear();
	int getNNZ();
	String getNNZString();
	double firstFromBDD(JDDNode filter);
	double minOverBDD(JDDNode filter);
	double maxOverBDD(JDDNode filter);
	double sumOverBDD(JDDNode filter);
	double sumOverMTBDD(JDDNode mult);
	JDDNode getBDDFromInterval(String relOp, double bound);
	JDDNode getBDDFromInterval(double lo, double hi);
	void print(PrismLog log);
	void printFiltered(PrismLog log, JDDNode filter);
}
