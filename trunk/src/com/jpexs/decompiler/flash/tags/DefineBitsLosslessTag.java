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
package com.jpexs.decompiler.flash.tags;

import com.jpexs.decompiler.flash.SWFInputStream;
import com.jpexs.decompiler.flash.SWFOutputStream;
import com.jpexs.decompiler.flash.tags.base.AloneTag;
import com.jpexs.decompiler.flash.tags.base.CharacterTag;
import com.jpexs.decompiler.flash.types.BITMAPDATA;
import com.jpexs.decompiler.flash.types.COLORMAPDATA;
import com.jpexs.decompiler.flash.types.RGB;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.InflaterInputStream;

public class DefineBitsLosslessTag extends CharacterTag implements AloneTag {

   public int characterID;
   public int bitmapFormat;
   public int bitmapWidth;
   public int bitmapHeight;
   public int bitmapColorTableSize;
   public byte zlibBitmapData[]; //TODO: Parse COLORMAPDATA,BITMAPDATA
   public static final int FORMAT_8BIT_COLORMAPPED = 3;
   public static final int FORMAT_15BIT_RGB = 4;
   public static final int FORMAT_24BIT_RGB = 5;
   private COLORMAPDATA colorMapData;
   private BITMAPDATA bitmapData;
   private boolean decompressed = false;

   public BufferedImage getImage() {
      BufferedImage bi = new BufferedImage(bitmapWidth, bitmapHeight, BufferedImage.TYPE_INT_RGB);
      Graphics g = bi.getGraphics();
      COLORMAPDATA colorMapData = null;
      BITMAPDATA bitmapData = null;
      if (bitmapFormat == DefineBitsLosslessTag.FORMAT_8BIT_COLORMAPPED) {
         colorMapData = getColorMapData();
      }
      if ((bitmapFormat == DefineBitsLosslessTag.FORMAT_15BIT_RGB) || (bitmapFormat == DefineBitsLosslessTag.FORMAT_24BIT_RGB)) {
         bitmapData = getBitmapData();
      }
      int pos32aligned = 0;
      int pos = 0;
      for (int y = 0; y < bitmapHeight; y++) {
         for (int x = 0; x < bitmapWidth; x++) {
            if (bitmapFormat == DefineBitsLosslessTag.FORMAT_8BIT_COLORMAPPED) {
               RGB color = colorMapData.colorTableRGB[colorMapData.colorMapPixelData[pos32aligned] & 0xff];
               g.setColor(new Color(color.red, color.green, color.blue));
            }
            if (bitmapFormat == DefineBitsLosslessTag.FORMAT_15BIT_RGB) {
               g.setColor(new Color(bitmapData.bitmapPixelDataPix15[pos].red * 8, bitmapData.bitmapPixelDataPix15[pos].green * 8, bitmapData.bitmapPixelDataPix15[pos].blue * 8));
            }
            if (bitmapFormat == DefineBitsLosslessTag.FORMAT_24BIT_RGB) {
               g.setColor(new Color(bitmapData.bitmapPixelDataPix24[pos].red, bitmapData.bitmapPixelDataPix24[pos].green, bitmapData.bitmapPixelDataPix24[pos].blue));
            }
            g.fillRect(x, y, 1, 1);
            pos32aligned++;
            pos++;
         }
         while ((pos32aligned % 4 != 0)) {
            pos32aligned++;
         }
      }
      return bi;
   }

   @Override
   public int getCharacterID() {
      return characterID;
   }

   public COLORMAPDATA getColorMapData() {
      if (!decompressed) {
         uncompressData();
      }
      return colorMapData;
   }

   public BITMAPDATA getBitmapData() {
      if (!decompressed) {
         uncompressData();
      }
      return bitmapData;
   }

   private void uncompressData() {
      try {
         SWFInputStream sis = new SWFInputStream(new InflaterInputStream(new ByteArrayInputStream(zlibBitmapData)), 10);
         if (bitmapFormat == FORMAT_8BIT_COLORMAPPED) {
            colorMapData = sis.readCOLORMAPDATA(bitmapColorTableSize, bitmapWidth, bitmapHeight);
         }
         if ((bitmapFormat == FORMAT_15BIT_RGB) || (bitmapFormat == FORMAT_24BIT_RGB)) {
            bitmapData = sis.readBITMAPDATA(bitmapFormat, bitmapWidth, bitmapHeight);
         }
      } catch (IOException ex) {
      }
      decompressed = true;
   }

   public DefineBitsLosslessTag(byte[] data, int version, long pos) throws IOException {
      super(20, "DefineBitsLossless", data, pos);
      SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
      characterID = sis.readUI16();
      bitmapFormat = sis.readUI8();
      bitmapWidth = sis.readUI16();
      bitmapHeight = sis.readUI16();
      if (bitmapFormat == FORMAT_8BIT_COLORMAPPED) {
         bitmapColorTableSize = sis.readUI8();
      }
      zlibBitmapData = sis.readBytes(sis.available());
   }

   /**
    * Gets data bytes
    *
    * @param version SWF version
    * @return Bytes of data
    */
   @Override
   public byte[] getData(int version) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStream os = baos;
      SWFOutputStream sos = new SWFOutputStream(os, version);
      try {
         sos.writeUI16(characterID);
         sos.writeUI8(bitmapFormat);
         sos.writeUI16(bitmapWidth);
         sos.writeUI16(bitmapHeight);
         if (bitmapFormat == FORMAT_8BIT_COLORMAPPED) {
            sos.writeUI8(bitmapColorTableSize);
         }
         sos.write(zlibBitmapData);
      } catch (IOException e) {
      }
      return baos.toByteArray();
   }
}