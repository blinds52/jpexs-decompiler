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
import java.util.List;

public class EachTreeItem extends TreeItem {

   public TreeItem object;
   public TreeItem collection;

   public EachTreeItem(GraphSourceItem instruction, TreeItem object, TreeItem collection) {
      super(instruction, NOPRECEDENCE);
      this.object = object;
      this.collection = collection;
   }

   @Override
   public String toString(ConstantPool constants) {
      return hilight("each (") + object.toString(constants) + hilight(" in ") + collection.toString(constants) + ")";
   }

   @Override
   public List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> getNeededSources() {
      List<com.jpexs.decompiler.flash.graph.GraphSourceItemPos> ret = super.getNeededSources();
      ret.addAll(object.getNeededSources());
      ret.addAll(collection.getNeededSources());
      return ret;
   }
}