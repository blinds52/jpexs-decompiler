/*
 *  Copyright (C) 2010-2013 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.abc.avm2.treemodel.operations;

import com.jpexs.decompiler.flash.abc.avm2.ConstantPool;
import com.jpexs.decompiler.flash.abc.avm2.instructions.AVM2Instruction;
import com.jpexs.decompiler.flash.abc.avm2.treemodel.TreeItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import java.util.HashMap;
import java.util.List;

public abstract class BinaryOpTreeItem extends TreeItem {

   public GraphTargetItem leftSide;
   public GraphTargetItem rightSide;
   protected String operator = "";

   public BinaryOpTreeItem(AVM2Instruction instruction, int precedence, GraphTargetItem leftSide, GraphTargetItem rightSide, String operator) {
      super(instruction, precedence);
      this.leftSide = leftSide;
      this.rightSide = rightSide;
      this.operator = operator;
   }

   @Override
   public String toString(ConstantPool constants, HashMap<Integer, String> localRegNames, List<String> fullyQualifiedNames) {
      String ret = "";
      if (leftSide.precedence > precedence) {
         ret += "(" + leftSide.toString(constants, localRegNames, fullyQualifiedNames) + ")";
      } else {
         ret += leftSide.toString(constants, localRegNames, fullyQualifiedNames);
      }
      ret += hilight(operator);
      if (rightSide.precedence > precedence) {
         ret += "(" + rightSide.toString(constants, localRegNames, fullyQualifiedNames) + ")";
      } else {
         ret += rightSide.toString(constants, localRegNames, fullyQualifiedNames);
      }
      return ret;
   }
}