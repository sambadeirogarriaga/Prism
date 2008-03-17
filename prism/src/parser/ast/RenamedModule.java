//==============================================================================
//	
//	Copyright (c) 2002-
//	Authors:
//	* Dave Parker <david.parker@comlab.ox.ac.uk> (University of Oxford, formerly University of Birmingham)
//	
//------------------------------------------------------------------------------
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

package parser.ast;

import java.util.ArrayList;

import parser.visitor.*;
import prism.PrismLangException;

public class RenamedModule extends ASTElement
{
	private String name;
	private String baseModule;
	private ArrayList<String> oldNames;
	private ArrayList<String> newNames;
	
	// Constructor
	
	public RenamedModule(String n, String b)
	{
		name = n;
		baseModule = b;
		oldNames = new ArrayList<String>();
		newNames = new ArrayList<String>();
	}
	
	// Set methods
	
	public void setName(String n)
	{
		name = n;
	}

	public void setBaseModule(String b)
	{
		baseModule = b;
	}
	
	public void addRename(String s1, String s2)
	{
		oldNames.add(s1);
		newNames.add(s2);
	}
		
	// Get methods
	
	public String getName()
	{
		return name;
	}
	
	public String getBaseModule()
	{
		return baseModule;
	}

	public String getNewName(String s)
	{
		int i = oldNames.indexOf(s);
		if (i == -1) {
			return null;
		}
		return newNames.get(i);
	}
		
	public String getOldName(String s)
	{
		int i = newNames.indexOf(s);
		if (i == -1) {
			return null;
		}
		return oldNames.get(i);
	}
	
	public int getNumRenames()
	{
		return oldNames.size();
	}
	
	public String getOldName(int i)
	{
		return oldNames.get(i); 
	}
	
	public String getNewName(int i)
	{
		return newNames.get(i); 
	}
	
	// Methods required for ASTElement:
	
	/**
	 * Visitor method.
	 */
	public Object accept(ASTVisitor v) throws PrismLangException
	{
		return v.visit(this);
	}
	
	/**
	 * Convert to string.
	 */
	public String toString()
	{
		String s = "";
		int i;
		
		s = s + "module " + name + " = " + baseModule + " [";
		for (i = 0; i < oldNames.size() - 1; i++) {
			s = s + oldNames.get(i) + " = " + newNames.get(i) + ", ";
		}
		i = oldNames.size() - 1;
		s = s + oldNames.get(i) + " = " + newNames.get(i) + "] endmodule";
		
		return s;
	}
	
	/**
	 * Perform a deep copy.
	 */
	public ASTElement deepCopy()
	{
		int i, n;
		RenamedModule ret = new RenamedModule(name, baseModule);
		n = oldNames.size();
		for (i = 0; i < n; i++) {
			ret.addRename(oldNames.get(i), newNames.get(i));
		}
		ret.setPosition(this);
		return ret;
	}
}

//------------------------------------------------------------------------------