/*
 * Copyright (c) 2002-2019 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.internal.handlers;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;

import java.util.concurrent.CompletableFuture;

import org.neo4j.driver.internal.async.inbound.InboundMessageDispatcher;
import org.neo4j.driver.internal.util.Clock;

import static org.neo4j.driver.internal.async.ChannelAttributes.setLastUsedTimestamp;

public class ChannelReleasingResetResponseHandler extends ResetResponseHandler
{
    private final Channel channel;
    private final ChannelPool pool;
    private final Clock clock;

    public ChannelReleasingResetResponseHandler( Channel channel, ChannelPool pool,
            InboundMessageDispatcher messageDispatcher, Clock clock, CompletableFuture<Void> releaseFuture )
    {
        super( messageDispatcher, releaseFuture );
        this.channel = channel;
        this.pool = pool;
        this.clock = clock;
    }

    @Override
    protected void resetCompleted( CompletableFuture<Void> completionFuture, boolean success )
    {
        if ( success )
        {
            // update the last-used timestamp before returning the channel back to the pool
            setLastUsedTimestamp( channel, clock.millis() );
        }
        else
        {
            // close the channel before returning it back to the pool if RESET failed
            channel.close();
        }

        Future<Void> released = pool.release( channel );
        released.addListener( ignore -> completionFuture.complete( null ) );
    }
}
