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
package com.jpexs.decompiler.flash.action.treemodel;

import com.jpexs.decompiler.flash.graph.GraphSourceItem;
import com.jpexs.decompiler.flash.graph.GraphTargetItem;
import java.util.List;

public class StartDragTreeItem extends TreeItem {

   public GraphTargetItem target;
   public GraphTargetItem lockCenter;
   public GraphTargetItem constrain;
   public GraphTargetItem y2;
   public GraphTargetItem x2;
   public GraphTargetItem y1;
   public GraphTargetItem x1;

   public StartDragTreeItem(GraphSourceItem instruction, GraphTargetItem target, GraphTargetItem lockCenter, GraphTargetItem constrain, GraphTargetItem x1, GraphTargetItem y1, GraphTargetItem x2, GraphTargetItem y2) {
      super(instruction, PRECEDENCE_PRIMARY);
      this.target = target;
      this.lockCenter = lockCenter;
      this.constrain = constrain;
      this.y2 = y2;
      this.x2 = x2;
      this.y1 = y1;
      this.x1 = x1;
   }

   @Override
   public String toString(ConstantPool constants) {
      boolean hasConstrains = true;
      if (constrain instanceof DirectValueTreeItem) {
         if (((DirectValueTreeItem) constrain).value instanceof Long) {
            if (((long) (Long) ((DirectValueTreeItem) constrain).value) == 0) {
               hasConstrains = false;
            }
         }
         if (((DirectValueTreeItem) constrain).value instanceof Boolean) {
            if (((boolean) (Boolean) ((DirectValueTreeItem) constrain).value) == false) {
               hasConstrains = false;
            }
         }
      }
      return hilight("startDrag(") + target.toString(constants) + hilight(",") + lockCenter.toString(constants) + (hasConstrains ? hilight(",") + x1.toString(constants) + hilight(",") + y1.toString(constants) + hilight(",") + x2.toString(constants) + hilight(",") + y2.toString(constants) : "") + hilight(")");
   }

   @Override
   public List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> getNeededSources() {
      List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> ret = super.getNeededSources();
      ret.addAll(target.getNeededSources());
      ret.addAll(constrain.getNeededSources());
      ret.addAll(x1.getNeededSources());
      ret.addAll(x2.getNeededSources());
      ret.addAll(y1.getNeededSources());
      ret.addAll(y2.getNeededSources());
      return ret;
   }
}