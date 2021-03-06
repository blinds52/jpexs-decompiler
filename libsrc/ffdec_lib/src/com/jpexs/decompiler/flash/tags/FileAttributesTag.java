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
import com.jpexs.decompiler.flash.types.BasicType;
import com.jpexs.decompiler.flash.types.annotations.Reserved;
import com.jpexs.decompiler.flash.types.annotations.SWFType;
import com.jpexs.helpers.ByteArrayRange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileAttributesTag extends Tag {

    public boolean useDirectBlit;

    public boolean useGPU;

    public boolean hasMetadata;

    public boolean actionScript3;

    public boolean useNetwork;

    public boolean noCrossDomainCache;

    @Reserved
    public boolean reserved1;

    @Reserved
    public boolean reserved2;

    @SWFType(value = BasicType.UB, count = 24)
    @Reserved
    public int reserved3;

    public static final int ID = 69;

    /**
     * Constructor
     *
     * @param swf
     */
    public FileAttributesTag(SWF swf) {
        super(swf, ID, "FileAttributes", null);
    }

    public FileAttributesTag(SWFInputStream sis, ByteArrayRange data) throws IOException {
        super(sis.getSwf(), ID, "FileAttributes", data);
        reserved1 = sis.readUB(1, "reserved1") == 1; // reserved
        // UB[1] == 0  (reserved)
        useDirectBlit = sis.readUB(1, "useDirectBlit") != 0;
        useGPU = sis.readUB(1, "useGPU") != 0;
        hasMetadata = sis.readUB(1, "hasMetadata") != 0;
        actionScript3 = sis.readUB(1, "actionScript3") != 0;
        noCrossDomainCache = sis.readUB(1, "noCrossDomainCache") != 0;
        reserved2 = sis.readUB(1, "reserved2") == 1; // reserved
        useNetwork = sis.readUB(1, "useNetwork") != 0;
        // UB[24] == 0 (reserved)
        reserved3 = (int) sis.readUB(24, "reserved3"); //reserved
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
            sos.writeUB(1, reserved1 ? 1 : 0); //reserved
            sos.writeUB(1, useDirectBlit ? 1 : 0);
            sos.writeUB(1, useGPU ? 1 : 0);
            sos.writeUB(1, hasMetadata ? 1 : 0);
            sos.writeUB(1, actionScript3 ? 1 : 0);
            sos.writeUB(1, noCrossDomainCache ? 1 : 0);
            sos.writeUB(1, reserved2 ? 1 : 0); //reserved
            sos.writeUB(1, useNetwork ? 1 : 0);
            sos.writeUB(24, reserved3); //reserved
        } catch (IOException e) {
            throw new Error("This should never happen.", e);
        }
        return baos.toByteArray();
    }
}
