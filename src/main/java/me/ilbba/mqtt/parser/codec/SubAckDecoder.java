/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package me.ilbba.mqtt.parser.codec;

import io.netty.buffer.ByteBuf;
import io.netty.util.AttributeMap;
import me.ilbba.mqtt.protocol.AbstractMessage;
import me.ilbba.mqtt.protocol.SubAckMessage;

import java.util.List;

/**
 * @author andrea
 */
public class SubAckDecoder extends DemuxDecoder {

    @Override
    public void decode(AttributeMap ctx, ByteBuf in, List<Object> out) throws Exception {
        //Common decoding part
        in.resetReaderIndex();
        SubAckMessage message = new SubAckMessage();
        if (!decodeCommonHeader(message, 0x00, in)) {
            in.resetReaderIndex();
            return;
        }
        int remainingLength = message.getRemainingLength();

        //MessageID
        message.setMessageID(in.readUnsignedShort());
        remainingLength -= 2;

        //Qos array
        if (in.readableBytes() < remainingLength) {
            in.resetReaderIndex();
            return;
        }
        for (int i = 0; i < remainingLength; i++) {
            byte qos = in.readByte();
            message.addType(AbstractMessage.QOSType.valueOf(qos));
        }

        out.add(message);
    }

}
