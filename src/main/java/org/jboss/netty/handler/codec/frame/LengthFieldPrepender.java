/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @author tags. See the COPYRIGHT.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.netty.handler.codec.frame;

import static org.jboss.netty.buffer.ChannelBuffers.*;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * TODO Documentation
 *
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 * @version $Rev$, $Date$
 */
public class LengthFieldPrepender extends OneToOneEncoder {

    private final int lengthFieldLength;

    public LengthFieldPrepender(int lengthFieldLength) {
        if (lengthFieldLength != 1 && lengthFieldLength != 2 &&
            lengthFieldLength != 3 && lengthFieldLength != 4 &&
            lengthFieldLength != 8) {
            throw new IllegalArgumentException(
                    "lengthFieldLength must be either 1, 2, 3, 4, or 8: " +
                    lengthFieldLength);
        }

        this.lengthFieldLength = lengthFieldLength;
    }

    @Override
    protected Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        ChannelBuffer header = channel.getConfig().getBufferFactory().getBuffer(lengthFieldLength);
        ChannelBuffer body = (ChannelBuffer) msg;

        switch (lengthFieldLength) {
        case 1:
            header.writeByte((byte) body.readableBytes());
            break;
        case 2:
            header.writeShort((short) body.readableBytes());
            break;
        case 3:
            header.writeMedium(body.readableBytes());
            break;
        case 4:
            header.writeInt(body.readableBytes());
            break;
        case 8:
            header.writeLong(body.readableBytes());
            break;
        default:
            throw new Error("should not reach here");
        }
        return wrappedBuffer(header, body);
    }
}