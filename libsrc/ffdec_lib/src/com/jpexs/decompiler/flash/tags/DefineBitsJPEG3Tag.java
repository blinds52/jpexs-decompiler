/*
 *  Copyright (C) 2010-2015 JPEXS, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.jpexs.decompiler.flash.tags;

import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.SWFInputStream;
import com.jpexs.decompiler.flash.SWFOutputStream;
import com.jpexs.decompiler.flash.helpers.ImageHelper;
import com.jpexs.decompiler.flash.tags.base.AloneTag;
import com.jpexs.decompiler.flash.tags.base.ImageTag;
import com.jpexs.decompiler.flash.types.BasicType;
import com.jpexs.decompiler.flash.types.annotations.SWFType;
import com.jpexs.helpers.ByteArrayRange;
import com.jpexs.helpers.SerializableImage;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DefineBitsJPEG3Tag extends ImageTag implements AloneTag {

    @SWFType(BasicType.UI16)
    public int characterID;

    @SWFType(BasicType.UI8)
    public ByteArrayRange imageData;

    @SWFType(BasicType.UI8)
    public byte[] bitmapAlphaData;

    public static final int ID = 35;

    @Override
    public int getCharacterId() {
        return characterID;
    }

    @Override
    public void setImage(byte[] data) throws IOException {
        if (ImageTag.getImageFormat(data).equals("jpg")) {
            SerializableImage image = new SerializableImage(ImageHelper.read(new ByteArrayInputStream(data)));
            byte[] ba = new byte[image.getWidth() * image.getHeight()];
            for (int i = 0; i < ba.length; i++) {
                ba[i] = (byte) 255;
            }
            bitmapAlphaData = ba;
        } else {
            bitmapAlphaData = new byte[0];
        }
        imageData = new ByteArrayRange(data);
        clearCache();
        setModified(true);
    }

    @Override
    public String getImageFormat() {
        String fmt = ImageTag.getImageFormat(imageData);
        if (fmt.equals("jpg")) {
            fmt = "png"; //transparency
        }
        return fmt;
    }

    @Override
    public InputStream getImageData() {
        return null;
    }

    @Override
    public SerializableImage getImage() {
        if (cachedImage != null) {
            return cachedImage;
        }
        try {
            InputStream stream;
            if (SWF.hasErrorHeader(imageData)) {
                stream = new ByteArrayInputStream(imageData.getArray(), imageData.getPos() + 4, imageData.getLength() - 4);
            } else {
                stream = new ByteArrayInputStream(imageData.getArray(), imageData.getPos(), imageData.getLength());
            }

            BufferedImage image = ImageHelper.read(stream);
            SerializableImage img = image == null ? null : new SerializableImage(image);
            if (bitmapAlphaData.length == 0) {
                cachedImage = img;
                return img;
            }

            int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
            for (int i = 0; i < pixels.length; i++) {
                int a = bitmapAlphaData[i] & 0xff;
                pixels[i] = multiplyAlpha((pixels[i] & 0xffffff) | (a << 24));
            }

            cachedImage = img;
            return img;
        } catch (IOException ex) {
        }
        return null;
    }

    /**
     * Constructor
     *
     * @param swf
     */
    public DefineBitsJPEG3Tag(SWF swf) {
        super(swf, ID, "DefineBitsJPEG3", null);
        characterID = swf.getNextCharacterId();
        imageData = ByteArrayRange.EMPTY;
        bitmapAlphaData = new byte[0];
    }

    public DefineBitsJPEG3Tag(SWFInputStream sis, ByteArrayRange data) throws IOException {
        super(sis.getSwf(), ID, "DefineBitsJPEG3", data);
        characterID = sis.readUI16("characterID");
        long alphaDataOffset = sis.readUI32("alphaDataOffset");
        imageData = sis.readByteRangeEx(alphaDataOffset, "imageData");
        bitmapAlphaData = sis.readBytesZlib(sis.available(), "bitmapAlphaData");
    }

    /**
     * Gets data bytes
     *
     * @return Bytes of data
     */
    @Override
    public byte[] getData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        SWFOutputStream sos = new SWFOutputStream(os, getVersion());
        try {
            sos.writeUI16(characterID);
            sos.writeUI32(imageData.getLength());
            sos.write(imageData);
            sos.writeBytesZlib(bitmapAlphaData);
        } catch (IOException e) {
            throw new Error("This should never happen.", e);
        }
        return baos.toByteArray();
    }
}
